package com.pandora.gui.struts.form;

/**
 * This class contain a data of error occurrence into UI layer.
 */
public class GeneralErrorForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    /** The error message description */
    private String errorMessage;
    
    /** The exception message from system */
    private String exceptionMessage;
    
    /** The exception stackTrace */
    private String stackTrace;

    /** optional arguments for error message */
    private String arg0;
    private String arg1;
    private String arg2;
    private String arg3;
    private String arg4;
    

    ///////////////////////////////////////////////    
    public String getStackTrace() {
        return stackTrace;
    }
    public void setStackTrace(String newValue) {
        this.stackTrace = newValue;
    }
    
    ///////////////////////////////////////////////
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String newValue) {
        this.errorMessage = newValue;
    }
    public void setErrorMessage(String newValue, String newArg0, String newArg1, String newArg2, String newArg3, String newArg4) {
        this.errorMessage = newValue;
        this.arg0 = newArg0;
        this.arg1 = newArg1;
        this.arg2 = newArg2;
        this.arg3 = newArg3;
        this.arg4 = newArg4;
    }
    
    ///////////////////////////////////////////////    
    public String getArg0() {
    	return this.arg0;
    }
    public String getArg1() {
    	return this.arg1;
    }
    public String getArg2() {
    	return this.arg2;
    }
    public String getArg3() {
    	return this.arg3;
    }
    public String getArg4() {
    	return this.arg4;
    }
    
    ///////////////////////////////////////////////        
    public void setExceptionMessage(String newValue) {
        
        //remove presentation noisy...
        if (newValue!=null) {
            newValue = newValue.replaceAll("com.pandora.exception.DataAccessException:", "");    
        }
      
        this.exceptionMessage = newValue;
    }
    public String getExceptionMessage() {
        return exceptionMessage;
    }
    
}
