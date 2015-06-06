package com.pandora.bus.occurrence;

import java.util.Locale;
import java.util.Vector;

import com.pandora.CalendarSyncTO;
import com.pandora.FieldValueTO;
import com.pandora.OccurrenceTO;
import com.pandora.TransferObject;
import com.pandora.bus.CalendarSyncInterface;

/**
 */
public class MilestoneOccurrence extends Occurrence implements CalendarSyncInterface{
    
    public static final String MILE_DATE        = "DATE";
    
    public static final String MILE_DESCRIPTION = "DESCRIPTIO";  
          
    
    public String getUniqueName() {
        return "label.occurrence.milestone";
    }
    
    public Vector getFields(){
        Vector<FieldValueTO> response = new Vector<FieldValueTO>();
        response.add(new FieldValueTO(MILE_DATE, "label.occurrence.milestone.date", FieldValueTO.FIELD_TYPE_DATE, 10, 10));
        response.add(new FieldValueTO(MILE_DESCRIPTION, "label.occurrence.milestone.description", FieldValueTO.FIELD_TYPE_AREA, 255, 8));
        
        return response;
    }
    
    
    public String getContextHelp() {
        return "label.occurrence.milestone.help";
    }
    
    
    public Vector getStatusValues() {
        Vector<TransferObject> response = new Vector<TransferObject>();
        response.add(new TransferObject(STATE_START,   "label.occurrence.milestone.status.planned"));
        response.add(new TransferObject("50",          "label.occurrence.milestone.status.postponed"));
        response.add(new TransferObject(STATE_FINAL_1, "label.occurrence.milestone.status.done"));        
        response.add(new TransferObject(STATE_FINAL_2, "label.occurrence.milestone.status.aborted"));
        return response;
    }

    
	public CalendarSyncTO populateCalendarFields(OccurrenceTO oto, String mask, Locale loc) {
		CalendarSyncTO cal = new CalendarSyncTO();
		cal.setDescription(oto.getFieldValueByKey(MilestoneOccurrence.MILE_DESCRIPTION, this, mask, loc));
		cal.setEventDate(oto.getFieldValueByKey(MilestoneOccurrence.MILE_DATE, this, mask, loc));
		cal.setLocation("");
		cal.setEventTime("00:00:00");
		cal.setDuration("1");
		cal.setConclusion("");
		cal.setIsAllDayEvent(true);
		cal.setDateProperties("VALUE=DATE");
		cal.setName(oto.getName());
		return cal;
	}

	
	public String getDateFieldId() {
		return MILE_DATE;
	}
}
