package com.pandora;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.integration.Integration;
import com.pandora.integration.ResourceTaskIntegration;

import com.pandora.PreferenceTO;

/**
 * This object it is a bean that represents a ResourceTask entity.
 */
public class ResourceTaskTO extends TransferObject {
    
	private static final long serialVersionUID = 1L;

    /** The estimated time (in minutes) to execute the task completelly */   
    private Integer estimatedTime; 
    
    /** The initial date in order to start the task*/
    private Timestamp startDate;
    
    /** The actual initial date of task*/    
    private Timestamp actualDate;

    /** The actual time (in minutes) to execute the task completelly */    
    private Integer actualTime;
    
    /** The resource user that must be perfomr the task */
    private ResourceTO resource;

    /** The task object related */
    private TaskTO task;
        
    /** Current status of task. */
    private TaskStatusTO taskStatus;
    
    /** List of Resource Task Alloc objects */
    private Vector<ResourceTaskAllocTO> allocList;
    
    /** Current user (resource or leader) envolved with task history */
    private UserTO handler;

    /** Content to be displayed by allocated resources list box (GUI). This attribute is not persistent. */
    private String label;
        
    /** this attribute is not persistent and is used to define if 
     * the Decision Question Text of workflow must be shown into form **/
    private boolean isTheLastWIPTask = false;
    private Boolean questionAnswer = null;
    
    /** Comment set by third-part interface (ex.:Gantt) */
    private String thirdPartComment;
    
    private boolean isAdHocTask = false;
    
    private boolean isCurrentTaskCreator = false;
    
    private String adHocTaskName = "";
    
    private String adHocTaskDescription = "";
    
    private String adHocCategoryId = "";
    
    private Boolean billableStatus;
    
    
    
    /**
     * This method populate a ResourceTask transfer object
     * using the information from Integration object.
     * @throws BusinessException
     */ 
    public void populate(Integration iobj, UserTO handler) throws BusinessException {
        ResourceTaskIntegration rti = (ResourceTaskIntegration)iobj;
        TaskDelegate tdel = new TaskDelegate();
        UserDelegate udel = new UserDelegate();
                
        TaskTO tto = new TaskTO(rti.getTaskId());
        try {
            tto = tdel.getTaskObject(tto);
        } catch (BusinessException e) {
            tto = null;
        }
        
        if (tto!=null) {
            this.setTask(tto);
            
            ResourceTO rto = new ResourceTO(rti.getResourceId());
            rto.setProject(tto.getProject());
            this.setResource(udel.getResource(rto));
            
            this.setHandler(handler);            
        } else {
            throw new BusinessException("There is not a task " + rti.getTaskId() + " into database");
        }
        
        if (this.taskStatus!=null) {
            if (this.taskStatus.isOpen()) {
                this.setStartDate(DateUtil.getDate(rti.getEstimatedDay(), rti.getEstimatedMonth(), rti.getEstimatedYear()));
                this.setEstimatedTime(new Integer(rti.getEstimatedTime()));    
            } else {
                this.setActualDate(DateUtil.getDate(rti.getEstimatedDay(), rti.getEstimatedMonth(), rti.getEstimatedYear()));
                this.setActualTime(new Integer(rti.getEstimatedTime()));
            }            
        }
        this.setThirdPartComment(rti.getComment());
                
    }
    
    /**
     * Overload the getId method of superclass returning the id of resource task 
     * through the id of task, resource and projects concatenated.
     */
    public String getId(){
        String myId = null;
        if (this.getTask()!=null && this.getResource()!=null && this.getTask().getProject()!=null) {
            String taskId = this.getTask().getId();
            String resId = this.getResource().getId();
            myId = taskId + "-" + resId + "-" + this.getTask().getProject().getId();            
        }
        return myId;
    }
    
