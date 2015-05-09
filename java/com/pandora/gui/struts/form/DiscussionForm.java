package com.pandora.gui.struts.form;

public class DiscussionForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String projectId;

	private String categoryId;
	
	///////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	///////////////////////////////////
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String newValue) {
		this.categoryId = newValue;
	}
	
	
}
