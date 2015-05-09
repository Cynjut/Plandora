package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

public class ArtifactForm extends HosterRepositoryForm {

	private static final long serialVersionUID = 1L;
	
    private String name;

    private String description;
        
    private String projectId;
    
    private String planningId;
    
    private String templateId;
    
    private String backToCaller;
    
    private String lang = "en";
    
    private String body;
    
    private String showSaveAsPopup = "off";
        
    private String editPath;
    
    private String editPathRev;
    
    private String snipList = "include_img=include_img;include_sql=include_sql;include_table=include_table";
    
    private String snipHtmlDimension;
    
    
    //////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    
    //////////////////////////////////////////    
    public void setName(String newValue) {
        this.name = newValue;
    }
    public String getName() {
        return name;
    }

    
    //////////////////////////////////////////     
    public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	
    ////////////////////////////////////////// 
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}
	
	
    //////////////////////////////////////////	
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String newValue) {
		this.templateId = newValue;
	}
	
	
    //////////////////////////////////////////
	public void setBackToCaller(String newValue) {
		this.backToCaller = newValue;
		
	}
	public String getBackToCaller() {
		return backToCaller;
	}
	
	
    //////////////////////////////////////////
	public String getBody() {
		return body;
	}
	public void setBody(String newValue) {
		this.body = newValue;
	}
	
	
    //////////////////////////////////////////	
    public String getLang() {
		return lang;
	}
	public void setLang(String newValue) {
		if (newValue!=null && (newValue.equalsIgnoreCase("pt") || newValue.equalsIgnoreCase("en") || 
				newValue.equalsIgnoreCase("es") || newValue.equalsIgnoreCase("ru"))) {
			this.lang = "\"" + newValue + "\"";	
		} else {
			this.lang = "\"en\"";
		}
	}
	
	
    //////////////////////////////////////////	
	public String getSnipList() {
		return snipList;
	}
	public void setSnipList(String newValue) {
		this.snipList = newValue;
	}
	
	
    //////////////////////////////////////////	
	public String getSnipHtmlDimension() {
		return snipHtmlDimension;
	}
	public void setSnipHtmlDimension(String newValue) {
		this.snipHtmlDimension = newValue;
	}
	
	
	//////////////////////////////////////////
	public String getShowSaveAsPopup() {
		return showSaveAsPopup;
	}
	public void setShowSaveAsPopup(String newValue) {
		this.showSaveAsPopup = newValue;
	}
	
	
	//////////////////////////////////////////	
    public String getEditPath() {
		return editPath;
	}
	public void setEditPath(String newValue) {
		this.editPath = newValue;
	}
	
	
	//////////////////////////////////////////	
	public String getEditPathRev() {
		return editPathRev;
	}
	public void setEditPathRev(String newValue) {
		this.editPathRev = newValue;
	}
	
	
	public void clear(){
        name = null;        
        description= null;
        projectId=null;
        planningId=null;
        templateId=null;
        backToCaller = "";
        body = "";
        showSaveAsPopup = "off";  
        lang = "en";
        editPath = "";
        editPathRev = "";
        this.setSaveMethod(null, null);
    }
    
    
    /**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		ActionErrors errors = new ActionErrors();
		
		//if (operation.equals("saveArtifact")){
		//}
		
		return errors;
	}
    
    
}
