package com.pandora.gui.struts.form;


/**
 */
public class ViewReportForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    /** Project related with report */
    private String projectId; 
    
    /** Project name related with report viewer*/
    private String projectName;

    /** ID of report that should be performed */
    private String reportId;
    
    /** Text data in html format */
    private String reportTable;
    
    /** category related */
    private String categoryId;
    
    /** Report export format */
    private String reportOutput;
    private String exportReportFormat;
    
    //////////////////////////////////////////////
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }    
    
    
    ///////////////////////////////////////
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String newValue) {
        this.projectName = newValue;
    }

    
    ///////////////////////////////////////    
    public String getReportTable() {
        return reportTable;
    }
    public void setReportTable(String newValue) {
        this.reportTable = newValue;
    }
    
    ///////////////////////////////////////       
    public String getReportId() {
        return reportId;
    }
    public void setReportId(String newValue) {
        this.reportId = newValue;
    }
    
    //////////////////////////////////////////////      
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }   
    
    ///////////////////////////////////////////////////	
	public String getReportOutput() {
		return reportOutput;
	}
	public void setReportOutput(String newValue) {
		this.reportOutput = newValue;
	}
	
    ///////////////////////////////////////////////////		
	public String getExportReportFormat() {
		return exportReportFormat;
	}
	public void setExportReportFormat(String newValue) {
		this.exportReportFormat = newValue;
	}      
    
	
}
