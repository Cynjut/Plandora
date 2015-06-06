package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.CalendarSyncInterface;
import com.pandora.bus.occurrence.Occurrence;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class RemainingDaysHtmlGadget extends HtmlGadget {

	private static final String REMDAYS_HTML_PROJECT = "PROJECT";
	
	private static final String REMDAYS_HTML_OCC_ID  = "OCCURRENCE_ID";

	
	protected String generate(Vector selectedFields) throws BusinessException {
        ProjectDelegate pdel = new ProjectDelegate();
        OccurrenceDelegate odel = new OccurrenceDelegate();
        StringBuffer response = new StringBuffer();
        try {
        	
        	//default value
    		response.append("<table class=\"table\" width=\"100%\" height=\"90%\" border=\"1\" bordercolor=\"#10389C\" cellspacing=\"1\" cellpadding=\"0\">");
    		response.append("<tr class=\"formBody\">");
    		response.append("<td>&nbsp;</td>");
    		response.append("</tr></table>");
        	
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(REMDAYS_HTML_PROJECT, selectedFields)), true);            
            if (pto!=null) {
            	String occStrId = super.getSelected(REMDAYS_HTML_OCC_ID, selectedFields);
            	if (occStrId!=null && !occStrId.trim().equals("") && !occStrId.trim().equals("-1")) {

            		OccurrenceTO occ = odel.getOccurrenceObject(new OccurrenceTO(occStrId));
            		if (occ!=null && occ.getVisible()) {
            			
            			Occurrence bus = this.getCalendarClass(occ.getSource());
            			if (bus!=null) {

                			Timestamp occDate = occ.getFieldDateByKey(((CalendarSyncInterface)bus).getDateFieldId(), bus);
                			if (occDate!=null) { 
                    			response = new StringBuffer();

                        		String header = super.getI18nMsg("label.manageOption.gadget.remdays.header", handler.getLocale());
                        		String toLbl = super.getI18nMsg("label.manageOption.gadget.remdays.toLbl", handler.getLocale());
                    			
                        		response.append("<table class=\"table\" width=\"100%\" height=\"90%\" border=\"1\" bordercolor=\"#10389C\" cellspacing=\"1\" cellpadding=\"0\">");
                        		response.append("<tr>");
                        		response.append("<td class=\"tableCell\"><center>" + header + "</center></td>");
                        		response.append("</tr>");
                    			
                        		int days = DateUtil.getSlotBetweenDates(DateUtil.getDate(DateUtil.getNow(), true), occDate);
                        		response.append("<tr>");            			
                    			response.append("<td class=\"tableCell\"><b><center><font size=\"16\">" + days + "</font></center></b></td>");
                    			response.append("</tr>");

                        		response.append("<tr>");
                        		response.append("<td class=\"tableCell\"><center>" + toLbl + " <b>" + occ.getName() + "</b></center></td>");
                        		response.append("</tr>");
                			}
            			}
            		}
            	}
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return response.toString();
	}



	public String getCategory() {
		return "label.manageOption.gadget.management";
	}

	
	public String getDescription() {
		return "label.manageOption.gadget.remdays.desc";
	}

	@Override
    public Vector getFields(){
		return getFields(null);
    }
	
	@Override
	public Vector<FieldValueTO> getFields(Vector<TransferObject> currentValues) {
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();
    	DbQueryDelegate db = new DbQueryDelegate();
    	OccurrenceDelegate odel = new OccurrenceDelegate();
    	Vector<TransferObject> projList = null;
    	HashMap<String, TransferObject> hmProjList = new HashMap<String, TransferObject>();
    	
    	try {
			String sql = "select distinct o.id, o.source, p.id as PROJECT_ID, p.name " +
			               "from project p, resource r, occurrence o, planning po " +
			              "where p.id = r.project_id and o.project_id = p.id and po.id = o.id " +
			                "and r.id = ? and p.id <> '0' and po.visible='1' and o.source is not null order by name";
			int[] tp = {Types.VARCHAR};
			Vector<Object> pm = new Vector<Object>();
			pm.add(this.handler.getId()); 
    		Vector<Vector<Object>> list = db.performQuery(sql, tp, pm);
    		if (list!=null && list.size()>1) {
    			for (Vector<Object> line : list) {
    				OccurrenceTO o = new OccurrenceTO((String)line.get(0));
    				Occurrence bus = this.getCalendarClass((String)line.get(1));
    				if (bus!=null) {
    					o = odel.getOccurrenceObject(o);
    					if (o!=null) {
                   			Timestamp dt = o.getFieldDateByKey(((CalendarSyncInterface)bus).getDateFieldId(), bus);
                   			if (dt!=null && dt.after(DateUtil.getNow()) && o.getVisible()) {
                				TransferObject to = new TransferObject((String)line.get(2), (String)line.get(3));
                				if (projList==null) {
                	    			projList = new Vector<TransferObject>();
                	    			projList.addElement(new TransferObject("-1", super.getI18nMsg("label.combo.select")));
                				}
                				if (hmProjList.get(to.getId())==null) {
                    				projList.add(to);
                    				hmProjList.put(to.getId(), to);        						
                				}   						
                   			}
    					}
           			}
				}
    		}    		

        	response.add(new FieldValueTO(REMDAYS_HTML_PROJECT, "label.manageOption.gadget.remdays.project", projList));
    		String projId = super.getSelected(REMDAYS_HTML_PROJECT, currentValues);

        	Vector<TransferObject> occList = new Vector<TransferObject>();
        	occList.addElement(new TransferObject("-1", super.getI18nMsg("label.combo.select")));
        	
        	if (projId!=null && !projId.equals("") && !projId.equals("-1")) {
        		TransferObject pto = hmProjList.get(projId);
       			if (pto!=null) {
       				Vector<OccurrenceTO> oList = odel.getOccurenceList(projId, true);
            		for (OccurrenceTO o : oList) {
            			Occurrence bus = this.getCalendarClass(o.getSource());
            			if (bus!=null) {
                			o = odel.getOccurrenceObject(o);            				
                			Timestamp dt = o.getFieldDateByKey(((CalendarSyncInterface)bus).getDateFieldId(), bus);
                			if (dt!=null && dt.after(DateUtil.getNow()) && o.getVisible()) {
                				occList.addElement(new TransferObject(o.getId(), o.getName()));	
                			}
            			}
					}
            	}
        	}
        	response.add(new FieldValueTO(REMDAYS_HTML_OCC_ID, "label.manageOption.gadget.remdays.occurrence", occList));
    		
    		
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
	}

	
	@Override	
	public Vector<TransferObject> getFieldsId() {
		Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(REMDAYS_HTML_PROJECT, "-1"));
       	response.add(new TransferObject(REMDAYS_HTML_OCC_ID, "-1"));
        return response;
	}

	
	public String getId() {
		return "REMAINING_DAYS_HTML";
	}

	public String getImgLogo() {
		return "../images/gdglogo-19.png";
	}
	
	
	public int getPropertyPanelHeight() {
		return 150;
	}

	
	public int getPropertyPanelWidth() {
		return 400;
	}


	@Override
	public String getUniqueName() {
		return "label.manageOption.gadget.remdays";
	}
	
	
	@Override
	public boolean canReloadFields() {
		return true;
	}	
	
	
	private Occurrence getCalendarClass(String source) {
		Occurrence response = null;
		try {

			UserDelegate udel = new UserDelegate();
			UserTO root = udel.getRoot();
			String allClasses = root.getPreference().getPreference(PreferenceTO.CALEND_SYNC_BUS_CLASS);
			
			Object o = OccurrenceTO.getClass(allClasses, source);
			if (o!=null && o instanceof CalendarSyncInterface) {
				response = (Occurrence)o;
			}

		} catch(Exception e) {
			response = null;
			e.printStackTrace();
		}
		return response;
	}
}
