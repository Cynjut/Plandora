package com.pandora.delegate;

import java.util.Vector;

import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RepositoryFileProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.RepositoryLogTO;
import com.pandora.UserTO;
import com.pandora.bus.repository.RepositoryBUS;
import com.pandora.exception.BusinessException;

public class RepositoryDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
    RepositoryBUS bus = new RepositoryBUS();

    
    /**
     * Get a list of logs of a file from repository, based to the project, a current path and a specific revision 
     */
	public Vector<RepositoryLogTO> getLogs(ProjectTO pto, String path, String rev) throws Exception {
		return bus.getLogs(pto, path, rev);
	}

    
    /**
     * Get a list of files from repository, based to the project, a current path and a specific revision 
     */
	public Vector<RepositoryFileTO> getFiles(ProjectTO pto, String path, String rev, boolean includeEntitiesLink) throws Exception {
		return bus.getFiles(pto, path, rev, includeEntitiesLink);
	}

    /**
     * Get a specific file from repository, based to the project, a current path and a specific revision 
     */
	public RepositoryFileTO getFile(ProjectTO pto, String pathName, String rev) throws Exception {
		return bus.getFile(pto, pathName, rev);
	}

	
    /**
     * Get information about a specific file from repository, based to the project, a current path and a specific revision 
     */
	public RepositoryFileTO getFileInfo(ProjectTO pto, String pathName, String rev) throws Exception {
		return bus.getFileInfo(pto, pathName, rev);
	}
	
	
	/**
	 * Get information about a specific file based to a project 
	 * that is stored into data base.
	 */
	public RepositoryFileProjectTO getFileFromDB(ProjectTO pto, String pathName) throws BusinessException {
		return bus.getFileFromDB(pto, pathName);
	}

	
	public boolean canUploadFile(ProjectTO pto) throws Exception {
		return bus.canUploadFile(pto);
	}
	
	public Vector<RepositoryFilePlanningTO> getFilesFromPlanning(PlanningTO pto) throws BusinessException {
		return bus.getFilesFromPlanning(pto);
	}
	

	public void updateRepositoryFilePlan(String path, String entityId, ProjectTO pto, String artifactTemplateType, boolean removeIfAlreadyExists) throws BusinessException {
		bus.updateRepositoryFilePlan(path, entityId, pto, artifactTemplateType, removeIfAlreadyExists);
	}

	public void breakRepositoryEntityLink(String pathId, String entityId) throws BusinessException {
		bus.breakRepositoryEntityLink(pathId, entityId);
	}
	
	
	public void updateDisabledStatus(ProjectTO pto, String path, boolean disabled, String artifactTemplateType) throws BusinessException {
		bus.updateDisabledStatus(pto, path, disabled, artifactTemplateType);
	}

	
	public Vector<RepositoryFileTO> getFilesToCustomerView(ProjectTO pto) throws BusinessException {
		return bus.getFilesToCustomerView(pto);
	}

	public String getExtractPath(ProjectTO pto, String path) throws BusinessException{
		return bus.getExtractPath(pto, path);
	}


	public void uploadFile(UserTO handler, String path, String projectId, byte[] fileData, String contentType, String fileName, String logMessage) throws Exception {
		bus.uploadFile(handler, path, projectId, fileData, contentType, fileName, logMessage);	
	}


	public void createFolder(UserTO handler, String path, String projectId, String logMessage) throws Exception {
		bus.createFolder(handler, path, projectId, logMessage);			
	}
}
