package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.RequirementTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.RefuseForm;
import com.pandora.helper.SessionUtil;


/**
 * This class handle the actions performed into Refuse Requirement form
 */
public class RefuseAction extends GeneralStrutsAction {

	/**
	 * Show for of refuse requirement.  
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showRefuse";
	    
	    try {
		    RefuseForm rfrm =(RefuseForm)form;
		    rfrm.setComment("");
		    rfrm.setRelatedRequirementId(null);
		    
		    if (rfrm.getRefuseType().equals(RefuseForm.CANCEL_TSK)) {
		    	String req = this.getReqFromLastTask(request, rfrm);
		    	rfrm.setRelatedRequirementId(req);
		    }
		    
		    this.clearMessages(request);

	    } catch(BusinessException e){
		    this.setErrorFormSession(request, "error.cancelTaskForm", e);
		}
		
	    return mapping.findForward(forward);
	}

	
	/**
	 * Refuse a requirement (receive post from form, set the 'refuse' status for
	 * the requirement and create a new record into requirement history)
	 */
	public ActionForward refuseRequirement(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = null;
		
		try {		    
		    //get current form and user
		    RefuseForm rfrm =(RefuseForm)form;
		    UserTO uto = SessionUtil.getCurrentUser(request);		    
		    forward = rfrm.getForwardAfterRefuse();
		    rfrm.setRelatedRequirementId(null);
		    
		    RequirementDelegate rdel = new RequirementDelegate();
		    RequirementTO rto = new RequirementTO();
		    rto.setId(rfrm.getRefusedId());
		    rdel.refuseRequirement(rto, uto, rfrm.getComment());

		    this.setSuccessFormSession(request, "message.refuse.RefusedSuccessfully");
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.cancelTaskForm", e);
		}
		
	    return mapping.findForward(forward);
	}

	
	/**
	 * Get the comment for task cancelation and perform the cancelation process
	 */
	public ActionForward cancelTask(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = null;
	    
	    try {
		    //clear current message of form
	        RefuseForm rfrm =(RefuseForm)form;
			TaskTO tto = new TaskTO(rfrm.getRefusedId());
			tto.setHandler(SessionUtil.getCurrentUser(request));
			tto.setComment(rfrm.getComment());
			forward = rfrm.getForwardAfterRefuse();
			rfrm.setRelatedRequirementId(null);
			
			//set the current task status to 'cancel'
			TaskDelegate tdel = new TaskDelegate();
			tdel.cancelTask(tto, rfrm.getReopenReqAfterTaskCancelation());

		    this.setSuccessFormSession(request, "message.refuse.CancelSuccessfully");
			
	    } catch(BusinessException e){
	        this.setErrorFormSession(request, "error.cancelTaskForm", e);
		}
	    
	    return mapping.findForward(forward);
	}
	
	private String getReqFromLastTask(HttpServletRequest request, RefuseForm rfrm) throws BusinessException{
        String response = null;
		
        try {
    		TaskDelegate tdel = new TaskDelegate();		        
            	        
            TaskTO tto = tdel.getTaskObject(new TaskTO(rfrm.getRefusedId()));
            if (tto.getRequirementId()!=null) {
                Vector<TaskTO> list = tdel.getTaskListByRequirement(tto.getRequirement(), tto.getProject(), false);
                response = tto.getRequirementId();
                
                Iterator<TaskTO> i = list.iterator();
                while(i.hasNext()) {
                	TaskTO t = i.next();
                	if (!t.getId().equals(tto.getId())) {
                		response = null;
                		break;
                	}
                }            	
            }
        	
        } catch(Exception e){
        	e.printStackTrace();
        }
        
        return response;
	    
	}
}
