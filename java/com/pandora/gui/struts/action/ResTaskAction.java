package com.pandora.gui.struts.action;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.AttachmentTO;
import com.pandora.CategoryTO;
import com.pandora.CustomerTO;
import com.pandora.DiscussionTopicTO;
import com.pandora.MetaFieldTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ReportTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceDateAllocTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.DiscussionTopicDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.ResourceTaskAllocDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.TaskStatusDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.InvalidTaskStateTransitionException;
import com.pandora.exception.InvalidTaskTimeException;
import com.pandora.exception.MandatoryMetaFieldBusinessException;
import com.pandora.exception.MetaFieldNumericTypeException;
import com.pandora.exception.ProjectTasksDiffRequirementException;
import com.pandora.exception.TasksDiffRequirementException;
import com.pandora.exception.WaitPredecessorUpdateException;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.ResTaskForm;
import com.pandora.gui.taglib.decorator.HideProjectDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntityCheckBoxDecorator;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * This class handle the actions performed into Resource Task form
 */
public class ResTaskAction extends GeneralStrutsAction {
    
	private ResourceTaskDelegate rtDeleg = new ResourceTaskDelegate();
    
	/**
	 * Shows the Manage Resource Task form
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
		try {
		    //clear current form
		    ResTaskForm trfrm = (ResTaskForm)form;
		    trfrm.clear();
		    		    
		    //clear the attachments of the task and update the session attribute taskAttachmentList
		    request.getSession().removeAttribute("taskAttachmentList");
		    request.getSession().setAttribute("taskAttachmentList", new Vector<AttachmentTO>());
		    
		    trfrm.setSaveMethod(ResTaskForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		    trfrm.setTitleDateMask(this.getBundleMessage(request, "label.resTaskForm.mask"));
		    
		    trfrm.setDateMask(super.getCalendarMask(request));
		    trfrm.setDefaultLocale(SessionUtil.getCurrentLocale(request));
		    
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    String frmtInput = uto.getPreference().getPreference(PreferenceTO.INPUT_TASK_FORMAT);
		    trfrm.setDecimalInput(!frmtInput.equals("2"));
		    
		    //for adHoc tasks...get the current resource task	
			this.resetAdHocTask(trfrm, request);
		    
		    this.clearMessages(request);
		    this.getActionFormFromTransferObject(trfrm, request);
		    this.defineCursor(trfrm, request);
		    
			//refresh resource task
		    this.refresh(mapping, form, request, response);
		    
		    this.updateDateAllocTimeList(trfrm, null);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showResTaskForm", e);
		}

		return mapping.findForward(forward);
	}

	
	public ActionForward refreshAfterTopicDiscussion(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showResTask";
	    try {
	    	ResTaskForm trfrm = (ResTaskForm)form;
		    this.getDiscussionData(request, trfrm);
		    
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showReqForm", e);
		}
		return mapping.findForward(forward);		
	}
	
	
	public ActionForward showMTConfirmation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showCloseMacroTaskConfirm";
		this.resetForm(form);		
		return mapping.findForward(forward);		
	}

	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		this.resetForm(form);	
		return mapping.findForward("showResTask");
	}
	
	
	public ActionForward showWorkflow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showResTask");
	}
	
	
	public ActionForward grabTask(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showResTask";
		try {
			UserDelegate udel = new UserDelegate();
			ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
		    ResTaskForm trfrm = (ResTaskForm)form;
		    this.resetForm(form);
		    
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    
			String fwd = request.getParameter("fwd");
			if (fwd!=null && !fwd.trim().equals("")) {
				forward = fwd;
			}
			
			UserTO root = udel.getRoot();
			ResourceTaskTO rtto = rtdel.getResourceTaskObject(trfrm.getTaskId(), root.getId());
			if (rtto!=null && rtto.getResource()!=null) {
				rtto.setHandler(uto);
				String comment = this.getBundleMessage(request, "message.changeAssignment.comment");
				rtdel.changeAssignment(rtto, uto, comment);
				
				//refresh resource task
			    this.refresh(mapping, form, request, response);	
			    
			} else {
				this.setErrorFormSession(request, "error.assignmentError", null);	
			}
						
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showResTaskForm", e);
		}

		return mapping.findForward(forward);
	}


	private void resetAdHocTask(ResTaskForm trfrm, HttpServletRequest request) {
		if (trfrm.getIsAdHocTask().equals("on")) {
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    trfrm.setResourceId(uto.getId());
		    trfrm.setActualDate(DateUtil.getDate(DateUtil.getNow(), uto.getCalendarMask(), SessionUtil.getCurrentLocale(request)));
		}
	}


	public ActionForward refreshProject(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
	    TaskDelegate tdel = new TaskDelegate();
	    OccurrenceDelegate odel = new OccurrenceDelegate();
	    UserDelegate udel = new UserDelegate();
	    
		try {
		    ResTaskForm trfrm = (ResTaskForm)form;			
			this.resetForm(form);
		    this.loadBillableStatus(trfrm);
			ProjectTO pto = new ProjectTO(trfrm.getProjectId());

			//get all categories from data base and put into http session (to be displayed by combo)
		    CategoryDelegate cdel = new CategoryDelegate();
		    Vector<CategoryTO> catlist = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, pto, false);
		    request.getSession().setAttribute("categoryList", catlist);			
			
		    this.refreshMetaFieldList(request, trfrm);
		    this.getDiscussionData(request, trfrm);
		    
			//get macro-tasks from data base (filter by requirement) and put into http session (to be displayed by combo)
		    RequirementTO rto = null;
		    if (trfrm.getTaskId()!=null && !trfrm.getTaskId().equals("")) {
			    TaskTO tto = new TaskTO(trfrm.getTaskId());
			    tto = tdel.getTaskObject(tto);
			    if (tto!=null) {
			    	rto = tto.getRequirement();	
			    }
		    }
		    Vector<TaskTO> parentList = tdel.getAvailableParentTaskList(rto, trfrm.getProjectId());
		    request.getSession().setAttribute("taskAvailable", parentList);
		    
			Vector<OccurrenceTO> iterations = odel.getIterationListByProject(trfrm.getProjectId(), false);
		    request.getSession().setAttribute("iterationList", iterations);

		    Vector<TransferObject> billableStatusList = new Vector<TransferObject>();
		    billableStatusList.add(new TransferObject("1", super.getBundleMessage(request, "label.yes")));
		    billableStatusList.add(new TransferObject("0", super.getBundleMessage(request, "label.no")));
		    request.getSession().setAttribute("billableStatusList", billableStatusList);
		    		    
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    trfrm.setCanSeeDiscussion(udel.checkCustomerViewDiscussion(uto, pto));
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showResTaskForm", e);
		}
		
		return mapping.findForward(forward);
	}


	private void loadBillableStatus(ResTaskForm trfrm)	throws BusinessException {		
		ProjectDelegate pdel = new ProjectDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
		
		//check if the resource could change the billable status
		
		ProjectTO pto = new ProjectTO(trfrm.getProjectId());
		pto = pdel.getProjectObject(pto, true);
		if (pto!=null) {
		    if (pto.getAllowBillable()!=null){
		    	trfrm.setAllowBillable(pto.getAllowBillable().booleanValue()?"on":"off");
		    } else {
		    	trfrm.setAllowBillable("off");
		    }

			if (trfrm.getIsAdHocTask().equals("on")) {
		    	CategoryTO cto = cdel.getCategory(new CategoryTO(trfrm.getCategoryId()));
		    	if (cto.getIsBillable()!=null) {
		    		trfrm.setBillableStatus(cto.getIsBillable().booleanValue()?"1":"0");	
		    	}				
			}
		}
	}

	
	public ActionForward showTaskReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ActionForward fwd = null;
		
		try {	    
			ResTaskForm frm = (ResTaskForm)form;
			UserDelegate udel = new UserDelegate();
			UserTO root = udel.getRoot();
			String url = root.getPreference().getPreference(PreferenceTO.TASK_REPORT_URL);
			String mask = super.getCalendarMask(request);
			
			Timestamp iniRange = null;
			Locale loc = SessionUtil.getCurrentLocale(request);
			
		    if (frm.getActualDate()!=null) {
		    	iniRange = frm.getDate(false, loc);    
		    } else {
		    	iniRange = frm.getDate(true, loc);
		    }
		    
		    iniRange = DateUtil.getChangedDate(iniRange, Calendar.DATE, frm.getAllocCursor()-1);
			
			Timestamp finalRange = DateUtil.getChangedDate(iniRange, Calendar.DATE, frm.getStepCursor()-1);
			url = url.replaceAll(ReportTO.PROJECT_ID, frm.getProjectId());
			url = url.replaceAll(ReportTO.INITIAL_RANGE, DateUtil.getDate(iniRange, mask, loc));
			url = url.replaceAll(ReportTO.FINAL_RANGE, DateUtil.getDate(finalRange, mask, loc));
			
			fwd = this.refresh(mapping, form, request, response); 
			frm.setReportTaskURL(url);
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showResTaskForm", e);
		}
		return fwd;	
	}
	
	
	public ActionForward refreshCategory(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
		try {
		    ResTaskForm trfrm = (ResTaskForm)form;			
			this.resetForm(form);
			this.loadBillableStatus(trfrm);
			this.refreshMetaFieldList(request, trfrm);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showResTaskForm", e);
		}
		return mapping.findForward(forward);
	}

	
	public ActionForward jumpToSlot(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
	    ResTaskForm trfrm = (ResTaskForm)form;
	    this.resetForm(form);
	    String slot = request.getParameter("newslot");
	    trfrm.setAllocCursor(Integer.parseInt(slot));
		return mapping.findForward(forward);
	}

	
	/**
	 * Refresh list of current resource task
	 */
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
	    TaskDelegate tdel = new TaskDelegate();
	    UserDelegate udel = new UserDelegate();

	    
		try {
		    ResTaskForm trfrm = (ResTaskForm)form;
		    this.resetForm(form);
		    ResourceTO rto = new ResourceTO(trfrm.getResourceId());
		    rto.setProject(new ProjectTO(trfrm.getProjectId()));
		    rto = udel.getResource(rto);
		    
		    if (rto!=null) {

		    	//get all Tasks (of current resource) from data base and put 
			    //into http session (to be displayed by grid)		    
		    	Vector<ResourceTaskTO> taskList = rtdel.getTaskListByResource(rto, trfrm.getBoolCloseRequest());
				request.getSession().setAttribute("taskList", taskList);
		    }
			
			request.getSession().setAttribute("reqAttachList", new Vector<AttachmentTO>());			
		    if (trfrm.getTaskId()!=null && !trfrm.getTaskId().equals("")) {
			    TaskTO tto = new TaskTO(trfrm.getTaskId());
			    tto = tdel.getTaskObject(tto);
			    if (tto!=null) {
				    RequirementTO reqto = tto.getRequirement();
				    if (reqto!=null) {
				        request.getSession().setAttribute("reqAttachList", reqto.getAttachments());        
				    }
				    this.refreshArtifacts(mapping, form, request, response);
			    }
		    }

			ProjectDelegate pdel = new ProjectDelegate();
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    Vector<ProjectTO> projtemp = pdel.getProjectListForWork(uto, true, true);
		    
		    Vector<ProjectTO> projList = new Vector<ProjectTO>();

		    if (projtemp!=null && projtemp.size()>0) {
			    Iterator<ProjectTO> i = projtemp.iterator();
			    while(i.hasNext()) {
			    	ProjectTO pto = i.next();
			    	if (!HideProjectDecorator.isHideProject(uto, pto.getId())) {
			    		projList.add(pto);
			    	}
			    }
		    }

		    String content = this.getHtmlCombo(request, projList);
			trfrm.setProjectHtmlList(content);

	    	projtemp = new Vector<ProjectTO>();
	    	 //insert a dummy project into top of the list
		    ProjectTO dummy = new ProjectTO("-1");
		    dummy.setName(this.getBundleMessage(request, "validate.manageTask.selectProject"));
		    projtemp.add(dummy);
		    projtemp.addAll(projList);
		    
		    
			//get all projects (of current resource) from data base and put into http session
			request.getSession().setAttribute("projectList", projtemp);
			
			
		    if (trfrm.getIsAdHocTask().equals("on")) {
				trfrm.setProjectId("-1");
				
				//get all TasksStatus from data base and put into http session (to be displayed by combo)			
				request.getSession().setAttribute("taskStatusList", getTaskStatusList(null));

			} else {
				//get all TasksStatus from data base and put into http session (to be displayed by combo)			
				request.getSession().setAttribute("taskStatusList", getTaskStatusList(trfrm.getTaskStatus()));			    
			}			
			
			this.refreshProject(mapping, form, request, response);
						
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showResTaskForm", e);
		}

