package com.pandora.exception;

public class ProjectCannotBeClosedException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public ProjectCannotBeClosedException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param msg
     */
    public ProjectCannotBeClosedException(String msg) {
        super(msg);
    }
    
    /**
     * Constructor
     */
    public ProjectCannotBeClosedException() {
        super("The project cannot be closed because it contain some pending entities (task, requirement, invoice, etc).");
    }    

}
