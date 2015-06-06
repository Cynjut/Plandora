package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
import com.pandora.helper.HtmlUtil;

public class ProjectPanelDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String link = "";
		
		ProjectTO pto = this.getProject(this.getObject());
		if (pto!=null){
			String tip = HtmlUtil.getHint(pto.getName());
    		link =  "<a class=\"gridLink\" " + tip + " href=\"../do/showProjectPanel?operation=prepareForm&projectId=" + pto.getId() + "\" border=\"0\"> \n";
    		link += pto.getName();
    		link += "</a>";    		
		}
		return link;
    }

    private ProjectTO getProject(Object object) {
    	ProjectTO response = null;
    	if (object instanceof ResourceTaskTO) {
    		ResourceTaskTO rtto = (ResourceTaskTO)getObject();
    		response = rtto.getTask().getProject();
    	} else if (object instanceof ProjectTO) {
    		response = (ProjectTO)getObject();
    	} else if (object instanceof RequirementTO) {
    		RequirementTO rto = (RequirementTO)getObject();
    		response = rto.getProject();
    	}
		return response;
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
