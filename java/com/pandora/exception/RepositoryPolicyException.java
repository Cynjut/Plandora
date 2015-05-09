package com.pandora.exception;

public class RepositoryPolicyException extends BusinessException {

	private static final long serialVersionUID = 1L;

    public RepositoryPolicyException(Exception e) {
        super(e);
    }
    
    public RepositoryPolicyException(String msg) {
        super(msg);
    }        

}
