package com.pandora.gui.struts.form;

public class GanttPanelEditForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
	private String taskId;
	
	private String resourceId;
	
	private String projectId;

	private String visibility;
	
	private String allocHtml;
	
	private String comment;
	
	private boolean isDecimalInput = true;
	
	
	///////////////////////////////////////////
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String newValue) {
		this.taskId = newValue;
	}
	
	///////////////////////////////////////////
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}
	
	///////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	
	///////////////////////////////////////////
	public String getAllocHtml() {
		return allocHtml;
	}
	public void setAllocHtml(String newValue) {
		this.allocHtml = newValue;
	}
	
	
	///////////////////////////////////////////	
	public boolean isDecimalInput() {
		return isDecimalInput;
	}
	public void setDecimalInput(boolean newValue) {
		this.isDecimalInput = newValue;
	}
	
	
	///////////////////////////////////////////		
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String newValue) {
		this.visibility = newValue;
	}
	
	///////////////////////////////////////////		
	public String getComment() {
		return comment;
	}
	public void setComment(String newValue) {
		this.comment = newValue;
	}
	
	
	
}
