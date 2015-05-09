package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ProjectTO;

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
        content = this.getSpace(pto.getGridLevel());
        
        if (!pto.getParentProject().getId().equals("0")){
           if (!content.trim().equals("")){
               content += "<img border=\"0\" src=\"../images/child.gif\" >";
           }
           content += columnValue;
        } else {
           content += columnValue;
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
