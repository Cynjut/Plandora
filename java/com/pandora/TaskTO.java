package com.pandora;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.bus.ResourceTaskBUS;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

/**
 * This object it is a bean that represents an Task entity. 
 */
public class TaskTO extends PlanningTO implements Comparable<TransferObject>{
        
	private static final long serialVersionUID = 1L;

    /** The Name of Task */
    private String name;

    /** Project related with task */
    private ProjectTO project;

    /** Requirement related with task */
    private RequirementTO requirement;
    
    /** Category related with task */
    private CategoryTO category;

    /** parent task of task. */
    private TaskTO parentTask;
        
    /** List of all resources of task and specific start date and estimated time for each one 
     * (List of ResourceTaskTO) */
    private Vector<ResourceTaskTO> allocResources;

    /** if is a Parent Task... */
    private Integer isParentTask;

    /** User (leader or resource) that was the creator of the current task. */
    private UserTO createdBy;
    
    /** User (leader or resource) that is handling the current task. This information is not persistent. */
    private UserTO handler;

    /** This information is not persistent. */
    private int gridLevel;

    /** List of all sub tasks. This attribute is not persistent */
    private HashMap<String, TaskTO> childTasks;
    
    /** Comment of current task */
    private String comment;
    
    private Boolean isUnpredictable;
    
    
    /**
     * This attribute is not persistent and is set by ResourceTaskDAO class
     * in order to define if task is linked with a decision point of a workflow.
     */
    private DecisionNodeTemplateTO decisionNode;
    private Integer templateInstanceId;
    
    
    /**
     * Constructor 
     */
    public TaskTO(){
    }

    
    /**
     * Constructor 
     */    
    public TaskTO(String id){
        this.setId(id);
    }      

    
    public String getEntityType(){
        return PLANNING_TASK;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(TransferObject arg) {
        int response = 0;        
        TaskTO anotherTask = (TaskTO)arg;

        //get the initial date of current task        
        if (this.hasResourceTask()){
            ResourceTaskTO firstRT1 = (ResourceTaskTO)this.getAllocResources().elementAt(0);
            Timestamp taskIniDate = firstRT1.getInitialDate();

            if (anotherTask.hasResourceTask()){
                //get the initial date of comparative task            
                ResourceTaskTO firstRT2 = (ResourceTaskTO)anotherTask.getAllocResources().elementAt(0);
                Timestamp anotherTaskIniDate = firstRT2.getInitialDate();
                
                //get the number of days between initial tasks
                response = DateUtil.getSlotBetweenDates(anotherTaskIniDate, taskIniDate);
            }
        }

        return response;
    }
    
    public static TaskTO getAdHocTask(ResourceTaskTO rtto) {
        TaskTO response = new TaskTO();
        
        Vector<ResourceTaskTO> allocRes = new Vector<ResourceTaskTO>();
        allocRes.add(rtto);
        rtto.setEstimatedTime(new Integer(rtto.getActualTime().intValue()));
        rtto.setStartDate(new Timestamp(rtto.getActualDate().getTime()));
        
        if (rtto.getTaskStatus().isOpen()) {
            rtto.setActualTime(new Integer(0));
            rtto.setActualDate(null);
        }
        
        response.setAllocResources(allocRes);
        
        response.setCategory(new CategoryTO(rtto.getAdHocCategoryId()));
        response.setComment(null);
        response.setCreationDate(DateUtil.getNow());
        response.setDescription(rtto.getAdHocTaskDescription());
        response.setFinalDate(null);
        response.setHandler(rtto.getResource());
        response.setIsParentTask(new Integer("0"));
        response.setName(rtto.getAdHocTaskName());
        response.setParentTask(null);
        response.setProject(rtto.getTask().getProject());
        response.setRequirement(null);
        response.setCreatedBy(rtto.getTask().getCreatedBy());
        response.setIsUnpredictable(new Boolean(true));
        
        if (rtto.getTask().getParentTask()!=null && !rtto.getTask().getParentTask().getId().equals("-1")) {
        	response.setParentTask(rtto.getTask().getParentTask());	
        }

        
        rtto.setTask(response);
        
        return response;
    }

    
    /**
     * Get the id of related Requirement or a null value
     */
    public String getRequirementId() {
        String reqId = null;
		if (getRequirement()!=null){
		    reqId = getRequirement().getId();
		} 
		return reqId;
    }

    
    /**
     * Get the parent task related or a null value
     */
    public String getParentTaskId() {
    	String parentTaskId = null;
    	if (getParentTask()!=null){
    	    parentTaskId = getParentTask().getId();
    	} 
    	return parentTaskId;
    }

    
    /**
     * Checks if current task contain 
     * resource task objects related.
     */
    public boolean hasResourceTask(){
        return (this.getAllocResources()!=null && this.getAllocResources().size()>0);
    }

    public boolean hasResourceTaskAlreadyStarted(){
    	boolean response = false;
        if (this.getAllocResources()!=null) {
    	    Iterator<ResourceTaskTO> i = this.getAllocResources().iterator();
    	    while(i.hasNext()){
    	        ResourceTaskTO rtto = i.next();
    	        TaskStatusTO tsto = rtto.getTaskStatus();
    	        if (tsto!=null && tsto.getStateMachineOrder()!=null && !tsto.isOpen()) {
    	        	response = true;
    	        	break;
    	        }
    	    }
        }
        return response;
    }
    
    /**
     * Generate a ordered list based on ruled of parent tasks. 
     * This recursive logic must be used by grid in order to display 
     * a tree structure of tasks.
     */
    public int order(Vector<TaskTO> unorderedList, Vector<TaskTO> orderedList, int level){
        int newLevel = level+1;
        
        this.setGridLevel(newLevel);
        orderedList.addElement(this);
        
        //get a list of child tasks...
        Iterator<TaskTO> i = unorderedList.iterator();
        while(i.hasNext()){
            //...for each child task...            
            TaskTO childTask = i.next();
            if (childTask.getParentTask()!=null && childTask.getParentTask().getId().equals(this.getId())){
                newLevel = childTask.order(unorderedList, orderedList, newLevel);
            }
        }
        
        newLevel--;
        
        return newLevel;
    }

    
    public String getInvolvedResources(){
    	return getInvolvedResources(false);
    }
    
    
    public String getInvolvedResources(boolean showAddInfo){
        StringBuffer filtered = new StringBuffer("");
        if (this.getAllocResources()!=null) {
    	    Iterator<ResourceTaskTO> i = this.getAllocResources().iterator();
    	    while(i.hasNext()){
    	        ResourceTaskTO rtto = i.next();   	        
    	        if (!filtered.toString().equals("")) {
    	        	filtered.append(", ");
    	        }
    	        String name = rtto.getResource().getName();
    	        if (rtto.getResource().getUsername().equals(RootTO.ROOT_USER)) {
    	        	name = "???";
    	        }
    	        filtered.append(name);
    	        
    	        if (showAddInfo && this.handler!=null && this.handler.getLocale()!=null) {
    	        	filtered.append(" - ");
    	        	ResourceTaskBUS rtbus = new ResourceTaskBUS();
    	        	filtered.append(DateUtil.getDate(rtbus.getPreferedDate(rtto), this.handler.getCalendarMask(), this.handler.getLocale()));
    	        	filtered.append(" - ");
    	        	Integer time = rtbus.getPreferedTime(rtto);
    	        	filtered.append(StringUtil.getFloatToString((float)(time.floatValue()/60), this.handler.getLocale()));
    	        	filtered.append("h - ");
    	        	filtered.append(rtto.getTaskStatus().getName());	
    	        }
    	    }
        }
        return filtered.toString();
    }

    

	public boolean isFinished() {
        return (this.getFinalDate()!=null);
	}

	
	public boolean isOpen() {
		boolean response = true;
        if (!this.isParentTask()){
        	
            Vector<ResourceTaskTO> resTask = this.getAllocResources();
            Iterator<ResourceTaskTO> i = resTask.iterator();
            while(i.hasNext()){
                ResourceTaskTO rtto = (ResourceTaskTO)i.next();
                if (!rtto.getTaskStatus().isOpen()) {
                	response = false;
                	break;
                }
            }
            
        } else {
        	response = (this.getFinalDate()==null);
        }
		
        return response;
	}
	
    /**
     * Insert a child task into current parent task
     * @param tto
     */
    public void addChild(TaskTO tto) {
        if (this.childTasks==null){
            this.childTasks = new HashMap<String, TaskTO>();
        }
        
        if (childTasks.get(tto.getId())==null) {
            this.childTasks.put(tto.getId(), tto);    
        }
        
        this.isParentTask = new Integer(1); //just in case..
    }

    public void addAllocResource(ResourceTaskTO rtto) {
        if (this.allocResources==null) {
            this.allocResources = new Vector<ResourceTaskTO>();
        }
        this.allocResources.addElement(rtto);
    }
    
    
    //////////////////////////////////////////////
    public CategoryTO getCategory() {
        return category;
    }
    public void setCategory(CategoryTO newValue) {
        this.category = newValue;
    }
        
    //////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

    //////////////////////////////////////////////        
    public TaskTO getParentTask() {
        return parentTask;
    }
    public void setParentTask(TaskTO newValue) {
        this.parentTask = newValue;
    }
    
    //////////////////////////////////////////////        
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }
    
