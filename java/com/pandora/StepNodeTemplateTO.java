package com.pandora;


public class StepNodeTemplateTO extends NodeTemplateTO {

	private static final long serialVersionUID = 1L;

	private String categoryId;
	
	private String categoryRegex;
	
	private String resourceId;
	
	private String iterationId;
	


	public StepNodeTemplateTO(String newId) {
		this.setId(newId);
	}
	
	
	/////////////////////////////////
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String newValue) {
		this.categoryId = newValue;
	}

	
	/////////////////////////////////
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}

	
	/////////////////////////////////
	public String getCategoryRegex() {
		return categoryRegex;
	}
	public void setCategoryRegex(String newValue) {
		this.categoryRegex = newValue;
	}


	/////////////////////////////////
	public String getIterationId() {
		return iterationId;
	}
	public void setIterationId(String newValue) {
		this.iterationId = newValue;
	}

	
}
