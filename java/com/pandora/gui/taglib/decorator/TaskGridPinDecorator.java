package com.pandora.gui.taglib.decorator;

import java.util.StringTokenizer;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PreferenceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

public class TaskGridPinDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        String image = "";
		String key = "label.grid.requestform.pin.0";
		String imgName = "empty.gif";
        String type = "0";
        
		UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);        
		ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
		TaskTO tto = rtto.getTask();
		
		PreferenceTO pto = uto.getPreference();
		String pin = pto.getPreference(PreferenceTO.PIN_TASK_LIST);
		
		if (pin!=null){
			
			boolean pinned = (pin.indexOf(tto.getId())>-1);
			if (pinned) {
				
				StringTokenizer st = new StringTokenizer(pin, "|");
	            while (st.hasMoreTokens()) {
	            	String token = st.nextToken();
	            	String[] element = token.split(";");
	            	if (element!=null && element.length==2) {
	            		if (element[0].trim().equals(tto.getId())) {

            				imgName = this.getImg(element[1].trim());
            				key = this.getLabelKey(element[1].trim());            				
	            			if (element[1].trim().equals("1") || 
	            			        element[1].trim().equals("2") || 
	            			        element[1].trim().equals("3")) {
	            				type = element[1].trim();
	            			}	            			
	        				break;
	            		}
	            	}
	            }				
			}
		}
		
		String altValue = this.getBundleMessage(key);
		image = "<a href=\"javascript:pin('" + tto.getId().trim() + "', '" + type + "');\" border=\"0\"> \n";		
		image += "<img id=\"PIN_" + tto.getId().trim() + "\" border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/" + imgName + "\" >";
		image += "</a>";
		
		return image;
    }

    
    public String getImg(String type){
		if (type.equals("1")) {
			return "alert-task.gif";
		} else if (type.equals("2")) {
		    return "fav-task.gif";
		} else if (type.equals("3")) {
			return "key-task.gif";
		} else {
		    return "empty.gif";
		}
    }

    public String getLabelKey(String type){
		if (type.equals("1")) {
			return "label.grid.requestform.pin.1";
		} else if (type.equals("2")) {
			return "label.grid.requestform.pin.2";		    
		} else if (type.equals("3")) {
			return "label.grid.requestform.pin.3";		    
		} else {
			return "label.grid.requestform.pin.0";
		}
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

