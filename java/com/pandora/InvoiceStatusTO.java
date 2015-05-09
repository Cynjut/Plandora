package com.pandora;

public class InvoiceStatusTO extends TransferObject {

	private static final long serialVersionUID = 1L;
	
    public static final Integer STATE_MACHINE_NEW    = new Integer(1);
    public static final Integer STATE_MACHINE_PAID   = new Integer(100);
    public static final Integer STATE_MACHINE_CANCEL = new Integer(101);
    public static final Integer STATE_MACHINE_REVIEW = new Integer(40);
    public static final Integer STATE_MACHINE_SUBMIT = new Integer(50);

    
    private String name;
    
    private String description;

    private Integer stateMachineOrder;
    
    
    /**
     * Constructor 
     */
    public InvoiceStatusTO(){
    }

    /**
     * Constructor 
     */    
    public InvoiceStatusTO(String id){
        this.setId(id);
    }
    
    
        
    /**
     * Return true if the current StateMachineOrder attribute is 
     * related with status of finished invoice
     */
    public boolean isFinish(){
        boolean response = false;
        if (this.getStateMachineOrder()!=null) {
            response = (this.getStateMachineOrder().equals(STATE_MACHINE_CANCEL)
                    || this.getStateMachineOrder().equals(STATE_MACHINE_PAID));
        }
        return response;
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
    
    ////////////////////////////////////////////////    
    public Integer getStateMachineOrder() {
        return stateMachineOrder;
    }
    public void setStateMachineOrder(Integer newValue) {
        this.stateMachineOrder = newValue;
    }    
	
}
