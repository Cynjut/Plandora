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
    		if (attribs.length!=2) {
    			attribs = null;
    		}
    	}
    	
        String name = "tx_" + id + "_value";
        String content = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td  class=\"formBody\" nowrap>";
        if (attribs==null) {
        	content = content + HtmlUtil.getTextBox(name, value, false, "");	
        } else {
        	content = content + HtmlUtil.getTextBox(name, value, false, "", Integer.parseInt(attribs[0]), Integer.parseInt(attribs[1]));
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
    
/*    
    private TransferObject getTO(Object obj){
    	TransferObject response = (TransferObject)obj;
    	if (obj instanceof RequirementTO) {
    		response.setId(((RequirementTO)obj).getParentRequirementId());
		}
    	return response;
    }
*/
}
