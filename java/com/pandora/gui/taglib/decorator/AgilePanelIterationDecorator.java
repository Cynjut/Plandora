package com.pandora.gui.taglib.decorator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.OccurrenceTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.RequirementHistoryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.AgilePanelForm;

public class AgilePanelIterationDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		Vector iterations = (Vector)this.getPageContext().getSession().getAttribute("iterationList");
		Object obj = this.getPageContext().getSession().getAttribute("agilePanelForm");

		RequirementWithTasksTO rwt = (RequirementWithTasksTO)getObject();
		if (rwt!=null) {
			boolean hideOldIterations = false;
			if (obj!=null) {
				AgilePanelForm frm = (AgilePanelForm)obj;
				hideOldIterations = frm.getHideOldIterations();
			}
			return this.getIterationName(rwt.getId(), rwt.getIteration(), iterations, hideOldIterations);	
		} else {
			return "";
		}
	}

	
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}

	private String getIterationName(String reqId, String id, Vector iterations, boolean hideOldIterations) {
		RequirementHistoryDelegate rhdel = new RequirementHistoryDelegate();
		String response = "";
				
		if (id!=null && iterations!=null && !id.trim().equals("-1")) {
			if (!hideOldIterations) {
				try {
					Vector iterationList = rhdel.getIterationList(reqId);
		        	if (iterationList!=null) {
		        		boolean first = true;
		        		Iterator i = iterationList.iterator();
		        		while(i.hasNext()) {
	            			if (first) {
	            				first = false;
	            			} else {
	            				response = response + ", ";
	            			}        				
		        			String iteration = (String)i.next();
	        				String name = getIterationName(iteration, iterations); 
	        				if (!iteration.equals(id)) {
	        					name = "<strike>" + name + "</strike>";
	        				}
	            			response = response + name;
		        		}
		        	}
				} catch (BusinessException e) {
					e.printStackTrace();
				}
			} else {
				response = this.getIterationName(id, iterations);
			}
		}
		return response;
	}

	private String getIterationName(String id, Vector iterations) {
		String response = "";
		Iterator i = iterations.iterator();
		while(i.hasNext()) {
			OccurrenceTO oto = (OccurrenceTO)i.next();
			if (oto.getId().equals(id)) {
				response = oto.getName();
				break;	
			}					
		}
		return response;
	}

}

