package com.pandora.gui.struts.form;

public class SnipArtifactForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String snip;

	private String htmlFormBody;
	
	private String popupTitle;
	
	
	////////////////////////////////////////
	public String getSnip() {
		return snip;
	}
	public void setSnip(String newValue) {
		this.snip = newValue;
	}
	
	
	////////////////////////////////////////
	public String getHtmlFormBody() {
		return htmlFormBody;
	}
	public void setHtmlFormBody(String newValue) {
		this.htmlFormBody = newValue;
	}
	
	
	////////////////////////////////////////
	public String getPopupTitle() {
		return popupTitle;
	}
	public void setPopupTitle(String newValue) {
		this.popupTitle = newValue;
	}
	
	
	
}
