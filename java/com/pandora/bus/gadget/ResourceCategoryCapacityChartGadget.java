package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.OccurrenceTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DBUtil;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

public final class ResourceCategoryCapacityChartGadget extends ChartGadget {

	private static final String RES_CAT_CAP_PROJECT   = "PROJECT";

	private static final String RES_CAT_CAP_RESOURCE  = "RESOURCE";

	private static final String RES_CAT_CAP_INTERVAL  = "INTERVAL";
	
	private HashMap<String,Integer> granularity = new HashMap<String,Integer>();

	
	public String getUniqueName(){
		return "label.manageOption.gadget.rescatcap";
	}
	
	public String getId(){
		return "RESOURCE_CAT_CAP_CHART";
	}

	public String getCategory() {
		return "label.manageOption.gadget.capacity";
	}

	public String getImgLogo() {
		return "../images/gdglogo-7.png";
	}	
	
	@Override	
	public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(RES_CAT_CAP_PROJECT, "-1"));
       	response.add(new TransferObject(RES_CAT_CAP_RESOURCE, "-1"));
       	response.add(new TransferObject(RES_CAT_CAP_INTERVAL, "1"));
        return response;
    }
	
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 160;
    }
        
	public int getHeight(){
		return 170;
	}
    
	@Override
	public Vector getFields(){
		return getFields(null);
    }
	
	
	@Override
	public boolean canReloadFields() {
		return true;
	}	
	
	public Vector<FieldValueTO> getFields(Vector<TransferObject> currentValues) {
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();

    	try {
         	Vector<TransferObject> buff = super.getProjectFromUser(true);
        	response.add(new FieldValueTO(RES_CAT_CAP_PROJECT, "label.manageOption.gadget.rescatcap.project", buff));
        	String projId = super.getSelected(RES_CAT_CAP_PROJECT, currentValues);
        	        	
    		Vector<TransferObject> userlist = new Vector<TransferObject>();        	
        	if (projId!=null && !projId.equals("")) {
        		userlist = super.getAtiveUsersByProject(projId);	
        	}
        	userlist.addElement(new TransferObject("-2", super.getI18nMsg("label.all", handler.getLocale())));
        	response.add(new FieldValueTO(RES_CAT_CAP_RESOURCE, "label.manageOption.gadget.rescatcap.resource", userlist));
        	
            Vector<TransferObject> intervList = new Vector<TransferObject>();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.rescatcap.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.rescatcap.interval.2"));
            intervList.add(new TransferObject("4", "label.manageOption.gadget.rescatcap.interval.4"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.rescatcap.interval.3"));
            intervList.add(new TransferObject("5", "label.manageOption.gadget.rescatcap.interval.5"));
        	response.add(new FieldValueTO(RES_CAT_CAP_INTERVAL, "label.manageOption.gadget.rescatcap.interval", intervList));
        	
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
        	String intervType = super.getSelected(RES_CAT_CAP_INTERVAL, selectedFields);
        	String resourceId = super.getSelected(RES_CAT_CAP_RESOURCE, selectedFields);
        	if (resourceId!=null && intervType!=null && !resourceId.equals("-1")) {
            	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(RES_CAT_CAP_PROJECT, selectedFields)), true);
            	Vector<OccurrenceTO> iterations = null;
                if (pto!=null) {                	
                	String mask = "yyyyMMdd";
                    Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -6);            
                	int slotNumber = 7;
                	if (intervType.equals("2")) {
                		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -29);
                		slotNumber = 30;
                		mask = "yyyyMMdd";
                	} else if (intervType.equals("3")) {
                		iniRange = DateUtil.getDate("1", "1", "1900");
                		slotNumber = 0;
                	} else if (intervType.equals("4")) {
                		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -364);
                		iniRange = DateUtil.getDateTime("01", DateUtil.get(iniRange, Calendar.MONTH)+"", 
                				DateUtil.get(iniRange, Calendar.YEAR)+"", "00", "00", "00");
                		slotNumber = 13;
                		mask = "MMM-yyyy";
                	} else if (intervType.equals("5")) {
                		iniRange = pto.getCreationDate();
                		OccurrenceDelegate odel = new OccurrenceDelegate();
                		iterations = odel.getIterationListByProject(pto.getId(), false);
                		if (iterations==null) {
                			iterations = new Vector<OccurrenceTO>();
                		}
                		OccurrenceTO dummy = new OccurrenceTO("-1");
                		dummy.setName(super.getI18nMsg("label.none", this.handler.getLocale()));
                		iterations.add(dummy);
                		slotNumber = iterations.size();	
                	}
                	this.defineGranulary(intervType, slotNumber, iniRange, mask, iterations);
                    String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange, iterations);

                    DbQueryDelegate qdel = new DbQueryDelegate();            	
                	HashMap<String, Integer> categoryHash = new HashMap<String, Integer>();                	
                    	
                	String[] labels = new String[100];
                    float[][] valBar = new float[slotNumber][100];
                    float[][] valSingle = new float[100][1];
                	
            		//get data related to allocation from data base...
                    int[] types = null;
                    Vector<Object> params = new Vector<Object>();
                    String resourceWhere = " and a.resource_id=? ";
                    if (resourceId.equals("-2")) { //ALL resources...
                    	types = new int[] {Types.TIMESTAMP};
                    	resourceWhere = "";
                    } else {
                        types = new int[] {Types.VARCHAR, Types.TIMESTAMP};
                        params.addElement(resourceId);                	
                    }
                    params.addElement(iniRange);
                    
                    String sqlData = "";
                    String dbname = qdel.getDBProductName();
                    
                    if (slotNumber>0) {
                        sqlData = "select sub.name, sum(sub.alloc_time) as alloc_time, sub.bucket_date, sub.iteration from ( ";
                    } else {
                    	sqlData = "select sub.name, sum(sub.alloc_time) as alloc_time from ( ";
                    }
                    
                    sqlData = sqlData + "select a.alloc_time, c.name, ";
                    sqlData = sqlData + DBUtil.addDate(dbname, "rt.actual_date", "a.sequence-1") + " as bucket_date ";
                    
                    //if (dbname.equalsIgnoreCase("MySQL")) {
                    //	sqlData = sqlData + "ADDDATE(rt.actual_date, a.sequence-1) as bucket_date ";
            		//} else {
            		//	sqlData = sqlData + "rt.actual_date+ cast((a.sequence-1) || ' day' as interval) as bucket_date ";
            		//}
                    
                    sqlData = sqlData + ", o.name as iteration, o.id " + 
                                "from resource_task_alloc a, task t, resource_task rt, category c, " +
                                     "planning pn left outer join occurrence o on (o.id = pn.iteration) " +
    			    	        "where a.project_id in (" + super.getProjectIn( pto.getId() )+ ") " +
    			    	        "and a.task_id = t.id  " +
    			    	        "and t.id = pn.id  " +
    			    	        "and a.task_id = rt.task_id " + 
    			    	        "and a.resource_id = rt.resource_id " + 
    			    	        "and a.project_id = rt.project_id  " +
    			    	        "and rt.actual_date is not null " +
    			    	        "and t.category_id = c.id " +
    			    	        "and a.alloc_time > 0 " + resourceWhere +
    	    			      ") as sub  " +
    	    			      "where sub.bucket_date >= ? "; 
                    if (slotNumber>0) {
                        sqlData = sqlData + "group by sub.name, sub.bucket_date, sub.iteration " +
    		        						"order by sub.bucket_date ";                        
                    } else {
                        sqlData = sqlData + "group by sub.name";
                    }
                    Vector<Vector<Object>> dbAllocList = qdel.performQuery(sqlData, types, params);
                    if (dbAllocList!=null) {
                    	for (int i=1; i<dbAllocList.size(); i++) {
                        	Vector<Object> item = (Vector<Object>)dbAllocList.elementAt(i);
                        	int index = i;
                        	
                            if (slotNumber>0) {
                            	Timestamp tm = (Timestamp)item.elementAt(2);
                            	
                            	String hashKey = null;
                            	if (intervType.equals("5")) {
                            		hashKey = (String)item.elementAt(3);
                            		if (hashKey==null) {
                            			hashKey = super.getI18nMsg("label.none", this.handler.getLocale());
                            		}
                            	} else {
                        			SimpleDateFormat df = new SimpleDateFormat(mask, this.handler.getLocale());			
                        			hashKey = df.format(tm);
                            	}
                            	Integer slot = (Integer)this.granularity.get(hashKey);
                            	
                    			if (slot!=null) {
                    				index = slot.intValue();	                        	
                    			} else {
                    				index = -1;
                    			}
                            }
                            
                            if (index>-1) {
                                String catName = (String)item.elementAt(0);
                                Integer idx = (Integer)categoryHash.get(catName);
                                if (idx==null) {
                                	Integer newValue = new Integer(categoryHash.size());
                                	categoryHash.put(catName, newValue);
                                	idx = newValue;
                                	if (catName.equals("&nbsp;")) {
                                		labels[idx.intValue()] = " ";
                                	} else {
                                		labels[idx.intValue()] = catName;	
                                	}
                                }
                                
                               	float alloc = StringUtil.getStringToFloat((String)item.elementAt(1), new Locale("en", "US")); //must be enUS because should transform the float value from db (ex.: ##.0)                            
                                if (slotNumber>0) {
                                   	valBar[index][idx.intValue()] = valBar[index][idx.intValue()] + (alloc/60);
                                } else {
                                	valSingle[index-1][0] = (alloc/60);
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
	                    		getJSonYLegend("(h)") + "," +                             
                        		getJSonAxis(labels, null, "x_axis") + "," +
                        		getJSonAxis(null, valSingle, "y_axis") + "," +
                        		getBarValues(valSingle) + "}";                	
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

	
	private void defineGranulary(String intervType, int slotNumber, Timestamp iniRange, String mask, Vector<OccurrenceTO> iterations) {
		Timestamp cursor = null;
		DateFormat df = null;
		for (int i=0; i <slotNumber; i++) {
			String gran = "";
			df = new SimpleDateFormat(mask, this.handler.getLocale());			
			if (intervType.equals("1")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.DATE, i);
				gran = df.format(cursor);
			} else if (intervType.equals("2")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.DATE, i);
				gran = df.format(cursor);
	    	} else if (intervType.equals("4")) {
	    		cursor = DateUtil.getChangedDate(iniRange, Calendar.MONTH, i);
	    		gran = df.format(cursor);
	    	} else if (intervType.equals("5")) {
	    		OccurrenceTO iteration = iterations.get(i);
	    		gran = iteration.getName();
	    	}
			this.granularity.put(gran, new Integer(i));			
		}		
	}
	

	private String[] getXAxisLabel(String intervType, int slotNumber, Timestamp iniRange, Vector<OccurrenceTO> iterations){
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
        	        	
        } else if (intervType.equals("4")) {
            for (int i=0; i<slotNumber;i++) {
            	DateFormat df = new SimpleDateFormat("MMM-yyyy", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.MONTH, i));
            	xaxis[i] = slotLabel;
            }
            
        } else if (intervType.equals("5")) {
        	Iterator<OccurrenceTO> i = iterations.iterator();
        	int c = 0;
        	while(i.hasNext()) {
        		OccurrenceTO oto = i.next();
            	xaxis[c++] = oto.getName();
        	}
        }
        
        return xaxis;
	}	
}
