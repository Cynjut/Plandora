package com.pandora;

import java.sql.Timestamp;

public class ArtifactTO extends PlanningTO {

	private static final long serialVersionUID = 1L;

	private String path;
	
	private String fileName;
	
	private String exportType;
	
	private String body;
	
	private String planningId;
	
	private String templateId;
	
	private String projectId;
	
	private String logMessage;
	
	private String user;
	
	private String pass;
	
	private UserTO handler;
	
	private RepositoryFileTO repositoryFile;

	private Timestamp lastUpdate;
	
	
	public ArtifactTO() {
	}
	
	public ArtifactTO(String newId) {
		this.setId(newId);
	}
	
	
	///////////////////////////////////
	public String getPath() {
		return path;
	}
	public void setPath(String newValue) {
		if (newValue==null) {
			newValue = "";
		}
		this.path = newValue;
	}

	
	///////////////////////////////////
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String newValue) {
		this.fileName = newValue;
	}

	
	///////////////////////////////////
	public String getExportType() {
		return exportType;
	}
	public void setExportType(String newValue) {
		this.exportType = newValue;
	}

	
	///////////////////////////////////
	public String getBody() {
		return body;
	}
	public void setBody(String newValue) {
		this.body = newValue;
	}

	
	///////////////////////////////////
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}

	
	///////////////////////////////////
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String newValue) {
		this.templateId = newValue;
	}

	
	///////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	///////////////////////////////////
	public String getLogMessage() {
		return logMessage;
	}
	public void setLogMessage(String newValue) {
		this.logMessage = newValue;
	}

	
	///////////////////////////////////
	public String getUser() {
		return user;
	}
	public void setUser(String newValue) {
		this.user = newValue;
	}

	
	///////////////////////////////////
	public String getPass() {
		return pass;
	}
	public void setPass(String newValue) {
		this.pass = newValue;
	}

	
	///////////////////////////////////
	public UserTO getHandler() {
		return handler;
	}
	public void setHandler(UserTO newValue) {
		this.handler = newValue;
	}
	
	
	///////////////////////////////////
	public RepositoryFileTO getRepositoryFile() {
		return repositoryFile;
	}
	public void setRepositoryFile(RepositoryFileTO newValue) {
		this.repositoryFile = newValue;
	}
	
	
	///////////////////////////////////
	public Timestamp getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Timestamp newValue) {
		this.lastUpdate = newValue;
	}
	
	
}
