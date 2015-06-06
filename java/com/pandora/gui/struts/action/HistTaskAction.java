package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.ResourceTO;
import com.pandora.TaskHistoryTO;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.HistTaskForm;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into Task History form
 */
public class HistTaskAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    
	    String forward = "showHistory";
	    TaskDelegate tdel = new TaskDelegate();
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
	    try {		
	        HistTaskForm frm = (HistTaskForm) form;
	        this.clearMessages(request);

	        //get all Information about task history 
	        Vector<TaskHistoryTO> taskList = null;
	        if (frm.getTaskId()!=null && !frm.getTaskId().equals("")){
	            if (frm.getResourceId()!=null && !frm.getResourceId().equals("")){
	                taskList = rtdel.getHistory(frm.getTaskId(), frm.getResourceId());
	            } else {
	                taskList = tdel.getHistory(frm.getTaskId());    
	            }
	        } else {
	            taskList = tdel.getHistoryByRequirementId(frm.getReqIdRelated());
	        }
	        request.getSession().setAttribute("taskHistoryList", taskList);
	        
	    } catch(BusinessException e){
	        LogUtil.log(this, LogUtil.LOG_ERROR, "Show TaskHistory error", e);
	    }

	    return mapping.findForward(forward);
	}

	
	public ActionForward showFollowUp(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {	    
	    String forward = "showTaskComment";
	    TaskDelegate tdel = new TaskDelegate();
	    
	    try {		
	        HistTaskForm frm = (HistTaskForm) form;
	        ResourceTO rto = new ResourceTO(frm.getResourceId());

	        @SuppressWarnings({ "rawtypes", "unchecked" })
			Vector<TaskHistoryTO> taskList = (Vector)request.getSession().getAttribute("taskHistoryList");
	        
			rto.setBundle(SessionUtil.getCurrentUser(request).getBundle());	
	        String comment = tdel.getTechCommentsFromTask(taskList, rto);
	        frm.setHistoryComment(comment);
	        
	    } catch(BusinessException e){
	        LogUtil.log(this, LogUtil.LOG_ERROR, "Show TaskHistory error", e);
	    }
	    return mapping.findForward(forward);	        
	}
	
	
	/**
	 * Return the comment of current task state selected
	 */
	public ActionForward viewComment(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    
	    String forward = "showTaskComment";
        HistTaskForm frm = (HistTaskForm) form;
 	
    	@SuppressWarnings({ "rawtypes", "unchecked" })
		Vector<TaskHistoryTO> taskList = (Vector)request.getSession().getAttribute("taskHistoryList");

		//get comments of requirement status selected
		String selectedItem = frm.getSelectedIndex();
		if (selectedItem!=null) {
		    int index = Integer.parseInt(selectedItem);
		    TaskHistoryTO thto = taskList.get(index);
		    frm.setHistoryComment(thto.getComment());
		}
		
		return mapping.findForward(forward);	    	
	}
	
}
