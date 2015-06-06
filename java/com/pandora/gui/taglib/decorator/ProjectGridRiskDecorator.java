package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.helper.HtmlUtil;

/**
 * 
 */
public class ProjectGridRiskDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String image = "";
		ProjectTO pto = (ProjectTO)this.getObject();
		String role = pto.getRoleIntoProject();
		if (role!=null && (role.equals(LeaderTO.ROLE_LEADER+"") || role.equals(LeaderTO.ROLE_RESOURCE+""))){
		    String altValue = this.getBundleMessage("label.grid.project.risk");		
		    image ="<a href=\"javascript:showRisk('" + columnValue + "');\" border=\"0\"> \n";
		    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/risk.gif\" >";
		    image += "</a>";
		}
		return image;
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
