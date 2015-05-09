package com.pandora.gui.struts.form;

public class BrowseArtifactForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String rev;
	
	private String projectId;
	
	private String path;
	
    private String artifactSaveType = "";
    
    private String artifactSaveLog = "";
    
    private String artifactSaveUser = "";
    
    private String artifactSavePwd = "";

    private String showUserPwd = "on";
    
    
	/**
     */
    public void clear(){
    	rev = null;
    	projectId=null;
    	path = "";
    	artifactSaveType = "1";
        artifactSaveLog = "";
        artifactSaveUser = "";
        artifactSavePwd = "";
        showUserPwd = "on";
        this.setSaveMethod(null, null);
    }

    
    ///////////////////////////////////////////////
	public String getArtifactSaveType() {
		return artifactSaveType;
	}
	public void setArtifactSaveType(String newValue) {
		this.artifactSaveType = newValue;
	}
	
	
    ///////////////////////////////////////////////
	public String getArtifactSaveLog() {
		return artifactSaveLog;
	}
	public void setArtifactSaveLog(String newValue) {
		this.artifactSaveLog = newValue;
	}
	
    ///////////////////////////////////////////////
	public String getArtifactSaveUser() {
		return artifactSaveUser;
	}
	public void setArtifactSaveUser(String newValue) {
		this.artifactSaveUser = newValue;
	}
	
    ///////////////////////////////////////////////
	public String getArtifactSavePwd() {
		return artifactSavePwd;
	}
	public void setArtifactSavePwd(String newValue) {
		this.artifactSavePwd = newValue;
	}

    ///////////////////////////////////////////////
	public String getRev() {
		return rev;
	}
	public void setRev(String newValue) {
		this.rev = newValue;
	}

	
    ///////////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}


    ///////////////////////////////////////////////
	public String getPath() {
		return path;
	}
	public void setPath(String newValue) {
		this.path = newValue;
	}


	///////////////////////////////////////////////
	public String getShowUserPwd() {
		return showUserPwd;
	}
	public void setShowUserPwd(String newValue) {
		this.showUserPwd = newValue;
	}


	
}
