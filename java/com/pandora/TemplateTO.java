package com.pandora;

import java.sql.Timestamp;

public class TemplateTO extends PlanningTO{
	
	private static final long serialVersionUID = 1L;

	private String name;
	
	private ProjectTO project;
	
	private Timestamp deprecatedDate;

	private NodeTemplateTO rootNode;
	
	private CategoryTO category;
	
	
	
	public TemplateTO(){
	}
	
	
	public TemplateTO(String newId) {
		this.setId(newId);
	}

	
	//////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	//////////////////////////////////
	public ProjectTO getProject() {
		return project;
	}
	public void setProject(ProjectTO newValue) {
		this.project = newValue;
	}

	
	//////////////////////////////////
	public Timestamp getDeprecatedDate() {
		return deprecatedDate;
	}
	public void setDeprecatedDate(Timestamp newValue) {
		this.deprecatedDate = newValue;
	}


	//////////////////////////////////
	public NodeTemplateTO getRootNode() {
		return rootNode;
	}
	public void setRootNode(NodeTemplateTO newValue) {
		this.rootNode = newValue;
	}


	//////////////////////////////////
	public CategoryTO getCategory() {
		return category;
	}
	public void setCategory(CategoryTO newValue) {
		this.category = newValue;
	}	
	
	
}
