package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DBUtil;
import com.pandora.helper.DateUtil;

public final class ResourceProjectCapacityChartGadget extends ChartGadget {

	private static final String RES_PRJ_CAP_RESOURCE = "RESOURCE";

	private static final String RES_PRJ_CAP_INTERVAL = "INTERVAL";

	private HashMap<String,Integer> granularity = new HashMap<String,Integer>();

	
	public String getUniqueName(){
		return "label.manageOption.gadget.resprjcap";
	}
	
	public String getId(){
		return "RESOURCE_PRJ_CAP_CHART";
	}

	public String getCategory() {
		return "label.manageOption.gadget.capacity";
	}

	public String getImgLogo() {
		return "../images/gdglogo-4.png";
	}
	
	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(RES_PRJ_CAP_RESOURCE, "-1"));
       	response.add(new TransferObject(RES_PRJ_CAP_INTERVAL, "1"));
        return response;
    }
	
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 140;
    }
    
	public int getHeight(){
		return 190;
	}
    
    public Vector getFields(){
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();

    	try {
        	Vector<TransferObject> userlist = super.getAtiveUsers(super.handler.getId());
        	response.add(new FieldValueTO(RES_PRJ_CAP_RESOURCE, "label.manageOption.gadget.resprjcap.resource", userlist));
        	
            Vector<TransferObject> intervList = new Vector<TransferObject>();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.resprjcap.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.resprjcap.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.resprjcap.interval.3"));
            intervList.add(new TransferObject("4", "label.manageOption.gadget.resprjcap.interval.4"));
        	response.add(new FieldValueTO(RES_PRJ_CAP_INTERVAL, "label.manageOption.gadget.resprjcap.interval", intervList));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}	
        return response;
    }
	
    
	public String generate(Vector selectedFields)  throws BusinessException {
        DbQueryDelegate qdel = new DbQueryDelegate();            			
        String response = "";
        HashMap<String, String> projectList = new HashMap<String, String>();
        Vector<String> projectNames = new Vector<String>();
        
        try {
        	String resourceId = super.getSelected(RES_PRJ_CAP_RESOURCE, selectedFields);
        	String intervType = super.getSelected(RES_PRJ_CAP_INTERVAL, selectedFields);

        	String mask = "yyyyMMdd";
            Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -6);            
        	int slotNumber = 7;
        	if (intervType.equals("2")) {
        		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -29);
        		slotNumber = 30;
        		mask = "yyyyMMdd";
        	} else if (intervType.equals("3")) {
        		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -179);
        		for (int w=0; w<=7; w++) {
        			iniRange = DateUtil.getChangedDate(iniRange, Calendar.DATE, w);
        			if (DateUtil.get(iniRange, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
        				break;
        			}
        		}
        		slotNumber = 26;
        		mask = "w-yy";
        	} else if (intervType.equals("4")) {
        		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -364);
        		iniRange = DateUtil.getDateTime("01", DateUtil.get(iniRange, Calendar.MONTH)+"", 
        				DateUtil.get(iniRange, Calendar.YEAR)+"", "00", "00", "00");
        		slotNumber = 13;
        		mask = "MMM-yyyy";
        	}
        	this.defineGranulary(intervType, slotNumber, iniRange, mask);
            String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange);
        	
        	String[] labels = new String[100];
            float[][] valBar = new float[slotNumber][100];
        	
    		//get data related to allocation from data base...
            int[] types = new int[] {Types.VARCHAR, Types.TIMESTAMP};
            Vector<Object> params = new Vector<Object>();
            params.addElement(resourceId);
            params.addElement(iniRange);
            String sqlData = "";
            String dbname = qdel.getDBProductName();
            
            sqlData = "select sub.name, sum(sub.alloc_time) as alloc_time, sub.bucket_date from ( ";
            sqlData = sqlData + "select a.alloc_time, p.name, p.id, ";
            sqlData = sqlData + DBUtil.addDate(dbname, "rt.actual_date", "a.sequence-1") + " as bucket_date ";
            
            //if (dbname.equalsIgnoreCase("MySQL")) {
            //	sqlData = sqlData + "ADDDATE(rt.actual_date, a.sequence-1) as bucket_date ";
    		//} else {
    		//	sqlData = sqlData + "rt.actual_date+ cast((a.sequence-1) || ' day' as interval) as bucket_date ";
    		//}
            
            sqlData = sqlData + "from resource_task_alloc a, task t, resource_task rt, project p " +
		    	        "where a.task_id = t.id  " +
		    	        "and a.task_id = rt.task_id " + 
		    	        "and a.resource_id = rt.resource_id " + 
		    	        "and a.project_id = rt.project_id  " +
		    	        "and rt.actual_date is not null " +
		    	        "and a.project_id = p.id " +
		    	        "and a.alloc_time > 0 and a.resource_id=? " +
    			      ") as sub  " +
    			      "where sub.bucket_date >= ? " +  
            		  "group by sub.name, sub.bucket_date " +
	        		  "order by sub.bucket_date ";                        

            Vector<Vector<Object>> dbAllocList = qdel.performQuery(sqlData, types, params);
            if (dbAllocList!=null) {
            	
            	for (int i=1; i<dbAllocList.size(); i++) {
            		Vector<Object> item = (Vector<Object>)dbAllocList.elementAt(i);
            		
            		String projectName = (String)item.elementAt(0);
            		String allocTimeStr = (String)item.elementAt(1);
            		Timestamp tm = (Timestamp)item.elementAt(2);
                	                	
        			SimpleDateFormat df = new SimpleDateFormat(mask, this.handler.getLocale());			
        			Integer slot = (Integer)this.granularity.get(df.format(tm));
        			if (slot!=null) {
        				
                    	if (projectList.get(projectName)==null) {
                    		String hashPos = projectList.size() + "";
                        	projectList.put(projectName, hashPos);
                        	projectNames.addElement(projectName);
                    	}
        				
        				int index = slot.intValue();                	
	                	try {
	                		if (index>=valBar.length) {
	                			index = valBar.length-1;
	                		}
	                		String pos = (String)projectList.get(projectName);
	                   		valBar[index][Integer.parseInt(pos)] += (Float.parseFloat(allocTimeStr)) / 60;
	                	} catch(ArrayIndexOutOfBoundsException e) {
	                		System.out.println("index:" + index + " slot: " + slot + " prj:" + item.elementAt(0) + " dt:" + tm + " gran.list:" + this.granularity);
	                	}
        			}
            	}

            	for (int i=0; i<projectNames.size(); i++) {
            		String projectName = (String)projectNames.elementAt(i);
            		labels[i] = projectName;
            	}
                        	
        		//draw the chart
                response = "{ \n" + 
                		getJSonTitle() + "," +
                		getJSonYLegend("(h)") + "," + 
                		getBarStackValues(valBar, labels) + "," +
                		getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, valBar, "y_axis") + "}";                	
            }                    
 
        	//empty chart...            
            if (response.equals("")) {
                response = "{ \n" + getJSonTitle() + "}";            	
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
	
	
	private void defineGranulary(String intervType, int slotNumber, Timestamp iniRange, String mask) {
		Timestamp cursor = null;
		DateFormat df = null;
		for (int i=0; i <slotNumber; i++) {
			if (intervType.equals("") || intervType.equals("1")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.DATE, i);
			} else if (intervType.equals("2")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.DATE, i);
	    	} else if (intervType.equals("3")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.WEEK_OF_YEAR, i);
	    	} else if (intervType.equals("4")) {
	    		cursor = DateUtil.getChangedDate(iniRange, Calendar.MONTH, i);							
	    	}
			df = new SimpleDateFormat(mask, this.handler.getLocale());
			this.granularity.put(df.format(cursor), new Integer(i));			
		}		
	}
	    
}
