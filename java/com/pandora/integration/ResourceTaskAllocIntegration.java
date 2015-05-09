package com.pandora.integration;

import org.w3c.dom.Node;

import com.pandora.helper.XmlDomParse;
import com.pandora.integration.exception.IntegrationException;
import java.util.Locale;
import com.pandora.helper.StringUtil;

/**
 * This object serializes the information about 
 * the resource task allocation (a slot into a job of Gantt).
 */
public class ResourceTaskAllocIntegration extends Integration {
    
	private static final long serialVersionUID = 1L;
	
    /** Used by XML tag reference */
    public static final String RESOURCE_TASK_ALLOC = "ALLOC";
    
    /** The task id related with current resource task */
    private String taskId;
    
    /** The resource id related with current resource task */
    private String resourceId;

    /** The sequence number of slot allocated */
    private String sequence;
    
    /** The value of current allocation */
    private String value;
    
    
    /**
     * Constructor
     */
    public ResourceTaskAllocIntegration() {
    }    
    
    /**
     * Constructor
     */
    public ResourceTaskAllocIntegration(String user, String password, String locale) {
       super.setUser(user);
       super.setPassword(password);
       super.setLocale(locale);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.integration.Integration#validateInsert()
     */
    public void validateInsert() throws IntegrationException {
        throw new IntegrationException("Feature not implemented");
    }

    
    /* (non-Javadoc)
     * @see com.pandora.integration.Integration#validateUpdate()
     */
    public void validateUpdate() throws IntegrationException {
        if (taskId==null || taskId.trim().equals("")){
            throw new IntegrationException("The taskId field is mandatory.");    
        }
        if (resourceId==null || resourceId.trim().equals("")){
            throw new IntegrationException("The resourceId field is mandatory.");
        }
        if (sequence==null || sequence.trim().equals("")){
            throw new IntegrationException("The sequence field is mandatory.");
        }
        if (value==null || value.trim().equals("")){
            throw new IntegrationException("The value field is mandatory.");
        }

        try {
            int seq = Integer.parseInt(sequence);
            if (seq<1){
                throw new IntegrationException("The sequence value cannot be zero or negative.");    
            }                
        } catch(Exception e){
            throw new IntegrationException("The sequence contain an invalid value.");    
        }
        

        try {
            if (value!=null){
                float val = StringUtil.getStringToFloat(value, new Locale(super.getLocale()));
                if (val<0){
                    throw new IntegrationException("The allocation value contain an invalid content.");    
                }
            } else {
                throw new IntegrationException("The allocation value contain an invalid content.");                
            }
        } catch(Exception e){
            throw new IntegrationException("The sequence contain an invalid value.");    
        }
    }

    
    /* (non-Javadoc)
     * @see com.pandora.integration.Integration#validateDelete()
     */
    public void validateDelete() throws IntegrationException {
        throw new IntegrationException("Feature not implemented");        
    }

    
    /* (non-Javadoc)
     * @see com.pandora.integration.Integration#toXML()
     */
    public String toXML() {
        String response = "<" + RESOURCE_TASK_ALLOC + 
        	" TASK_ID=\"" + this.getTaskId() +
    		"\" RESOURCE_ID=\"" + this.getResourceId() +
    		"\" SEQUENCE=\"" + this.getSequence() +
    		"\" VALUE=\"" + this.getValue() + "\"" + 
    		super.toXML() + "/>";
        return response;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.integration.Integration#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) {
        this.setTaskId(XmlDomParse.getAttributeTextByTag(node, "TASK_ID"));
        this.setResourceId(XmlDomParse.getAttributeTextByTag(node, "RESOURCE_ID"));
        this.setSequence(XmlDomParse.getAttributeTextByTag(node, "SEQUENCE"));
        this.setValue(XmlDomParse.getAttributeTextByTag(node, "VALUE"));
        super.fromXML(node);
    }
    
    
    ///////////////////////////////////////////        
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String newValue) {
        this.resourceId = newValue;
    }
    
    
    ///////////////////////////////////////////        
    public String getSequence() {
        return sequence;
    }
    public void setSequence(String newValue) {
        this.sequence = newValue;
    }
    
    
    ///////////////////////////////////////////        
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String newValue) {
        this.taskId = newValue;
    }
    
    
    ///////////////////////////////////////////        
    public String getValue() {
        return value;
    }
    public void setValue(String newValue) {
        this.value = newValue;
    }

    
    public String toString() {
        return "taskId: " + taskId +
        	" resourceId: " + resourceId +
        	" sequence: " + sequence +
        	" value: " + value;
    }
}
