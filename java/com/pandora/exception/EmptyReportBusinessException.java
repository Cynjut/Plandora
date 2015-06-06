package com.pandora.exception;

/**
 * This exception is thrown when an report contain no data to be displayed 
 */
public class EmptyReportBusinessException extends BusinessException {

	private static final long serialVersionUID = 1L;

	/**
     * @param e
     */
    public EmptyReportBusinessException(Exception e) {
        super(e);
    }

    /**
     * 
     * @param e
     */
    public EmptyReportBusinessException() {
        super("There is no data to be displayed.");
    }    

}
