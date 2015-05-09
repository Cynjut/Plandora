package com.pandora.gui.struts.form;

/**
 */
public class HistAttachForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String attachmentId;
    
    private String SelectedIndex;
    
    private String historyContent;
    

    /////////////////////////////////////
    public String getAttachmentId() {
        return attachmentId;
    }
    public void setAttachmentId(String newValue) {
        this.attachmentId = newValue;
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
