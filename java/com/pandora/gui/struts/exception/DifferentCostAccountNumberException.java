package com.pandora.gui.struts.exception;

import com.pandora.exception.SystemException;

public class DifferentCostAccountNumberException  extends SystemException {

	private static final long serialVersionUID = 1L;

    public DifferentCostAccountNumberException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     */
    public DifferentCostAccountNumberException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     */
    public DifferentCostAccountNumberException() {
        super("The cost category must be the same.");
    }        
	
}
