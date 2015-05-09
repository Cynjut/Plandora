package com.pandora;

public class RepositoryFilePlanningTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private RepositoryFileTO file;

	private PlanningTO entity;

	
	////////////////////////////////////////////
	public RepositoryFileTO getFile() {
		return file;
	}
	public void setFile(RepositoryFileTO newValue) {
		this.file = newValue;
	}
	
	
	////////////////////////////////////////////	
	public PlanningTO getEntity() {
		return entity;
	}
	public void setEntity(PlanningTO entity) {
		this.entity = entity;
	}

	
	
}
