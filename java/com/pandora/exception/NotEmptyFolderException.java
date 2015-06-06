package com.pandora.exception;

public class NotEmptyFolderException extends BusinessException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param e
     */
    public NotEmptyFolderException(Exception e) {
        super(e);
    }

    /**
     * Constructor
     * @param e
     */
    public NotEmptyFolderException() {
        super("The folder is not empty and cannot be removed.");
    }    


}
