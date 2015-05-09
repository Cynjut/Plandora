package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class AgilePanelTaskForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
	private String taskProjectId;
	
	private String taskId;
	
	private String name;
	
	private String description;
	
	private String requirementId;
	
	private String resourceId;
	
	private String categoryId;
	
	private String estimatedTime;
	
	private String estimatedDate;
	
	private boolean isUnPredictable = false;
	
	private String isOpen = "on";
	
	private String parentTaskId;
	

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.isUnPredictable = false;
	}
	
	
	//////////////////////////////////////////
	public String getTaskProjectId() {
		return taskProjectId;
	}
	public void setTaskProjectId(String newValue) {
		this.taskProjectId = newValue;
	}

	
	//////////////////////////////////////////
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String newValue) {
		this.taskId = newValue;
	}

	
	//////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}
	
	
	//////////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}
	
	
	
	//////////////////////////////////////////
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}


	//////////////////////////////////////////
	public boolean getIsUnPredictable() {
		return isUnPredictable;
	}
	public void setIsUnPredictable(boolean newValue) {
		this.isUnPredictable = newValue;
	}

	
	//////////////////////////////////////////
	public String getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(String newValue) {
		this.requirementId = newValue;
	}

	
	//////////////////////////////////////////
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String newValue) {
		this.categoryId = newValue;
	}


	//////////////////////////////////////////
	public String getEstimatedTime() {
		return estimatedTime;
	}
	public void setEstimatedTime(String newValue) {
		this.estimatedTime = newValue;
	}

	
	//////////////////////////////////////////
	public String getEstimatedDate() {
		return estimatedDate;
	}
	public void setEstimatedDate(String newValue) {
		this.estimatedDate = newValue;
	}

	
	//////////////////////////////////////////
	public String getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(String newValue) {
		this.isOpen = newValue;
	}
	
	
	//////////////////////////////////////////	
    public String getParentTaskId() {
        return parentTaskId;
    }
    public void setParentTaskId(String newValue) {
        this.parentTaskId = newValue;
    }
}
