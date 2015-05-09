package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.TaskTO;

/**
 * This decorator formats a grid cell with a tree structure based on Task attributes.
 */
public class TaskTreeDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        String content = "";
        TaskTO tto = (TaskTO)this.getObject();        
        content = this.getSpace(tto.getGridLevel());
        
        if (tto.getParentTask()!=null){
            if (tto.isParentTask()){            
                content += "<img border=\"0\" src=\"../images/child.gif\" >" + "<B>" + columnValue + "</B>";
            } else {
                content += "<img border=\"0\" src=\"../images/child.gif\" >" + columnValue;
            }

        } else {
            if (tto.isParentTask()){
                content += "<B>" + columnValue + "</B>";
            } else {
                content += columnValue;
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
