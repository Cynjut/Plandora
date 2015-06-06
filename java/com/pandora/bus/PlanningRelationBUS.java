package com.pandora.bus;

import java.util.Vector;

import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.dao.PlanningRelationDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.InvalidRelationException;

public class PlanningRelationBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    PlanningRelationDAO dao = new PlanningRelationDAO();

    
    public Vector<PlanningRelationTO> getRelationList(PlanningTO pto) throws BusinessException {
    	Vector<PlanningRelationTO> response = new Vector<PlanningRelationTO>();
        try {
        	response = dao.getRelationList(pto);
        } catch (DataAccessException e) {
        	throw new BusinessException(e);
        }    	
        return response;
    }

    
    public void insertRelation(PlanningRelationTO planRelation) throws BusinessException {
        try {

        	if (planRelation.getRelationType().equals(PlanningRelationTO.RELATION_BLOCKS)) {
        		
            	if (!planRelation.getPlanType().equals(PlanningRelationTO.ENTITY_TASK) ||
            			!planRelation.getRelatedType().equals(PlanningRelationTO.ENTITY_TASK)) {
            		
            		throw new InvalidRelationException();
            	}        		
        	}
        	
            dao.insertRelation(planRelation);
            
        } catch (DataAccessException e) {
        	throw new BusinessException(e);
        }    	
    }

    public void removeRelation(PlanningRelationTO planRelation) throws BusinessException {
        try {        
            dao.removeRelation(planRelation);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }    	
    }

}
