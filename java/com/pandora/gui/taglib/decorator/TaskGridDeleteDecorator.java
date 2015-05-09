package com.pandora.gui.taglib.decorator;

import java.util.Iterator;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ResourceTaskTO;
import com.pandora.RootTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

public class TaskGridDeleteDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        String image = "";
		TaskDelegate tdel = new TaskDelegate();
		UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);        
		ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
		TaskTO tto = (TaskTO)rtto.getTask();
		
		if (tto.getTemplateInstanceId()==null) {
			
			boolean isUnsignedTask = false;
			Vector alloc = tto.getAllocResources();
			Iterator i = alloc.iterator();
			while(i.hasNext()) {
				ResourceTaskTO rt = (ResourceTaskTO)i.next();
				if (rt.getResource()!=null && rt.getResource().getUsername().equals(RootTO.ROOT_USER)){
					isUnsignedTask = true;
					break;
				}
			}
			
			
			if (!isUnsignedTask && tdel.isUserTaskOwner(tto, uto)){
			    String altValue = this.getBundleMessage("label.grid.requestform.removeTask");
				image ="<a href=\"javascript:remove('" + tto.getId() + "', 'TSK');\" border=\"0\"> \n";
				image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/remove.gif\" >";
				image += "</a>";
			} else {
			    image += "&nbsp;";
			}			
		} else {
			GridWorkflowDecorator wf = new GridWorkflowDecorator();
			image = wf.getWorkFlowIcon("", this.getObject());
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
