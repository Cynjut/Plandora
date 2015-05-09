package com.pandora;

/**
 * This object is used by process that changes the state of requirement.
 */
public class RequirementTriggerTO extends TransferObject{

	private static final long serialVersionUID = 1L;

    /** The new state of requirement */
    private Integer newState;
    
    /** The comment related with state transiction */
    private String comment;
    
    
    /**
     * Constructor 
     */
    public RequirementTriggerTO(){
    }

    /**
     * Constructor 
     */    
    public RequirementTriggerTO(String c, Integer s){
        this.setComment(c);
        this.setNewState(s);
    }

    //////////////////////////////////////
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }
    
    //////////////////////////////////////    
    public Integer getNewState() {
        return newState;
    }
    public void setNewState(Integer newValue) {
        this.newState = newValue;
    }
}
