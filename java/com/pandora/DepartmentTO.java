package com.pandora;

/**
 * This object it is a bean that represents a Department entity. 
 */
public class DepartmentTO extends TransferObject {
    
	private static final long serialVersionUID = 1L;

    /** The name of department */
    private String name;
    
    /** The description of department */
    private String description;
    
    /**
     * Constructor 
     */
    public DepartmentTO(){
    }

    /**
     * Constructor 
     */    
    public DepartmentTO(String id){
        this.setId(id);
    }    
    
    /////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    /////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
}
