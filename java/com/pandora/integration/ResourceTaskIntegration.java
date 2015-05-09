package com.pandora.integration;

import org.w3c.dom.Node;

import com.pandora.helper.XmlDomParse;
import com.pandora.integration.exception.IntegrationException;

/**
 * This object serializes the information about 
 * the resource task (a job into Gantt).
 */
public class ResourceTaskIntegration extends Integration {

	private static final long serialVersionUID = 1L;
	
    /** Used by XML tag reference */
    public static final String RESOURCE_TASK = "TASK";
    
    /** The task id related with current resource task */
    private String taskId;
    
    /** The resource id related with current resource task */
    private String resourceId;
    
    /** The year of estimated start date of task */
    private String estimatedYear;
    
    /** The month of estimated start date of task */
    private String estimatedMonth;
    
    /** The day of estimated start date of task */
    private String estimatedDay;
    
    /** The estimated time of task (total values of all slots) */
    private String estimatedTime;

    /**
     * Constructor
     */
    public ResourceTaskIntegration() {
    }
    
    /**
     * Constructor
     */
    public ResourceTaskIntegration(String user, String password, String locale) {
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
        if (estimatedYear==null || estimatedYear.trim().equals("")){
            throw new IntegrationException("The estimatedYear field is mandatory.");
        }
        if (estimatedMonth==null || estimatedMonth.trim().equals("")){
            throw new IntegrationException("The estimatedMonth field is mandatory.");
        }
        if (estimatedDay==null || estimatedDay.trim().equals("")){
            throw new IntegrationException("The estimatedDay field is mandatory.");
        }
        if (estimatedTime==null || estimatedTime.trim().equals("")){
            throw new IntegrationException("The estimatedTime field is mandatory.");
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
        String response = "<" + RESOURCE_TASK + " TASK_ID=\"" + getTaskId() + 
        	"\" RESOURCE_ID=\"" + getResourceId() + 
        	"\" ESTIMATED_YEAR=\"" + getEstimatedYear() +
        	"\" ESTIMATED_MONTH=\"" + getEstimatedMonth() +
        	"\" ESTIMATED_DAY=\"" + getEstimatedDay() + 
        	"\" ESTIMATED_TIME=\"" + getEstimatedTime() + "\"" +
        	super.toXML() + "/>";             
        return response; 
    }

    
    /* (non-Javadoc)
     * @see com.pandora.integration.Integration#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) {
        this.setTaskId(XmlDomParse.getAttributeTextByTag(node, "TASK_ID"));
        this.setResourceId(XmlDomParse.getAttributeTextByTag(node, "RESOURCE_ID"));
        this.setEstimatedYear(XmlDomParse.getAttributeTextByTag(node, "ESTIMATED_YEAR"));
        this.setEstimatedMonth(XmlDomParse.getAttributeTextByTag(node, "ESTIMATED_MONTH"));
        this.setEstimatedDay(XmlDomParse.getAttributeTextByTag(node, "ESTIMATED_DAY"));
        this.setEstimatedTime(XmlDomParse.getAttributeTextByTag(node, "ESTIMATED_TIME"));
        super.fromXML(node);
    }
  

    ///////////////////////////////////////////    
    public String getEstimatedDay() {
        return estimatedDay;
    }
    public void setEstimatedDay(String newValue) {
        this.estimatedDay = newValue;
    }


    ///////////////////////////////////////////    
    public String getEstimatedMonth() {
        return estimatedMonth;
    }
    public void setEstimatedMonth(String newValue) {
        this.estimatedMonth = newValue;
    }


    ///////////////////////////////////////////    
    public String getEstimatedYear() {
        return estimatedYear;
    }
    public void setEstimatedYear(String newValue) {
        this.estimatedYear = newValue;
    }


    ///////////////////////////////////////////    
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String newValue) {
        this.resourceId = newValue;
    }


    ///////////////////////////////////////////    
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String newValue) {
        this.taskId = newValue;
    }

    ///////////////////////////////////////////    
    public String getEstimatedTime() {
        return estimatedTime;
    }
    public void setEstimatedTime(String newValue) {
        this.estimatedTime = newValue;
    }
    
    public String toString(){
        return "taskId:" + taskId + 
        	" resourceId:" + resourceId + 
        	" estimatedYear:" + estimatedYear +
        	" estimatedMonth:" + estimatedMonth +
        	" estimatedDay:" + estimatedDay +
        	" estimatedTime:" + estimatedTime; 
    }

}
