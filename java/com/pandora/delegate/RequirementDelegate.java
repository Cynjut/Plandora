package com.pandora.delegate;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.ResourceTaskTO;
import com.pandora.UserTO;
import com.pandora.bus.RequirementBUS;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

/**
 * This class has the interface for the Requirement entity information access.
 */
public class RequirementDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    RequirementBUS bus = new RequirementBUS();     

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.getListByUser(com.pandora.UserTO, boolean, boolean)
     */    
    public Vector<RequirementTO> getListByUser(UserTO uto, boolean hideClosed, boolean sharingView) throws BusinessException{
        return bus.getListByUser(uto, hideClosed, sharingView);
    }

    
    public void changeRequirementStatus(ResourceTaskTO rtto, Integer newState, String taskComment) throws BusinessException {
        bus.changeRequirementStatus(rtto, newState, taskComment);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.getListByProject(com.pandora.ProjectTO, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */    
    public Vector<RequirementTO> getListByProject(ProjectTO pto, String status, String requester, String priority, 
    		String categoryName, String viewMode) throws BusinessException {   
    	Timestamp ini = DateUtil.getNow();
    	Vector<RequirementTO> listByProject = bus.getListByProject(pto, status, requester, priority, categoryName, viewMode);
    	System.out.println("RequirementDelegate.getListByProject(): " + (DateUtil.getNow().getTime() - ini.getTime()) + "ms");
        return listByProject;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.getListByFilter(com.pandora.ProjectTO, com.pandora.CustomerTO, java.util.Vector)
     */    
    public Vector<RequirementTO> getListByFilter(ProjectTO pto, CustomerTO cto, Vector kwList) throws BusinessException {
        return bus.getListByFilter(pto, cto, kwList);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.getPendingListByUser(com.pandora.UserTO, boolean)
     */    
    public Vector<RequirementTO> getPendingListByUser(UserTO uto, boolean exceptCurrUserReq) throws BusinessException{
        return bus.getPendingListByUser(uto, exceptCurrUserReq);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.getRequirement(com.pandora.RequirementTO)
     */    
    public RequirementTO getRequirement(RequirementTO filter) throws BusinessException{
        return bus.getRequirement(filter);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.getHistory(java.lang.String)
     */    
    public Vector<RequirementHistoryTO> getHistory(String reqId) throws BusinessException{
        return bus.getHistory(reqId);        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.insertRequirement(com.pandora.RequirementTO)
     */    
    public void insertRequirement(RequirementTO rto) throws BusinessException{
        bus.insertRequirement(rto);
    }


    public void insertRequirement(Vector<RequirementTO> rlist) throws BusinessException{
    	bus.insertRequirement(rlist);
	}
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.insertRequirement(com.pandora.RequirementTO)
     */    
    public void updateRequirement(RequirementTO rto) throws BusinessException{
        bus.updateRequirement(rto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.removeRequirement(com.pandora.RequirementTO)
     */    
    public void removeRequirement(RequirementTO rto) throws BusinessException{
        bus.removeRequirement(rto);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementBUS.refuseRequirement(com.pandora.RequirementTO, com.pandora.UserTO, java.lang.String)
     */    
    public void refuseRequirement(RequirementTO rto, UserTO uto, String comment) throws BusinessException{
        bus.refuseRequirement(rto, uto, comment);
    }


	public Vector<RequirementWithTasksTO> getRequirementWithTaskList(String projectId, String iterationId, 
			boolean hideFinishedReqs, boolean hideOldIterations) throws BusinessException{
		return bus.getRequirementWithTaskList(projectId, iterationId, hideFinishedReqs, hideOldIterations);
	}


	public void deleteRequirement(String reqId) throws BusinessException {
		bus.deleteRequirement(reqId);		
	}
    
    public Vector<RequirementTO> getThinListByProject(ProjectTO pto, String categoryId) throws BusinessException {    	
        return bus.getThinListByProject(pto, categoryId);
    }

	
}
