package com.pandora;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.bus.ResourceTaskBUS;
import com.pandora.delegate.TaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

/**
 * This object it is a bean that represents an Task entity. 
 */
public class TaskTO extends PlanningTO implements Comparable{
        
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
    private HashMap childTasks;
    
    /** Comment of curent task */
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
    public int compareTo(Object arg) {
        int response = 0;        
        TaskTO anotherTask = (TaskTO)arg;

        //get the initial date of current task        
        if (this.hasResourceTask()){
            ResourceTaskTO firstRT1 = (ResourceTaskTO)this.getAllocResources().elementAt(0);
            Timestamp taskIniDate = firstRT1.getInitialDate();

            if (anotherTask.hasResourceTask()){
                //get the initial date of compative task            
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
        
        Vector allocRes = new Vector();
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
    	    Iterator i = this.getAllocResources().iterator();
    	    while(i.hasNext()){
    	        ResourceTaskTO rtto = (ResourceTaskTO)i.next();
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

    /**
     * Return the current task object information on Applet PARAM format.
     * @param i
     */
    public String getResBodyFormat(int i) {
        int size = 1;
        String parent = " ";
        
        Vector alloc = this.getAllocResources();
        if (alloc!=null && alloc.size()>0){
            size = alloc.size();
        }
        
        if (this.getParentTask()!=null){
            parent = this.getParentTask().getId();
        }
        
        String buff = "<param name=\"RES_" + i + "\" value=\"" + 
        							this.getId() + "|" + 
        							StringUtil.formatWordForParam(this.getName()) + "|" +
        							StringUtil.trunc(StringUtil.formatWordForParam(this.getDescription()), 120, true) + "|" +
        							parent + "|" + size + " \" />\n";
        return buff;
    }

    
    public String getInvolvedResources(){
    	return getInvolvedResources(false);
    }
    
    
    public String getInvolvedResources(boolean showAddInfo){
        StringBuffer filtered = new StringBuffer("");
        if (this.getAllocResources()!=null) {
    	    Iterator i = this.getAllocResources().iterator();
    	    while(i.hasNext()){
    	        ResourceTaskTO rtto = (ResourceTaskTO)i.next();   	        
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

    /**
     * Return the task relationship information on Applet PARAM format.
     */
    public Vector getRelationshipBodyFormat(int i) {
    	Vector buff = new Vector();
    	int c = i;
    	try {
        	Vector v = this.getRelationList();
        	Vector resTask = this.getAllocResources();
        	TaskDelegate tdel = new TaskDelegate();
      	        	
        	if (v!=null && resTask!=null) {
        		Iterator j = v.iterator();
        		while (j.hasNext()) {
        			PlanningRelationTO prto = (PlanningRelationTO)j.next();
        			if (prto.getRelatedType().equals(PlanningRelationTO.ENTITY_TASK) &&
        					prto.getPlanType().equals(PlanningRelationTO.ENTITY_TASK) &&
        					prto.getRelated().getId().equals(this.getId())) {
        				
             			TaskTO master = tdel.getTaskObject(new TaskTO(prto.getPlanning().getId()));        				
            			Vector masterResTask = master.getAllocResources();
            			if (masterResTask!=null && masterResTask.size()>0) {
            				
                			Iterator k = resTask.iterator();
                			while(k.hasNext()){
                				ResourceTaskTO rtto = (ResourceTaskTO)k.next();
                				buff.addElement("<param name=\"DEP_" + (++c) + "\" value=\"" + rtto.getId() + "|" + this.getId() + "|" +
                							((ResourceTaskTO)(masterResTask.get(0))).getId() + "|" + master.getId() + "|1 \" />\n");
                			}    				        				
            			}
        			}
        		}
        	}    		
    	} catch(Exception e) {
    		buff = new Vector();
    	}
        
        return buff;
    }    
    
    /**
     * Return the resource task objects information on Applet PARAM format.
     * @param cursor
     * @return
     */
    public String getJobBodyFormat(int cursor){
       String response = "";
       if (!this.isParentTask()) {
           Vector resTask = this.getAllocResources();
           if (resTask!=null){
               Iterator i = resTask.iterator();
               while(i.hasNext()){
                   ResourceTaskTO rtto = (ResourceTaskTO)i.next();
                   response += rtto.getJobBodyFormat(++cursor);
               }                          
           }
       }
       return response;
    }
    
    /**
     * Return the resource task objects information on Applet PARAM format.
     * @param allocBody
     * @param currCursor
     * @return
     */
    public int getAllocBodyFormat(StringBuffer allocBody, int currCursor, Timestamp iniDate){
        int cursor = 0; 
        if (!this.isParentTask()){            
            Vector resTask = this.getAllocResources();
            Iterator i = resTask.iterator();
            while(i.hasNext()){
                ResourceTaskTO rtto = (ResourceTaskTO)i.next();
                allocBody.append(rtto.getAllocBodyFormat(currCursor+cursor, iniDate));
                cursor+=rtto.getAllocList().size();
            }           
        }
        return cursor;
    }

	public boolean isFinished() {
        return (this.getFinalDate()!=null);
	}

	
	public boolean isOpen() {
		boolean response = true;
        if (!this.isParentTask()){
        	
            Vector resTask = this.getAllocResources();
            Iterator i = resTask.iterator();
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
            this.childTasks = new HashMap();
        }
        
        if (childTasks.get(tto.getId())==null) {
            this.childTasks.put(tto.getId(), tto);    
        }
        
        this.isParentTask = new Integer(1); //just in case..
    }

    public void addAllocResource(ResourceTaskTO rtto) {
        if (this.allocResources==null) {
            this.allocResources = new Vector();
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
    public void setAllocResources(Vector newValue) {
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
    public HashMap getChildTasks() {
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
