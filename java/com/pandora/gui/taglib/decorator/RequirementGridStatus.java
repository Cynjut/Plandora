package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;

/**
 * This decorator set the bold format for requirement status if Requirement
 * follow the specific business rules:
 * <li>contain a refused status</li>
 * <li>contain a calceled status</li>
 */
public class RequirementGridStatus extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        String content = "&nbsp;";
		RequirementTO rto = (RequirementTO)this.getObject();
		RequirementStatusTO rsto = rto.getRequirementStatus();
		
		if (rsto.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_CANCEL) ||
		        rsto.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_REFUSE) ) {
			content += "<font color=\"#BB0000\"><B>" + rsto.getName() + "</B></font>";
		} else if (rsto.getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_CLOSE)) {
			content += "<font color=\"#OO8800\"><B>" + rsto.getName() + "</B></font>";		
		} else {
			content = rsto.getName();		    
		}
		return content;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        return decorate(columnValue);
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
