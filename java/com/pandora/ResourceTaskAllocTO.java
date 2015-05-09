package com.pandora;

import java.util.Locale;

import com.pandora.delegate.TaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.StringUtil;
import com.pandora.integration.Integration;
import com.pandora.integration.ResourceTaskAllocIntegration;


/**
 * This object it is a bean that represents a Resource Task Allocation entity.
 */
public class ResourceTaskAllocTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    /** The resource task related */
    private ResourceTaskTO resourceTask;
    
    /** The sequence number of slot allocated */
    private Integer sequence;
    
    /** The value (in minutes) of a slot */
    private Integer allocTime;
    
    
    /**
     * Constructor 
     */
    public ResourceTaskAllocTO(){
    }

    /**
     * Constructor 
     */    
    public ResourceTaskAllocTO(ResourceTaskTO rtto, int seq, int allocTm){
        this.resourceTask = rtto;
        this.sequence = new Integer(seq);
        this.allocTime = new Integer(allocTm);
    }      

    /**
     * Constructor 
     */    
    public ResourceTaskAllocTO(ResourceTaskAllocTO clone){
        if (clone!=null) {
            this.setResourceTask(clone.getResourceTask());
            this.setSequence(clone.getSequence());
            this.setAllocTime(clone.getAllocTime());
        }
    }  
    
    public String getId() {
        String myId = null;
        if (this.getResourceTask()!=null && this.getSequence()!=null){
            myId = this.getResourceTask().getId() + "-" + this.getSequence();
        }
        return myId;
    }
    
    /**
     * This method populate a ResourceTaskAlloc transfer object
     * using the information from Integration object.
     */
    public void populate(Integration iobj, UserTO handler) {
        TaskDelegate tdel = new TaskDelegate();
        ResourceTaskAllocIntegration rtai = (ResourceTaskAllocIntegration)iobj;
        
        this.setSequence(new Integer(rtai.getSequence()));
        float value = StringUtil.getStringToFloat(rtai.getValue(), handler.getLocale());
        this.setAllocTime(new Integer(((int)value*60)));
        
        //load task object from database
        ResourceTaskTO rtto = new ResourceTaskTO();
        TaskTO tto = new TaskTO(rtai.getTaskId());
        try {
            tto = tdel.getTaskObject(tto);
        } catch (BusinessException e) {
            tto = null;
        }
        rtto.setTask(tto);

        //load resource object from database        
        ResourceTO rto = new ResourceTO(rtai.getResourceId());
        rto.setProject(tto.getProject());
        rtto.setResource(new ResourceTO(rto));
        
        this.setResourceTask(rtto);
    }
    
    
    /**
     * Return the alloc time in hours format (decimal)
     */
    public String getAllocTimeInHours(Locale loc) {
        String response = "";
        Integer alloc = this.getAllocTime();
        
        if (alloc!=null){
            float f = alloc.floatValue() / 60;
            response = StringUtil.getFloatToString(f, loc);
        }
        
        return response;
    }

    
    /**
     * Return the alloc time in time format
     */
    public String getAllocTimeInTimeFormat(Locale loc) {
        return StringUtil.getIntegerToHHMM(this.getAllocTime(), loc);
    }
    
    
    ///////////////////////////////////
    public Integer getAllocTime() {
        return allocTime;
    }
    public void setAllocTime(Integer newValue) {
        this.allocTime = newValue;
    }
    
    ///////////////////////////////////    
    public ResourceTaskTO getResourceTask() {
        return resourceTask;
    }
    public void setResourceTask(ResourceTaskTO newValue) {
        this.resourceTask = newValue;
    }
    
    ///////////////////////////////////    
    public Integer getSequence() {
        return sequence;
    }
    public void setSequence(Integer newValue) {
        this.sequence = newValue;
    }

    /**
     * Return the resource task alloc object information on Applet PARAM format.
     * @param i
     * @param slot
     * @return
     */
    public String getAllocBodyFormat(int i, int slot, int type) {
        TaskTO tto = resourceTask.getTask();
        return "<param name=\"AUNIT_" + i + "\" value=\"" + resourceTask.getId() + 
        							"|" + tto.getId() + "|" + ((float)this.getAllocTime().intValue()/60) + 
        							"|" + slot + "|" + slot + "|" + type +
        							"\" />\n";
    }
    

    public String toString() {
        return "allocTime:[" + this.getAllocTime().toString() + "] sequence:[" + this.getSequence().toString()+ "]";
    }
}
