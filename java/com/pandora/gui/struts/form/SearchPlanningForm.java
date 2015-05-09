package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;


/**
 * This class handle the data of Search Requirement Form
 */
public class SearchPlanningForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** Project Id related with current search form. 
     * Used by form to get the list of Customers of project of combo filter. */
    private String projectId;
    
    /** The customer id used to filter the requirement searching */
    private String customerId;
    
    /** The content to be used such as filter into requirement searching */
    private String keyword;
    
    /** This flag could be filled/empty value depending on if exists elements into searching grid */
    private String hasOptionsList;

    private String type;
    
    private String reqStatus;
    
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		if (this.operation.equals("search")) {
			
			if (keyword==null || keyword.trim().equals("")) {
	            errors.add("keyword", new ActionError("validate.showSearchForm.blankKeyword") );			
			}

		}
		
		return errors;
	}
    
    
    ////////////////////////////////////////
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String newValue) {
        this.customerId = newValue;
    }
    
    ////////////////////////////////////////    
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String newValue) {
        this.keyword = newValue;
    }
    
    ////////////////////////////////////////
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
    
    ////////////////////////////////////////    
    public String getHasOptionsList() {
        return hasOptionsList;
    }
    public void setHasOptionsList(String newValue) {
        this.hasOptionsList = newValue;
    }

    
    ////////////////////////////////////////    
    public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}

	
    ////////////////////////////////////////   
	public String getReqStatus() {
		return reqStatus;
	}
	public void setReqStatus(String newValue) {
		this.reqStatus = newValue;
	}
    
    
}
