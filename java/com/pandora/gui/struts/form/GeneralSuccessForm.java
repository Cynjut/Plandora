package com.pandora.gui.struts.form;

/**
 * This class contain a data of success occurrence into UI layer.
 */
public class GeneralSuccessForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    /** The successfully message description */
    private String successMessage;

    /////////////////////////////////////////////
    public String getSuccessMessage() {
        return successMessage;
    }
    public void setSuccessMessage(String newValue) {
        this.successMessage = newValue;
    }
}
