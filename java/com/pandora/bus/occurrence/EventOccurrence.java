package com.pandora.bus.occurrence;

import java.util.Locale;
import java.util.Vector;

import com.pandora.CalendarSyncTO;
import com.pandora.FieldValueTO;
import com.pandora.OccurrenceTO;
import com.pandora.TransferObject;
import com.pandora.bus.CalendarSyncInterface;
import com.pandora.helper.StringUtil;

/**
 */
public class EventOccurrence extends Occurrence implements CalendarSyncInterface{

    public static final String EVENT_DATE        = "DATE";
    
    public static final String EVENT_TIME        = "TIME";
    
    public static final String EVENT_DURATION    = "DURATION";
    
    public static final String EVENT_DESCRIPTION = "DESCRIPTIO";
    
    public static final String EVENT_CONCLUSION  = "CONCLUSION";    
    
    public static final String EVENT_REASON      = "REASON";
    
    public static final String EVENT_LOCATION    = "LOCATION";
    
    public static final String EVENT_PARTICIP    = "PARTICIP";
   
    
    public String getUniqueName() {
        return "label.occurrence.event";
    }
    
    public Vector getFields(){
        Vector<FieldValueTO> response = new Vector<FieldValueTO>();
        response.add(new FieldValueTO(EVENT_DATE,    "label.occurrence.event.date", FieldValueTO.FIELD_TYPE_DATE, 10, 10));
        
        Vector<TransferObject> timeList = this.getTimeForDay();
        response.add(new FieldValueTO(EVENT_TIME, "label.occurrence.event.time", timeList));       
        
        response.add(new FieldValueTO(EVENT_DURATION,"label.occurrence.event.duration", FieldValueTO.FIELD_TYPE_TEXT, 5, 10));
        response.add(new FieldValueTO(EVENT_LOCATION,"label.occurrence.event.location", FieldValueTO.FIELD_TYPE_TEXT, 60, 60));
        
        Vector<TransferObject> resonList = new Vector<TransferObject>();
        resonList.add(new TransferObject("1", "label.occurrence.event.reason.meeting"));
        resonList.add(new TransferObject("2", "label.occurrence.event.reason.celebration"));
        resonList.add(new TransferObject("3", "label.occurrence.event.reason.workshop"));
        resonList.add(new TransferObject("4", "label.occurrence.event.reason.training"));
        resonList.add(new TransferObject("9", "label.occurrence.event.reason.other"));
        response.add(new FieldValueTO(EVENT_REASON, "label.occurrence.event.reason", resonList));
        
        response.add(new FieldValueTO(EVENT_DESCRIPTION, "label.occurrence.event.description", FieldValueTO.FIELD_TYPE_AREA, 255, 5));
        response.add(new FieldValueTO(EVENT_CONCLUSION,  "label.occurrence.event.conclusion", FieldValueTO.FIELD_TYPE_AREA, 255, 5));
        
        Vector<FieldValueTO> columns = new Vector<FieldValueTO>();
        columns.add(new FieldValueTO("PART_EMAIL","label.occurrence.event.team.email", FieldValueTO.FIELD_TYPE_TEXT, 150, 68));
        Vector<TransferObject> confList = new Vector<TransferObject>();
        confList.add(new TransferObject("", ""));
        confList.add(new TransferObject("1", "label.yes"));
        confList.add(new TransferObject("2", "label.no"));
        confList.add(new TransferObject("3", "label.occurrence.event.team.maybe"));
        columns.add(new FieldValueTO("PART_CONF", "label.occurrence.event.team.conf", confList));
        response.add(new FieldValueTO(EVENT_PARTICIP, "label.occurrence.event.team", columns, null));
        
        return response;
    }
    
    
    public String getContextHelp() {
        return "label.occurrence.event.help";
    }
    
    
    public Vector getStatusValues() {
        Vector<TransferObject> response = new Vector<TransferObject>(); 
        
        response.add(new TransferObject(STATE_START,   "label.occurrence.event.status.open"));
        response.add(new TransferObject("50",          "label.occurrence.event.status.postponed"));
        response.add(new TransferObject(STATE_FINAL_1, "label.occurrence.event.status.canceled"));
        response.add(new TransferObject(STATE_FINAL_2, "label.occurrence.event.status.finished"));
        return response;
    }    

    private Vector<TransferObject> getTimeForDay(){
        Vector<TransferObject> response = new Vector<TransferObject>();
        for (int i= 8; i<=18; i++) {
            String hour = StringUtil.fill(i+"", "0", 2, true);
            response.add(new TransferObject(hour + ":00:00", hour + ":00"));
            response.add(new TransferObject(hour + ":30:00", hour + ":30"));
        }
        
        for (int i= 0; i<=7; i++) {
            String hour = StringUtil.fill(i+"", "0", 2, true);
            response.add(new TransferObject(hour + ":00:00", hour + ":00"));
            response.add(new TransferObject(hour + ":30:00", hour + ":30"));
        }

        for (int i= 19; i<=23; i++) {
            String hour = StringUtil.fill(i+"", "0", 2, true);
            response.add(new TransferObject(hour + ":00:00", hour + ":00"));
            response.add(new TransferObject(hour + ":30:00", hour + ":30"));
        }

        return response;
    }
    

	public CalendarSyncTO populateCalendarFields(OccurrenceTO oto, String mask, Locale loc) {
		CalendarSyncTO cal = new CalendarSyncTO();
		cal.setDescription(oto.getFieldValueByKey(EventOccurrence.EVENT_DESCRIPTION, this, mask, loc));
	    cal.setLocation(oto.getFieldValueByKey(EventOccurrence.EVENT_LOCATION, this, mask, loc));
		cal.setEventDate(oto.getFieldValueByKey(EventOccurrence.EVENT_DATE, this, mask, loc));
		cal.setEventTime(oto.getFieldValueByKey(EventOccurrence.EVENT_TIME, this, mask, loc));
	    cal.setDuration(oto.getFieldValueByKey(EventOccurrence.EVENT_DURATION, this, mask, loc));
	    cal.setConclusion(oto.getFieldValueByKey(EventOccurrence.EVENT_CONCLUSION, this, mask, loc));
		cal.setIsAllDayEvent(false);
		cal.setDateProperties("TZID=America/Sao_Paulo");
		cal.setName(oto.getName());
		return cal;
	}

	public String getDateFieldId() {
		return EVENT_DATE;
	}
	
}
