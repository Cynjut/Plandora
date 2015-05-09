package com.pandora.exception;

public class ZeroCapacityException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public ZeroCapacityException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param msg
     */
    public ZeroCapacityException(String msg) {
        super(msg);
    }
}
