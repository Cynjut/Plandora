package com.pandora.delegate;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;

import com.pandora.bus.TaskBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for Task entity information access.
 */
public class TaskDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
    TaskBUS bus = new TaskBUS();
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getTaskListByRequirement(com.pandora.RequirementTO, com.pandora.ProjectTO, boolean)
     */
    public Vector<TaskTO> getTaskListByRequirement(RequirementTO rto, ProjectTO pto, boolean evenClose) throws BusinessException {
        return bus.getTaskListByRequirement(rto, pto, evenClose, true);
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getTaskListByRequirement(com.pandora.RequirementTO, com.pandora.ProjectTO, boolean, boolean)
     */
    public Vector getTaskListByRequirement(RequirementTO rto, ProjectTO pto, boolean evenClose, boolean isSort) throws BusinessException {
        return bus.getTaskListByRequirement(rto, pto, evenClose, isSort);
    }
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.isUserTaskOwner(com.pandora.TaskTO, com.pandora.UserTO)
     */
    public boolean isUserTaskOwner(TaskTO tto, UserTO currentUser) {
    	return bus.isUserTaskOwner(tto, currentUser);
    }
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getAvailableParentTaskList(com.pandora.RequirementTO, java.lang.String)
     */
    public Vector<TaskTO> getAvailableParentTaskList(RequirementTO rto, String projectId) throws BusinessException {       
        return bus.getAvailableParentTaskList(rto, projectId); 
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getSubTasksList(com.pandora.TaskTO)
     */    
    public Vector getSubTasksList(TaskTO parenttask) throws BusinessException {       
        return bus.getSubTasksList(parenttask); 
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.isBlocked(com.pandora.TaskTO)
     */    
    public boolean isBlocked(TaskTO tto) {
    	return bus.isBlocked(tto);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.insertTask(com.pandora.TaskTO)
     */
    public void insertTask(TaskTO tto) throws BusinessException {
        bus.insertTask(tto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.insertTask(java.util.Vector)
     */    
    public void insertTask(Vector list) throws BusinessException {
        bus.insertTask(list);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.updateTask(com.pandora.TaskTO, boolean)
     */
    public void updateTask(TaskTO tto, boolean closeReqIfLastTask) throws BusinessException {
        bus.updateTask(tto, closeReqIfLastTask);        
    }

    
    public void updateByResource(TaskTO to) throws BusinessException {
    	bus.updateByResource(to);
    }
    

    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.removeTask(com.pandora.TaskTO)
     */
    public void removeTaskByOwner(TaskTO tto) throws BusinessException {
        bus.removeTask(tto);        
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.cancelTask(com.pandora.TaskTO, boolean)
     */
    public void cancelTask(TaskTO tto, boolean reopenRelatedReq) throws BusinessException {
        bus.cancelTask(tto, reopenRelatedReq);        
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getTaskObject(com.pandora.TaskTO)
     */
    public TaskTO getTaskObject(TaskTO tto) throws BusinessException {
        return bus.getTaskObject(tto);
    }
    
        
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getTaskListByProject(com.pandora.ProjectTO, java.util.Timestamp, boolean, boolean)
     */
    public Vector<TaskTO> getTaskListByProject(ProjectTO pto, Timestamp iniRange, boolean orderedIt, boolean includeSubProjects) throws BusinessException { 
        return bus.getTaskListByProject(pto, iniRange, orderedIt, includeSubProjects);        
    }    

    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getTaskListByProjectInTree(com.pandora.ProjectTO)
     */
    public Vector<TaskTO> getTaskListByProjectInTree(ProjectTO pto) throws BusinessException{
        return bus.getTaskListByProjectInTree(pto);                
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getDateFromTaskList(java.util.Vector, boolean)
     */
    public Timestamp getDateFromTaskList(Vector taskList, boolean isInitial) throws BusinessException {
        return bus.getDateFromTaskList(taskList, isInitial);        
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getLastHistoryByTask(com.pandora.ResourceTaskTO)
     */
    public TaskHistoryTO getLastHistoryByTask(ResourceTaskTO rtto) throws BusinessException {
        return bus.getLastHistoryByTask(rtto);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getHistory(java.lang.String)
     */
    public Vector getHistory(String taskId) throws BusinessException {
        return bus.getHistory(taskId);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getHistoryByRequirementId(java.lang.String)
     */
    public Vector getHistoryByRequirementId(String reqId) throws BusinessException {
        return bus.getHistoryByRequirementId(reqId);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getTechCommentsFromTask(com.pandora.RequirementHistoryTO, com.pandora.CustomerTO)
     */    
    public String getTechCommentsFromTask(RequirementHistoryTO rhto, CustomerTO cto) throws BusinessException {
        return bus.getTechCommentsFromTask(rhto, cto);
    }
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getTechCommentsFromTask(java.util.Vector, com.pandora.CustomerTO)
     */    
    public String getTechCommentsFromTask(Vector items, CustomerTO cto) throws BusinessException {
        return bus.getTechCommentsFromTask(items, cto);
    }    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskBUS.getReopenTimes(com.pandora.TaskTO)
     */        
    public int getReopenTimes(TaskTO tto) throws BusinessException {
    	return bus.getReopenTimes(tto);
    }
}
