package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.helper.HtmlUtil;

public class GridMindMapLinkDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
    	return decorate(columnValue, null);
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		return this.getLink(columnValue+"", tag);
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
        return columnValue + "";
    }
    
    public String getLink(String columnValue, String tag){
    	String link = ""; 	    	
    	String tip = null;
    	
    	if (tag!=null) {
        	if (tag.equals("PARENT_ID")) {
        		columnValue = null;
        		ResourceTaskTO rtto = (ResourceTaskTO)getObject();
        		if (rtto.getTask()!=null && rtto.getTask().getParentTask()!=null) {
        			columnValue = rtto.getTask().getParentTask().getId();
        		}
        	} else if (tag.equals("REQ_ID")) {

            	Object obj = getObject();
            	if (obj instanceof ResourceTaskTO) {
            		TaskTO tto = ((ResourceTaskTO)obj).getTask();
            		if (tto!=null && tto.getRequirement()!=null) {
            			tip = tto.getRequirement().getDescription();
            		}
            	}
        	}
    	}    	
    	
    	if (tip!=null) {
    		tip = HtmlUtil.getHint(tip);	
    	} else {
    		tip = "";
    	}
    	
    	if (columnValue!=null && !columnValue.trim().equals("") && !columnValue.equalsIgnoreCase("null")) {
    		link =  "<a class=\"gridLink\" " + tip + " href=\"javascript:loadMindMap('" + columnValue + "');\" border=\"0\"> \n";
    		link += columnValue;
    		link += "</a>";    		
    	}
		return link;
    }
}
