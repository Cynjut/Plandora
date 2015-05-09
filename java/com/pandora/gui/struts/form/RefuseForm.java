package com.pandora.gui.struts.form;

/**
 * This class handle the data of Refusing Form
 */
public class RefuseForm  extends GeneralStrutsForm{
    
	private static final long serialVersionUID = 1L;
	
    /** Used by refuseType attribute and means that the form should refused a requirement */
    public static final String REFUSE_REQ = "REQ";
    
    /** Used by refuseType attribute and means that the form should cancel a task */    
    public static final String CANCEL_TSK = "TSK";

    
    /** The reason of requirement refusing */
    private String comment;
    
    /** Id of object (requirement, task, etc) selected into form to be refused */
    private String refusedId = null;

    /** Define if current form should be used to refuse a requirement or a task */
    private String refuseType = REFUSE_REQ;
        
    private String forwardAfterRefuse = null;
    
    private String relatedRequirementId = null;
    
    private boolean reopenReqAfterTaskCancelation;
    
    
    ////////////////////////////////////////////
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }
    
    ////////////////////////////////////////////	
    public String getRefusedId() {
        return refusedId;
    }
    public void setRefusedId(String newValue) {
        this.refusedId = newValue;
    }

    ////////////////////////////////////////////    
    public String getRefuseType() {
        return refuseType;
    }
    public void setRefuseType(String newValue) {
        this.refuseType = newValue;
    }
    
	////////////////////////////////////////////
	public String getForwardAfterRefuse() {
		return forwardAfterRefuse;
	}
	public void setForwardAfterRefuse(String newValue) {
		this.forwardAfterRefuse = newValue;
	}

	
	////////////////////////////////////////////
	public String getRelatedRequirementId() {
		return relatedRequirementId;
	}
	public void setRelatedRequirementId(String newValue) {
		this.relatedRequirementId = newValue;
	}


	////////////////////////////////////////////
	public boolean getReopenReqAfterTaskCancelation() {
		return reopenReqAfterTaskCancelation;
	}
	public void setReopenReqAfterTaskCancelation(boolean newValue) {
		this.reopenReqAfterTaskCancelation = newValue;
	}
    
    
}
