package com.pandora.bus;

import java.util.Locale;

import com.pandora.CalendarSyncTO;
import com.pandora.OccurrenceTO;

public interface CalendarSyncInterface {

	public CalendarSyncTO populateCalendarFields(OccurrenceTO oto, String mask, Locale loc);
	
}
