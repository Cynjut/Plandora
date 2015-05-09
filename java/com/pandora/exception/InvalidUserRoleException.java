package com.pandora.exception;

/**
 * This exception is thown when an user don't has any role and trying to access the system.
 */
public class InvalidUserRoleException extends BusinessException {
   
	private static final long serialVersionUID = 1L;

    /**
     * @param e
     */
    public InvalidUserRoleException(Exception e) {
        super(e);
    }

    /**
     * 
     * @param e
     */
    public InvalidUserRoleException() {
        super("The current user don't has any role into data base to access the system");
    }    

}
