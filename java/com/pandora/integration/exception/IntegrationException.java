package com.pandora.integration.exception;

/**
 * This class represent the gerenal exception of integration process 
 */
public class IntegrationException extends Exception {
    
	private static final long serialVersionUID = 1L;
	
	/** Message from exception */
	private String errorMessage;
	
	
	/**
	 * Constructor
	 */
	public IntegrationException(Exception e){
		super(e.toString(), e.getCause());
		this.errorMessage = e.toString();
	}

	/**
	 * Constructor
	 */
	public IntegrationException(String msg){
		super(msg);
		this.errorMessage = msg;
	}
	
	
    //////////////////////////////////////////	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String newValue) {
		errorMessage = newValue;
	}

}