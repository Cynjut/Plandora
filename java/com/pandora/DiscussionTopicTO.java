package com.pandora;

import java.sql.Timestamp;

public class DiscussionTopicTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private String planningId;
	
	private String content;
	
	private DiscussionTopicTO parentTopic;
	
	private UserTO user; 
	
	private Timestamp creationDate;

	//transient attributes
	private Timestamp lastUpd;
	private Integer replyNumber;
	
    /**
     * Constructor 
     */
	public DiscussionTopicTO(){
	}
	
    /**
     * Constructor 
     */
	public DiscussionTopicTO(String newValue){
		setId(newValue);
	}

	//////////////////////////////////
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}

	
	//////////////////////////////////	
	public String getContent() {
		return content;
	}
	public void setContent(String newValue) {
		this.content = newValue;
	}

	
	//////////////////////////////////	
	public DiscussionTopicTO getParentTopic() {
		return parentTopic;
	}
	public void setParentTopic(DiscussionTopicTO newValue) {
		this.parentTopic = newValue;
	}

	
	//////////////////////////////////	
	public UserTO getUser() {
		return user;
	}
	public void setUser(UserTO newValue) {
		this.user = newValue;
	}

	
	//////////////////////////////////	
	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp newValue) {
		this.creationDate = newValue;
	}

	
	//////////////////////////////////	
	public Timestamp getLastUpd() {
		return lastUpd;
	}
	public void setLastUpd(Timestamp newValue) {
		this.lastUpd = newValue;
	}

	
	//////////////////////////////////		
	public Integer getReplyNumber() {
		return replyNumber;
	}
	public void setReplyNumber(Integer newValue) {
		this.replyNumber = newValue;
	}
	
	
	public DiscussionTopicTO getClone() {
		DiscussionTopicTO response = new DiscussionTopicTO();
		response.setContent(this.getContent());
		response.setCreationDate(this.getCreationDate());
		response.setGenericTag(response.getGenericTag());
		response.setGridRowNumber(this.getGridRowNumber());
		response.setId(this.getId());
		response.setLastUpd(this.getLastUpd());
		response.setParentTopic(this.getParentTopic());
		response.setPlanningId(this.getPlanningId());
		response.setReplyNumber(this.getReplyNumber());
		response.setUser(this.getUser());
		return response;
	}
	
}
