package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

/**
 * This decorator formats a grid cell of Meta Field form
 */
public class GridMetaFieldDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String index = "";
		if (columnValue!=null) {
		    index = ((Integer)columnValue).toString();
    	}    
		return this.getBundleMessage("label.formMetaField.type" + index);
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
		String index = "";
		if (columnValue!=null) {
		    index = ((Integer)columnValue).toString();
    	}    
		return this.getBundleMessage("label.formMetaField.type" + index);
    }
    
}
