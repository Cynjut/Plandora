package com.pandora;

import java.sql.Timestamp;

public class TeamInfoTO extends TransferObject {

	public static final String TYPE_TASK        = "TSK";
	public static final String TYPE_REQUIREMENT = "REQ";
	public static final String TYPE_RISK        = "RSK";
	public static final String TYPE_OCCURRENCE  = "OCC";
	public static final String TYPE_ATTACHMENT  = "ATT";
	public static final String TYPE_TOPIC       = "TPC";
	
	private static final long serialVersionUID = 1L;

	private String name;
	
	private String status;
	
	private String comment;
	
	private String type;
	
	private String parentTopic;
	
	private String parentTopicUser;
	
	private String parentId;
	
	private ProjectTO project;
	
	private UserTO user;
	
	private Timestamp creationDate;
	
	
	//////////////////////////////////////////////
	public void setProject(ProjectTO newValue) {
		project = newValue;
	}
	public ProjectTO getProject() {
		return project;
	}

	
	//////////////////////////////////////////////	
	public void setName(String newValue) {
		name = newValue;
	}
	public String getName() {
		return name;
	}
	
	
	//////////////////////////////////////////////
	public String getStatus() {
		return status;
	}
	public void setStatus(String newValue) {
		this.status = newValue;
	}
	
	
	//////////////////////////////////////////////
	public UserTO getUser() {
		return user;
	}
	public void setUser(UserTO newValue) {
		this.user = newValue;
	}
	
	
	//////////////////////////////////////////////	
	public String getComment() {
		return comment;
	}
	public void setComment(String newValue) {
		this.comment = newValue;
	}
	
	
	//////////////////////////////////////////////	
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Timestamp newValue) {
		this.creationDate = newValue;
	}
	
	
	//////////////////////////////////////////////
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}
	
	
	//////////////////////////////////////////////
	public String getParentTopic() {
		return parentTopic;
	}
	public void setParentTopic(String newValue) {
		this.parentTopic = newValue;
	}
	
	
	//////////////////////////////////////////////
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String newValue) {
		this.parentId = newValue;
	}
	
	
	//////////////////////////////////////////////	
	public String getParentTopicUser() {
		return parentTopicUser;
	}
	public void setParentTopicUser(String newValue) {
		this.parentTopicUser = newValue;
	}
	

}
