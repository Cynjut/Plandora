package com.pandora.exception;

public class ProjectTasksDiffRequirementException extends BusinessException {

	private static final long serialVersionUID = 1L;

	
    /**
     * Constructor
     */
    public ProjectTasksDiffRequirementException(Exception e) {
        super(e);
    }

    
    /**
     * Constructor
     */
    public ProjectTasksDiffRequirementException() {
        super("The project of a task and the project of requirement are different.");
    }    

}
