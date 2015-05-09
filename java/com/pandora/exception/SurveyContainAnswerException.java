package com.pandora.exception;

public class SurveyContainAnswerException extends BusinessException {

	private static final long serialVersionUID = 1L;

	
    /**
     * Constructor
     */
    public SurveyContainAnswerException(Exception e) {
        super(e);
    }

    
    /**
     * Constructor
     */
    public SurveyContainAnswerException() {
        super("The survey questions cannot be removed because one or more questions contain anwers into it.");
    }    

}
