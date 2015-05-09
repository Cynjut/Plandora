package com.pandora.exception;

/**
 * This exception is thown when a new project (into the insertion process) contain
 * some customer user different of set of customers of related parent project. 
 */
public class IncompatibleUsrsBetweenPrjException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public IncompatibleUsrsBetweenPrjException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public IncompatibleUsrsBetweenPrjException() {
        super("The current project contain customer users that not exists into parent project.");
    }    

}
