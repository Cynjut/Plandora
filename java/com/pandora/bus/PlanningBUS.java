package com.pandora.bus;

import java.util.Vector;

import com.pandora.OccurrenceTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RiskTO;
import com.pandora.UserTO;
import com.pandora.dao.PlanningDAO;
import com.pandora.delegate.ProjectDelegate;
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
        return getSpecializedObject(planning, null);
    }

    
    public PlanningTO getSpecializedObject(PlanningTO planning, Vector<ProjectTO> projects) throws BusinessException {
    	PlanningTO response = null;
    	ProjectDelegate pdel = new ProjectDelegate(); 
        try {
            response = dao.getSpecializedObject(planning);
            
            //check if the user can view the planning object
            if (projects!=null) {
            	if(response instanceof RiskTO && !((RiskTO)response).getVisible() && !pdel.containsProject(projects, response.getPlanningProject())){
           			response = null;
            	}else if(response instanceof OccurrenceTO && !((OccurrenceTO)response).getVisible() && !pdel.containsProject(projects, response.getPlanningProject() )){
           			response = null;
            	}
            } else {
            	if (response instanceof OccurrenceTO && !((OccurrenceTO)response).getVisible() ) {
					((OccurrenceTO)response).setName("** Prvt.Info. **");
					response.setDescription("** Prvt.Info. **");
				}else if (response instanceof RiskTO && !((RiskTO)response).getVisible() ) {
					((RiskTO)response).setName("** Prvt.Info. **");
					response.setDescription("** Prvt.Info. **");
				}
            }
            
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
