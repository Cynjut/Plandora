package com.pandora.gui.struts.form;

public class HosterRepositoryForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String onlyFolders = "off";

	private String multiple = "on";
	
    /////////////////////////////////////////////// 
	public String getOnlyFolders() {
		return onlyFolders;
	}
	public void setOnlyFolders(String newValue) {
		this.onlyFolders = newValue;
	}

	
    /////////////////////////////////////////////// 
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String newValue) {
		this.multiple = newValue;
	}

}
