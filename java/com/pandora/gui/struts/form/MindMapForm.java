package com.pandora.gui.struts.form;

public class MindMapForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String serverURI;
    
    private String formTitle;
    
    private String planningId;
    
    private String projectId;
    
    
    //////////////////////////////////////
    public String getServerURI() {
        return serverURI;
    }
    public void setServerURI(String newValue) {
        this.serverURI = newValue;
    }

    
    //////////////////////////////////////
    public String getFormTitle() {
		return formTitle;
	}
	public void setFormTitle(String newValue) {
		this.formTitle = newValue;
	}
	
	
    //////////////////////////////////////
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}
	
	
    //////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
    
	
	
}
