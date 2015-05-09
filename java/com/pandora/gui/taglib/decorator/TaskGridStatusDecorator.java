package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator show a bullet int the grid cell with representing the general Task status.<br>
 */
public class TaskGridStatusDecorator extends ColumnDecorator {
    
    /** value used to specified the type of image color related with status */
    private int RED_BALLON = 1;
    
    /** value used to specified the type of image color related with status */
    private int YELLOW_BALLON = 2;
    
    /** value used to specified the type of image color related with status */
    private int GREEN_BALLON = 3;
    
    /** value used to specified the type of image color related with status */
    private int EMPTY = -1;

    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "COLOR");        
    }

    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        String content = "";
        Object obj = this.getObject();
        String label = this.getStatusLabelOfAllocResource(obj);
        
        if (tag.equals("COLOR")){
            
            String img = "redballon.gif";
            int colorType = 0;
            colorType = this.getStatusColorOfAllocResource(obj);

            if (colorType==GREEN_BALLON || colorType==0){
                img = "greenballon.gif";
            } else if (colorType==YELLOW_BALLON){
                img = "yellowballon.gif";
            } else if (colorType==EMPTY){
                img = "empty.gif";
            }
            content = "<img border=\"0\" " + HtmlUtil.getHint(label) + " src=\"../images/" + img + "\" >";            
        } else {
            content = label;
        }
        
        return content;     
        
    }
    
    
    /**
     * Define the rules to show the colors of ballons related with task: <br>
     * <li>If one of them was 'open' status, the icon related is red.</li>
     * <li>If one of them was 'in-progress', but the others is not 'open' status, the icon related is yellow.</li>
     * <li>If ALL taskResources objects is final state of state-machine ('close', 'cancel', etc), the icon related is green.</li>
     * 
     * @param tto
     * @return
     */
    private int getStatusColorOfAllocResource(Object obj){
        int colorType = 0;
        Integer type = null;
        ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
        
        if (obj instanceof TaskTO) {
            TaskTO tto = (TaskTO)this.getObject();
            if (tto.isParentTask()){
                colorType = EMPTY;
            } else {
                type = rtdel.getStatusOfAllocResource(tto);
                colorType = this.getColorByType(type);                    
            }
        } else {
            ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
            type = rtdel.getStatusOfAllocResource(rtto);
            colorType = this.getColorByType(type);
        }
        
        return colorType;
    }

    /**
     * Define the rules to show the task status label related with task.
     * @param obj
     * @return
     */
    private String getStatusLabelOfAllocResource(Object obj){
        String response = "";
        Integer type = null;
        ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
        
        if (obj instanceof TaskTO) {
            TaskTO tto = (TaskTO)this.getObject();
            if (tto.isParentTask()){                
                response = "";
            } else {
                type = rtdel.getStatusOfAllocResource(tto);
                response = this.getLabelByType(type);                    
            }
        } else {
            ResourceTaskTO rtto = (ResourceTaskTO)this.getObject();
            response = rtto.getTaskStatus().getName();
        }
        
        return response;
    }
    
    /**
     * Define the rules to show the colors of ballons related with task: <br>
     * <li>If one of them is 'open' or 'reopen' status, the icon related is red.</li>
     * <li>If one of them is 'in-progress' or 'hold', but the others is not 'open' status, the icon related is yellow.</li> 
     * @param rtto
     * @return
     */
    private int getColorByType(Integer type){
        int response = 0;        
                
        if (type.equals(TaskStatusTO.STATE_MACHINE_OPEN) || 
        		type.equals(TaskStatusTO.STATE_MACHINE_REOPEN)){
            response = RED_BALLON;
        } else if (type.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) ||
                   type.equals(TaskStatusTO.STATE_MACHINE_HOLD)){
            response = YELLOW_BALLON;
        }
        return response;
    }
    
    /**
     * Get correct label from resource bundle based on status type.
     */
    private String getLabelByType(Integer type){
        String response = "";        
        if (type.equals(TaskStatusTO.STATE_MACHINE_OPEN)){
            response = this.getBundleMessage("error.manageTask.openTask");
        } else if (type.equals(TaskStatusTO.STATE_MACHINE_REOPEN)){
        	response = this.getBundleMessage("error.manageTask.reopenTask");
        } else if (type.equals(TaskStatusTO.STATE_MACHINE_PROGRESS)){
            response = this.getBundleMessage("error.manageTask.inProgressHoldTask");
        } else if (type.equals(TaskStatusTO.STATE_MACHINE_HOLD)){
            response = this.getBundleMessage("error.manageTask.holdTask");
        } else if (type.equals(TaskStatusTO.STATE_MACHINE_CLOSE) || 
                type.equals(TaskStatusTO.STATE_MACHINE_CANCEL)){
            response = this.getBundleMessage("error.manageTask.CloseCancelTask");
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
