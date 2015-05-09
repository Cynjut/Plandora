package com.pandora;

public class CostStatusTO extends TransferObject {

	public static final Integer STATE_MACHINE_WAITING  = new Integer(1);
    public static final Integer STATE_MACHINE_BUDGETED = new Integer(10);
    public static final Integer STATE_MACHINE_PAID     = new Integer(100);
    public static final Integer STATE_MACHINE_CANCELED = new Integer(101);

    
	private static final long serialVersionUID = 1L;

    private String name;
    
    private String description;

    private Integer stateMachineOrder;
    
    
    /**
     * Constructor 
     */
    public CostStatusTO(){
    }

    /**
     * Constructor 
     */    
    public CostStatusTO(String id){
        this.setId(id);
    }
    
    
        
    /**
     * Return true if the current StateMachineOrder attribute is 
     * related with status of finished cost
     */
    /*
    public boolean isFinish(){
        boolean response = false;
        if (this.getStateMachineOrder()!=null) {
            response = (this.getStateMachineOrder().equals(STATE_MACHINE_ACCEPTED)
                    || this.getStateMachineOrder().equals(STATE_MACHINE_DENIED));
        }
        return response;
    }    
    */
    
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
    
    ////////////////////////////////////////////////    
    public Integer getStateMachineOrder() {
        return stateMachineOrder;
    }
    public void setStateMachineOrder(Integer newValue) {
        this.stateMachineOrder = newValue;
    }    
}
