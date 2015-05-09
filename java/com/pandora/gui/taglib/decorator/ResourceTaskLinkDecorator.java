package com.pandora.gui.taglib.decorator;

import java.util.Vector;
import java.util.Iterator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.delegate.TaskDelegate;

import com.pandora.ProjectTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RootTO;
import com.pandora.TaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.helper.HtmlUtil;


/**
 * This decorator formats a grid cell with a edit html image.<br>
 */
public class ResourceTaskLinkDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "";
		TaskDelegate tdel = new TaskDelegate();
		
		ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
		if (rtto!=null){
			TaskStatusTO tsto = rtto.getTaskStatus(); 
		    TaskTO tto = rtto.getTask();	
			ResourceTO rto = rtto.getResource();
			if (tto!=null && rto!=null){
			    boolean useLink = true;
			    String altValue = this.getBundleMessage("label.grid.edit");
			    String img = "edit.gif";
			    ProjectTO pto = tto.getProject();    

				if (!tag.equals("SHOW_ALL") && rto.getUsername().equals(RootTO.ROOT_USER)) {
					img = "grab_task.gif";
					altValue = this.getBundleMessage("label.resHome.grabTask");
					String descLbl = this.getBundleMessage("label.resHome.grabTask.confirm");
					String yesLbl = this.getBundleMessage("label.yes");
					String noLbl = this.getBundleMessage("label.no");
				    image ="<a href=\"#\" onclick=\"displayStaticMessage('<center><p class=&quot;gridBody&quot; " +
				    			"style=&quot;text-align:center&quot;><br>" + descLbl + "</p>" + 
				    			"</p><input type=&quot;button&quot; value=&quot;  " + yesLbl + "  &quot; class=&quot;button&quot; onclick=&quot;grabTask(" + tto.getId() + ", " + pto.getId() + ");closeMessage();&quot;>&nbsp;&nbsp;&nbsp;" +
				    			    "<input type=&quot;button&quot; value=&quot;" + noLbl + "&quot; class=&quot;button&quot; onclick=&quot;closeMessage()&quot;></center>" +
				    			"', 350, 105);return false;\"> \n";			    	
				} else {

				    if (tsto.isFinish()){
					    altValue = this.getBundleMessage("label.resHome.adjustTask");
					    img = "adjustreq.gif";
				    } else if (!tag.equals("SHOW_ALL") && tdel.isBlocked(tto)){
					    altValue = this.getBundleMessage("label.resHome.blockTask") + this.getPredecessors(tto);
					    img = "lock.png";
					    useLink = false;
				    }
				    
				    if (useLink) {
				    	image ="<a href=\"javascript:callResourceTask('" + tto.getId() + "', '" + rto.getId() + "', '" + pto.getId() + "');\" border=\"0\"> \n";	
				    }								    
				}
				
				image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/" + img + " \">";
				if (useLink) {
					image += "</a>";	
				}					
			}
		}
		
		return image;
    }

    private String getPredecessors(TaskTO tto){
        String response = "";
    	Vector relations = tto.getRelationList();
    	if (relations!=null && relations.size()>0) {
    		Iterator i = relations.iterator();
    		while(i.hasNext()) {
    			PlanningRelationTO relation = (PlanningRelationTO)i.next();
    			if (relation.getRelated().getId().equals(tto.getId())) {
        			if (relation.getRelationType().equals(PlanningRelationTO.RELATION_BLOCKS) && 
        					relation.getPlanning().getFinalDate()==null) {
        				response = response + "[" + relation.getPlanning().getId() + "] ";
        			}    			    
    			}
    		}
    	}
    	return response;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
