package com.pandora.integration;

import com.pandora.integration.exception.IntegrationException;

public class RepositoryMessageIntegration extends Integration {

    /** Used by XML tag reference */
    public static final String REPOSITORY_PROJ_ID = "PROJ_ID";
    public static final String REPOSITORY_REPOS   = "REPOS";
    public static final String REPOSITORY_FILES   = "FILES";
    public static final String REPOSITORY_LOG     = "LOG";
    public static final String REPOSITORY_AUTHOR  = "AUTHOR";
    
	private static final long serialVersionUID = 1L;

	private String projectId;
	
	private String repositoryPath;
	
	private String files;

	private String author;
	
	
	public String getLastTokenPath() {
		String token = "";
		if (repositoryPath!=null && !repositoryPath.trim().equals("")) {
			String[] t = repositoryPath.split("/");
			token = t[t.length-1];
			token = token.trim();
		}
		return token;
	}
	
	
	///////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	///////////////////////////////////////////
	public String getRepositoryPath() {
		return repositoryPath;
	}
	public void setRepositoryPath(String newValue) {
		this.repositoryPath = newValue;
	}

	
	///////////////////////////////////////////
	public String getFiles() {
		return files;
	}
	public void setFiles(String newValue) {
		this.files = newValue;
	}

	
	///////////////////////////////////////////
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String newValue) {
		this.author = newValue;
	}


	public void validateDelete() throws IntegrationException {
	}

	public void validateInsert() throws IntegrationException {
	}

	public void validateUpdate() throws IntegrationException {
	}

}
