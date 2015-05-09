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

public final class ProjectRiskChartGadget extends ChartGadget {

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
		return 170;
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
            	
            	Vector tList = this.getSubList(rList, true);
            	Vector oList = this.getSubList(rList, false);
            	
            	int size = 1;
            	if (tList.size() > size) size = tList.size();
            	if (oList.size() > size) size = oList.size();

            	String[][] labels = new String[2][size];
                float[][] val = new float[2][size];
                String[] axis = this.getIndicatorLabels(indicatorId);
                
                this.initArray(val);

                
                String[] name = new String[2];
                name[0] = super.getI18nMsg("label.formRisk.type.0");
                name[1] = super.getI18nMsg("label.formRisk.type.1");
                
                String title = pto.getName() + " - ";
            	if (indicatorId.equals("1")) {
            		title = title + super.getI18nMsg("label.formRisk.probability");
            	} else if (indicatorId.equals("2")) {
            		title = title +  super.getI18nMsg("label.formRisk.impact");
            	} else {
            		title = title +  super.getI18nMsg("label.formRisk.tendency");
            	}
                
            	if (tList!=null) {
            		this.fillInArray(tList, val, labels, indicatorId, 0);
            	}
            	if (oList!=null) {
            		this.fillInArray(oList, val, labels, indicatorId, 1);
            	}

        		//draw the chart
                if (rList!=null && (val[0].length>0 || val[1].length>0)) {
                	response = "{ \n" + 
                        		getJSonTitle(title) + ", " +
                        		getRadarValues(val, labels, name) + ", " +
                        		getJSonAxis(axis, null, "radar_axis") + "}";
                }        		
        	}

            //...or draw an empty chart...            
            if (response.equals("")) {
                response = "{ \n" + getJSonTitle() + "}";            	
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
				response = new String[6];
				response[0] = "";
				response[1] = super.getI18nMsg("label.formRisk.probability.1");
				response[2] = super.getI18nMsg("label.formRisk.probability.2");
				response[3] = super.getI18nMsg("label.formRisk.probability.3");
				response[4] = super.getI18nMsg("label.formRisk.probability.4");
				response[5] = super.getI18nMsg("label.formRisk.probability.5");
				
			} else if (indicatorId.equals("2")) {
				response = new String[6];
				response[0] = "";
				response[1] = super.getI18nMsg("label.formRisk.impact.1");
				response[2] = super.getI18nMsg("label.formRisk.impact.2");
				response[3] = super.getI18nMsg("label.formRisk.impact.3");
				response[4] = super.getI18nMsg("label.formRisk.impact.4");
				response[5] = super.getI18nMsg("label.formRisk.impact.5");
				
			} else {
				response = new String[4];
				response[0] = "";
				response[1] = super.getI18nMsg("label.formRisk.tendency.1");
				response[2] = super.getI18nMsg("label.formRisk.tendency.2");
				response[3] = super.getI18nMsg("label.formRisk.tendency.3");
			}
		}
		return response;
	}

	
	private Vector getSubList(Vector list, boolean isThreat) {
		Vector response = new Vector();
		
        if (list!=null) {
            Iterator i = list.iterator();
            while(i.hasNext()) {
            	
            	RiskTO rto = (RiskTO)i.next();
            	RiskStatusTO rsto = rto.getStatus();
            	
            	if (rsto.getStatusType()!=null && !rsto.getStatusType().equals(RiskStatusTO.VOIDED_RISK_TYPE)) {
                	boolean typeThreat = (rto.getRiskType()==null || rto.getRiskType().equals(RiskTO.RISK_TYPE_THREAT));
                	if ((isThreat && typeThreat) || (!isThreat && !typeThreat)) {
                		response.add(rto);
                	}            		
            	}
            }
        }
		return response;
	}	
	
	private void fillInArray(Vector list, float[][] val, String[][] labels, String indicatorId, int typeIdx){
		
        if (list!=null) {
            Iterator i = list.iterator();
            int c = 0;
            while(i.hasNext()) {
            	RiskTO rto = (RiskTO)i.next();
            	if (indicatorId.equals("1")) {
            		val[typeIdx][c] = Float.parseFloat(rto.getProbability());
            	} else if (indicatorId.equals("2")) {
            		val[typeIdx][c] = Float.parseFloat(rto.getImpact());                		
            	} else {
            		val[typeIdx][c] = Float.parseFloat(rto.getTendency());
            	}
            	labels[typeIdx][c] = rto.getName();
            	c++;
            }
        }                    
	}
	
	private void initArray(float[][] val){
		for (int i=0; i<val.length; i++) {
			for (int j=0; j<val[i].length; j++) {
				val[i][j]=-1;
			}
		}
	}
}
