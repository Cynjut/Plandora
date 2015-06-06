package com.pandora.exception;

public class WaitPredecessorUpdateException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public WaitPredecessorUpdateException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public WaitPredecessorUpdateException() {
        super("The task cannot be updated because it is waiting for the conclusion of a predecessor task.");
    }    

}
