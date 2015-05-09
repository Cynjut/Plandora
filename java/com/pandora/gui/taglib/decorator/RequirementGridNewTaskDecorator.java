package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a new Task html image.<br>
 */
public class RequirementGridNewTaskDecorator extends ColumnDecorator {

	private static final String CHECK_STATUS  = "CHECK_STATUS";
	private static final String ONLY_HISTORY  = "ONLY_HISTORY";
	private static final String ONLY_NEW      = "ONLY_NEW";
	
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, CHECK_STATUS);    	
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "&nbsp;";
		
		RequirementTO rto = (RequirementTO)this.getObject();
		RequirementStatusTO rsto = rto.getRequirementStatus();
		Integer state = rsto.getStateMachineOrder();
		ProjectTO pto = rto.getProject();
		
		if (tag.equals(CHECK_STATUS)) {
			if (state==null || (!state.equals(RequirementStatusTO.STATE_MACHINE_CANCEL) && 
        			!state.equals(RequirementStatusTO.STATE_MACHINE_CLOSE) &&
        			!state.equals(RequirementStatusTO.STATE_MACHINE_REFUSE))){
				image = this.getNewTaskIcon(columnValue, pto);	
				
			} else if (state.equals(RequirementStatusTO.STATE_MACHINE_CLOSE)){
				//show a task detail icon only for close Requirement...				
				image = this.getHistoryIcon(columnValue); 						    
			}

		} else if (tag.equals(ONLY_HISTORY)) {
			if (state!=null && !state.equals(RequirementStatusTO.STATE_MACHINE_WAITING)){
				image = this.getHistoryIcon(columnValue);	 
			}
			
		} else if (tag.equals(ONLY_NEW)) {
			image = this.getNewTaskIcon(columnValue, pto);
		} 
	        
		return image;        
    }

	private String getHistoryIcon(Object columnValue) {
		String image = "";
		String altValue = this.getBundleMessage("label.showAllReqForm.grid.detailTask");		
		image ="<a href=\"javascript:openTaskHistPopupByReq('" + columnValue + "');\" border=\"0\"> \n";
		image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/detailed.gif\" >";
		image += "</a>";
		return image;
	}

	
	private String getNewTaskIcon(Object columnValue, ProjectTO pto) {
		String image = "";
		String altValue = this.getBundleMessage("label.grid.requestform.accept");
		
		image ="<a href=\"javascript:newTaskByReq('" + pto.getId() + "', '" + columnValue + "');\" border=\"0\"> \n";
		image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/newTask.gif\" >";
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
