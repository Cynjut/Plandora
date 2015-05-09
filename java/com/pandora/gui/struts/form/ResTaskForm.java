package com.pandora.gui.struts.form;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.FormValidationUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * This class handle the data of Resource Task Form
 */
public class ResTaskForm extends HosterRepositoryForm {
    
	private static final long serialVersionUID = 1L;
	
    public static final String EMPTY_SLOT = "-";
    
        
	/** The Name of Task related (disable field on GUI)*/
    private String taskName;
    
    /** The Description of Task related (disable field on GUI)*/
    private String taskDesc;
    
    /** The Status of Task related (state machine number) */
    private Integer taskStatus;
    
    /** The Estimated Date of Task related (disable field on GUI)*/
    private String estimDate;
    
    /** The Estimated Time of Task related (disable field on GUI)*/
    private String estimTime;
    
    /** The actual initial date of task*/
    private String actualDate;
    
    /** The actual time (in hours) to execute the task completelly */
    private String actualTime;
    
    /** The comment typed by resource related with current task status */
    private String comment;
    
    /** The id of project related */
    private String projectId;
    
    /** The id of resource related */
    private String resourceId;
    
    /** The id of task related */
    private String taskId;

    /** show or hide the closed tasks into grid.*/
    private String hideClosedTasks = "on";
    
    /** Customer name related with requirement of task */
    private String reqCustomerName;

    /** show or hide the allocated Days Grid .*/
    private String showAllocatedDays = "off";

    /** define if the creator of current task is the current user.*/
    private String isCurrentTaskCreator = "off";

    /** define if the macro task confirmation dialog box (to close it) must be shown.*/
    private String showCloseMacroTaskConfirm = "off";
    
    private String showWorkflowDiagram = "off";

    private String allowBillable = "off";

    private String billableStatus;
    
    /** the Allocation hash cursor for displaying purposes */
    private int allocCursor;

    /** the number of days of cursor skipping */
    private int stepCursor;

    /** The date of the first allocation slot */
    private String firstAllocSlot;
    
    /** List of Allocation values indexed by allocation date in string format */
    private HashMap<String, ResourceTaskAllocTO> allocationList;

    /** List of Estimated Allocation values indexed by allocation date in string format */
    private HashMap<String, ResourceTaskAllocTO> estimatedAllocList;
    
    /** Mask used to format the date on top of schedule grid (The mask was read from bundle) */
    private String titleDateMask;

    /** category id used by adHoc task */
    private String categoryId;

    private String iterationId;
    
    /** define if the Decision Question Text must be shown .*/
    private String showDecisionQuestion = "off";

    /** The answer of decision point */
    private Boolean questionAnswer;

    /** The question text of decision point */
    private String questionText;

    /** The parent task related */
    private String parentTaskId;

    private boolean isDecimalInput; 
    
    private String reportTaskURL;
    
    private String planningId;
    
    private String dateMask;
    
