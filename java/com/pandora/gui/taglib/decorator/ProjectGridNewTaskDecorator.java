package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.RootTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a new Task html image.<br>
 */
public class ProjectGridNewTaskDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String image = "&nbsp;";
		ProjectTO pto = (ProjectTO)this.getObject();
		if (pto!=null && pto.getRoleIntoProject()!=null && (
				pto.getRoleIntoProject().equals(LeaderTO.ROLE_LEADER+"") || 
			    pto.getRoleIntoProject().equals(RootTO.ROLE_ROOT+""))){
			
		    if (pto.getBollCanAlloc()) {
		    	
			    String altValue = this.getBundleMessage("label.grid.project.newTask");		    
				image ="<a href=\"javascript:newTask('" + columnValue + "');\" border=\"0\"> \n";
				image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/newTask.gif\" >";
				image += "</a>";
				
		    }
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
