package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceCapacityTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.UserTO;
import com.pandora.bus.gadget.ChartGadget;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ResourceCapacityDelegate;
import com.pandora.delegate.ResourceTaskAllocDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class ResourceCapacityChart extends ChartGadget {

	Vector vals = new Vector();
	Vector accVals = new Vector();
	Vector allocVals = new Vector();
	Vector labelVals = new Vector();
	Vector listFromDb = null;
	Vector dtValList = new Vector();
	HashMap projectList = null;
	
	public String getUniqueName(){
		return "title.resCapacity";
	}

	public String getId(){
		return "RES_CAPACITY_FORM_CHART";
	}
	
	public String generate(Vector selectedFields)  throws BusinessException {
		StringBuffer response = new StringBuffer();
		ResourceCapacityDelegate rcdel = new ResourceCapacityDelegate();
    	ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
    	ResourceTaskAllocDelegate rtadel = new ResourceTaskAllocDelegate();
		ProjectDelegate pdel = new ProjectDelegate();
		UserDelegate udel = new UserDelegate();
		
		Integer currentCapacity = ResourceTO.DEFAULT_CAPACITY;
		int maxValue = 600;
		String title = "";
		
		Locale loc = this.handler.getLocale();
		String dtmask = this.handler.getCalendarMask();
		String labelLine1 = handler.getBundle().getMessage(loc, "title.resCapacity.line1");
		String labelLine2 = handler.getBundle().getMessage(loc, "title.resCapacity.line2");
		String labelLine3 = handler.getBundle().getMessage(loc, "title.resCapacity.line3");
		
		if (selectedFields!=null && (selectedFields.size()==2 || selectedFields.size()==4)) {
			String resourceId = (String)selectedFields.elementAt(0);
			String projectId = (String)selectedFields.elementAt(1);
			
			Timestamp initialDate = null;
			Timestamp finalDate = null;
			if (selectedFields.size()>2) {
				initialDate = DateUtil.getDateTime((String)selectedFields.elementAt(2), dtmask, loc);
				finalDate = DateUtil.getDateTime((String)selectedFields.elementAt(3), dtmask, loc);
			}
			
			projectList = pdel.getProjectListToHash(true);
			
			ProjectTO pto = (ProjectTO)projectList.get(projectId);			
			if (pto!=null) {
				listFromDb = rcdel.getListByResourceProject(resourceId, null);
				
				UserTO uto = udel.getUser(new UserTO(resourceId));
				title = uto.getName() + " - " + pto.getName(); 
				Timestamp firstDate = null;
				
				ResourceCapacityTO lastValue = null;
				if (listFromDb!=null) {
					Vector allocatedProjects = pdel.getProjectListByUser(uto);					
					ResourceCapacityTO rcto = null;
					
					Iterator i = listFromDb.iterator();
					while(i.hasNext()) {
						rcto = (ResourceCapacityTO)i.next();
						if (rcto.getProjectId().equals(projectId) && 
								(initialDate==null || !initialDate.before(rcto.getDate())) && 
								(finalDate==null || !finalDate.after(rcto.getDate())) ) {
							
							this.generateValues((lastValue==null?pto.getCreationDate():lastValue.getDate()), 
									rcto.getDate(), currentCapacity, rcto.getCapacity(), allocatedProjects, pto, uto);
							currentCapacity = rcto.getCapacity();
							lastValue = new ResourceCapacityTO(rcto);	
							
							if (firstDate==null) {
								firstDate = (lastValue==null?pto.getCreationDate():lastValue.getDate());
							}
						}
					}
					
					if (lastValue!=null) {
						int diffToday = DateUtil.getSlotBetweenDates(lastValue.getDate(), DateUtil.getNow());
						if (diffToday>0) {
							this.generateValues(lastValue.getDate(), DateUtil.getNow(), 
									lastValue.getCapacity(), lastValue.getCapacity(), allocatedProjects, pto, uto);
						}
					}
					
					if (firstDate!=null) {
			        	Vector allocList = rtdel.getListByProject(pto, "-2", uto.getId(), false);
			        	Iterator j = allocList.iterator();
			        	while(j.hasNext()) {
			        		ResourceTaskTO rtto = (ResourceTaskTO)j.next();
			        		Vector allocs = rtadel.getListByResourceTask(rtto);
			        		rtto.setAllocList(allocs);
			        	}
						for (int k=0; k<=vals.size(); k++) {
							Timestamp cursor = DateUtil.getChangedDate(firstDate, Calendar.DATE, (k * 20));
							allocVals.add(new Integer(this.getAllocTasks(cursor, pto, uto, allocList)));
							dtValList.add(DateUtil.getDate(cursor, uto.getCalendarMask(), uto.getLocale()));
						}						
					}
				}
			}
		}
		
		response.append("{ \"elements\": [ ");
		response.append("{ \"type\": \"line\", ");
		response.append("\"values\": [ ");
		
		for (int j=0; j<vals.size(); j++) {
			String val = (String)vals.elementAt(j);
			String labelVal = (String)labelVals.elementAt(j);
			if (j >0) {
				response.append(", ");	
			}
						
			if (!labelVal.trim().equals("")) {
				response.append("{ \"type\": \"hollow-dot\", \"value\": " + val + ", \"colour\": \"#D02020\", \"tip\": \"#x_label# #val#min " + dtValList.elementAt(j) + "'\" }");
			} else {
				response.append(val);	
			}
			
			if (Integer.parseInt(val)>maxValue) {
				maxValue = Integer.parseInt(val);
			}
		}
		
		response.append("], ");
		response.append("\"text\": \"" + labelLine1 + "\", \"font-size\": 10, ");
		response.append("\"dot-style\": { \"type\": \"dot\", \"dot-size\": 3, \"halo-size\": 1}, ");
		response.append("\"colour\": \"#0000CC\" } ");
		
		response.append(", { \"type\": \"line\", ");
		response.append("\"values\": [ ");
		
		for (int j=0; j<vals.size(); j++) {
			if (accVals.size() > j){
				String val = (String)accVals.elementAt(j);
				String labelVal = (String)accVals.elementAt(j);
				if (j >0) {
					response.append(", ");	
				}
							
				if (!labelVal.trim().equals("")) {
					response.append("{ \"type\": \"dot\", \"value\": " + val + " }");				
				} else {
					response.append(val);	
				}
				
				if (Integer.parseInt(val)>maxValue) {
					maxValue = Integer.parseInt(val);
				}				
			}
		}

		response.append("], ");
		response.append("\"text\": \"" + labelLine2 + "\", \"font-size\": 10, ");
		response.append("\"dot-style\": { \"type\": \"dot\", \"dot-size\": 3, \"halo-size\": 1}, ");
		response.append("\"colour\": \"#CC0000\" } ");
		
		response.append(", { \"type\": \"line\", ");
		response.append("\"values\": [ ");
		
		for (int j=0; j<vals.size(); j++) {
			if (allocVals.size() > j){
				Integer val = (Integer)allocVals.elementAt(j);
				if (j >0) {
					response.append(", ");	
				}
				response.append("{ \"type\": \"dot\", \"value\": " + val + " }");				
				if (val.intValue()>maxValue) {
					maxValue = val.intValue();
				}				
			}
		}

		response.append("], ");
		response.append("\"text\": \"" + labelLine3 + "\", \"font-size\": 10, ");
		response.append("\"dot-style\": { \"type\": \"dot\", \"dot-size\": 3, \"halo-size\": 1}, ");
		response.append("\"colour\": \"#00CC00\" } ");
		

		response.append("], ");		
		response.append("\"title\": { \"text\": \"" + title + "\" }, \"bg_colour\": \"#EFEFEF\", ");
		response.append("\"x_axis\": { \"colour\": \"#000000\", \"grid-colour\": \"#C0C0C0\", \"labels\": { \"rotate\": 270, \"colour\": \"#000000\", \"size\": 10, \"labels\": [");
		for (int j=0; j<labelVals.size(); j++) {
			String labelVal = (String)labelVals.elementAt(j);
			if (j >0) {
				response.append(", ");	
			}
			response.append( "\"" + labelVal + "\"");
		}
		response.append("] } }, ");
		//response.append("\"y_axis_right\": { \"colour\": \"#000000\", \"grid-colour\": \"#C0C0C0\", \"labels\": { \"labels\": [ \"Zero\", \"One\", \"Two\", \"Three\", \"Four\", \"Five\", \"Six\", \"Seven\", \"Eight\" ]}}, ");
		response.append("\"y_axis\": { \"min\": 0, \"colour\": \"#000000\", \"grid-colour\": \"#C0C0C0\", \"max\": " + maxValue + ", \"steps\": " + (maxValue>2400?1440:240) + " } }");

		
		return response.toString();
	}

	
	private void generateValues(Timestamp iniDate, Timestamp currentDate, Integer currentCapacity, 
			Integer newCapacity, Vector allocatedProjects, ProjectTO pto, UserTO uto){
		
		int diff = DateUtil.getSlotBetweenDates(iniDate, currentDate);
		for (int j=0; j<=diff; j+=20) {
			labelVals.addElement(" ");
			vals.addElement(currentCapacity.toString());	
			
			Timestamp cursor = DateUtil.getChangedDate(iniDate, Calendar.DATE, j);
			int capacityAcc = this.getAccCapacity(cursor, null, allocatedProjects);
			accVals.add(capacityAcc+"");		
		}
		
		//append the new value of capacity...
		labelVals.remove(labelVals.size()-1);
		vals.remove(vals.size()-1);
		accVals.remove(accVals.size()-1);
		
		String lastSlot = DateUtil.getDate(currentDate, this.handler.getCalendarMask(), this.handler.getLocale());
		labelVals.addElement(lastSlot);
		if (newCapacity!=null) {
			vals.addElement(newCapacity.toString());	
		} else {
			vals.addElement("0");
		}
		int capacityAcc = this.getAccCapacity(currentDate, newCapacity, allocatedProjects);
		accVals.add(capacityAcc+"");
	}
	

    private int getAccCapacity(Timestamp cursor, Integer defaultValue, Vector allocatedProjects) {
    	int response = 0;
    	HashMap listByProject = new HashMap();
    	HashMap allocProj = new HashMap();

    	if (allocatedProjects!=null) {

    		//check which projects are valid..
    		Iterator j = allocatedProjects.iterator();
    		while(j.hasNext()) {
    			ProjectTO prj = (ProjectTO)j.next();
    			if (prj.getRoleIntoProject().equals(ResourceTO.ROLE_RESOURCE+"") || 
    					prj.getRoleIntoProject().equals(LeaderTO.ROLE_LEADER+"")) {
    				allocProj.put(prj.getId(), prj);	
    			}
    		}
    		
    		//check which dates are valid..    	
    		Iterator i = listFromDb.iterator();
    		while(i.hasNext()) {
    			ResourceCapacityTO rcto = (ResourceCapacityTO)i.next();
    			String projId = rcto.getProjectId();
    			ProjectTO pto = (ProjectTO)projectList.get(projId);

    			if (pto.getFinalDate()==null && pto.getBollCanAlloc() && 
    					!rcto.getDate().after(cursor) && allocProj.get(projId)!=null) {
    				
    				ResourceCapacityTO candidate = (ResourceCapacityTO)listByProject.get(projId);
    				if (candidate==null) {
    					listByProject.put(projId, rcto);
    				} else {
    					if (!candidate.getDate().after(rcto.getDate())) {
    						listByProject.remove(projId);
    						listByProject.put(projId, rcto);
    					}
    				}
    			}
    		}

    		//sum all values from the same date
    		Iterator k = listByProject.values().iterator();
    		while(k.hasNext()) {
    			ResourceCapacityTO rcto = (ResourceCapacityTO)k.next();
    			response += rcto.getCapacity().intValue();
    		}    		
    	}
		
    	if (response==0) {
    		response = defaultValue.intValue();
    	}

		return response;
	}

    private int getAllocTasks(Timestamp cursor, ProjectTO pto, UserTO uto, Vector list) {
    	ArrayList<Integer> bucketGroup = new ArrayList<Integer>();
    	int dayAlloc = 0;

    	try {
        	Iterator i = list.iterator();
        	while(i.hasNext()) {
        		ResourceTaskTO rtto = (ResourceTaskTO)i.next();
        		
        		Timestamp refDate = null;
        		if (rtto.getActualDate()!=null) {
        			refDate = rtto.getActualDate();
        		} else {
        			refDate = rtto.getStartDate();
        		}
        		
        		Vector alloc = rtto.getAllocList();
        		if (alloc!=null) {
            		Iterator j = alloc.iterator();
            		while(j.hasNext()) {
            			ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)j.next();
            			Timestamp bucket = DateUtil.getChangedDate(refDate, Calendar.DATE, rtato.getSequence().intValue()-1);
            			if (bucket.after(cursor) || DateUtil.getChangedDate(cursor, Calendar.DATE, -20).after(bucket)) {
            				break;
            			} else {
            				for (int d=0; d<=20; d++){
            					if (DateUtil.getChangedDate(cursor, Calendar.DATE, -d).before(bucket)) {
            						bucketGroup.add(rtato.getAllocTime());
                    				break;
            					}
            				}
            			}       			
            		}        			
        		}
        	}
        	
        	//calculate the average of values...
        	Iterator<Integer> j = bucketGroup.iterator();
        	while(j.hasNext()) {
        		Integer val = j.next();
        		dayAlloc += val.intValue();
        	}
        	if (dayAlloc>0) {
        		dayAlloc = dayAlloc / bucketGroup.size();
        	}
        	
    	} catch(Exception e){
    		e.printStackTrace();
    		dayAlloc = 0;
    	}
    	return dayAlloc;
    }
    
    
	public Vector getFields(){
    	return new Vector();
    }

    public Vector getFieldsId(){
    	return new Vector();
    }
	
}