    private Locale defaultLocale;
    
    
    /**
     * Clear values of Form
     */
    public void clear(){
    	super.setMultiple("on");
    	super.setOnlyFolders("off");
    	
        this.hideClosedTasks = "on";
        this.showAllocatedDays = "off";
        this.isCurrentTaskCreator = "off";
        this.allocationList = null;
        this.estimatedAllocList = null;
        this.allocCursor = 1;
        this.taskName = null;
        this.taskDesc = null;
        this.comment = null;
        this.reqCustomerName = null;
        this.actualDate = null;
        this.estimDate = null;
        this.actualTime = null;
        this.estimTime = null;
        this.categoryId = null;
        this.taskStatus = null;
        this.firstAllocSlot = null;
        this.questionAnswer = null;
        this.questionText = null;
        this.planningId = null;
        this.showDecisionQuestion = "off";
        this.showCloseMacroTaskConfirm = "off";
        this.stepCursor = 7;
        this.reportTaskURL = "";
        this.showWorkflowDiagram = "off";
        this.allowBillable = "off";
        this.iterationId = "-1";
        this.billableStatus = "0";
    }

	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		Locale loc = SessionUtil.getCurrentLocale(request);
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		if (this.operation.equals("saveResTaskPreProcess")){
				    
	        //validate actual date field
		    FormValidationUtil.checkDate(errors, "Actual Date", this.actualDate, loc, uto.getCalendarMask());
		    
		    //for Ad hoc task, check if 'actual date' field is not null
		    if (this.getIsAdHocTask().equals("on")) {
				if (this.taskName==null || this.taskName.trim().equals("")){             	
				    errors.add("Name", new ActionError("validate.manageTask.blankName") );
				}		        		    	
		    }

			if (this.projectId==null || this.projectId.trim().equals("") || this.projectId.trim().equals("-1")){             	
			    errors.add("Project", new ActionError("validate.manageTask.blankProject") );
			}		        		    	
					
			
			if (this.showDecisionQuestion.equals("on") && this.questionAnswer==null && 
					this.taskStatus!=null && this.taskStatus.equals(TaskStatusTO.STATE_MACHINE_CLOSE)) {
				errors.add("Question", new ActionError("validate.manageTask.blankQuestion") );
			}
	        
			
			//validate actual time field
			if (this.actualTime!=null){
            	if (isDecimalInput) {
    		        if (FormValidationUtil.checkFloat(errors, "Actual Time", this.actualTime, loc)) {         
    		            float f = StringUtil.getStringToFloat(actualTime, loc);
    		            actualTime = StringUtil.getFloatToString(f, loc);	            
    		        }            		 
            	} else {
    		        FormValidationUtil.checkHHMM(errors, "Actual Time", this.actualTime, loc, false);
            	}
			}

	        //if combo is Close or Cancel, the actual date, actual time and comments are mandatory
		    if (this.getIsAdHocTask().equals("off")) {

		        if (this.taskStatus!=null && (this.taskStatus.equals(TaskStatusTO.STATE_MACHINE_CLOSE) || 
		        		this.taskStatus.equals(TaskStatusTO.STATE_MACHINE_CANCEL))){
    	            				    
			        if (this.comment==null || this.comment.trim().equals("")){
			            errors.add("Comment", new ActionError("validate.resTaskForm.reqComment") );
			        }
			    }
		    }			
		}

