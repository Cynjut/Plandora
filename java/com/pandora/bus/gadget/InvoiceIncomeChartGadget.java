package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.InvoiceStatusTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class InvoiceIncomeChartGadget extends ChartGadget {

	private static final String INVOICE_INCOME_PROJECT = "PROJECT";
	
	private static final String INVOICE_INCOME_INTERVAL = "INTERVAL";
	
	private HashMap granularity = new HashMap();

	
	public String getUniqueName(){
		return "label.manageOption.gadget.invoiceinc";
	}
	
	public String getId(){
		return "INVOICE_INCOME_CHART";
	}

	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(INVOICE_INCOME_PROJECT, "-1"));
       	response.add(new TransferObject(INVOICE_INCOME_INTERVAL, "1"));
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
		return "label.manageOption.gadget.invoiceinc.desc";
	}

	public String getImgLogo() {
		return "../images/gdglogo-1.png";
	}

	public Vector getFields(){
    	Vector response = new Vector();

    	try {
        	Vector projList = super.getProjectFromUser(true);
        	response.add(new FieldValueTO(INVOICE_INCOME_PROJECT, "label.manageOption.gadget.invoiceinc.project", projList));
        	
            Vector intervList = new Vector();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.invoiceinc.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.invoiceinc.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.invoiceinc.interval.3"));
        	response.add(new FieldValueTO(INVOICE_INCOME_INTERVAL, "label.manageOption.gadget.invoiceinc.interval", intervList));
        	
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
        	String intervType = super.getSelected(INVOICE_INCOME_INTERVAL, selectedFields);
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(INVOICE_INCOME_PROJECT, selectedFields)), true);
                    	
        	String mask = "yyyyMMdd";
            Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -89);            
        	int slotNumber = 26;
        	if (intervType.equals("2")) {
        		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -179);
        		iniRange = DateUtil.getDateTime("01", DateUtil.get(iniRange, Calendar.MONTH)+"", 
        				DateUtil.get(iniRange, Calendar.YEAR)+"", "00", "00", "00");        		
        		slotNumber = 13;
        		mask = "MMM-yyyy";
        	} else if (intervType.equals("3")) {
        		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -364);
        		iniRange = DateUtil.getDateTime("01", DateUtil.get(iniRange, Calendar.MONTH)+"", 
        				DateUtil.get(iniRange, Calendar.YEAR)+"", "00", "00", "00");
        		slotNumber = 25;
        		mask = "MMM-yyyy";
        	}
        	this.defineGranulary(intervType, slotNumber, iniRange, mask);
    		float[][] chartVals = new float[slotNumber][2]; 
            String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange);
            
            if (pto!=null) {
                int[] types = new int[] {Types.TIMESTAMP};
                Vector params = new Vector();
                params.addElement(iniRange);
                
                DbQueryDelegate qdel = new DbQueryDelegate();
                String sql = "select s.state_machine_order, v.due_date, " +
                		         "sum((i.price * i.amount) * i.type_index) as val " +
                		     "from invoice v, invoice_item i, invoice_status s " +
                		     "where i.invoice_id = v.id " +
                		       "and v.project_id in (" + super.getProjectIn( pto.getId() )+ ") " +
                		       "and v.due_date > ? " +
                		       "and s.id = v.invoice_status_id " +
                		       "and s.state_machine_order <> " + InvoiceStatusTO.STATE_MACHINE_CANCEL + " " +
                		     "group by v.due_date, s.state_machine_order " +
                		     "order by v.due_date";
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
                            		if (index < chartVals.length) {
    		                    		if (item.elementAt(2)!=null) {
    		                        		int val = Integer.parseInt(item.elementAt(2)+"");
    		                        		float currencyInvoice = (float)val / 100;
    		                        		
    		                        		String state = (String)item.elementAt(0);		                        		
    		                            	if (state!=null && state.equals(InvoiceStatusTO.STATE_MACHINE_PAID + "")) {
    		                            		chartVals[index][0]+=currencyInvoice;  
    		                            	} else {
    		                            		chartVals[index][1]+=currencyInvoice; 
    		                            	}
    		                    		}
                            		}
                            	} catch(ArrayIndexOutOfBoundsException e) {
                            		System.out.println("index:" + index + " slot: " + slot + " dt:" + item.elementAt(1) + " gran.list:" + this.granularity);
                            	}
                			} else {
                				System.out.println("index is null. dt:" + tm + " gran.list:" + this.granularity);
                			}                    		
                    	}
                	}
                }
                
                String[] labels = new String[] {
                		super.getI18nMsg("label.manageOption.gadget.invoiceinc.paid"),
                		super.getI18nMsg("label.manageOption.gadget.invoiceinc.notpaid") };
                
        		//draw the bars
                response = "{ \n" + 
                	getJSonTitle() + "," +
                	getJSonYLegend("($)") + "," + 
                	getBarStackValues(chartVals, labels) + "," +
                	getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, chartVals, "y_axis") + "}";
            } else {
            	
            	//empty chart...
                response = "{ \n" + 
                	getJSonTitle() + "," +
                	getJSonAxis(xaxis, null, "x_axis") + "," +
                	getJSonYLegend("($)") + "}";
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
            	DateFormat df = new SimpleDateFormat("w-yy", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.WEEK_OF_YEAR, i));
            	xaxis[i] = slotLabel;
            }        		
        	
        } else if (intervType.equals("2")) {
            for (int i=0; i<slotNumber;i++) {
            	DateFormat df = new SimpleDateFormat("MMM-yyyy", this.handler.getLocale());
            	String slotLabel = df.format(DateUtil.getChangedDate(iniRange, Calendar.MONTH, i));
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
		if (intervType!=null && !intervType.trim().equals("")) {
			for (int i=0; i <slotNumber; i++) {
		    	if (intervType.equals("1")) {
					cursor = DateUtil.getChangedDate(iniRange, Calendar.WEEK_OF_YEAR, i);
		    	} else if (intervType.equals("2")) {
		    		cursor = DateUtil.getChangedDate(iniRange, Calendar.MONTH, i);
		    	} else if (intervType.equals("3")) {
		    		cursor = DateUtil.getChangedDate(iniRange, Calendar.MONTH, i);								    		
		    	}
				df = new SimpleDateFormat(mask, this.handler.getLocale());
				this.granularity.put(df.format(cursor), new Integer(i));			
			}					
		}
	}
		
}
