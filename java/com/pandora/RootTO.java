package com.pandora;

/**
 * This object it is a bean that represents a Root entity. 
 */
public class RootTO extends LeaderTO {

	private static final long serialVersionUID = 1L;

    /** The value used by root constant into data base*/
    public static final Integer ROLE_ROOT = new Integer(1);

    /** The username of root used by system should be 'root' */
    public static final String ROOT_USER = "root";
    
    
    /**
     * Constructor 
     */
    public RootTO(){
    }

    /**
     * Constructor 
     */
    public RootTO(String id){
        this.setId(id);
    }
    
    /**
     * Constructor 
     */    
    public RootTO(LeaderTO parent){
        this.setId(parent.getId());
        this.setProject(parent.getProject());
        this.setArea(parent.getArea());
        this.setColor(parent.getColor());
        this.setDepartment(parent.getDepartment());
        this.setEmail(parent.getEmail());
        this.setFunction(parent.getFunction());
        this.setName(parent.getName());
        this.setPassword(parent.getPassword());
        this.setPhone(parent.getPhone());
        this.setUsername(parent.getUsername());
        this.setFinalDate(parent.getFinalDate());
        this.setPreApproveReq(parent.getPreApproveReq());
        this.setPreference(parent.getPreference());
        this.setProjectFunction(parent.getProjectFunction());
        this.setCanSeeInvoice(parent.getCanSeeInvoice());
        this.setCanSeeRepository(parent.getCanSeeRepository());        
    }
    
    /**
     * Constructor 
     */    
    public RootTO(UserTO parent){
        this.setId(parent.getId());
        this.setArea(parent.getArea());
        this.setColor(parent.getColor());
        this.setDepartment(parent.getDepartment());
        this.setEmail(parent.getEmail());
        this.setFunction(parent.getFunction());
        this.setName(parent.getName());
        this.setPassword(parent.getPassword());
        this.setPhone(parent.getPhone());
        this.setUsername(parent.getUsername());
        this.setPreference(parent.getPreference());
    }    
    
    
    public String getForwardLogin() {
        return "rootHome";
    }
}