		//validate the grid values
        for (int i=1; i<=7; i++) {
            String topValue = request.getParameter("slotTop"+ i);
            if (topValue!=null && !topValue.trim().equals(EMPTY_SLOT)) {
            	
            	if (isDecimalInput) {
                    FormValidationUtil.checkFloat(errors, uto.getBundle().getMessage(loc, "label.resTaskForm.estTime"), topValue, loc);
                    FormValidationUtil.checkMaxValue(errors, uto.getBundle().getMessage(loc, "label.resTaskForm.estTime"), topValue, 24, loc);                
            	} else {
            		FormValidationUtil.checkHHMM(errors, uto.getBundle().getMessage(loc, "label.resTaskForm.estTime"), topValue, loc, true);            		
            	}
            	
            }
            String botValue = request.getParameter("slotBotton"+ i);
        	if (isDecimalInput) {
                FormValidationUtil.checkFloat(errors, uto.getBundle().getMessage(loc, "label.resTaskForm.actualTime"), botValue, loc);
                FormValidationUtil.checkMaxValue(errors, uto.getBundle().getMessage(loc, "label.resTaskForm.actualTime"), botValue, 24, loc);
        	} else {
                FormValidationUtil.checkHHMM(errors, uto.getBundle().getMessage(loc, "label.resTaskForm.actualTime"), botValue, loc, true);
        	}
            
        }
		
		
		return errors;
	}

	
	public String getIsAdHocTask(){
	    String response = "off";
	    if (this.getProjectId()==null || this.getProjectId().equals("") || 
	            this.getTaskId()==null || this.getTaskId().equals("")) {
	        response = "on";
	    }
	    return response;
	}
	
	
	public String getContainEstimatedValues(){
		return (estimatedAllocList!=null && estimatedAllocList.size()>0 ? "on": "off");
	}
	
	
	public boolean getBoolCloseRequest(){
	    return (this.hideClosedTasks.equals("on")?true:false);
	}

	
    public void addAllocationList(Timestamp prefDate, ResourceTaskAllocTO rtato, int index, boolean forceAddIntoActual) {
        ResourceTaskAllocTO alloc = null;
        if (this.allocationList==null) {
            this.allocationList = new HashMap<String, ResourceTaskAllocTO>();
        }

        Timestamp refDate = rtato.getResourceTask().getStartDate();
        if (!this.taskStatus.equals(TaskStatusTO.STATE_MACHINE_OPEN) || forceAddIntoActual) {
            if (rtato.getResourceTask().getActualDate()!=null) {
            	refDate = rtato.getResourceTask().getActualDate();
            }
        }
    	if (prefDate!=null) {
        	int diff = DateUtil.getSlotBetweenDates(prefDate, refDate);
        	if (diff!=0) {
        		refDate = prefDate;
        	}            		
    	}            
        Timestamp dateShift = DateUtil.getChangedDate(refDate, Calendar.DATE, index-1);
            
        if (rtato!=null) {
            alloc = new ResourceTaskAllocTO(rtato);    
        }
        
        if (this.taskStatus.equals(TaskStatusTO.STATE_MACHINE_OPEN) && !forceAddIntoActual) {
            if (this.estimatedAllocList==null) {
            	this.estimatedAllocList = new HashMap<String, ResourceTaskAllocTO>();
            }
        	this.estimatedAllocList.put(getAllocKey(dateShift), alloc);	
        } else {
        	this.allocationList.put(getAllocKey(dateShift), alloc);	
        }
    }
    

    public void updateAllocationList(Timestamp prefDate, String topValue, String bottonValue, int index, ResourceTaskTO rtto, Locale loc) {
        String value = "";    	
    	if (bottonValue!=null && !bottonValue.equals("")) {
           value = bottonValue;
        }

        String key = this.getAllocKey(DateUtil.getChangedDate(prefDate, Calendar.DATE, index-1));        

        if (value==null || value.trim().length()==0) {
            value = "0";
        }

        int valueInMin = 0;
        if (isDecimalInput) {
        	valueInMin = (int)(StringUtil.getStringToFloat(value, loc) * 60);	
        } else {
        	valueInMin = StringUtil.getHHMMToInteger(value);
        }
        if (valueInMin<0) {
            valueInMin = Math.abs(valueInMin);
        }

        ResourceTaskAllocTO rtato = null;
        if (this.getAllocationList()!=null) {
            rtato = (ResourceTaskAllocTO)this.getAllocationList().get(key);    
        }
        if (rtato==null) {
            rtato = new ResourceTaskAllocTO(rtto, index, valueInMin);    
            this.addAllocationList(prefDate, rtato, index, true);                
        } else {
            rtato.setAllocTime(new Integer(valueInMin));    
        }

        //reorder the sequence...        
        this.reorderSequence(prefDate, rtto);        
    }

    
    public void reorderSequence(Timestamp prefDate, ResourceTaskTO rtto) {
        int newSeq = 0;
        int upperbound = this.getAllocationList().size();
        
		Timestamp refDate = rtto.getActualDate();
		if (refDate==null) {
		    refDate = rtto.getStartDate(); 
		}
    	int diff = DateUtil.getSlotBetweenDates(prefDate, refDate);
    	if (diff!=0) {
    		refDate = prefDate;
    	}            		
        
        for (int i=0; i<upperbound; i++ ){
            newSeq++;
            String cursorDate = this.getAllocKey(DateUtil.getChangedDate(refDate, Calendar.DATE, i));
            ResourceTaskAllocTO alloc = (ResourceTaskAllocTO)this.getAllocationList().get(cursorDate);
            if (alloc!=null) {
                alloc.setSequence(new Integer(newSeq));
            } else {
                //fill the gaps with blank allocations
                ResourceTaskAllocTO rtato = new ResourceTaskAllocTO(rtto, newSeq, 0);    
                this.addAllocationList(refDate, rtato, newSeq, true);                
            }
        }
    }

    
    private String getAllocKey(Timestamp date){
        return DateUtil.getDate(date, this.dateMask, this.defaultLocale);
    }
    
	
	private String getTitleForSlot(int slotIndex){
	    String response = "";
	    Locale loc =  this.getCurrentUser().getLocale();
	    Timestamp currDate = null;
	    
	    if (this.getActualDate()!=null) {
	        currDate = this.getDate(false, loc);    
	    } else {
	        currDate = this.getDate(true, loc);
	    }
	    
		currDate = DateUtil.getChangedDate(currDate, Calendar.DATE, slotIndex + (this.getAllocCursor()-1));
		response = DateUtil.getDate(currDate, titleDateMask, loc);
		
	    return response;	    
	}
	
	
	public Timestamp getDate(boolean isEstimated, Locale loc){
	    Timestamp response = DateUtil.getNow();
	    UserTO uto = this.getCurrentUser();
	    String format = uto.getCalendarMask();
	    
	    if (isEstimated) {
	        if (this.getEstimDate()!=null && !this.getEstimDate().trim().equals("")) {
	            response = DateUtil.getDateTime(this.getEstimDate(), format, loc);
	        }
	    } else {
	        if (this.getActualDate()!=null && !this.getActualDate().trim().equals("")) {
	            response = DateUtil.getDateTime(this.getActualDate(), format, loc);
	        }
	    }
	    
	    return response;
	}
	
	
    public Vector<ResourceTaskAllocTO> getAllocationInVectorFormat() {
        Vector<ResourceTaskAllocTO> list = new Vector<ResourceTaskAllocTO>();

        if (allocationList!=null) {
            Iterator<ResourceTaskAllocTO> i = allocationList.values().iterator();
            while(i.hasNext()){
                ResourceTaskAllocTO rtato = i.next();
                if (rtato!=null) {
                    list.addElement(rtato);
                }
            }            
        }
        return list;
    }
    
    public Integer sumarizeEstimatedTimeInMinutes(){
        int total = 0;

        if (allocationList!=null) {
            Iterator<ResourceTaskAllocTO> i = allocationList.values().iterator();
            while(i.hasNext()){
                ResourceTaskAllocTO rtato = i.next();
                if (rtato!=null) {
                    total += rtato.getAllocTime().intValue();
                }
            }
        }
        
        return (new Integer(total));        
    }

    
    public String getSlotInHtml() {
    	String response = "";
    	try {
        	Locale loc = this.getCurrentUser().getLocale();
        	String mask = this.getCurrentUser().getCalendarMask();
        	Timestamp firstDate = DateUtil.getDateTime(this.getFirstAllocSlot(), mask, loc);
        	
        	if (this.getAllocationList()!=null && firstDate!=null) {
        		response = "";
        		
        		String altValue = "";
        		if (this.getCurrentUser().getBundle()!=null) {
        			altValue = this.getCurrentUser().getBundle().getMessage("label.resTaskForm.grid.refresh");	
        		}
        		
                int upperbound = this.getAllocationList().size();
            	
                for (int i=0; i<upperbound; i++ ){
                	Timestamp cursor = DateUtil.getChangedDate(firstDate, Calendar.DATE, i);
                    ResourceTaskAllocTO alloc = (ResourceTaskAllocTO)this.getAllocationList().get(this.getAllocKey(cursor));
                            		    
                    if (alloc!=null && alloc.getAllocTime()!=null && alloc.getAllocTime().intValue()>0){
                        String value = "";
                        if (this.isDecimalInput) {
                        	value = alloc.getAllocTimeInHours(loc);
                        } else {
                        	value = alloc.getAllocTimeInTimeFormat(loc);
                        }
                        
                        int slots = DateUtil.getSlotBetweenDates(firstDate, cursor) + 1;
                    	
                    	response = response + "<tr class=\"pagingFormBody\"><td class=\"formTitle\"><center>" +
                    				DateUtil.getDate(cursor, mask, loc) + 
                    				"</center></td><td align=\"center\" class=\"formTitle\"><center>" + value + "</center></td>" +
                    						"<td><a href=\"javascript:jumpToSlot(" + slots + ")\">" +
                    						"<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/arrowrfs.gif\" /></a></td>" +
                    						"</tr>";	
                    }
                }
        	}    		
    	} catch (Exception e) {
    		e.printStackTrace(); 
    	}
        return response;
    }

    
	public String getCommentLabel() {
		String label = "";
		
		//set default value...
		if (super.getCurrentUser()!=null && super.getCurrentUser().getBundle()!=null && super.getCurrentUser().getLocale()!=null) {
			label = super.getCurrentUser().getBundle().getMessage(super.getCurrentUser().getLocale(), "label.resTaskForm.comment");
		}
		
		if (this.getCategoryId()!=null && !this.getCategoryId().equals("0")) {
			CategoryDelegate cdel = new CategoryDelegate();
			CategoryTO cto = new CategoryTO(this.getCategoryId());
			try {
				cto = cdel.getCategory(cto);
				if (cto.getIsTesting()!=null && cto.getIsTesting().booleanValue() && super.getCurrentUser().getLocale()!=null) {
					label = super.getCurrentUser().getBundle().getMessage(super.getCurrentUser().getLocale(), 
							"label.resTaskForm.comment.test");	
				}				
			} catch (BusinessException e) {
				cto = null;
			}
		}
		
		return label;
	}

	////////////////////////////////////////////////    
    public String getParentTaskId() {
        return parentTaskId;
    }
    public void setParentTaskId(String newValue) {
        this.parentTaskId = newValue;
    }
	
	////////////////////////////////////////
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }
    
	////////////////////////////////////////    
    public String getEstimDate() {
        return estimDate;
    }
    public void setEstimDate(String newValue) {
        this.estimDate = newValue;
    }
    
	////////////////////////////////////////    
    public String getEstimTime() {
        return estimTime;
    }
    public void setEstimTime(String newValue) {
        this.estimTime = newValue;
    }
    
	////////////////////////////////////////    
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
    
	////////////////////////////////////////    
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String newValue) {
        this.resourceId = newValue;
    }
    
	////////////////////////////////////////    
    public String getTaskDesc() {
        return taskDesc;
    }
    public void setTaskDesc(String newValue) {
        this.taskDesc = newValue;
    }
    
	////////////////////////////////////////    
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String newValue) {
        this.taskId = newValue;
    }
    
	////////////////////////////////////////    
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String newValue) {
        this.taskName = newValue;
    }
    
	////////////////////////////////////////    
    public Integer getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(Integer newValue) {
        this.taskStatus = newValue;
    }

	////////////////////////////////////////
    public String getActualDateFormat() {
        String response = " --/--/--";
	    boolean isOpenTask = (this.getTaskStatus()!=null && this.getTaskStatus().equals(TaskStatusTO.STATE_MACHINE_OPEN));
		if (!isOpenTask){
		    response = actualDate; 
		}        
        return response;
    }
    public String getActualDate() {
        return actualDate;
    }
    public void setActualDate(String newValue) {
        this.actualDate = newValue;
    }
    
    
	////////////////////////////////////////    
	public String getBillableStatus() {
		return billableStatus;
	}
	public void setBillableStatus(String newValue) {
		this.billableStatus = newValue;
	}
	

	////////////////////////////////////////    
    public String getActualTime() {
        return actualTime;
    }
    public void setActualTime(String newValue) {
        this.actualTime = newValue;
    }

    ////////////////////////////////////////////    
    public String getHideClosedTasks() {
        return hideClosedTasks;
    }
    public void setHideClosedTasks(String newValue) {
        this.hideClosedTasks = newValue;
    }

    ////////////////////////////////////////////       
    public String getReqCustomerName() {
        return reqCustomerName;
    }
    public void setReqCustomerName(String newValue) {
        this.reqCustomerName = newValue;
    }
    
    ////////////////////////////////////////////   
    public Boolean getQuestionAnswer() {
		return questionAnswer;
	}
	public void setQuestionAnswer(Boolean newValue) {
		this.questionAnswer = newValue;
	}

    ////////////////////////////////////////////   	
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String newValue) {
		this.questionText = newValue;
	}

	////////////////////////////////////////////    
    public int getAllocCursor() {
        return allocCursor;
    }
    public void setAllocCursor(int newValue) {
        this.allocCursor = newValue;
    }

	////////////////////////////////////////////        
	public int getStepCursor() {
		return stepCursor;
	}
	public void setStepCursor(int newValue) {
		this.stepCursor = newValue;
	}

	////////////////////////////////////////////    
    public String getShowAllocatedDays() {
        return showAllocatedDays;
	}
	public void setShowAllocatedDays(String newValue) {
		this.showAllocatedDays = newValue;
	}
    
	
	////////////////////////////////////////////    
    public String getShowDecisionQuestion() {
		return showDecisionQuestion;
	}
	public void setShowDecisionQuestion(String newValue) {
		this.showDecisionQuestion = newValue;
	}

	
	////////////////////////////////////////////
    public String getFirstAllocSlot() {
        return firstAllocSlot;
    }
    public void setFirstAllocSlot(String newValue) {
        this.firstAllocSlot = newValue;
    }
    
    
    ////////////////////////////////////////////    
    public String getTitleDateMask() {
        return titleDateMask;
    }
    public void setTitleDateMask(String newValue) {
        this.titleDateMask = newValue;
    }
    
    
    ////////////////////////////////////////////       
    public HashMap<String,ResourceTaskAllocTO> getAllocationList() {
        return allocationList;
    }

    public HashMap<String,ResourceTaskAllocTO> getEstimAllocList() {
        return estimatedAllocList;
    }

    
    ////////////////////////////////////////////      
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }
    
     
    ////////////////////////////////////////////    
    public String getIsCurrentTaskCreator() {
    	if (this.getIsAdHocTask().equals("on")) {
    		isCurrentTaskCreator = "on";
    	}
    	return isCurrentTaskCreator;
	}
	public void setIsCurrentTaskCreator(String newValue) {
		this.isCurrentTaskCreator = newValue;
	}


    ////////////////////////////////////////////	
	public void setStrDecimalInput(String newValue) {
		this.isDecimalInput = (newValue!=null && newValue.equals("on"));
	}
	public String getStrDecimalInput() {
		return (isDecimalInput?"on":"off");
	}
	
	
    ////////////////////////////////////////////	
	public void setDecimalInput(boolean newValue) {
		this.isDecimalInput = newValue;
	}
	public boolean isDecimalInput() {
		return isDecimalInput;
	}

	
    ////////////////////////////////////////////	
    public String getIterationId() {
		return iterationId;
	}
	public void setIterationId(String newValue) {
		this.iterationId = newValue;
	}

	
	////////////////////////////////////////////		
	public String getReportTaskURL() {
		return reportTaskURL;
	}
	public void setReportTaskURL(String newValue) {
		this.setShowCloseMacroTaskConfirm("off");
		this.reportTaskURL = newValue;
	}

	
    ////////////////////////////////////////////	
	public String getShowCloseMacroTaskConfirm() {
		return showCloseMacroTaskConfirm;
	}
	public void setShowCloseMacroTaskConfirm(String newValue) {
		this.showCloseMacroTaskConfirm = newValue;
	}
	

    ////////////////////////////////////////////		
	public String getShowWorkflowDiagram() {
		return showWorkflowDiagram;
	}
	public void setShowWorkflowDiagram(String newValue) {
		this.showWorkflowDiagram = newValue;
	}

	
    ////////////////////////////////////////////	
    public String getAllowBillable() {
		return allowBillable;
	}
	public void setAllowBillable(String allowBillable) {
		this.allowBillable = allowBillable;
	}
	

	////////////////////////////////////////////	
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}

	
    ////////////////////////////////////////////	
	public void setDateMask(String newValue) {
		this.dateMask = newValue;
	}

	
    ////////////////////////////////////////////		
	public void setDefaultLocale(Locale newValue) {
		this.defaultLocale = newValue;
	}

	///////////////////////////////////////////////    
    public String getSlotTitle1() {
        return getTitleForSlot(0);
    }
    public String getSlotTitle2() {
        return getTitleForSlot(1);
    }
    public String getSlotTitle3() {
        return getTitleForSlot(2);
    }
    public String getSlotTitle4() {
        return getTitleForSlot(3);
    }
    public String getSlotTitle5() {
        return getTitleForSlot(4);
    }
    public String getSlotTitle6() {
        return getTitleForSlot(5);
    }
    public String getSlotTitle7() {
        return getTitleForSlot(6);
    }

}
