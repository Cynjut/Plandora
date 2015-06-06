package com.pandora.bus;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.AdditionalFieldTO;
import com.pandora.CategoryTO;
import com.pandora.LeaderTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.bus.kb.IndexEngineBUS;
import com.pandora.dao.ResourceTaskDAO;
import com.pandora.dao.TaskDAO;
import com.pandora.dao.TaskHistoryDAO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.TaskStatusDelegate;
import com.pandora.exception.AcceptTaskInsertionException;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.InvalidTaskStateTransitionException;
import com.pandora.exception.InvalidTaskTimeException;
import com.pandora.exception.InvalidUserRoleException;
import com.pandora.exception.MandatoryMetaFieldBusinessException;
import com.pandora.exception.MandatoryMetaFieldDataException;
import com.pandora.exception.TasksDiffRequirementException;
import com.pandora.exception.WaitPredecessorUpdateException;
import com.pandora.helper.DateUtil;

/**
 * This class contain the business rules related with Resource Task entity.
 */
public class ResourceTaskBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    ResourceTaskDAO dao = new ResourceTaskDAO();
    

	public HashMap<String, ResourceTaskAllocTO> getHashAlloc(String projectFilter, String resourceFilter, Timestamp iniDate, Timestamp finalDate) throws BusinessException {
		HashMap<String, ResourceTaskAllocTO> response = new HashMap<String, ResourceTaskAllocTO>();
        try {
        	response = dao.getHashAlloc(projectFilter, resourceFilter, iniDate, finalDate);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }                
		return response;
	}

	
    /**
     * Get all taskResources related with a task object.
     */
    public Vector<ResourceTaskTO> getListByTask(TaskTO tto) throws BusinessException {
        Vector<ResourceTaskTO> list = null;
        try {
            list = dao.getListByTask(tto, false);
            return list;
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }                
    }

    /**
     * Get all taskResources related with a project object and some search criteria. 
     */
    public Vector<ResourceTaskTO> getListByProject(ProjectTO pto, String statusSel, String resourceSel, String dateRangeSel, boolean isProjectHierarchy) throws BusinessException {
        ProjectBUS pbus = new ProjectBUS();
        Vector<ResourceTaskTO> list = new Vector<ResourceTaskTO>();
        try {

            //get the tasks of child projects        	
        	if (isProjectHierarchy) {
                Vector<ProjectTO> childs = pbus.getProjectListByParent(pto, true);
                Iterator<ProjectTO> i = childs.iterator();
                while(i.hasNext()){
                    ProjectTO childProj = (ProjectTO)i.next();
                    Vector<ResourceTaskTO> tskOfChild = this.getListByProject(childProj, statusSel, resourceSel, dateRangeSel, isProjectHierarchy);
                    list.addAll(tskOfChild);
                }        		
        	}
                        
            Vector<ResourceTaskTO> tskOfParent = dao.getListByProject(pto, statusSel, resourceSel, dateRangeSel);
            list.addAll(tskOfParent);
            
            return list;
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }                
    }


    /**
     * Update a list of Resource Task object into data base.
     */
    public void removeAllAndInsert(TaskTO tto, Vector<ResourceTaskTO> v) throws BusinessException {     
        try {
                        
            //get a current list of Resource Task From database (including canceled resourceTask objects)
            Vector<ResourceTaskTO> currentList = dao.getListByTask(tto, false);
            
            Vector<ResourceTaskTO> removeList = this.minus(currentList, v, true);
            Vector<ResourceTaskTO> insertList = this.minus(v, currentList, false);
            Vector<ResourceTaskTO> updateList = this.merge(currentList, v);
                        
            //insert, insert or update data of resource task into database
            dao.updateLists(tto, removeList, insertList, updateList);
        
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }    


    /**
     * Get a list of resource tasks based on resource object
     */
    public Vector<ResourceTaskTO> getTaskListByResource(ResourceTO rto, boolean hideClosed) throws BusinessException {
        Vector<ResourceTaskTO> response = new Vector<ResourceTaskTO>();
        try {
            response = dao.getTaskListByResource(rto, hideClosed);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }


    /**
     * Get a list of resource tasks based on resource object and project object.
     */
    public Vector<ResourceTaskTO> getTaskListByResourceProject(ResourceTO rto, ProjectTO pto) throws BusinessException {
        Vector<ResourceTaskTO> response = new Vector<ResourceTaskTO>();
        try {
            response = dao.getTaskListByResourceProject(rto, pto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }


    /**
     * Get a resource task object from data base
     */
    public ResourceTaskTO getResourceTaskObject(ResourceTaskTO rtto) throws BusinessException {
        ResourceTaskTO response = null;
        try {
            response = (ResourceTaskTO)dao.getObject(rtto);
            
            if (response!=null && response.getTaskStatus()!=null) {
            	
                //check if the current resource_task is the last 'in-progress' of all allocs from task
        	    Integer cs = response.getTaskStatus().getStateMachineOrder();
        	    if (cs.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) && 
        	    		response.getTask()!=null && 
        	    		response.getTask().getDecisionNode()!=null) {

        	    	boolean lastNotClosedTask = true;
                    Vector<ResourceTaskTO> allocs = rtto.getTask().getAllocResources();
                	if (allocs.size()>1) {
                		Iterator<ResourceTaskTO> i = allocs.iterator();
                		while (i.hasNext()) {
                			ResourceTaskTO dummy = i.next();
                			if (!dummy.getResource().getId().equals(rtto.getResource().getId())) {
                				TaskStatusTO tsto = dummy.getTaskStatus();
                				if (!tsto.isFinish()) {
                					lastNotClosedTask = false;
                					break;
                				}
                			}
                			
                		}
                	}            
                	response.setIsTheWIPTask(lastNotClosedTask);
                	
        	    } else {
                	response.setIsTheWIPTask(false);
        	    }            	
            }

        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }    


    /**
     * Calculate the number of slots for a bunch of ResourceTask objects.
     */
    public Vector<ResourceTaskTO> generateAllocation(Vector<ResourceTaskTO> resourceTaskList) throws BusinessException{
        Vector<ResourceTaskTO> response = null;
        
        if (resourceTaskList!=null){
            Iterator<ResourceTaskTO> i = resourceTaskList.iterator();
            while(i.hasNext()){
                ResourceTaskTO rtto = i.next();
                
                //re-build the list of resourceTask allocations (if necessary)...
                rtto = this.generateAllocation(rtto);
                if (response==null) response = new Vector<ResourceTaskTO>();
                response.addElement(rtto);
            }
        }
        return response;
    }


    /**
     * Calculate the number of slots that each Resource Task
     * should have and generate a list of allocation objects into it.
     */
    public ResourceTaskTO generateAllocation(ResourceTaskTO rtto) throws BusinessException{
        Vector<ResourceTaskAllocTO> allocList = new Vector<ResourceTaskAllocTO>();
        Calendar c = Calendar.getInstance();
        int seq = 0;
        
        if (rtto.getAllocList()==null) {
            
            int total = this.getPreferedTime(rtto).intValue();
            Timestamp iniDate = this.getPreferedDate(rtto);

            while(total>0){
                int value = 0;                
                Timestamp cursor = DateUtil.getChangedDate(iniDate, Calendar.DATE, seq);
                int capacity = rtto.getResource().getCapacityPerDay(cursor).intValue();
                
                if (capacity==0) {
                	capacity = ResourceTO.DEFAULT_FULLDAY_CAPACITY;
                }
                
                c.setTimeInMillis(cursor.getTime());
                seq++;            
                if (c.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY && 
                        c.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) {
                    if (total > capacity){
                        value = capacity;
                    } else {
                        value = total; //get remaining capacity...
                    }
                    total = total - capacity;                
                }

                //create a new allocation object to store the calculated value of slot
                ResourceTaskAllocTO rtato = new ResourceTaskAllocTO(rtto, seq, value);
                allocList.addElement(rtato);
            }
            
            //if the current resource task don't has any allocation specified, set a default slot with value=0
            if (allocList.size()==0){
                ResourceTaskAllocTO dummy = new ResourceTaskAllocTO(rtto, 1, 0);
                allocList.addElement(dummy);
            }
            
            rtto.setAllocList(allocList);
        }
        
        return rtto;
    }


    public void updateResourceTask(ResourceTaskTO rtto) throws BusinessException {
    	try {
    		dao.update(rtto);	
    	}catch(Exception e){
    		throw new  BusinessException(e);    		
    	}
	}

   
    /**
     * Change the status of Task.
     */
    public void changeTaskStatus(ResourceTaskTO rtto, Integer newState, String comment, Vector<AdditionalFieldTO> additionalFields, boolean isMTClosingAllowed) throws BusinessException{
        TaskBUS tbus = new TaskBUS();        
        try {

		    TaskTO tto = rtto.getTask();
		    
		    //for tasks "not open" the 'actual time' should be > 0
            if (newState.equals(TaskStatusTO.STATE_MACHINE_CLOSE) || newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL)) {
            	if (rtto.getActualTimeInHours()<=0) {
            		throw new InvalidTaskTimeException();	
            	}
            	
            	//if estimated time is zero, and the task will be closed, set the estimated time with actual time 
            	if (rtto.getEstimatedTime()==null || rtto.getEstimatedTime().intValue()==0) {
            		rtto.setEstimatedTime(rtto.getActualTime());
            	}
		    }

		    //if the current status is OPEN but the actual time was changed...improve the status to on-Progress
            if ((newState.equals(TaskStatusTO.STATE_MACHINE_OPEN) || newState.equals(TaskStatusTO.STATE_MACHINE_REOPEN)) 
            		&& rtto.getActualTime().intValue()>0) {
                newState = TaskStatusTO.STATE_MACHINE_PROGRESS; 
            }

		    if (tbus.isBlocked(tto) && !newState.equals(TaskStatusTO.STATE_MACHINE_OPEN)) {
		    	throw new WaitPredecessorUpdateException();
		    }
            
		    //Cancel status could be used only by leader...
		    if (newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL) && !(rtto.getHandler() instanceof LeaderTO)){
		        throw new InvalidTaskStateTransitionException("The Cancel status should be used only by leader role.");
		    }

		    //check if is should create a new task (Ad hoc task)
		    String iteration = tto.getIteration();
		    if (rtto.isAdHocTask()) {
		        tto = TaskTO.getAdHocTask(rtto);
		        this.checkParentData(rtto, tto);
	            tto.setAdditionalFields(additionalFields);		        
			    if (newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL) || newState.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
			    	tto.setFinalDate(DateUtil.getNow());
			    }
			    tto.setIteration(iteration);
		        tbus.insertTask(tto);
		        
		    } else {
	            //load data of related task object from data base 
	            tto = tbus.getTaskObject(tto);
	            this.checkParentData(rtto, tto);
	            tto.setHandler(rtto.getHandler());
	            tto.setAdditionalFields(additionalFields);
	            tto.setIteration(iteration);
	            rtto.setTask(tto);
	            
                //check if 'current state' and 'new state' is a allowed transition
                if (!this.checkStateTransition(rtto, newState)){
                    throw new InvalidTaskStateTransitionException();    
                }
                
                //if the current user is the owner of task, update some information.
	            if (rtto.isCurrentTaskCreator()) {
	            	tto.setCategory(new CategoryTO(rtto.getAdHocCategoryId()));
	            	tto.setName(rtto.getAdHocTaskName());
	            	tto.setDescription(rtto.getAdHocTaskDescription());
	            	tbus.updateByOwner(tto);
	            }
                
                //update status of resource task object                
                dao.changeStatus(rtto, newState, comment, isMTClosingAllowed);
		    }		    		    
            
			//update the content of object into Knowledge Base
	        IndexEngineBUS ind = new IndexEngineBUS();
	        ind.update(rtto);

        } catch (WaitPredecessorUpdateException e) {
        	throw new WaitPredecessorUpdateException(e);
        } catch (TasksDiffRequirementException e) {
        	throw new TasksDiffRequirementException();
        } catch (InvalidTaskTimeException e) {
        	throw new InvalidTaskTimeException();
        } catch (AcceptTaskInsertionException e) {
            throw new InvalidUserRoleException(e);
        } catch (MandatoryMetaFieldBusinessException e) {
        	throw e;
        } catch (MandatoryMetaFieldDataException e) {
            throw new MandatoryMetaFieldBusinessException(e, e.getAfto());
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        } catch (Exception e) {
            throw new  BusinessException(e);
		}
    }


	private void checkParentData(ResourceTaskTO rtto, TaskTO tto) throws DataAccessException, TasksDiffRequirementException, BusinessException {
        TaskBUS tbus = new TaskBUS();   
        
		TaskTO parentTask = rtto.getTask().getParentTask();
		if (parentTask!=null && !parentTask.getId().equals("-1")) {
			tto.setParentTask(parentTask);
			
			//make sure that the parent and sub task contain the same requirement...
			if (!tbus.isTaskAndParentTaskSameRequirement(tto)) {
				throw new TasksDiffRequirementException();
			} else {
		    	parentTask = tbus.getTaskObject(parentTask);
		    	tto.setRequirement(parentTask.getRequirement());
			}
				            	
		} else {
			tto.setParentTask(null);
		}
	}
    

    
    /**
     * Check if a resourceTask can be removed
     */
    public boolean validateCancelAction(ResourceTaskTO rtto) {
	    boolean response = false;
	    Integer cs = rtto.getTaskStatus().getStateMachineOrder();
	    if (cs.equals(TaskStatusTO.STATE_MACHINE_OPEN) || 
	    		cs.equals(TaskStatusTO.STATE_MACHINE_REOPEN) ||
	            cs.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) ||
	            	cs.equals(TaskStatusTO.STATE_MACHINE_HOLD)){
	        response = true;
	    }
	    return response;
    }

    
    /**
     * Check if 'current state' and 'new state' is a allowed transition. <br>
     * See the Task state machine diagram into system documentation for more information.
     */
    private boolean checkStateTransition(ResourceTaskTO rtto, Integer newState) throws BusinessException{
        boolean isOk = false;
	    Integer currStatus = this.getCurrentState(rtto);
	    
        //The OPEN state can only succeed the IN PROGRESS, CANCEL or OPEN states
        if (currStatus.equals(TaskStatusTO.STATE_MACHINE_OPEN) && 
                (newState.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) || 
                   newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL) || 
                   newState.equals(TaskStatusTO.STATE_MACHINE_OPEN))){
            isOk = true;
        } else if (currStatus.equals(TaskStatusTO.STATE_MACHINE_REOPEN) && 
                    (newState.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) || 
                       newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL) || 
                       newState.equals(TaskStatusTO.STATE_MACHINE_REOPEN))){
            isOk = true;
        } else if (currStatus.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) && 
                (newState.equals(TaskStatusTO.STATE_MACHINE_HOLD) || 
                   newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL) || 
                   newState.equals(TaskStatusTO.STATE_MACHINE_CLOSE) ||
                   newState.equals(TaskStatusTO.STATE_MACHINE_PROGRESS))){
            //The IN PROGRESS state can only succeed the HOLD, CLOSE, CLOSE_ACCEPT and IN PROGRESS states
            isOk = true;
        } else if (currStatus.equals(TaskStatusTO.STATE_MACHINE_HOLD) && 
                (newState.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) || 
                   newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL) || 
                   newState.equals(TaskStatusTO.STATE_MACHINE_HOLD))){
            //The HOLD state can only succeed the IN PROGRESS, CANCEL and HOLD states
            isOk = true;
        } else if (currStatus.equals(TaskStatusTO.STATE_MACHINE_CLOSE) && 
                  newState.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
            //The CLOSE state can be updated if new state is also CLOSE
            isOk = true;
        } else if (currStatus.equals(TaskStatusTO.STATE_MACHINE_CANCEL) && 
                newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL)){
          //The CANCEL state can be updated if new state is also CANCEL
          isOk = true;
      }        
        return isOk;
    }

    /**
     * Retrieve the current state of task from current ResourceTask object
     * @return
     * @throws BusinessException
     */
    private Integer getCurrentState(ResourceTaskTO rtto) throws BusinessException{
        TaskStatusBUS tsbus = new TaskStatusBUS();
        Integer currStatus = null;
        
        //load current machine order from data base 
        rtto.setTaskStatus(tsbus.getObjectByResourceTask(rtto));
        currStatus = rtto.getTaskStatus().getStateMachineOrder();
        
        return currStatus;
    }
    
    /**
     * Perform the operation of minus between two vectors.
     */
    private Vector<ResourceTaskTO> minus(Vector<ResourceTaskTO> v1, Vector<ResourceTaskTO> v2, boolean isRemoving){
        Vector<ResourceTaskTO> response = null;
        boolean find = false;
        
        if (v1!=null){
            Iterator<ResourceTaskTO> i = v1.iterator();
            while (i.hasNext()){
                find = false;
                ResourceTaskTO rtto1 = i.next();
                rtto1.setId(rtto1.getTask().getId() + "|" + rtto1.getResource().getId() + "|" + rtto1.getTask().getProject().getId());
                if (v2!=null){
                    Iterator<ResourceTaskTO> j = v2.iterator();
                    while (j.hasNext()){
	                    ResourceTaskTO rtto2 = j.next();
	                    rtto2.setId(rtto2.getTask().getId() + "|" + rtto2.getResource().getId() + "|" + rtto2.getTask().getProject().getId());
	                    if (rtto2.getHandler()!=null) rtto1.setHandler(rtto2.getHandler());
	                    if (rtto1.getId().equals(rtto2.getId())){
	                        find = true;
	                    }
                    }
                } else {
                    response = v1;
                }
                
                if (find == false){
                    boolean ok = true;
                    
                    if (isRemoving) {
                        //if is removing...verify if current Resource Task is currently with open status
                    	Integer state = rtto1.getTaskStatus().getStateMachineOrder(); 
                        if (!state.equals(TaskStatusTO.STATE_MACHINE_OPEN) && 
                        		!state.equals(TaskStatusTO.STATE_MACHINE_REOPEN)){
                            ok = false;
                        }
                    }

                    if (ok){
                        if (response==null) response = new Vector<ResourceTaskTO>();
                        response.addElement(rtto1);                        
                    }
                }
            }            
        }
        
        return response;
    }
    
    
    private Vector<ResourceTaskTO> merge(Vector<ResourceTaskTO> v1, Vector<ResourceTaskTO> v2) {
        Vector<ResourceTaskTO> response = null;
        
        if (v1!=null && v2!=null){
            Iterator<ResourceTaskTO> i = v1.iterator();
            while (i.hasNext()){
                ResourceTaskTO rtto1 = i.next();
                rtto1.setId(rtto1.getTask().getId() + "|" + rtto1.getResource().getId() + "|" + rtto1.getTask().getProject().getId());

                Iterator<ResourceTaskTO> j = v2.iterator();
                while (j.hasNext()){
                    ResourceTaskTO rtto2 = j.next();
                    rtto2.setId(rtto2.getTask().getId() + "|" + rtto2.getResource().getId() + "|" + rtto2.getTask().getProject().getId());
                    if (rtto1.getId().equals(rtto2.getId())){
                        
                        //only resourceTask with Open, Close and Cancel status should be updated...
                        Integer state = rtto2.getTaskStatus().getStateMachineOrder();
                        if (state.equals(TaskStatusTO.STATE_MACHINE_OPEN) || state.equals(TaskStatusTO.STATE_MACHINE_REOPEN)) { 
                            if (response==null) response = new Vector<ResourceTaskTO>();
                            rtto2.setTask(rtto1.getTask());
                            response.addElement(rtto2);
                            break;                            
                        }
                    }                    
                }
            }            
        }
        
        return response;
    }    
    
    
    /**
     * Define the rules to return the representative status of Task object based on his 
     * ResourceTask list.
     * <li>If contain any Progress or Hold resourceTask, than the status of task is Progress; </li>
     * <li>If contain any Open resourceTask and not contain any Progress/Hold, than the status of task is Open; </li>
     * <li>If all status of ResourceTask is Close or Cancel, than the status of task is Close </li>
     *   
     * @param tto
     * @return
     */
    public Integer getStatusOfAllocResource(TaskTO tto){
        Integer response = new Integer(0); 
        Vector<ResourceTaskTO> list = tto.getAllocResources();
        boolean isIntermediate = false;
        boolean isInitial = false;        

        if (list==null || list.size()==0){
            ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
            try {
                list = rtdel.getListByTask(tto);
            } catch (BusinessException e) {
                list = null;
            }
        }

        //check if status of all resourceTasks objects is Open or In-Progress        
        if (list!=null) {
            if (list.size()>1){
                Iterator<ResourceTaskTO> i = list.iterator();
                while(i.hasNext()){
                    ResourceTaskTO rtto = i.next();
                    response = getStatusOfAllocResource(rtto);

                    //if (response.equals(TaskStatusTO.STATE_MACHINE_CANCEL) ||
                    //        response.equals(TaskStatusTO.STATE_MACHINE_CLOSE)) {
                    //    isFinished = true;
                    if (response.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) ||
                        	response.equals(TaskStatusTO.STATE_MACHINE_HOLD)) {
                        isIntermediate = true;
                        
                    } else if (response.equals(TaskStatusTO.STATE_MACHINE_OPEN) ||
                    		response.equals(TaskStatusTO.STATE_MACHINE_REOPEN)){
                        isInitial = true;
                        break;
                    }
                }
                
                //generic state for the task...
            	if (isIntermediate){
            	    response = TaskStatusTO.STATE_MACHINE_PROGRESS;            
            	} else if (isInitial) {
                    response = TaskStatusTO.STATE_MACHINE_OPEN;
                } else {
                    response = TaskStatusTO.STATE_MACHINE_CLOSE;
                }
                
            } else {
                if (list.size()>0) {
                    ResourceTaskTO rtto = (ResourceTaskTO)list.elementAt(0);
                    response = rtto.getTaskStatus().getStateMachineOrder();                    
                }
            }
        }

        return response;
    }
    
    
    /**
     * Define the rules to return the representative status of a list of ResourceTask.
     */
    public Integer getStatusOfAllocResource(ResourceTaskTO rtto){      
        TaskStatusTO tsto = rtto.getTaskStatus();
        Integer type = tsto.getStateMachineOrder();
        
        if (type==null){
            TaskStatusDelegate tdel = new TaskStatusDelegate();
            try {
                tsto = tdel.getTaskStatusObject(tsto);
                type = tsto.getStateMachineOrder();
            } catch (BusinessException e) {
                type = TaskStatusTO.STATE_MACHINE_OPEN; //default value
            }
        }        
        return type;
    }


    /**
     * Return the latest or newest date from the resourceTask list.
     */
    public Timestamp getDateFromResTaskList(Vector<ResourceTaskTO> resTaskList, boolean isInitial) {
        Timestamp ts = null;
        
        if (resTaskList!=null){
            Iterator<ResourceTaskTO> j = resTaskList.iterator();
            while(j.hasNext()){
                ResourceTaskTO rtto = j.next();
                Timestamp currDate = rtto.getInitialDate();
                if (ts==null) ts=currDate;

                if (isInitial){
                    if (ts.after(currDate)) ts=currDate;
                } else {
                    if (ts.before(currDate)) ts=currDate;
                }
            }            
        }
        return ts;
    }
    
    
    /**
     * The reference date is prioritally the actual date 
     * and optionally the start date.
     */
    public Timestamp getPreferedDate(ResourceTaskTO rtto) {
        Timestamp refDate = rtto.getActualDate();
        if (refDate==null){
            refDate = rtto.getStartDate();
        }
        return refDate;
    }

    /**
     * The reference time is prioritally the actual time 
     * and optionally the estimanted date.
     * 
     * @param rtto
     * @return
     */
    public Integer getPreferedTime(ResourceTaskTO rtto) {
        Integer refTime = rtto.getActualTime();
        if (refTime==null || (refTime.intValue()==0 && rtto.getEstimatedTime()!=null)){
            refTime = rtto.getEstimatedTime();
        }
        return refTime;
    }    
   
    
    /**
     * Get a list of Task History objects from data base, based on task id and resource id.
     */
    public Vector<TaskHistoryTO> getHistory(String taskId, String resourceId) throws BusinessException {
        Vector<TaskHistoryTO> response = new Vector<TaskHistoryTO>();
        try {
            TaskHistoryDAO dao = new TaskHistoryDAO();
            response = dao.getListByResourceTask(taskId, resourceId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    
    /**
     * Check if the requirement contain resource_tasks or requirements NOT CLOSED.
     * @param rto
     * @param involvedTask The resource task involved with closing event. This task must be ignored during the validation. 
     * @param involvedReq The requirement involved with closing event. This req must be ignored during the validation.
     */
    public boolean thereAreRemainingNonClosedNodes(RequirementTO rto, ResourceTaskTO involvedTask, RequirementTO involvedReq) throws BusinessException{
        boolean response = false;
        TaskDelegate tdel = new TaskDelegate();
        
        if (rto!=null) {
            
            //get all NON CLOSED tasks...
            Vector<TaskTO> taskListOfReq = tdel.getTaskListByRequirement(rto, rto.getProject(), false);
            if (taskListOfReq!=null && taskListOfReq.size()>0){
            	if (involvedTask!=null) {
                    Iterator<TaskTO> i = taskListOfReq.iterator();
                    while(i.hasNext()){
                        TaskTO tto = i.next();
                        tto = tdel.getTaskObject(tto);
                        Vector<ResourceTaskTO> resTaskList = tto.getAllocResources();
                        
                        if (resTaskList!=null) {
                            Iterator<ResourceTaskTO> j = resTaskList.iterator();
                            while (j.hasNext()) {
                                ResourceTaskTO rtto = j.next();
                                boolean sameTask = involvedTask.getTask().getId().equals(tto.getId());
                                boolean sameResource = involvedTask.getResource().getId().equals(rtto.getResource().getId());

                                if (!tto.isParentTask() && !rtto.getTaskStatus().isFinish() 
                                		&& (!sameResource || !sameTask)) {
                                    response = true; //I found a remaining resource_task not closed!!! 
                                    break;
                                }                        
                            }                        
                        }
                    }            		
            	} else {
            		response = true; //I found a remaining resource_task not closed!!!
            	}
            }
            
            //get all NON CLOSED reqs...
			Vector<PlanningRelationTO> relations = rto.getRelationList();
			Vector<PlanningRelationTO> children = PlanningRelationTO.getRelation(relations, 
									PlanningRelationTO.RELATION_PART_OF, rto.getId(), false);
			if (children!=null && children.size()>0) {
				if (involvedReq!=null) {
	                Iterator<PlanningRelationTO> i = children.iterator();
	                while(i.hasNext()) {
	                	PlanningRelationTO prto = i.next();
	                	if (prto!=null && prto.getPlanning()!=null && 
	                			prto.getPlanning().getFinalDate()==null && 
	                			!prto.getPlanning().getId().equals(involvedReq.getId())) {
	                		response = true; //I found a remaining child requirement not closed!!!
	                		break;
	                	}
	                }					
				} else {
					response = true; //I found a remaining child requirement not closed!!!
				}
			}
        }
                
        return response;
    }

    
	public Vector<ResourceTaskTO> getTasksWithoutReq(String projectId, String iteration, boolean hideOldIterations) throws BusinessException {
		TaskDelegate tdel = new TaskDelegate();
		ProjectBUS pbus = new ProjectBUS();
		Vector<ResourceTaskTO> response = new Vector<ResourceTaskTO>();
		
        //get the tasks of child projects
        Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(projectId), true);
        Iterator<ProjectTO> i = childs.iterator();
        while(i.hasNext()){
            ProjectTO childProj = i.next();
            Vector<ResourceTaskTO> tskOfChild = this.getTasksWithoutReq(childProj.getId(), iteration, hideOldIterations);
            response.addAll(tskOfChild);
        }
                    
		Vector<TaskTO> list = tdel.getTaskListByRequirement(null, new ProjectTO(projectId), true, false);
        if (list!=null) {
            Iterator<TaskTO> j = list.iterator();
            while (j.hasNext()) {
                TaskTO tto = j.next();
                if (tto.getRequirement()==null) {
                	if (iteration.equals("-1") || !hideOldIterations ||  
                			(hideOldIterations && tto.getIteration()!=null && tto.getIteration().equals(iteration))) {
                		response.addAll(tto.getAllocResources());	
                	}
                }
            }
        }
        
		return response;
	}    
	
    
    public Vector<ResourceTaskTO> getListUntilID(String initialId, String finalId) throws BusinessException {
        Vector<ResourceTaskTO> response = new Vector<ResourceTaskTO>();
        try {
            response = dao.getListUntilID(initialId, finalId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }


	public void changeAssignment(ResourceTaskTO rtto, UserTO uto, String comment) throws BusinessException {
        try {
            dao.changeAssignment(rtto, uto, comment);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
	}


	public ResourceTaskTO getResourceTaskObject(String taskId, String resourceId) throws BusinessException {
		ResourceTaskTO response = null;
		
		if (taskId!=null && resourceId!=null) {
			TaskDelegate tdel = new TaskDelegate();
			
			TaskTO tto = tdel.getTaskObject(new TaskTO(taskId));
			if (tto!=null) {
				ProjectTO pto = tto.getProject();
				ResourceTO rto = new ResourceTO(resourceId);
				
				ResourceTaskTO rtto = new ResourceTaskTO();
				rto.setProject(pto);

				rtto.setTask(tto);
				rtto.setResource(rto);
				response = this.getResourceTaskObject(rtto);
				response.setTask(tto);
			}			
		}
		return response;
	}


	public Vector<TaskTO> getNotClosedTasksUnderMacroTask(TaskTO subTask) throws BusinessException {
		TaskDAO tdao = new TaskDAO();
		Vector<TaskTO> response = new Vector<TaskTO>();
		
        try {
    	    TaskTO parenttask = subTask.getParentTask();
    	    if (parenttask!=null) {
    	    
    	    	tdao.loadSubTasksList(parenttask);
    	    	if (parenttask.getChildTasks()!=null) {
    		    	Iterator<TaskTO> j = parenttask.getChildTasks().values().iterator();
    		    	while(j.hasNext()) {
    		    		TaskTO t = j.next();
    		    		if (t.getFinalDate()==null && !t.getId().equals(subTask.getId())) {
    		    			response.addElement(t);	
    		    		}
    		    	}	
    		   }
    	    }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        
        return response;
	}

    public void updateByResource(ResourceTaskTO rtto) throws BusinessException {
        try {        
            if (rtto!=null){
            	dao.updateByResource(rtto);
            }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }    	
    }

    public ResourceTaskTO changeProject(String projectId, ResourceTaskTO rtto) throws BusinessException{
    	 try {
    		 
    		 if(!projectId.equals(rtto.getTask().getProject().getId())){
    			 CategoryDelegate cdel = new CategoryDelegate();
    			 
    			 CategoryTO ctoold = cdel.getCategory(rtto.getTask().getCategory());
        		 
        		 ProjectTO pto = new ProjectTO(projectId);
     		     Vector<CategoryTO> catlist = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, pto, false);
     		     
     		     CategoryTO ctonew = null;
     		     Iterator<CategoryTO> itCatList = catlist.iterator();
     		     while(itCatList.hasNext()){
     		    	 CategoryTO cto = itCatList.next();
     		    	 if(cto.getName().equals(ctoold.getName())){
     		    		 ctonew = cto;
     		    		 break;
     		    	 }
     		     }
    			 
     		     if(ctonew == null){
     		    	 ctonew = new CategoryTO("0");
     		     }
     		     
             	 dao.changeProject(projectId, rtto, ctonew);
             	 TaskTO tto = rtto.getTask();
             	 tto.setCategory(ctonew);
    		 }
         } catch (DataAccessException e) {
             throw new  BusinessException(e);
         }    
    	 return rtto;
    }

}
