package com.pandora;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

/**
 * This object it is a bean that represents the Requirement entity.
 */
public class RequirementTO extends PlanningTO{

	private static final long serialVersionUID = 1L;

    /** The date suggested by customer when the request was done */
    private Timestamp suggestedDate;

    /** The date assumed by leader to delivery the Requirements */
    private Timestamp deadlineDate;
    
    /** The list of status related with requirement. */
    private Vector lnkRequirementHistory;

    /** The project related with requirement */
    private ProjectTO project;

    /** Category related with Requirement  */
    private CategoryTO category;
    
    /** The user that create the Requirement */
    private CustomerTO requester;
    
    /** The current status of Requirement */
    private RequirementStatusTO lnkRequirementStatus;
    
    /** The estimated time (in minutes) to execute the task completelly (used by selfAllocation) */
    private Integer estimatedTime; 
    
    /** List of resource Task objects related with current Requirement. (used by ShowAllRequreiment Form) */
    private Vector resourceTaskList;

    /** The requirement prioriry considered by customer */
    private Integer priority; 
    
    /** The id of parent requirement */
    private String parentRequirementId;
    
    /** If true, the current requirement is a Adjustment Requirement. It is not a persistent attribute */
    private boolean isAdjustment;

    /** If true, the current requirement should be reopend. It is not a persistent attribute */
    private boolean isReopening;
    
    /** If the requirement was pre-approved, this attrbute should be set with the id of the resource that will be allocated. */
    private String preApprovedReqResource;
    
    /** This field could be used by user to add a comment into the requirement history.*/
    private String additionalComment;
    
    /** This field contain the number of times that the current requirement was reopened. */
    private Integer reopeningOccurrences;
    
    /** This information is not persistent. */
    private int gridLevel = 0;
    
    
    /**
     * Constructor 
     */
    public RequirementTO(){
    }

    /**
     * Constructor 
     */    
    public RequirementTO(String id){
        this.setId(id);
    }          
    

    public RequirementTO(RequirementTO clone){
        this.suggestedDate = clone.getSuggestedDate();
        this.deadlineDate = clone.getDeadlineDate();
        this.lnkRequirementHistory=clone.getRequirementHistory();
        this.project=clone.getProject();
        this.category=clone.getCategory();
        this.requester=clone.getRequester();
        this.lnkRequirementStatus=clone.getRequirementStatus();
        this.estimatedTime=clone.getEstimatedTime(); 
        this.resourceTaskList=clone.getResourceTaskList();
        this.priority=clone.getPriority(); 
        this.parentRequirementId=clone.getParentRequirementId();
        this.isAdjustment=clone.isAdjustment();
        this.isReopening=clone.isReopening();
        this.preApprovedReqResource=clone.getPreApprovedReqResource();
        this.additionalComment=clone.getAdditionalComment();
        this.reopeningOccurrences=clone.getReopeningOccurrences();
        this.gridLevel=clone.getGridLevel();
    	
        this.setDescription(clone.getDescription());
        this.setCreationDate(clone.getCreationDate());
        this.setFinalDate(clone.getFinalDate());
        this.setAdditionalFields(clone.getAdditionalFields());
        this.setAttachments(clone.getAttachments());
        this.setDiscussionTopics(clone.getDiscussionTopics());
        this.setIteration(clone.getIteration());
        this.setRelationList(clone.getRelationList());
        this.setType(clone.getType());
        
        this.setId(clone.getId());
        this.setGenericTag(clone.getGenericTag());
        this.setGridRowNumber(clone.getGridRowNumber());
    }          
    
    
    public String getEntityType(){
        return PLANNING_REQUIREMENT;
    }
    
    
    public String getName(){
        return super.getDescription();
    }
    
    
    public boolean canPreApprove(){
        return (this.estimatedTime!=null && 
                this.suggestedDate!=null && 
                this.preApprovedReqResource!=null);
    }
        
    
    /////////////////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }

    //////////////////////////////////////////////
    public CategoryTO getCategory() {
        return category;
    }
    public void setCategory(CategoryTO newValue) {
        this.category = newValue;
    }
    
