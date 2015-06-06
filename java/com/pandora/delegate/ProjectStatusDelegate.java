package com.pandora.delegate;

import java.util.Vector;

import com.pandora.ProjectStatusTO;
import com.pandora.ProjectTO;
import com.pandora.bus.ProjectStatusBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for the Project Status entity.
 */
public class ProjectStatusDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */        
    ProjectStatusBUS bus = new ProjectStatusBUS(); 

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectStatusBUS.getProjectStatusListByProject(com.pandora.ProjectTO)
     */
	public Vector<ProjectStatusTO> getProjectStatusListByProject(ProjectTO pto) throws BusinessException{
        return bus.getProjectStatusListByProject(pto);	    
	}

	
    /* (non-Javadoc)
     * @see com.pandora.bus.ProjectStatusBUS.getProjectStatus(java.lang.Integer)
     */
	public ProjectStatusTO getProjectStatus(Integer state) throws BusinessException{
		return bus.getProjectStatus(state);
	}    
}
