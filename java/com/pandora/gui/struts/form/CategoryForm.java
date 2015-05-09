package com.pandora.gui.struts.form;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.pandora.helper.FormValidationUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the data of Category Form
 */
public class CategoryForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** Name of Category */
    private String name;

    /** Description of Category */
    private String description;

    /** The type of Category, e.g., where the category is applicable (Task, Requirement, etc) */
    private String type;

    /** Project related with Category */
    private String projectId = "-1";
    
    private String positionOrder;
    
    private boolean isBillableTask;
    
    private boolean isDefectTask;
    
    private boolean isTestingTask;
    
    private boolean isDevelopingTask;
    
    private boolean isHidden;
    
    
    public void clear() {
        this.name = null;
        this.description = null;
        this.type = null;
        this.projectId = null;
        this.isBillableTask = false;
        this.isDefectTask = false;
        this.isTestingTask = false;
        this.isDevelopingTask = false;
        this.isHidden = false;
        this.positionOrder = "0";
    }

    
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	this.isBillableTask = false;
        this.isDefectTask = false;
        this.isTestingTask = false;
        this.isDevelopingTask = false;
    	this.isHidden = false;
	}

    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (this.operation.equals("saveCategory")) {
			MessageResources mr = this.getCurrentUser().getBundle();
			Locale loc = SessionUtil.getCurrentLocale(request);
			FormValidationUtil.checkInt(errors, mr.getMessage(loc, "label.category.order"), this.positionOrder);
		}
		
		return errors;
	}
	
	
	////////////////////////////////////////	
	public boolean getIsBillableTask() {
		return isBillableTask;
	}
	public void setIsBillableTask(boolean newValue) {
		this.isBillableTask = newValue;
	}

	
	////////////////////////////////////////	
	public boolean getIsDefectTask() {
		return isDefectTask;
	}
	public void setIsDefectTask(boolean newValue) {
		this.isDefectTask = newValue;
	}


	////////////////////////////////////////
	public boolean getIsTestingTask() {
		return isTestingTask;
	}
	public void setIsTestingTask(boolean newValue) {
		this.isTestingTask = newValue;
	}

	
	////////////////////////////////////////
	public boolean getIsDevelopingTask() {
		return isDevelopingTask;
	}
	public void setIsDevelopingTask(boolean newValue) {
		this.isDevelopingTask = newValue;
	}


	////////////////////////////////////////		
    public boolean getIsHidden() {
		return isHidden;
	}
	public void setIsHidden(boolean newValue) {
		this.isHidden = newValue;
	}


	//////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    //////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
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
	public String getPositionOrder() {
		return positionOrder;
	}
	public void setPositionOrder(String newValue) {
		this.positionOrder = newValue;
	}
    
    
}