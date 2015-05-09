package com.pandora;

/**
 * This object it is a bean that represents a Function entity. 
 */
public class FunctionTO extends TransferObject{

	private static final long serialVersionUID = 1L;

    /** The name of Function */
    private String name;
    
    /** The description of Function */
    private String description;

    
    /**
     * Constructor 
     */
    public FunctionTO(){
    }

    /**
     * Constructor 
     */    
    public FunctionTO(String id){
        this.setId(id);
    }
    
    
    //////////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
}
