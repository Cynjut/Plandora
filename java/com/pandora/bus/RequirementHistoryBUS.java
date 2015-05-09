package com.pandora.bus;

import java.util.Vector;

import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementTO;
import com.pandora.UserTO;
import com.pandora.dao.RequirementHistoryDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class RequirementHistoryBUS extends GeneralBusiness {

	private RequirementHistoryDAO dao = new RequirementHistoryDAO();
	
	public void insert(RequirementTO rto, UserTO handler, String comment) throws BusinessException{
        try {
        	if (rto.getIteration()==null || (rto.getIteration()!=null && !rto.getIteration().equals("-1"))) {
            	RequirementHistoryTO rhto = new RequirementHistoryTO(rto);
            	rhto.setResource(handler);
            	rhto.setComment(comment);
    		    dao.insert(rhto);        		
        	}
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }	
		
	}

	public Vector getIterationList(String requirementId) throws BusinessException {
		Vector response = new Vector();
        try {
        	response = dao.getIterationList(requirementId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }	
        return response;
	}
}
