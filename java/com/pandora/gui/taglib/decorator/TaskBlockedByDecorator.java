package com.pandora.gui.taglib.decorator;

import java.util.Iterator;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PlanningRelationTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;

public class TaskBlockedByDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
        String content = "";
        GridMindMapLinkDecorator mind = new GridMindMapLinkDecorator();
        
    	try {        
    		Object obj = this.getObject();
    		TaskTO tto = getTask(obj);        
    		if (tto!=null) {
    			Vector list = tto.getRelationList();
    			if (list!=null) {
    		    	Vector relation = PlanningRelationTO.getRelation(list, PlanningRelationTO.RELATION_BLOCKS, tto.getId(), false);
    		    	if (relation!=null) {
    		    		Iterator i = relation.iterator();
    		    		while(i.hasNext()) {
    		    			PlanningRelationTO prto = (PlanningRelationTO)i.next();
    		    			if (prto.getRelated()!=null) {
    		    				if (!content.trim().equals("")) {
    		    					content = content + ", ";
    		    				}
	    		    			content = content + mind.getLink(prto.getPlanning().getId(), null);    		    				
    		    			}
    		    		}
    		    	}
    			}
    		}        
		} catch (Exception e) {
			content = "";
		}
    		
		return content;
	}

	
    private TaskTO getTask(Object obj){
    	TaskTO response = null;
        
        if (obj instanceof TaskTO) {
        	response = (TaskTO)this.getObject();
        } else {
            ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
            response = rtto.getTask();
        }

        return response;
    }
	
	
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return "";
	}
	
}
