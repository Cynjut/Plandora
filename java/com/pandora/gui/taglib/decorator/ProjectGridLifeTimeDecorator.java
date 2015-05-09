package com.pandora.gui.taglib.decorator;

import java.sql.Timestamp;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.helper.DateUtil;

/**
 * This decorator formats a label displaying the life time of a project.
 */
public class ProjectGridLifeTimeDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		ProjectTO pto = (ProjectTO)this.getObject();
		
		Timestamp iniDate = pto.getCreationDate();
		Timestamp finalDate = null;
		if (pto.getFinalDate()==null){
		    finalDate = DateUtil.getNow();
		} else {
		    finalDate = pto.getFinalDate();
		}
		int betw = DateUtil.getSlotBetweenDates(iniDate, finalDate);
		
	    String timeunit = this.getBundleMessage("label.resHome.project.unitTime");		
		return betw + " " + timeunit;
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
