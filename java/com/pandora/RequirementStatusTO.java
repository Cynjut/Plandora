package com.pandora;

/**
 * This object it is a bean that represents the Requirement Status entity. 
 */
public class RequirementStatusTO extends TransferObject{
    
	private static final long serialVersionUID = 1L;

    /** The 'Waiting Approve' state machine */
    public static final Integer STATE_MACHINE_WAITING = new Integer(1);

    /** The 'Planned' state machine */
    public static final Integer STATE_MACHINE_PLANNED = new Integer(100);
    
    /** The 'Cancel' state machine */
    public static final Integer STATE_MACHINE_CANCEL = new Integer(200);
    
    /** The 'Close' state machine */
    public static final Integer STATE_MACHINE_CLOSE = new Integer(201);
    
    /** The 'Reject' state machine */
    public static final Integer STATE_MACHINE_REFUSE = new Integer(202);

    /** The 'in-Progress' state machine */
    public static final Integer STATE_MACHINE_PROGRESS = new Integer(300);

    
    /** Name of Requirement Status (used by GUI to show into combos and grids) */
    private String name;
    
    /** Description of Requirement Status */
    private String description;

    /** The state machine order is used by system to define the meanning of specific RequirementStatus. 
     * <li> 1 - the initial status of Requirement. Ex.: 'Waiting Approve' status </li>
     * <li> 100 - means that the Requirement is scheduled to be executed. Ex.: 'Planning' status </li>
     * <li> 200 - the forced final status by cliente of Requirement. Ex.: 'Cancelled' status </li>
     * <li> 201 - the normal final status of Requirement. Ex.: 'Closed' status </li>
     * <li> 202 - the forced final status by leader of Requirement. Ex.: 'Rejected' status </li>
     * <li> 300 - means that the task related of Requirement is in-progress.</li>
     */
    private Integer stateMachineOrder;
    
    
    ////////////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    ////////////////////////////////////////////////    
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
    
	public boolean isFinished() {
		boolean response = false;
		Integer state = this.getStateMachineOrder();
		if (state!=null) {
			response = (state.equals(RequirementStatusTO.STATE_MACHINE_CANCEL) ||
							state.equals(RequirementStatusTO.STATE_MACHINE_CLOSE) ||
							state.equals(RequirementStatusTO.STATE_MACHINE_REFUSE));			
		}
		return response;
	}
    
}
