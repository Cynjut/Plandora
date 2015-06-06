package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 */
public class MetaFormForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private String viewableCols;
    
    private String filterColId;
    
    private Integer gridNumber;
    
    private String jsBeforeSave;
    private String jsAfterSave;
    private String jsAfterLoad;
    
    
    /**
     * Clear values of Form
     */
    public void clear(){      
        this.name = null;
        this.viewableCols = null;
        this.filterColId = null;
        this.gridNumber = new Integer("6");
        this.jsBeforeSave = null;
        this.jsAfterSave = null;
        this.jsAfterLoad = null;
        this.setAdditionalFields(null);
    }      
    
    
    /////////////////////////////////////
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    

    /////////////////////////////////////    
    public String getViewableCols() {
        return viewableCols;
    }
    public void setViewableCols(String newValue) {
        this.viewableCols = newValue;
    }
    
    
    /////////////////////////////////////
	public String getFilterColId() {
		return filterColId;
	}
	public void setFilterColId(String newValue) {
		this.filterColId = newValue;
	}


    /////////////////////////////////////
	public Integer getGridNumber() {
		return gridNumber;
	}
	public void setGridNumber(Integer newValue) {
		this.gridNumber = newValue;
	}
    

	/////////////////////////////////////        
    public String getJsAfterSave() {
        return jsAfterSave;
    }
    public void setJsAfterSave(String newValue) {
        this.jsAfterSave = newValue;
    }
    
    
    /////////////////////////////////////        
    public String getJsAfterLoad() {
        return jsAfterLoad;
    }
    public void setJsAfterLoad(String newValue) {
        this.jsAfterLoad = newValue;
    }
    
    
    /////////////////////////////////////        
    public String getJsBeforeSave() {
        return jsBeforeSave;
    }
    public void setJsBeforeSave(String newValue) {
        this.jsBeforeSave = newValue;
    }
    
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
	
		if (this.operation.equals("saveMetaForm")) {
		    
		    if (this.name==null || this.name.trim().equals("")){
		        errors.add("Name", new ActionError("validate.metaform.blankName") );
		    }
		}
		
		return errors;
	}
    
}
