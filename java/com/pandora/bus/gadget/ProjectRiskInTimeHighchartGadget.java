package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.RiskHistoryTO;
import com.pandora.RiskStatusTO;
import com.pandora.RiskTO;
import com.pandora.TransferObject;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RiskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class ProjectRiskInTimeHighchartGadget extends HighchartGadget {
	private static final String RISK_PERIOD_PROJECT   = "PROJECT";
	
	private static final String RISK_PERIOD_INTERVAL  = "INTERVAL";

	private static final String RISK_PERIOD_INDICATOR = "INDICATOR";
	
	
	private HashMap<String,Integer> granularity = new HashMap<String,Integer>();
	
	
	public String getUniqueName(){
		return "label.manageOption.gadget.riskperi";
	}
	
	
	public String getId(){
		return "RISK_PERIOD_CHART";
	}

	
	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(RISK_PERIOD_PROJECT, "-1"));
       	response.add(new TransferObject(RISK_PERIOD_INTERVAL, "1"));
       	response.add(new TransferObject(RISK_PERIOD_INDICATOR, "1"));
        return response;
    }
	
	
    public int getPropertyPanelWidth(){
    	return 400;
    }

    
    public int getPropertyPanelHeight(){
    	return 150;
    }
    
    
	public String getCategory() {
		return "label.manageOption.gadget.management";
	}

	
	public String getDescription() {
		return "label.manageOption.gadget.riskperi.desc";
	}

	
	public String getImgLogo() {
		return "../images/gdglogo-17.png";
	}

	
	public Vector getFields(){
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();
    	try {
        	Vector<TransferObject> projList = super.getProjectFromUser(false);
        	response.add(new FieldValueTO(RISK_PERIOD_PROJECT, "label.manageOption.gadget.riskperi.project", projList));
        	
            Vector<TransferObject> intervList = new Vector<TransferObject>();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.riskperi.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.riskperi.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.riskperi.interval.3"));
        	response.add(new FieldValueTO(RISK_PERIOD_INTERVAL, "label.manageOption.gadget.riskperi.interval", intervList));
        	
            Vector<TransferObject> indicatorList = new Vector<TransferObject>();
            indicatorList.add(new TransferObject("1", "label.formRisk.probability"));
            indicatorList.add(new TransferObject("2", "label.formRisk.impact"));
            indicatorList.add(new TransferObject("3", "label.formRisk.tendency"));
        	response.add(new FieldValueTO(RISK_PERIOD_INDICATOR, "label.manageOption.gadget.riskperi.indicator", indicatorList));

    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
        return response;
    }
	
	
	public String generate(Vector selectedFields)  throws BusinessException {
        ProjectDelegate pdel = new ProjectDelegate();
        RiskDelegate rdel = new RiskDelegate();
        String response = "";
        
        try {
        	String intervType = super.getSelected(RISK_PERIOD_INTERVAL, selectedFields);
        	String indicator = super.getSelected(RISK_PERIOD_INDICATOR, selectedFields);
        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(super.getSelected(RISK_PERIOD_PROJECT, selectedFields)), true);

        	String mask = "yyyyMMdd";
            Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -29);;            
            int slotNumber = 30;
   		 	int incType = Calendar.DATE;
        	if (intervType.equals("2")) {
        		incType = Calendar.WEEK_OF_YEAR;
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
        		incType = Calendar.MONTH;
        		iniRange = DateUtil.getChangedDate(DateUtil.getNowWithoutTime(), Calendar.DATE, -364);
        		iniRange = DateUtil.getDateTime("01", DateUtil.get(iniRange, Calendar.MONTH)+"", 
        				DateUtil.get(iniRange, Calendar.YEAR)+"", "00", "00", "00");
        		slotNumber = 13;
        		mask = "MMM-yyyy";
        	}
        	String[] labels = null;        	
        	float[][] riskVals = null;
        	String[] barColors = null;
        	String indicatorLabel = "";
        	if (indicator.equals("1")) { //probability
        		riskVals = new float[slotNumber][5];
        		labels = new String[] {this.getI18nMsg("label.formRisk.probability.1"), this.getI18nMsg("label.formRisk.probability.2"), this.getI18nMsg("label.formRisk.probability.3"), this.getI18nMsg("label.formRisk.probability.4"), this.getI18nMsg("label.formRisk.probability.5")};
        		barColors = new String[]{"00FF00", "A9D12E", "FFFF00", "FF9000", "FF0000"};
        		indicatorLabel = super.getI18nMsg("label.formRisk.probability");
        	} else if (indicator.equals("2")) { //impact
        		riskVals = new float[slotNumber][5];
        		labels = new String[] {this.getI18nMsg("label.formRisk.impact.1"), this.getI18nMsg("label.formRisk.impact.2"), this.getI18nMsg("label.formRisk.impact.3"), this.getI18nMsg("label.formRisk.impact.4"), this.getI18nMsg("label.formRisk.impact.5")};
        		barColors = new String[]{"00FF00", "A9D12E", "FFFF00", "FF9000", "FF0000"};
        		indicatorLabel = super.getI18nMsg("label.formRisk.impact");
        	} else if (indicator.equals("3")) { //tendency
        		riskVals = new float[slotNumber][3];
        		labels = new String[] {this.getI18nMsg("label.formRisk.tendency.1"), this.getI18nMsg("label.formRisk.tendency.2"), this.getI18nMsg("label.formRisk.tendency.3")};
        		barColors = new String[]{"A9D12E", "FFFF00", "FF0000"};
        		indicatorLabel = super.getI18nMsg("label.formRisk.tendency");
        	}
            String xaxis[] = this.getXAxisLabel(intervType, slotNumber, iniRange);        	
       		this.defineGranulary(intervType, slotNumber, iniRange, mask);	
        	            
            if (pto!=null) {
            	Vector<RiskTO> rList = rdel.getRiskList(pto.getId());
                if (rList!=null) {
                	for (RiskTO rto : rList) {
                		Vector<RiskHistoryTO> histList = rdel.getHistory(rto.getId());
                		if (histList!=null) {
                			Vector<RiskHistoryTO>milestones = this.populateHistoryInTime(histList, iniRange, mask, incType, slotNumber, intervType);
                			for (RiskHistoryTO milestone : milestones) {
                				
                    			SimpleDateFormat df = new SimpleDateFormat(mask, this.handler.getLocale());			
                    			Integer slot = (Integer)this.granularity.get(df.format(milestone.getCreationDate()));
                    			if (slot!=null) {
                    				int index = slot.intValue();	                        	
    	                        	try {
    	                        		if (index < riskVals.length) {
    	                        			
    	                        			int typeIdx = -1;
    	                        			if (indicator.equals("1") && milestone.getProbability()!=null && !milestone.getProbability().trim().equals("")) {
    	                        				typeIdx = Integer.parseInt(milestone.getProbability()) - 1;
    	                        			} else if (indicator.equals("2") && milestone.getImpact()!=null && !milestone.getImpact().trim().equals("")) {
    	                        				typeIdx = Integer.parseInt(milestone.getImpact()) - 1;
    	                        			} else if (indicator.equals("3") && milestone.getTendency()!=null && !milestone.getTendency().trim().equals("")) {
    	                        				typeIdx = Integer.parseInt(milestone.getTendency()) - 1;
    	                        			}
			                        		if (typeIdx>-1) {
			                        			riskVals[index][typeIdx]++;	
			                        		}
			                        		
    	                        		}
    	                        	} catch(ArrayIndexOutOfBoundsException e) {
    	                        		System.out.println("index:" + index + " slot: " + slot + " dt:" + milestone.getCreationDate() + " gran.list:" + this.granularity);
    	                        	}
                    			}								
							}
                		}
                	}
                }
                
        		//draw the bars
                response = super.getHeader(this.getId(), "column", super.getI18nMsg("label.manageOption.gadget.riskperi") + " - " + indicatorLabel, pto.getName(), false) +
             		   super.getLegend(false) +
             		   super.getXAxis(xaxis, false) +
             		   super.getYAxis(0, super.getI18nMsg("label.qty")) +
             		   super.getTooltip() +
             		   super.getPlot(labels, riskVals, true, barColors) +
             		   super.getFooter();
            }
            
        } catch (Exception e) {
        	e.printStackTrace();
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
					cursor = DateUtil.getChangedDate(iniRange, Calendar.WEEK_OF_YEAR, i);
		    	} else if (intervType.equals("3")) {
		    		cursor = DateUtil.getChangedDate(iniRange, Calendar.MONTH, i);							
		    	}
				df = new SimpleDateFormat(mask, this.handler.getLocale());
				this.granularity.put(df.format(cursor), new Integer(i));			
			}					
		}
	}
	

	private String[] getXAxisLabel(String intervType, int slotNumber, Timestamp iniRange){
		String xaxis[] = new String[slotNumber];

        if (intervType.equals("1")) {
            for (int i=0; i<slotNumber;i++) {
            	DateFormat df = new SimpleDateFormat("dd-MMM", this.handler.getLocale());
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
	
	private Vector<RiskHistoryTO> populateHistoryInTime(Vector<RiskHistoryTO> sourceList, Timestamp iniRange, 
			String mask, int incType, int slotNumber, String intervType){
		Vector<RiskHistoryTO> response = new Vector<RiskHistoryTO>();
		HashMap<String, RiskHistoryTO> hm = new HashMap<String, RiskHistoryTO>();
		Timestamp finishedDate = null;
		
		Timestamp cursor = iniRange;
		Timestamp finalCursor = null;
		finalCursor = DateUtil.getChangedDate(iniRange, incType, slotNumber-1);

		while (!cursor.after(finalCursor)){
			cursor = DateUtil.getChangedDate(cursor, incType, 1);
			
			RiskHistoryTO newMilestone = null;
			for (RiskHistoryTO rhto : sourceList) {
				if (rhto.getRiskStatusType()!=null && 
						!rhto.getRiskStatusType().equals(RiskStatusTO.MATERIALIZE_RISK_TYPE) && 
						!rhto.getRiskStatusType().equals(RiskStatusTO.VOIDED_RISK_TYPE)) {
					
					if (!rhto.getCreationDate().after(cursor)) {
						newMilestone = new RiskHistoryTO();
						newMilestone.setImpact(rhto.getImpact());
						newMilestone.setProbability(rhto.getProbability());
						newMilestone.setTendency(rhto.getTendency());
						newMilestone.setCreationDate(DateUtil.getChangedDate(cursor, incType, -1));
					}				
				} else {
					if (finishedDate==null) {
						finishedDate = DateUtil.getDate(rhto.getCreationDate(), true) ;
					}
				}
			}
			
			if (newMilestone!=null) {

				DateFormat df = null;
		        if (intervType.equals("1")) {
	            	df = new SimpleDateFormat("dd-MMM-yyyy", this.handler.getLocale());	            	
		        } else if (intervType.equals("2")) {
	            	df = new SimpleDateFormat("w-yy", this.handler.getLocale());
		        } else if (intervType.equals("3")) {
	            	df = new SimpleDateFormat("MMM-yyyy", this.handler.getLocale());
		        }				
		        String slotLabel = df.format(newMilestone.getCreationDate());
				
				if (hm.get(slotLabel)==null) {
					hm.put(slotLabel, newMilestone);
					response.add(newMilestone);	
				}
			}			
		}
		
		if (finishedDate!=null) {
			for(int i=response.size()-1; i>0 ;i--) {
				RiskHistoryTO rto = response.get(i);
				if (!rto.getCreationDate().before(finishedDate)) {
					response.remove(i);
				} else {
					break;
				}
			}			
		}
		
		return response;
	}

}
