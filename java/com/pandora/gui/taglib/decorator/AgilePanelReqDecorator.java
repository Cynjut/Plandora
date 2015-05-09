package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementStatusTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.helper.HtmlUtil;

public class AgilePanelReqDecorator extends ColumnDecorator {

   	public String getPreContent(Object columnValue, String tag) {
		String response = "";
		RequirementWithTasksTO rwto = (RequirementWithTasksTO)this.getObject();
		if (rwto!=null && rwto.getId()!=null) {
			RequirementStatusTO rsto = rwto.getRequirementStatus();

			RequirementGridEditDecorator editReq = new RequirementGridEditDecorator();
	   		editReq.init(super.getPageContext(), null);
	   		editReq.initRow(rwto, 0, 0);
	   		String editImg = editReq.decorate(rwto.getId(), "REQ");

	        String altValue = this.getBundleMessage("title.agilePanelForm.newReq.task");
	        String altReqValue = this.getBundleMessage("label.grid.remove");
	        boolean containTasks = (rwto.getResourceTaskList()!=null && rwto.getResourceTaskList().size()>0);
	        
	        
	   		response = response + "<table width=\100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> \n";
	   		response = response + "<tr> \n";   	
	   		response = response +     "<td width=\"20\">" + editImg + "</td> \n";

	   		if (rsto.isFinished()) {
	   			response = response +     "<td width=\"20\">&nbsp;</td> \n";	
	   		} else {
	   	   		response = response +     "<td width=\"20\"><a href=\"javascript:newPostTask(" + rwto.getId() + ");\" border=\"0\"><img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/newTask.gif\" ></a></td> \n";   			
	   		}

	   		if (!containTasks) {
	   	   		response = response +     "<td width=\"20\"><a href=\"javascript:removeReq(" + rwto.getId() + ");\" border=\"0\"><img border=\"0\" " + HtmlUtil.getHint(altReqValue) + " src=\"../images/remove.gif\" ></a></td> \n";   			
	   		}
	   		
	   		response = response +     "<td>&nbsp;</td> \n";   		
	   		response = response + "</tr><tr>";

	   		if (!containTasks) {
	   			response = response +     "<td class=\"agileReqText\" colspan=\"4\">";
	   		} else {
	   			response = response +     "<td class=\"agileReqText\" colspan=\"3\">";	
	   		}
	   		
	   		if (rsto.isFinished()) {
	   			response = response +     "<strike>";	
	   		}			
		}
		
   		return response;
   	}
   	
   	
	public String decorate(Object columnValue) {
		RequirementWithTasksTO rwto = (RequirementWithTasksTO)this.getObject();
		if (rwto!=null && rwto.getId()!=null) {
			return rwto.getName();	
		} else {
			return "&nbsp;";
		}
	}

	
   	public String getPostContent(Object columnValue, String tag) {
   		String response = "";
		RequirementWithTasksTO rwto = (RequirementWithTasksTO)this.getObject();
		if (rwto!=null && rwto.getId()!=null) {
			RequirementStatusTO rsto = rwto.getRequirementStatus();   		
	   		if (rsto.isFinished()) {
	   			response = "</strike>";	
	   		}
	   		return response + "</td></tr></table>\n"; 			
		} else {
			return response; 
		}
   	}

   	
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return (String)columnValue;
	}
	
}
