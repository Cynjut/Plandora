package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CategoryTO;

/**
 * This decorator formats a grid cell with the project name of Category object.
 * If Category is not related with any project the decorator show an aproppriate label.
 */
public class CategoryProjectDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String text = "";
		CategoryTO cto = (CategoryTO)this.getObject();
	    
		if (cto.getProject()!=null) {
		    text = cto.getProject().getName();    
		} else {
		    text = this.getBundleMessage("label.category.anyProject");
		    
		}
		return text;
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
		String response = "";    	
		CategoryTO cto = (CategoryTO)this.getObject();
		if (cto.getProject()!=null) {
			response = cto.getProject().getName();    
		} else {
			response = "";
		}
    	return response;
    }
    
}
