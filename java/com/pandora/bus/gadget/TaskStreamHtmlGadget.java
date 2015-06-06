package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.TaskStatusTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;

public class TaskStreamHtmlGadget extends HtmlGadget {

	private static final String STREAM_HTML_PROJECT     = "PROJECT";
	
	private static final String STREAM_HTML_INTERVAL    = "INTERVAL";
	
	private static final String STREAM_HTML_MAX_ENTRIES = "MAX_ENTRIES";
	

	protected String generate(Vector selectedFields) throws BusinessException {
        ProjectDelegate pdel = new ProjectDelegate();
        StringBuffer response = new StringBuffer();
        try {
        	String strInterval = super.getSelected(STREAM_HTML_INTERVAL, selectedFields);
        	String strMaxEntries = super.getSelected(STREAM_HTML_MAX_ENTRIES, selectedFields);
        	int maxEntries = 20;
        	try {
        		maxEntries = Integer.parseInt(strMaxEntries);
        	}catch(Exception e) {
        		maxEntries = 20;
        	}
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(STREAM_HTML_PROJECT, selectedFields)), true);
        	if (pto!=null) {
                this.writeHeader(response, pto);
                this.writeBody(response, pto, strInterval, maxEntries);        		
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return response.toString();
	}



	public String getCategory() {
		return "label.manageOption.gadget.task";
	}

	
	public String getDescription() {
		return "label.manageOption.gadget.taskstream.desc";
	}

	
	public Vector getFields() {
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();

    	try {
    		Locale loc = super.handler.getLocale();

        	Vector<TransferObject> projList = getProjectFromUser(false);
        	response.add(new FieldValueTO(STREAM_HTML_PROJECT, "label.manageOption.gadget.taskstream.project", projList));

        	Vector<TransferObject> intervalList= new Vector<TransferObject>();
        	for (int j=1; j<=3; j++) {
        		intervalList.add(new TransferObject(j+"", super.getI18nMsg("label.manageOption.gadget.taskstream.interval." + j, loc)));	
        	}
        	response.add(new FieldValueTO(STREAM_HTML_INTERVAL, "label.manageOption.gadget.taskstream.interval", intervalList));
        	response.add(new FieldValueTO(STREAM_HTML_MAX_ENTRIES, "label.manageOption.gadget.taskstream.max", "20", 3, 7));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
	}

	@Override	
	public Vector<TransferObject> getFieldsId() {
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(STREAM_HTML_PROJECT, "-1"));
       	response.add(new TransferObject(STREAM_HTML_INTERVAL, "1"));
       	response.add(new TransferObject(STREAM_HTML_MAX_ENTRIES, "20"));
        return response;
	}

	
	public String getId() {
		return "TASK_STREAM_HTML";
	}

	public String getImgLogo() {
		return "../images/gdglogo-15.png";
	}
	
	
	public int getPropertyPanelHeight() {
		return 150;
	}

	
	public int getPropertyPanelWidth() {
		return 400;
	}

	
	public String getUniqueName() {
		return "label.manageOption.gadget.taskstream";
	}
	
