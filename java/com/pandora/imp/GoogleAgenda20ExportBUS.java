package com.pandora.imp;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.TaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

/**
 */
public class GoogleAgenda20ExportBUS extends ExportBUS {
	
	
	
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFields()
     */
    public Vector<FieldValueTO> getFields() throws BusinessException {    	
    	Vector<FieldValueTO> list = new Vector<FieldValueTO>();
    	
    	FieldValueTO iniDate = new FieldValueTO("INI_DATE", "label.importExport.inidate", FieldValueTO.FIELD_TYPE_DATE, 10, 10);
    	list.add(iniDate);
    	
    	FieldValueTO finalDate = new FieldValueTO("FINAL_DATE", "label.importExport.enddate", FieldValueTO.FIELD_TYPE_DATE, 10, 10);
    	list.add(finalDate);

    	return list;
    }
    

    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getUniqueName()
     */
    public String getUniqueName() {
        return "GOOGLE_AGENDA_20_EXPORT";
    }

    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getLabel()
     */
    public String getLabel() throws BusinessException {
        return "label.importExport.googleAgenda20Export";
    }

    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFileName(com.pandora.ProjectTO)
     */    
    public String getFileName(ProjectTO pto) throws BusinessException {
        return "basicFromPlandora.ics";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getContentType()
     */    
    public String getContentType() throws BusinessException {
    	String encoding = SystemSingleton.getInstance().getDefaultEncoding();
        return "text/calendar; charset=" + encoding;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getHeader(com.pandora.ProjectTO, java.util.Vector)
     */    
    public StringBuffer getHeader(ProjectTO pto, Vector fields) throws BusinessException {
        StringBuffer sb = new StringBuffer();
        sb.append("BEGIN:VCALENDAR\n");
        sb.append("PRODID:-//Google Inc//Google Calendar 70.9054//EN\n");
        sb.append("VERSION:2.0\n");
        sb.append("CALSCALE:GREGORIAN\n");
        sb.append("METHOD:PUBLISH\n");
        return sb;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getBody(com.pandora.ProjectTO, com.pandora.UserTO, java.util.Vector)
     */    
    public StringBuffer getBody(ProjectTO pto, UserTO handler, Vector fields) throws BusinessException {
	    TaskDelegate tdel = new TaskDelegate();
	    StringBuffer response = new StringBuffer();

	    //get task list related with project in tree structure
	    Vector<TaskTO> treeTskList = tdel.getTaskListByProjectInTree(pto);	    
	    Iterator<TaskTO> j = treeTskList.iterator();
	    while(j.hasNext()){	    
	        TaskTO tto = (TaskTO)j.next();
	        Vector<ResourceTaskTO> taskResources = tto.getAllocResources(); 
	        Iterator<ResourceTaskTO> i = taskResources.iterator();
	        while(i.hasNext()) {
	            ResourceTaskTO rtto = i.next();
	            
	            if (rtto.getResource().getId().equals(handler.getId()) && rtto.getTaskStatus().isOpen()) {

	                Timestamp cursor = rtto.getActualDate();
	                if (cursor==null) {
	                	cursor = rtto.getStartDate();
	                }

	                int capacity = ((ResourceTO)handler).getCapacityPerDay(cursor).intValue();
	                if (capacity==0) {
	                	capacity = ResourceTO.DEFAULT_FULLDAY_CAPACITY;
	                }

	                int daysAhead = rtto.getEstimatedTime().intValue() / capacity;
	                Timestamp endPeriod = DateUtil.getChangedDate(rtto.getStartDate(), Calendar.DATE, daysAhead);
	                int minutes = rtto.getEstimatedTime().intValue() % ((ResourceTO)handler).getCapacityPerDay(cursor).intValue();
	                Timestamp dummy = DateUtil.getDate(endPeriod, true );
	                Timestamp rest = new Timestamp(dummy.getTime() + minutes * 60 * 1000);

	                response.append(this.formatRecord(rtto.getStartDate(), rest, tto.getCreationDate(), 
	                        tto.getDescription(), tto.getName(), "", "TSK-" + rtto.toString(), 
	                        handler.getLocale()));
	            }
	        }
	    }
	    
        return response;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.imp.ExportBUS#getFooter(com.pandora.ProjectTO, java.util.Vector)
     */
    public StringBuffer getFooter(ProjectTO pto, Vector fields) throws BusinessException {
        StringBuffer sb = new StringBuffer();
        sb.append("END:VCALENDAR");
        return sb;
    }
    
    
    private StringBuffer formatRecord(Timestamp iniDate, Timestamp endDate, Timestamp creationDate, 
            String description, String name, String location, String uid, Locale loc) {
        StringBuffer response = new StringBuffer();
        
        response.append("BEGIN:VEVENT\n");	                
        response.append("DTSTART;TZID=America/Sao_Paulo:" + DateUtil.getDate(iniDate, "yyyyMMdd", loc)+ "T080000\n");
        
        response.append("DTEND;TZID=America/Sao_Paulo:" + StringUtil.getGoogleDateFormat(endDate, loc, true) + "\n");
        response.append("DTSTAMP:" + StringUtil.getGoogleDateFormat(creationDate, loc, true) + "\n");
        response.append("UID:" + uid + "@plandora.org\n");
        response.append("CLASS:PRIVATE\n");
        response.append("CREATED:" + StringUtil.getGoogleDateFormat(creationDate, loc, true) + "\n");
        response.append("DESCRIPTION:" + description +"\n");
        response.append("LAST-MODIFIED:" + StringUtil.getGoogleDateFormat(DateUtil.getNow(), loc, true) + "\n");
        response.append("LOCATION:" + location + "\n");
        response.append("SEQUENCE:0\n");
        response.append("STATUS:CONFIRMED\n");
        response.append("SUMMARY:" + name +"\n");
        response.append("TRANSP:OPAQUE\n");
        response.append("END:VEVENT\n");
        
        return response;
    }
    
}
