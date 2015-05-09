package com.pandora.bus;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.TemplateTO;
import com.pandora.dao.TaskTemplateDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class TaskTemplateBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    TaskTemplateDAO dao = new TaskTemplateDAO();

    
	public Vector getTemplateListByProject(String projectId, boolean onCascade) throws BusinessException {
        Vector response = new Vector();
        ProjectBUS pbus = new ProjectBUS();
        try {
        	
        	if (onCascade) {
                Vector childs = pbus.getProjectListByParent(new ProjectTO(projectId), true);
                Iterator i = childs.iterator();
                while(i.hasNext()){
                    ProjectTO childProj = (ProjectTO)i.next();
                    Vector rskOfChild = this.getTemplateListByProject(childProj.getId(), true);
                    response.addAll(rskOfChild);
                }        		
        	}
                 
            response.addAll(dao.getListByProject(projectId));
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

	public TemplateTO getTaskTemplate(String templateId) throws BusinessException {
		TemplateTO response = null;
        try {
            response = (TemplateTO) dao.getObject(new TemplateTO(templateId));
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

	public TemplateTO getTaskTemplateByInstance(String instanceId) throws BusinessException {
		TemplateTO response = null;
        try {
            response = (TemplateTO) dao.getObjectByInstance(instanceId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}
	
}
