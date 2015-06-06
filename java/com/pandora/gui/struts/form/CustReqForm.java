package com.pandora.gui.struts.form;

import java.sql.Timestamp;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * This class handle the data of Customer Request Form
 */
public class CustReqForm extends HosterRepositoryForm {


	private static final long serialVersionUID = 1L;
	
    /** Name of requester customer */
    private String requester;
    
    /** request number (id) to display into form */    
    private String reqNum;
    
    /** Category Id related with requirement */
    private String categoryId;
    
    /** Id of requester customer */
    private String requesterId;
    
    /** Id of project related with request */
    private String projectRelated;

    /** The priority of requirement set by customer */
    private String priority;

    /** Description of request */
    private String description;

    /** Suggested date for requested proposed by customer*/
    private String suggestedDate;

    /** Deadline date for requested proposed by leader */
    private String deadlineDate;
    
    /** checkbox that defines if must to show or hide the closed requests into grid.*/
    private String hideClosedRequests = "on";

    /** checkbox that defines if current requirement is a selfAlloc or not.*/
    private String isPreApproveRequest = "off";
    
    /** show or hide some fields related to adjustment of customer input by leader.*/
    private String isAdjustment = "off";

    /** show or hide some fields related to adjustment not considering the project changing.*/
    private String isAdjustmentWithoutProj = "off";

    /** show or hide some fields related to the Re-Opening action.*/
    private String isReopenRequirement = "off";

    /** enable or disable the priority combo box.*/
    private String isPriorityChangeAllowed = "on";

    /** show or hide the discussion topic feature depending the settings of user/project.*/
    private String canSeeDiscussion = "off";

    private String showTechComments = "off";
    
    
    /** show or hide the artifacts grid depending the user/project setting. 
     * A customer is not allow to see this grid. Resources/Leader will see this 
     * grid always but will edit only under permission.*/
    private String canSeeArtifacts = "off";  

    private String canChangeRequester = "off";
    
    /** enable/disable form to be displayed in 'read only' mode.*/
    private String readOnlyMode = "off";

    /** define if requester is a leader of related project.*/
    private String isRequesterLeader = "off";
    
    /** Comment related to the re-opening action performed by customer. */
    private String comment = "";
    
    /** show or hide the backwards button. Arbitrary value different of null, mean =TRUE */
    private String showBackward = null;

    /** List of boolean status (1 or 0) of current preApproveReq status for each project of combo list */
    private String preApproveList = "";
    
    /** The Estimated Time of Task related (used by selfAllocation) */
    private String estimTime;

    /** The creation date of current requirement */
    private Timestamp creationDate;

    /** If customer is able to pre-approve requirement, this attribute contain the resource related to the tasl, that should be creanted */
    private String selectedResource;
    
    /** The current iteration related to the requirement */
    private String iteration;

    /** The previous priority values of requirement */
    private String previousPriority;
    

    /**
     * This method is used by customerHome.jsp
     * @return
     */
    public String getPreApproveReqShow() {
        String response = "off";
        if (this.isAdjustment.equals("off") && isPreApproveRequest.equals("on")){
            response = "on";
        }
        return response;
    }

    
    /**
     * Clear values of Form
     */
    public void clear(){
        this.id = null;
        this.requester = null;
        this.reqNum = null;
        this.requesterId = null;
        this.projectRelated = null;
        this.categoryId = null;        
        this.description = null;
        this.suggestedDate = null;
        this.hideClosedRequests = "on";
        this.isAdjustment = "off";
        this.isAdjustmentWithoutProj = "off";
        this.isReopenRequirement = "off";
        this.isPriorityChangeAllowed = "on";
        this.canSeeDiscussion = "off";
        this.canSeeArtifacts = "off";         
        this.canChangeRequester = "off"; 
        this.readOnlyMode = "off";
        this.isRequesterLeader = "off";
        this.showTechComments = "off";
        this.setSaveMethod(null, null);
        this.estimTime = null;
        this.selectedResource = "-1";
        this.comment = null;
        this.iteration = null;
        this.previousPriority = null;
        this.priority = null;
        this.setAdditionalFields(null);
    }    


    ////////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    ////////////////////////////////////////////    
    public String getHideClosedRequests() {
        return hideClosedRequests;
    }
    public void setHideClosedRequests(String newValue) {
        this.hideClosedRequests = newValue;
    }
    
    ////////////////////////////////////////////    
    public String getIsPreApproveRequest() {
        return this.isPreApproveRequest;
    }
    public void setIsPreApproveRequest(String newValue) {
        if (this.preApproveList!=null && this.preApproveList.indexOf("1", 0)>-1){
            this.isPreApproveRequest = newValue;
        } else {
            this.isPreApproveRequest = "off";
        }
    }
        
