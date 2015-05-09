package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * This class handle the data of Logon Form 
 */
public class LoginForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    /** Current username */
    private String username;
    
    /** Current passoword of user */
    private String password;
    
    
    ///////////////////////////////////////////////
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    
    
    ///////////////////////////////////////////////
    public void setPassword(String newValue) {
        this.password = newValue;
    }
    public void setUsername(String newValue) {
        this.username = newValue;
    }
    
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		ActionErrors errors = new ActionErrors();
		
		if (operation!=null && operation.equals("doLogin")){
		    
			if (this.username==null || this.username.trim().equals("")){
				errors.add("username", new ActionError("errors.required", "Username") );
			}
			
		}
		
		return errors;
	}
    
}
