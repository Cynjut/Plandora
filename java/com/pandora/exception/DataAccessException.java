package com.pandora.exception;

/**
 * This class represents a generic exception occurred into system on Data Base layer 
 */
public class DataAccessException extends SystemException {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public DataAccessException(String msg){
		super(msg);
	}
    
	/**
	 * Constructor
	 */
	public DataAccessException(Exception e){
		super(e);
	}


}
