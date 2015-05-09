package com.pandora;

public class ProjectStatusTO extends TransferObject{
    
	private static final long serialVersionUID = 1L;

    /** The 'Open' state machine */
    public static final Integer STATE_MACHINE_OPEN = new Integer(1);

    /** The 'on-Hold' state machine */
    public static final Integer STATE_MACHINE_HOLD = new Integer(2);
    
    /** The 'Close' and 'Abort' state machine */
    public static final Integer STATE_MACHINE_CLOSE_ABORT = new Integer(3);

    
    /** Name of Project Status */
    private String name;
    
    /** Note related with Project Status */
    private String note;
    
    /** The state machine order is used by system to define the role os specific ProjectStatus. 
     * <li> 1 - the initial status of Project. Ex.: 'Open' status </li>
     * <li> 2 - the normal final status of Project. Ex.: 'Closed' status </li>
     * <li> 4 - the forced final status of Project. Ex.: 'Abort' status </li>
     */
    private Integer stateMachineOrder;
    
    /**
     * Constructor
     */
    public ProjectStatusTO(){
    }

    /**
     * Constructor
     * @param id
     */
    public ProjectStatusTO(String id){
        this.setId(id);
    }
    
    ////////////////////////////////////
    public String getName() {
        return name;
    }
    public void setName(String newvalue) {
        this.name = newvalue;
    }
    
    ////////////////////////////////////    
    public String getNote() {
        return note;
    }
    public void setNote(String newvalue) {
        this.note = newvalue;
    }

    ////////////////////////////////////    
    public Integer getStateMachineOrder() {
        return stateMachineOrder;
    }
    public void setStateMachineOrder(Integer newvalue) {
        this.stateMachineOrder = newvalue;
    }
    
}
