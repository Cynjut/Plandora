package com.pandora.exception;

public class ZeroCapacityDBException extends DataAccessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public ZeroCapacityDBException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param msg
     */
    public ZeroCapacityDBException(String msg) {
        super(msg);
    }

}
