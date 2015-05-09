package com.pandora.bus;

import java.util.Vector;

import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.dao.PlanningRelationDAO;

import com.pandora.exception.BusinessException;
import com.pandora.exception.InvalidRelationException;
import com.pandora.exception.DataAccessException;

public class PlanningRelationBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    PlanningRelationDAO dao = new PlanningRelationDAO();

    
    public Vector getRelationList(PlanningTO pto) throws BusinessException {
    	Vector response = new Vector();
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
