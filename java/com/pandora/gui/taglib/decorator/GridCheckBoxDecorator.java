package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;
import com.pandora.TransferObject;
import com.pandora.helper.HtmlUtil;

public class GridCheckBoxDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String response = "&nbsp;";
		
		if (columnValue!=null) {
			TransferObject to = (TransferObject)getObject();
			
			boolean checked = false;
			if (to.getGenericTag()!=null && !to.getGenericTag().trim().equals("")) {
				checked = true;
			}
			response = HtmlUtil.getChkBox(checked, to.getId(), tag, false);
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
