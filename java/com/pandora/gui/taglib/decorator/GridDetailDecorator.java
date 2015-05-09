package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ResourceTaskTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a detailed html image.<br>
 * It uses the information from table to assembly the a javascript call.<br>
 */
public class GridDetailDecorator extends ColumnDecorator {
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "REQ");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "";
		String func = "";
		
		if (tag.equals("'REQ'")){
		    func = "openPopup";
		    
		} else if (tag.equals("'TSK'")){
		    func = "openTaskHistPopup";
		    
		} else if (tag.equals("'RES_TSK'")){
		    func = "openResTaskHistPopup";
		    ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
		    columnValue = columnValue + "','" + rtto.getResource().getId();
		    
		} else if (tag.equals("'RSK'")){
		    func = "openRiskHistPopup";
		    
		} else if (tag.equals("'OCC'")){
		    func = "openOccHistPopup";
		    
		} else if (tag.equals("'INV'")){
		    func = "openInvHistPopup";
		}
		
		String altValue = this.getBundleMessage("label.grid.detail");		
		image ="<a href=\"javascript:" + func + "('" + columnValue + "');\" border=\"0\"> \n";
		image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/detailed.gif\" >";
		image += "</a>"; 				
        return image;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
