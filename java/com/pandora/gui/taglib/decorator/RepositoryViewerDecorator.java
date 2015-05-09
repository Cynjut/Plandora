package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.helper.HtmlUtil;

public class RepositoryViewerDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		ProjectTO pto = (ProjectTO)this.getObject();	
		
		String role = pto.getRoleIntoProject();
		boolean accessAllowed = role!=null && role.equals(LeaderTO.ROLE_LEADER+"");
		
		if (pto.getRepositoryClass()!=null && !pto.getRepositoryClass().equals("-1") &&
				(accessAllowed || pto.getCanSeeRepository())) {
			
		    String altValue = this.getBundleMessage("label.grid.project.repository");		
		    image ="<a href=\"javascript:showRepository('" + columnValue + "');\" border=\"0\"> \n";
		    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/repository.gif\" >";
		    image += "</a>";			
		}
		return image;
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}

}
