package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.helper.HtmlUtil;

/**
 */
public class ProjectGridReportDecorator extends ColumnDecorator {

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
		String image = "";
		ProjectTO pto = (ProjectTO)this.getObject();
		if (pto.getRoleIntoProject()!=null){
			if (pto.getRoleIntoProject().equals(LeaderTO.ROLE_LEADER+"") ||
					pto.getRoleIntoProject().equals(ResourceTO.ROLE_RESOURCE+"")){
			    String altValue = this.getBundleMessage("label.grid.project.report");		
			    image ="<a href=\"javascript:showReport('" + columnValue + "', " + tag + ");\" border=\"0\"> \n";
			    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/report.gif\" >";
			    image += "</a>";
			}
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
