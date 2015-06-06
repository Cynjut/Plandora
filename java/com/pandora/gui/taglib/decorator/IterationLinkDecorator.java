package com.pandora.gui.taglib.decorator;

import java.util.Iterator;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.OccurrenceTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.HtmlUtil;

public class IterationLinkDecorator extends ColumnDecorator {


    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */	
	public String decorate(Object columnValue) {
		return decorate(columnValue, "read_only");
	}


    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */	
	public String decorate(Object columnValue, String tag) {
        String content = "";

    	try {        
    		Object obj = this.getObject();
    		
    		if (tag.equals("req_hist")) {
    			RequirementHistoryTO rto = (RequirementHistoryTO)obj;
    			if (rto!=null) {
    				content = getIterationName(rto.getIteration());
    			}
    		} else if (tag.equals("req_only")) {
    			RequirementTO rto = (RequirementTO)obj;
    			if (rto!=null) {
    				content = getIterationName(rto.getIteration());
    			}
    			
			} else {
				
	    		TaskTO tto = getTask(obj);
	    		if (tto!=null) {
	    			
	    			if (tag.equals("read_only")) {
    					content = getIterationName(tto.getIteration());
	    				
	    			} else if (tag.equals("req")) {
	    				
	    				RequirementTO rto = tto.getRequirement();
	    				if (rto!=null) {
	    					content = getIterationName(rto.getIteration());
	    				}
	    				
	    			} else {
	    				
	        			Vector values = (Vector)this.getSession().getAttribute("iterationList");
	        			content = HtmlUtil.getComboBox("cb_" + tto.getId() + "_iteration", getIterationList(values), "textBox", tto.getIteration());
	    			}
	    		}				
			}
    		
		} catch (Exception e) {
			content = "";
		}
		
		return content;
	}


	public String contentToSearching(Object columnValue) {
		return decorate(columnValue);
	}

	
	private String getIterationName(String iterationId) throws BusinessException{
		String response = "";
		if (iterationId!=null && !iterationId.trim().equals("")) {
			OccurrenceDelegate occDel = new OccurrenceDelegate();		
			OccurrenceTO octo = occDel.getOccurrenceObject(new OccurrenceTO(iterationId));
			if (octo!=null) {
				response = octo.getName();	
			}						
		}
		return response;
	}
	
	
	
    private TaskTO getTask(Object obj){
    	TaskTO tto = null;
        if (obj instanceof TaskTO) {
            tto = (TaskTO)this.getObject();
        } else if (obj instanceof ResourceTaskTO) {
            ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
            tto = rtto.getTask();
        }
        return tto;
    }
		
    
	private Vector<OccurrenceTO> getIterationList(Vector<OccurrenceTO> iterations){
		Vector<OccurrenceTO> response = new Vector<OccurrenceTO>();
		
		OccurrenceTO oto = new OccurrenceTO("-1");
		oto.setGenericTag("");
		response.addElement(oto);
		
		if (iterations!=null) {
			Iterator<OccurrenceTO> i = iterations.iterator();
			while(i.hasNext()) {
				OccurrenceTO o = i.next();
				o.setGenericTag(o.getName());
				response.add(o);
			}			
		}
		return response;
	}
    
}