    ////////////////////////////////////////////    
    public String getProjectRelated() {
        return projectRelated;
    }
    public void setProjectRelated(String newValue) {
        this.projectRelated = newValue;
    }
    
	////////////////////////////////////////////////    
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }
    
    ////////////////////////////////////////////    
    public String getRequester() {
        return requester;
    }
    public void setRequester(String newValue) {
        this.requester = newValue;
    }

    ////////////////////////////////////////////    
    public String getRequesterId() {
        return requesterId;
    }
    public void setRequesterId(String newValue) {
        this.requesterId = newValue;
    }

    ////////////////////////////////////////////    
    public String getReqNum() {
        return reqNum;
    }
    public void setReqNum(String newValue) {
        this.reqNum = newValue;
    }
    
    ////////////////////////////////////////////    
    public String getSuggestedDate() {
        return suggestedDate;
    }
    public void setSuggestedDate(String newValue) {
        this.suggestedDate = newValue;
    }

    ////////////////////////////////////////////        
    public String getDeadlineDate() {
        return deadlineDate;
    }
    public void setDeadlineDate(String newValue) {
        this.deadlineDate = newValue;
    }
    
    ////////////////////////////////////////////    
    public String getShowBackward() {
        return showBackward;
    }
    public void setShowBackward(String newValue) {
        this.showBackward = newValue;
    }        
    
    ////////////////////////////////////////////    
    public String getIsAdjustment() {
        return isAdjustment;
    }
    public void setIsAdjustment(String newValue) {
        this.isAdjustment = newValue;
    }

    ////////////////////////////////////////////    
    public String getPreApproveList() {
        return preApproveList;
    }
    public void setPreApproveList(String newValue) {
        this.preApproveList = newValue;
    }
    
	////////////////////////////////////////    
    public String getEstimTime() {
        return estimTime;
    }
    public void setEstimTime(String newValue) {
        this.estimTime = newValue;
    }
    
	////////////////////////////////////////    
    public String getPriority() {
        return priority;
    }
    public void setPriority(String newValue) {
        this.priority = newValue;
    }

	////////////////////////////////////////    
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }
    
    
	////////////////////////////////////////   
    public String getIsAdjustmentWithoutProj() {
        return isAdjustmentWithoutProj;
    }
    public void setIsAdjustmentWithoutProj(String newValue) {
        this.isAdjustmentWithoutProj = newValue;
    } 

    
	////////////////////////////////////////       
	public String getIsPriorityChangeAllowed() {
		return isPriorityChangeAllowed;
	}
	public void setIsPriorityChangeAllowed(String newValue) {
		this.isPriorityChangeAllowed = newValue;
	}


	////////////////////////////////////////	
	public String getCanSeeDiscussion() {
		return canSeeDiscussion;
	}
	public void setCanSeeDiscussion(String newValue) {
		this.canSeeDiscussion = newValue;
	}

	
	////////////////////////////////////////
	public String getCanSeeArtifacts() {
		return canSeeArtifacts;
	}
	public void setCanSeeArtifacts(String newValue) {
		this.canSeeArtifacts = newValue;
	}


	////////////////////////////////////////	
	public String getCanChangeRequester() {
		return canChangeRequester;
	}
	public void setCanChangeRequester(String newValue) {
		this.canChangeRequester = newValue;
	}
	
	////////////////////////////////////////    
    public String getSelectedResource() {
        return selectedResource;
    }
    public void setSelectedResource(String newValue) {
        this.selectedResource = newValue;
    }    

	////////////////////////////////////////    
    public String getIsReopenRequirement() {
        return isReopenRequirement;
    }
    public void setIsReopenRequirement(String newValue) {
        this.isReopenRequirement = newValue;
    }

	////////////////////////////////////////    
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }
    
	////////////////////////////////////////        
	public String getIteration() {
		return iteration;
	}
	public void setIteration(String newValue) {
		this.iteration = newValue;
	}

	//////////////////////////////////////// 
	public String getPreviousPriority() {
		return previousPriority;
	}
	public void setPreviousPriority(String newValue) {
		this.previousPriority = newValue;
	}

	
	//////////////////////////////////////// 	
	public String getReadOnlyMode() {
		return readOnlyMode;
	}
	public void setReadOnlyMode(String newValue) {
		this.readOnlyMode = newValue;
	}

	
	//////////////////////////////////////// 	
	public String getShowTechComments() {
		return showTechComments;
	}
	public void setShowTechComments(String newValue) {
		this.showTechComments = newValue;
	}

	

	//////////////////////////////////////// 	
    public String getIsRequesterLeader() {
        return isRequesterLeader;
    }
    public void setIsRequesterLeader(String newValue) {
        this.isRequesterLeader = newValue;
    }
    
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
				
		if (this.operation.equals("saveRequirement")){
		    Locale loc = SessionUtil.getCurrentLocale(request);
		    UserTO uto = SessionUtil.getCurrentUser(request);

		    if (this.requester==null || this.requester.trim().equals("")){
		    	errors.add("id", new ActionError("validate.requestForm.requester"));
		    }

		    if (this.readOnlyMode.equals("on")){
		    	errors.add("id", new ActionError("validate.requestForm.readonlyMode"));
		    }
		    
		    if (this.isAdjustment.equals("off")){
				//check if suggested date is before now...
		        Timestamp sugg = getDateField(this.suggestedDate, uto, loc);
				if (sugg!=null && sugg.before(DateUtil.getNow())) {
				    errors.add("DataSugerida", new ActionError("validate.requestForm.suggDateBefNow"));
				}		        
		    }
		    
		    if (this.isPriorityChangeAllowed.equals("off") && !previousPriority.equals(priority)) {
		    	errors.add("priority", new ActionError("validate.requestForm.priorNotAllowed"));
		    }
		    
		    
		    if (this.isReopenRequirement.equals("on")) {
		        if (this.comment==null || this.comment.trim().length()==0){
		            errors.add("Comentario", new ActionError("validate.requestForm.comment"));    
		        }
		    }

	        //validate estimated time field (only if preApproveRequest checkbox is enable)
			if (request.getParameter("isPreApproveCheckbox")!=null && 
			        request.getParameter("isPreApproveCheckbox").equals("on")){
		        Timestamp sugg = getDateField(this.suggestedDate, uto, loc);
				if (sugg==null) {
				    errors.add("DataSugerida", new ActionError("validate.requestForm.reqSuggDate"));
				}		        
			    
				if (this.estimTime!=null){
			        if (!StringUtil.checkIsFloat(this.estimTime, loc)){
			            errors.add("Estimated Time", new ActionError("validate.requestForm.invalidEstimTime") );         
			        } else {
			            float f = StringUtil.getStringToFloat(this.estimTime, loc);
			            this.estimTime = StringUtil.getFloatToString(f, loc);	            
			        }
				    
				}
	            if (this.estimTime==null || this.estimTime.trim().equals("")){
	                errors.add("EstimTime", new ActionError("validate.requestForm.reqEstimTime") );
	            }
	            
	            if (this.selectedResource!=null && this.selectedResource.equals("-1")){
	                errors.add("resource", new ActionError("validate.requestForm.selResource") );
	            }
			}
			
			if (this.description==null || this.description.trim().equals("")){             	
			    errors.add("Descricao", new ActionError("errors.required", "Descricao"));
			}
			
			if (this.projectRelated==null || this.projectRelated.trim().equals("")){             	
			    errors.add("Project", new ActionError("errors.required", "Projeto") );
			}
			
			//is form is a requirement adjustment, check if current user is a leader
			if (this.isAdjustment!=null && this.isAdjustment.equals("on")){
			    			    
			    boolean isLeader = uto.isLeader(new ProjectTO(this.projectRelated));
			    if (!uto.getUsername().equals(this.requester) && !isLeader) {
			        errors.add("Role", new ActionError("validate.requestForm.adjustRole"));
                }

    			//check if deadline date is before now...
			    Timestamp deadline = getDateField(this.deadlineDate, uto, loc);
    			if (deadline!=null){
    			    if (deadline.before(DateUtil.getNow())){
    			        errors.add("DataSugerida", new ActionError("validate.requestForm.deadlineDateBefNow"));
    			    }
    			}
                
			}
		}

		return errors;
	}

	/**
	 * Get the a date value from form.
	 */
	private Timestamp getDateField(String dateField, UserTO uto, Locale loc){
	    Timestamp response = null;
	    
	    try {
			if (dateField!=null && !dateField.trim().equals("")){
			    response = DateUtil.getDateTime(dateField, uto.getCalendarMask(), loc);
			    response = DateUtil.getDate(response, false);
			}	        
	    } catch(Exception e){
	        response = null;
	    }
		
		return response;
	}

	public boolean getBoolCloseRequest(){
	    return (this.hideClosedRequests.equals("on")?true:false);
	}
}
