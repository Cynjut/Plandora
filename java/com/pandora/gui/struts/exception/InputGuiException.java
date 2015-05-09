package com.pandora.gui.struts.exception;

import com.pandora.exception.SystemException;

/**
 * This exception is thrown when an error occurs due to an input into graphic user interface layer.
 */
public class InputGuiException extends SystemException{

	private static final long serialVersionUID = 1L;
	
    /**
     * Constructor
     */
    public InputGuiException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     */
    public InputGuiException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     */
    public InputGuiException() {
        super("The process failed because occurs an error of data input.");
    }        
}
