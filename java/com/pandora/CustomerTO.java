package com.pandora;

import java.util.Vector;

/**
 * This object it is a bean that represents a Customer entity. 
 */
public class CustomerTO extends UserTO {
    
	private static final long serialVersionUID = 1L;

    /** The value used by customer constant into data base*/
    public static final Integer ROLE_CUSTOMER = new Integer(0);  

    /** The project related with Customer role object */
    private ProjectTO lnkProject;
    
    /** This attribute allow/not the current customer to show a specific project */ 
    private Boolean isDisabled = new Boolean(false);
    
    /** This attribute allow/not the current customer to accept a requirement */ 
    private Boolean isReqAcceptable = new Boolean(false);
    
    /** This attribute allow/not allow the current customer view the techinical comments related with requirement of current project */
    private Boolean canSeeTechComments = new Boolean(false);
    
    /** This attribute allow/not allow the customer to create an automatic task after the requirement creation. */
    private Boolean preApproveReq = new Boolean(false);
    
    private Boolean canSeeDiscussion = new Boolean(false);
    
    private FunctionTO projectFunctionId = null;
    
    private Boolean canSeeOtherReqs = new Boolean(false);
    
    private Boolean canOpenOtherOwnerReq = new Boolean(false);
    
    private Vector<CustomerFunctionTO> roles;
    
    
    
    /**
     * Constructor 
     */
    public CustomerTO(){
    }

    /**
     * Constructor 
     */
    public CustomerTO(String id){
        this.setId(id);
    }
    
    /**
     * Constructor 
     */    
    public CustomerTO(UserTO parent){
        this.setId(parent.getId());
        this.lnkProject = null;
        this.setArea(parent.getArea());
        this.setColor(parent.getColor());
        this.setDepartment(parent.getDepartment());
        this.setEmail(parent.getEmail());
        this.setAuthenticationMode(parent.getAuthenticationMode());
        this.setPermission(parent.getPermission());
        this.setBirth(parent.getBirth());
        this.setCompany(parent.getCompany());
        this.setFunction(parent.getFunction());
        this.setName(parent.getName());
        this.setPassword(parent.getPassword());
        this.setPhone(parent.getPhone());
        this.setUsername(parent.getUsername());
        this.setFinalDate(parent.getFinalDate());
        this.setLanguage(parent.getLanguage());
        this.setCountry(parent.getCountry());
        this.setBundle(parent.getBundle());
        this.canSeeTechComments = new Boolean(false);
        this.isDisabled = new Boolean(false);
        this.isReqAcceptable = new Boolean(false);
        this.preApproveReq = new Boolean(false);
        this.canSeeDiscussion = new Boolean(false);
        this.projectFunctionId = null;
        this.canSeeOtherReqs = new Boolean(false);
    }
    
    
    public String getForwardLogin() {
        return "customerHome";
    }

    
	public boolean getBoolIsDisabled(){
	    boolean resp = false;
	    if (this.isDisabled!=null){
	        resp = this.isDisabled.booleanValue();    
	    }
	    return resp;
	}

	public boolean getBoolIsReqAcceptable(){
	    boolean resp = false;
	    if (this.isReqAcceptable!=null){
	        resp = this.isReqAcceptable.booleanValue();
	    }
        return resp;
	}	
	
	public boolean getBoolCanSeeTechComments(){
	    boolean resp = false;
	    if (this.canSeeTechComments!=null){
	        resp = this.canSeeTechComments.booleanValue();    
	    }
	    return resp;
	}
	
	
	public boolean getBoolPreApproveReq(){
	    boolean resp = false;
	    if (this.preApproveReq!=null){
	        resp = this.preApproveReq.booleanValue();    
	    }
	    return resp;
	}

	public int getIntPreApproveReq(){
	    int resp = 0;
	    if (this.preApproveReq!=null){
	        resp = (this.preApproveReq.booleanValue()?1:0);
	    }
        return resp;
	}
	
