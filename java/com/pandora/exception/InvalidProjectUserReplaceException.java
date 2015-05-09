package com.pandora.exception;

/**
 * This exception is thown when an user cannot be replaced or removed from project.
 */
public class InvalidProjectUserReplaceException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public InvalidProjectUserReplaceException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public InvalidProjectUserReplaceException() {
        super("A user cannot be removed from project, because it related with requirements or tasks into project.");
    }    

    /**
     * Constructor
     * @param e
     */
    public InvalidProjectUserReplaceException(String msg) {
        super(msg);
    }    
    
}
