package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CalendarSyncTO;
import com.pandora.FieldValueTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.CalendarSyncInterface;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.taglib.decorator.OccurrenceColorGridDecorator;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;

public final class CalendarHtmlGadget extends HtmlGadget {

	private static final String CALENDAR_HTML_PROJECT = "PROJECT";
	
	private static final String CALENDAR_HTML_MONTH   = "MONTH";
	
	private static final String CALENDAR_HTML_YEAR    = "YEAR";
	
    private HashMap<String, ArrayList<CalendarSyncTO>> hm = new HashMap<String, ArrayList<CalendarSyncTO>>();
    

	protected String generate(Vector selectedFields) throws BusinessException {
        ProjectDelegate pdel = new ProjectDelegate();
        StringBuffer response = new StringBuffer();
        try {
        	String strMonth = super.getSelected(CALENDAR_HTML_MONTH, selectedFields);
        	int currentMonth = DateUtil.get(DateUtil.getNow(), Calendar.MONTH);        	
        	if (strMonth!=null && !strMonth.equals("")) {
        		currentMonth = Integer.parseInt(strMonth);
        	}

        	String strYear = super.getSelected(CALENDAR_HTML_YEAR, selectedFields);        	
        	int currentYear = DateUtil.get(DateUtil.getNow(), Calendar.YEAR);        	
        	if (strYear!=null && !strYear.equals("")) {
        		currentYear = Integer.parseInt(strYear);
        	}
        	
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(CALENDAR_HTML_PROJECT, selectedFields)), true);            
            if (pto!=null) {
            	this.loadOccurrences(pto, currentMonth, currentYear);
            }
            this.writeHeader(response, pto, currentMonth, currentYear);
            this.writeBody(response, currentMonth, currentYear);
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return response.toString();
	}



	public String getCategory() {
		return "label.manageOption.gadget.management";
	}

	
	public String getDescription() {
		return "label.manageOption.gadget.calendar.desc";
	}

	
	public Vector getFields() {
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();

    	try {
    		Vector<TransferObject> projList = getProjectFromUser(false);
        	response.add(new FieldValueTO(CALENDAR_HTML_PROJECT, "label.manageOption.gadget.calendar.project", projList));

        	Vector<TransferObject> montList= new Vector<TransferObject>();
        	String[] months = new DateFormatSymbols(this.handler.getLocale()).getMonths();
        	for (int i=0; i<months.length-1; i++) {
        		montList.add(new TransferObject(i+"", months[i]));
        	}
        	response.add(new FieldValueTO(CALENDAR_HTML_MONTH, "label.manageOption.gadget.calendar.month", montList));

        	Vector<TransferObject> yearList= new Vector<TransferObject>();
        	int currentYear = DateUtil.get(DateUtil.getNow(), Calendar.YEAR);
        	for (int j=currentYear-2; j<=currentYear+2; j++) {
        		yearList.add(new TransferObject(j+"", j+""));	
        	}
        	response.add(new FieldValueTO(CALENDAR_HTML_YEAR, "label.manageOption.gadget.calendar.year", yearList));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
	}

	@Override	
	public Vector<TransferObject> getFieldsId() {
		Vector<TransferObject> response = new Vector<TransferObject>();
    	int currentMonth = DateUtil.get(DateUtil.getNow(), Calendar.MONTH);        	      	
    	int currentYear = DateUtil.get(DateUtil.getNow(), Calendar.YEAR);        	
       	response.add(new TransferObject(CALENDAR_HTML_PROJECT, "-1"));
       	response.add(new TransferObject(CALENDAR_HTML_MONTH, "" + currentMonth));
       	response.add(new TransferObject(CALENDAR_HTML_YEAR, "" + currentYear));
        return response;
	}

	
	public String getId() {
		return "CALENDAR_HTML";
	}

	public String getImgLogo() {
		return "../images/gdglogo-11.png";
	}
	
	
	public int getPropertyPanelHeight() {
		return 150;
	}

	
	public int getPropertyPanelWidth() {
		return 400;
	}

	
	public String getUniqueName() {
		return "label.manageOption.gadget.calendar";
	}
	
	private void writeBody(StringBuffer response, int currentMonth, int currentYear) {
		Timestamp cursor = DateUtil.getDateTime("1", ""+currentMonth, ""+currentYear, "14", "30", "00");
		Timestamp nextFirstDay = DateUtil.getChangedDate(cursor, Calendar.MONTH, +1);
		Timestamp lastDay = DateUtil.getChangedDate(nextFirstDay, Calendar.DATE, -1);

		int lastweek = DateUtil.get(lastDay, Calendar.WEEK_OF_YEAR);
		if (lastweek==1) {
			lastDay = DateUtil.getChangedDate(lastDay, Calendar.WEEK_OF_YEAR, -1);
			lastweek = DateUtil.get(lastDay, Calendar.WEEK_OF_YEAR);
			lastweek++;
		}
		
		int numOfWeeks = lastweek - DateUtil.get(cursor, Calendar.WEEK_OF_YEAR) + 1;
		int rowHeight = (100 / numOfWeeks); 
		
		for (int row = 0; row < numOfWeeks; row++) {
			response.append("<tr class=\"formBody\">");
			for (int col = 1; col <= 7; col++) {
				response.append("<td width=\"14%\" height=\"" + rowHeight + "%\">");				
				cursor = getCell(response, cursor, col, currentMonth);
				response.append("</td>");
			}
			response.append("</tr>");
		}
		response.append("</table></br>");
	}


	private void writeHeader(StringBuffer response, ProjectTO pto, int currentMonth, int currentYear) {
		String[] weekdays = new DateFormatSymbols(this.handler.getLocale()).getShortWeekdays();
		
		String label = "";
		if (pto!=null) {
			label = pto.getName() + " - ";
		}
		
    	String[] months = new DateFormatSymbols(this.handler.getLocale()).getShortMonths();
    	label = label + months[currentMonth] + "/";
    	label = label + currentYear;
		
		response.append("<table class=\"table\" width=\"100%\" height=\"90%\" border=\"1\" bordercolor=\"#10389C\" cellspacing=\"1\" cellpadding=\"0\">");
		response.append("<tr class=\"formBody\">");
		response.append("<td colspan=\"7\" height=\"20\" ><center>" + label + "</center></td>");
		response.append("</tr>");
		response.append("<tr class=\"tableRowHeader\">");
		for (int i=1; i<=7; i++) {
			response.append("<td width=\"14%\" height=\"25\"><b><center>" + weekdays[i] + "</center></b></td>");
		}
		response.append("</tr>");
	}



	private Timestamp getCell(StringBuffer response, Timestamp cursor, int col, int currentMonth) {
		int w = DateUtil.get(cursor, Calendar.DAY_OF_WEEK);
		boolean weekend = (w==Calendar.SATURDAY || w==Calendar.SUNDAY);
		
		if (w==col && DateUtil.get(cursor, Calendar.MONTH)==currentMonth){
			
			response.append("<div class=\"formNotes\">");	
			if (weekend){
				response.append("<b>");	
			}
			response.append(DateUtil.get(cursor, Calendar.DATE));
			
			String key = "DAY_" + DateUtil.get(cursor, Calendar.DATE);
			ArrayList<CalendarSyncTO> occOfDay = hm.get(key);
			if (occOfDay!=null) {
				response.append(this.getOccIntoCell(occOfDay));				
			}
			if (weekend){
				response.append("</b>");	
			}			
			response.append("</div>");

			cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, +1);			
		} else {
			response.append("&nbsp;");	
		}
		return cursor;
	}
	
	
	private StringBuffer getOccIntoCell(ArrayList<CalendarSyncTO> occOfDay) {
		OccurrenceColorGridDecorator dec = new OccurrenceColorGridDecorator();
		StringBuffer response = new StringBuffer();
		for (int i=0; i<occOfDay.size(); i++) {
			CalendarSyncTO csto = (CalendarSyncTO)occOfDay.get(i);
			
			String projName = "";
			if (csto.getOccurrence()!=null && csto.getOccurrence().getProject()!=null && 
					csto.getOccurrence().getProject().getName()!=null) {
				projName = " - " + csto.getOccurrence().getProject().getName();
			}
			
			String label = csto.getName() + projName;
			String img = dec.getOccurrenceBallon(csto.getOccurrence());
			response.append("&nbsp;<img border=\"0\" " + HtmlUtil.getHint(label) + " src=\"../images/" + img + "\" >");
		}
	
		return response;
	}
	
	
	private void loadOccurrences(ProjectTO pto, int currentMonth, int currentYear) throws Exception{
        OccurrenceDelegate odel = new OccurrenceDelegate();
        
		UserDelegate udel = new UserDelegate();
		UserTO root = udel.getRoot();
		String allClasses = root.getPreference().getPreference(PreferenceTO.CALEND_SYNC_BUS_CLASS);

		Vector<OccurrenceTO> dbList = odel.getOccurenceList(pto.getId(), false);
        if (dbList!=null) {
        	Locale loc = null;
            Iterator<OccurrenceTO> i = dbList.iterator();
            while(i.hasNext()) {
            	OccurrenceTO oto = (OccurrenceTO)i.next();
				loc = oto.getLocale();
				String mask = super.getI18nMsg("calendar.format", loc);

            	Object bus = OccurrenceTO.getClass(allClasses, oto.getSource());
				if (bus!=null && oto.isVisible()) { //only public events and milestones
					oto = odel.getOccurrenceObject(oto);

					CalendarSyncTO csto = ((CalendarSyncInterface)bus).populateCalendarFields(oto, mask, loc);
					csto.setName(oto.getName());
					csto.setOccurrence(oto);
					
					if (csto!=null && csto.getEventDate()!=null) {
						
						Timestamp event = DateUtil.getDateTime(csto.getEventDate() + " " + csto.getEventTime(), mask + " hh:mm:ss", loc);
						if (DateUtil.get(event, Calendar.MONTH)==currentMonth &&
								DateUtil.get(event, Calendar.YEAR)==currentYear) {
							
							String key = "DAY_" + DateUtil.get(event, Calendar.DATE);
							ArrayList<CalendarSyncTO> dayOcc = hm.get(key);
							if (dayOcc==null) {
								ArrayList<CalendarSyncTO> newDayOcc = new ArrayList<CalendarSyncTO>();
								newDayOcc.add(csto);
								hm.put(key, newDayOcc);
							} else {
								dayOcc.add(csto);
							}
						}
					}
				}
        	}
        }                
		
	}
}
