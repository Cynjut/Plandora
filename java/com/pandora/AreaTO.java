package com.pandora;

/**
 * This object it is a bean that represents an Area entity.
 */
public class AreaTO extends TransferObject{
    
	private static final long serialVersionUID = 1L;
	
    /** The Name of Area */
    private String name;
     
    /** The description of Area */
    private String description;
    
    /**
     * Constructor 
     */
    public AreaTO(){
    }

    /**
     * Constructor 
     */    
    public AreaTO(String id){
        this.setId(id);
    }
    
    
    /////////////////////////////////////////////
    public String getDescription(){
    	return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
}
