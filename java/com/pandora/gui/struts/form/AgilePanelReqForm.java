package com.pandora.gui.struts.form;

public class AgilePanelReqForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String priority;
	private String reqProjectId;
	private String reqDescription;
	private String iteration;
	private String categoryId;
	
	
	//////////////////////////////////////////
	public String getPriority() {
		return priority;
	}
	public void setPriority(String newValue) {
		this.priority = newValue;
	}

	
	//////////////////////////////////////////
	public String getReqProjectId() {
		return reqProjectId;
	}
	public void setReqProjectId(String newValue) {
		this.reqProjectId = newValue;
	}


	//////////////////////////////////////////
	public String getReqDescription() {
		return reqDescription;
	}
	public void setReqDescription(String newValue) {
		this.reqDescription = newValue;
	}

	
	//////////////////////////////////////////
	public String getIteration() {
		return iteration;
	}
	public void setIteration(String newValue) {
		this.iteration = newValue;
	}

	
	//////////////////////////////////////////
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String newValue) {
		this.categoryId = newValue;
	}

}
