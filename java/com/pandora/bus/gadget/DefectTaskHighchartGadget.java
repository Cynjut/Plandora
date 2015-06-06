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
import com.pandora.helper.DBUtil;
import com.pandora.helper.DateUtil;

public class DefectTaskHighchartGadget extends HighchartGadget {

	private static final String DEFECT_TASK_PROJECT = "PROJECT";
	
	private static final String DEFECT_TASK_INTEVAL = "INTERVAL";
	
	private HashMap<String, Integer> granularity = new HashMap<String, Integer>();

	
	public String getUniqueName(){
		return "label.manageOption.gadget.defectTask";
	}
	
	public String getId(){
		return "DEFECT_TASK_CHART";
	}

	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(DEFECT_TASK_PROJECT, "-1"));
       	response.add(new TransferObject(DEFECT_TASK_INTEVAL, "1"));
        return response;
    }
	
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 140;
    }
    
	public String getCategory() {
		return "label.manageOption.gadget.task";
	}
	

	public String getDescription() {
		return "label.manageOption.gadget.defectTask.desc";
	}

	public String getImgLogo() {
		return "../images/gdglogo-10.png";
	}	

	
    public Vector getFields(){
    	Vector response = new Vector();

    	try {
    		Vector projList = super.getProjectFromUser(true);    	
        	response.add(new FieldValueTO(DEFECT_TASK_PROJECT, "label.manageOption.gadget.defectTask.project", projList));
        	
            Vector intervList = new Vector();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.defectTask.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.defectTask.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.defectTask.interval.3"));
            intervList.add(new TransferObject("4", "label.manageOption.gadget.defectTask.interval.4"));
        	response.add(new FieldValueTO(DEFECT_TASK_INTEVAL, "label.manageOption.gadget.defectTask.interval", intervList));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
    }
	
	private void defineGranulary(String intervType, int slotNumber, Timestamp iniRange, String mask) {
		Timestamp cursor = null;
		DateFormat df = null;
		if (intervType!=null && !intervType.trim().equals("")) {
			for (int i=0; i <slotNumber; i++) {
				if (intervType.equals("1")) {
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
	
	public String generate(Vector selectedFields)  throws BusinessException {
        ProjectDelegate pdel = new ProjectDelegate();
        String response = "";
        
        try {
        	String intervType = super.getSelected(DEFECT_TASK_INTEVAL, selectedFields);
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(DEFECT_TASK_PROJECT, selectedFields)), true);
            
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
    		float[][] dataVals = new float[slotNumber][2]; 
            String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange);
            
            if (pto!=null) {
                int[] types = new int[] {Types.TIMESTAMP};
                Vector params = new Vector();
                params.addElement(iniRange);
                
                DbQueryDelegate qdel = new DbQueryDelegate();
                String dbname = qdel.getDBProductName();
                
                String sql = "select sub.is_defect, sub.bucket_date, sub.alloc_time from (" +
                		"select c.is_defect, a.alloc_time, ";
                
                sql = sql + DBUtil.addDate(dbname, "rt.actual_date", "a.sequence-1") + " as bucket_date ";
                
        		//if (dbname.equalsIgnoreCase("MySQL")) {
        		//	sql = sql + "ADDDATE(rt.actual_date, a.sequence-1) as bucket_date ";
        		//} else {
        		//	sql = sql + "rt.actual_date+ cast((a.sequence-1) || ' day' as interval) as bucket_date ";
        		//}

                sql = sql +	"from resource_task_alloc a, task t, resource_task rt, category c " +
                		"where a.project_id in (" + super.getProjectIn( pto.getId() )+ ") " +
                		"and a.task_id = t.id " +
                		"and a.task_id = rt.task_id " +
                		"and a.resource_id = rt.resource_id " +
                		"and a.project_id = rt.project_id " +
                		"and t.category_id = c.id and a.alloc_time > 0 " +
                		"and rt.actual_date is not null) as sub " +
                		"where sub.bucket_date >= ?" + 
                		"order by sub.bucket_date";
                Vector dbList = qdel.performQuery(sql, types, params);

                if (dbList!=null) {                	
                	for (int i=1; i<dbList.size(); i++) {
                    	Vector item = (Vector)dbList.elementAt(i);
                    	Timestamp tm = (Timestamp)item.elementAt(1);
                    	if (!tm.after(DateUtil.getNow())) {
                    	
                			SimpleDateFormat df = new SimpleDateFormat(mask, this.handler.getLocale());			
                			Integer slot = (Integer)this.granularity.get(df.format(tm));
                			if (slot!=null) {
                				int index = slot.intValue();
    	                    	try {
    	                    		if (index<dataVals.length) {
    		                    		if (item.elementAt(2)!=null) {
    		                        		int duration = Integer.parseInt(item.elementAt(2)+"");
    		                        		float hours = (float)duration / 60;
    		                            	if (item.elementAt(0)!=null && ((String)item.elementAt(0)).equals("1")) {
    		                            		dataVals[index][0]+=hours;  
    		                            	} else {
    		                            		dataVals[index][1]+=hours; 
    		                            	}                    		                    			
    		                    		}
    	                    		}
    	                    	} catch(ArrayIndexOutOfBoundsException e) {
    	                    		System.out.println("index:" + index + " slot: " + slot + " dt:" + item.elementAt(1) + " gran.list:" + this.granularity);
    	                    	}                				
                			}
                    	}
                	}
                }
                
                String defectLbl = super.getI18nMsg("label.manageOption.gadget.defectTask.label.1");
                String nonDefectLbl = super.getI18nMsg("label.manageOption.gadget.defectTask.label.2");
                String[] labels = new String[] {defectLbl, nonDefectLbl};
                
        		//draw the bars
                response = super.getHeader(this.getId(), "column", defectLbl + " X " + nonDefectLbl, pto.getName(), false) +
                		   super.getLegend(false) +
                		   super.getXAxis(xaxis, false) +
                		   super.getYAxis(0, "(h)") +
                		   super.getTooltip() +
                		   super.getPlot(labels, dataVals, true, null) +
                		   super.getFooter();
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
