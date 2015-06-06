package com.pandora;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

/**
 * This object it is a bean that represents a Resource entity. 
 */
public class ResourceTO extends CustomerTO {
    
	private static final long serialVersionUID = 1L;

    /** The value used by resource constant into data base*/
    public static final Integer ROLE_RESOURCE = new Integer(1);  

    /** The default value of resource capacity */
    public static final Integer DEFAULT_FULLDAY_CAPACITY = new Integer(480);
    
    
    /** This attribute allow/not allow that the resource to open the Agile board form */
    private Boolean canSelfAlloc = new Boolean(false);
        
    /** This attribute allow/not allow that the current resource see a customer related with requirement of current project */
    private Boolean canSeeCustomer = new Boolean(false);

    /** This attribute allow/not allow that the current resource to see the project repository */
    private Boolean canSeeRepository = new Boolean(false);

    /** This attribute allow/not allow that the current resource to see the project invoice form */
    private Boolean canSeeInvoice = new Boolean(false);

    
    private Vector<ResourceCapacityTO> resourceCapacityList;
    
    
    /**
     * Constructor 
     */
    public ResourceTO(){
    }

    /**
     * Constructor 
     */
    public ResourceTO(String id){
        this.setId(id);
    }

    
    public String getForwardLogin() {
        return "resourceHome";
    }
    

	public boolean getBoolCanSeeCustomer(){
	    boolean resp = false;
	    if (this.canSeeCustomer!=null){
	        resp = this.canSeeCustomer.booleanValue();    
	    }
	    return resp;
	}
	
	
	public boolean getBoolCanSelfAlloc(){
	    if (this.canSelfAlloc!=null){
	        return this.canSelfAlloc.booleanValue();    
	    } else {
	        return false;
	    }
	}

	public boolean getBoolCanSeeRepository(){
	    if (this.canSeeRepository!=null){
	        return this.canSeeRepository.booleanValue();    
	    } else {
	        return false;
	    }
	}
	
	public boolean getBoolCanSeeInvoice(){
	    if (this.canSeeInvoice!=null){
	        return this.canSeeInvoice.booleanValue();    
	    } else {
	        return false;
	    }
	}
	
    
    ///////////////////////////////////////////
    public Boolean getCanSeeRepository() {
        return canSeeRepository;
    }
    public void setCanSeeRepository(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }
        this.canSeeRepository = newValue;
    }
    

    ///////////////////////////////////////////
    public Boolean getCanSeeInvoice() {
        return canSeeInvoice;
    }
    public void setCanSeeInvoice(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }
        this.canSeeInvoice = newValue;
    }

    
    ///////////////////////////////////////////
    public Boolean getCanSelfAlloc() {
        return canSelfAlloc;
    }
    public void setCanSelfAlloc(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }
        this.canSelfAlloc = newValue;
    }
    
        
    ///////////////////////////////////////////    
    public Boolean getCanSeeCustomer() {
        return canSeeCustomer;
    }
    public void setCanSeeCustomer(Boolean newValue) {
        if (newValue==null){
            newValue = new Boolean(false);
        }        
        this.canSeeCustomer = newValue;
    }
    
    
    ///////////////////////////////////////////    
    public Vector<ResourceCapacityTO> getResourceCapacityList() {
		return resourceCapacityList;
	}
	public void setResourceCapacityList(Vector<ResourceCapacityTO> newValue) {
		this.resourceCapacityList = newValue;
	}


    ///////////////////////////////////////////    
    public Integer getCapacityPerDay(Timestamp currentDate) {
    	Integer response = DEFAULT_FULLDAY_CAPACITY;
    	if (this.resourceCapacityList!=null && currentDate!=null) {
    		Iterator<ResourceCapacityTO> i = this.resourceCapacityList.iterator();
    		Integer lastValue = DEFAULT_FULLDAY_CAPACITY;
    		while(i.hasNext()) {
    			ResourceCapacityTO rto = i.next();
    			if (currentDate.before(rto.getDate())) {    			
    				response = lastValue;
    				break;
    			}
    			lastValue = rto.getCapacity();
    		}
    	}
    	
        return response;
    }
    
	
	/**
     * Constructor 
     */    
    public ResourceTO(CustomerTO parent){
        this.setId(parent.getId());
        this.setProject(parent.getProject());
        this.setArea(parent.getArea());
        this.setColor(parent.getColor());
        this.setDepartment(parent.getDepartment());
        this.setEmail(parent.getEmail());
        this.setBirth(parent.getBirth());
        this.setCompany(parent.getCompany());
        this.setAuthenticationMode(parent.getAuthenticationMode());
        this.setPermission(parent.getPermission());
        this.setFunction(parent.getFunction());
        this.setName(parent.getName());
        this.setPassword(parent.getPassword());
        this.setPhone(parent.getPhone());
        this.setUsername(parent.getUsername());
        this.setFinalDate(parent.getFinalDate());
        this.canSelfAlloc = new Boolean(false);
        this.canSeeRepository = new Boolean(true);
        this.canSeeInvoice = new Boolean(false);
        this.setIsDisabled(parent.getIsDisabled());
        this.setIsReqAcceptable(parent.getIsReqAcceptable());
        this.setCanSeeTechComments(parent.getCanSeeTechComments());
        this.setCanSeeDiscussion(parent.getCanSeeDiscussion());
        this.setCanSeeOtherReqs(parent.getCanSeeOtherReqs());
        this.setCanOpenOtherOwnerReq(parent.getCanOpenOtherOwnerReq());
        this.setPreApproveReq(parent.getPreApproveReq());
        this.setProjectFunction(parent.getProjectFunction());        
        this.canSeeCustomer = new Boolean(false);
        this.setRoles(parent.getRoles());
        this.setPreference(parent.getPreference());
    }
    
    
}
