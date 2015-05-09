package com.pandora.exception;

public class PaidExpenseCannotBeExcludedException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public PaidExpenseCannotBeExcludedException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     */
    public PaidExpenseCannotBeExcludedException() {
        super("An expense that contains paid installments cannot be excluded.");
    }    

}
