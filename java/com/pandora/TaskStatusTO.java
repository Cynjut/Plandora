package com.pandora;

/**
 * This object it is a bean that represents a Task Status entity.
 */
public class TaskStatusTO extends TransferObject{
    
	private static final long serialVersionUID = 1L;

    public static final Integer STATE_MACHINE_OPEN     = new Integer(1);
    public static final Integer STATE_MACHINE_REOPEN   = new Integer(2);
    public static final Integer STATE_MACHINE_CLOSE    = new Integer(100);
    public static final Integer STATE_MACHINE_CANCEL   = new Integer(101);
    public static final Integer STATE_MACHINE_PROGRESS = new Integer(20);
    public static final Integer STATE_MACHINE_HOLD     = new Integer(50);

    
    /** The Name of Task Status */
    private String name;
    
    /** The description of Task Status*/
    private String description;

    /** The state machine order is used by system to define the meaning of specific Task Status. 
     * <li> 1 - the initial status of Task. Ex.: 'Open' status </li>
     * <li> 2 - the initial status of Task. Ex.: 'Reopen' status </li>
     * <li> 100 - the normal final status of Task. Ex.: 'Closed' status </li>
     * <li> 101 - the forced final status by leader or resource of Task. Ex.: 'Cancel' status </li>
     */
    private Integer stateMachineOrder;
    
    /**
     * Constructor 
     */
    public TaskStatusTO(){
    }

    /**
     * Constructor 
     */    
    public TaskStatusTO(String id){
        this.setId(id);
    }
    
    
    /**
     * Return true if the current StateMachineOrder attribute 
     * is STATE_MACHINE_OPEN or STATE_MACHINE_REOPEN
     */
    public boolean isOpen(){
        boolean response = false;
        if (this.getStateMachineOrder()!=null){
            response = this.getStateMachineOrder().equals(STATE_MACHINE_OPEN) ||
            			this.getStateMachineOrder().equals(STATE_MACHINE_REOPEN);
        }
        return response;
    }

    
    public boolean isOpenNotReopened(){
        boolean response = false;
        if (this.getStateMachineOrder()!=null){
            response = this.getStateMachineOrder().equals(STATE_MACHINE_OPEN);
        }
        return response;
    }
    
    /**
     * Return true if the current StateMachineOrder attribute is 
     * related with status of finished task (STATE_MACHINE_CANCEL 
     * or STATE_MACHINE_CLOSE) 
     * @return
     */
    public boolean isFinish(){
        boolean response = false;
        if (this.getStateMachineOrder()!=null) {
            response = (this.getStateMachineOrder().equals(STATE_MACHINE_CANCEL)
                    || this.getStateMachineOrder().equals(STATE_MACHINE_CLOSE));
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
