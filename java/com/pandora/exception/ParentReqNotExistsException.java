package com.pandora.exception;

public class ParentReqNotExistsException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public ParentReqNotExistsException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param msg
     */
    public ParentReqNotExistsException(String msg) {
        super(msg);
    }

}
