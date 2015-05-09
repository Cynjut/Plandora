package com.pandora.gui.struts.form;

public class GadgetForm extends GeneralStrutsForm {
	
	private static final long serialVersionUID = 1L;
	
	private String htmlGadgetList;
	
	private String category;
	
	
	///////////////////////////////////////////
	public String getHtmlGadgetList() {
		return htmlGadgetList;
	}
	public void setHtmlGadgetList(String newValue) {
		this.htmlGadgetList = newValue;
	}

	
	///////////////////////////////////////////
	public String getCategory() {
		return category;
	}
	public void setCategory(String newValue) {
		this.category = newValue;
	}
	
	
	
}
