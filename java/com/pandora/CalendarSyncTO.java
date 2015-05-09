package com.pandora;

public class CalendarSyncTO extends TransferObject {

	private static final long serialVersionUID = 1L;
	
	private String name = "";
	private OccurrenceTO occurrence = null;
	
	private String description = "";
    private String location = "";
    private String eventDate = "";
    private String eventTime = "";
    private String duration = "";
    private String conclusion = "";
    private String dateProperties = "";
    private boolean isAllDayEvent = false;
    
    
    
	///////////////////////////////////////        
	public OccurrenceTO getOccurrence() {
		return occurrence;
	}
	public void setOccurrence(OccurrenceTO newValue) {
		this.occurrence = newValue;
	}
	
	
	///////////////////////////////////////    
    public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}
	
	
	///////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}
	
	
    ///////////////////////////////////////
	public String getLocation() {
		return location;
	}
	public void setLocation(String newValue) {
		this.location = newValue;
	}
	
	
    ///////////////////////////////////////
	public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String newValue) {
		this.eventDate = newValue;
	}
	
	
    ///////////////////////////////////////
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String newValue) {
		this.eventTime = newValue;
	}
	
	
    ///////////////////////////////////////
	public String getDuration() {
		return duration;
	}
	public void setDuration(String newValue) {
		this.duration = newValue;
	}
	
	
    ///////////////////////////////////////
	public String getConclusion() {
		return conclusion;
	}
	public void setConclusion(String newValue) {
		this.conclusion = newValue;
	}
	
	
    ///////////////////////////////////////	
	public boolean isAllDayEvent() {
		return isAllDayEvent;
	}
	public void setIsAllDayEvent(boolean newValue) {
		this.isAllDayEvent = newValue;
	}
	
	
    ///////////////////////////////////////	
	public String getDateProperties() {
		return dateProperties;
	}
	public void setDateProperties(String newValue) {
		this.dateProperties = newValue;
	}
	
	
}
