package com.pandora.exception;

/**
 * This exception is thown when an username already exists into data base 
 * during the insert action.
 */
public class AreaNameAlreadyExistsException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * @param e
     */
    public AreaNameAlreadyExistsException(Exception e) {
        super(e);
    }

    /**
     * 
     * @param e
     */
    public AreaNameAlreadyExistsException() {
        super("The process failed because the current area name already exists into data base.");
    }    
}
