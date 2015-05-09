package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a refuse html image.
 */
public class RequirementGridRefuseDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "&nbsp;";
		
		RequirementTO rto = (RequirementTO)this.getObject();
		RequirementStatusTO rsto = rto.getRequirementStatus();
		Integer state = rsto.getStateMachineOrder();
		
		if (state==null || state.equals(RequirementStatusTO.STATE_MACHINE_WAITING)){
		    String altValue = this.getBundleMessage("label.grid.requestform.refuse");
		    image ="<a href=\"#\" onclick=\"displayMessage('../do/refuse?operation=prepareForm&forwardAfterRefuse=" + tag + "&refuseType=REQ&refusedId=" + columnValue + "', 475, 220);return false;\" border=\"0\"> \n";
			image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/refuse.gif\" >";
			image += "</a>";		    
		}
		return image;    	
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "home");
    } 
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
