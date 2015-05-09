package com.pandora.exception;

/**
 * This class represents a generic exception occurred into system on Business layer
 */
public class BusinessException extends SystemException {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public BusinessException(String msg){
		super(msg);
	}
	
	/**
	 * Constructor
	 */
	public BusinessException(Exception e){
		super(e);
	}
	
}
