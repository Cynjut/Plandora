package com.pandora.gui.struts.exception;

import com.pandora.exception.SystemException;

public class DifferentCostCategoryException extends SystemException {

	private static final long serialVersionUID = 1L;

    public DifferentCostCategoryException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     */
    public DifferentCostCategoryException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     */
    public DifferentCostCategoryException() {
        super("The cost category must be the same.");
    }        
	
}
