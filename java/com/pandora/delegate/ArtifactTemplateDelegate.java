package com.pandora.delegate;

import java.util.Vector;

import com.pandora.ArtifactTemplateTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.bus.artifact.ArtifactTemplateBUS;
import com.pandora.exception.BusinessException;
import com.pandora.exception.MaxSizeAttachmentException;

public class ArtifactTemplateDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private ArtifactTemplateBUS bus = new ArtifactTemplateBUS();

    
    public String getTemplateContent(String templateId, ProjectTO project, UserTO uto, String name) throws BusinessException {
        return bus.getTemplateContent(templateId, project, uto, name);
    }
    
    
    public Vector<ArtifactTemplateTO> getListByProject(String projectId) throws BusinessException {
        return bus.getListByProject(projectId);
    }

    
	public void commitArtifact(String path, String fileName, String exportType, String body, String planningId, String templateId, 
			String projectId, String logMessage, String user, String pass, UserTO handler) throws MaxSizeAttachmentException, BusinessException {
		bus.commitArtifact(path, fileName, exportType, body, planningId, templateId, projectId, logMessage, user, pass, handler);
	}

}
