package com.pandora;


/**
 * This object it is a bean that represents a Leader entity. 
 */
public class LeaderTO extends ResourceTO {

    /** The value used by leader constant into data base*/
    public static final Integer ROLE_LEADER = new Integer(2);  

	private static final long serialVersionUID = 1L;

    
    /**
     * Constructor 
     */
    public LeaderTO(){
    }

    /**
     * Constructor 
     */
    public LeaderTO(String id){
        this.setId(id);
    }
    
    /**
     * Constructor 
     */    
    public LeaderTO(ResourceTO parent){
        this.setId(parent.getId());
        this.setProject(parent.getProject());
        this.setArea(parent.getArea());
        this.setColor(parent.getColor());
        this.setBirth(parent.getBirth());
        this.setAuthenticationMode(parent.getAuthenticationMode());
        this.setPermission(parent.getPermission());
        this.setDepartment(parent.getDepartment());
        this.setEmail(parent.getEmail());
        this.setFunction(parent.getFunction());
        this.setName(parent.getName());
        this.setPassword(parent.getPassword());
        this.setPhone(parent.getPhone());
        this.setUsername(parent.getUsername());
        this.setFinalDate(parent.getFinalDate());
        this.setIsDisabled(parent.getIsDisabled());
        this.setIsReqAcceptable(parent.getIsReqAcceptable());
        this.setCanSeeTechComments(parent.getCanSeeTechComments());
        this.setCanSeeDiscussion(parent.getCanSeeDiscussion());
        this.setCanSeeOtherReqs(parent.getCanSeeOtherReqs());  
        this.setCanOpenOtherOwnerReq(parent.getCanOpenOtherOwnerReq());
        this.setPreApproveReq(parent.getPreApproveReq());
        this.setPreApproveReq(parent.getPreApproveReq());
        this.setRoles(parent.getRoles());
        this.setCanSeeCustomer(parent.getCanSeeCustomer());
        this.setCanSelfAlloc(parent.getCanSelfAlloc());
        this.setPreference(parent.getPreference());
        this.setProjectFunction(parent.getProjectFunction());
        this.setCanSeeInvoice(parent.getCanSeeInvoice());
        this.setCanSeeRepository(parent.getCanSeeRepository());
    }
    
    /**
     * Constructor 
     */    
    public LeaderTO(UserTO parent){
        this.setId(parent.getId());
        this.setArea(parent.getArea());
        this.setColor(parent.getColor());
        this.setDepartment(parent.getDepartment());
        this.setEmail(parent.getEmail());
        this.setBirth(parent.getBirth());
        this.setFunction(parent.getFunction());
        this.setName(parent.getName());
        this.setPassword(parent.getPassword());
        this.setPhone(parent.getPhone());
        this.setUsername(parent.getUsername());
        this.setPreference(parent.getPreference());
    }    
    
    public String getForwardLogin() {
        return "leaderHome";
    }
    
    
}
