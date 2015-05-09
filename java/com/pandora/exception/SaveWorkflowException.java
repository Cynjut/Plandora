package com.pandora.exception;

public class SaveWorkflowException extends DataAccessException {

	private static final long serialVersionUID = 1L;

	private static String msgInBundle;

    /**
     * Constructor
     */
    public SaveWorkflowException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     */
    public SaveWorkflowException(String msg, Exception e) {
    	super(e);
    	super.setErrorMessage(msg);
    	msgInBundle = msg;
    }


    public SaveWorkflowException(String msg) {
    	super(msg);
    	msgInBundle = msg;
    }
    
    //////////////////////////////////////
	public String getMsgInBundle() {
		return msgInBundle;
	}    

}
