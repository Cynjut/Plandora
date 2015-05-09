package com.pandora.gui.struts.form;

/**
 * This class handle the data of Requirement History Form
 */
public class HistReqForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** Id of requirement related */
    private String reqId;
    
    /** Item number of requirement selected by user in order to view the history comment */
    private String selectedIndex;

    /** Description of Current Requirement */
    private String descRequirement;
    
    /** The estimated deadline for the requirement */
    private String deadlineDateTime;

    /** The suggested deadline by customer */
    private String deadlineSuggested;
    
    /** History comment of requirement status selected */
    private String historyComment;
    
    
    /////////////////////////////////////
    public String getReqId() {
        return reqId;
    }
    public void setReqId(String newValue) {
        this.reqId = newValue;
    }

    /////////////////////////////////////
    public String getSelectedIndex() {
        return selectedIndex;
    }
    public void setSelectedIndex(String newValue) {
        this.selectedIndex = newValue;
    }

    /////////////////////////////////////    
    public String getDeadlineDateTime() {
        return deadlineDateTime;
    }
    public void setDeadlineDateTime(String newValue) {
        this.deadlineDateTime = newValue;
    }
    
    /////////////////////////////////////    
    public String getDeadlineSuggested() {
        return deadlineSuggested;
    }
    public void setDeadlineSuggested(String newValue) {
        this.deadlineSuggested = newValue;
    }

    /////////////////////////////////////        
    public String getDescRequirement() {
        return descRequirement;
    }
    public void setDescRequirement(String newValue) {
        this.descRequirement = newValue;
    }
    
    /////////////////////////////////////       
    public String getHistoryComment() {
        return historyComment;
    }
    public void setHistoryComment(String newValue) {
        this.historyComment = newValue;
    }
}
