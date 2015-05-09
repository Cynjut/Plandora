package com.pandora.gui.struts.form;

/**
 * This class handle the data of Task History Form
 */
public class HistTaskForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    /** Id of Task related */
    private String taskId;

    /** Item number of task selected by user in order to view the history comment */
    private String selectedIndex;
    
    /** Id of resource related (if history is related with ResourceTask object) */
    private String resourceId;
    
    /** Id of Requirement related with Task history */
    private String reqIdRelated;
    
    /** History comment of task status selected */
    private String historyComment;

        
    ////////////////////////////////////////    
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String newValue) {
        this.taskId = newValue;
    }
    
    ////////////////////////////////////////    
    public String getReqIdRelated() {
        return reqIdRelated;
    }
    public void setReqIdRelated(String newValue) {
        this.reqIdRelated = newValue;
    }
    
    ////////////////////////////////////////    
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String newValue) {
        this.resourceId = newValue;
    }
    
    ////////////////////////////////////////      
    public String getHistoryComment() {
        return historyComment;
    }
    public void setHistoryComment(String newValue) {
        this.historyComment = newValue;
    }
    
    ////////////////////////////////////////      
    public String getSelectedIndex() {
        return selectedIndex;
    }
    public void setSelectedIndex(String newValue) {
        this.selectedIndex = newValue;
    }
}