	public int getIntCanSeeDiscussion(){
	    int resp = 0;
	    if (this.canSeeDiscussion!=null){
	        resp = (this.canSeeDiscussion.booleanValue()?1:0);
	    }
        return resp;
	}

	public boolean getBoolCanSeeDiscussion(){
	    boolean resp = false;
	    if (this.canSeeDiscussion!=null){
	        resp = this.canSeeDiscussion.booleanValue();    
	    }
	    return resp;
	}	
	
	public int getIntCanSeeOtherReqs(){
	    int resp = 0;
	    if (this.canSeeOtherReqs!=null){
	        resp = (this.canSeeOtherReqs.booleanValue()?1:0);
	    }
        return resp;
	}

	public boolean getBoolCanSeeOtherReqs(){
	    boolean resp = false;
	    if (this.canSeeOtherReqs!=null){
	        resp = this.canSeeOtherReqs.booleanValue();    
	    }
	    return resp;
	}	
	

	public int getIntCanOpenOtherOwnerReq(){
	    int resp = 0;
	    if (this.canOpenOtherOwnerReq!=null){
	        resp = (this.canOpenOtherOwnerReq.booleanValue()?1:0);
	    }
        return resp;
	}

	public boolean getBoolCanOpenOtherOwnerReq(){
	    boolean resp = false;
	    if (this.canOpenOtherOwnerReq!=null){
	        resp = this.canOpenOtherOwnerReq.booleanValue();    
	    }
	    return resp;
	}	

	
    ///////////////////////////////////////////
    public Boolean getPreApproveReq() {
        return preApproveReq;
    }
    public void setPreApproveReq(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }
        this.preApproveReq = newValue;
    }
	
	
    ////////////////////////////////////////////
    public ProjectTO getProject() {
        return lnkProject;
    }
    public void setProject(ProjectTO newValue) {
        this.lnkProject = newValue;
    }
    
    ////////////////////////////////////////////    
    public Boolean getIsDisabled() {
        return isDisabled;
    }
    public void setIsDisabled(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }        
        this.isDisabled = newValue;
    }

    ////////////////////////////////////////////    
    public Boolean getIsReqAcceptable() {
        return isReqAcceptable;
    }
    public void setIsReqAcceptable(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }         
        this.isReqAcceptable = newValue;
    }

    ///////////////////////////////////////////    
    public Boolean getCanSeeTechComments() {
        return canSeeTechComments;
    }
    public void setCanSeeTechComments(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }             
        this.canSeeTechComments = newValue;
    }

    ///////////////////////////////////////////     
	public Boolean getCanSeeDiscussion() {
		return canSeeDiscussion;
	}
	public void setCanSeeDiscussion(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }             
		this.canSeeDiscussion = newValue;
	}

	
    ///////////////////////////////////////////
	public String getProjectFunctionId() {
		String response = "-1";
		if (projectFunctionId!=null) {
			response = projectFunctionId.getId();
		}
		return response; 
	}
	public FunctionTO getProjectFunction() {
		return projectFunctionId;
	}
	public void setProjectFunction(FunctionTO newValue) {
		this.projectFunctionId = newValue;
	}

	
    ///////////////////////////////////////////     	
	public Boolean getCanSeeOtherReqs() {
		return canSeeOtherReqs;
	}

	public void setCanSeeOtherReqs(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }             		
		this.canSeeOtherReqs = newValue;
	}
    
	
    ///////////////////////////////////////////	
    public Boolean getCanOpenOtherOwnerReq() {
		return canOpenOtherOwnerReq;
	}
	public void setCanOpenOtherOwnerReq(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }             		
		this.canOpenOtherOwnerReq = newValue;
	}
	

	///////////////////////////////////////////
	public Vector<CustomerFunctionTO> getRoles() {
		return roles;
	}
	public void setRoles(Vector<CustomerFunctionTO> roles) {
		this.roles = roles;
	}
    
}
