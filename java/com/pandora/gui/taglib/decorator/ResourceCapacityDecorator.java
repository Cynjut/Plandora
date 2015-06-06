package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.helper.HtmlUtil;

public class ResourceCapacityDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		String altValue = this.getBundleMessage("title.resCapacity");
		
		if (this.getObject() instanceof ResourceTO) {
			ResourceTO rto = (ResourceTO) this.getObject();
			image ="<a href=\"javascript:showResourceCapacity('" + rto.getId() + "', '" + rto.getProject().getId() + "');\" border=\"0\"> \n";
			image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/linechart.gif\" >";
			image += "</a>"; 				
			
		} else	if (this.getObject() instanceof ProjectTO) {
			ProjectTO pto = (ProjectTO) this.getObject();
			if (pto!=null && pto.getRoleIntoProject()!=null && (pto.getRoleIntoProject().equals(LeaderTO.ROLE_LEADER+"") || 
				    pto.getRoleIntoProject().equals(RootTO.ROLE_ROOT+""))){
				image ="<a href=\"javascript:showResourceCapacity('" + pto.getId() + "');\" border=\"0\"> \n";
				image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/linechart.gif\" >";
				image += "</a>"; 				
			} else {
				image += "&nbsp;";
			}
		}
 
			
		return image;
	}

	
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return "";
	}
	
}
