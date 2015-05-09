package com.pandora.exception;

/**
 * This exception is thown when a invalid transition of a task state is done.<br>
 * See the Task state machine diagram into system documentation for more information.
 */
public class InvalidTaskStateTransitionException extends BusinessException {
    
	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public InvalidTaskStateTransitionException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public InvalidTaskStateTransitionException() {
        super("A invalid transition state of task 'state machine' was perfomed.");
    }    

    /**
     * Constructor
     * @param msg
     */
    public InvalidTaskStateTransitionException(String msg) {
        super(msg);
    }
    
}
