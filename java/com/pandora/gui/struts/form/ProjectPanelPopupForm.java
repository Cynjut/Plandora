package com.pandora.gui.struts.form;

public class ProjectPanelPopupForm extends GeneralStrutsForm {

	private static final long	serialVersionUID	= 1L;
	
	private String projectId;
	
	private String panelBoxId;
	
	private String panelBoxHtml;
	
    //////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	

    //////////////////////////////////////////
	public String getPanelBoxId() {
		return panelBoxId;
	}
	public void setPanelBoxId(String newValue) {
		this.panelBoxId = newValue;
	}

    //////////////////////////////////////////
	public String getPanelBoxHtml() {
		return panelBoxHtml;
	}
	public void setPanelBoxHtml(String newValue) {
		this.panelBoxHtml = newValue;
	}	
	
}