		return mapping.findForward(forward);
	}
	
	
	public ActionForward refreshArtifacts(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showResTask";
	    RepositoryDelegate rdel = new RepositoryDelegate();
	    
		try {
		    ResTaskForm trfrm = (ResTaskForm)form;		
		    if (trfrm.getTaskId()!=null && !trfrm.getTaskId().equals("")) {
		    	TaskTO tto = new TaskTO(trfrm.getTaskId());
				Vector<RepositoryFilePlanningTO> repositoryList = rdel.getFilesFromPlanning(tto);
				request.getSession().setAttribute("repositoryList", repositoryList);
				request.getSession().setAttribute(RepositoryEntityCheckBoxDecorator.REPOSITORY_ENTITY_ID, tto.getId());
		    }			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showResTaskForm", e);
		}
		return mapping.findForward(forward);
	}
	
	
	
	
	private void refreshMetaFieldList(HttpServletRequest request, ResTaskForm trfrm) throws BusinessException{
		MetaFieldDelegate mfdel = new MetaFieldDelegate();
		if (trfrm.getProjectId()!=null && !trfrm.getProjectId().equals("")) {
			Vector<MetaFieldTO> mflist = mfdel.getListByProjectAndContainer(trfrm.getProjectId(), trfrm.getCategoryId(), MetaFieldTO.APPLY_TO_TASK);
			request.getSession().setAttribute("metaFieldList", mflist);		    
		} else {
		    request.getSession().setAttribute("metaFieldList", new Vector<MetaFieldTO>());
		}		
	}
	
	
	public ActionForward saveResTaskPreProcess(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    ActionForward forward = mapping.findForward("showResTask");
	    try {
	    	this.preProcessForm(form, request);
	        forward = saveResTask(mapping, form, request, response, true);
	    } catch(MetaFieldNumericTypeException e){
	    	this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch(Exception e) {
	    	this.setErrorFormSession(request, "error.invalidTaskTransState", e);
	    }
	    return forward;
	}
	
	private String getHtmlCombo(HttpServletRequest request, Vector<ProjectTO> v) {
		String list = "<select id=\"selectedProjectId\" name=\"selectedProjectId\" class=\"textBox\">";
		list = list + "<option value=\"-1\">" + super.getBundleMessage(request, "label.combo.select") + "</option>";
		if (v!=null) {
			list = list + HtmlUtil.getComboOptions("selectedProjectId", v, "textBox", null);
		}
		list = list + "</select>";
		return list;
	}
	
	/**
	 * Save a resource task into data base. 
	 */
	private ActionForward saveResTask(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response, boolean isMTClosingAllowed){
	    String forward = "showResTask";
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
	    TaskStatusDelegate tsdel = new TaskStatusDelegate();
	    
		try {
		    ResTaskForm trfrm = (ResTaskForm)form;
		    this.clearMessages(request);
	    
		    //get the user connected
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    trfrm.setSaveMethod(ResTaskForm.UPDATE_METHOD, uto);
		    
		    //create a Resource Task object based on html fields
		    Locale loc = SessionUtil.getCurrentLocale(request);
		    ResourceTaskTO rtto = this.getTransferObjectFromActionForm(trfrm, loc, request);

		    //get data of Task Status selected from data base
		    TaskStatusTO tsto = rtto.getTaskStatus(); 
		    tsto = tsdel.getTaskStatusObject(tsto);

		    //save new status and additional information into database
		    rtto.setHandler(uto);
		    rtdel.changeTaskStatus(rtto, tsto.getStateMachineOrder(), trfrm.getComment(), 
		    		rtto.getTask().getAdditionalFields(), isMTClosingAllowed);

			//set success message into http session
			this.setSuccessFormSession(request, "message.saveResTask");
		    trfrm.clear(); //clear current alloc hash
		    this.getActionFormFromTransferObject(trfrm, request);
		    this.defineCursor(trfrm, request);

			//get all resourceStatys from data base 
			this.refresh(mapping, form, request, response);
			this.resetAdHocTask(trfrm, request);	
			
			this.updateDateAllocTimeList(trfrm, null);

		} catch(WaitPredecessorUpdateException e){
			this.setErrorFormSession(request, "error.manageTask.waitPredes", e);
		} catch(MetaFieldNumericTypeException e){
			this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch(ProjectTasksDiffRequirementException e){
		    this.setErrorFormSession(request, "error.manageTask.diffPrjReq", e);			
		} catch(TasksDiffRequirementException e){
			this.setErrorFormSession(request, "error.manageTask.diffReq", e);			
		} catch(InvalidTaskTimeException e){
			this.setErrorFormSession(request, "validate.resTaskForm.nonOpenZeroTime", e);
		} catch(InvalidTaskStateTransitionException e){
		    this.setErrorFormSession(request, "error.invalidTaskTransState", e);
		} catch(MandatoryMetaFieldBusinessException e) {
			this.setErrorFormSession(request, "errors.required", e.getAfto().getMetaField().getName(), null, null, null, null, e);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.saveResTaskForm", e);
		}

		return mapping.findForward(forward);
	}

	
	public ActionForward showHideAllocatedDays(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showResTask";
		try {
			ResTaskForm trfrm = (ResTaskForm)form;
			this.resetForm(form);
			
			if (trfrm.getShowAllocatedDays().equals("off")) {
				trfrm.setShowAllocatedDays("on");	
			} else {
				trfrm.setShowAllocatedDays("off");
			}
			
        } catch (Exception e) {
            this.setErrorFormSession(request, "error.showResTaskForm", e);
        }
	    
		return mapping.findForward(forward);
	}
	

	public ActionForward nextDay(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showResTask";
	    try {
	        ResTaskForm trfrm = (ResTaskForm)form;	        
	        this.resetForm(form);
	        
		    //increment the cursor (day) of allocation grid 	        
	        this.refreshBeforeSlotJump(request, trfrm, 1);
            
        } catch(MetaFieldNumericTypeException e){
        	this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
        } catch (Exception e) {
            this.setErrorFormSession(request, "error.showResTaskForm", e);
        }
		return mapping.findForward(forward);
	}
		
	
	/**
	 * Perform the action after Next button clicking at allocation grid 
	 */
	public ActionForward next(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
	    try {
	        ResTaskForm trfrm = (ResTaskForm)form;	        
	        this.resetForm(form);
	        
		    //increment the cursor (week) of allocation grid 	        
	        this.refreshBeforeSlotJump(request, trfrm, trfrm.getStepCursor());
            
        } catch(MetaFieldNumericTypeException e){
        	this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
        } catch (Exception e) {
            this.setErrorFormSession(request, "error.showResTaskForm", e);
        }
	    
		return mapping.findForward(forward);
	}


	private Timestamp refreshBeforeSlotJump(HttpServletRequest request, ResTaskForm trfrm, int jumpSize) throws BusinessException {
		Locale loc = SessionUtil.getCurrentLocale(request);	        

		String mask = super.getCalendarMask(request);
		String refDate = trfrm.getActualDate();
		if (refDate==null) {
		    refDate = trfrm.getEstimDate();   
		}

		Timestamp actualDate = DateUtil.getDateTime(refDate, mask, loc);	        
		ResourceTaskAllocTO rttoCopy = this.getAllocBySequence(trfrm, 1, true);
		if (rttoCopy==null) {
			this.getTransferObjectFromActionForm(trfrm, loc, request);
			rttoCopy = this.getAllocBySequence(trfrm, 1, true);
		}
		this.updateAllocationList(rttoCopy.getResourceTask(), trfrm, request, actualDate);
		
		this.calcActualTimeOnForm(trfrm, loc);

	    int cursor = trfrm.getAllocCursor();
	    cursor = cursor + jumpSize;
	    
	    if (cursor<1) {
	        
	        actualDate = DateUtil.getChangedDate(actualDate, Calendar.DATE, jumpSize);
	        trfrm.setActualDate(DateUtil.getDate(actualDate, mask, loc));
	        
	        //complement the allocation with new slots...	        
	        for (int i=2; i<=-jumpSize; i++) {
	            ResourceTaskAllocTO rtato = getEmptyResTaskAlloc(rttoCopy.getResourceTask(), i);
	            trfrm.addAllocationList(actualDate, rtato, i, true);		            
	        }
	        
	        trfrm.reorderSequence(actualDate, rttoCopy.getResourceTask());
	        cursor = 1;
	    }
	    
	    
	    trfrm.setAllocCursor(cursor);
		
	    updateDateAllocTimeList(trfrm, DateUtil.getChangedDate(actualDate, Calendar.DATE, cursor-1));
	    
		return actualDate;
	}

	
	public ActionForward previousDay(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
	    try {
	        ResTaskForm trfrm = (ResTaskForm)form;	        
	        this.resetForm(form);
	        
		    //decrement the cursor (day) of allocation grid 	        
	        this.refreshBeforeSlotJump(request, trfrm, -1);
	        
	    } catch(MetaFieldNumericTypeException e){
	    	this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
	    } catch (Exception e) {
	        this.setErrorFormSession(request, "error.showResTaskForm", e);
	    }
		
		return mapping.findForward(forward);
	}
	
	
	/**
	 * Perform the action after Previous button clicking at allocation grid 
	 */
	public ActionForward previous(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
	    try {
	        ResTaskForm trfrm = (ResTaskForm)form;	        
	        this.resetForm(form);
	        
		    //increment the cursor (week) of allocation grid 	        
	        this.refreshBeforeSlotJump(request, trfrm, -trfrm.getStepCursor());
            
        } catch(MetaFieldNumericTypeException e){
        	this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
        } catch (Exception e) {
            this.setErrorFormSession(request, "error.showResTaskForm", e);
        }
	    
		return mapping.findForward(forward);
	}
	

	public ActionForward removeTask(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showResTask";
	    TaskDelegate tdel = new TaskDelegate();
	    
		try {        
	        this.resetForm(form);

			String taskId = request.getParameter("task_id");
			String fwd = request.getParameter("nextFwd");
			if (fwd!=null) {
				forward = fwd;
			}
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			TaskTO tto = tdel.getTaskObject(new TaskTO(taskId));
			tto.setHandler(uto);
			tdel.removeTaskByOwner(tto);
			
		    this.refresh(mapping, form, request, response);

			this.setSuccessFormSession(request, "message.removeTask");
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showResTaskForm", e);
		}
		
		return mapping.findForward(forward);
	}
	

	private Vector<TaskStatusTO> getTaskStatusList(Integer currentStateMachine) throws BusinessException{
	    Vector<TaskStatusTO> response = new Vector<TaskStatusTO>();
	    TaskStatusDelegate tsdel = new TaskStatusDelegate();	    
	 
	    if (currentStateMachine==null) {
	        TaskStatusTO tsto1 = tsdel.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_OPEN);
	        response.add(tsto1);
	        TaskStatusTO tsto2 = tsdel.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_PROGRESS);
	        response.add(tsto2);
	        TaskStatusTO tsto3 = tsdel.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_CLOSE);
	        response.add(tsto3);
	    } else {
		    response = tsdel.getTaskStatusList(currentStateMachine);	        
	    }
	 
	    return response;
	}
	
	
	/**
	 * Put data of html fields into TransferObject 
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	private ResourceTaskTO getTransferObjectFromActionForm(ResTaskForm frm, Locale loc, HttpServletRequest request) throws BusinessException{
	    UserDelegate udel = new UserDelegate();
	    ResourceTaskTO resTask = new ResourceTaskTO();
	    TaskStatusDelegate tsdel = new TaskStatusDelegate();
	    TaskDelegate tdel = new TaskDelegate();
	    String mask = super.getCalendarMask(request);
	    
	    if (frm.getEstimDate()!=null) {
	        resTask.setStartDate(DateUtil.getDateTime(frm.getEstimDate(), mask, loc));

	        int intEstimatedTime = 0;
		    if (frm.isDecimalInput()) {
		    	intEstimatedTime = (int)(StringUtil.getStringToFloat(frm.getEstimTime(), loc) * 60) ;	
		    } else {
		    	intEstimatedTime = StringUtil.getHHMMToInteger(frm.getEstimTime());		    	
		    }
	        resTask.setEstimatedTime(new Integer(intEstimatedTime));
	    }
	    
	    TaskTO tto = new TaskTO(frm.getTaskId());
	    if (frm.getTaskId()!=null && !frm.getTaskId().trim().equals("")) {
	    	tto = tdel.getTaskObject(tto);	
	    }
	    ProjectTO pto = new ProjectTO(frm.getProjectId());
	    tto.setProject(pto);
	    
	    ResourceTO rto = new ResourceTO(frm.getResourceId());
	    rto.setProject(pto);
	    rto = udel.getResource(rto);
	    if (rto==null && !pto.getId().equals("-1")) {
	        throw new BusinessException("The resource is not part of project id [" + frm.getProjectId() + "]");
	    }
	    
	    resTask.setResource(rto);
	    resTask.setTask(tto);
        
	    TaskStatusTO tsto = tsdel.getObjectByStateMachine(frm.getTaskStatus());
	    resTask.setTaskStatus(tsto);

	    if (frm.getIsAdHocTask().equals("on")) {
	    	resTask.setAdHocTask(true);
	    }
	    
	    if (frm.getIsCurrentTaskCreator().equals("on")) {
	    	resTask.setCurrentTaskCreator(true);
	        resTask.setAdHocTaskDescription(frm.getTaskDesc());
	        resTask.setAdHocTaskName(frm.getTaskName());
	        resTask.setAdHocCategoryId(frm.getCategoryId());
	        resTask.setResource(rto);
	        tto.setCreatedBy(SessionUtil.getCurrentUser(request));
	    }
	    
	    //update allocation hash into session
	    if (frm.getActualDate()==null) {
	        frm.setActualDate(frm.getEstimDate());
	    }
	    resTask.setActualDate(DateUtil.getDateTime(frm.getActualDate(), mask, loc));
	    if (resTask.getStartDate()==null) {
	    	resTask.setStartDate(resTask.getActualDate());
	    }
	    
        this.populateAllocIntoMap(resTask, frm, request);
        
        resTask.setAllocList(frm.getAllocationInVectorFormat());
	    
	    //recalc the actual time based to the time set into slots
	    resTask.setActualTime(frm.sumarizeEstimatedTimeInMinutes());
	    	    
	    //crop the highest slot that contain a value = zero
	    this.rTrim(frm, resTask);
	    
	    //re-process the sequencing of slots and get the new actualDate (the first slot non-empty)
	    Timestamp newActualDate = this.recalcActualDate(frm, resTask);
	    resTask.setActualDate(newActualDate);
	    frm.reorderSequence(newActualDate, resTask);
	    
	    if (tsto.getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_CLOSE) && 
	    		(resTask.getEstimatedTime()==null || resTask.getEstimatedTime().intValue()==0)) {
	    	resTask.setEstimatedTime(resTask.getActualTime());	    	
	    }
	    
	    resTask.setQuestionAnswer(frm.getQuestionAnswer());
	    
		@SuppressWarnings("rawtypes")
		Vector<DiscussionTopicTO> discussionTopicList = (Vector)request.getSession().getAttribute("discussionTopicList");
		tto.setDiscussionTopics(discussionTopicList);
	    
		Vector<MetaFieldTO> metaFieldList = (Vector<MetaFieldTO>)request.getSession().getAttribute("metaFieldList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, tto);
		if (metaFieldList!=null) {
		    frm.setAdditionalFields(tto.getAdditionalFields());
		}
	    
	    if (frm.getParentTaskId().equals("-1")) {
	        tto.setParentTask(null);    
	    } else {
	        tto.setParentTask(new TaskTO(frm.getParentTaskId()));
	    }
		
	    if (frm.getIterationId()!=null && !frm.getIterationId().equals("-1")) {
	    	tto.setIteration(frm.getIterationId());	
	    } else {
	    	tto.setIteration(null);
	    }

	    resTask.setBillableStatus(new Boolean(frm.getBillableStatus().equals("1")));
	    
        return resTask;
	}

	
	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 */
	private void getActionFormFromTransferObject(ResTaskForm trfrm, HttpServletRequest request) throws BusinessException{
	    TaskDelegate tdel = new TaskDelegate();
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();	    
	    Locale loc = SessionUtil.getCurrentLocale(request);
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    String mask = uto.getCalendarMask();
	    
	    //create resource, project and task objects related with form...
	    ResourceTO rto = new ResourceTO(trfrm.getResourceId());
	    if (trfrm.getTaskId()!=null && !trfrm.getTaskId().equals("")) {
		    TaskTO tto = new TaskTO(trfrm.getTaskId());
		    ProjectTO pto = new ProjectTO(trfrm.getProjectId());
		    rto.setProject(pto);
		    
		    //set data of related task into form     
		    tto = tdel.getTaskObject(tto);
		    trfrm.setTaskName(tto.getName());
		    trfrm.setTaskDesc(tto.getDescription());
		    
		    //set the additional Fields to current form
		    trfrm.setAdditionalFields(tto.getAdditionalFields());

		    //set the relationship of task
		    request.getSession().setAttribute("relationshipList", tto.getRelationList());
		    request.getSession().setAttribute("discussionTopicList", new Vector<DiscussionTopicTO>());
		    if (tto.getAttachments()!=null) {
		    	request.getSession().setAttribute("taskAttachmentList", tto.getAttachments());
		    }
		    
		    ResourceTaskTO rtto = new ResourceTaskTO();
		    rtto.setTask(tto);	    
		    rtto.setResource(rto);
		    rtto = rtdel.getResourceTaskObject(rtto);
		    if (rtto.getStartDate()!=null) {
		    	trfrm.setEstimDate(DateUtil.getDate(rtto.getStartDate(), mask, loc));	
		    }
		    if (trfrm.isDecimalInput()) {
		    	trfrm.setEstimTime(StringUtil.getFloatToString(rtto.getEstimatedTimeInHours(), loc));	
		    } else {
		    	trfrm.setEstimTime(StringUtil.getIntegerToHHMM(rtto.getEstimatedTime(), loc));		    	
		    }
		    if (tto.getCategory()!=null) {
		    	trfrm.setCategoryId(tto.getCategory().getId());	
		    }
		    
	    	trfrm.setShowDecisionQuestion(rtto.isTheWIPTask()? "on": "off");
	    	if (rtto.getTask()!=null && rtto.getTask().getDecisionNode()!=null) {
	    		trfrm.setQuestionText(rtto.getTask().getDecisionNode().getQuestionContent());	
	    	}
	    	
		    Timestamp prefFirstDate = rtDeleg.getPreferedDate(rtto);
			trfrm.setFirstAllocSlot(DateUtil.getDate(prefFirstDate, mask, loc));	        
						
			if (rtto.getTaskStatus().isOpenNotReopened()) {
			    trfrm.setActualDate(null);
			} else {
				Timestamp tsbuff = rtto.getActualDate()!=null?rtto.getActualDate():rtto.getStartDate();
			    trfrm.setActualDate(DateUtil.getDate(tsbuff, mask, loc));
			}
		    
			if (tdel.isUserTaskOwner(tto, uto)){
				trfrm.setIsCurrentTaskCreator("on");
			} else {
				trfrm.setIsCurrentTaskCreator("off");
			}
			
		    //get the last history object of task in order to get the last comment
		    TaskHistoryTO thto = tdel.getLastHistoryByTask(rtto);
		    if (thto!=null){
		        trfrm.setComment(thto.getComment());
		    } else {
		        LogUtil.log(this, LogUtil.LOG_ERROR, "Error. The resourceTask: [" + rtto.getId() + "] does not contain any history record.");
		    }
		    
		    trfrm.setTaskStatus(rtto.getTaskStatus().getStateMachineOrder());
		    
		    //refreh allocation hash
		    this.populateAllocIntoMap(rtto, trfrm, request);

		    //check if current task is connected to a requirement
		    if (tto.getRequirement()!=null){		        
		        CustomerTO requester = tto.getRequirement().getRequester();
		        
			    //if allowed, get the name of customer related with requirement	        
		        trfrm.setReqCustomerName(null);
		        if (this.checkIfCanSeeCustomer(pto, uto)){
		            trfrm.setReqCustomerName(requester.getName());    
		        }	        
		    }
		    
		    if (!rtto.getTaskStatus().getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_CLOSE)) {
			    if (tdel.isBlocked(tto)) {
			    	String errorMsg = getBundleMessage(request, "error.blockedTask");
			    	throw new BusinessException(errorMsg);
			    }
		    }
		    
		    if (tto.getParentTask()!=null){
		        trfrm.setParentTaskId(tto.getParentTask().getId());   
		    }else {
		        trfrm.setParentTaskId("-1");
		    }
		    
		    trfrm.setIterationId(tto.getIteration());
		    
		    if (rtto.getBillableStatus()!=null) {
		    	trfrm.setBillableStatus(rtto.getBillableStatus().booleanValue()?"1":"0");
		    } else {
		    	//get default value (copy the billable status of task category...)
		    	trfrm.setBillableStatus(tto.getCategory().getIsBillable().booleanValue()?"1":"0");	
		    }		    
	    }
	}
	
	
	/**
	 * Assembly the allocation hashMap to populate the grid of allocation time.
	 */
	private void populateAllocIntoMap(ResourceTaskTO rtto, ResTaskForm trfrm, HttpServletRequest request) throws BusinessException{
	    //get the prefered date (may be estimated or actual date)	    
	    Timestamp prefDate = rtDeleg.getPreferedDate(rtto); 
	    
	    if (trfrm.getAllocationList()!=null) {
	        this.updateAllocationList(rtto, trfrm, request, prefDate);
	    } else {
		    //get current allocation list from data base 
		    this.initAllocationHash(rtto, trfrm);
	    }
	    
	    //calculate actual time with values from data base
	    if (!rtto.getTaskStatus().isOpenNotReopened()) {
		    Locale loc = SessionUtil.getCurrentLocale(request);
	        this.calcActualTimeOnForm(trfrm, loc);		        	        
	    } else {
	        trfrm.setActualTime("0");
	    }
	}


    private void calcActualTimeOnForm(ResTaskForm trfrm, Locale loc) {
		Integer timeInMinutes = trfrm.sumarizeEstimatedTimeInMinutes();
	    if (trfrm.isDecimalInput()) {
	        float actualTime = ((float)timeInMinutes.intValue() / 60);	    	
	    	trfrm.setActualTime(StringUtil.getFloatToString(actualTime, loc));
	    } else {
	    	trfrm.setActualTime(StringUtil.getIntegerToHHMM(timeInMinutes, loc));		    	
	    }    		
    }


    private void updateAllocationList(ResourceTaskTO rtto, ResTaskForm trfrm, HttpServletRequest request, Timestamp prefDate) {
        Locale loc = SessionUtil.getCurrentLocale(request);
        
        //set data from current displayable week into hash 
        for (int i=1; i<=7; i++) {
            String topValue = request.getParameter("slotTop"+ i);
            String botValue = request.getParameter("slotBotton"+ i);
            trfrm.updateAllocationList(prefDate, topValue, botValue, (i + trfrm.getAllocCursor() - 1), rtto, loc);
        }
    }


    /**
	 * Get current allocation list from data base, and setup the 
	 * hashMap into current form object.
	 */
    private void initAllocationHash(ResourceTaskTO rtto, ResTaskForm trfrm) throws BusinessException {
        ResourceTaskAllocDelegate rtadel = new ResourceTaskAllocDelegate();
        
        Vector<ResourceTaskAllocTO> allocFromDB = null;
        if (rtto.getTask()!=null && !rtto.getTask().getId().equals("")) {
            allocFromDB = rtadel.getListByResourceTask(rtto);
        }

        if (allocFromDB!=null && allocFromDB.size()>0) {
            for (int i = 0 ; i<allocFromDB.size(); i++) {
                ResourceTaskAllocTO rtato = allocFromDB.elementAt(i);
                rtato.setResourceTask(rtto);
                trfrm.addAllocationList(null, rtato, rtato.getSequence().intValue(), false);
            }
        } else {
            ResourceTaskAllocTO rtato = this.getEmptyResTaskAlloc(rtto, 1);
            trfrm.addAllocationList(null, rtato, 1, true);
        }
        
    }
    
    private void updateDateAllocTimeList(ResTaskForm trfrm, Timestamp actualDate) throws BusinessException{
    	if(actualDate == null){
    		Locale loc =  trfrm.getCurrentUser().getLocale();
		    
		    if (trfrm.getActualDate()!=null) {
		    	actualDate = trfrm.getDate(false, loc);    
		    } else {
		    	actualDate = trfrm.getDate(true, loc);
		    }
		    
		    actualDate = DateUtil.getChangedDate(actualDate, Calendar.DATE,  (trfrm.getAllocCursor()-1));
    	}
    	ResourceTaskAllocDelegate rtadel = new ResourceTaskAllocDelegate();
		ResourceTO rto = new ResourceTO(trfrm.getResourceId());
		Vector<ResourceDateAllocTO> dateAllocTimeList = rtadel.getResourceDateAllocList(rto, actualDate, DateUtil.getChangedDate(actualDate, Calendar.DATE, 7));
		trfrm.setDateAllocTimeList(dateAllocTimeList);
    }
    
    
    private ResourceTaskAllocTO getEmptyResTaskAlloc(ResourceTaskTO rtto, int seq){
        ResourceTaskAllocTO response = new ResourceTaskAllocTO();
        response.setAllocTime(new Integer(0));
        response.setResourceTask(rtto);
        response.setSequence(new Integer(seq));
    	return response;
    }

    
    /**
     * Remove the empty alloc unit values on "right to left" direction. 
     * In other words: crop the rightest slot that contain a empty value
     */
    private void rTrim(ResTaskForm frm, ResourceTaskTO rtto) throws BusinessException {
        if (frm.getAllocationList()!=null) {
            for (int i = frm.getAllocationList().size(); i>0; i--){
                ResourceTaskAllocTO rtato = this.getAllocBySequence(frm, i, true);
                if (rtato!=null) {
                    Integer value = rtato.getAllocTime();
                    if (value==null || value.intValue()==0){
                        rtto.getAllocList().remove(rtato);
                    } else {
                        break;
                    }                
                }
            }            
        }
    }
    

    /**
     * Re-process the sequencing of slots and return the new actualDate (the first slot non-empty)
     */
    private Timestamp recalcActualDate(ResTaskForm frm, ResourceTaskTO rtto) throws BusinessException {
        Timestamp newActualDate = rtto.getActualDate();
        int seq = 0;
        
        //crop the fist empty slots
        if (frm.getAllocationList()!=null) {
            for (int i=1; i<=frm.getAllocationList().size(); i++){
                ResourceTaskAllocTO rtato = this.getAllocBySequence(frm, i, true);
                if (rtato!=null) {
                    Integer value = rtato.getAllocTime();
                    if (value==null || value.intValue()==0){
                        rtto.getAllocList().remove(rtato);
                        seq++;
                        newActualDate = DateUtil.getChangedDate(rtto.getActualDate(), Calendar.DATE, i);
                    } else {
                        break;
                    }
                }
            }

            //if there is not any alloc into list..set the actual date to the previous value
            if (rtto.getAllocList().size()==0) {
                newActualDate = rtto.getActualDate();
            }
            
            //if the process has cropped the alloc list, then rebuild the sequence...
            if (seq>0) {
                Iterator<ResourceTaskAllocTO> i = rtto.getAllocList().iterator();
                while(i.hasNext()) {
                    ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)i.next();
                    Integer sequence = rtato.getSequence();
                    rtato.setSequence(new Integer((sequence.intValue() - seq + 1)));
                }
            }            
        }
                
        return newActualDate;
    }
    
    /**
	 * Check if current resource can see the owner of requirement (has permission).  
	 */
	private boolean checkIfCanSeeCustomer(ProjectTO pto, UserTO uto) throws BusinessException{
	    UserDelegate udel = new UserDelegate();
	    ResourceTO filter = new ResourceTO();
	    filter.setId(uto.getId());
	    filter.setProject(pto);
	    ResourceTO rto = udel.getResource(filter);
	    return rto.getBoolCanSeeCustomer();
	}	
	
	
    /**
     * Return a allocation object (specific slot) based on sequence
     */
    private ResourceTaskAllocTO getAllocBySequence(ResTaskForm trfrm, int seq, boolean forceAddIntoActual){
        ResourceTaskAllocTO response = null;
        
        HashMap<String,ResourceTaskAllocTO> list = null;
        if (trfrm.getTaskStatus()!=null && trfrm.getTaskStatus().equals(TaskStatusTO.STATE_MACHINE_OPEN) && !forceAddIntoActual) {
        	list = trfrm.getEstimAllocList();
        } else {
        	list = trfrm.getAllocationList();
        }
        
        if (list!=null) {
            Iterator<ResourceTaskAllocTO> i = list.values().iterator();
            while(i.hasNext()){
                ResourceTaskAllocTO token = (ResourceTaskAllocTO)i.next();
                if (token.getSequence().intValue()==seq) {
                    response = token;
                    break;
                }
            }            
        }
        return response;
    }

    
	/**
	 * Define a initial value for the cursor
	 */
	private void defineCursor(ResTaskForm trfrm, HttpServletRequest request) {
		if (trfrm.getFirstAllocSlot()!=null && !trfrm.getFirstAllocSlot().trim().equals("")) {
		    Timestamp firstDate = DateUtil.getDateTime(trfrm.getFirstAllocSlot(), super.getCalendarMask(request), SessionUtil.getCurrentLocale(request));
		    int slots = DateUtil.getSlotBetweenDates(firstDate, DateUtil.getNow());
		    int cursorIdx = (new Double(Math.ceil(slots/7))).intValue();
		    trfrm.setAllocCursor((cursorIdx * 7)+1);
		}
	}

	
	private ResourceTaskTO preProcessForm(ActionForm form, HttpServletRequest request) throws BusinessException{  
	    ResTaskForm trfrm = (ResTaskForm)form;
	    this.resetForm(form);
        Locale loc = this.getLocale(request);
	    ResourceTaskTO rtto = this.getTransferObjectFromActionForm(trfrm, loc, request);
	    this.rTrim(trfrm, rtto);
	    
	    return rtto;
	}
	
	
	private void resetForm(ActionForm form){
	    ResTaskForm trfrm = (ResTaskForm)form;
    	trfrm.setReportTaskURL("");
    	trfrm.setShowWorkflowDiagram("off");
	}
	
	
	public ActionForward clickNodeTemplate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			ShowAllTaskAction showAllAction = new ShowAllTaskAction();
			ResTaskForm trfrm = (ResTaskForm)form;
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        
	        PrintWriter out = response.getWriter();
	        String content = showAllAction.createDiagramNodeTip(request, trfrm.getPlanningId()).toString();
	        out.println(content);
	        
	        out.flush();
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showAllReqForm", e);
		}

		return null;
	}	
	
	private void getDiscussionData(HttpServletRequest request, ResTaskForm rtfrm) throws BusinessException{
	    DiscussionTopicDelegate dtdel = new DiscussionTopicDelegate();		
		if (rtfrm.getTaskId()!=null) {
			Vector<DiscussionTopicTO> listDt = dtdel.getListByPlanning(rtfrm.getTaskId());
			request.getSession().setAttribute("discussionTopicList", listDt);			
		}		
	}	
	
	public ActionForward showProjectPopup(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showChangeProjResTask");
	}
	
	public ActionForward changeProject(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
		try{
			ResTaskForm trfrm = (ResTaskForm)form;
			String projectId = request.getParameter("id");
			ResourceTaskTO rtto = this.preProcessForm(trfrm, request);
			
			rtto = rtdel.changeProject(projectId, rtto);
			
			trfrm.setProjectId(projectId);
			trfrm.setCategoryId(rtto.getTask().getCategory().getId());
			
			this.refresh(mapping, form, request, response);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.saveResTask");
		} catch(MetaFieldNumericTypeException e){
			this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch(Exception e){
			this.setErrorFormSession(request, "error.showAllTaskForm", e);
		}
		return mapping.findForward("showResTask");
	}
	
	public ActionForward refreshAfterAttach(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showResTask";	
		try {
			ResTaskForm trfrm = (ResTaskForm)form;
		    
		    if (trfrm.getTaskId()!=null && !trfrm.getTaskId().equals("")) {
		    	trfrm.setSaveMethod(GeneralStrutsForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
				this.getActionFormFromTransferObject(trfrm, request);		    	
		    } else {
		    	request.getSession().setAttribute("attachmentList", new Vector<AttachmentTO>());
		    }
								
		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.showReportForm", e);
		}

		return mapping.findForward(forward);
	}	
	
}
