package com.pandora.gui.struts.form;

/**
 */
public class HistOccForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    private String occId;
    
    private String SelectedIndex;
    
    private String historyContent;
    

    /////////////////////////////////////
    public String getOccId() {
        return occId;
    }
    public void setOccId(String newValue) {
        this.occId = newValue;
    }
    
    
    /////////////////////////////////////
    public String getSelectedIndex() {
        return SelectedIndex;
    }
    public void setSelectedIndex(String newValue) {
        SelectedIndex = newValue;
    }
    
    
    /////////////////////////////////////
    public String getHistoryContent() {
        return historyContent;
    }
    public void setHistoryContent(String newValue) {
        this.historyContent = newValue;
    }
}
