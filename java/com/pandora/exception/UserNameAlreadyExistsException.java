package com.pandora.exception;

/**
 * This exception is thown when an username already exists into data base 
 * during the insert action.
 */
public class UserNameAlreadyExistsException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * @param e
     */
    public UserNameAlreadyExistsException(Exception e) {
        super(e);
    }

    /**
     * 
     * @param e
     */
    public UserNameAlreadyExistsException() {
        super("The process failed because the current username already exists into data base.");
    }    
}
