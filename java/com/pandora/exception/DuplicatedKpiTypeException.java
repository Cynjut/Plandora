package com.pandora.exception;

public class DuplicatedKpiTypeException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public DuplicatedKpiTypeException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public DuplicatedKpiTypeException() {
        super("This Kpi type already exists for the related project.");
    }    

}
