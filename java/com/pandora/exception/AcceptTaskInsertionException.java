package com.pandora.exception;

/**
 * This exception is thrown when a acceptance task cannot be inserted into data base.
 */
public class AcceptTaskInsertionException extends DataAccessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public AcceptTaskInsertionException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public AcceptTaskInsertionException() {
        super("The acceptance task cannot be inserted into data base. Check if the requester has the resource role into current project.");
    }    

}
