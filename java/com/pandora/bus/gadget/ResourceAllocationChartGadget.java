package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DBUtil;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

public final class ResourceAllocationChartGadget extends ChartGadget {

	private static final String RES_ALLOC_TASK_PROJECT = "PROJECT";
	
	private static final String RES_ALLOC_TASK_INTEVAL = "INTERVAL";

	private HashMap granularity = new HashMap();
	
	public String getUniqueName(){
		return "label.manageOption.gadget.resalloc";
	}
	
	public String getId(){
		return "RESOURCE_ALLOC_CHART";
	}

	public String getCategory() {
		return "label.manageOption.gadget.capacity";
	}
	
	public String getImgLogo() {
		return "../images/gdglogo-8.png";
	}	
	
	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(RES_ALLOC_TASK_PROJECT, "-1"));
       	response.add(new TransferObject(RES_ALLOC_TASK_INTEVAL, "1"));
        return response;
    }
	
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 140;
    }
        
    public Vector getFields(){
    	Vector response = new Vector();

    	try {
         	Vector buff = super.getProjectFromUser(true);
        	response.add(new FieldValueTO(RES_ALLOC_TASK_PROJECT, "label.manageOption.gadget.resalloc.project", buff));
        	
            Vector intervList = new Vector();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.resalloc.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.resalloc.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.resalloc.interval.3"));
        	response.add(new FieldValueTO(RES_ALLOC_TASK_INTEVAL, "label.manageOption.gadget.resalloc.interval", intervList));
        	
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
        	String intervType = super.getSelected(RES_ALLOC_TASK_INTEVAL, selectedFields);

        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(RES_ALLOC_TASK_PROJECT, selectedFields)), true);
            if (pto!=null) {

            	String mask = "yyyyMMdd";
                Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -6);            
            	int slotNumber = 7;
            	if (intervType.equals("2")) {
            		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -29);
            		slotNumber = 30;
            		mask = "yyyyMMdd";
            	} else if (intervType.equals("3")) {
            		slotNumber = 0;
            		iniRange = DateUtil.getDate("1", "1", "1970"); //magic initial Range ;-)
            	}
            	this.defineGranulary(intervType, slotNumber, iniRange, mask);
                String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange);
            	
                DbQueryDelegate qdel = new DbQueryDelegate();            	
            	HashMap resList = new HashMap();
            	
            	//get the users of project
                String sql = "select distinct r.id, u.username from resource as r, tool_user as u " +
                			 "where r.project_id in (" + super.getProjectIn( pto.getId() )+ ") " +
                			   "and r.id = u.id";
                Vector dbList = qdel.performQuery(sql, null, null);
                if (dbList!=null) {
                	String[] labels = new String[dbList.size()];
                	
                    for (int i=1; i<dbList.size(); i++) {
                    	Vector item = (Vector)dbList.elementAt(i);
                    	UserTO res = new UserTO((String)item.elementAt(0));
                    	res.setUsername((String)item.elementAt(1));
                    	res.setGenericTag((i-1)+"");
                    	resList.put(res.getId(), res);
                    	labels[i-1] = res.getUsername(); 
                    }
                    float[][] valBar = new float[slotNumber][dbList.size()];
                    float[] valPie = new float[dbList.size()];
                    String[] valPieLabel = new String[dbList.size()];

            		//get data related to allocation from data base...
                    int[] types = new int[] {Types.TIMESTAMP};
                    Vector params = new Vector();
                    params.addElement(iniRange);
                    String sqlData = "";
                    String dbname = qdel.getDBProductName();
                    
                    if (slotNumber>0) {
                        sqlData = "select sub.resource_id, sum(sub.alloc_time) as alloc_time, sub.bucket_date from ( ";
                    } else {
                    	sqlData = "select sub.resource_id, sum(sub.alloc_time) as alloc_time from ( ";
                    }
                    
                    sqlData = sqlData + "select a.alloc_time, rt.resource_id, ";
                    
                    sqlData = sqlData + DBUtil.addDate(dbname, "rt.actual_date", "a.sequence-1") + " as bucket_date ";
                    
                    //if (dbname.equalsIgnoreCase("MySQL")) {
                    //	sqlData = sqlData + "ADDDATE(rt.actual_date, a.sequence-1) as bucket_date ";
            		//} else {
            		//	sqlData = sqlData + "rt.actual_date+ cast((a.sequence-1) || ' day' as interval) as bucket_date ";
            		//}
                    
                    sqlData = sqlData + "from resource_task_alloc a, task t, resource_task rt " +
				    	        "where a.project_id in (" + super.getProjectIn( pto.getId() )+ ") " +
				    	        "and a.task_id = t.id  " +
				    	        "and a.task_id = rt.task_id " + 
				    	        "and a.resource_id = rt.resource_id " + 
				    	        "and a.project_id = rt.project_id  " +
				    	        "and rt.actual_date is not null " +
				    	        "and a.alloc_time > 0 " +
		    			      ") as sub  " +
		    			      "where sub.bucket_date >= ? "; 
                    if (slotNumber>0) {
                        sqlData = sqlData + "group by sub.resource_id, sub.bucket_date " +
			        						"order by sub.bucket_date ";                        
                    } else {
                        sqlData = sqlData + "group by sub.resource_id";
                    }
                    Vector dbAllocList = qdel.performQuery(sqlData, types, params);
                    if (dbAllocList!=null) {
                    	for (int i=1; i<dbAllocList.size(); i++) {
                        	Vector item = (Vector)dbAllocList.elementAt(i);
                        	int index = i;
                        	
                            if (slotNumber>0) {
                            	Timestamp tm = (Timestamp)item.elementAt(2);
                    			SimpleDateFormat df = new SimpleDateFormat(mask, this.handler.getLocale());			
                    			Integer slot = (Integer)this.granularity.get(df.format(tm));
                    			if (slot!=null) {
                    				index = slot.intValue();
                    			} else {
                    				index = -1;
                    			}
                            }
                            
                            if (index > -1) {
                                String resId = (String)item.elementAt(0);
                                UserTO res = (UserTO)resList.get(resId);
                                if (res!=null && (index < valBar.length || intervType.equals("3")) ) {
                                	int resIdx = Integer.parseInt(res.getGenericTag());
                                	float alloc = StringUtil.getStringToFloat((String)item.elementAt(1), new Locale("en", "US")); //must be enUS because should transform the float value from db (ex.: ##.0)
                                    if (slotNumber>0) {
                                    	valBar[index][resIdx] = valBar[index][resIdx] + (alloc/60);	
                                    } else {
                                    	valPie[resIdx] = (alloc/60);
                                    	if (alloc>0) {
                                    		valPieLabel[resIdx] = res.getUsername();	
                                    	}
                                    }
                            	}                            	
                            }
                    	}

                		//draw the chart
                        if (slotNumber>0) {
                            response = "{ \n" + 
                        		getJSonTitle() + "," +
                        		getJSonYLegend("(h)") + "," + 
                        		getBarStackValues(valBar, labels) + "," +
                        		getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, valBar, "y_axis") + "}";                	

                        } else {
                            response = "{ \n" + 
                        		getJSonTitle() + "," +
                        		getPieValues(valPie, valPieLabel) + "}";                	
                        }
                    	

                    }                    
                }
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
    
	
	private void defineGranulary(String intervType, int slotNumber, Timestamp iniRange, String mask) {
		Timestamp cursor = null;
		DateFormat df = null;
		for (int i=0; i <slotNumber; i++) {
			if (intervType.equals("1")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.DATE, i);
			} else if (intervType.equals("2")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.DATE, i);
	    	}
			df = new SimpleDateFormat(mask, this.handler.getLocale());
			this.granularity.put(df.format(cursor), new Integer(i));			
		}		
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
        }
        
        return xaxis;
	}	
}
