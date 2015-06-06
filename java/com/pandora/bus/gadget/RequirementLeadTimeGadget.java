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
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class RequirementLeadTimeGadget extends ChartGadget {
	
	private static final String REQ_LEADTIME_PROJECT = "PROJECT";
	
	private static final String REQ_LEADTIME_INTEVAL = "INTERVAL";

	private HashMap granularity = new HashMap();
	
	
	public String getUniqueName(){
		return "label.manageOption.gadget.reqleadtime";
	}
	
	public String getId(){
		return "REQ_LEAD_TIME_CHART";
	}
	
	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(REQ_LEADTIME_PROJECT, "-1"));
       	response.add(new TransferObject(REQ_LEADTIME_INTEVAL, "1"));
        return response;
    }
	
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 140;
    }
        
	public String getCategory() {
		return "label.manageOption.gadget.management";
	}
	
	public String getDescription() {
		return "label.manageOption.gadget.reqleadtime.desc";
	}

	public String getImgLogo() {
		return "../images/gdglogo-12.png";
	}
	
	@SuppressWarnings("unchecked")
	public Vector getFields(){
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();

    	try {
        	Vector<TransferObject> projList = super.getProjectFromUser(false);
        	response.add(new FieldValueTO(REQ_LEADTIME_PROJECT, "label.manageOption.gadget.reqleadtime.project", projList));

            Vector<TransferObject> intervList = new Vector<TransferObject>();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.reqleadtime.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.reqleadtime.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.reqleadtime.interval.3"));
            intervList.add(new TransferObject("4", "label.manageOption.gadget.reqleadtime.interval.4"));
        	response.add(new FieldValueTO(REQ_LEADTIME_INTEVAL, "label.manageOption.gadget.reqleadtime.interval", intervList));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
    }


	public String generate(Vector selectedFields)  throws BusinessException {
        ProjectDelegate pdel = new ProjectDelegate();
        String response = "";
    
        try {
        	String intervType = super.getSelected(REQ_LEADTIME_INTEVAL, selectedFields);
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(REQ_LEADTIME_PROJECT, selectedFields)), true);
        	
        	int slotNumber = 7;
        	int intervalDays = 7;
        	int interv = Calendar.DATE;
        	if (intervType.equals("2")) {
        		intervalDays = 30;
        		slotNumber = 30;
        	} else if (intervType.equals("3")) {
        		intervalDays = 180;
        		slotNumber = 26; 
        		interv = Calendar.WEEK_OF_YEAR;
        	} else if (intervType.equals("4")) {
        		intervalDays = 365;
        		slotNumber = 12;
        		interv = Calendar.MONTH;
        	}
            Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -intervalDays + 1);
                        
    		float[][] reqNumVals = new float[slotNumber][1]; 
            String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange);
            
            if (pto!=null) {
                int[] types = new int[] {Types.TIMESTAMP, Types.TIMESTAMP};
                Vector params = new Vector();
                params.addElement(iniRange);
                params.addElement(iniRange);
    
                DbQueryDelegate qdel = new DbQueryDelegate();
                String sql = "select p.creation_date, p.final_date, r.project_id " + 
            			       "from requirement r, planning p "+ 
            			      "where r.id = p.id and r.project_id in (" + super.getProjectIn( pto.getId() )+ ") " +
            			        "and (p.creation_date >= ? or p.final_date >= ?)";
                Vector dbList = qdel.performQuery(sql, types, params);
                if (dbList!=null) {                	
                	for (int i=0; i<slotNumber; i++) {

                		int totalHour = 0;
                		int numberOfvalues = 0;
                		Timestamp cursor0 = DateUtil.getChangedDate(iniRange, interv, i);
                    	Timestamp cursor1 = DateUtil.getChangedDate(cursor0, interv, 1);

                    	for (int j=1; j<dbList.size(); j++) {
                        	Vector item = (Vector)dbList.elementAt(j);
                        	
                        	Timestamp tmCreationDt = null;
                        	if (item.elementAt(0)!=null) {
                        		tmCreationDt = (Timestamp)item.elementAt(0);
                            	if ((tmCreationDt.equals(cursor0) || tmCreationDt.after(cursor0)) && 
                            			(tmCreationDt.equals(cursor1) || tmCreationDt.before(cursor1))) {
                            		
                                	Timestamp tmFinalDt = null;                        	                            		
                                	if (item.elementAt(1)!=null) {
                                		tmFinalDt = (Timestamp)item.elementAt(1);
                                	} else {
                                		tmFinalDt = cursor1;
                                	}
                                	
                                	if (tmCreationDt.before(tmFinalDt)) {
                                		long diff = (tmFinalDt.getTime() - tmCreationDt.getTime());
                                		totalHour+= (int)(diff / 3600000);
                                		numberOfvalues++;
                                	}
                            	}
                        	}
                    	}
                    	
                    	if (numberOfvalues>0) {
                    		reqNumVals[i][0] = (totalHour/numberOfvalues);	
                    	} else {
                    		reqNumVals[i][0] = 0;
                    	}
                		                    	
                	}                	
                }
                
        		//draw the bars
                response = "{ \n" + 
            		getJSonTitle(pto.getName()) + "," +
            		getJSonYLegend("(h)") + "," + 
            		getBarStackValues(reqNumVals, null) + "," +
            		getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, reqNumVals, "y_axis") + "}";
                
            } else {
            	
            	//empty chart...
                response = "{ \n" + 
                	getJSonTitle() + "," +
                	getJSonAxis(xaxis, null, "x_axis") + "," +
                	getJSonYLegend("(h)") + "}";
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return response;
	}
	
	
	private String[] getXAxisLabel(String intervType, int slotNumber, Timestamp iniRange){
		String xaxis[] = new String[slotNumber];
		
        if (intervType.equals("1")) {
            for (int i=0; i<slotNumber;i++) {
            	DateFormat df = new SimpleDateFormat("E", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.DATE, i));
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
        	
        } else if (intervType.equals("4")) {
            for (int i=0; i<slotNumber;i++) {
            	DateFormat df = new SimpleDateFormat("MMM-yyyy", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.MONTH, i));
            	xaxis[i] = slotLabel;
            }        		
        }
        
        return xaxis;
	}
	
}
