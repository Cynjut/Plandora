package com.pandora;

import java.sql.Timestamp;

import com.pandora.helper.DateUtil;

/**
 * This object it is a bean that represents the Requirement History entity.
 */
public class RequirementHistoryTO  extends TransferObject{
    
	private static final long serialVersionUID = 1L;

    /** Current resource envolved with requirement 'step' */
    private UserTO resource;
    
    /** Id of related Requirement */
    private String requirementId;

    /** Current status of requirement */
    private RequirementStatusTO status;
    
    /** Date of current requirement 'step' */
    private Timestamp date;

    private String iteration;

    /** Comment related with current requirement status */
    private String comment;
    
    
    public RequirementHistoryTO() {
	}

    
    public RequirementHistoryTO(RequirementTO rto) {
    	this.setRequirementId(rto.getId());
    	this.setDate(DateUtil.getNow());
    	this.setResource(rto.getRequester());
    	this.setStatus(rto.getRequirementStatus());
    	this.setIteration(rto.getIteration());
	}

    
    //////////////////////////////////////////
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp newValue) {
        this.date = newValue;
    }
    
    //////////////////////////////////////////    
    public String getRequirementId() {
        return requirementId;
    }
    public void setRequirementId(String newValue) {
        this.requirementId = newValue;
    }

    //////////////////////////////////////////    
    public UserTO getResource() {
        return resource;
    }
    public void setResource(UserTO newValue) {
        this.resource = newValue;
    }
    
    //////////////////////////////////////////    
    public RequirementStatusTO getStatus() {
        return status;
    }
    public void setStatus(RequirementStatusTO newValue) {
        this.status = newValue;
    }
    
    //////////////////////////////////////////    
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }

    
    //////////////////////////////////////////
	public String getIteration() {
		return iteration;
	}
	public void setIteration(String newValue) {
		this.iteration = newValue;
	}
    
    
}
