package com.pandora.gui.struts.form;

public class RepositoryFileViewerForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String projectId;
	
	private String outputType;

	private String formTitle;
	
	private String userLocale;
	
	private String serverURI;
	
	private String newpath;
	
	
	//////////////////////////////////////////////
	public String getServerURI(){
		return this.serverURI;
	}
	public void setServerURI(String newValue){
		this.serverURI = newValue;
	}
	
	////////////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	////////////////////////////////////////////////
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String newValue) {
		this.outputType = newValue;
	}

	
	////////////////////////////////////////////////
	public String getFormTitle() {
		return formTitle;
	}
	public void setFormTitle(String newValue) {
		this.formTitle = newValue;
	}

	
	////////////////////////////////////////////////
	public String getNewpath() {
		return newpath;
	}
	public void setNewpath(String newValue) {
		this.newpath = newValue;
	}


	////////////////////////////////////////////////
	public String getUserLocale() {
		return userLocale;
	}
	public void setUserLocale(String newValue) {
		this.userLocale = newValue;
	}
	
	
	
}
