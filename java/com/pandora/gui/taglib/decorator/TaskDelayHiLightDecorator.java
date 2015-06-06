package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PreferenceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.DateUtil;

/**
 * This decorator check if the current task is: open, on-progress and on-hold
 * and change the label if the deadline date is later than today. 
 */
public class TaskDelayHiLightDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {        
        return decorate(columnValue, "20");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        String content = "";
        String w1 = "", w2 = "";
        String c1 = "", c2 = "";
        int words = 0;
        
        UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
        ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
        content = (String)columnValue;
        TaskStatusTO tsto = rtto.getTaskStatus();
        
        Integer st = tsto.getStateMachineOrder();
        if (st!=null &&( st.equals(TaskStatusTO.STATE_MACHINE_OPEN) || 
        		st.equals(TaskStatusTO.STATE_MACHINE_REOPEN) ||
        		st.equals(TaskStatusTO.STATE_MACHINE_HOLD) ||
                st.equals(TaskStatusTO.STATE_MACHINE_PROGRESS))){

            if (tag!=null && tag.trim().length()>0) {
                tag = uto.getPreference().getPreference(tag);
                words = Integer.parseInt(tag);
                if (content!=null && content.trim().length()>0) {
                    content = this.getCropWords(content, words);
                }                
            }
            
            int criticalDelay = Integer.parseInt(uto.getPreference().getPreference(PreferenceTO.CRITICAL_DAY_TASK));
            int warningDelay = Integer.parseInt(uto.getPreference().getPreference(PreferenceTO.WARNING_DAY_TASK));
            int delay = DateUtil.getSlotBetweenDates(rtto.getStartDate(), DateUtil.getNow());
           
            if (criticalDelay>0 && delay >= criticalDelay) {
                c1 = "<font color=\"#BB0000\">";
                c2 = "</font>";
            } 
            
            if (warningDelay>0 && delay >= warningDelay) {
                w1 = "<b>";
                w2 = "</b>";        
            }                
            
            content = c1 + w1 + content + w2 + c2;
        }
        
        return content;
    }
    
    
    private String getCropWords(String content, int number) {
        String response = content;
        
        String[] list = content.split(" ");
        if (list.length>0 && list.length>number) {
            response = "";
            for(int i=0 ; i< list.length; i++) {
                response = response + list[i] + " ";
                if (i>= number) {
                    break;
                }
            }
        }
        
        return response;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
        return (String)columnValue;
    }
    
}
