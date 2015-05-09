package com.pandora.gui.struts.form;

public class NewArtifactForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
	private String htmlArtifactList;
	
	private String category;
	
	private String name;
	
	private String projectId;
		
	private String selectedTemplate;
	
	
	///////////////////////////////////////////	
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}
	
	
	///////////////////////////////////////////
	public String getHtmlArtifactList() {
		return htmlArtifactList;
	}
	public void setHtmlArtifactList(String newValue) {
		this.htmlArtifactList = newValue;
	}

	
	///////////////////////////////////////////
	public String getCategory() {
		return category;
	}
	public void setCategory(String newValue) {
		this.category = newValue;
	}

	
	///////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	
	///////////////////////////////////////////
	public String getSelectedTemplate() {
		return selectedTemplate;
	}
	public void setSelectedTemplate(String newValue) {
		this.selectedTemplate = newValue;
	}
	

}
