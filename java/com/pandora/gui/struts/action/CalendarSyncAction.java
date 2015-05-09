package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CalendarSyncTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.bus.CalendarSyncInterface;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

public class CalendarSyncAction extends GeneralStrutsAction {

	//How to call:
	// http://localhost/[project]/do/calendarSync?projectId=2  OR
	// http://localhost/[project]/do/calendarSync?prj=MY_PROJ_NAME
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		ProjectDelegate pdel = new ProjectDelegate();
		StringBuffer sb = new StringBuffer();
		StringBuffer header = new StringBuffer();
		Locale loc = null;
		
		try {
			//service(request);

			//get parameters from URL
			String projectId = request.getParameter("projectId");
			if (projectId==null || projectId.trim().equals("")) {
				String projectName = request.getParameter("prj");
				ProjectTO pto = pdel.getProjectByName(projectName);
				if (pto!=null) {
					projectId = pto.getId();
				}
			}

			if (projectId!=null && !projectId.trim().equals("")) {
				
				String app = this.getBundleMessage(request, "footer.copyright");
				header.append("BEGIN:VCALENDAR\n");
				header.append("PRODID:-//" + app + "//BR\n");
				header.append("VERSION:\n");
				header.append("CALSCALE:GREGORIAN\n");
				header.append("METHOD:PUBLISH\n");

				UserDelegate udel = new UserDelegate();
				UserTO root = udel.getRoot();
				String allClasses = root.getPreference().getPreference(PreferenceTO.CALEND_SYNC_BUS_CLASS);
				
				OccurrenceDelegate del = new OccurrenceDelegate();
				Vector v = del.getOccurenceList(projectId, false);
				for (int i = 0; i < v.size(); i++) {
					OccurrenceTO oto = (OccurrenceTO) v.elementAt(i);
					loc = oto.getLocale();
					oto = del.getOccurrenceObject(oto);
					Object bus = OccurrenceTO.getClass(allClasses, oto.getSource());
					String mask = this.getResources(request).getMessage("calendar.format", loc);
					
					if (bus!=null && oto.isVisible()) { //only public events and milestones

						CalendarSyncTO csto = ((CalendarSyncInterface)bus).populateCalendarFields(oto, mask, loc);

						if (csto.getEventDate()!=null && !csto.getEventDate().trim().equals("") &&
								csto.getEventTime()!=null && !csto.getEventTime().trim().equals("") &&
								csto.getDuration()!=null && !csto.getDuration().trim().equals("")) {
							
							
							String location = "";
							if (csto.getLocation()!=null) {
								location = StringUtil.getIso2Utf8(csto.getLocation());
							}
						    						
							String desc = "";
							if (csto.getDescription()!=null) {
								desc = desc + csto.getDescription() + "\n"; 
							}
							if (csto.getConclusion()!=null) {
								desc = desc + csto.getConclusion() + "\n"; 
							}
							desc = StringUtil.getIso2Utf8(desc);
							
							Timestamp iniDate = DateUtil.getDateTime(csto.getEventDate() + " " + csto.getEventTime(), mask + " hh:mm:ss", loc);
							Timestamp endPeriod = DateUtil.getChangedDate(iniDate, Calendar.MINUTE, Integer.parseInt(csto.getDuration()));	
							
							String prop = csto.getDateProperties();
							
							sb.append("BEGIN:VEVENT\n");
							sb.append("DTSTART;" + prop + ":" + StringUtil.getGoogleDateFormat(iniDate, loc, !csto.isAllDayEvent()) + "\n");
							sb.append("DTEND;" + prop + ":" + StringUtil.getGoogleDateFormat(endPeriod, loc, !csto.isAllDayEvent()) + "\n");
							sb.append("DTSTAMP:" + DateUtil.getDate(oto.getCreationDate(), "yyyyMMdd", loc) + "T080000\n");
							sb.append("UID:" + oto.getId() + "@plandora.org\n");
							sb.append("CREATED:" + DateUtil.getDate(oto.getCreationDate(), "yyyyMMdd", loc) + "T080000\n");
							sb.append("CLASS:PRIVATE\n");
							sb.append("DESCRIPTION:" + desc + "\n");
							//sb.append("LAST-MODIFIED:20080624T185411Z\n");
							sb.append("SEQUENCE:0\n");
							if (location!=null) {
								sb.append("LOCATION:" + location + "\n");	
							}
							sb.append("STATUS:CONFIRMED\n");
							sb.append("ACTION:DISPLAY\n");
							sb.append("SUMMARY:" + StringUtil.getIso2Utf8(oto.getName()) + "\n");
							sb.append("TRANSP:TRANSPARENT\n");
							sb.append("END:VEVENT\n");							
						}
					}
				}

				sb.append("END:VCALENDAR");
				header.append(sb);
			}

			
		} catch (Exception e) {
			setErrorFormSession(request, "error.authentication", e);
		} finally {
			// put response content into Standard output
			ServletOutputStream sos;
			try {
				sos = response.getOutputStream();
				response.setContentType("text/calendar; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=\"basic.ics");
				response.setContentLength(header.length());
				String content = header.toString();
				sos.write(content.getBytes());
				sos.close();
			} catch (Exception e2) {
				setErrorFormSession(request, "error.authentication", e2);
			}
		}
		return null;
	}
	
	/*
	public void service(HttpServletRequest req) {
		try {
			BufferedInputStream is = new
			BufferedInputStream(req.getInputStream());
			int available = is.available();
			
		    BufferedReader in = req.getReader();
		    char[] cbuf = new char[10000];
		    in.read(cbuf, 0, available );

		    System.out.println(cbuf);
			
		} catch (Exception e ) {}
	}
*/

}
