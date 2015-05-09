package com.pandora.gui.struts.form;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 */
public class AttachmentForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private String status;
    
    private String planningId;
    
    private String user;
    
    private String visibility;
    
    private Timestamp creationDate;
    
    private String type;
    
    private String comment;
    
    private boolean isUpload;
    
    private FormFile theFile;
    
    private String source;
    
    private String maxSizeFile;
    
    private String fwd;
    
    
    
    /**
     * Clear values of Form
     */
    public void clear(){
    	this.id = null;
        this.name = null;
        this.status = null;
        this.user = null;
        this.visibility = null;
        this.creationDate = null;
        this.type = null;
        this.comment = null;
        this.isUpload = true;
    }
    
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (this.operation.equals("saveRisk")) {

		    if (this.name==null || this.name.trim().equals("")){
		        errors.add("Name", new ActionError("validate.formAttachment.blankName") );
		    }
		    
		}	
		return errors;		
	}
    
	/////////////////////////////////////////
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }
    
	/////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
	/////////////////////////////////////////    
    public String getPlanningId() {
        return planningId;
    }
    public void setPlanningId(String newValue) {
        this.planningId = newValue;
    }
    
	/////////////////////////////////////////    
    public String getStatus() {
        return status;
    }
    public void setStatus(String newValue) {
        this.status = newValue;
    }
    
	/////////////////////////////////////////    
    public String getType() {
        return type;
    }
    public void setType(String newValue) {
        this.type = newValue;
    }
    
	/////////////////////////////////////////    
    public String getUser() {
        return user;
    }
    public void setUser(String newValue) {
        this.user = newValue;
    }
    
	/////////////////////////////////////////    
    public String getVisibility() {
        return visibility;
    }
    public void setVisibility(String newValue) {
        this.visibility = newValue;
    }

	/////////////////////////////////////////  
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
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
    

	/////////////////////////////////////////      
    public String getSource() {
        return source;
    }
    public void setSource(String newValue) {
        this.source = newValue;
    }

    
	/////////////////////////////////////////      
    public String getMaxSizeFile() {
        return maxSizeFile;
    }
    public void setMaxSizeFile(String newValue) {
        this.maxSizeFile = newValue;
    }

    
	/////////////////////////////////////////
	public String getFwd() {
		return fwd;
	}
	public void setFwd(String newValue) {
		this.fwd = newValue;
	}
    
    
}
