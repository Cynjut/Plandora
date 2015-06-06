package com.pandora.exception;

public class MetaFieldNumericTypeException extends BusinessException {
	private static final long serialVersionUID = 1L;
	
	private String metaFieldName;
	
	/**
	 * Constructor
	 */
	public MetaFieldNumericTypeException(String msg, String metaFieldName){
		super(msg);
		this.metaFieldName = metaFieldName;
	}
	
	/**
	 * Constructor
	 */
	public MetaFieldNumericTypeException(String metaFieldName){
		super("error.formMetaField.numericValue");
		this.metaFieldName = metaFieldName;
	}
	
	/**
	 * Constructor
	 */
	public MetaFieldNumericTypeException(Exception e, String metaFieldName){
		super(e);
		this.metaFieldName = metaFieldName;
	}

	public String getMetaFieldName() {
		return metaFieldName;
	}

	public void setMetaFieldName(String metaFieldName) {
		this.metaFieldName = metaFieldName;
	}
	
	
}
