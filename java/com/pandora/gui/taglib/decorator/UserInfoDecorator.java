package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RootTO;

/**
 */
public class UserInfoDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, null);        
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {

		String content ="" + columnValue;
		 
		if (columnValue!=null && tag!=null && !content.equals(RootTO.ROOT_USER)){
		    content = "<a class=\"gridLink\" href=\"javascript:void(0);\" onclick=\"openFloatPanel(''); showUserInfo('" + tag + "', '" + columnValue + "');\" border=\"0\"> \n" + columnValue + "</a>";
		} else {
			if (content.equals(RootTO.ROOT_USER)) {
				content = super.getBundleMessage("label.manageTask.define");
			}
		}
		
		return content;        
    }
    

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
		String content ="" + columnValue;
		 
		if (content.equals(RootTO.ROOT_USER)) {
			content = super.getBundleMessage("label.manageTask.define");
		}
    	
    	return content;
    }
    
    
}
