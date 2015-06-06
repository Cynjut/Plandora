package com.pandora.gui.struts.form;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.UserTO;
import com.pandora.helper.DateUtil;
import com.pandora.helper.FormValidationUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * This class handle the data of Manage Task Form 
 */
public class TaskForm extends GeneralStrutsForm {
   
	private static final long serialVersionUID = 1L;
	
    /** Name of Task */
    private String name;
    
    /** Description of Task */
    private String description;

    /** Time (duration) planned for task */
    private String estimatedTime;
    
    /** Start Date of task */
    private String initDate;
    
    /** Project Id related with current task. Used by form to get the list of Resources. */
    private String projectId;
    
    /** Category Id related with task*/
    private String categoryId;
    
    /** Resource Id selected into available resources combo */
    private String resourceId;
    
    /** The parent task related */
    private String parentTaskId;

    /** if current task is a parent task... */
    private String isParentTask;
    
    /** if current task is a parent task... */
    private boolean isParentTaskCheckbox;
    
    /** Requirement Id related with task. Actually, the task is attending the requirement related. */
    private String requirementId;
    
    /** Requirement Number related with task.*/
    private String requestNum;
    
    /** Requirement Description related with task.*/
    private String requestDesc;
    
    /** Requirement Suggested Date related with task.*/
    private String requestSuggDate;
    
    /** Requirement Customer requester related with task.*/
    private String requestCustomer;
    
    /** current locale of user connected */
    private Locale userLocale;
    
    /** current status of task */
    private String taskStatus;

    /** show or hide the closed tasks into grid.*/
    private String hideClosedTasks = "on";
    
    /** Show (must be value="on") or hide the message box warning about the edit constraints. */
    private String showWarnPopup = "";

    /** Id of task that must be edited and was sent by external form. */
    private String taskIdFromExternalForm = "";
    
    /** CreationDate of current task */
    private Timestamp creationDate;
    
    private Timestamp finalDate;
    
    
    private UserTO createdBy;
    
    private String showAllocField = "off";
    
    private HttpServletRequest localRequestRef = null;
    
    private boolean isDecimalInput;
    
    private String showCloseReqConfirmation = "off";

