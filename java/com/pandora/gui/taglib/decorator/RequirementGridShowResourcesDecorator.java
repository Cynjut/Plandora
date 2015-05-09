package com.pandora.gui.taglib.decorator;

import java.util.Iterator;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;

/**
 * This decorator formats a grid cell with a names of resources that is 
 * working for the Requirement.
 */
public class RequirementGridShowResourcesDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String content = "";
		RequirementTO rto = (RequirementTO)this.getObject();
		
		Vector list = rto.getResourceTaskList();
		if (list!=null){
		    
		    Vector filtered = this.filterUniqueResource(list);
		    Iterator i = filtered.iterator();
		    while(i.hasNext()){
		        String name = (String)i.next();
		        if (!content.equals("")){
		            content = content + " / ";
		        }
		        content = content + name;
		    }
		} else {
		    content += "&nbsp;";
		}
		return content;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        return decorate(columnValue);        
    }
    
    
    /**
     * Return a list of resource names based on list of resource task objects
     * @param listS
     * @return
     */
    private Vector filterUniqueResource(Vector list){
        Vector filtered = new Vector();
	    Iterator i = list.iterator();
	    while(i.hasNext()){
	        ResourceTaskTO rtto = (ResourceTaskTO)i.next();
	        boolean found = false;
	        String currName = rtto.getResource().getUsername();
	        Iterator j = filtered.iterator();
	        while(j.hasNext()){
	            String username = (String)j.next();
	            if (username.equals(currName)){
	                found = true;
	            }
	        }
	        if (!found){
	            filtered.addElement(currName);
	        }
	    }
        return filtered;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
