package com.pandora.bus.gadget;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.ProjectTO;
import com.pandora.ReportResultTO;
import com.pandora.ReportTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.StringUtil;

public final class KPIHistogramGadgetChart extends ChartGadget {

	private static final String KPI_HISTOGRAM_ID    = "KPI_HISTOGRAM_ID";
	
	private static final String KPI_HISTOGRAM_GROUP = "KPI_HISTOGRAM_GROUP";
	

	public String getUniqueName(){
		return "label.manageOption.gadget.kpiHistogram";
	}
	
	public String getId(){
		return "KPI_HISTOGRAM_CHART";
	}

	@Override	
    public Vector<TransferObject> getFieldsId(){
    	Vector<TransferObject> response = new Vector<TransferObject>();
       	response.add(new TransferObject(KPI_HISTOGRAM_ID, "-1"));
       	response.add(new TransferObject(KPI_HISTOGRAM_GROUP, "0"));
        return response;
    }
	
    public int getPropertyPanelWidth(){
    	return 700;
    }

    public int getPropertyPanelHeight(){
    	return 140;
    }
        
	public String getCategory() {
		return "label.manageOption.gadget.kpi";
	}

	public String getDescription() {
		return "label.manageOption.gadget.kpiHistogram.desc";
	}

	public String getImgLogo() {
		return "../images/gdglogo-13.png";
	}

	public Vector getFields(){
    	Vector response = new Vector();
    	ReportDelegate rdel = new ReportDelegate();
    	HashMap kpiHash = new HashMap();
    	
    	try {
        	Vector kpiList = new Vector();
        	kpiList.addElement(new TransferObject("-1", super.getI18nMsg("label.combo.select")));
        	
         	Vector buff = super.getProjectFromUser(true);
        	if (buff!=null) {
        		for (int i=1; i<buff.size(); i++) {
        			ProjectTO pto = (ProjectTO)buff.elementAt(i);	
                	Vector list = rdel.getListBySource(true, null, pto, false);
                	if (list!=null) {
                    	Iterator j = list.iterator();
                    	while(j.hasNext()) {
                    		ReportTO rto = (ReportTO)j.next();
                    		if (rto.getDataType().equals(ReportTO.FLOAT_DATA_TYPE) || 
                    		        rto.getDataType().equals(ReportTO.CURRENCY_DATA_TYPE)) {
                    		    kpiHash.put(rto.getId(), rto);    
                    		}
                    	}
                	}
            	}
            	
            	Iterator j = kpiHash.values().iterator();
            	while(j.hasNext()) {
            		ReportTO to = (ReportTO)j.next();
            		kpiList.addElement(new TransferObject(to.getId(), to.getProject().getName() + " - " + to.getName()));		
            	}
        	}
        	response.add(new FieldValueTO(KPI_HISTOGRAM_ID, "label.manageOption.gadget.kpiHistogram.kpi", kpiList));

        	response.add(new FieldValueTO(KPI_HISTOGRAM_GROUP, "label.manageOption.gadget.kpiHistogram.group", "", 10, 10));
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		response = null;
    	}
    	
        return response;
    }
	
