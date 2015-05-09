package com.pandora.gui.struts.form;

public class MindMapForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String serverURI;
    
    private String formTitle;
    
    
    //////////////////////////////////////
    public String getServerURI() {
        return serverURI;
    }
    public void setServerURI(String newValue) {
        this.serverURI = newValue;
    }

    
    //////////////////////////////////////
    public String getFormTitle() {
		return formTitle;
	}
	public void setFormTitle(String newValue) {
		this.formTitle = newValue;
	}
    
}
