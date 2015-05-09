package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.helper.HtmlUtil;

public class ProjectGridSurveyDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "";
		ProjectTO pto = (ProjectTO)this.getObject();
		String role = pto.getRoleIntoProject();
		if (role!=null && role.equals(LeaderTO.ROLE_LEADER+"")){
		    String altValue = this.getBundleMessage("label.grid.project.survey");		
		    image ="<a href=\"javascript:showSurvey('" + columnValue + "');\" border=\"0\"> \n";
		    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/survey.gif\" >";
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
