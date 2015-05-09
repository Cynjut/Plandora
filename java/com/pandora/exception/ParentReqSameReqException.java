package com.pandora.exception;

public class ParentReqSameReqException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public ParentReqSameReqException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param msg
     */
    public ParentReqSameReqException(String msg) {
        super(msg);
    }
	
}
