package com.pandora.exception;

/**
 * This exception is thown when a task is trying to be inserted but the requirement related is invalid.
 */
public class InvalidRequirementException  extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public InvalidRequirementException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public InvalidRequirementException() {
        super("The current status of requirement related is closed or aborted");
    }    

}
