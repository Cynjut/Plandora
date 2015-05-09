package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.RootTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a edit html image.<br>
 * It works like GridEditDecorator (generic decorator) but has a special feature related with Project form.
 * If the user's role of current project is different of "Leader" the icon cannot be displayed. 
 */
public class ProjectGridEditDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "'edit'");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "&nbsp;";
		ProjectTO pto = (ProjectTO)this.getObject();
		if (pto.getRoleIntoProject()!=null){
			if (pto.getRoleIntoProject().equals(LeaderTO.ROLE_LEADER+"") || 
			    pto.getRoleIntoProject().equals(RootTO.ROLE_ROOT+"")){

			    String altValue = this.getBundleMessage("label.grid.project.edit");		    		    
				image ="<a href=\"javascript:edit('" + columnValue + "'," + tag + ");\" border=\"0\"> \n";
				image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/edit.gif\" >";
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
