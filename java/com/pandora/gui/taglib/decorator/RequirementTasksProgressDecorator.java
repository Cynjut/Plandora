package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;

public class RequirementTasksProgressDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
	    return "";
    }
	
	
   	public String getPreContent(Object columnValue, String tag) {	
   		String response = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\"><tr height=\"5px\">";
   		int allTasks = 0;
   		int closedTask = 0;
   		int percDone = 0;
   		
		try {
			
			String closedLbl = super.getBundleMessage("label.showAllReqForm.grid.progTask.1");
			String nonClosedLbl = super.getBundleMessage("label.showAllReqForm.grid.progTask.2");
			
			RequirementTO rto = (RequirementTO)this.getObject();
			if (rto!=null) {
				
				for (ResourceTaskTO rtto : rto.getResourceTaskList()) {
                	allTasks++;
                	if (rtto.getTaskStatus()!=null && rtto.getTaskStatus().isFinish()) {
                		closedTask++;
                	}
				}
	            
				if (allTasks>0) {
					percDone = (int)((float)((float)closedTask / (float)allTasks) * 100);	
				} else {
					percDone = 0;
				}
			}
				
			if (percDone>0 && percDone<100) {
				response = response + "<td title=\"" + closedTask + " " + closedLbl + "\" class=\"tablecell\" style=\"background-repeat:no-repeat\" bgcolor=\"#00ff00\" width=\"" + percDone + 
									   "%\">&nbsp;</td><td title=\"" + (allTasks-closedTask) + " " + nonClosedLbl + "\" style=\"background-repeat:no-repeat\" bgcolor=\"#ff0000\">&nbsp;</td>";	
			} else if (percDone==100) {
				response = response + "<td title=\"" + closedTask + " " + closedLbl + "\" class=\"tablecell\" style=\"background-repeat:no-repeat\" bgcolor=\"#00ff00\">&nbsp;</td>";
			} else if (percDone==0) {				
				response = response + "<td " + (allTasks>0?"title=\"" + allTasks + " " + nonClosedLbl + "\"":"") + " class=\"tablecell\" style=\"background-repeat:no-repeat\" bgcolor=\"#ff0000\">&nbsp;</td>";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
   	}
   	

   	public String getPostContent(Object columnValue, String tag) {
		return "</tr></table>";   		
   	}

   	
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
    	return decorate(columnValue, null);
    }

    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
        return "";
    }
	
}
