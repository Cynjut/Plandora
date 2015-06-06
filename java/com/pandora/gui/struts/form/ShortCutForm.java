package com.pandora.gui.struts.form;

public class ShortCutForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String shorcutTitle;
	
	private String shortcutURI;
	
	private String iconType;
	
	private String opening;
	
	private String htmlOpenList;
	
	private String htmlTypeList;
	
	private String gotoAfterSave;
	
	
	
	///////////////////////////////////////
	public String getShorcutTitle() {
		return shorcutTitle;
	}
	public void setShorcutTitle(String newValue) {
		this.shorcutTitle = newValue;
	}

	
	///////////////////////////////////////
	public String getIconType() {
		return iconType;
	}
	public void setIconType(String newValue) {
		this.iconType = newValue;
	}

	
	///////////////////////////////////////
	public String getOpening() {
		return opening;
	}
	public void setOpening(String newValue) {
		this.opening = newValue;
	}
	
	
	///////////////////////////////////////
	public String getHtmlOpenList() {
		return htmlOpenList;
	}
	public void setHtmlOpenList(String newValue) {
		this.htmlOpenList = newValue;
	}
	
	
	///////////////////////////////////////
	public String getHtmlTypeList() {
		return htmlTypeList;
	}
	public void setHtmlTypeList(String newValue) {
		this.htmlTypeList = newValue;
	}
	
	
	///////////////////////////////////////	
	public String getGotoAfterSave() {
		return gotoAfterSave;
	}
	public void setGotoAfterSave(String newValue) {
		this.gotoAfterSave = newValue;
	}
	
	
	///////////////////////////////////////		
	public String getShortcutURI() {
		return shortcutURI;
	}
	public void setShortcutURI(String newValue) {
		this.shortcutURI = newValue;
	}
	
	
}
