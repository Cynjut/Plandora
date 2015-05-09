package com.pandora.exception;

public class InvalidTaskTimeException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public InvalidTaskTimeException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public InvalidTaskTimeException() {
        super("invalid task time.");
    }    

    /**
     * Constructor
     * @param msg
     */
    public InvalidTaskTimeException(String msg) {
        super(msg);
    }

}
