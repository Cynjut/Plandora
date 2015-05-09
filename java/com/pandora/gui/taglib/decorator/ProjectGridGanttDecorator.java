package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a html image related with Gantt Chart.
 */
public class ProjectGridGanttDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		return decorate(columnValue, "'PRJ'");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "&nbsp;";
	    boolean show = true;
	    
	    if (tag.equals("'REQ'")) {
	        Object obj = getObject();
	        if (obj instanceof RequirementTO) {
	            RequirementTO rto = (RequirementTO)obj;
	            if (rto.getRequirementStatus().getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_WAITING)) {
	                show = false;    
	            }
	        }
	    }
	    
	    if (show) {
		    String altValue = this.getBundleMessage("label.grid.project.gantt");	        
			image ="<a href=\"javascript:showGantt('" + columnValue + "', " + tag + ");\" border=\"0\"> \n";
			image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/gantticon.gif\" >";
			image += "</a>";	        
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
