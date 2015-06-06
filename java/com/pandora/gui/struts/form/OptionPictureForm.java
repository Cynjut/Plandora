package com.pandora.gui.struts.form;

import org.apache.struts.upload.FormFile;

public class OptionPictureForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

    private FormFile theFile;

    private String confirmationMsg;
    
    
	/////////////////////////////////////////        
    public FormFile getTheFile() {
      return theFile;
    }
    public void setTheFile(FormFile newValue) {
      this.theFile = newValue;
    }

	
	////////////////////////////////////////
	public String getConfirmationMsg() {
		return confirmationMsg;
	}
	public void setConfirmationMsg(String newValue) {
		this.confirmationMsg = newValue;
	}
	
}
