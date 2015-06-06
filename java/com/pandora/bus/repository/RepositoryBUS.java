package com.pandora.bus.repository;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.PlanningTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RepositoryFileProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.RepositoryLogTO;
import com.pandora.UserTO;
import com.pandora.bus.GeneralBusiness;
import com.pandora.dao.RepositoryDAO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.MaxSizeAttachmentException;

public class RepositoryBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
	RepositoryDAO dao = new RepositoryDAO();
	
	
	public Vector<RepositoryFilePlanningTO> getEntitiesFromFile(RepositoryFileTO rfto) throws BusinessException {
		Vector<RepositoryFilePlanningTO> response = new Vector<RepositoryFilePlanningTO>();
        try {
        	response = dao.getEntitiesFromFile(rfto);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }		
		return response;
	}

	
	public Vector<RepositoryFileTO> getFiles(ProjectTO pto, String path, String rev, boolean includeEntitiesLink) throws Exception {
		Vector<RepositoryFileTO> response = null;
		String url = pto.getRepositoryURL();
		String repositoryClass = pto.getRepositoryClass();
		String user = pto.getRepositoryUser();
		String pass = pto.getRepositoryPass();
		
		Repository rep = getRepositoryClass(repositoryClass);
		if (rep==null) {
			throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
		} else {
			response = rep.getFiles(pto, url, path, rev, user, pass);	
		}
		
		//for each repository item, fetch additional attributes from data base...
		if (response!=null) {
			Iterator<RepositoryFileTO> i = response.iterator();
			while(i.hasNext()) {
				RepositoryFileTO rfto = i.next();
				
				if (includeEntitiesLink) {
					if (rfto.getPath()!=null) {
						rfto.setPath(this.getExtractPath(pto, rfto.getPath()));
						Vector<RepositoryFilePlanningTO> entities = dao.getEntitiesFromFile(rfto);
						rfto.setEntities(entities);					
					}					
				}

	    		String extractedPath = getExtractPath(pto, rfto.getPath());
				RepositoryFileProjectTO dbRfto = dao.getFileFromDB(pto, extractedPath);
				if (dbRfto!=null && dbRfto.getFile()!=null) {
					rfto.setArtifactTemplateType(dbRfto.getFile().getArtifactTemplateType());
				}
			}
		}
		
		
		return response;
	}
	
	
	public Vector<RepositoryLogTO> getLogs(ProjectTO pto, String path, String rev) throws Exception {
		Vector<RepositoryLogTO> response = null;
		String url = pto.getRepositoryURL();
		String repositoryClass = pto.getRepositoryClass();
		String user = pto.getRepositoryUser();
		String pass = pto.getRepositoryPass();
		
		Repository rep = getRepositoryClass(repositoryClass);
		if (rep==null) {
			throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
		} else {
			response = rep.getLogs(pto, url, path, rev, user, pass);	
		}
		return response;
	}

	
	public RepositoryFileTO getFile(ProjectTO pto, String pathName, String rev) throws Exception {
		RepositoryFileTO rfto = null;
		String url = pto.getRepositoryURL();
		String repositoryClass = pto.getRepositoryClass();
		String user = pto.getRepositoryUser();
		String pass = pto.getRepositoryPass();
		
		Repository rep = getRepositoryClass(repositoryClass);
		if (rep==null) {
			throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
		} else {
			rfto = rep.getFile(pto, url, pathName, rev, user, pass);  
			if (rfto!=null) {
				rfto.setPath(this.getExtractPath(pto, rfto.getPath()));
				RepositoryFileProjectTO dbRfto = dao.getFileFromDB(pto, rfto.getPath());
				if (dbRfto!=null && dbRfto.getFile()!=null) {
					rfto.setArtifactTemplateType(dbRfto.getFile().getArtifactTemplateType());
					rfto.setArtifact(dbRfto.getFile().getArtifact());
				}
			}			
		}
		return rfto;		
	}	

	
	public void removeFile(ProjectTO pto, String pathName, String rev) throws Exception {
		if (this.canRemoveFile(pto)) {
			String url = pto.getRepositoryURL();
			String repositoryClass = pto.getRepositoryClass();
			String user = pto.getRepositoryUser();
			String pass = pto.getRepositoryPass();
			
			Repository rep = getRepositoryClass(repositoryClass);
			rep.removeFile(pto, url, pathName, rev, user, pass);
			
		} else {
			throw new Exception("This project do not accept removing repository files.");
		}
	}

	
	public RepositoryFileTO getFileInfo(ProjectTO pto, String pathName, String rev) throws Exception {
		String url = pto.getRepositoryURL();
		String repositoryClass = pto.getRepositoryClass();
		String user = pto.getRepositoryUser();
		String pass = pto.getRepositoryPass();
		
		Repository rep = getRepositoryClass(repositoryClass);
		if (rep==null) {
			throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
		} else {
			return rep.getFileInfo(pto, url, pathName, rev, user, pass);	
		}
	}

	
	public RepositoryFileProjectTO getFileFromDB(ProjectTO pto, String path) throws BusinessException {
		RepositoryFileProjectTO response = null;
        try {
        	path = getExtractPath(pto, path);
        	response = dao.getFileFromDB(pto, path);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }
		return response;
	}

	
	public RepositoryFileProjectTO getFileFromDBbyId(ProjectTO pto, String fileId) throws BusinessException {
		RepositoryFileProjectTO response = null;
        try {
        	response = dao.getFileFromDBbyId(pto, fileId);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }
		return response;
	}

	
	
	public void commitFile(ProjectTO pto, String path, String fileName, String rev,  
			String logMessage, String contentType, byte[] fileData, String user, String pass) throws Exception {
		String url = pto.getRepositoryURL();
		String repositoryClass = pto.getRepositoryClass();
		
		//note: the user and password of commit event must be set by commit popup
		//String user = pto.getRepositoryUser();
		//String pass = pto.getRepositoryPass();
		
		Repository rep = getRepositoryClass(repositoryClass);
		if (rep==null) {
			throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
		} else {
			rep.commitFile(pto, url, path, fileName, rev, user, pass, logMessage, contentType, fileData);	
		}
	}
	
	
	public Vector<RepositoryFilePlanningTO> getFilesFromPlanning(PlanningTO pto) throws BusinessException {
		Vector<RepositoryFilePlanningTO> response = new Vector<RepositoryFilePlanningTO>();
        try {
        	response = dao.getFilesFromEntity(pto);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }
		return response;
	}
	
	
	public void updateDisabledStatus(ProjectTO pto, String path, boolean disabled) throws BusinessException {
        try {
        	path = getExtractPath(pto, path);
        	dao.updateDisabledStatus(pto, path, disabled);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }		
	}
	

	public Vector<RepositoryFileTO> getFilesToCustomerView(ProjectTO pto) throws BusinessException {
		Vector<RepositoryFileTO> response = new Vector<RepositoryFileTO>();
        try {
        	response = dao.getFilesToCustomerView(pto);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }
		return response;
	}

	
	public void updateRepositoryFilePlan(String path, String entityId, ProjectTO pto, boolean removeIfAlreadyExists) throws BusinessException {
        try {
        	dao.updateRepositoryFilePlan(path, entityId, pto, removeIfAlreadyExists);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }
	}

	
	public void updateDownloadableStatus(ProjectTO pto, String fileId, boolean isDownloadable) throws BusinessException {
        try {
        	dao.updateDownloadableStatus(pto, fileId, isDownloadable);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }
	}

	
	
	public void breakRepositoryEntityLink(String pathId, String entityId) throws BusinessException {
        try {
        	dao.breakRepositoryEntityLink(pathId, entityId);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }
	}
	
	
	/**
	 * Static method used to return a instance of repository 
	 */
	public static Repository getRepositoryClass(String className){
		Repository response = null;
		if (className!=null && !className.trim().equals("-1")) {
	        try {
	            @SuppressWarnings("rawtypes")
				Class klass = Class.forName(className);
	            response = (Repository)klass.newInstance();
	        } catch (Exception e) {
	        	e.printStackTrace();
	            response = null;
	        }			
		}
        return response;
	}


	/**
	 * Remove the url prefix of repository path... 
	 */
	public String getExtractPath(ProjectTO pto, String path) throws BusinessException{
		String response = path;
		ProjectDelegate pdel = new ProjectDelegate();
		pto = pdel.getProjectObject(pto, true);
		if (pto!=null && pto.getRepositoryURL()!=null) {
			String url = pto.getRepositoryURL();
			if (!url.endsWith("\\")) {
				url = url + "\\";
			}
			if (!url.endsWith("/")) {
				url = url + "/";
			}
			response = path.replaceAll(url, "");
		}
    	return response;
	}


	public boolean canUploadFile(ProjectTO pto) throws Exception{
		String repositoryClass = pto.getRepositoryClass();
		Repository rep = getRepositoryClass(repositoryClass);
		if (rep==null) {
			throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
		} else {
			return rep.canUploadFile();	
		}
	}

	
	public boolean canRemoveFile(ProjectTO pto) throws Exception{
		String repositoryClass = pto.getRepositoryClass();
		Repository rep = getRepositoryClass(repositoryClass);
		if (rep==null) {
			throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
		} else {
			return rep.canRemoveFile();	
		}
	}	
	

	public void uploadFile(UserTO handler, String path, String projectId, byte[] fileData, String contentType, String fileName, String logMessage) throws Exception {
		UserDelegate udel = new UserDelegate();
		ProjectDelegate pdel = new ProjectDelegate();
		try {		
			UserTO root = udel.getRoot();
			String maxFile = root.getPreference().getPreference(PreferenceTO.UPLOAD_MAX_SIZE);
			if (fileData!=null && maxFile!=null && fileData.length>Integer.parseInt(maxFile)) {
	            throw new MaxSizeAttachmentException();    
			} else {
				ProjectTO pto = pdel.getProjectObject(new ProjectTO(projectId), true);
				String repositoryClass = pto.getRepositoryClass();
				Repository rep = getRepositoryClass(repositoryClass);
				if (rep==null) {
					throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
				} else {
					rep.newFile(pto, pto.getRepositoryURL(), path, fileName, 
							handler.getUsername(), null, logMessage, contentType, fileData);	
				}				
			}
		
		} catch (MaxSizeAttachmentException e) {
			throw e;
		} catch (BusinessException e) {
			throw new  BusinessException(e);
		}					
	}

	public void createFolder(UserTO handler, String path, String projectId, String logMessage) throws Exception {
		ProjectDelegate pdel = new ProjectDelegate();
		try {		
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(projectId), true);
			String repositoryClass = pto.getRepositoryClass();
			Repository rep = getRepositoryClass(repositoryClass);
			if (rep==null) {
				throw new Exception("The repository wrapper [" +  repositoryClass + "] was not found.");
			} else {
				rep.createFolder(pto, pto.getRepositoryURL(), path,	handler.getUsername(), logMessage);	
			}						
		} catch (BusinessException e) {
			throw new  BusinessException(e);
		}					
	}


	public void insertHistory(UserTO handler, ProjectTO pto, RepositoryFileTO file, String repAction, String comment) throws BusinessException {
		try {		
			dao.insertHistory(handler, pto, file, repAction, comment);	
		} catch (Exception e) {
			throw new  BusinessException(e);
		}					
	}


}

