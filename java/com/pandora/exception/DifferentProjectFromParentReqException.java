package com.pandora.exception;

public class DifferentProjectFromParentReqException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public DifferentProjectFromParentReqException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param msg
     */
    public DifferentProjectFromParentReqException(String msg) {
        super(msg);
    }

}
