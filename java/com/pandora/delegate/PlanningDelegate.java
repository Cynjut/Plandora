package com.pandora.delegate;

import java.util.Vector;

import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.bus.PlanningBUS;
import com.pandora.exception.BusinessException;

public class PlanningDelegate  extends GeneralDelegate {

    /** The Business object related with current delegate */    
    PlanningBUS bus = new PlanningBUS();
	
    public Vector<PlanningTO> getListByKeyword(Vector<String> kwList, String projectId, UserTO uto) throws BusinessException {
    	return bus.getListByKeyword(kwList, projectId, uto);
    }

    public PlanningTO getSpecializedObject(PlanningTO planning) throws BusinessException {
    	return this.getSpecializedObject(planning, null);
    }
    
    public PlanningTO getSpecializedObject(PlanningTO planning, Vector<ProjectTO> projects) throws BusinessException {
    	return bus.getSpecializedObject(planning,  projects);
    }

}
