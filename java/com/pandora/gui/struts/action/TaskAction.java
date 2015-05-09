package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RootTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.InvalidRequirementException;
import com.pandora.exception.ProjectTasksDiffRequirementException;
import com.pandora.exception.TasksDiffRequirementException;
import com.pandora.exception.ZeroCapacityException;
import com.pandora.gui.struts.exception.InputGuiException;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.TaskForm;
import com.pandora.gui.struts.form.UserForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;


/**
 * This class handle the actions performed into Manage Task form
 */
public class TaskAction extends GeneralStrutsAction {

	/**
	 * Shows the Manage Task form
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){

	    String forward = "showTask";
		try {
		    //clear current form
		    TaskForm tfrm = (TaskForm)form;
		    tfrm.setSaveMethod(TaskForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
		    
		    //the caller form could request to clear requirement id field... 
		    if (tfrm.getRequirementId().equals("-1")){
		        tfrm.setRequirementId(null);
		    }
		    
		    this.clearForm(form, request);
		    
		    refreshAuxiliaryList(tfrm, request);
		    
			//get all tasks from data base (based on requirement id) and put into http session (to be displayed by grid)
			this.refresh(mapping, form, request, response);
			
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    String frmtInput = uto.getPreference().getPreference(PreferenceTO.INPUT_TASK_FORMAT);
		    tfrm.setDecimalInput(!frmtInput.equals("2"));
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showTaskForm", e);
		}

		return mapping.findForward(forward);
	}

	/**
	 * Load data of category list and resource available 
	 */
	private void refreshAuxiliaryList(TaskForm tfrm,  HttpServletRequest request) throws BusinessException{
	    tfrm.setUserLocale(SessionUtil.getCurrentLocale(request));
	    
	    //get all resources from data base (filter by project) and put into http session (to be displayed by combo)
	    UserDelegate udel = new UserDelegate();
	    Vector<ResourceTO>  reslist = udel.getResourceByProject(tfrm.getProjectId(), false, true);
	    
	    UserTO root = udel.getRoot();
	    ResourceTO rtoRoot = new ResourceTO(root.getId());
	    rtoRoot.setName(super.getBundleMessage(request, "label.manageTask.anyRes"));
	    reslist.add(0, rtoRoot);
	    
	    request.getSession().setAttribute("resourceAvailable", reslist);

	    //get all categories from data base and put into http session (to be displayed by combo)
	    ProjectTO pto = new ProjectTO(tfrm.getProjectId());	    
	    CategoryDelegate cdel = new CategoryDelegate();
	    Vector<CategoryTO> catlist = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, pto, false);
	    request.getSession().setAttribute("categoryList", catlist);	    
	}

	
	/**
	 * Get all tasks from data base (based on requirement id) and put into 
	 * http session (to be displayed by grid).
	 */
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showTask";
		try {
		    request.getSession().removeAttribute("manageTaskList");
		    TaskForm frm = (TaskForm)form;
	        frm.setShowCloseReqConfirmation("off");
		    frm.setSaveMethod(frm.getSaveMethod(), SessionUtil.getCurrentUser(request));
		    this.refreshList(request, form);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showTaskForm", e);
		}
		return mapping.findForward(forward);	    
	}

	
	/**
	 * Clear the form and refresh the list of tasks related with requirement. 
	 */
	public ActionForward refreshRequirementAfterSearch(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showTask";
	    try {
	        TaskForm frm = (TaskForm)form;
	        frm.setShowCloseReqConfirmation("off");
	        this.getRelatedRequirement(frm, request);
	    } catch(BusinessException e){
	        this.setErrorFormSession(request, "error.showTaskForm", e);
	    }

		return mapping.findForward(forward);	   
	}
	

	/**
	 * This method prepare the form for updating a task. This method get the 
	 * information of specific task from data base and put the data into the
	 * html fields.
	 */
	public ActionForward editTask(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){

	    String forward = "showTask";
	    try {
		    //clear current message of form
		    this.clearMessages(request);
			TaskForm frm = (TaskForm)form;

			frm.setShowCloseReqConfirmation("off");
	        frm.setSaveMethod(UserForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
			
			//verify if a external form sent a task id to edition
			if (frm.getTaskIdFromExternalForm()!=null && !frm.getTaskIdFromExternalForm().equals("")){
			    frm.setId(new String(frm.getTaskIdFromExternalForm()));
			    frm.setTaskIdFromExternalForm(null);
			    request.getSession().removeAttribute("manageTaskList");
			}
		    this.refreshAuxiliaryList(frm, request);
		    
		    //get data of task from data base	        
	        TaskDelegate tdel = new TaskDelegate();
	        TaskTO tto = new TaskTO(frm.getId());
	        tto = tdel.getTaskObject(tto);
	        
	        //set data of task into html fields
	        this.getActionFormFromTransferObject(tto, frm, request);
	        
			//populate the task grid 
		    this.refreshList(request, form);
		    
		    //set the form to be able to show the warning msgBox (if necessary)
		    if (tto.hasResourceTaskAlreadyStarted()){
		        frm.setShowWarnPopup("on");    
		    } else {
		        frm.setShowWarnPopup("off");
		    }

	    } catch(BusinessException e){
	        this.setErrorFormSession(request, "error.showTaskForm", e);
	    }

	    return mapping.findForward(forward);
	}

	
	public ActionForward saveTask(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		TaskForm frm = (TaskForm)form;
		if (this.checkRemainingTasks(frm, request)) {
			frm.setShowCloseReqConfirmation("on");	
			return mapping.findForward("showTask");
		} else {
			frm.setShowCloseReqConfirmation("off");
			frm.setCloseRequirement("off");
			return saveTaskNoVerification(mapping, form, request, response);			
		}
	}	
	

	/**
	 * This method is performed after Save button click event.<br>
	 * Is used to update or insert data of task into data base.
	 */
	public ActionForward saveTaskNoVerification(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){

	    String forward = "showTask";   
		String errorMsg = "error.updateTaskForm";
		String succeMsg = "message.updateTask";

		try {
			TaskForm frm = (TaskForm)form;
			TaskDelegate tdel = new TaskDelegate();

			if (frm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){			    
			    errorMsg = "error.insertTaskForm";
			    succeMsg = "message.insertTask";
			}
			
			//create a Task object based on html fields
			TaskTO tto = this.getTransferObjectFromActionForm(frm, request);

			//If the current task is the parent task of itselft...
			if (tto.getParentTask()!=null && tto.getParentTask().getId().equals(tto.getId())){
			    throw new BusinessException("Cyclic relationship error. A Task cannot be the parent task of itself."); 
			}

			if (frm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){			    
			    tto.setCreationDate(DateUtil.getNow());
			    tto.setCreatedBy(SessionUtil.getCurrentUser(request));
			    tdel.insertTask(tto);
			    this.clearForm(form, request);
			} else {
				String confirm = frm.getCloseRequirement();
			    tdel.updateTask(tto, (confirm!=null && confirm.equalsIgnoreCase("on")));
			    
			    //refresh allocated resources...
			    this.createResourceAllocList(tto.getAllocResources(), request);
			}

			//set success message into http session
			this.setSuccessFormSession(request, succeMsg);
	        frm.setShowCloseReqConfirmation("off");
	        
			//get all projects from data base and put into http session (to be displayed by grid)
			request.getSession().removeAttribute("manageTaskList");			
			this.refreshList(request, form);

		} catch(ZeroCapacityException e){
			this.setErrorFormSession(request, "error.manageTask.zeroCapacity", e);
		} catch(InputGuiException e){
		    this.setErrorFormSession(request, "error.manageTask.invalidAllocResData", e);
		} catch(ProjectTasksDiffRequirementException e){
		    this.setErrorFormSession(request, "error.manageTask.diffPrjReq", e);
		} catch(TasksDiffRequirementException e){
		    this.setErrorFormSession(request, "error.manageTask.diffReq", e);
		} catch(InvalidRequirementException e){
		    this.setErrorFormSession(request, "error.manageTask.invalidReq", e);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}
		return mapping.findForward(forward);		
	}	

	
	/**
	 * This method set the current task status to 'cancel'. 
	 */
	/*public ActionForward cancelTask(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){

	    String forward = "showTask";
	    try {
		    //clear current message of form
		    this.clearMessages(request);
			TaskForm frm = (TaskForm)form;
			frm.setShowCloseReqConfirmation("off");

			//create a Task object based on html fields
			TaskTO tto = this.getTransferObjectFromActionForm(frm, request);

			//set the current task status to 'cancel'
			TaskDelegate tdel = new TaskDelegate();
			tdel.cancelTask(tto);

            //get all projects from data base and put into http session (to be displayed by grid)
            this.refreshList(request, form);
			
		} catch(InputGuiException e){
		    this.setErrorFormSession(request, "error.manageTask.invalidAllocResData", e);			
	    } catch(BusinessException e){
	        this.setErrorFormSession(request, "error.cancelTaskForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}
	*/

	/**
	 * Clear all values of current form.
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showTask";	
	    this.clearForm(form, request);
	    this.clearMessages(request);	    
		return mapping.findForward(forward);	    
	}

	
	/**
	 * Clear all values of current form.
	 */
	public ActionForward clearSearch(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showTask";
	    TaskForm tfrm = (TaskForm)form;
        tfrm.setShowCloseReqConfirmation("off");
	    tfrm.setRequirementId("-1");
	    return mapping.findForward(forward);	    
	}

	
	public ActionForward addResource(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showTask";
	    try {
	     
		    TaskForm frm = (TaskForm)form;
	        frm.setShowCloseReqConfirmation("off");
	        
		    Locale loc = SessionUtil.getCurrentLocale(request);

		    //include a new resource task into the list
		    ResourceTaskTO rtto = getResTaskFromVector(request, frm, loc);
	        Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");
	        
	        boolean notExists = true;
		    Iterator i = resList.iterator();
		    while(i.hasNext()){
		        ResourceTaskTO buff = (ResourceTaskTO)i.next();
		        if (buff.getResource().getId().equals(rtto.getResource().getId())) {
		            this.setErrorFormSession(request, "message.resourceIntoListExists", null);
		            notExists = false;
		            break;
		        }
		    }
	        
		    if (notExists) {
		        resList.addElement(rtto);
		        request.getSession().setAttribute("resourceAllocated", resList);		            		        
		    }

		    //check again
	        resList = (Vector)request.getSession().getAttribute("resourceAllocated");
	        if (resList!=null && resList.size()>0) {
	            frm.setShowAllocField("off");
	        }
	        
		} catch(Exception e){
	        this.setErrorFormSession(request, "error.cancelTaskForm", e);
		}	    
	    return mapping.findForward(forward);	
	}
	
	
	public ActionForward removeResource(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showTask";
	    try {
		    TaskForm frm = (TaskForm)form;
	        frm.setShowCloseReqConfirmation("off");	
		    Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");
		    
		    boolean alreadyExists = false;
		    Iterator i = resList.iterator();
		    while(i.hasNext()){
		        ResourceTaskTO rtto = (ResourceTaskTO)i.next();
		        if (rtto.getResource().getId().equals(frm.getResourceId())) {
			        Integer state = null;
			        if (rtto.getTaskStatus()!=null) {
			            state = rtto.getTaskStatus().getStateMachineOrder();    
			        }
			        if (state!=null && (state.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) || 
			                state.equals(TaskStatusTO.STATE_MACHINE_CLOSE) ||
			                state.equals(TaskStatusTO.STATE_MACHINE_CANCEL) ||
			                state.equals(TaskStatusTO.STATE_MACHINE_HOLD))) {
			            this.setErrorFormSession(request, "message.resourceCannotBeRemoved", null);
			            alreadyExists = true;
			        }
		        }		            
		    }
		    
        	if (!alreadyExists) {
    		    i = resList.iterator();
    		    while(i.hasNext()){
    		        ResourceTaskTO rtto = (ResourceTaskTO)i.next();
    		        if (rtto.getResource().getId().equals(frm.getResourceId())) {
    	        	    resList.remove(rtto);
    	        	    break;
    		        }
    		    }
    		    request.getSession().setAttribute("resourceAllocated", resList);    		    
        	}
		    
		} catch(Exception e){
	        this.setErrorFormSession(request, "error.cancelTaskForm", e);
		}	    
	    return mapping.findForward(forward);	
	}

	
	
	public ActionForward showCloseReqConfirmation(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showCloseReqConfirmation";
	    return mapping.findForward(forward);
	}
	 
	
	/**
	 * Clear all values of current form.
	 */
	private void clearForm(ActionForm form, HttpServletRequest request){
	    TaskForm tfrm = (TaskForm)form;
        tfrm.setShowCloseReqConfirmation("off");
	    Locale loc = SessionUtil.getCurrentLocale(request);
	    
	    tfrm.setUserLocale(loc);
	    tfrm.clear();
	    tfrm.setInitDate(DateUtil.getDate(DateUtil.getNow(), super.getCalendarMask(request), loc));
	    
		//set current operation status for Insertion
	    tfrm.setSaveMethod(TaskForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
	    
		//set empty to lists 
	    request.getSession().setAttribute("resourceAllocated", new Vector());
	}

	
	/**
	 * Refresh grid with list of all tasks from data base.
	 */
	private void refreshList(HttpServletRequest request, ActionForm form) throws BusinessException{
		TaskDelegate tdel = new TaskDelegate();
		RequirementTO rto = null;
		
		TaskForm tfrm = (TaskForm)form;
		rto = this.getRelatedRequirement(tfrm, request);
		
		//get all task based on current requirement
	    Vector taskList = (Vector)request.getSession().getAttribute("manageTaskList");
	    if (taskList==null || taskList.size()==0) {
		    taskList = tdel.getTaskListByRequirement(rto, new ProjectTO(tfrm.getProjectId()), !tfrm.getBoolCloseTasks());
			request.getSession().setAttribute("manageTaskList", taskList);		        
	    }
		
		//get all tasks from data base (filter by requirement) and put into http session (to be displayed by combo) 
	    Vector parentList = tdel.getAvailableParentTaskList(rto, tfrm.getProjectId());
	    request.getSession().setAttribute("taskAvailable", parentList);
	}
	
	
	private RequirementTO getRelatedRequirement(TaskForm tfrm, HttpServletRequest request) throws BusinessException{
	    RequirementTO rto = null;
		RequirementDelegate rdel = new RequirementDelegate();
		
		if (tfrm.getRequirementId()!=null && !tfrm.getRequirementId().equals("")){
		    
		    //get a Requirement from data base based on html field requirementId 
		    rto = new RequirementTO(tfrm.getRequirementId());
		    rto = rdel.getRequirement(rto);
		    
			//get current locale of connected user 
			Locale loc = SessionUtil.getCurrentLocale(request);
			
		    //set requirement information into html fields
			tfrm.setRequestNum(rto.getId());
			tfrm.setRequirementId(rto.getId());
			tfrm.setRequestDesc(rto.getDescription());
			tfrm.setRequestCustomer(rto.getRequester().getName());
			if (rto.getSuggestedDate()!=null){
			    tfrm.setRequestSuggDate(DateUtil.getDate(rto.getSuggestedDate(), super.getCalendarMask(request), loc));    
			} else {
			    tfrm.setRequestSuggDate("");
			}
		}
		
		return rto;
	}
	
	
	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 */
	private void getActionFormFromTransferObject(TaskTO to, TaskForm frm, HttpServletRequest request){
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();

	    Locale loc = SessionUtil.getCurrentLocale(request);	    
	    frm.setDescription(to.getDescription());
	    frm.setName(to.getName());
	    frm.setId(to.getId());
	    frm.setCategoryId(to.getCategory().getId());
	    frm.setProjectId(to.getProject().getId());
	    if (to.getRequirement()!=null) {
		    frm.setRequirementId(to.getRequirementId());
		    frm.setRequestCustomer(to.getRequirement().getRequester().getName());
		    frm.setRequestDesc(to.getRequirement().getDescription());
		    frm.setRequestNum(to.getRequirementId());
		    
		    if (to.getRequirement().getSuggestedDate()!=null){
		        frm.setRequestSuggDate(DateUtil.getDate(to.getRequirement().getSuggestedDate(), super.getCalendarMask(request), loc));    
		    } else {
		        frm.setRequestSuggDate("");
		    }		    
	    } else {
	        frm.setRequirementId("");
	        frm.setRequestCustomer("");
	        frm.setRequestDesc("");
	        frm.setRequestSuggDate("");
	        frm.setRequestNum("");
	    }
	    
	    Integer status = rtdel.getStatusOfAllocResource(to);
	    if (status!=null) {
	        frm.setTaskStatus(status.toString());    
	    }
	    frm.setCreationDate(to.getCreationDate());
	    frm.setFinalDate(to.getFinalDate());
	    
	    if (to.getParentTask()!=null){
	        frm.setParentTaskId(to.getParentTask().getId());   
	    }else {
	        frm.setParentTaskId("-1");
	    }
	    
	    if (to.isParentTask()) {
	        frm.setIsParentTask("on");
	        frm.setIsParentTaskCheckbox(true);
	    } else {
	        frm.setIsParentTask("");
	        frm.setIsParentTaskCheckbox(false);
	    }
	    
	    frm.setCreatedBy(to.getCreatedBy());
	    
	    //put the data of resource task using format: resource_Id|start_Date(using appropriate locale)|estimate_time
	    Vector<ResourceTaskTO> allocRes = to.getAllocResources();
	    this.createResourceAllocList(allocRes, request);
	    
	    //set the relationship of task
	    request.getSession().setAttribute("taskRelationshipList", to.getRelationList());
	    
        if (allocRes!=null && allocRes.size()>0) {
            frm.setShowAllocField("off");
        }
	    
	}


	/**
	 * Put the data of resource task using format: resource_Id|start_Date(using appropriate locale)|estimate_time
	 */
	private void createResourceAllocList(Vector<ResourceTaskTO> allocRes, HttpServletRequest request){
	    Locale loc = SessionUtil.getCurrentLocale(request);	    
	    if (allocRes!=null){
	        Iterator<ResourceTaskTO> i = allocRes.iterator();
	        while (i.hasNext()){
		        ResourceTaskTO rtto = i.next();
		        String name = rtto.getResource().getName();
		        if (rtto.getResource().getUsername().equals(RootTO.ROOT_USER)) {
		        	name = super.getBundleMessage(request, "label.manageTask.anyRes");	
		        }
		        
		        //check if current resource Task is already started (inProgress)
		        String status = "";
		        Integer state = rtto.getTaskStatus().getStateMachineOrder();
		        if (state.equals(TaskStatusTO.STATE_MACHINE_PROGRESS)){
		            status = this.getResources(request).getMessage(loc, "label.manageTask.alreadyStarted");
		        } else if (state.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
		            status = this.getResources(request).getMessage(loc, "label.manageTask.alreadyClosed");
		        } else if (state.equals(TaskStatusTO.STATE_MACHINE_CANCEL)){
		            status = this.getResources(request).getMessage(loc, "label.manageTask.alreadyCanceled");
		        } else if (state.equals(TaskStatusTO.STATE_MACHINE_HOLD)){
		            status = this.getResources(request).getMessage(loc, "label.manageTask.alreadyHold");
		        } else {
		            status = "";
		        }
		        
		        String label = name + " " + status;
		        rtto.setLabel(label);
	        }  
		    request.getSession().setAttribute("resourceAllocated", allocRes);		    
	    }	    
	}
	

	/**
	 * Put data of html fields into TransferObject 
	 */
	private TaskTO getTransferObjectFromActionForm(TaskForm frm, HttpServletRequest request) throws InputGuiException, BusinessException{
	    TaskTO tto = new TaskTO();
	    TaskDelegate tdel = new TaskDelegate();
	    
	    Locale loc = SessionUtil.getCurrentLocale(request);	    
	    
	    tto = tdel.getTaskObject(new TaskTO(frm.getId()));
	    if (tto==null) {
	    	tto = new TaskTO();
	    	tto.setId(frm.getId());	
	    }
	    
	    //link current user with task object
	    tto.setHandler(SessionUtil.getCurrentUser(request));
	    
	    tto.setName(frm.getName());
	    tto.setDescription(frm.getDescription());	    
	    tto.setCategory(new CategoryTO(frm.getCategoryId()));	    
	    tto.setFinalDate(null);
	    if (frm.getRequirementId()!=null && !frm.getRequirementId().equals("")) {
	        tto.setRequirement(new RequirementTO(frm.getRequirementId()));    
	    } else {
	        tto.setRequirement(null);
	    }
	    tto.setProject(new ProjectTO(frm.getProjectId()));

	    tto.setAllocResources(this.getAllocatedResources(frm, tto, loc, request));
	    
	    if (frm.getIsParentTaskCheckbox()){
	        tto.setIsParentTask(new Integer(1));
	    } else {
	        tto.setIsParentTask(new Integer(0));
	    }
	    
	    if (frm.getParentTaskId().equals("-1")) {
	        tto.setParentTask(null);    
	    } else {
	        tto.setParentTask(new TaskTO(frm.getParentTaskId()));
	    }
	    
	    tto.setCreationDate(frm.getCreationDate());
	    tto.setFinalDate(frm.getFinalDate());
	    tto.setCreatedBy(frm.getCreatedBy());
	    
	    return tto;
	}
		
	/**
	 * Get list of resource allocated from http form and create a vector of resource task objects.
	 */
	private Vector<ResourceTaskTO> getAllocatedResources(TaskForm frm, TaskTO tto, Locale loc, HttpServletRequest request) throws InputGuiException{
	    Vector<ResourceTaskTO> response = new Vector<ResourceTaskTO>();
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
	    
	    try {
	        Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");		    
		    if (resList!=null && resList.size()>0) {
			    Iterator i = resList.iterator();
			    while(i.hasNext()) {
			        ResourceTaskTO buff = (ResourceTaskTO)i.next();
			        ResourceTaskTO rtto = new ResourceTaskTO();
			        
			        //refresh attributes...
			        if (tto.getId()!=null && !tto.getId().trim().equals("") && buff.getTaskStatus()!=null){
			            rtto = rtdel.getResourceTaskObject(buff);
			            rtto.setTask(tto);
			            
			            rtto.setResource(buff.getResource());
			            rtto.setStartDate(buff.getStartDate());
			            rtto.setEstimatedTime(buff.getEstimatedTime());
			            rtto.setHandler(tto.getHandler());
			            
			            response.addElement(rtto);
			        } else {
			            buff.setHandler(tto.getHandler());
			            response.addElement(buff);
			        }
			    }
		    }	        

	    } catch(Exception e){
	        throw new InputGuiException(e);
	    }
	    return response;
	}
	

	private ResourceTaskTO getResTaskFromVector(HttpServletRequest request, TaskForm frm, Locale loc) throws BusinessException{
        ResourceTaskTO rtto = new ResourceTaskTO();
        UserDelegate udel = new UserDelegate(); 
        UserTO root = udel.getRoot();   
        
        String resourceId = frm.getResourceId();
        String initialDate = frm.getInitDate();
        String estimTime = frm.getEstimatedTime();

        //get the resource object from database
        ResourceTO rto = new ResourceTO(resourceId);
        ProjectTO pto = new ProjectTO(frm.getProjectId());
        rto.setProject(pto);
        rto = udel.getResource(rto);

    	if (root.getId().equals(resourceId)) {
    		
        	//If the root is not part of project, insert it into customer and resource tables
        	//This is used when the leader is trying to allocate a resource 'Any' at Allocation Form     		
            if (rto==null) {
        		udel.attribRootIntoProjectResource(root, pto);
                rto = new ResourceTO(resourceId);
                rto.setProject(pto);
                rto = udel.getResource(rto);
            }
    	    rto.setName(super.getBundleMessage(request, "label.manageTask.anyRes"));            
    	}

        TaskTO tto = new TaskTO(frm.getId());
        tto.setProject(rto.getProject());
        
        TaskStatusTO tsto = new TaskStatusTO();
        tsto.setStateMachineOrder(TaskStatusTO.STATE_MACHINE_OPEN);
        rtto.setTaskStatus(tsto);
        
        rtto.setResource(rto);    
        rtto.setTask(tto);
        rtto.setLabel(rto.getName());
        
        //update new estimated date and start date
	    if (frm.isDecimalInput()) {
	    	rtto.setEstimatedTime(new Integer((int)(StringUtil.getStringToFloat(estimTime, loc) * 60)));
	    } else {
	    	rtto.setEstimatedTime(new Integer(StringUtil.getHHMMToInteger(estimTime, false)));		    	
	    }        
        
        rtto.setStartDate(DateUtil.getDateTime(initialDate, super.getCalendarMask(request), loc));
        
        return rtto;
	}

	
	/**
	 * Check if all tasks of the same requirement will be closed after saving...
	 */
	private boolean checkRemainingTasks(TaskForm frm, HttpServletRequest request){
		boolean showConfirmation = false;
		TaskDelegate tdel = new TaskDelegate();
		RequirementDelegate rdel = new RequirementDelegate();
		
		try {

			if (frm.getRequirementId()!=null && !frm.getRequirementId().equals("")) {
				RequirementTO rto = rdel.getRequirement(new RequirementTO(frm.getRequirementId()));
				if (!rto.getRequirementStatus().isFinished()) {
					
					Vector<TaskTO> list = tdel.getTaskListByRequirement(rto, new ProjectTO(frm.getProjectId()), false);
					if (list!=null) {
						Iterator<TaskTO> i = list.iterator();
						while(i.hasNext()) {
							TaskTO t = i.next();
							if (t.isFinished() && !t.getId().equals(frm.getId())) {
								showConfirmation = true;
							} else {
								showConfirmation = false;
								break;
							}
						}
						
						if (showConfirmation) {
							Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");
							Iterator<ResourceTaskTO> j = resList.iterator();
							while(j.hasNext()) {
								ResourceTaskTO rt = j.next();
								if (rt.getTaskStatus().isFinish()) {
									showConfirmation = true;
								} else {
									showConfirmation = false;
									break;								
								}
							}							
						}
					}						
				}
			}
			
	    } catch(Exception e){
	        this.setErrorFormSession(request, "error.showTaskForm", e);
	    }
				
		return showConfirmation;
		
	}
}
