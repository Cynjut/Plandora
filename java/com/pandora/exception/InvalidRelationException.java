package com.pandora.exception;

public class InvalidRelationException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public InvalidRelationException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public InvalidRelationException() {
        super("Invalid entities relation.");
    }    

}