    /**
     * Return the resource task object information on Applet PARAM format.
     * @param i
     * @return
     */
    public String getJobBodyFormat(int i) {
        String taskId = this.getTask().getId();
        String resId = this.getResource().getId();
        TaskStatusTO tsto = this.getTaskStatus();
        
        String type = "0";
        if (tsto.getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
            type = "1";
        }
        
        return "<param name=\"JOB_" + i + "\" value=\"" + this.getId() + "|" + taskId + "| |" + tsto.getName() + "|" + resId + "|" + type + "\" />\n";
    }
    
    /**
     * Return the resource task alloc objects information on Applet PARAM format.
     * @param cursor
     * @param iniDate
     * @return
     */
    public String getAllocBodyFormat(int cursor, Timestamp iniDate) {
        String response = "";
        Vector<ResourceTaskAllocTO> allocList = this.getAllocList();
        Iterator<ResourceTaskAllocTO> i = allocList.iterator();
        ResourceTaskDelegate rtdel = new ResourceTaskDelegate();

        //define the decoration of gantt alloc unit
        int allocUnitType = 0;
        //TODO soh pode descomentar depois que implmentar corretamente o shape de allocUnit no gantt
        //Integer state = this.taskStatus.getStateMachineOrder();
        //if (state.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
        //   allocUnitType = 1;
        //}

        
        while(i.hasNext()){
            ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)i.next();

            Timestamp refDate = rtdel.getPreferedDate(this);            
            int currSlot = DateUtil.getSlotBetweenDates(iniDate, refDate) + rtato.getSequence().intValue();
            response+=rtato.getAllocBodyFormat(++cursor, currSlot, allocUnitType);
        }           
        return response;
    }
    
    /**
     * Return the lattest date/time of current ResourceTask. The value returned
     * is calculated based on: actualDate or startDate + actualTime or estimatedTime.
     * @return
     */
    public Timestamp getLattestDate(){
        Timestamp endResTask = null;
        
        //get the actual date or optionally the start date
        endResTask = this.getInitialDate();
        
        //get the actual time or optionally the estimanted time, 
        //and increment (in minutes -> type=12) the endResTask variable.
        //Note: We have to multiply by 3, because the minutes of time (actual or estimated) is only 1/3 of day
        //and the calculation here should to shift day by day.
        if (getActualTime()!=null){
            endResTask = DateUtil.getChangedDate(endResTask, 12 , (getActualTime().intValue() * 3));
        } else {
            endResTask = DateUtil.getChangedDate(endResTask, 12 , (getEstimatedTime().intValue() * 3));
        }

        return endResTask;
    }
    
    public String getClassification(){
    	String response = "0";
    	
		TaskTO tto = this.getTask();
		if (handler!=null && tto!=null) {
			PreferenceTO pto = handler.getPreference();
			if (pto!=null) {
				String pin = pto.getPreference(PreferenceTO.PIN_TASK_LIST);
				if (pin!=null && !pin.trim().equals("")){			
					StringTokenizer st = new StringTokenizer(pin, "|");
		            while (st.hasMoreTokens()) {
		            	String token = st.nextToken();
		            	String[] element = token.split(";");
		            	if (element!=null && element.length==2) {
		            		if (element[0].trim().equals(tto.getId())) {
		            			response = element[1];
		            			break;
		            		}
		            	}
		            }
				}							
			}
		}
    	return response;
    }
    
    /**
     * Return the initial date of resource Task based on actualDate (prefered) or startDate (optionally) 
     * @return
     */
    public Timestamp getInitialDate(){
        Timestamp currDate = this.getActualDate();
        if (currDate==null) {
            currDate = this.getStartDate();    
        }        
        return currDate;
    }
    
    ////////////////////////////////////////
    public float getEstimatedTimeInHours() {
        int min = 0;
        if (estimatedTime!=null){
            min = estimatedTime.intValue();
        }        
        return ((float)min/60);
    }    
    public Integer getEstimatedTime() {
        return estimatedTime;
    }
    public void setEstimatedTime(Integer newValue) {
        this.estimatedTime = newValue;
    }

    ////////////////////////////////////////
    public Timestamp getStartDate() {
        return startDate;
    }
    public void setStartDate(Timestamp newValue) {
        this.startDate = newValue;
    }
    
    ////////////////////////////////////////    
    public ResourceTO getResource() {
        return resource;
    }
    public void setResource(ResourceTO newValue) {
        this.resource = newValue;
    }
    
    ////////////////////////////////////////    
    public TaskTO getTask() {
        return task;
    }
    public void setTask(TaskTO newValue) {
        this.task = newValue;
    }
    
    ////////////////////////////////////////        
    public String getLabel() {
        return label;
    }
    public void setLabel(String newValue) {
        this.label = newValue;
    }
       
    //////////////////////////////////////////////    
    public TaskStatusTO getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(TaskStatusTO newValue) {
        this.taskStatus = newValue;
    }

    //////////////////////////////////////////////    
    public Vector<ResourceTaskAllocTO> getAllocList() {
        return allocList;
    }
    public void setAllocList(Vector<ResourceTaskAllocTO> newValue) {
        this.allocList = newValue;
    }

	////////////////////////////////////////    
    public Timestamp getActualDate() {
        return actualDate;
    }
    public void setActualDate(Timestamp newValue) {
        this.actualDate = newValue;
    }
    
	////////////////////////////////////////
    public float getActualTimeInHours() {
        int min = 0;
        if (actualTime!=null){
            min = actualTime.intValue();
        }
        return ((float)min/60);
    }    
    public Integer getActualTime() {
        return actualTime;    
    }
    public void setActualTime(Integer newValue) {
        this.actualTime = newValue;
    }
    
    //////////////////////////////////////////    
    public UserTO getHandler() {
        return handler;
    }
    public void setHandler(UserTO newValue) {
        this.handler = newValue;
    }


    //////////////////////////////////////////    
    public String getThirdPartComment() {
        return thirdPartComment;
    }
    public void setThirdPartComment(String newValue) {
        this.thirdPartComment = newValue;
    }
    
    
    public String toString() {
        return this.getId();
    }    

    //////////////////////////////////////////       
    public boolean isAdHocTask() {
        return isAdHocTask;
    }
    public void setAdHocTask(boolean newValue) {
        this.isAdHocTask = newValue;
    }
    
    
    //////////////////////////////////////////         
    public boolean isCurrentTaskCreator() {
		return isCurrentTaskCreator;
	}
	public void setCurrentTaskCreator(boolean newValue) {
		this.isCurrentTaskCreator = newValue;
	}

	
	//////////////////////////////////////////        
    public String getAdHocTaskDescription() {
        return adHocTaskDescription;
    }
    public void setAdHocTaskDescription(String newValue) {
        this.adHocTaskDescription = newValue;
    }
    
    
    //////////////////////////////////////////        
    public String getAdHocTaskName() {
        return adHocTaskName;
    }
    public void setAdHocTaskName(String newValue) {
        this.adHocTaskName = newValue;
    }
    
   
    //////////////////////////////////////////       
    public String getAdHocCategoryId() {
        return adHocCategoryId;
    }
    public void setAdHocCategoryId(String newValue) {
        this.adHocCategoryId = newValue;
    }

    
    //////////////////////////////////////////   
	public boolean isTheWIPTask() {
		return isTheLastWIPTask;
	}
	public void setIsTheWIPTask(boolean newValue) {
		this.isTheLastWIPTask = newValue;
	}
    
	
    ////////////////////////////////////////////   
    public Boolean getQuestionAnswer() {
		return questionAnswer;
	}
	public void setQuestionAnswer(Boolean newValue) {
		this.questionAnswer = newValue;
	}

	
    ////////////////////////////////////////////   
	public Boolean getBillableStatus() {
		return billableStatus;
	}
	public void setBillableStatus(Boolean newValue) {
		this.billableStatus = newValue;
	}
	
    
    
}