	private void writeBody(StringBuffer response, ProjectTO pto, String strInterval, int maxEntries) {	
        DbQueryDelegate qdel = new DbQueryDelegate();
        try {
	    	int intervalHours = 24;
	    	if (strInterval.equals("2")) {
	    		intervalHours = 72;
	    	} else if (strInterval.equals("3")) {
	    		intervalHours = 168;
	    	}        
	        Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.HOUR, -intervalHours + 1);
	        int[] types = new int[] {Types.TIMESTAMP};
	        Vector params = new Vector();
	        params.addElement(iniRange);
	        
	        String sql = "select t.name, u.username, u.name as fullname, u.id, h.creation_date, " +
	        		            "h.comment, s.state_machine_order " +
	        		     "from task_history h , task t, tool_user u, task_status s " + 
	    			     "where h.creation_date > ? "+ 
	    			       "and t.id = h.task_id " +
	    			       "and u.id = h.resource_id " +
	    			       "and h.task_status_id = s.id " +
	    			       "and h.project_id in (" + super.getProjectIn( pto.getId() )+ ") " +
	    			     "order by h.creation_date desc";
	        Vector dbList = qdel.performQuery(sql, types, params);
			
	        if (dbList!=null) {
	    		for (int row = 1; row < dbList.size(); row++) {
	    			Vector item = (Vector)dbList.elementAt(row);
	    			String username = (String)item.elementAt(1);
	    			String fullname = (String)item.elementAt(2);
	    			String userId = (String)item.elementAt(3);
	    			
	    			String taskName = (String)item.elementAt(0);
	    			//String comment = (String)item.elementAt(5);
	    			Timestamp statusDt = (Timestamp)item.elementAt(4);
	    			
	    			Integer statusState = null;
	    			if ((String)item.elementAt(6)!=null) {
	    				statusState = new Integer((String)item.elementAt(6));	
	    			}
	    			
	    			response.append("<tr class=\"tableRowAction\">");
	    			//response.append("<tr class=\"formNotes\">");
					response.append("<td width=\"23\" valign=\"top\">");				
					response.append("<img width=\"23\" " + HtmlUtil.getHint(fullname) + 
										" height=\"30\" border=\"0\" src=\"../do/login?operation=getUserPic&id=" + userId + "&ts=" +DateUtil.getNow().toString() + "\">");
					response.append("</td>");
			
					response.append("<td>");				
					response.append(this.getHow(statusState) + "<spam class=\"formNotes\">" + taskName + "</spam><i>" + this.getWhen(statusDt)+"</i>");	
					response.append("</td>");
					
	    			response.append("</tr>");
	    			
	    			if (row >= maxEntries) {
	    				break;
	    			}
	    		}        	
	        }
			response.append("</table><br>");
		
        } catch (Exception e) {
        	e.printStackTrace();
        }		
	}


	private String getWhen(Timestamp statusDt) {
		String response = "";
		Locale loc = super.handler.getLocale();
		if (statusDt!=null) {
	        long diffLong = (DateUtil.getNow().getTime() - statusDt.getTime());
	        if (diffLong>0) {
	        	int diff = (int)(diffLong / 3600000);
				response = " " + super.getI18nMsg("label.manageOption.gadget.taskstream.since", loc);
				if (diff/24 >= 1) {
					response = response + " " + (diff/24) + " " + super.getI18nMsg("label.manageOption.gadget.taskstream.days", loc);
				} else {
					response = response + " " + diff + " " + super.getI18nMsg("label.manageOption.gadget.taskstream.hours", loc);	
				}
			}
		}
		return response;
	}



	private String getHow(Integer statusState) {
		Locale loc = super.handler.getLocale();
		String response = "";
		if (statusState.equals(TaskStatusTO.STATE_MACHINE_OPEN)) {
			response = " " + super.getI18nMsg("label.manageOption.gadget.taskstream.open", loc) + " ";
		} else if (statusState.equals(TaskStatusTO.STATE_MACHINE_CANCEL)) {
			response = " " + super.getI18nMsg("label.manageOption.gadget.taskstream.cancel", loc) + " ";
		} else if (statusState.equals(TaskStatusTO.STATE_MACHINE_PROGRESS)) {
			response = " " + super.getI18nMsg("label.manageOption.gadget.taskstream.progress", loc) + " ";
		} else if (statusState.equals(TaskStatusTO.STATE_MACHINE_CLOSE)) {
			response = " " + super.getI18nMsg("label.manageOption.gadget.taskstream.close", loc) + " ";
		} else if (statusState.equals(TaskStatusTO.STATE_MACHINE_HOLD)) {
			response = " " + super.getI18nMsg("label.manageOption.gadget.taskstream.hold", loc) + " ";
		} else if (statusState.equals(TaskStatusTO.STATE_MACHINE_REOPEN)) {
			response = " " + super.getI18nMsg("label.manageOption.gadget.taskstream.reopen", loc) + " ";
		}
		return response;
	}



	private void writeHeader(StringBuffer response, ProjectTO pto) {
		String label = pto.getName();
		response.append("<table class=\"table\" width=\"100%\" height=\"90%\" border=\"0\" cellspacing=\"1\" cellpadding=\"0\">");
		response.append("<tr class=\"formBody\">");
		response.append("<td colspan=\"7\" height=\"20\" ><center>" + label + "</center></td>");
		response.append("</tr>");
	}
}
