package com.pandora.gui.struts.form;


public class SnipArtifactForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String projectId;
	
	private String planningId;
	
	private String snip;

	private String htmlFormBody;
	
	private String popupTitle;
	
	private String refreshCommand;
		
	
	
	////////////////////////////////////////	
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}
	
	
	////////////////////////////////////////	
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	
	////////////////////////////////////////
	public String getSnip() {
		return snip;
	}
	public void setSnip(String newValue) {
		this.snip = newValue;
	}
	
	
	////////////////////////////////////////
	public String getHtmlFormBody() {
		return htmlFormBody;
	}
	public void setHtmlFormBody(String newValue) {
		this.htmlFormBody = newValue;
	}
	
	
	////////////////////////////////////////
	public String getPopupTitle() {
		return popupTitle;
	}
	public void setPopupTitle(String newValue) {
		this.popupTitle = newValue;
	}
	
	
	////////////////////////////////////////	
	public String getRefreshCommand() {
		return refreshCommand;
	}
	public void setRefreshCommand(String newValue) {
		this.refreshCommand = newValue;
	}
	
}