    /////////////////////////////////////////////    
    public Vector getRequirementHistory() {
        return lnkRequirementHistory;
    }
    public void setRequirementHistory(Vector newValue) {
        this.lnkRequirementHistory = newValue;
    }
    
    /////////////////////////////////////////////    
    public RequirementStatusTO getRequirementStatus() {
        return lnkRequirementStatus;
    }
    public void setRequirementStatus(RequirementStatusTO newValue) {
        this.lnkRequirementStatus = newValue;
    }
    
    /////////////////////////////////////////////    
    public CustomerTO getRequester() {
        return requester;
    }
    public void setRequester(CustomerTO newValue) {
        this.requester = newValue;
    }
    
    /////////////////////////////////////////////    
    public Timestamp getSuggestedDate() {
        return suggestedDate;
    }
    public void setSuggestedDate(Timestamp newValue) {
        this.suggestedDate = newValue;
    }
    
    /////////////////////////////////////////////    
    public Timestamp getDeadlineDate() {
        return deadlineDate;
    }
    public void setDeadlineDate(Timestamp newValue) {
        this.deadlineDate = newValue;
    }
    
    /////////////////////////////////////////////    
    public Integer getEstimatedTime() {
        return estimatedTime;
    }
    public void setEstimatedTime(Integer newValue) {
        this.estimatedTime = newValue;
    }

    /////////////////////////////////////////////    
    public Vector getResourceTaskList() {
        return resourceTaskList;
    }
    public void setResourceTaskList(Vector newValue) {
        this.resourceTaskList = newValue;
    }
    
    //////////////////////////////////////////////    
    public int getGridLevel() {
        return gridLevel;
    }
    public void setGridLevel(int newValue) {
        this.gridLevel = newValue;
    }
    
    /////////////////////////////////////////////
    public String getStrPriority() {
    	String response = "0";
    	if (priority!=null) {
    		response = priority.intValue()+"";
    	}
        return response;
    }    
    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer newValue) {
        this.priority = newValue;
    }
    
    /////////////////////////////////////////////    
    public String getParentRequirementId() {
    	if (parentRequirementId==null) {
    		
    		//find out the parent requirement throught the relation list...
    		Vector relationList = PlanningRelationTO.getRelation(getRelationList(), PlanningRelationTO.RELATION_PART_OF, this.getId(), true);
    		if (relationList!=null && relationList.size()>0) {
        		Iterator i = relationList.iterator();
        		String ids = "";
        		while(i.hasNext()) {
        			PlanningRelationTO relation = (PlanningRelationTO)i.next();
            		if (relation.getRelated()!=null &&	relation.getPlanning()!=null) {
            			if (!ids.equals("")) {
            				ids = ids + "; ";	
            			}
            			ids = ids + relation.getRelated().getId();
            		}
        		}
        		parentRequirementId = ids; 
    		}
    	}    	
		return parentRequirementId;
	}
	public void setParentRequirementId(String newValue) {
		this.parentRequirementId = newValue;
	}

	/////////////////////////////////////////////            
    public boolean isAdjustment() {
        return isAdjustment;
    }
    public void setIsAdjustment(boolean newValue) {
        this.isAdjustment = newValue;
    }
            
    /////////////////////////////////////////////    
    public String getPreApprovedReqResource() {
        return preApprovedReqResource;
    }
    public void setPreApprovedReqResource(String newValue) {
        this.preApprovedReqResource = newValue;
    }
    
    /////////////////////////////////////////////      
    public String getAdditionalComment() {
        return additionalComment;
    }
    public void setAdditionalComment(String newValue) {
        this.additionalComment = newValue;
    }
    
    /////////////////////////////////////////////        
    public boolean isReopening() {
        return isReopening;
    }
    public void setReopening(boolean newValue) {
        this.isReopening = newValue;
    }

    /////////////////////////////////////////////    
    public Integer getReopeningOccurrences() {
        if (reopeningOccurrences==null) {
            reopeningOccurrences = new Integer(0);
        }
        return reopeningOccurrences;
    }
    public void setReopeningOccurrences(Integer newValue) {
        this.reopeningOccurrences = newValue;
    }
}
