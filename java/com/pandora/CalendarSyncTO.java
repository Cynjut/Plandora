package com.pandora;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.Uid;

import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

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
	
	public String toString(){
		return name;
	}
	
	
	public VEvent toVEvent(TimeZone timezone, TzId timeZoneId) {
		String tsStr = this.getEventDate() + " " + this.getEventTime();
		Timestamp iniTs = DateUtil.getDateTime(tsStr, "dd/MM/yyyy hh:mm:ss", this.getOccurrence().getLocale());
		Timestamp finalTs = null;
		if (iniTs!=null) {
			if (this.getDuration()!=null && !this.getDuration().equals("") && StringUtil.hasOnlyDigits(this.getDuration())) {
				finalTs = DateUtil.getChangedDate(iniTs, Calendar.MINUTE, Integer.parseInt(this.getDuration()));	
			} else {
				finalTs = iniTs;
			}
		}
		
		java.util.Calendar startDate = new GregorianCalendar();
		startDate.setTimeZone(timezone);
		if (iniTs!=null) {
			startDate.set(Calendar.MONTH, DateUtil.get(iniTs, Calendar.MONTH));
			startDate.set(Calendar.DAY_OF_MONTH, DateUtil.get(iniTs, Calendar.DAY_OF_MONTH));
			startDate.set(Calendar.YEAR, DateUtil.get(iniTs, Calendar.YEAR));
			startDate.set(Calendar.HOUR_OF_DAY, DateUtil.get(iniTs, Calendar.HOUR_OF_DAY));
			startDate.set(Calendar.MINUTE, DateUtil.get(iniTs, Calendar.MINUTE));
			startDate.set(Calendar.SECOND, DateUtil.get(iniTs, Calendar.SECOND));			
		}

		java.util.Calendar endDate = new GregorianCalendar();
		endDate.setTimeZone(timezone);
		if (finalTs!=null) {
			endDate.set(Calendar.MONTH, DateUtil.get(finalTs, Calendar.MONTH));
			endDate.set(Calendar.DAY_OF_MONTH, DateUtil.get(finalTs, Calendar.DAY_OF_MONTH));
			endDate.set(Calendar.YEAR, DateUtil.get(finalTs, Calendar.YEAR));
			endDate.set(Calendar.HOUR_OF_DAY, DateUtil.get(finalTs, Calendar.HOUR_OF_DAY));
			endDate.set(Calendar.MINUTE, DateUtil.get(finalTs, Calendar.MINUTE));
			endDate.set(Calendar.SECOND, DateUtil.get(finalTs, Calendar.SECOND));			
		}

		// Create the event
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		VEvent meeting = new VEvent(start, end, this.getName());
		meeting.getProperties().add(new Location(this.getLocation()));
		meeting.getProperties().add(new Description(this.getDescription()));
		meeting.getProperties().add(timeZoneId);
		
		// generate unique identifier..
		Uid uid = new Uid("PLAND_" + this.getId());
		meeting.getProperties().add(uid);

		// add attendees..
		//Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
		//dev1.getParameters().add(Role.REQ_PARTICIPANT);
		//dev1.getParameters().add(new Cn("Developer 1"));
		//meeting.getProperties().add(dev1);
		//Attendee dev2 = new Attendee(URI.create("mailto:dev2@mycompany.com"));
		//dev2.getParameters().add(Role.OPT_PARTICIPANT);
		//dev2.getParameters().add(new Cn("Developer 2"));
		//meeting.getProperties().add(dev2);
		
		return meeting;
	}
	
	
}
