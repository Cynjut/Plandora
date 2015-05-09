package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 */
public class ProjectImportExportForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String projectId;
    
    private String projectName;
    
    private String importExportOption;
    
    private String fieldsHtml;
    
    private FormFile importExternalFile;
    
    
    /**
     */
    public void clear(){
        
    }
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		return errors;
	}
    
	////////////////////////////////////////////
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
    
	////////////////////////////////////////////    
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String newValue) {
        this.projectName = newValue;
    }
    
	////////////////////////////////////////////     
    public String getImportExportOption() {
        return importExportOption;
    }
    public void setImportExportOption(String newValue) {
        this.importExportOption = newValue;
    }
    
    ///////////////////////////////////////////      
    public String getFieldsHtml() {
        return fieldsHtml;
    }
    public void setFieldsHtml(String newValue) {
        this.fieldsHtml = newValue;
    }

    
	///////////////////////////////////////// 
    public FormFile getImportExternalFile() {
		return importExternalFile;
	}
	public void setImportExternalFile(FormFile newValue) {
		this.importExternalFile = newValue;
	}
    
       
    
    
    
}
