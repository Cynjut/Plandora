package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.TransferObject;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a generic textBox into grid cell.
 */
public class GridTextBoxDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String response = "&nbsp;";
		TransferObject to = (TransferObject)this.getObject();
		if (columnValue==null) {
			columnValue = "";	
		}
        response = this.getTextBox(to.getId(), columnValue+"", tag);
		return response;
    }
    
    /**
     * Return a CheckBox html.
     */
    private String getTextBox(String id, String value, String tag){

    	String[] attribs = null;
    	if (tag!=null && !tag.trim().equals("")) {
    		attribs = tag.split(";");
    		if (attribs.length!=3) {
    			attribs = null;
    		}
    	}
    	
        String name = "tx_" + id + "_value";
        String content = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td  class=\"gridLink\" nowrap>";
        if (attribs==null) {
        	content = content + HtmlUtil.getTextBox(name, value, false, "");	
        } else {
        	String txtBox = HtmlUtil.getTextBox(name, value, false, "", Integer.parseInt(attribs[0]), Integer.parseInt(attribs[1]));
        	
        	String mindMapLink = "";
        	if (!attribs[2].trim().equals("") && value!=null && !value.trim().equals("")) {
        		String hint = this.getBundleMessage("title.mindMapForm");
        		mindMapLink = "&nbsp;<a class=\"gridLink\" title=\"" + hint + "\" alt=\"" + hint + 
        				"\" href=\"javascript:loadMindMap('" + value + "');\" border=\"0\"><img valign=\"center\" src=\"../images/relation.gif\" border=\"0\"/></a>";    		
        	}
        	
        	content = content + txtBox + mindMapLink;
        }
        
        content = content + "</td></tr></table>";
        
        return content;
    }
 
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }    
}
