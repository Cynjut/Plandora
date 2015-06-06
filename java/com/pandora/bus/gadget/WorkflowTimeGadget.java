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
import com.pandora.ProjectTO;
import com.pandora.TemplateTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DBUtil;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

public final class WorkflowTimeGadget extends ChartGadget {

	private static final String WORKFLOW_TIME_PROJECT  = "PROJECT";
	private static final String WORKFLOW_TIME_WORKFLOW = "WORFLOW";
	private static final String WORKFLOW_TIME_INTERVAL = "INTERVAL";
	
	private HashMap<String,Integer> granularity = new HashMap<String,Integer>();

	
	public String getUniqueName(){
		return "label.manageOption.gadget.workflowtime";
	}
	
	public String getId(){
		return "WORKFLOW_TIME_CHART";
	}

	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(WORKFLOW_TIME_PROJECT, "-1"));
       	response.add(new TransferObject(WORKFLOW_TIME_WORKFLOW, "-1"));
       	response.add(new TransferObject(WORKFLOW_TIME_INTERVAL, "-1"));
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
    
	public String getImgLogo() {
		return "../images/gdglogo-9.png";
	}	

	public String getDescription() {
		return "label.manageOption.gadget.workflowtime.desc";
	}
	
	
    public Vector getFields(){
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();

    	try {
         	Vector<TransferObject> buff = super.getProjectFromUser(true);
        	response.add(new FieldValueTO(WORKFLOW_TIME_PROJECT, "label.manageOption.gadget.workflowtime.project", buff));

        	Vector<TransferObject> wfList = new Vector<TransferObject>();
        	TaskTemplateDelegate ttdel = new TaskTemplateDelegate();
        	Vector<TemplateTO> workfList = ttdel.getTemplateListByProject(null, false);
        	Iterator<TemplateTO> i = workfList.iterator();
        	while(i.hasNext()) {
        		TemplateTO tto = i.next();
        		wfList.add(new TransferObject(tto.getId(), tto.getName()));		
        	}
        	response.add(new FieldValueTO(WORKFLOW_TIME_WORKFLOW, "label.manageOption.gadget.workflowtime.workflow", wfList));
        	
            Vector<TransferObject> intervList = new Vector<TransferObject>();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.workflowtime.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.workflowtime.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.workflowtime.interval.3"));
        	response.add(new FieldValueTO(WORKFLOW_TIME_INTERVAL, "label.manageOption.gadget.workflowtime.interval", intervList));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
    }

	public String generate(Vector selectedFields) throws BusinessException {
        String response = "";
        ProjectDelegate pdel = new ProjectDelegate();
        
        try {
            Timestamp now = DateUtil.getNow();
        	String intervType = super.getSelected(WORKFLOW_TIME_INTERVAL, selectedFields);
        	String templateId = super.getSelected(WORKFLOW_TIME_WORKFLOW, selectedFields);
        	
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(WORKFLOW_TIME_PROJECT, selectedFields)), true);
            if (pto!=null && !intervType.equals("") && !templateId.equals("")) {

            	String mask = "yyyyMMdd";
                Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -6);            
            	int slotNumber = 7;
            	if (intervType.equals("2")) {
            		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -179);
            		for (int w=0; w<=7; w++) {
            			iniRange = DateUtil.getChangedDate(iniRange, Calendar.DATE, w);
            			if (DateUtil.get(iniRange, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
            				break;
            			}
            		}
            		slotNumber = 26;
            		mask = "w-yy";
            	} else if (intervType.equals("3")) {
            		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -364);
            		iniRange = DateUtil.getDateTime("01", DateUtil.get(iniRange, Calendar.MONTH)+"", 
            				DateUtil.get(iniRange, Calendar.YEAR)+"", "00", "00", "00");
            		slotNumber = 13;
            		mask = "MMM-yyyy";
            	}
            	this.defineGranulary(intervType, slotNumber, iniRange, mask);
                String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange);
                
                DbQueryDelegate qdel = new DbQueryDelegate();            	
            	HashMap<String, Integer> templateHash = new HashMap<String, Integer>();
            	
            	String[] labels = new String[100];
                float[][] valBar = new float[slotNumber][100];
            	
        		//get data related to allocation from data base...
                int[] types = new int[] {Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP};
                Vector<Object> params = new Vector<Object>();
                params.addElement(templateId);
                params.addElement(templateId);
                params.addElement(iniRange);
                StringBuffer sqlData = new StringBuffer("");
                String dbname = qdel.getDBProductName();
                
                sqlData.append("select n.name, sum(sub1.alloc_time), sub1.bucket_date, sub1.project_id " +
                				"from node_template n, " +
                				"custom_node_template c LEFT OUTER JOIN ( ");
                sqlData.append("  select a.task_id, a.alloc_time, a.project_id, ");  
                sqlData.append(DBUtil.addDate(dbname, "rt.actual_date", "a.sequence-1") + " as bucket_date ");
                
                //if (dbname.equalsIgnoreCase("MySQL")) {
                //	sqlData.append("ADDDATE(rt.actual_date, a.sequence-1) as bucket_date ");
        		//} else {
        		//	sqlData.append("rt.actual_date+ cast((a.sequence-1) || ' day' as interval) as bucket_date ");
        		//}
                sqlData.append("  from resource_task_alloc a, task t, resource_task rt ");
               	sqlData.append("  where a.task_id = t.id  ");
                sqlData.append("   and a.task_id = rt.task_id ");
                sqlData.append("   and a.resource_id = rt.resource_id ");
                sqlData.append("   and a.project_id = rt.project_id  ");
                sqlData.append("   and rt.actual_date is not null ");
                sqlData.append("   and a.alloc_time > 0 ");
                sqlData.append("   and a.task_id in ( select related_task_id from custom_node_template where template_id=? ) ");
                sqlData.append(") as sub1 on sub1.task_id = c.related_task_id ");
                sqlData.append("where c.template_id=? and c.node_template_id = n.id and n.node_type = '1' and sub1.bucket_date >= ? ");
                sqlData.append("and sub1.project_id in (" + super.getProjectIn( pto.getId() )+ ") ");
                sqlData.append("group by n.name, sub1.bucket_date, sub1.project_id");
                
                Vector<Vector<Object>> dbAllocList = qdel.performQuery(sqlData.toString(), types, params);
                if (dbAllocList!=null) {
                	for (int i=1; i<dbAllocList.size(); i++) {
                    	Vector<Object> item = (Vector<Object>)dbAllocList.elementAt(i);
                    	int index = i;
                    	
                    	Timestamp tm = (Timestamp)item.elementAt(2); 
            			SimpleDateFormat df = new SimpleDateFormat(mask, this.handler.getLocale());			
            			Integer slot = (Integer)this.granularity.get(df.format(tm));
            			if (slot!=null) {
            				index = slot.intValue();
            			} else {
            				index = -1;
            			}
                    	         
            			if (index>-1) {
                            String templateName = (String)item.elementAt(0);
                            Integer idx = (Integer)templateHash.get(templateName);
                            if (idx==null) {
                            	Integer newValue = new Integer(templateHash.size());
                            	templateHash.put(templateName, newValue);
                            	idx = newValue;
                            	if (templateName.equals("&nbsp;")) {
                            		labels[idx.intValue()] = " ";
                            	} else {
                            		labels[idx.intValue()] = templateName;	
                            	}
                            }
                            
                           	float alloc = StringUtil.getStringToFloat((String)item.elementAt(1), new Locale("en", "US")); //must be enUS because should transform the float value from db (ex.: ##.0)                            
                           	valBar[index][idx.intValue()] = valBar[index][idx.intValue()] + (alloc/60);            				
            			}
                	}

            		//draw the chart
                    response = "{ \n" + 
                		getJSonTitle() + "," +
                		getJSonYLegend("(h)") + "," + 
                		getBarStackValues(valBar, labels) + "," +
                		getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, valBar, "y_axis") + "}";                	
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
            	DateFormat df = new SimpleDateFormat("w-yy", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.WEEK_OF_YEAR, i));
            	xaxis[i] = slotLabel;
            }        		
        	
        } else if (intervType.equals("3")) {
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
			if (intervType.equals("1")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.DATE, i);
	    	} else if (intervType.equals("2")) {
				cursor = DateUtil.getChangedDate(iniRange, Calendar.WEEK_OF_YEAR, i);
	    	} else if (intervType.equals("3")) {
	    		cursor = DateUtil.getChangedDate(iniRange, Calendar.MONTH, i);							
	    	}
			df = new SimpleDateFormat(mask, this.handler.getLocale());
			this.granularity.put(df.format(cursor), new Integer(i));			
		}		
	}	
}