    //////////////////////////////////////////////        
    public RequirementTO getRequirement() {
        return requirement;
    }
    public void setRequirement(RequirementTO newValue) {
        this.requirement = newValue;
    }
    
    //////////////////////////////////////////////    
    public Vector<ResourceTaskTO> getAllocResources() {
    	if (allocResources==null) {
    		//lazzy initialization
            ResourceTaskBUS rtbus = new ResourceTaskBUS();
            try {
				allocResources = rtbus.getListByTask(this);
			} catch (BusinessException e) {
				allocResources = null;
			}    		
    	}
        return allocResources;
    }
    public void setAllocResources(Vector<ResourceTaskTO> newValue) {
        this.allocResources = newValue;
    }
    
    //////////////////////////////////////////////    
    public UserTO getHandler() {
        return handler;
    }
    public void setHandler(UserTO newValue) {
        this.handler = newValue;
    }
    
    //////////////////////////////////////////////
    public boolean isParentTask() {
        return (isParentTask!=null && isParentTask.equals(new Integer(1)));
    }    
    public Integer getIsParentTask() {
        return isParentTask;
    }
    public void setIsParentTask(Integer newValue) {
        this.isParentTask = newValue;
    }

    //////////////////////////////////////////////    
    public int getGridLevel() {
        return gridLevel;
    }
    public void setGridLevel(int newValue) {
        this.gridLevel = newValue;
    }

    //////////////////////////////////////////////    
    public HashMap<String, TaskTO> getChildTasks() {
        return childTasks;
    }

    //////////////////////////////////////////////    
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }

    //////////////////////////////////////////////  
	public UserTO getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(UserTO newValue) {
		this.createdBy = newValue;
	}

	
    //////////////////////////////////////////////  
	public DecisionNodeTemplateTO getDecisionNode() {
		return decisionNode;
	}
	public void setDecisionNode(DecisionNodeTemplateTO newValue) {
		this.decisionNode = newValue;
	}


    //////////////////////////////////////////////  
	public Integer getTemplateInstanceId() {
		return templateInstanceId;
	}
	public void setTemplateInstanceId(Integer newValue) {
		this.templateInstanceId = newValue;
	}


    ////////////////////////////////////////////// 
	public Boolean getIsUnpredictable() {
		return isUnpredictable;
	}
	public void setIsUnpredictable(Boolean newValue) {
		this.isUnpredictable = newValue;
	}
	
	
}
