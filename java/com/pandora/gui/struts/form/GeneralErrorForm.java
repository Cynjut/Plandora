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
