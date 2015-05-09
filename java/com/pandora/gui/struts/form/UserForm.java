package com.pandora.gui.struts.form;

import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.pandora.RootTO;

/**
 * This class handle the data of Manage User Form 
 */
public class UserForm extends GeneralStrutsForm{
     
	private static final long serialVersionUID = 1L;
	
    /** The username (unique) related with user */ 
    private String username;
    
    /** The full name of User */
    private String name;
    
    /** The phone number of User*/
    private String phone;
    
    /** The email address of User*/
    private String email;
    
    /** The color that represents the User on GUI*/
    private String color;
    
    /** The password of User*/
    private String password;
    
    /** The confirmation of password */
    private String confirmation;
    
    /** Department selected */
    private String userDepartment;
    
    /** Area selected */
    private String userArea;
    
    /** Function selected */
    private String userFunction;
    
    /** The birth date of current user */
    private String birth;
    
    private String authenticationMode;
    
    private FormFile theFile;
    
    private boolean isUpload;
    
    private String enableStatus;
    
    private boolean hideDisableUsers;
    
    private String permissionListHtmlBody;
    
    
    /**
     * Clear values of UserTO
     */
    public void clear(){
        id = null;
        username = null;        
        name= null;
        phone= null;
        email= null;
        color= null;
        password= null;
        confirmation= null;
        userDepartment="1";
        userArea="1";
        userFunction="1";
        birth = null;
        theFile = null;
        enableStatus = "1";
        this.isUpload = true;
        permissionListHtmlBody=null;
        this.setSaveMethod(null, null);
    }
    
    
	public void reset(ActionMapping mapping, ServletRequest request) {
		hideDisableUsers=false;
	}


	/////////////////////////////////////////      
    public boolean isUpload() {
        return isUpload;
    }
    public void setUpload(boolean newValue) {
        this.isUpload = newValue;
    }
       
	/////////////////////////////////////////        
    public FormFile getTheFile() {
      return theFile;
    }
    public void setTheFile(FormFile newValue) {
      this.theFile = newValue;
    }
    
    ///////////////////////////////////////
    public String getUsername() {
        return username;
    }
    public void setUsername(String newValue) {
        this.username = newValue;
    }

    ///////////////////////////////////////    
    public String getColor() {
        return color;
    }
    public void setColor(String newValue) {
        this.color = newValue;
    }
    
    ///////////////////////////////////////    
    public String getEmail() {
        return email;
    }
    public void setEmail(String newValue) {
        this.email = newValue;
    }
    
    ///////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    ///////////////////////////////////////    
    public String getPassword() {
        return password;
    }
    public void setPassword(String newValue) {
        this.password = newValue;
    }
    
    ///////////////////////////////////////
    public String getPhone() {
        return phone;
    }       
    public void setPhone(String newValue) {
        this.phone = newValue;
    }

    ///////////////////////////////////////
    public String getConfirmation() {
        return confirmation;
    }
    public void setConfirmation(String newValue) {
        this.confirmation = newValue;
    }

    ///////////////////////////////////////    
    public void setUserArea(String newValue) {
        this.userArea = newValue;
    }
    public String getUserArea() {
        return userArea;
    }

    ///////////////////////////////////////    
    public void setUserDepartment(String newValue) {
        this.userDepartment = newValue;
    }
    public String getUserDepartment() {
        return userDepartment;
    }
    
    ///////////////////////////////////////    
    public void setUserFunction(String newValue) {
        this.userFunction = newValue;
    }
    public String getUserFunction() {
        return userFunction;
    }
    
    ///////////////////////////////////////        
    public String getBirth() {
        return birth;
    }
    public void setBirth(String newValue) {
        this.birth = newValue;
    }    
    
    ///////////////////////////////////////     
	public String getAuthenticationMode() {
		return authenticationMode;
	}
	public void setAuthenticationMode(String newValue) {
		this.authenticationMode = newValue;
	}
	
    ///////////////////////////////////////     
	public String getEnableStatus() {
		return enableStatus;
	}
	public void setEnableStatus(String newValue) {
		this.enableStatus = newValue;
	}
	
	
    /////////////////////////////////////// 	
	public boolean getHideDisableUsers() {
		return hideDisableUsers;
	}
	public void setHideDisableUsers(boolean newValue) {
		this.hideDisableUsers = newValue;
	}

	
    /////////////////////////////////////// 	
	public String getPermissionListHtmlBody() {
		return permissionListHtmlBody;
	}
	public void setPermissionListHtmlBody(String newValue) {
		this.permissionListHtmlBody = newValue;
	}


	///////////////////////////////////////	
	public String getUserPictureHtml(){
	  	return "<img width=\"50\" height=\"60\" border=\"0\" src=\"../do/login?operation=getUserPic&id=" +  this.id + "\" />";    
	}
	
	
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		ActionErrors errors = new ActionErrors();
		
		if (this.operation.equals("saveUser")){
		    
			if (this.name==null || this.name.trim().equals("")){             	
			    errors.add("Nome", new ActionError("validate.blankName") );
			}
			if (this.username==null || this.username.trim().equals("")){             	
			    errors.add("Username", new ActionError("validate.blankUsername") );
			}			
			if (this.password==null || this.password.trim().equals("")){             	
			    errors.add("Senha", new ActionError("validate.blankPass") );
			}
			if (this.color==null || this.color.trim().equals("")){             	
			    errors.add("Cor", new ActionError("validate.blankColor") );
			} else {
		        if (!Pattern.matches("[0-9abcdefABCDEF]{6}+", this.color)) {
		            errors.add("Cor", new ActionError("validate.invalidColor") );
		        }			    
			}	
		}
		
		if (this.operation.equals("changePassword")){
			if (this.confirmation==null || this.confirmation.trim().equals("")){             	
			    errors.add("Confirma&ccedil;&atilde;o", new ActionError("validate.blankPass") );
			}			    
			if (this.confirmation!=null && this.password!=null && (!this.password.equals(this.confirmation))){             	
			    errors.add("Senha", new ActionError("validate.PassConfirm") );
			}			    
		}
		
		if (this.operation.equals("saveUser") || this.operation.equals("changePassword")){
			if (this.getEnableStatus()!=null && this.getEnableStatus().equals("0")) {
				if (this.getUsername().equals(RootTO.ROOT_USER)) {
					errors.add("Disable User", new ActionError("validate.rootDisabled") );
				}
			}
		}
		
		return errors;
	}
	    
}
