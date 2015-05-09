package com.pandora.exception;

/**
 * This exception is thown when an attachment file contain a size 
 * greater then the max limit set by root user.
 */
public class MaxSizeAttachmentException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * @param e
     */
    public MaxSizeAttachmentException(Exception e) {
        super(e);
    }
    
    public MaxSizeAttachmentException(String msg) {
        super(msg);
    }        

    /**
     * 
     * @param e
     */
    public MaxSizeAttachmentException() {
        super("The size of attachment has exceeded the maximum limit.");
    }    
}
