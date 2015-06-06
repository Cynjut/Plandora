package com.pandora.bus.gadget;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.RiskStatusTO;
import com.pandora.RiskTO;
import com.pandora.TransferObject;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RiskDelegate;
import com.pandora.exception.BusinessException;

public class ProjectRiskHighchartGadget extends HighchartGadget {

	private static final String PRJ_RISK_PROJECT   = "PROJECT";
	
	private static final String PRJ_RISK_INDICATOR = "INDICATOR";


	public String getUniqueName(){
		return "label.manageOption.gadget.prjrsk";
	}
	
	public String getId(){
		return "PROJECT_RISK_CHART";
	}
	
	public String getImgLogo() {
		return "../images/gdglogo-5.png";
	}	

	public String getCategory() {
		return "label.manageOption.gadget.management";
	}
	
	public String getDescription() {
		return "label.manageOption.gadget.prjrsk.desc";
	}
	
	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(PRJ_RISK_PROJECT, "-1"));
       	response.add(new TransferObject(PRJ_RISK_INDICATOR, "1"));
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
        	Vector<TransferObject> prjlist = super.getProjectFromUser(true);
        	response.add(new FieldValueTO(PRJ_RISK_PROJECT, "label.manageOption.gadget.prjrsk.project", prjlist));
     
            Vector<TransferObject> indicatorList = new Vector<TransferObject>();
            indicatorList.add(new TransferObject("1", "label.formRisk.probability"));
            indicatorList.add(new TransferObject("2", "label.formRisk.impact"));
            indicatorList.add(new TransferObject("3", "label.formRisk.tendency"));
        	response.add(new FieldValueTO(PRJ_RISK_INDICATOR, "label.manageOption.gadget.prjrsk.indicator", indicatorList));

    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}	
        return response;
    }
	
	public String generate(Vector selectedFields)  throws BusinessException {        			
        String response = "";
    	RiskDelegate rdel = new RiskDelegate();
    	ProjectDelegate pdel = new ProjectDelegate();
        try {
        	String projectId = super.getSelected(PRJ_RISK_PROJECT, selectedFields);
        	String indicatorId = super.getSelected(PRJ_RISK_INDICATOR, selectedFields);
        	
        	ProjectTO pto = new ProjectTO(projectId);
        	pto = pdel.getProjectObject(pto, true);
        	
        	if (pto!=null && pto.getId()!=null) {
            	Vector<RiskTO> rList = rdel.getRiskList(pto.getId());
            	
            	Vector<RiskTO> tList = this.getSubList(rList, true);
            	Vector<RiskTO> oList = this.getSubList(rList, false);
            	
            	int size = 1;
            	if (tList.size() > size) size = tList.size();
            	if (oList.size() > size) size = oList.size();

            	String[] labels = new String[size];
                float[][] val = new float[size][2];
                String[] axis = this.getIndicatorLabels(indicatorId);
                
                this.initArray(val);

                String[] name = new String[2];
                name[0] = super.getI18nMsg("label.formRisk.type.0");
                name[1] = super.getI18nMsg("label.formRisk.type.1");
                
                String title = super.getI18nMsg(getUniqueName()) + " - ";
            	if (indicatorId.equals("1")) {
            		title = title + super.getI18nMsg("label.formRisk.probability");
            	} else if (indicatorId.equals("2")) {
            		title = title +  super.getI18nMsg("label.formRisk.impact");
            	} else {
            		title = title +  super.getI18nMsg("label.formRisk.tendency");
            	}
                
            	if (tList!=null) { //threat
            		this.fillInArray(tList, val, labels, indicatorId, 0);
            	}
            	if (oList!=null) { //opportunity
            		this.fillInArray(oList, val, labels, indicatorId, 1);
            	}

        		//draw the chart
                response = super.getHeader(this.getId(), "area", title, pto.getName(), true) +
             		   super.getLegend(false) +
             		   super.getXAxis(labels, true) +
             		   super.getYAxis(0, null) +
             		   //super.getTooltip() +
             		   "tooltip: {shared: true, pointFormat: '<span style=\"color:{series.color}\">{series.name}: <b>${point.y:,.0f}</b><br/>'}," +
             		   super.getSeries(name, val, null, true) +
             		   super.getFooter();
        	}

        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        
        return response;
	}


	private String[] getIndicatorLabels(String indicatorId) {
		String[] response = null;
		if (indicatorId!=null) {
			if (indicatorId.equals("1")) {
				response = new String[5];
				response[0] = super.getI18nMsg("label.formRisk.probability.1");
				response[1] = super.getI18nMsg("label.formRisk.probability.2");
				response[2] = super.getI18nMsg("label.formRisk.probability.3");
				response[3] = super.getI18nMsg("label.formRisk.probability.4");
				response[4] = super.getI18nMsg("label.formRisk.probability.5");
				
			} else if (indicatorId.equals("2")) {
				response = new String[5];
				response[0] = super.getI18nMsg("label.formRisk.impact.1");
				response[1] = super.getI18nMsg("label.formRisk.impact.2");
				response[2] = super.getI18nMsg("label.formRisk.impact.3");
				response[3] = super.getI18nMsg("label.formRisk.impact.4");
				response[4] = super.getI18nMsg("label.formRisk.impact.5");
				
			} else {
				response = new String[3];
				response[0] = super.getI18nMsg("label.formRisk.tendency.1");
				response[1] = super.getI18nMsg("label.formRisk.tendency.2");
				response[2] = super.getI18nMsg("label.formRisk.tendency.3");
			}
		}
		return response;
	}

	
	private Vector<RiskTO> getSubList(Vector<RiskTO> list, boolean isThreat) {
		Vector<RiskTO> response = new Vector<RiskTO>();
		
        if (list!=null) {
            Iterator<RiskTO> i = list.iterator();
            while(i.hasNext()) {
            	
            	RiskTO rto = i.next();
            	RiskStatusTO rsto = rto.getStatus();
            	
            	if (rsto.getStatusType()!=null && !rsto.getStatusType().equals(RiskStatusTO.VOIDED_RISK_TYPE) 
            			&& !rsto.getStatusType().equals(RiskStatusTO.MATERIALIZE_RISK_TYPE)) {
                	boolean typeThreat = (rto.getRiskType()==null || rto.getRiskType().equals(RiskTO.RISK_TYPE_THREAT));
                	if ((isThreat && typeThreat) || (!isThreat && !typeThreat)) {
                		response.add(rto);
                	}            		
            	}
            }
        }
		return response;
	}	
	
	private void fillInArray(Vector<RiskTO> list, float[][] val, String[] labels, String indicatorId, int typeIdx){
		
        if (list!=null) {
            Iterator<RiskTO> i = list.iterator();
            int c = 0;
            while(i.hasNext()) {
            	RiskTO rto = i.next();
            	if (indicatorId.equals("1")) {
            		val[c][typeIdx] = Float.parseFloat(rto.getProbability());
            	} else if (indicatorId.equals("2")) {
            		val[c][typeIdx] = Float.parseFloat(rto.getImpact());                		
            	} else {
            		val[c][typeIdx] = Float.parseFloat(rto.getTendency());
            	}
            	labels[(list.size()*typeIdx) + c] = rto.getName();
            	c++;
            }
        }                    
	}
	
	private void initArray(float[][] val){
		for (int i=0; i<val.length; i++) {
			for (int j=0; j<val[i].length; j++) {
				val[i][j]=0;
			}
		}
	}

}
