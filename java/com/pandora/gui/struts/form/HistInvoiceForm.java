package com.pandora.gui.struts.form;

public class HistInvoiceForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String invId;

    private String historyContent;
    
    private String selectedIndex;
    
    
	public String getInvId() {
		return invId;
	}
	public void setInvId(String newValue) {
		this.invId = newValue;
	}
	
	
	public String getHistoryContent() {
		return historyContent;
	}
	public void setHistoryContent(String newValue) {
		this.historyContent = newValue;
	}
	
	
	public String getSelectedIndex() {
		return selectedIndex;
	}
	public void setSelectedIndex(String newValue) {
		selectedIndex = newValue;
	}

	
    
}
