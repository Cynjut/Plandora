package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.PreferenceTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RootTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.InvalidTaskStateTransitionException;
import com.pandora.exception.InvalidTaskTimeException;
import com.pandora.exception.ProjectTasksDiffRequirementException;
import com.pandora.exception.TasksDiffRequirementException;
import com.pandora.gui.struts.exception.InputGuiException;
import com.pandora.gui.struts.form.AgilePanelTaskForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.FormValidationUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class AgilePanelTaskAction extends GeneralStrutsAction {

	public ActionForward showEditPopup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showAgilePanelEdit";
		
		try {
			AgilePanelTaskForm frm = (AgilePanelTaskForm)form;
			String taskId = frm.getTaskId();
			Locale loc = SessionUtil.getCurrentLocale(request);
			String mask = super.getCalendarMask(request);
			RequirementTO rto = null;
			
			CategoryDelegate cdel = new CategoryDelegate();
			Vector<CategoryTO> taskCategList = new Vector<CategoryTO>();
			
			Vector<RequirementWithTasksTO> newReqList = this.getReqList(request);
			request.getSession().setAttribute("requirementList", newReqList);
				
			ResourceTaskTO rtto = null;
			if (taskId!=null && !taskId.trim().equals("")) {
				ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
				rtto = rtdel.getResourceTaskObject(frm.getTaskId(), frm.getResourceId());
				TaskTO tto = rtto.getTask();
				UserTO uto = SessionUtil.getCurrentUser(request);
				
				frm.setName(tto.getName());
				frm.setDescription(tto.getDescription());
				if (tto.getIsUnpredictable()!=null) {
					frm.setIsUnPredictable(tto.getIsUnpredictable().booleanValue());	
				}
				frm.setIsOpen(tto.isOpen()?"on":"off");
				
				if (tto.getRequirementId()==null) {
					frm.setRequirementId("-1");
				} else {
					frm.setRequirementId(tto.getRequirementId());	
				}
												
				frm.setEstimatedDate(DateUtil.getDate(rtto.getStartDate(), mask, loc));
				frm.setEstimatedTime(this.formatTime(uto, loc, rtto));
				
		        if (tto.getParentTask()!=null && !tto.getParentTask().getId().trim().equals("-1")) {
		            frm.setParentTaskId(tto.getParentTask().getId());	
		        } else {
		            frm.setParentTaskId("-1");    
		        }
				
				taskCategList = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, tto.getProject(), false);			
				frm.setCategoryId(tto.getCategory().getId());
				rto = tto.getRequirement();
				
			} else {
				frm.setName("");
				frm.setDescription("");
				frm.setCategoryId("0");
				frm.setParentTaskId("-1");
				frm.setEstimatedDate(DateUtil.getDate(DateUtil.getNow(), mask, loc));
				frm.setEstimatedTime("");
				request.getSession().setAttribute("requirementList", newReqList);
				frm.setIsOpen("on");
				
				RequirementDelegate rdel = new RequirementDelegate();
				rto = rdel.getRequirement(new RequirementTO(frm.getRequirementId()));
				if (rto!=null) {
					taskCategList = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, rto.getProject(), false);	
				}
			}

			if (rto!=null) {
				//get macro-tasks from data base (filter by requirement) and put into http session (to be displayed by combo)
				TaskDelegate tdel = new TaskDelegate();
				Vector<TaskTO> parentList = tdel.getAvailableParentTaskList(rto, rto.getProject().getId());
			    request.getSession().setAttribute("taskAvailable", parentList);
			    
				UserDelegate udel = new UserDelegate();
				Vector<ResourceTO> resList = udel.getResourceByProject(rto.getProject().getId(), false, true);
				for (ResourceTO res : resList) {
					if (res.getUsername().equals(RootTO.ROOT_USER)) {
						resList.remove(res);
						break;
					}
				}
				UserTO anyUser = udel.getRoot();
				ResourceTO anyRes = new ResourceTO(anyUser.getId());
				anyRes.setName(super.getBundleMessage(request, "label.manageTask.anyRes"));
				resList.add(0, anyRes);
				request.getSession().setAttribute("projectResourceList", resList);			

			} else {
				request.getSession().setAttribute("taskAvailable", new Vector<TaskTO>());
				if (rtto!=null) {
					Vector<ResourceTO> resList = new Vector<ResourceTO>();
					resList.add(rtto.getResource());
					request.getSession().setAttribute("projectResourceList", resList);
				} else {
					request.getSession().setAttribute("projectResourceList", new Vector<ResourceTO>());	
				}
			}
			
			request.getSession().setAttribute("taskCategoryList", taskCategList);
			
		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);		
		}
		
		return mapping.findForward(forward);
	}


	public ActionForward saveTask(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			AgilePanelTaskForm frm = (AgilePanelTaskForm)form;
			TaskDelegate tdel = new TaskDelegate();
			UserTO uto = SessionUtil.getCurrentUser(request);
			Locale loc = SessionUtil.getCurrentLocale(request);
			String taskId = frm.getTaskId();
			
			if (taskId!=null && !taskId.trim().equals("")) {
				
				TaskTO tto = tdel.getTaskObject(new TaskTO(taskId));
				tto.setHandler(uto);
				
				tto.setName(frm.getName());
				tto.setDescription(frm.getDescription());
				tto.setIsUnpredictable(new Boolean(frm.getIsUnPredictable()));
				tto.setCategory(new CategoryTO(frm.getCategoryId()));

				if (frm.getRequirementId()!=null && !frm.getRequirementId().trim().equals("-1")) {
					tto.setRequirement(new RequirementTO(frm.getRequirementId()));	
				} else {
					tto.setRequirement(null);
				}

		        if (frm.getParentTaskId()!=null && !frm.getParentTaskId().trim().equals("-1")) {
		            tto.setParentTask(new TaskTO(frm.getParentTaskId()));	
		        } else {
		            tto.setParentTask(null);    
		        }
				
				if (tto.isOpen()) {
					ResourceTaskTO rtto = (ResourceTaskTO)tto.getAllocResources().get(0);
					this.setEstimatedFields(frm, uto, loc, rtto);
				}
				
				tdel.updateTask(tto, false);
				
				//TODO implement the resource changing...
				//String resourceId = frm.getResourceId();				
				//ResourceTaskTO rtto = rtdel.getResourceTaskObject(taskId, resourceId);				
				
			} else {
				String reqId = frm.getRequirementId();
				RequirementDelegate rdel = new RequirementDelegate();
				RequirementTO rto = rdel.getRequirement(new RequirementTO(reqId));				
				
				if (rto!=null) {
					TaskTO tto = new TaskTO(null);
					tto.setHandler(uto);
					tto.setName(frm.getName());
					tto.setDescription(frm.getDescription());
					tto.setIsUnpredictable(new Boolean(frm.getIsUnPredictable()));				
					tto.setRequirement(rto);
					tto.setCategory(new CategoryTO(frm.getCategoryId()));
				    tto.setFinalDate(null);
				    tto.setProject(rto.getProject());
			        tto.setIsParentTask(new Integer(0));
			        tto.setParentTask(null);    
				    tto.setCreationDate(DateUtil.getNow());
				    tto.setCreatedBy(uto);

					Vector<ResourceTaskTO> allocation = new Vector<ResourceTaskTO>();
					ResourceTaskTO rtto = new ResourceTaskTO();
					rtto.setHandler(uto);					
					rtto.setResource(new ResourceTO(frm.getResourceId()));
					rtto.setTask(tto);
					allocation.addElement(rtto);
					tto.setAllocResources(allocation);
					
					this.setEstimatedFields(frm, uto, loc, rtto);
					
					tdel.insertTask(tto);					
				}
			}
			
			this.setSuccessFormSession(request, "message.agilePanelForm.saveTask");
			
		} catch(InputGuiException e){
			this.setErrorFormSession(request, "message.agilePanelForm.inputErr", e);
		} catch(ProjectTasksDiffRequirementException e){
		    this.setErrorFormSession(request, "error.manageTask.diffPrjReq", e);			
		} catch(TasksDiffRequirementException e){
			this.setErrorFormSession(request, "error.manageTask.diffReq", e);			
		} catch(InvalidTaskTimeException e){
			this.setErrorFormSession(request, "validate.resTaskForm.nonOpenZeroTime", e);
		} catch(InvalidTaskStateTransitionException e){
		    this.setErrorFormSession(request, "error.invalidTaskTransState", e);
		} catch(Exception e){
		    this.setErrorFormSession(request, "message.agilePanelForm.errSaveTask", e);		    
		}

		return mapping.findForward("goToAgileForm");		
	}


	private void setEstimatedFields(AgilePanelTaskForm frm, UserTO uto,
			Locale loc, ResourceTaskTO rtto) throws InputGuiException {
		
		if (frm.getEstimatedDate()!=null && !frm.getEstimatedDate().trim().equals("")) {
			Timestamp startDate = DateUtil.getDateTime(frm.getEstimatedDate(), uto.getCalendarMask(), loc);
			if (startDate!=null) {
				rtto.setStartDate(startDate);	
			} else {
				throw new InputGuiException("The estimated date contain an invalid value.");
			}
		} else {
			rtto.setStartDate(DateUtil.getNow());
		}
		
		if (frm.getEstimatedTime()!=null && !frm.getEstimatedTime().trim().equals("")) {
			Integer val = formatTime(uto, loc, frm.getEstimatedTime());
			if (val!=null) {
				rtto.setEstimatedTime(val);	
			} else {
				throw new InputGuiException("The estimated time contain an invalid value.");
			}
		} else {
			rtto.setEstimatedTime(new Integer(0));
		}
	}

	
	private String formatTime(UserTO uto, Locale loc, ResourceTaskTO rtto) {
		String response = "";
	    String frmtInput = uto.getPreference().getPreference(PreferenceTO.INPUT_TASK_FORMAT);
	    if (!frmtInput.equals("2")) {
	        response = StringUtil.getFloatToString(rtto.getEstimatedTimeInHours(), loc);
	    } else {
	    	response =  StringUtil.getIntegerToHHMM(rtto.getEstimatedTime(), loc);		    	
	    }    		
	    return response;
	}

	private Integer formatTime(UserTO uto, Locale loc, String value) {
		Integer response = null;
		ActionErrors errors = new ActionErrors();

	    String frmtInput = uto.getPreference().getPreference(PreferenceTO.INPUT_TASK_FORMAT);
	    int timeInMinutes = 0;
	    try {
		    if (!frmtInput.equals("2")) {
		        FormValidationUtil.checkFloat(errors, "", value, loc);
				if (errors.isEmpty()) {
			    	timeInMinutes = (int)(StringUtil.getStringToFloat(value, loc) * 60);
			    	if (timeInMinutes>=0) {
			    		response = new Integer(timeInMinutes);	
			    	}
				}
		    } else {
				FormValidationUtil.checkHHMM(errors, "", value, loc, true);
				if (errors.isEmpty()) {
					timeInMinutes = StringUtil.getHHMMToInteger(value);
					response = new Integer(timeInMinutes);	
				}
		    }			    
		    
	    } catch (Exception e){
	    	response = null;
	    }			

	    return response;
	}
	
	
	@SuppressWarnings("unchecked")
	private Vector<RequirementWithTasksTO> getReqList(HttpServletRequest request) {
		Vector<RequirementWithTasksTO> newReqList = new Vector<RequirementWithTasksTO>();
		RequirementWithTasksTO rwto = new RequirementWithTasksTO();
		rwto.setId("-1");
		rwto.setDescription("");
		newReqList.addElement(rwto);
		
		Vector<RequirementWithTasksTO> reqList = (Vector<RequirementWithTasksTO>)request.getSession().getAttribute("reqTaskBoardList");
		if (reqList!=null) {
			Iterator<RequirementWithTasksTO> r = reqList.iterator();
			while(r.hasNext()) {
				RequirementWithTasksTO item = (RequirementWithTasksTO)r.next();
				RequirementStatusTO rsto = item.getRequirementStatus();
				if (!rsto.isFinished()){
					newReqList.addElement(item);
				}
			}			
		}
		return newReqList;
	}

}
