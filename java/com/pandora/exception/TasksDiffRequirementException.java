package com.pandora.exception;

/**
 * This exception is thown when a task is trying to be inserted or updated but her
 * parent task is related with a different requirement.
 */
public class TasksDiffRequirementException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public TasksDiffRequirementException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public TasksDiffRequirementException() {
        super("A task and his parent task cannot have differents requirement.");
    }    

}
