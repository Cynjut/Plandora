package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class GanttPanelForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
	private String projectId;
	
	private String resourceId;
	
	private String requirementId;

	private String initialDate;
	
	private String finalDate;
	
	private String visibility;
	
	private String lockedGantt;
	
	private String htmlGanttTimeLine;
	
	private String htmlGanttLeftBar;
	
	private String htmlGanttBody;
	
	private String refSlot;
	
	private String resourceTaskId;

	private String changeTaskCommand;
	
	private boolean hideOccurrences;

	
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	this.hideOccurrences = false;
	}

	
	////////////////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	
	////////////////////////////////////////////////////	
	public String getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(String newValue) {
		this.initialDate = newValue;
	}
	
	
	////////////////////////////////////////////////////	
	public String getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(String newValue) {
		this.finalDate = newValue;
	}
	
	
	////////////////////////////////////////////////////		
	public String getHtmlGanttTimeLine() {
		return htmlGanttTimeLine;
	}
	public void setHtmlGanttTimeLine(String newValue) {
		this.htmlGanttTimeLine = newValue;
	}
	
	
	////////////////////////////////////////////////////	
	public String getHtmlGanttLeftBar() {
		return htmlGanttLeftBar;
	}
	public void setHtmlGanttLeftBar(String newValue) {
		this.htmlGanttLeftBar = newValue;
	}
	
	
	////////////////////////////////////////////////////		
	public String getHtmlGanttBody() {
		return htmlGanttBody;
	}
	public void setHtmlGanttBody(String newValue) {
		this.htmlGanttBody = newValue;
	}
	
	
	////////////////////////////////////////////////////			
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}
	
	
	////////////////////////////////////////////////////	
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String newValue) {
		this.visibility = newValue;
	}
	
	public boolean isWeekDayVisibility() {
		boolean response = true;
		if (this.visibility!=null && this.visibility.trim().equals("2")) {
			response = false;
		}
		return response;
	}
	
	
	////////////////////////////////////////////////////		
	public String getLockedGantt() {
		return lockedGantt;
	}
	public void setLockedGantt(String newValue) {
		this.lockedGantt = newValue;
	}
	
	
	////////////////////////////////////////////////////	
	public String getResourceTaskId() {
		return resourceTaskId;
	}
	public void setResourceTaskId(String newValue) {
		this.resourceTaskId = newValue;
	}
	
	
	////////////////////////////////////////////////////		
	public String getRefSlot() {
		return refSlot;
	}
	public void setRefSlot(String newValue) {
		this.refSlot = newValue;
	}
	
	
	////////////////////////////////////////////////////		
	public String getChangeTaskCommand() {
		return changeTaskCommand;
	}
	public void setChangeTaskCommand(String newValue) {
		this.changeTaskCommand = newValue;
	}
	
	
	////////////////////////////////////////////////////
	public boolean getHideOccurrences() {
		return hideOccurrences;
	}
	public void setHideOccurrences(boolean newValue) {
		this.hideOccurrences = newValue;
	}
	
	
	////////////////////////////////////////////////////
	public String getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(String newValue) {
		this.requirementId = newValue;
	}	

	
	
}
