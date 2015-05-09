package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;
import com.pandora.UserTO;
import com.pandora.PreferenceTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

public class HideProjectDecorator extends ColumnDecorator {

    public final static String PROJ_HIDE = "PROJ_HIDE";
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
    	String projectId = (String)columnValue;
        String response = HtmlUtil.getChkBox(false, projectId, PROJ_HIDE, false);
        
        UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
        if (isHideProject(uto, projectId)){
        	response = HtmlUtil.getChkBox(true, projectId, PROJ_HIDE, false);
        } 
        
        return response;
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
    
    public static boolean isHideProject(UserTO uto, String projectId){
    	boolean response = false;
        PreferenceTO pref = uto.getPreference();
        String hiddenList = pref.getPreference(PreferenceTO.HIDE_PROJECT);
        
        if (hiddenList!=null && projectId!=null){
            
            //check if project id is into the hidden list
            String[] list = hiddenList.split("\\|");
            for (int i =0;i<list.length; i++) {
            	if (list[i].trim().equals(projectId)) {
                    response = true;
                    break;
            	}
            }
        } 
    	
        return response;
    }
}
