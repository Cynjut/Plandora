package com.pandora;

import java.sql.Timestamp;

public class RepositoryFileProjectTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private RepositoryFileTO file;
	
	private ProjectTO project;
	
	private Boolean isDisabled;
	
	private Boolean isIndexable;
	
	private Timestamp lastUpdate;

	
	////////////////////////////////////////////
	public RepositoryFileTO getFile() {
		return file;
	}
	public void setFile(RepositoryFileTO newValue) {
		this.file = newValue;
	}

	
	////////////////////////////////////////////
	public ProjectTO getProject() {
		return project;
	}
	public void setProject(ProjectTO newValue) {
		this.project = newValue;
	}

	
	////////////////////////////////////////////
	public Boolean getIsDisabled() {
		return isDisabled;
	}
	public void setIsDisabled(Boolean newValue) {
		this.isDisabled = newValue;
	}

	
	////////////////////////////////////////////
	public Timestamp getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Timestamp newValue) {
		this.lastUpdate = newValue;
	}
	
	
	////////////////////////////////////////////
	public Boolean getIsIndexable() {
		return isIndexable;
	}
	public void setIsIndexable(Boolean newValue) {
		this.isIndexable = newValue;
	}
	
	
}
