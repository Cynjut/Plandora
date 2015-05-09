package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.TaskStatusTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class KanbanLimitWIPChartGadget extends ChartGadget {

	private static final String KANBAN_WIP_PROJECT  = "PROJECT";
	
	private static final String KANBAN_WIP_RESOURCE = "RESOURCE";
	
	private static final String KANBAN_WIP_INTERVAL = "INTERVAL";
	
	private HashMap<String, Integer> granularity = new HashMap<String, Integer>();

	
	
	public String getUniqueName(){
		return "label.manageOption.gadget.kanbanwip";
	}
	
	public String getId(){
		return "KANBAN_WIP_CHART";
	}

	@Override
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(KANBAN_WIP_PROJECT, "-1"));
       	response.add(new TransferObject(KANBAN_WIP_RESOURCE, "-1"));
       	response.add(new TransferObject(KANBAN_WIP_INTERVAL, "3"));
        return response;
    }
	
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 160;
    }
        
	public String getCategory() {
		return "label.manageOption.gadget.agile";
	}

	public String getDescription() {
		return "label.manageOption.gadget.kanbanwip.desc";
	}

	public String getImgLogo() {
		return "../images/gdglogo-16.png";
	}

	@Override
	public Vector getFields(){
		return getFields(null);
    }
	
	
	@Override
	public boolean canReloadFields() {
		return true;
	}
	

	@Override
	public Vector<FieldValueTO> getFields(Vector<TransferObject> currentValues) {
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();

    	try {
        	Vector<TransferObject> projList = super.getProjectFromUser(false);
        	response.add(new FieldValueTO(KANBAN_WIP_PROJECT, "label.manageOption.gadget.kanbanwip.project", projList));
    		String projId = super.getSelected(KANBAN_WIP_PROJECT, currentValues);

    		Vector<TransferObject> userlist = new Vector<TransferObject>();
        	if (projId!=null && !projId.equals("")) {
        		userlist = super.getAtiveUsersByProject(projId);	
        	}
        	userlist.add((userlist.size()>0?1:0), new TransferObject("-2", super.getI18nMsg("label.all", handler.getLocale())));
        	response.add(new FieldValueTO(KANBAN_WIP_RESOURCE, "label.manageOption.gadget.kanbanwip.resource", userlist));        		
        	
        	Vector<TransferObject> intervList = new Vector<TransferObject>();
            intervList.add(new TransferObject("3", "label.manageOption.gadget.kanbanwip.interval.3"));
        	intervList.add(new TransferObject("2", "label.manageOption.gadget.kanbanwip.interval.2"));
            intervList.add(new TransferObject("1", "label.manageOption.gadget.kanbanwip.interval.1"));
        	response.add(new FieldValueTO(KANBAN_WIP_INTERVAL, "label.manageOption.gadget.kanbanwip.interval", intervList));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
	}
	
	@Override
	public String generate(Vector selectedFields)  throws BusinessException {
        ProjectDelegate pdel = new ProjectDelegate();
        String response = "";
        
        try {
        	String intervType = super.getSelected(KANBAN_WIP_INTERVAL, selectedFields);
        	String resourceId = super.getSelected(KANBAN_WIP_RESOURCE, selectedFields);
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(KANBAN_WIP_PROJECT, selectedFields)), true);
        	
        	String leg1   = super.getI18nMsg("label.manageOption.gadget.kanbanwip.leg.1", super.handler.getLocale());
        	String leg2   = super.getI18nMsg("label.manageOption.gadget.kanbanwip.leg.2", super.handler.getLocale());
        	String leg3   = super.getI18nMsg("label.manageOption.gadget.kanbanwip.leg.3", super.handler.getLocale());
        	String legLbl = super.getI18nMsg("label.manageOption.gadget.kanbanwip.leg.lbl", super.handler.getLocale());
        	
    		int intervalDays = 180;
    		int slotNumber = 26;
    		int step = 7;
    		String mask = "w-yy";
        	if (intervType.equals("2")) {
        		intervalDays = 30;
        		slotNumber = 30;
        		step = 1;
        		mask = "yyyyMMdd";
        	} else if (intervType.equals("1")) {
            	slotNumber = 13;
            	intervalDays = 365;
            	step = 1;
            	mask = "MMM-yyyy";        	
        	}

            Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -intervalDays + 1);
        	if (intervType.equals("3")) {
        		for (int w=0; w<=7; w++) {
        			Timestamp newdt = DateUtil.getChangedDate(iniRange, Calendar.DATE, w);
        			if (DateUtil.get(newdt, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
        				iniRange = newdt;
        				break;
        			}
        		}
        	} else if (intervType.equals("1")) {
        		iniRange = DateUtil.getDateTime("01", DateUtil.get(iniRange, Calendar.MONTH)+"", DateUtil.get(iniRange, Calendar.YEAR)+"", "00", "00", "00");
        	}
        	
    		float[][] vals = new float[3][slotNumber]; 
            String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange);
            
            if (pto!=null && resourceId!=null && !resourceId.trim().equals("") && !resourceId.trim().equals("-1") &&   
            		intervType!=null && !intervType.trim().equals("")) {
            	
            	this.defineGranulary(intervType, slotNumber, iniRange, mask);
            	
            	HashMap[] hmOpen  = new HashMap[slotNumber];
            	HashMap[] hmWIP   = new HashMap[slotNumber];
            	HashMap[] hmClose = new HashMap[slotNumber];

                Vector<Object> params = new Vector<Object>();               
            	int[] types = null;
            	if (!resourceId.equals("-2")) {
            		types = new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP,
            				           Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP};
            		params.add(resourceId); params.add(resourceId); params.add(resourceId);params.addElement(iniRange);
            		params.add(resourceId); params.add(resourceId); params.add(resourceId);params.addElement(iniRange);
            	} else {
            		types = new int[] {Types.TIMESTAMP, Types.TIMESTAMP};
            		params.addElement(iniRange);
            		params.addElement(iniRange);
            	}
                
                String projectList = super.getProjectIn( pto.getId() );
                DbQueryDelegate qdel = new DbQueryDelegate();
                
                String sql = "" +
                	"select o.task_id, o.dtRefOpen, p.dtRefProg, d.dtRefDone from ( " +
                	    "select h.task_id, min(h.creation_date) as dtRefOpen, null as dtRefProg, null as dtRefDone, h.project_id " +
                	    "from task_history h, task_status s " +
                	    "where h.task_status_id = s.id and s.state_machine_order=" + TaskStatusTO.STATE_MACHINE_OPEN + " " +
               		      "and project_id in (" + projectList + ") " + (!resourceId.equals("-2")? "and resource_id=? " : "") +                	    
                	    "group by h.project_id, h.task_id " +
                	") as o left outer join ( " +
                	    "select h.task_id, null as dtRefOpen, min(h.creation_date) as dtRefProg, null as dtRefDone, h.project_id " +
                	    "from task_history h, task_status s  " +
               		    "where h.task_status_id = s.id and s.state_machine_order not in (" + TaskStatusTO.STATE_MACHINE_OPEN + ", " + TaskStatusTO.STATE_MACHINE_CLOSE + "," + TaskStatusTO.STATE_MACHINE_CANCEL + ") " +
               		      "and project_id in (" + projectList + ") " + (!resourceId.equals("-2")? "and resource_id=? " : "") +                	    
                	    "group by h.project_id, h.task_id " +
                	") as p on (o.task_id = p.task_id) left outer join ( " +
                	    "select h.task_id, null as dtRefOpen, null as dtRefProg, max(h.creation_date) as dtRefDone, h.project_id " +
                	    "from task_history h, task_status s " +
 				        "where h.task_status_id = s.id and s.state_machine_order in (" + TaskStatusTO.STATE_MACHINE_CLOSE + "," + TaskStatusTO.STATE_MACHINE_CANCEL + ") " +
               		      "and project_id in (" + projectList + ") " + (!resourceId.equals("-2")? "and resource_id=? " : "") +
					      "and h.creation_date > ? " +               		      
                	    "group by h.project_id, h.task_id " +
                	") as d on (d.task_id = p.task_id) " +
                	"union " +
                	"select d.task_id, o.dtRefOpen, p.dtRefProg, d.dtRefDone from ( " +
                	    "select h.task_id, null as dtRefOpen, null as dtRefProg, max(h.creation_date) as dtRefDone, h.project_id " +
                	    "from task_history h, task_status s " +
			            "where h.task_status_id = s.id and s.state_machine_order in (" + TaskStatusTO.STATE_MACHINE_CLOSE + "," + TaskStatusTO.STATE_MACHINE_CANCEL + ") " +
               		      "and project_id in (" + projectList + ") " + (!resourceId.equals("-2")? "and resource_id=? " : "") +
					      "and h.creation_date > ? " +
                	    "group by h.project_id, h.task_id " +
                	") as d left outer join ( " +
                	    "select h.task_id, null as dtRefOpen, min(h.creation_date) as dtRefProg, null as dtRefDone, h.project_id " +
                	    "from task_history h, task_status s  " +
               		    "where h.task_status_id = s.id and s.state_machine_order not in (" + TaskStatusTO.STATE_MACHINE_OPEN + ", " + TaskStatusTO.STATE_MACHINE_CLOSE + "," + TaskStatusTO.STATE_MACHINE_CANCEL + ") " +
               		      "and project_id in (" + projectList + ") " + (!resourceId.equals("-2")? "and resource_id=? " : "") +
                	    "group by h.project_id, h.task_id " +
                	") as p on (d.task_id = p.task_id) left outer join ( " +
                	    "select h.task_id, min(h.creation_date) as dtRefOpen, null as dtRefProg, null as dtRefDone, h.project_id " +
                	    "from task_history h, task_status s " +
                	    "where h.task_status_id = s.id and s.state_machine_order=" + TaskStatusTO.STATE_MACHINE_OPEN + " " +
               		      "and project_id in (" + projectList + ") " + (!resourceId.equals("-2")? "and resource_id=? " : "") +
                	    "group by h.project_id, h.task_id " +
                	") as o on (o.task_id = p.task_id) " +
                	"where o.dtRefOpen is null";                
                
                
                Vector<Vector<Object>> dbList = qdel.performQuery(sql, types, params);
                if (dbList!=null) {                	
                	
                	Timestamp cursor = iniRange;
        			SimpleDateFormat df = new SimpleDateFormat(mask, this.handler.getLocale());
        			
                	while(!cursor.after(DateUtil.getNowWithoutTime())) {
                    	Timestamp nextCursor = DateUtil.getChangedDate(cursor, Calendar.DATE, step);
                    	
            			Integer slot = (Integer)this.granularity.get(df.format(cursor));
            			if (slot!=null) {
            				int index = slot.intValue();
            				if (index<vals[0].length) {

                            	for (int i=1; i<dbList.size(); i++) {
                            		Vector<Object> item = (Vector<Object>)dbList.elementAt(i);
                                	String taskId = (String)item.elementAt(0);
                                	Timestamp dtRefOpen  = DateUtil.getDate((Timestamp)item.elementAt(1), true);
                                	Timestamp dtRefWIP   = DateUtil.getDate((Timestamp)item.elementAt(2), true);
                                	Timestamp dtRefClose = DateUtil.getDate((Timestamp)item.elementAt(3), true);
                                	
                                	Timestamp innerCursor = cursor;
                                	while(innerCursor.before(nextCursor) ) {

                                		if (hmOpen[index]==null) hmOpen[index] = new HashMap();
                                		if (hmWIP[index]==null) hmWIP[index] = new HashMap();
                                		if (hmClose[index]==null) hmClose[index] = new HashMap();
                                		
                                		if (dtRefClose!=null && !innerCursor.before(dtRefClose)) {
                                			hmOpen[index].put(taskId + "|" + index, taskId + "|" + index);
                                			hmWIP[index].put(taskId + "|" + index, taskId + "|" + index);
                                			hmClose[index].put(taskId + "|" + index, taskId + "|" + index);
                                		} else if ((dtRefClose!=null && dtRefWIP!=null && !innerCursor.before(dtRefWIP) && !innerCursor.after(dtRefClose)) ||
                                				   (dtRefClose==null && dtRefWIP!=null && !innerCursor.before(dtRefWIP))) {
                                			hmOpen[index].put(taskId + "|" + index, taskId + "|" + index);
                                			hmWIP[index].put(taskId + "|" + index, taskId + "|" + index);
                                		} else if ((dtRefWIP!=null && dtRefOpen!=null && !innerCursor.before(dtRefOpen) && !innerCursor.after(dtRefWIP)) ||
                                				   (dtRefWIP==null && dtRefOpen!=null && !innerCursor.before(dtRefOpen))) {
                                			hmOpen[index].put(taskId + "|" + index, taskId + "|" + index);
                                		}
                                		
                                		innerCursor = DateUtil.getChangedDate(innerCursor, Calendar.DATE, 1);
                                	}
                            	}
            				}
            			}
                		
            			cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, step);
                	}
                }
                
                String[] labels = new String[] {leg3, leg2, leg1};
                for (int v=0; v<vals[0].length; v++){
                	vals[0][v] = (hmClose[v]!=null ? hmClose[v].values().size() : 0);
                	vals[1][v] =   (hmWIP[v]!=null ? hmWIP[v].values().size()   : 0);
                	vals[2][v] =  (hmOpen[v]!=null ? hmOpen[v].values().size()  : 0);                	
                }            	
                
        		//draw the bars
                response = "{ \n" + 
                	getJSonTitle() + "," +
                	getJSonYLegend(legLbl) + "," + 
                	getAreaChartValues(vals, labels, null, null, new String[]{"008000", "FFFF00", "800000"}, true) + "," +
                	getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, vals, null, "y_axis", false) + "}";
            } else {

            	//empty chart...
                response = "{ \n" + 
                	getJSonTitle() + "," +
                	getJSonAxis(xaxis, null, "x_axis") + "," +
                	getJSonYLegend(legLbl) + "}";
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return response;
	}
	
	private void defineGranulary(String intervType, int slotNumber, Timestamp iniRange, String mask) {
		Timestamp cursor = null;
		DateFormat df = null;
		for (int i=0; i <slotNumber; i++) {
			if (intervType.equals("1")) {
	    		cursor = DateUtil.getChangedDate(iniRange, Calendar.MONTH, i);				
			} else if (intervType.equals("2")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.DATE, i);
	    	} else if (intervType.equals("3")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.WEEK_OF_YEAR, i);
	    	}
			df = new SimpleDateFormat(mask, this.handler.getLocale());
			this.granularity.put(df.format(cursor), new Integer(i));			
		}		
	}

	
	private String[] getXAxisLabel(String intervType, int slotNumber, Timestamp iniRange){
		String xaxis[] = new String[slotNumber];
		
        if (intervType.equals("1")) {
            for (int i=0; i<slotNumber;i++) {
            	DateFormat df = new SimpleDateFormat("MMM-yyyy", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.MONTH, i));
            	xaxis[i] = slotLabel;
            }        		

        } else if (intervType.equals("2")) {
            for (int i=0; i<slotNumber;i++) {
            	DateFormat df = new SimpleDateFormat("dd-MMM", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.DATE, i));
            	xaxis[i] = slotLabel;
            }        		
        	
        } else if (intervType.equals("3")) {
            for (int i=0; i<slotNumber;i++) {
            	DateFormat df = new SimpleDateFormat("w-yy", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.WEEK_OF_YEAR, i));
            	xaxis[i] = slotLabel;
            }        		        	
        }   
        return xaxis;
	}
}
