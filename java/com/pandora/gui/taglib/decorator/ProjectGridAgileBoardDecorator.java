package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.helper.HtmlUtil;

public class ProjectGridAgileBoardDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String image = "";
		ProjectTO pto = (ProjectTO)this.getObject();	
		
		String role = pto.getRoleIntoProject();
		boolean accessAllowed = role!=null && role.equals(LeaderTO.ROLE_LEADER+"");

		if (accessAllowed || pto.getCanSelfAlloc()){
		    String altValue = this.getBundleMessage("label.grid.project.agile");		
		    image ="<a href=\"javascript:showAgilBoard('" + columnValue + "');\" border=\"0\"> \n";
		    image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/agile.png\" >";
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
