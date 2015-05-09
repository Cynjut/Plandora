package com.pandora.delegate;

import java.util.Vector;

import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.bus.PlanningRelationBUS;
import com.pandora.exception.BusinessException;

public class PlanningRelationDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
	PlanningRelationBUS bus = new PlanningRelationBUS();

    public Vector getRelationList(PlanningTO pto) throws BusinessException {
    	return bus.getRelationList(pto);
    }
	
    public void insertRelation(PlanningRelationTO planRelation) throws BusinessException {
        bus.insertRelation(planRelation);
    }

    public void removeRelation(PlanningRelationTO planRelation) throws BusinessException {
        bus.removeRelation(planRelation);
    }

}
