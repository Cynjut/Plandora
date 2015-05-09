package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.helper.HtmlUtil;

/**
 */
public class ProjectGridOccurrenceBookDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String image = "";
		ProjectTO pto = (ProjectTO)this.getObject();
		String role = pto.getRoleIntoProject();
		if (role!=null && role.equals(LeaderTO.ROLE_LEADER+"")){
		    String altValue = this.getBundleMessage("label.grid.project.occurrence");		
		    image ="<a href=\"javascript:window.location='../do/manageOccurrence?operation=prepareForm&projectId=" + columnValue + "'\" border=\"0\"> \n";
		    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/occurrence.gif\" >";
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
