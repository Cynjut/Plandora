package com.pandora.gui.taglib.decorator;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.DateUtil;

/**
 * This decorator, format a date content into grid <br>
 * Usage example: <br> 
 * &lt;display:column property="dateValue" tag="2; 3" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" /&gt; <br>
 * Where tag attribute must be used to ask to decorator the format of date and time. Those information must be separated by ";". 
 * The order of values must be: date format; time format. If the time format was set to 0, the decorator hide the time part.<br>
 * The default value if tag attribute was empty is: formats=2 (DateFormat.DEFAULT).  
 */
public class GridDateDecorator extends ColumnDecorator {

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
        int dateFormat = 2, timeFormat = 2; //default value
        String response = "&nbsp;";
                    
        if (columnValue!=null){
        
            UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
            Locale loc = uto.getLocale();
        	
	        //get format and locale from tag string
            int c = 0;
	        StringTokenizer st = new StringTokenizer(tag, ";");
	        if (st!=null){
	            while(st.hasMoreTokens()){
	                String token = st.nextToken();
	                if (c==0){
	                    try {
	                        dateFormat = Integer.parseInt(token);
	                    }catch(Exception e){
	                        dateFormat = 2; //default value
	                    }
	                } else if (c==1){
	                    try {
	                        timeFormat = Integer.parseInt(token);
	                    }catch(Exception e){
	                        timeFormat = 2; //default value
	                    }
	                    break;
	                }
	                c++;
	            }
	        }
	        
	        //format date!
	        if (timeFormat==0){
	            response = DateUtil.getDate((Timestamp)columnValue, uto.getCalendarMask(), loc);    
	        } else {
	            response = DateUtil.getDateTime((Timestamp)columnValue, loc, dateFormat, timeFormat);
	        }

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
