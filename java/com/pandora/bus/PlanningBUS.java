package com.pandora.bus;

import java.util.Vector;

import com.pandora.PlanningTO;
import com.pandora.UserTO;
import com.pandora.dao.PlanningDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class PlanningBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    PlanningDAO dao = new PlanningDAO();

    
    public Vector<PlanningTO> getListByKeyword(Vector<String> kwList, String projectId, UserTO uto) throws BusinessException {
    	Vector<PlanningTO> response = new Vector<PlanningTO>();
        try {
            response = dao.getListByKeyword(kwList, projectId, uto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    public PlanningTO getSpecializedObject(PlanningTO planning) throws BusinessException {
    	PlanningTO response = null;
        try {
            response = dao.getSpecializedObject(planning);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    	
    }
    
    public static String extractPlanningIdFromComment(String comment){
    	String response = null;
		if (comment.startsWith("[#")) {
			int finalToken = comment.indexOf("]");
			if (finalToken>-1) {
				response = comment.substring(2, finalToken);
			} else {
				response = null;	
			}
		}
    	return response;
    }
    
    
}
