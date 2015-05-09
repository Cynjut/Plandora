package com.pandora.gui.struts.form;

public class HistRiskForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String riskId;
    
    private String SelectedIndex;
    
    private String historyContent;
    

    /////////////////////////////////////
    public String getRiskId() {
        return riskId;
    }
    public void setRiskId(String newValue) {
        this.riskId = newValue;
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
