package com.pandora;

import java.sql.Timestamp;
import java.util.Vector;

public class ExternalDataTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private String externalId;
	
	private String planningId;
	
	private String externalSys;
	
	private Timestamp eventDate;
	
	private String source;
	
	private String destination;
	
	private Timestamp messageDate;
	
	private String summary;
	
	private String body;

	private Integer priority;
	
	private Timestamp dueDate;
	
	private String externalAccount;
	
	private String externalHost;
	
	private Vector<AttachmentTO> attachments;
	
	
	
	public void addAttachment(AttachmentTO atto){
		if (attachments==null) {
			attachments = new Vector<AttachmentTO>();
		}
		attachments.add(atto);
	}

	
	public Vector<AttachmentTO> getAttachments(){
		return attachments;
	}

	
	public void setAttachments(Vector<AttachmentTO> atts) {
		attachments = atts;
	}

	
	public String getContent(){
		String content = "";
		
		if (this.getExternalSys()!=null && !this.getExternalSys().equals("")) {
			content = content + "[" +  getExternalSys() + "] ";
		}

		if (this.getExternalId()!=null && !this.getExternalId().equals("")) {
			content = content + getExternalId() + " - ";
		}
		
		if (this.getSummary()!=null && !this.getSummary().equals("")) {
			content = content + getSummary() + " ";
		}

		if (this.getBody()!=null && !this.getBody().equals("")) {
			content = content + getBody();
		}

		return content;		
	}
	
	///////////////////////////////////
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String newValue) {
		this.externalId = newValue;
	}

	
	///////////////////////////////////
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}

	
	///////////////////////////////////
	public String getExternalSys() {
		return externalSys;
	}
	public void setExternalSys(String newValue) {
		this.externalSys = newValue;
	}

	
	///////////////////////////////////
	public Timestamp getEventDate() {
		return eventDate;
	}
	public void setEventDate(Timestamp newValue) {
		this.eventDate = newValue;
	}

	
	///////////////////////////////////
	public String getSource() {
		return source;
	}
	public void setSource(String newValue) {
		this.source = newValue;
	}

	
	///////////////////////////////////
	public String getDestination() {
		return destination;
	}
	public void setDestination(String newValue) {
		this.destination = newValue;
	}

	
	///////////////////////////////////
	public Timestamp getMessageDate() {
		return messageDate;
	}
	public void setMessageDate(Timestamp newValue) {
		this.messageDate = newValue;
	}

	
	///////////////////////////////////
	public String getSummary() {
		return summary;
	}
	public void setSummary(String newValue) {
		this.summary = newValue;
	}

	
	///////////////////////////////////
	public String getBody() {
		return body;
	}
	public void setBody(String newValue) {
		this.body = newValue;
	}
	
	///////////////////////////////////	
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer newValue) {
		if (newValue==null){
			this.priority = new Integer("0");
		} else {
			this.priority = newValue;
		}
	}
	
	
	///////////////////////////////////	
	public Timestamp getDueDate() {
		return dueDate;
	}
	public void setDueDate(Timestamp newValue) {
		this.dueDate = newValue;
	}
	
	
	///////////////////////////////////	
	public String getExternalAccount() {
		return externalAccount;
	}
	public void setExternalAccount(String newValue) {
		this.externalAccount = newValue;
	}
	
	
	///////////////////////////////////	
	public String getExternalHost() {
		return externalHost;
	}
	public void setExternalHost(String newValue) {
		this.externalHost = newValue;
	}
	
}