	public String generate(Vector selectedFields)  throws BusinessException {
		ReportDelegate rdel = new ReportDelegate();
        String response = "";
        String kpiName = "";
        
        try {
        	String kpiId = super.getSelected(KPI_HISTOGRAM_ID, selectedFields);
        	String acc = super.getSelected(KPI_HISTOGRAM_GROUP, selectedFields);
        	
        	int slotNumber = -1;
        	if (acc==null || acc.trim().equals("") || !StringUtil.hasOnlyDigits(acc)){
        		slotNumber = 0;
        	} else {
        		slotNumber = Integer.parseInt(acc);
        	}
        	
        	String lblFrequency = super.getI18nMsg("label.manageOption.gadget.kpiHistogram.frequency", this.handler.getLocale());
        	float[][] histogram = null;
        	
            if (kpiId!=null && !kpiId.equals("-1") && slotNumber>-1) {
            	
            	ReportTO kpi = rdel.getReport(new ReportTO(kpiId));
            	if (kpi!=null) {
                    int[] types = new int[] {Types.VARCHAR};
                    Vector params = new Vector();
                    params.addElement(kpiId);
                    
                    DbQueryDelegate qdel = new DbQueryDelegate();
                    String sql = "select r.name, rr.value, count(rr.value) as c " +
                    		       "from report r, report_result rr " + 
                			      "where r.id = rr.report_id and r.id=? "+ 
                			      "group by r.name, rr.value";
                    Vector dbList = qdel.performQuery(sql, types, params);
                    if (dbList!=null) {
                    	
                    	if (dbList.size()>1) {
                        	Vector firstRow = (Vector)dbList.elementAt(1);
                        	kpiName = (String)firstRow.elementAt(0);                		
                    	}
                    	                	
                    	ArrayList sortedList = this.sortList(dbList);
                    	if (slotNumber==0 || slotNumber>sortedList.size()) {
                    		slotNumber = sortedList.size();
                    	}
                    	
                		histogram = new float[slotNumber][1]; 
                        String xaxis[] = this.getXAxisLabel(sortedList, slotNumber, kpi);
                		float relation = ((float)slotNumber / (float)sortedList.size());
                		
                    	for (int i=0; i<sortedList.size(); i++) {
                        	int index = (int)(i * relation);
                    		TransferObject item = (TransferObject)sortedList.get(i);
                    		if (index>=histogram.length) {
                    			index = histogram.length-1;
                    		}
                    		histogram[index][0]+= Integer.parseInt(item.getGenericTag());
                    	}
                    	
                		//draw the bars
                        response = "{ \n" + 
                        	getJSonTitle(kpiName) + "," +
                        	getJSonYLegend("(" + lblFrequency + ")") + "," + 
                        	getBarValues(histogram) + "," +
                        	getJSonAxis(xaxis, null, "x_axis") + "," + 
                        	getJSonAxis(null, histogram, null, "y_axis", false) +
                        	"}";                	
                    }            	            		
            	}
            }
            
        	//empty chart...
            if (response.equals("")) {
                response = "{ \n" + 
            		getJSonTitle() + "," +
            		getJSonYLegend("(" + lblFrequency + ")") + "}";            	
            }
            
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return response;
	}

	
	private ArrayList sortList(Vector dbList) {
		ArrayList response = new ArrayList();
		for(int i=1; i<dbList.size(); i++) {
			Vector item = (Vector)dbList.elementAt(i);
			String id = (String)item.get(1);
			TransferObject to = new TransferObject(id, (String)item.get(2));
			response.add(to);
		}

		Collections.sort (response, new Comparator() {
		            public int compare(Object o1, Object o2) {  
		            	TransferObject p1 = (TransferObject) o1;  
		            	TransferObject p2 = (TransferObject) o2;
		            	
		            	if (p1!=null && p2!=null) {
		            		float e1 = Float.parseFloat(p1.getId());
		            		float e2 = Float.parseFloat(p2.getId());
		            		return e1 < e2 ? -1 : (e1 > e2 ? 1 : 0);	
		            	} else {
		            		return 0;
		            	}
		             }  
		         }); 		
		return response;
	}
	

	private String[] getXAxisLabel(ArrayList sortedList, int slotNumber, ReportTO kpi) {
		Locale loc = super.handler.getLocale();
		String mask = super.getI18nMsg("calendar.format", loc);
		
		String[] axis = new String[slotNumber];
		int step = (sortedList.size()/slotNumber);		
		for(int i=0; i<slotNumber; i++) {
			TransferObject item = null;
        	if (slotNumber==sortedList.size()) {
        		item = (TransferObject)sortedList.get(i);
        	} else {
        		int index = (step * i) + (step/2);
        		item = (TransferObject)sortedList.get(index);
        	}
        	
        	StringBuffer sb = ReportResultTO.format(loc, mask, kpi.getDataType(), item.getId(), loc);
        	if (sb!=null) {
        		axis[i] = sb.toString();	
        	} else {
        		axis[i] = "-";
        	}
		}
		return axis;
	}
}
