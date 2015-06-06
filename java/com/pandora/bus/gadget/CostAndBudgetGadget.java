package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CostInstallmentTO;
import com.pandora.CostStatusTO;
import com.pandora.CostTO;
import com.pandora.FieldValueTO;
import com.pandora.InvoiceItemTO;
import com.pandora.InvoiceTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceCapacityTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.delegate.CostDelegate;
import com.pandora.delegate.InvoiceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ResourceCapacityDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class CostAndBudgetGadget extends ChartGadget {

	private static final String COST_BUDGET_PROJECT  = "PROJECT";
	
	private static final String COST_BUDGET_INTERVAL = "INTERVAL";

	
	private HashMap<String, Integer> granularity = new HashMap<String, Integer>();
    private float[][] valBar = null;
    private float[][] valLine = null;

	
	public String getUniqueName(){
		return "label.manageOption.gadget.costbudget";
	}
	
	public String getId(){
		return "COST_BUDGET_CHART";
	}
	
	public String getImgLogo() {
		return "../images/gdglogo-18.png";
	}	

	public String getCategory() {
		return "label.manageOption.gadget.management";
	}
	
	public String getDescription() {
		return "label.manageOption.gadget.costbudget.desc";
	}
	
	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(COST_BUDGET_PROJECT, "-1"));
       	response.add(new TransferObject(COST_BUDGET_INTERVAL, "1"));
        return response;
    }
	
    public Vector getFields(){
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();

    	try {
        	ProjectDelegate pdel = new ProjectDelegate();
        	Vector<ProjectTO> buff = pdel.getProjectListByUser(super.handler);
        	
        	Vector<TransferObject> projList = new Vector<TransferObject>();
        	TransferObject defaultOpt = new TransferObject("-1", "label.combo.select");
        	projList.addElement(defaultOpt);
        	
        	if (buff!=null) {
            	Iterator<ProjectTO> i = buff.iterator();
            	while(i.hasNext()) {
            		ProjectTO pto = (ProjectTO)i.next();
            		TransferObject to = new TransferObject(pto.getId(), pto.getName());
            		projList.add(to);
            	}    		
        	}
        	response.add(new FieldValueTO(COST_BUDGET_PROJECT, "label.manageOption.gadget.costbudget.project", projList));

            Vector<TransferObject> intervList = new Vector<TransferObject>();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.costbudget.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.costbudget.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.costbudget.interval.3"));
            intervList.add(new TransferObject("4", "label.manageOption.gadget.costbudget.interval.4"));
        	response.add(new FieldValueTO(COST_BUDGET_INTERVAL, "label.manageOption.gadget.costbudget.interval", intervList));
        	        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
    }
    
    public int getPropertyPanelWidth(){
    	return 400;
    }

    public int getPropertyPanelHeight(){
    	return 140;
    }
    
   
	public String generate(Vector selectedFields)  throws BusinessException {		
	    String response = null;
	    UserDelegate udel = new UserDelegate();
	    ProjectDelegate pdel = new ProjectDelegate();
	    ResourceCapacityDelegate rcdel = new ResourceCapacityDelegate();
        String xaxis[] = null;
        
        try {
        	
        	Locale loc = udel.getCurrencyLocale();
			NumberFormat nf = NumberFormat.getCurrencyInstance(loc);
			String cs = nf.getCurrency().getSymbol();
			String title = this.getI18nMsg("label.manageOption.gadget.costbudget", handler.getLocale());
			
        	String intervType = super.getSelected(COST_BUDGET_INTERVAL, selectedFields);
            ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(COST_BUDGET_PROJECT, selectedFields)), true);
            if (pto!=null && intervType!=null && !intervType.trim().equals("")) {
                
            	
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
            	this.initArray(slotNumber);
                xaxis = this.getXAxisLabel(intervType, slotNumber, iniRange);

                Vector<ResourceCapacityTO> rescap = rcdel.getListByResourceProject(null, pto.getId(), null);
            	this.populateMaterialCosts(pto, mask, iniRange, DateUtil.getNow());
                this.populateHumanCosts(pto, mask, rescap);
                this.populateBudget(pto, mask);
                
                String[] barLabel = new String[]{this.getI18nMsg("label.manageOption.gadget.costbudget.mat", handler.getLocale()),
                								 this.getI18nMsg("label.manageOption.gadget.costbudget.hr", handler.getLocale())};
                
        		//draw the bars
                response = "{ \n" + 
                	getJSonTitle(title) + "," +
                	getJSonYLegend(cs) + "," +
                	getBarStackValues(this.valBar, barLabel, new String[]{"ff0000", "800000"}, this.valLine, null, new String[]{"008000"}, true) + "," +            	
                	getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, this.valBar, this.valLine, "y_axis") + "}";            
            }
            
        	//empty chart...
            if (response==null) {
                response = "{ \n" + 
            		getJSonTitle(title) + "," +
            		getJSonAxis(new String[1], null, "x_axis") + "," +
            		getJSonYLegend(cs) + "}";        	                
            }
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return response;
	}

	
	private void populateBudget(ProjectTO pto, String mask) throws BusinessException {
		InvoiceDelegate idel = new InvoiceDelegate();
		Vector<InvoiceTO> list = idel.getInvoiceList(pto.getId(), true);
		if (list!=null) {
			for (InvoiceTO ito : list) {
				Vector<InvoiceItemTO> itemList = ito.getItemsList();
				if (itemList!=null && ito.getDueDate()!=null) {
					for (InvoiceItemTO iito : itemList) {
						if (iito.getAmount()!=null && iito.getPrice()!=null && iito.getType()!=null) {
							if (iito.getType().intValue()!=3 && iito.getType().intValue()!=4) { //except taxes and disconts...
								long val = iito.getAmount().intValue() * iito.getPrice().longValue();
								this.putValueIntoArray(mask, ito.getDueDate(), new Long(val), 2);	
							}
						}
					}
				}
			}
		}
	}
	

	private void populateHumanCosts(ProjectTO pto, String mask, Vector<ResourceCapacityTO> rescapList) throws BusinessException{
		TaskDelegate tdel = new TaskDelegate();
		CostDelegate cdel = new CostDelegate();
		
		Vector<TaskTO> taskList = tdel.getTaskListByProject(pto, null, null, false, true);
		if (taskList!=null) {
			for (TaskTO tto : taskList) {
				Vector<ResourceTaskTO> list = tto.getAllocResources();
				if (list!=null) {
					for (ResourceTaskTO rtto : list) {
						Vector<ResourceTaskAllocTO> allocs = rtto.getAllocList();
						if (allocs!=null && rtto.getResource()!=null) {
							for (ResourceTaskAllocTO alloc : allocs) {
								if (alloc.getSequence()!=null && alloc.getAllocTime()!=null) {

									Timestamp tm = null;
									if (rtto.getActualDate()!=null) {
										tm = DateUtil.getChangedDate(rtto.getActualDate(), Calendar.DATE, alloc.getSequence().intValue());
									} else if (rtto.getStartDate()!=null) {
										tm = DateUtil.getChangedDate(rtto.getStartDate(), Calendar.DATE, alloc.getSequence().intValue());
									}
									
									if(tm!=null) {
										Integer cost = cdel.getCost(rescapList, tm, pto, rtto.getResource());
										Long newVal = null;
										if (cost!=null) {
											newVal = new Long((int)((alloc.getAllocTime().floatValue() / 60) * cost.intValue())); 
										}
										
										if (newVal!=null) {
											this.putValueIntoArray(mask, tm, newVal, 1);	
										}
									}
								}
							}					
						}
					}					
				}
			}
		}
	}
	
	
	private void populateMaterialCosts(ProjectTO pto, String mask, Timestamp iniRange, Timestamp finalRange) throws BusinessException{
        CostDelegate cdel = new CostDelegate();
        Vector<CostTO> allcosts = cdel.getListByProject(pto, true, iniRange, finalRange);
        if (allcosts!=null) {
        	for (CostTO cto : allcosts) {
				Vector<CostInstallmentTO> insts = cto.getInstallments();
				if (insts!=null) {
					for (CostInstallmentTO cito : insts) {

						if (cito.getValue()!=null && cito.getDueDate()!=null && cito.getCostStatus()!=null) {							
							if (!cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_CANCELED)) {
								this.putValueIntoArray(mask, cito.getDueDate(), cito.getValue(), 0);
							}
						}
					}
				}
			}
        }
	}
	
	
	private void putValueIntoArray(String mask, Timestamp tm, Long val, int idx){
		SimpleDateFormat df = new SimpleDateFormat(mask, this.handler.getLocale());			
		Integer slot = (Integer)this.granularity.get(df.format(tm));
		
		if (slot==null && !tm.after(DateUtil.getNow())) {
			slot = new Integer(0);
		}
		
		if (slot!=null) {
			int index = slot.intValue();
			
			if (idx<2) {
				if (index<this.valBar.length) {
					for(int s=index; s<this.valBar.length; s++) {
						this.valBar[s][idx] = this.valBar[s][idx] + ((val.floatValue())/100); 
					}
				}
			} else {
				if (index<this.valLine.length) {
					for(int s=index; s<this.valLine.length; s++) {
						this.valLine[s][0] = this.valLine[s][0] + ((val.floatValue())/100); 
					}
				}				
			}
		}
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

	
	private void initArray(int size){

		if (size<=0) {
			size = 1;
		}
	
		this.valBar = new float[size][2];
		this.valLine = new float[size][1];
	
        for (int i=0; i<size; i++) {
        	this.valBar[i][0] = 0;
        	this.valBar[i][1] = 0;
        	this.valLine[i][0] = 0;
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
