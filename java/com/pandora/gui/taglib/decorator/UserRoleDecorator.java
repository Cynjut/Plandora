package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

/**
 * This decorator, replaces the string of bundle id (related 
 * with User Role) to the meaning from Resource Bundle file.   
 */
public class UserRoleDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String column ="&nbsp;";
				 
		if (columnValue!=null){
		    String key = "label.resHome.role_" + (String)columnValue;
		    String bundle = this.getBundleMessage(key);
			if (bundle != null && !bundle.equals("err!")) {
				column = bundle;
			}
		} 
		return column;
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
