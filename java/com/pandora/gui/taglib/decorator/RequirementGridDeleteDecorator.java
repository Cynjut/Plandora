package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a delete html image.<br>
 * It works like GridDeleteDecorator (generic decorator) but has a special feature related with Requirement form.
 * If the status of current Requirement is different of "waiting approve" the icon cannot be displayed. 
 */
public class RequirementGridDeleteDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "remove");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "";
		
		UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
		RequirementTO rto = (RequirementTO)this.getObject();
		boolean isWaiting = rto.getRequirementStatus().getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_WAITING);

		if (uto.getId().equals(rto.getRequester().getId()) && isWaiting){
		    String altValue = this.getBundleMessage("label.grid.requestform.remove");
			image ="<a href=\"javascript:remove('" + columnValue + "'," + tag + ");\" border=\"0\"> \n";
			image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/remove.gif\" >";
			image += "</a>";
		} else {
		    image += "&nbsp;";
		}
		return image;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
