package com.pandora;

import java.sql.Timestamp;

/**
 * This object it is a bean that represents the Task History entity.
 */
public class TaskHistoryTO  extends TransferObject{
        
	private static final long serialVersionUID = 1L;

    /** Current user (resource or leader) related to the task 'step' */
    private UserTO handler;
    
    /** Id of related resource task */    
    private ResourceTaskTO resourceTask;
    
    /** Current status of task */
    private TaskStatusTO status;
    
    /** Date of current Requirement 'step' */
    private Timestamp date;
    
    /** Comment related with current Requirement status */
    private String comment;
    
    private OccurrenceTO iteration;
    
    
    /** The actual initial date of task related with current task step */    
    private Timestamp actualDate;

    /** The actual time (in minutes) of task related with current task step */    
    private Integer actualTime;
    
    /** The estimated time (in minutes) of task related with current task step */
    private Integer estimatedTime; 
    
    /** The initial date of task related with current task step */
    private Timestamp startDate;
    
    
    //////////////////////////////////////////    
    public UserTO getHandler() {
        return handler;
    }
    public void setHandler(UserTO newValue) {
        this.handler = newValue;
    }
    
    //////////////////////////////////////////    
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }
    
    
    //////////////////////////////////////////     
    public OccurrenceTO getIteration() {
		return iteration;
	}
	public void setIteration(OccurrenceTO newValue) {
		this.iteration = newValue;
	}
	
	
	//////////////////////////////////////////    
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp newValue) {
        this.date = newValue;
    }
        
    //////////////////////////////////////////    
    public TaskStatusTO getStatus() {
        return status;
    }
    public void setStatus(TaskStatusTO newValue) {
        this.status = newValue;
    }
    
    //////////////////////////////////////////    
    public ResourceTaskTO getResourceTask() {
        return resourceTask;
    }
    public void setResourceTask(ResourceTaskTO newValue) {
        this.resourceTask = newValue;
    }
    
    ////////////////////////////////////////
    public float getEstimatedTimeInHours() {
        int min = 0;
        if (estimatedTime!=null){
            min = estimatedTime.intValue();
        }        
        return ((float)min/60);
    }        
    public Integer getEstimatedTime() {
        return estimatedTime;
    }
    public void setEstimatedTime(Integer newValue) {
        this.estimatedTime = newValue;
    }

    ////////////////////////////////////////
    public Timestamp getStartDate() {
        return startDate;
    }
    public void setStartDate(Timestamp newValue) {
        this.startDate = newValue;
    }

	////////////////////////////////////////    
    public Timestamp getActualDate() {
        return actualDate;
    }
    public void setActualDate(Timestamp newValue) {
        this.actualDate = newValue;
    }
    
	////////////////////////////////////////    
    public float getActualTimeInHours() {
        int min = 0;
        if (actualTime!=null){
            min = actualTime.intValue();
        }
        return ((float)min/60);
    }        
    public Integer getActualTime() {
        return actualTime;    
    }
    public void setActualTime(Integer newValue) {
        this.actualTime = newValue;
    }
    
}
