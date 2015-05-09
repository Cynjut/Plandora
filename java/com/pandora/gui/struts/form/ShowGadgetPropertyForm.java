package com.pandora.gui.struts.form;

public class ShowGadgetPropertyForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String gagid;
	
	private String propertyTitle;
	
	private String forwardAfterSave;
	
	private String fieldsHtml;

	private String showGadgetCall;
	
	///////////////////////////////////////
	public String getGagid() {
		return gagid;
	}
	public void setGagid(String newValue) {
		this.gagid = newValue;
	}
	
	
	///////////////////////////////////////
	public String getPropertyTitle() {
		return propertyTitle;
	}
	public void setPropertyTitle(String newValue) {
		this.propertyTitle = newValue;
	}

	
	///////////////////////////////////////
	public String getForwardAfterSave() {
		return forwardAfterSave;
	}
	public void setForwardAfterSave(String newValue) {
		this.forwardAfterSave = newValue;
	}

	
	///////////////////////////////////////
	public String getFieldsHtml() {
		return fieldsHtml;
	}
	public void setFieldsHtml(String newValue) {
		this.fieldsHtml = newValue;
	}
	
	///////////////////////////////////////	
	public String getShowGadgetCall() {
		return showGadgetCall;
	}
	public void setShowGadgetCall(String newValue) {
		this.showGadgetCall = newValue;
	}
	
}
