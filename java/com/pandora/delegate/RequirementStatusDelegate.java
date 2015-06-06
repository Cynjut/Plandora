package com.pandora.delegate;

import java.util.Vector;

import com.pandora.RequirementStatusTO;
import com.pandora.bus.RequirementStatusBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for RequirementStatus entity information access.
 */
public class RequirementStatusDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
    RequirementStatusBUS bus = new RequirementStatusBUS(); 

    
    /* (non-Javadoc)
     * @see com.pandora.bus.RequirementStatusBUS.getRequirementStatusList()
     */    
    public Vector<RequirementStatusTO> getRequirementStatusList() throws BusinessException{
        return bus.getRequirementStatusList();
    }


	public RequirementStatusTO getObjectByStateMachine(Integer state) throws BusinessException{
		return bus.getObjectByStateMachine(state);
	}

	
	public RequirementStatusTO getObjectById(String statusId) throws BusinessException{
		return bus.getObjectById(statusId);
	}
	
}
