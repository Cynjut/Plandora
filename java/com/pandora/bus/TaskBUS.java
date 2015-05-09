package com.pandora.bus;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.dao.TaskDAO;
import com.pandora.dao.TaskHistoryDAO;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.ProjectTasksDiffRequirementException;
import com.pandora.exception.TasksDiffRequirementException;
import com.pandora.exception.ZeroCapacityDBException;
import com.pandora.exception.ZeroCapacityException;
import com.pandora.helper.DateUtil;

/**
 * This class contain the business rules related with Task entity.
 */
public class TaskBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    TaskDAO dao = new TaskDAO();
    
    
    /**
     * Get a list of Task objects based on Requirement object.
     */
    public Vector<TaskTO> getTaskListByRequirement(RequirementTO rto, ProjectTO pto, boolean evenClose, boolean isSort) throws BusinessException {
        Vector<TaskTO> response = new Vector<TaskTO>();
        try {
        	Vector<TaskTO> list = dao.getTaskListByRequirement(rto, pto, evenClose);
        	if (isSort) {
        		response = this.sortTasks(list);
        	} else {
        		response = list;	
        	}
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    public Vector getSubTasksList(TaskTO parenttask) throws BusinessException {
        Vector response = new Vector();
        try {
            response = dao.getSubTasksList(parenttask);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;    	
    }
    
    
    /**
     * This method check if the current user is the owner of a specific task.
	 * - the related task must be created by the same user that is trying to remove the task
	 * - the related task must not be linked to a requirement
	 * - the resource task must not has other 'resource tasks' linked into the same task  
     */
    public boolean isUserTaskOwner(TaskTO tto, UserTO currentUser) {
		return (currentUser.getId().equals(tto.getCreatedBy().getId()) && tto.getAllocResources().size()==1 &&
				(tto.getRequirement()==null || tto.getRequirement().getId()==null));
    }
    
    
    /**
     * Get a list of tasks based on project object
     */
    public Vector<TaskTO> getTaskListByProject(ProjectTO pto, Timestamp iniRange, boolean isSort, boolean includeSubProjects) throws BusinessException {
        Vector<TaskTO> response = new Vector<TaskTO>();
        ProjectBUS pbus = new ProjectBUS();
        
        try {
        	
            //get the tasks of child projects        	
        	if (includeSubProjects) { 
                Vector<ProjectTO> childs = pbus.getProjectListByParent(pto, true);
                Iterator<ProjectTO> i = childs.iterator();
                while(i.hasNext()){
                    ProjectTO childProj = i.next();
                    Vector<TaskTO> tskOfChild = this.getTaskListByProject(childProj, iniRange, isSort, includeSubProjects);
                    response.addAll(tskOfChild);
                }        		
        	}
                    
            response.addAll(dao.getTaskListByProject(pto, iniRange));
            
            //sort all the tasks...            
            if (isSort) {
                response = this.sortTasks(response);                
            }
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }    
     
    
    /**
     * Insert a list of Task objects into data base 
     */
    public void insertTask(Vector list) throws BusinessException {
        try {
            dao.insert(list);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }
    
    
    /**
     * Check if a task object contain predecessors that blocks 
     * the current task (tto) starting.
     */
    public boolean isBlocked(TaskTO tto) {
    	boolean response = false;
    	
    	Vector relations = tto.getRelationList();
    	if (relations!=null && relations.size()>0) {
    		Iterator i = relations.iterator();
    		while(i.hasNext()) {
    			PlanningRelationTO relation = (PlanningRelationTO)i.next();
    			if (relation.getRelated().getId().equals(tto.getId())) {
        			if (relation.getRelationType().equals(PlanningRelationTO.RELATION_BLOCKS) && 
        					relation.getPlanning().getFinalDate()==null) {
        				response = true;
        				break;
        			}    			    
    			}
    		}
    	}
    	return response;    	
    }
    
    
    /**
     * Get a list of tasks based on project object.<br>
     * The list should be returned in tree format.
     */
    public Vector<TaskTO> getTaskListByProjectInTree(ProjectTO pto) throws BusinessException {
        //get the unordered list of tasks of project...
        Vector<TaskTO> response = this.getTaskListByProject(pto, null, false, true);
        
        //...and create a tree structure
        response = this.getTaskListInTree(response);
        
        return response;
    }
    
    
    /**
     * Get a list of Task objects that can be used for parent task.
     * @param rto the requirement of task used such a filter. This filter could be null.
     * @return
     * @throws BusinessException
     */
    public Vector<TaskTO> getAvailableParentTaskList(RequirementTO rto, String projectId) throws BusinessException {
        Vector<TaskTO> response = new Vector<TaskTO>();
        try {
            response = dao.getAvailableParentTaskList(rto, projectId);    
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }    
    
    
    /**
     * Get a Task object from database.
     * @param tto
     * @return
     * @throws BusinessException
     */
    public TaskTO getTaskObject(TaskTO tto) throws BusinessException {
        TaskTO response = null;
        try {        
            if (tto!=null){
                response = (TaskTO)dao.getObject(tto);    
            }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }


    /**
     * Insert a new Task object into data base
     */
    public void insertTask(TaskTO tto) throws BusinessException {
		try {        
	        //check if task contain the same requirement of his parent task
	        if (this.isTaskAndParentTaskSameRequirement(tto)){
	            
		        //create a new task
		        dao.insert(tto);
		        			        
	        } else {
	            throw new TasksDiffRequirementException();    
	        }
		        
		} catch (ZeroCapacityDBException e) {
			throw new ZeroCapacityException(e);
			
		} catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }

    
    public void updateByResource(TaskTO to) throws BusinessException {
        try {        
            if (to!=null){
            	dao.updateByResource(to);
            }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }    	
    }

    /**
     * Update task object into data base. <br>
     * The intent here is to keep these two process (update of basic data and update of allocated resources) 
     * on differents DB trasactions. If second process fail, the basic information could be updated normally.
     */
    public void updateTask(TaskTO tto, boolean closeReqIfLastTask) throws BusinessException {
		RequirementDelegate rdel = new RequirementDelegate();
		
		try {
			//if tto is a parent task, set the requirement id for all sub tasks
			if (tto.isParentTask()) {
				if (tto.getChildTasks()!=null) {
					Iterator i = tto.getChildTasks().values().iterator();
					while(i.hasNext()) {
						TaskTO child = (TaskTO)i.next();
						child.setRequirement(tto.getRequirement());
						child.setParentTask(tto);
						dao.updateByResource(child);
					}					
				}
			}

	        //check if task's project and requirement's project are the same...
			if (tto.getRequirement()!=null) {			
				tto.setRequirement(rdel.getRequirement(tto.getRequirement()));
				ProjectTO pto = tto.getRequirement().getProject();
				ProjectTO ptoto = tto.getProject();
				if (!pto.getId().equals(ptoto.getId()) && tto.getTemplateInstanceId()==null){
					throw new ProjectTasksDiffRequirementException();
				}
			}
			
	        //check if task contain the same requirement of his parent task
	        if (this.isTaskAndParentTaskSameRequirement(tto)){
	            
	        	//set to the task the same requirement of macro task
	            TaskTO parentTask = tto.getParentTask();
	            if (parentTask!=null && !parentTask.getId().equals("-1")) {
	            	parentTask = this.getTaskObject(parentTask);
		            tto.setRequirement(parentTask.getRequirement());
	            }
	        	
	            //step 1: update basic data of task
		        dao.update(tto);
		        
	            //step 2: update resource task objects related with task
		        ResourceTaskBUS rtbus = new ResourceTaskBUS(); 
		        rtbus.removeAllAndInsert(tto, tto.getAllocResources());
		        
		        if (closeReqIfLastTask) {
			        Vector taskByReq = this.getTaskListByRequirement(tto.getRequirement(), tto.getProject(), false, false);
		            if (taskByReq==null || taskByReq.size()==0){
		            	ResourceTaskTO first = (ResourceTaskTO)tto.getAllocResources().get(0);
		            	rdel.changeRequirementStatus(first, first.getTaskStatus().getStateMachineOrder(), null);
		            }		        	
		        }

		        		        
	        } else {
	            throw new TasksDiffRequirementException();    
	        }
		    
		} catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Removing criteria:
     * @see com.pandora.bus.TaskBUS.isUserTaskOwner(com.pandora.TaskTO, com.pandora.UserTO)
     */
    public void removeTask(TaskTO tto) throws BusinessException {
		try {
			if (isUserTaskOwner(tto, tto.getHandler())) {
				dao.remove(tto);	
			} else {
				throw new BusinessException("The current user is not allowed to remove this task.");
			}
	        
		} catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    	
    }
    
    /**
     * Removing criteria:
     * @see com.pandora.bus.TaskBUS.isUserTaskOwner(com.pandora.TaskTO, com.pandora.UserTO)
     */
    public void updateByOwner(TaskTO tto) throws BusinessException {
		try {
			
		     //This method check if the current user is the owner of a specific task and:
			 // - the related task must be created by the same user that is trying to remove the task
			 // - the resource task must not has other 'resource tasks' linked into the same task  
			UserTO currentUser = tto.getHandler();
			if (currentUser.getId().equals(tto.getCreatedBy().getId()) && tto.getAllocResources().size()==1) {
				dao.updateByOwner(tto);	
			} else {
				throw new BusinessException("The current user is not allowed to update this task.");
			}
	        
		} catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }    
    
    /**
     * Remove (Cancel) a task object from database.
     */
    public void cancelTask(TaskTO tto, boolean reopenRelatedReq) throws BusinessException {
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
        
		try {
		    String currentComment = tto.getComment(); //save the comment from GUI
	        tto = (TaskTO) dao.getObject(tto);
	        tto.setComment(currentComment);
	        
	        //if is a super-task with child task into it, throw a exception...
	        if (tto.isParentTask() && tto.getChildTasks()!=null){
	        	
	        	//Check if the all children is closed or canceled
	            Iterator i = tto.getChildTasks().values().iterator();
	            while(i.hasNext()) {
	                TaskTO child = (TaskTO)i.next();
	                child = this.getTaskObject(child);
	                if (!child.isFinished()) {
	                	throw new BusinessException("A task with 'not finished' sub-tasks into it cannot be cancelled.");
	                }
	            }
	        }
	        
	        //check if at least one of related resourceTask can be cancelled 
	        if (tto.hasResourceTask()){
	            Iterator i = tto.getAllocResources().iterator();
	            boolean canRemove = false;
	            while(i.hasNext()) {
	                ResourceTaskTO rtto = (ResourceTaskTO)i.next();
	                if (rtbus.validateCancelAction(rtto)) {
	                    canRemove = true;
	                    break;
	                }    
	            }
	            if (!canRemove) {
	                throw new DataAccessException("To be cancelled, the current status of task have to be 'Opened', 'In-Progress' or 'on-Hold'.");    
	            }
	        }
	        
	        //cancel task and resource tasks...
	        dao.cancel(tto, reopenRelatedReq);
	        	        
		} catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }
    
        
    /**
     * Check if the current task (to be update or insert) is related with the same requirement of
     * his parent task.  
     */
    public boolean isTaskAndParentTaskSameRequirement(TaskTO tto) throws DataAccessException{
        boolean response = true;
        
        TaskTO parent = tto.getParentTask();
        if (parent!=null){
            parent = (TaskTO)dao.getObject(parent);
            RequirementTO parentReq = parent.getRequirement();
            RequirementTO currentReq = tto.getRequirement();
            
            if (parentReq!=null && currentReq!=null){
            	response = (parentReq.getId().equals(currentReq.getId()));
            } else if (parentReq==null && currentReq!=null){
            	response = false;
            }
        }

        return response;
    }

    
    /**
     * Return a first or last timestamp of task based on list of tasks.
     */
    public Timestamp getDateFromTaskList(Vector taskList, boolean isInitial) throws BusinessException {
        Timestamp ts = null;
        //ResourceTaskBUS rtbus = new ResourceTaskBUS();
        
        if (taskList!=null){
            Iterator i = taskList.iterator();
            while(i.hasNext()){
                TaskTO tto = (TaskTO)i.next();
                
                Vector rtlist = tto.getAllocResources();
                
                //was implemented the lazy initialization...
                //if (rtlist==null){
                //    rtlist = rtbus.getListByTask(tto);
                //    tto.setAllocResources(rtlist);
                //}

                Iterator j = rtlist.iterator();
                while(j.hasNext()){
                    ResourceTaskTO rtto = (ResourceTaskTO)j.next();
                    Timestamp currDate = rtto.getInitialDate();
                    if (ts==null) ts=currDate;

                    if (isInitial){
                        if (ts.after(currDate)) ts=currDate;
                    } else {
                        if (ts.before(currDate)) ts=currDate;
                    }
                }                                 
            }
        }
        return ts;
    }

    
    /**
     * This method set into parent tasks the related child tasks objects. <br>
     * The response is a list of tasks in tree format.  
     */
    private Vector getTaskListInTree(Vector taskList){
        Vector response = new Vector();
        HashMap hm = new HashMap();

        if (taskList!=null){
            
            //create a hash of all tasks...
            Iterator i = taskList.iterator();
            while(i.hasNext()){
                TaskTO tto = (TaskTO)i.next();
                hm.put(tto.getId(), tto);
            }
            
            //iterate the task list again and set the child task into parent.
            Iterator j = taskList.iterator();
            while(j.hasNext()){
                TaskTO tto = (TaskTO)j.next();
                
                //if token is under parent task...
                if (tto.getParentTask()!=null){
                    
                    //...get the parent task related and put itself into it
                    TaskTO parent = (TaskTO)hm.get(tto.getParentTask().getId());
                    parent.addChild(tto); 
                }
            }

            //Iterate again to select only the parent (roots) tasks...
            Iterator k = taskList.iterator();
            while(k.hasNext()){
                TaskTO tto = (TaskTO)k.next();
                if (tto.getParentTask()==null){
                    response.addElement(tto);
                }
            }
        }
        
        return response;
    }

    
    /**
     * Get a list of tasks without parent (roots of tree)
     */
    private Vector<TaskTO> sortTasks(Vector<TaskTO> unorderedList){
        Vector<TaskTO> response = new Vector<TaskTO>();
        Vector<TaskTO> childTasksWithParent = new Vector<TaskTO>();
    	Vector<TaskTO> childTasksWithoutParent = new Vector<TaskTO>();
    	
        if (unorderedList!=null){

            //sort all tasks using the comparable method of each task object
            Collections.sort(unorderedList);
            
            //..after sorting process, set the child tasks into parent tasks
            Iterator<TaskTO> i = unorderedList.iterator();
            while(i.hasNext()){
                TaskTO tto = i.next();
                if (tto.getParentTask()==null){
                    if (!tto.isParentTask()){
                        childTasksWithoutParent.addElement(tto);
                    } else {
                        Vector<TaskTO> subList = new Vector<TaskTO>();                        
                        tto.order(unorderedList, subList, -1);
                        childTasksWithParent.addAll(subList);                        
                    }
                }
            }            
            response.addAll(childTasksWithoutParent);
            response.addAll(childTasksWithParent);
            
            if (response.size()==0){
                response = unorderedList;
            }
        }
        
        return response;
    }


    /**
     * Return the newest Task History object from database based on Resource Task object.
     */
    public TaskHistoryTO getLastHistoryByTask(ResourceTaskTO rtto) throws BusinessException {
        TaskHistoryTO response = null;
        try {
            TaskHistoryDAO dao = new TaskHistoryDAO();
            response = dao.getLastHistoryByTask(rtto);
            if (response!=null) {
            	response.setHandler(rtto.getTask().getHandler());	
            }
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }


    /**
     * Get a list of Task History objects from data base, based on task id.
     */
    public Vector getHistory(String taskId) throws BusinessException {
        Vector response = new Vector();
        try {
            TaskHistoryDAO dao = new TaskHistoryDAO();
            response = dao.getListByTask(taskId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    
    /**
     * Get a list of Task History objects from data base, based on requirement id.
     */
    public Vector getHistoryByRequirementId(String reqId) throws BusinessException {
        Vector response = new Vector();
        try {
            TaskHistoryDAO dao = new TaskHistoryDAO();
            response = dao.getHistoryByRequirementId(reqId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;        
    }

    public String getTechCommentsFromTask(RequirementHistoryTO rhto, CustomerTO reader) throws BusinessException {
        String response = "";
        TaskHistoryDAO thdao = new TaskHistoryDAO();
        try {
            Vector items = thdao.getListByRequirementHistory(rhto);
            response = this.getTechCommentsFromTask(items, reader);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }


    public String getTechCommentsFromTask(Vector items, CustomerTO reader) throws BusinessException {
        StringBuffer buff = new StringBuffer();

        Locale loc = reader.getLocale();
        String label = " " + reader.getBundle().getMessage(loc, "label.requestHistory.wrote");

        Iterator i = items.iterator();
        while(i.hasNext()){
            TaskHistoryTO thto = (TaskHistoryTO)i.next();
            if (thto.getComment()!=null && thto.getComment().length()>0 ){

                buff.append("\n------" + thto.getHandler().getUsername() + 
                        label + DateUtil.getDateTime(thto.getDate(), loc, 2, 2) + "------");
                buff.append("\n" + thto.getComment() + "\n");                    
            }
        }

        return buff.toString();    	
    }
    
    public long getMaxID() throws BusinessException {
        long response = -1;
        try {
            response = dao.getMaxId("task");
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
        return response;
    }


	public int getReopenTimes(TaskTO tto) throws BusinessException {
		TaskDelegate tdel = new TaskDelegate();
		Vector reopenList = new Vector();
		int response = 1;
		
		if (tto!=null) {
			
			if (!tto.isParentTask()) {
				reopenList = tdel.getHistory(tto.getId());
			}

			if (reopenList.size()>0) {				
				for(int i=0 ; i < reopenList.size(); i++) {
					TaskHistoryTO thto = (TaskHistoryTO)reopenList.elementAt(i);
					if (thto.getStatus().getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_REOPEN)) {
						response++;							
					}
				}
			}
			
		}
		
		return response;
	}



}
