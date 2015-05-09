package com.pandora.exception;

/**
 * This exception is thown when an report contain no data to be displayed 
 */
public class EmptyReportException extends DataAccessException {

	private static final long serialVersionUID = 1L;

    /**
     * @param e
     */
    public EmptyReportException(Exception e) {
        super(e);
    }

    /**
     * 
     * @param e
     */
    public EmptyReportException() {
        super("There is no data to be displayed.");
    }    
    
}
