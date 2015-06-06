package com.pandora;

import java.sql.Timestamp;

public class EdiTO extends TransferObject {

	public static String RSS_TYPE_UNCL_TASK  = "UNCL_TASK"; 
	public static String RSS_TYPE_ALL_TASK   = "ALL_TASK";
	public static String RSS_TYPE_TEAM_INFO  = "TEAM_INFO";
	public static String RSS_TYPE_UNCL_REQ   = "UNCL_REQ"; 
	public static String RSS_TYPE_ALL_RISK   = "ALL_RISK"; 
	public static String RSS_TYPE_ALL_OCC    = "ALL_OCC";
	public static String ICAL_TYPE_ALL_OCC   = "CAL_EVENTS";
	public static String ICAL_TYPE_ALL_TSK   = "CAL_TASKS";
	
	private static final long serialVersionUID = 1L;
	
	private String userId;
	
	private String ediId;
	
	private String ediUUID;
	
	private Timestamp updateDate;

	
	///////////////////////////////////////////
	public String getUserId() {
		return userId;
	}
	public void setUserId(String newValue) {
		this.userId = newValue;
	}

	///////////////////////////////////////////
	public String getEdiId() {
		return ediId;
	}
	public void setEdiId(String newValue) {
		this.ediId = newValue;
	}

	///////////////////////////////////////////
	public String getEdiUUID() {
		return ediUUID;
	}
	public void setEdiUUID(String newValue) {
		this.ediUUID = newValue;
	}

	///////////////////////////////////////////
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp newValue) {
		this.updateDate = newValue;
	}
	
	public boolean isRss(){
		boolean response = false;
		if (ediId!=null) {
			response = (ediId.equalsIgnoreCase(RSS_TYPE_ALL_OCC) || ediId.equalsIgnoreCase(RSS_TYPE_ALL_RISK) 
						|| ediId.equalsIgnoreCase(RSS_TYPE_ALL_TASK) || ediId.equalsIgnoreCase(RSS_TYPE_TEAM_INFO)
						|| ediId.equalsIgnoreCase(RSS_TYPE_UNCL_REQ) || ediId.equalsIgnoreCase(RSS_TYPE_UNCL_TASK));
		}
		return response;
	}
}
