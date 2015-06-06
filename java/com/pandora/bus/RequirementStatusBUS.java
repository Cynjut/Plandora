package com.pandora.bus;

import java.util.Vector;

import com.pandora.RequirementStatusTO;
import com.pandora.dao.RequirementStatusDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Requirement Status entity.
 */
public class RequirementStatusBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    RequirementStatusDAO dao = new RequirementStatusDAO();
    
    
    /**
     * Get a list of all Requirement Status TOs from data base.
     */
    public Vector<RequirementStatusTO> getRequirementStatusList() throws BusinessException{
        Vector<RequirementStatusTO> response = new Vector<RequirementStatusTO>();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }


	public RequirementStatusTO getObjectByStateMachine(Integer state) throws BusinessException{
		RequirementStatusTO response = null;
		try {
            response = dao.getObjectByStateMachine(state);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
		return response;
	}


	public RequirementStatusTO getObjectById(String statusId) throws BusinessException {
		RequirementStatusTO response = null;
		try {
			RequirementStatusTO filter = new RequirementStatusTO();
			filter.setId(statusId);
            response = (RequirementStatusTO) dao.getObject(filter);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
		return response;
	}    
}
