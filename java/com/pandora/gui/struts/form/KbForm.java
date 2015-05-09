package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * 
 */
public class KbForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String projectName;
    
    private String projectId;
    
    private String subject;
    
    private String type;
    
    private int currentPage;
    
    private String htmlKbGrid;
    
    private int querySize;
    
    
    public void clear(){
        this.projectName = null;
        this.subject = null;
        this.type = null;
        this.currentPage = 0;
        this.htmlKbGrid = "";
        this.querySize = 0;
    }
    
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		//if (this.operation.equals("")) {
		    
		//}
			
		return errors;
	}
	
	
	////////////////////////////////////////
	public String getHtmlKbGrid() {
		return htmlKbGrid;
	}
	public void setHtmlKbGrid(String newValue) {
		this.htmlKbGrid = newValue;
	}


	//////////////////////////////////////////	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int newValue) {
		this.currentPage = newValue;
	}


	//////////////////////////////////////////
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String newValue) {
        this.projectName = newValue;
    }
    
    
	//////////////////////////////////////////    
    public String getSubject() {
        return subject;
    }
    public void setSubject(String newValue) {
        this.subject = newValue;
    }
    
    
	//////////////////////////////////////////    
    public String getType() {
        return type;
    }
    public void setType(String newValue) {
        this.type = newValue;
    }
    
    
	//////////////////////////////////////////      
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }

    
	//////////////////////////////////////////   
	public int getQuerySize() {
		return querySize;
	}
	public void setQuerySize(int newValue) {
		this.querySize = newValue;
	}
    
    
}
