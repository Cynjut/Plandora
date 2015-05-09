package com.pandora.bus.repository;

import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.RepositoryLogTO;
import com.pandora.UserTO;

public class Repository {

	protected UserTO handler;
	
	 
	/**
	 * This method must be overriden by sub class
	 */
	public String getUniqueName(){
		return null;
	}

	
	/**
	 * This method must be overriden by sub class
	 */
	public String getId(){
		return null;
	}

	
	/**
	 * This method must be overriden by sub class
	 */	
	public Vector<RepositoryFileTO> getFiles(ProjectTO pto, String url, String path, String rev, String user, String pass) throws Exception {
		throw new Exception("The method Repository.getFiles() must be overriden. Cannot be called at superclass scope.");
	}

	/**
	 * This method must be overriden by sub class
	 */	
	public RepositoryFileTO getFile(ProjectTO pto, String url, String pathName, String rev, String user, String pass) throws Exception {
		throw new Exception("The method Repository.getFile() must be overriden. Cannot be called at superclass scope.");
	}

	/**
	 * This method must be overriden by sub class
	 */	
	public RepositoryFileTO getFileInfo(ProjectTO pto, String url, String pathName, String rev, String user, String pass) throws Exception {
		throw new Exception("The method Repository.getFile() must be overriden. Cannot be called at superclass scope.");
	}
	
	/**
	 * This method must be overriden by sub class
	 */	
	public Vector<RepositoryLogTO> getLogs(ProjectTO pto, String url, String path, String rev, String user, String pass) throws Exception {
		throw new Exception("The method Repository.getLogs() must be overriden. Cannot be called at superclass scope.");
	}
	
	
	/**
	 * This method must be overriden by sub class
	 */	
	public void commitFile(ProjectTO pto, String url, String path, String fileName, String rev, String user, String pass, String logMessage, String contentType, byte[] data) throws Exception {
		throw new Exception("The method Repository.commitFile() must be overriden. Cannot be called at superclass scope.");
	}


	/**
	 * This method must be overriden by sub class
	 */	
	public void newFile(ProjectTO pto, String url, String path, String fileName, String user, String pass, String logMessage, String contentType, byte[] data) throws Exception {
		throw new Exception("The method Repository.newFile() must be overriden. Cannot be called at superclass scope.");
	}

	
	/**
	 * This method must be overriden by sub class
	 */	
	public void createFolder(ProjectTO pto, String repositoryURL, String path, String username, String logMessage) throws Exception {
		throw new Exception("The method Repository.newFile() must be overriden. Cannot be called at superclass scope.");		
	}	

	
	public boolean showUserPwdFields() {
		return true;
	}


	public boolean canUploadFile() {
		return false;
	}
	
	
	protected long formatRevision(String rev){
		long response = -1; //default value
		try {
			if (rev!=null && !rev.trim().equals("") && !rev.equalsIgnoreCase("null") &&
					!rev.trim().equals(RepositoryFileTO.REPOSITORY_HEAD)) {
				response  = Long.parseLong(rev);
			}			 
		} catch (Exception e){
			e.printStackTrace();
		}
		return response;
	}


}
