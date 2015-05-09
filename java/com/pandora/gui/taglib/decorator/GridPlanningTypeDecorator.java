package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PlanningTO;

public class GridPlanningTypeDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
    	String response = "??";
    	
        if (columnValue!=null){
        	String type = (String)columnValue;
        	
        	if (type.equals(PlanningTO.PLANNING_TASK)) {
        		response = this.getBundleMessage("label.relationTag.entity.1");
        	} else if (type.equals(PlanningTO.PLANNING_REQUIREMENT)) {
        		response = this.getBundleMessage("label.relationTag.entity.2");
        	} else if (type.equals(PlanningTO.PLANNING_PROJECT)) {
        		response = this.getBundleMessage("label.relationTag.entity.3");
        	} else if (type.equals(PlanningTO.PLANNING_OCCURENCE)) {
        		response = this.getBundleMessage("label.relationTag.entity.4");
        	} else if (type.equals(PlanningTO.PLANNING_RISK)) {
        		response = this.getBundleMessage("label.relationTag.entity.5");
        	} else {
        		response = "-" ;
        	}
        }
        return response;
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
