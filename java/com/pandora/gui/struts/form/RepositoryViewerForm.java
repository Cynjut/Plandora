package com.pandora.gui.struts.form;




public class RepositoryViewerForm extends HosterRepositoryForm {

	private static final long serialVersionUID = 1L;

    private String projectId;

    private String rev = "";
    
    private String logrev = "";
    
    private String path;
    
    private String logFileListHtml = "";
    
    private String emulateCustomerViewer = "off";
    
    private String showUploadButton = "off";
    
    private String uriPath;
    
    
    /////////////////////////////////////        
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }

    
    ///////////////////////////////////// 
    public String getRev() {
		return rev;
	}
	public void setRev(String newValue) {
		this.rev = newValue;
	}
	
	
    ///////////////////////////////////// 	
    public String getLogrev() {
		return logrev;
	}
	public void setLogrev(String newValue) {
		this.logrev = newValue;
	}
	
	
	///////////////////////////////////// 
	public String getPath() {
		return path;
	}
	public void setPath(String newValue) {
		this.path = newValue;
	}
	
	
    ///////////////////////////////////// 
	public String getLogFileListHtml() {
		return logFileListHtml;
	}
	public void setLogFileListHtml(String newValue) {
		this.logFileListHtml = newValue;
	}

	
    ///////////////////////////////////// 
	public String getEmulateCustomerViewer() {
		return emulateCustomerViewer;
	}
	public void setEmulateCustomerViewer(String newValue) {
		this.emulateCustomerViewer = newValue;
	}
	
	
    ///////////////////////////////////// 	
	public String getShowUploadButton() {
		return showUploadButton;
	}
	public void setShowUploadButton(String newValue) {
		this.showUploadButton = newValue;
	}

	

	public void setUriPath(String newValue) {
		this.uriPath = newValue;
	}
	public String getAnonymousURI(){
		return this.uriPath + "/artifact?id=" + this.getId() + "&prj=" + this.getProjectId();
	}
	
}
