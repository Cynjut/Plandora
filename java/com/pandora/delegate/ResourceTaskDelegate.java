package com.pandora.delegate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Vector;

import com.pandora.AdditionalFieldTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.bus.ResourceTaskBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for the Resource Task entity information access.
 */
public class ResourceTaskDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */        
    ResourceTaskBUS bus = new ResourceTaskBUS();

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getListByTask(com.pandora.TaskTO)
     */
    public Vector<ResourceTaskTO> getListByTask(TaskTO tto) throws BusinessException {
        return bus.getListByTask(tto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getTaskListByResource(com.pandora.ResourceTO, boolean)
     */
    public Vector<ResourceTaskTO> getTaskListByResource(ResourceTO rto, boolean hideClosed) throws BusinessException {
        return bus.getTaskListByResource(rto, hideClosed);        
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getListByProject(com.pandora.ProjectTO, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public Vector<ResourceTaskTO> getListByProject(ProjectTO pto, String statusSel, String resourceSel, String dateRangeSel, boolean isProjectHierarchy) throws BusinessException {
        return bus.getListByProject(pto, statusSel, resourceSel, dateRangeSel, isProjectHierarchy);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.changeTaskStatus(com.pandora.ResourceTaskTO, java.lang.Integer, java.lang.String, java.util.Vector, boolean)
     */
    public void changeTaskStatus(ResourceTaskTO rtto, Integer newState, String comment, 
    		Vector<AdditionalFieldTO> additionalFields, boolean isMTClosingAllowed) throws BusinessException{
        bus.changeTaskStatus(rtto, newState, comment, additionalFields, isMTClosingAllowed);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getResourceTaskObject(com.pandora.ResourceTaskTO)
     */
    public ResourceTaskTO getResourceTaskObject(ResourceTaskTO rtto) throws BusinessException {
        return bus.getResourceTaskObject(rtto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getResourceTaskObject(java.lang.String, java.lang.String)
     */
	public ResourceTaskTO getResourceTaskObject(String taskId, String resourceId) throws BusinessException {
		return bus.getResourceTaskObject(taskId, resourceId);
	}

	
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getStatusOfAllocResource(com.pandora.TaskTO)
     */
    public Integer getStatusOfAllocResource(TaskTO tto){
        return bus.getStatusOfAllocResource(tto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getStatusOfAllocResource(com.pandora.ResourceTaskTO)
     */
    public Integer getStatusOfAllocResource(ResourceTaskTO rtto){
        return bus.getStatusOfAllocResource(rtto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getDateFromResTaskList(java.util.Vector, boolean)
     */
    public Timestamp getDateFromResTaskList(Vector<ResourceTaskTO> resTaskList, boolean isInitial){
        return bus.getDateFromResTaskList(resTaskList, isInitial);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getPreferedDate(com.pandora.ResourceTaskTO)
     */
    public Timestamp getPreferedDate(ResourceTaskTO rtto) {
        return bus.getPreferedDate(rtto);        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskBUS.getHistory(java.lang.String, java.lang.String)
     */
    public Vector<TaskHistoryTO> getHistory(String taskId, String resourceId) throws BusinessException {
        return bus.getHistory(taskId, resourceId);
    }


	public void changeAssignment(ResourceTaskTO rtto, UserTO uto, String comment) throws BusinessException {
		bus.changeAssignment(rtto, uto, comment);
	}

	public Vector<ResourceTaskTO> getTasksWithoutReq(String projectId, String iteration, boolean hideOldIterations) throws BusinessException {
		return bus.getTasksWithoutReq(projectId, iteration, hideOldIterations);
	}


	public Vector<TaskTO> getNotClosedTasksUnderMacroTask(TaskTO task) throws BusinessException {
		return bus.getNotClosedTasksUnderMacroTask(task);
	}


    public void updateByResource(ResourceTaskTO rtto) throws BusinessException {
    	bus.updateByResource(rtto);
    }

    
    public void updateResourceTask(ResourceTaskTO rtto) throws BusinessException {
    	bus.updateResourceTask(rtto);
    }

    
    public ResourceTaskTO changeProject(String projectId, ResourceTaskTO rtto) throws BusinessException{
    	return bus.changeProject(projectId, rtto);
    }


	public HashMap<String, ResourceTaskAllocTO> getHashAlloc(
			String projectFilter, String resourceFilter, Timestamp iniDate,
			Timestamp finalDate) throws BusinessException{
		return bus.getHashAlloc(projectFilter, resourceFilter, iniDate, finalDate);
	}

    
    
}
