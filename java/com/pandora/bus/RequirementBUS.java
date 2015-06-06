package com.pandora.bus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.apache.struts.util.MessageResources;

import com.pandora.CustomerTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.RequirementTriggerTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.dao.RequirementDAO;
import com.pandora.dao.RequirementHistoryDAO;
import com.pandora.delegate.TaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.DifferentProjectFromParentReqException;
import com.pandora.exception.MandatoryMetaFieldBusinessException;
import com.pandora.exception.MandatoryMetaFieldDataException;
import com.pandora.exception.ParentReqNotExistsException;
import com.pandora.exception.ParentReqSameReqException;

/**
 * This class contain the business rules related with Requirement entity.
 */
public class RequirementBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    RequirementDAO dao = new RequirementDAO();
    
    
    /**
     * Get a list of all Requirement TOs from data base based on user id and 
     * boolean flag that must be used by searching.
     */
    public Vector<RequirementTO> getListByUser(UserTO uto, boolean hideClosed, boolean sharingView) throws BusinessException{
        Vector<RequirementTO> response = new Vector<RequirementTO>();
        try {
            response = dao.getListByUser(uto, hideClosed, sharingView);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    public void changeRequirementStatus(ResourceTaskTO rtto, Integer newState, String taskComment) throws BusinessException{
        try {
            dao.changeRequirementStatus(rtto, newState, taskComment);
        } catch (Exception e) {
            throw new  BusinessException(e);
        }
    }

    
    public Vector getListUntilID(String initialId, String finalId) throws BusinessException{
        Vector response = new Vector();
        try {
            response = dao.getListUntilID(initialId, finalId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    
    /**
     * Get a list of all Requirement TOs from data base based on user and project.
     */
    public Vector<RequirementTO> getListByUserProject(UserTO uto, ProjectTO pto, boolean sharingView) throws BusinessException{
        Vector<RequirementTO> response = new Vector<RequirementTO>();
        try {
            response = dao.getListByUserProject(uto, pto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }    
        
    /**
     * Get a list of all Requirement TOs from data base based on project id.
     */
    public Vector<RequirementTO> getListByProject(ProjectTO pto, String status, String requester, String priority, String categoryName, 
    		String viewMode) throws BusinessException {
    	Vector<RequirementTO> response = new Vector<RequirementTO>();
    	ProjectBUS pbus = new ProjectBUS();
    	try {
        	String templateId = null;
        	boolean isHierarchy = false;
        	if (viewMode!=null) {
            	isHierarchy = viewMode.equals("0");
            	if (!viewMode.equals("-1") && !viewMode.equals("0")) {
            		templateId = viewMode;
            	}    		
        	}
        	
        	String idsList = pbus.getProjectIn(pto.getId(), true);
        	response = dao.getListByProject(idsList, status, requester, priority, categoryName, templateId);

            if (isHierarchy && response!=null) {
            	
            	//set the objects into the hash
            	HashMap<String, RequirementTO> hm = new HashMap<String, RequirementTO>();
                Iterator<RequirementTO> i = response.iterator();
                while(i.hasNext()){
                	RequirementTO r = i.next();
                	hm.put(r.getId(), r);
                }
                
            	//get first requirements parent 
                Vector<RequirementTO> hierarchyList = new Vector<RequirementTO>();
                Iterator<RequirementTO> j = response.iterator();
                while(j.hasNext()){
                	RequirementTO r = j.next();
                	Vector<PlanningRelationTO> parentList = PlanningRelationTO.getRelation(r.getRelationList(), PlanningRelationTO.RELATION_PART_OF, r.getId(), true);
                	if (parentList==null || parentList.size()==0) {
                		RequirementTO currentParent = (RequirementTO)hm.get(r.getId());
                		this.checkHierarchy(currentParent, hm, hierarchyList, 0);	
                	}
                }
                
                response = hierarchyList;
            }    		
            
    	} catch(Exception e) {
    		throw new  BusinessException(e);
    	}
        
        return response;
    }
    
    
    private void checkHierarchy(RequirementTO r, HashMap<String, RequirementTO> hm, 
    		Vector<RequirementTO> hierarchyList, int level){

    	r.setGridLevel(level);
    	hierarchyList.add(r);

    	Vector<PlanningRelationTO> childrenList = PlanningRelationTO.getRelation(r.getRelationList(), PlanningRelationTO.RELATION_PART_OF, r.getId(), false);
    	if (childrenList!=null && childrenList.size()>0) {
    		level++;    		
            Iterator<PlanningRelationTO> i = childrenList.iterator();
            while(i.hasNext()){
            	PlanningRelationTO pr = i.next();
            	if (pr!=null && pr.getPlanning()!=null && pr.getPlanning().getId()!=null) {
                	RequirementTO clone = (RequirementTO)hm.get(pr.getPlanning().getId());
                	if (clone!=null) {
                		RequirementTO child = new RequirementTO(clone);
                		this.checkHierarchy(child, hm, hierarchyList, level);
                	} 
            	}
            }          
    	}
    	
    }
    
    public Vector<RequirementTO> getThinListByProject(ProjectTO pto, String categoryId) throws BusinessException{
        Vector<RequirementTO> response = new Vector<RequirementTO>();
        ProjectBUS pbus = new ProjectBUS();
        try {
            
            //get the requirements of child projects 
            Vector<ProjectTO> childs = pbus.getProjectListByParent(pto, true);
            Iterator<ProjectTO> i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = (ProjectTO)i.next();
                Vector<RequirementTO> reqOfChild = this.getThinListByProject(childProj, categoryId);
                response.addAll(reqOfChild);
            }
           
            Vector<RequirementTO> reqOfParent = dao.getThinListByProject(pto, categoryId);
            response.addAll(reqOfParent);
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;    	
    }
   
    
    
    /**
     * Get a list of all Requirement TOs from data base based on project id and
     */
    public Vector<RequirementTO> getListByFilter(ProjectTO pto, CustomerTO cto, Vector kwList) throws BusinessException {
    	Vector<RequirementTO> response = new Vector<RequirementTO>();
        try {
            response = dao.getListByFilter(pto, cto, kwList);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Get a list of all pending Requirement TOs into data base based on leader user.
     * @param uto
     * @param exceptCurrUserReq if true, not consider the requirements of the current user
     * @return
     * @throws BusinessException
     */
    public Vector<RequirementTO> getPendingListByUser(UserTO uto, boolean exceptCurrUserReq) throws BusinessException {
        Vector<RequirementTO> response = new Vector<RequirementTO>();
        try {
            response = dao.getPendingListByUser(uto, exceptCurrUserReq);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Get a specific Requirement object from data base.
     */
    public RequirementTO getRequirement(RequirementTO filter) throws BusinessException{
        RequirementTO response = null;
        try {
            response = (RequirementTO)dao.getObject(filter);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }     
    
    
    /**
     * Get a list of Requirement History objects from data base, base on requirement id.
     */
    public Vector<RequirementHistoryTO> getHistory(String reqId) throws BusinessException{
        Vector<RequirementHistoryTO> response = new Vector<RequirementHistoryTO>();
        try {
            RequirementHistoryDAO dao = new RequirementHistoryDAO();
            response = dao.getListByRequirement(reqId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }   
    
    /**
     * Insert a new Requirement object into data base
     */
    public void insertRequirement(RequirementTO rto) throws BusinessException{
        try {
            dao.insert(rto);
        } catch (MandatoryMetaFieldDataException e) {
        	throw new MandatoryMetaFieldBusinessException(e, e.getAfto());
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }

    /**
     * Insert a list of new Requirements into data base
     */
	public void insertRequirement(Vector<RequirementTO> rlist) throws BusinessException{
        try {
            dao.insertList(rlist);
        } catch (MandatoryMetaFieldDataException e) {
        	throw new MandatoryMetaFieldBusinessException(e, e.getAfto());
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}
    
    
    /**
     * Update data of Requirement object into data base
     */
    public void updateRequirement(RequirementTO rto) throws BusinessException{
        try {            
            if (!rto.isAdjustment()) {
                //double check for valid requirement status to update
                RequirementTO rtoCheck = this.getRequirement(rto);
                RequirementStatusTO rsto = rtoCheck.getRequirementStatus();
                if (!rsto.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_WAITING) &&
                        !rsto.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_CLOSE)){
                    throw new  BusinessException("The current requirement cannot be updated, because the current status is not 'Wait Approve' or 'Close'");    
                }
            }

            if (rto.isReopening()) {
                rto.setReopeningOccurrences(new Integer(rto.getReopeningOccurrences().intValue() + 1));
            }
            
            if (rto.getParentRequirementId()!=null) {
            	String[] ids = rto.getParentRequirementId().split(";");
            	
            	for (int i=0; i<ids.length; i++) {
            		
                    //check if the parent id contain the same id of current requirement            		
                    if (ids[i].trim().equals(rto.getId())) {
                    	throw new ParentReqSameReqException("The requirement [" + rto.getId() + "] cannot be a parent requirement of it self.");
                    }
                    
                    //check if the current requirement and the parent req are into the same project
                    if (!rto.getParentRequirementId().trim().equals("")) {
                    	if (!ids[i].trim().equals("")) {
                        	RequirementTO parentReq = (RequirementTO) dao.getObject(new RequirementTO(ids[i].trim()));
                        	if (parentReq!=null) {
                        		if (!parentReq.getProject().getId().equals(rto.getProject().getId())) {
                        			throw new DifferentProjectFromParentReqException("The requirement [" + rto.getId() + "] is not into the same project of requirement [" + rto.getParentRequirementId() + "]");
                        		}
                        	} else {
                        		throw new ParentReqNotExistsException("The requirement [" + rto.getParentRequirementId() + "] not exists.");
                        	}            		                    		
                    	}
                    }                
            	}
            }
            
            dao.update(rto);
        
        } catch (MandatoryMetaFieldDataException e) {
        	throw new MandatoryMetaFieldBusinessException(e, e.getAfto());
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }

    /**
     * Change the status of Requirement based on state transitioned from task.
     */
    public RequirementTriggerTO getRequirementToChangeStatus(TaskTO tto, Integer newTaskState, Integer newReqState) throws BusinessException{
        String bundleKey = "";

        if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_PROGRESS)){
            bundleKey = "message.reqChangeStatus.progress";
        } else if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_OPEN)){
            bundleKey = "message.reqChangeStatus.open";
        } else if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_REOPEN)){
        	bundleKey = "message.reqChangeStatus.open";
        } else if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_CANCEL)){
        	if (newReqState==null || newReqState.equals(RequirementStatusTO.STATE_MACHINE_WAITING)) {
        		bundleKey = "message.reqChangeStatus.cancel";	
        	} else {
        		bundleKey = "message.reqChangeStatus.close";
        	}
        } else if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
            bundleKey = "message.reqChangeStatus.close";
        } else if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_HOLD)){
            bundleKey = "message.reqChangeStatus.hold";
        }
                
        return this.getRequirementToChangeStatus(tto, newTaskState, newReqState, bundleKey);
    }


    /**
     * Change the status of Requirement based on state transitioned from task.
     */
    private RequirementTriggerTO getRequirementToChangeStatus(TaskTO tto, Integer newTaskState, Integer newReqState, String bundleKey) throws BusinessException{
        RequirementBUS rbus = new RequirementBUS();
        String comment = "";
        MessageResources mr = tto.getHandler().getBundle();
        RequirementTriggerTO response = new RequirementTriggerTO();

        //get the locale related with requester, if exists...
        Locale loc = tto.getHandler().getLocale();
        if (tto.getRequirement()!=null){
            UserTO requester = tto.getRequirement().getRequester();
            if (requester==null) {
                tto.setRequirement(rbus.getRequirement(tto.getRequirement()));
                requester = tto.getRequirement().getRequester();
            }
            loc = requester.getLocale();            
        }

        if (mr!=null) {
            comment = mr.getMessage(loc, bundleKey);    
        }
        
        if (newReqState==null){
            if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_PROGRESS)){
                newReqState = RequirementStatusTO.STATE_MACHINE_PROGRESS;
            } else if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_OPEN) ||
            	       newTaskState.equals(TaskStatusTO.STATE_MACHINE_REOPEN) ||
                       newTaskState.equals(TaskStatusTO.STATE_MACHINE_HOLD)){
    	        newReqState = RequirementStatusTO.STATE_MACHINE_PLANNED;
            } else if (newTaskState.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
                newReqState = RequirementStatusTO.STATE_MACHINE_CLOSE;
            }                     
        }
        
        response.setComment(comment);
        response.setNewState(newReqState);
        
        return response;
    }
    
        
    /**
     * Cancel a Requirement from data base. Verify if Requirement can be canceled 
     * (if current) request don't have referentes into Task table.
     */
    public void removeRequirement(RequirementTO rto) throws BusinessException{
        try {
            //double check for requirement status removing permission
            RequirementTO rtoCheck = this.getRequirement(rto);
            RequirementStatusTO rsto = rtoCheck.getRequirementStatus();
            if (!rsto.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_WAITING)){
                throw new  BusinessException("The current requirement cannot be removed, because the current status is not 'Wait Approve'");    
            }
            
            dao.remove(rto);
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }

    
    public void deleteRequirement(String id) throws BusinessException{
        try {
        	
            //double check for requirement status removing permission        	
        	RequirementTO rto = this.getRequirement(new RequirementTO(id));
        	TaskDelegate tdel = new TaskDelegate();
        	Vector taskList = tdel.getTaskListByRequirement(rto, rto.getProject(), true);
            if (taskList!=null && taskList.size()>0){
                throw new BusinessException("The current requirement cannot be removed, because there are tasks liked with it.");    
            }
            
            dao.deleteFromDB(rto);
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }    	
    }
    
    
    /**
     * Refuse a Requirement. Set the current status of requirement to 'refuse' and
     * create a new record into requirement history with a comment.
     */
    public void refuseRequirement(RequirementTO rto, UserTO uto, String comment) throws BusinessException { 
        try {        
            dao.refuse(rto, uto, comment);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }
    
    
    /**
     * Verify if the requirement object related is valid (requirement status <> finished).
     */
    /*
    public boolean isValidRequirement(RequirementTO rto) throws BusinessException{
        boolean response = true;
		try {
		    if (rto!=null) {
		        rto = this.getRequirement(rto);
		        if (rto!=null){
			        RequirementStatusTO cSt = rto.getRequirementStatus();
			        if (cSt.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_CANCEL) ||
			                cSt.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_CLOSE) ||
			                cSt.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_REFUSE)){
			            response = false;
			        }	            
		        }		        
		    }

        } catch (BusinessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
     */

    public long getMaxID() throws BusinessException {
        long response = -1;
        try {
            response = dao.getMaxId("requirement");
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
        return response;
    }


	public Vector<RequirementWithTasksTO> getRequirementWithTaskList(String projectId, String iterationId, boolean hideFinishedReqs, boolean hideOldIterations) throws BusinessException {
        Vector<RequirementWithTasksTO> response = new Vector<RequirementWithTasksTO>();
        ProjectBUS pbus = new ProjectBUS();
        try {
            
            //get the requirements of child projects 
        	ProjectTO pto = new ProjectTO(projectId);
            Vector<ProjectTO> childs = pbus.getProjectListByParent(pto, true);
            Iterator<ProjectTO> i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = i.next();
                Vector<RequirementWithTasksTO> reqOfChild = this.getRequirementWithTaskList(childProj.getId(), iterationId, hideFinishedReqs, hideOldIterations);
                response.addAll(reqOfChild);
            }
           
            Vector<RequirementWithTasksTO> reqs = dao.getRequirementWithTaskList(pto, iterationId, hideFinishedReqs, hideOldIterations);
            response.addAll(reqs);
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

}
