package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * This class handle the data of Area Form 
 */
public class AreaForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    /** Name of Area */
    private String name;
    
    /** Description of Area */
    private String description;
        
    
    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    /**
     * Clear values of UserTO
     */
    public void clear(){
        name = null;        
        description= null;
        id=null;
        this.setSaveMethod(null, null);
    }
    
    /**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		ActionErrors errors = new ActionErrors();
		
		if (operation.equals("saveArea")){
		    if (this.name==null || this.name.trim().equals("")){
		    	errors.add("Name", new ActionError("errors.required", "Area") );
		    }			
		}
		
		return errors;
	}
   
}
