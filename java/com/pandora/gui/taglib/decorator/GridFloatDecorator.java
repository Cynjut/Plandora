package com.pandora.gui.taglib.decorator;

import java.util.Locale;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.StringUtil;

/**
 * This decorator, format a float number into grid cell using the appropriate locale.
 */
public class GridFloatDecorator extends ColumnDecorator {

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
        String response = "err!";
        UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
        String language = uto.getLanguage();
        String country = uto.getCountry();            

        if (columnValue!=null){
            Locale loc = new Locale(language, country);
            Float f = (Float)columnValue;
            
	        if (isHHMMformat(tag, uto)) {
	        	float ff = f.floatValue() * 60;
	        	response = StringUtil.getIntegerToHHMM(new Integer((new Float(ff)).intValue()), loc);
	        } else {
	            response = StringUtil.getFloatToString(f.floatValue(), loc);        	
	        }
        }
        
        return response + tag;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
    
    private boolean isHHMMformat(String tag, UserTO uto){
    	boolean response = false;
    	if (tag!=null && tag.trim().equals("h")) {
		    String frmtInput = uto.getPreference().getPreference(PreferenceTO.INPUT_TASK_FORMAT);
		    response = (frmtInput!=null && frmtInput.equals("2"));
    	}
    	return response;
    }
}
