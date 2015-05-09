package com.pandora.bus.gadget;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.ReportResultTO;
import com.pandora.ReportTO;
import com.pandora.TransferObject;
import com.pandora.delegate.ReportDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

public class KpiChartGadget extends ChartGadget {

	private static final String KPI_PROJECT    = "KPI_PROJECT";
	
	private static final String KPI_ID         = "KPI_ID";	
	
	private static final String KPI_INTEVAL    = "KPI_INTERVAL";

	private static final String KPI_GROUP      = "KPI_GROUP";
	
	private static final String KPI_SHOW_GOAL  = "KPI_SHOW_GOAL";
	
	private static final String KPI_COMP_ID    = "KPI_COMP_ID";
	
	
	public String getUniqueName(){
		return "label.manageOption.gadget.kpichart";
	}
	
	
	public String getDescription() {
		return "label.manageOption.gadget.kpichart.desc";
	}
	
	public String getId(){
		return "PROJECT_KPI_CHART";
	}

	public String getCategory() {
		return "label.manageOption.gadget.kpi";
	}
    
	public String getImgLogo() {
		return "../images/gdglogo-2.png";
	}
	
	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(KPI_ID, "-1"));
       	response.add(new TransferObject(KPI_INTEVAL, "1"));
       	response.add(new TransferObject(KPI_GROUP, "1"));
       	response.add(new TransferObject(KPI_SHOW_GOAL, "1"));
       	response.add(new TransferObject(KPI_COMP_ID, "-1"));
        return response;
    }

    
    public int getPropertyPanelWidth(){
    	return 700;
    }

    public int getPropertyPanelHeight(){
    	return 215;
    }
	
	@Override
    public Vector getFields(){
		return getFields(null);
    }

	
	@Override
	public boolean canReloadFields() {
		return true;
	}
	
	
	@Override
	public Vector<FieldValueTO> getFields(Vector<TransferObject> currentValues) {
    	Vector<FieldValueTO> response = new Vector<FieldValueTO>();
    	ReportDelegate rdel = new ReportDelegate();
    	
    	try {
    		
        	Vector<TransferObject> projList = super.getProjectFromUser(false);
        	response.add(new FieldValueTO(KPI_PROJECT, "label.manageOption.gadget.kpichart.project", projList));
    		String projId = super.getSelected(KPI_PROJECT, currentValues);
    		
        	Vector<TransferObject> kpiList = new Vector<TransferObject>();
        	kpiList.addElement(new TransferObject("-1", super.getI18nMsg("label.combo.select")));
        	
        	if (projId!=null && !projId.equals("")) {
        		for (int i=1; i<projList.size(); i++) {
        			ProjectTO pto = (ProjectTO)projList.elementAt(i);
        			if (pto.getId().equals(projId)) {
                    	Vector<ReportTO> list = rdel.getListBySource(true, null, pto, false);
                    	if (list!=null) {
                        	Iterator<ReportTO> j = list.iterator();
                        	while(j.hasNext()) {
                        		ReportTO rto = j.next();
                        		if (rto.getDataType().equals(ReportTO.FLOAT_DATA_TYPE) || 
                        		        rto.getDataType().equals(ReportTO.CURRENCY_DATA_TYPE)) {
                        			kpiList.addElement(new TransferObject(rto.getId(), rto.getName()));   
                        		}
                        	}
                    	}  
                    	break;
        			}
            	}
        	}
        	response.add(new FieldValueTO(KPI_ID, "label.manageOption.gadget.kpichart.kpi", kpiList));

            Vector<TransferObject> intervList = new Vector<TransferObject>();
            intervList.add(new TransferObject("1", "label.manageOption.gadget.kpichart.interval.1"));
            intervList.add(new TransferObject("2", "label.manageOption.gadget.kpichart.interval.2"));
            intervList.add(new TransferObject("3", "label.manageOption.gadget.kpichart.interval.3"));
        	response.add(new FieldValueTO(KPI_INTEVAL, "label.manageOption.gadget.kpichart.interval", intervList));

            Vector<TransferObject> groupList = new Vector<TransferObject>();
            groupList.add(new TransferObject("1", "label.manageOption.gadget.kpichart.group.1"));
            groupList.add(new TransferObject("2", "label.manageOption.gadget.kpichart.group.2"));
            groupList.add(new TransferObject("3", "label.manageOption.gadget.kpichart.group.3"));
            groupList.add(new TransferObject("4", "label.manageOption.gadget.kpichart.group.4"));
        	response.add(new FieldValueTO(KPI_GROUP, "label.manageOption.gadget.kpichart.group", groupList));

            Vector<TransferObject> showGoalList = new Vector<TransferObject>();
            showGoalList.add(new TransferObject("0", "label.no"));            
            showGoalList.add(new TransferObject("1", "label.yes"));
        	response.add(new FieldValueTO(KPI_SHOW_GOAL, "label.manageOption.gadget.kpichart.showgoal", showGoalList));

        	response.add(new FieldValueTO(KPI_COMP_ID, "label.manageOption.gadget.kpichart.compare", kpiList));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
    }


	public String generate(Vector selectedFields) throws BusinessException {
        String response = "";
        ReportDelegate del = new ReportDelegate();
        String xaxis[] = null;
        float[][] valBar = null;
        float[][] goalVals = null;
        ReportTO rtoComp = null;
        String[] goalLbl = new String[1];
        Vector compValues = null;
        
        String kpiId = super.getSelected(KPI_ID, selectedFields);
    	
    	ReportTO rto = del.getReport(new ReportTO(kpiId));    	
        if (rto!=null && 
                (rto.getDataType().equals(ReportTO.FLOAT_DATA_TYPE) 
                        || rto.getDataType().equals(ReportTO.CURRENCY_DATA_TYPE))) {
            
        	String intervType = super.getSelected(KPI_INTEVAL, selectedFields);
        	if (intervType==null || intervType.equals("")) {
        	    intervType = "1";
        	}
        	
        	String groupBy = super.getSelected(KPI_GROUP, selectedFields);
        	if (groupBy==null || groupBy.equals("")) {
        	    intervType = "1";
        	}
        	
        	String showGoalStr = super.getSelected(KPI_SHOW_GOAL, selectedFields);
        	boolean showGoal = (showGoalStr!=null && showGoalStr.equals("1"));
        	
        	String kpiCompareId = super.getSelected(KPI_COMP_ID, selectedFields);
        	if (kpiCompareId!=null && !kpiCompareId.equals("-1")) {
        		rtoComp = del.getReport(new ReportTO(kpiCompareId));	
        	}            
            
        	Timestamp finalDbDate = DateUtil.getNow();
        	Timestamp initalDbDate = null;
        	int slotNumber = 30;
        	
        	if (intervType.equals("1")) {
        		initalDbDate = DateUtil.getChangedDate(finalDbDate, Calendar.DATE, -30);
		    	xaxis = new String[slotNumber];
                for (int i=0; i<slotNumber;i++) {
                	DateFormat df = new SimpleDateFormat("d-MM", this.handler.getLocale());
                	String slotLabel = df.format(DateUtil.getChangedDate(initalDbDate, Calendar.DATE, i+1));
                	xaxis[i] = slotLabel;			    		
                }
        		
        	} else if (intervType.equals("2")) {
        		initalDbDate = DateUtil.getChangedDate(finalDbDate, Calendar.DATE, -180);
        		slotNumber = 26; 
        		xaxis = new String[slotNumber];
                for (int i=0; i<slotNumber;i++) {
                	DateFormat df = new SimpleDateFormat("w-yy", this.handler.getLocale());
                	String slotLabel = df.format(DateUtil.getChangedDate(initalDbDate, Calendar.WEEK_OF_YEAR, i+1));
                	xaxis[i] = slotLabel;			    		
                }
        		
        	} else if (intervType.equals("3")) {
        		initalDbDate = DateUtil.getChangedDate(finalDbDate, Calendar.DATE, -365);
        		slotNumber = 12; 
        		xaxis = new String[slotNumber];
                for (int i=0; i<slotNumber;i++) {
                	DateFormat df = new SimpleDateFormat("MMM-yy", this.handler.getLocale());
                	String slotLabel = df.format(DateUtil.getChangedDate(initalDbDate, Calendar.MONTH, i+1));
                	xaxis[i] = slotLabel;			    		
                }
        	}        	
        	
        	if (rtoComp!=null) {
    		    del.getKpiValues(initalDbDate, finalDbDate, rtoComp);
    		    compValues = rtoComp.getResultList();
        	}
        	
		    del.getKpiValues(initalDbDate, finalDbDate, rto);
		    Vector values = rto.getResultList();
		    if (values!=null) {
		    	Vector[] compValList = null;

		    	Vector[] valList = this.getValues(intervType, initalDbDate, values, slotNumber);
		    	if (compValues!=null) {
		    		valBar = new float[slotNumber][2];	
		    		compValList = this.getValues(intervType, initalDbDate, compValues, slotNumber);
		    	} else {
		    		valBar = new float[slotNumber][1];		    		
		    	}
	    		
		    	if (showGoal) {
		        	goalLbl[0] = this.getI18nMsg("label.manageReport.goal");		    		
		    		goalVals =  this.getGoal(rto, slotNumber);
		    	}
		    	
                this.setIntoList(valBar, groupBy, valList, 0);
		    	if (compValues!=null) {
		    		this.setIntoList(valBar, groupBy, compValList, 1);	
		    	}
                
            }

    		//draw the bars
            response = "{ \n" + 
            	getJSonTitle(rto.getName()) + "," +
            	getJSonYLegend(" ") + "," +
            	getBarStackValues(valBar, null, goalVals, goalLbl, new String[]{"800000"}, false) + "," +            	
            	getJSonAxis(xaxis, null, "x_axis") + "," + getJSonAxis(null, valBar, "y_axis") + "}";            
            
        } else {

        	//empty chart...
            response = "{ \n" + 
            	getJSonTitle() + "," +
            	getJSonAxis(new String[1], null, "x_axis") + "," +
            	getJSonYLegend(" ") + "}";        	
        }
        
        return response;
	}


	private void setIntoList(float[][] valBar, String groupBy, Vector[] list, int idx) {
		for (int j=0; j<list.length; j++ ) {
			Vector item = list[j];
			if (item!=null && item.size()>0) {
				
				if (groupBy.equals("1") || groupBy.equals("2")) { //sum and average calc
			    	Iterator k = item.iterator();
			    	
			    	while(k.hasNext()) {
			    		Float val = (Float)k.next();
			        	if (groupBy.equals("1") || groupBy.equals("2")) {
			        		valBar[j][idx] = valBar[j][idx] + val.floatValue();	
			        	}
			    	}
			    	
			    	if (groupBy.equals("2")) {
			    		valBar[j][idx] = valBar[j][idx] / item.size();	
			    	}
			    	
				} else if (groupBy.equals("3")) { //later value
					Float val = (Float)item.get(item.size()-1);
					valBar[j][idx] = val.floatValue();
				} else if (groupBy.equals("4")) { //earlier value
					Float val = (Float)item.get(0);
					valBar[j][idx] = val.floatValue();
				}
			}
		}
	}


	private Vector[] getValues(String intervType, Timestamp initalDbDate, Vector values, int slotNumber) {
    	Vector[] response = new Vector[slotNumber];
    	
		Iterator i = values.iterator();
		while(i.hasNext()) {
			ReportResultTO result = (ReportResultTO)i.next();
			if (result!=null && result.getValue()!=null) {

		    	int index = 0;
		    	if (intervType.equals("1")) {
		    		index = DateUtil.getSlotBetweenDates(initalDbDate, result.getLastExecution());
		    		
		    	} else if (intervType.equals("2")) {
		        	DateFormat df = new SimpleDateFormat("w", this.handler.getLocale());
		        	String weekinitial = df.format(initalDbDate);
		        	String weekcursor = df.format(result.getLastExecution());
		        	int iniYear = DateUtil.get(initalDbDate, Calendar.YEAR);
		        	int cursorYear = DateUtil.get(result.getLastExecution(), Calendar.YEAR);
		        	index = (((cursorYear - iniYear) * 52) + Integer.parseInt(weekcursor) - Integer.parseInt(weekinitial))-1;
				
		    	} else if (intervType.equals("3")) {
		        	int iniMonth = DateUtil.get(initalDbDate, Calendar.MONTH)+1;
		        	int cursorMonth = DateUtil.get(result.getLastExecution(), Calendar.MONTH)+1;
		        	int iniYear = DateUtil.get(initalDbDate, Calendar.YEAR);
		        	int cursorYear = DateUtil.get(result.getLastExecution(), Calendar.YEAR);
		        	index = (((cursorYear - iniYear) * 12) + (cursorMonth - iniMonth))-1; 
		    	}

		    	if (index >= 0 && response.length>index) {
		    		if (response[index]==null) {
		    			response[index] = new Vector();
		    		}
		    		response[index].addElement(new Float(result.getValue()));
		    	}
			}   	
		}
		
		return response;
	}
	
	
	private float[][] getGoal(ReportTO rto, int size){
	    float[][] goalVals = null;
		float value = 0;

		Integer dataType = rto.getDataType();
		if (rto.getGoal()!=null && 
		        (dataType.equals(ReportTO.FLOAT_DATA_TYPE) || dataType.equals(ReportTO.CURRENCY_DATA_TYPE) )) {
		    String val = rto.getGoal();
		    value = StringUtil.getStringToFloat(val, ReportTO.KPI_DEFAULT_LOCALE);
		}
		
		goalVals = new float[size][1];
		for (int i=0; i<size; i++) {
		    goalVals[i][0] = value;
		}
		
		return goalVals;
	}	
}
