package com.pandora.bus;

import java.util.Vector;

import com.pandora.ProjectStatusTO;
import com.pandora.ProjectTO;
import com.pandora.dao.ProjectDAO;
import com.pandora.dao.ProjectStatusDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Project Status entity.
 */
public class ProjectStatusBUS extends GeneralBusiness {

    /**
     * Get a list of Project Status objects based on project.
     */
	public Vector<ProjectStatusTO> getProjectStatusListByProject(ProjectTO pto) throws BusinessException{
        Vector<ProjectStatusTO> response = new Vector<ProjectStatusTO>();
        ProjectStatusDAO dao = new ProjectStatusDAO();
        ProjectDAO pdao = new ProjectDAO();
        
        try {
        	ProjectTO project = (ProjectTO) pdao.getProjectById(pto, true);
        	if (project!=null) {
        		ProjectStatusTO currStatus = project.getProjectStatus();
        		if (currStatus!=null && currStatus.getStateMachineOrder()!=null) {
                    if (currStatus.getStateMachineOrder().equals(ProjectStatusTO.STATE_MACHINE_CLOSE_ABORT)) {
                    	ProjectStatusTO status = dao.getProjectStatus(ProjectStatusTO.STATE_MACHINE_CLOSE_ABORT);
                    	response.add(status);
                    }        			
        		}
        	}
        	
        	if (response!=null && response.size()==0) {
        		response =  dao.getList();	
        	}
        	
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}
	
	
	public ProjectStatusTO getProjectStatus(Integer status) throws BusinessException {
		ProjectStatusTO response = null;
        ProjectStatusDAO dao = new ProjectStatusDAO();
        try {
        	response = dao.getProjectStatus(status);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }		
        
        return response;
	}
}
