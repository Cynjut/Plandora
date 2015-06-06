package com.pandora.gui.struts.form;

public class EdiForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String gotoAfterSave;
	
	private String htmlTypeList;
	
	private String htmlDefaultOpt;
	
	private String ediType;
	
	
	
	///////////////////////////////////////	
	public String getEdiType() {
		return ediType;
	}
	public void setEdiType(String newValue) {
		this.ediType = newValue;
	}
	
	
	///////////////////////////////////////	
	public String getGotoAfterSave() {
		return gotoAfterSave;
	}
	public void setGotoAfterSave(String newValue) {
		this.gotoAfterSave = newValue;
	}
	
	
	///////////////////////////////////////
	public String getHtmlTypeList() {
		return htmlTypeList;
	}
	public void setHtmlTypeList(String newValue) {
		this.htmlTypeList = newValue;
	}
	
	///////////////////////////////////////
	public String getHtmlDefaultOpt() {
		return htmlDefaultOpt;
	}
	public void setHtmlDefaultOpt(String newValue) {
		this.htmlDefaultOpt = newValue;
	}
	
	
}
