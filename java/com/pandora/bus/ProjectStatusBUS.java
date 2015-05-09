package com.pandora.bus;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.ProjectStatusTO;
import com.pandora.ProjectTO;
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
	public Vector getProjectStatusListByProject(ProjectTO pto) throws BusinessException{
        Vector response = new Vector();
        ProjectStatusDAO dao = new ProjectStatusDAO();
        
        try {
            ProjectStatusTO currStatus = pto.getProjectStatus();
            if (currStatus.getId()!=null && currStatus.getStateMachineOrder()==null){
                currStatus = (ProjectStatusTO)dao.getObject(currStatus);
            }
            
            //get all project status from data base
            Vector projList = dao.getList();
            Iterator i = projList.iterator();
            while(i.hasNext()){
                ProjectStatusTO psto = (ProjectStatusTO)i.next();
                
                if (currStatus.getId()!=null) {
                    boolean consider = false;                    
                    //show into combo only valid options depending on current status of project.
                    if (psto.getStateMachineOrder()!=null){
                        int sm = psto.getStateMachineOrder().intValue();
                        int csm = currStatus.getStateMachineOrder().intValue();
                        consider = (sm>=csm);
                    } else {
                        consider = true;
                    }
                    
                    //select only valid status depending current status of project related
                    if (consider){
                        response.addElement(psto);
                    }
            	} else {
            		Integer state = psto.getStateMachineOrder();
            	    if (state.equals(ProjectStatusTO.STATE_MACHINE_OPEN)){
            	        response.addElement(psto);
            	        break;
            	    }
            	}
        	}
            
            if (response.size()==0){
                throw new  BusinessException("There is not a valid Project Status into data base.");
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
