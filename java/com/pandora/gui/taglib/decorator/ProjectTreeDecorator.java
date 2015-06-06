package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a tree structure based on Project attributes.
 */
public class ProjectTreeDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        String content = "";
        ProjectTO pto = (ProjectTO)this.getObject();
        
        if (pto!=null) {
        	String link = "";
			String tip = HtmlUtil.getHint(pto.getName());
    		link =  "<a class=\"gridLink\" " + tip + " href=\"../do/showProjectPanel?operation=prepareForm&projectId=" + pto.getId() + "\" border=\"0\"> \n";
    		link += columnValue;
    		link += "</a>";    		

        	
        	
            UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
        	String hiddenList = uto.getPreference().getPreference(PreferenceTO.HIDE_PROJECT);
            boolean isParentHidden = false;
            if (hiddenList!=null && pto.getParentProject()!=null && hiddenList.indexOf(pto.getParentProject().getId()+"|")>-1) {
            	isParentHidden = true;
            }
        	
            if (!isParentHidden) {
            	content = this.getSpace(pto.getGridLevel());
            } else {
            	content = this.getSpace(pto.getGridLevel()-1);
            }
            
            if (pto.getParentProject()!=null && pto.getParentProject().getId()!=null) {                	
                if (!pto.getParentProject().getId().equals("0")){
                    if (!content.trim().equals("")){
                        content += "<img border=\"0\" src=\"../images/child.gif\" >";
                    }
                    content += link;
                 } else {
                    content += link;
                 }        	            	
            }            	
        }
        
        return content;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        return decorate(columnValue);
    }
    
    
    /**
     * Get spaces in html format based on current level of task.
     * @param level
     * @return
     */
    private String getSpace(int level){
        String response = "";
        for(int i=0; i<level; i++){
            response+="<img border=\"0\" src=\"../images/empty.gif\" />";
        }
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
