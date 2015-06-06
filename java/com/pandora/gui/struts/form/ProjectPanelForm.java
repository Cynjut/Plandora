package com.pandora.gui.struts.form;

public class ProjectPanelForm extends GeneralStrutsForm {
	
	public static final String PROJ_INFO  = "PROJ_INFO";
	
	public static final String PROJ_STHLD = "PROJ_STHLD";
	
	
	private static final long serialVersionUID = 1L;

	private String projectId;
	
	private String panelBoxId;

	private String projectName;
	
	private String portletHtml;
	
	
	///////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	///////////////////////////////////////////
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String newValue) {
		this.projectName = newValue;
	}
	
	///////////////////////////////////////////
	public String getPanelBoxId() {
		return panelBoxId;
	}
	public void setPanelBoxId(String newValue) {
		this.panelBoxId = newValue;
	}
	
	///////////////////////////////////////////
	public String getPortletHtml() {
		return portletHtml;
	}
	public void setPortletHtml(String newValue) {
		this.portletHtml = newValue;
	}
	
	
}