    private String closeRequirement = "off";

    
    /**
     * Clear values of Task form
     */
    public void clear() {
        this.id="";
        this.estimatedTime = null;
        this.name = "";
        this.description = "";
        this.categoryId = null;
        this.isParentTask = "";
        this.isParentTaskCheckbox = false;
        this.taskStatus = "0";
        this.hideClosedTasks = "on";
        this.showWarnPopup = "";
        this.createdBy = null;
    }
    
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        localRequestRef = request;
        isParentTaskCheckbox = false;
    }
    
    /** 
     * Return if the list of tasks related with requirement
     * must be shown
     *  */
    public String getRequirementRelated(){
        String response = "on";
        if (this.requirementId==null || this.requirementId.trim().equals("")){
            response = "off";
        }
        return response;
    }

    
	////////////////////////////////////////////////
    public String getEstimatedTime() {
        return estimatedTime;
    }
    public void setEstimatedTime(String newValue) {
        this.estimatedTime = newValue;
    }
    
	////////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

	////////////////////////////////////////////////    
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
	////////////////////////////////////////////////    
    public String getInitDate() {
        return initDate;
    }
    public void setInitDate(String newValue) {
        this.initDate = newValue;
    }
    
	////////////////////////////////////////////////    
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }

	////////////////////////////////////////////////    
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }

	////////////////////////////////////////////////    
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String newValue) {
        this.resourceId = newValue;
    }
    

	////////////////////////////////////////////////    
    public String getRequirementId() {
        return requirementId;
    }
    public void setRequirementId(String newValue) {
        this.requirementId = newValue;
        
        if (newValue==null || newValue.equals("") || newValue.equals("-1")){
            requestNum = "";
            requestDesc = "";
            requestSuggDate = "";
            requestCustomer = "";
            requirementId = "";
        }
    }

	////////////////////////////////////////////////    
    public String getRequestDesc() {
        return requestDesc;
    }
    public void setRequestDesc(String newValue) {
        this.requestDesc = newValue;
    }
    
	////////////////////////////////////////////////    
    public String getRequestNum() {
        return requestNum;
    }
    public void setRequestNum(String newValue) {
        this.requestNum = newValue;
    }

	////////////////////////////////////////////////    
    public String getRequestCustomer() {
        return requestCustomer;
    }
    public void setRequestCustomer(String newValue) {
        this.requestCustomer = newValue;
    }
    
	////////////////////////////////////////////////    
    public String getRequestSuggDate() {
        return requestSuggDate;
    }
    public void setRequestSuggDate(String newValue) {
        this.requestSuggDate = newValue;
    }
   
	////////////////////////////////////////////////    
    public Locale getUserLocale() {
        return userLocale;
    }
    public void setUserLocale(Locale newValue) {
        this.userLocale = newValue;
    }   
    
	////////////////////////////////////////////////    
    public String getParentTaskId() {
        return parentTaskId;
    }
    public void setParentTaskId(String newValue) {
        this.parentTaskId = newValue;
    }
    
	////////////////////////////////////////////////    
    public String getIsParentTask() {
        return isParentTask;
    }
    public void setIsParentTask(String newValue) {
        this.isParentTask = newValue;
    }

	////////////////////////////////////////////////    
    public boolean getIsParentTaskCheckbox() {
        return isParentTaskCheckbox;
    }
    public void setIsParentTaskCheckbox(boolean newValue) {
        this.isParentTaskCheckbox = newValue;
    }
    
	////////////////////////////////////////////////    
    public String getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(String newValue) {
        this.taskStatus = newValue;
    }
    
    ////////////////////////////////////////////    
    public String getHideClosedTasks() {
        return hideClosedTasks;
    }
    public void setHideClosedTasks(String newValue) {
        this.hideClosedTasks = newValue;
    }

    ////////////////////////////////////////////    
    public String getShowWarnPopup() {
        return showWarnPopup;
    }
    public void setShowWarnPopup(String newValue) {
        this.showWarnPopup = newValue;
    }

    ////////////////////////////////////////////    
    public String getTaskIdFromExternalForm() {
        return taskIdFromExternalForm;
    }
    public void setTaskIdFromExternalForm(String newValue) {
        this.taskIdFromExternalForm = newValue;
    }

    ////////////////////////////////////////////    
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }

    ////////////////////////////////////////////    
    public Timestamp getFinalDate() {
        return finalDate;
    }
    public void setFinalDate(Timestamp newValue) {
        this.finalDate = newValue;
    }
    
    
    //////////////////////////////////////////////  
	public UserTO getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(UserTO newValue) {
		this.createdBy = newValue;
	}
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		//HashMap hm = new HashMap();
		
		if (this.operation.equals("addResource")){
		    
			UserTO uto = SessionUtil.getCurrentUser(request);
			
	        //validate start date field
	        if (DateUtil.getDateTime(this.initDate, uto.getCalendarMask(), this.userLocale)==null){
	            errors.add("Start Date", new ActionError("validate.manageTask.invalidStartDate") );         
	        }
	        
	        //validate estimated time field
        	if (isDecimalInput) {
    	        if (FormValidationUtil.checkFloat(errors, "Estimated Time", this.estimatedTime, this.userLocale)){
    	            float f = StringUtil.getStringToFloat(this.estimatedTime, this.userLocale);
    	            this.estimatedTime = StringUtil.getFloatToString(f, this.userLocale);
    	        }
        	} else {
		        FormValidationUtil.checkHHMM(errors, "Estimated Time", this.estimatedTime, this.userLocale, false);
        	}		    
		}
		
		if (this.operation.equals("saveTask")){
			if (this.name==null || this.name.trim().equals("")){             	
			    errors.add("Nome", new ActionError("validate.manageTask.blankName") );
			}
			
			if (this.isParentTask.equals("")){
			    @SuppressWarnings("rawtypes")
				Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");
			    
				if (resList==null || resList.size()==0){
				    errors.add("Alloc", new ActionError("validate.manageTask.blankAllocRes") );
				}
			}
		}
		
		return errors;		
	}
 
	public boolean getBoolCloseTasks(){
	    return (this.hideClosedTasks.equals("on")?true:false);
	}
	
	
	//////////////////////////////////////////////
    public void setShowAllocField(String newValue) {
        this.showAllocField = newValue;
    }
    
	@SuppressWarnings("rawtypes")
	public String getShowAllocField(){
		Vector allocList = null;
	    String response = "off";
	    
	    try {
		    if (localRequestRef!=null) {
		        allocList = (Vector)localRequestRef.getSession().getAttribute("resourceAllocated");
		    }
	        if (allocList==null || this.showAllocField.equals("on") || (allocList!=null && allocList.size()==0)) {
	            response = "on";
	        }	    	
	    } catch(Exception e) {
	    	response = "off";
	    }
	    return response;
	}
	
	
    ////////////////////////////////////////////	
	public void setDecimalInput(boolean newValue) {
		this.isDecimalInput = newValue;
	}
	public boolean isDecimalInput() {
		return isDecimalInput;
	}

	
    ////////////////////////////////////////////	
	public String getShowCloseReqConfirmation() {
		return showCloseReqConfirmation;
	}
	public void setShowCloseReqConfirmation(String newValue) {
		this.showCloseReqConfirmation = newValue;
		if (newValue!=null && newValue.equalsIgnoreCase("off")) {
			closeRequirement = "off";	
		}
	}

	
    ////////////////////////////////////////////
	public String getCloseRequirement() {
		return closeRequirement;
	}
	public void setCloseRequirement(String newValue) {
		this.closeRequirement = newValue;
	}	
	
	
}
