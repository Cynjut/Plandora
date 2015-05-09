package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CustomerTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementTO;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.HistReqForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * This class handle the actions performed into Requirement History form
 */
public class HistReqAction extends GeneralStrutsAction {

	/**
	 * Shows the login form
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response) {	
		String forward = "showHistory";
		
        try {		
            HistReqForm frm = (HistReqForm) form;
			this.clearMessages(request);
			
			//get all Information about request history 
			RequirementDelegate rdel = new RequirementDelegate();
			Vector reqList = rdel.getHistory(frm.getReqId());
			request.getSession().setAttribute("reqHistoryList", reqList);
			Locale loc = SessionUtil.getCurrentLocale(request);
			
			//get current Requirement Object
			RequirementTO rto = rdel.getRequirement(new RequirementTO(frm.getReqId()));
			frm.setDescRequirement(rto.getDescription());
			
			//set deadlines values...
			this.refreshDeadLine(frm, request, loc);
			
		} catch(BusinessException e){
		    LogUtil.log(this, LogUtil.LOG_ERROR, "Show RequirementHistory error", e);
		}
		
		return mapping.findForward(forward);
	}

	/**
	 * Return the content of current requirement state selected
	 */
	public ActionForward viewComment(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    
	    String forward = "showReqComment";
        try {		
	    	
	    	HistReqForm frm = (HistReqForm) form;
			RequirementDelegate rdel = new RequirementDelegate();	    	
	    	RequirementTO rto = rdel.getRequirement(new RequirementTO(frm.getReqId()));
	    	
	    	Locale loc = SessionUtil.getCurrentLocale(request);
	    	Vector reqList = (Vector)request.getSession().getAttribute("reqHistoryList");

			//get data about current requester
			UserDelegate udel = new UserDelegate();
			CustomerTO cto = rto.getRequester();
			cto.setProject(rto.getProject());
			cto = udel.getCustomer(cto);
			cto.setBundle(SessionUtil.getCurrentUser(request).getBundle());			

			//get comments of requirement status selected
			String selectedItem = frm.getSelectedIndex();
			if (selectedItem!=null) {
			    int index = Integer.parseInt(selectedItem);
			    RequirementHistoryTO rhto = (RequirementHistoryTO)reqList.get(index);
			    frm.setHistoryComment(this.getComments(rhto, loc, cto));
			}
			
						
		} catch(BusinessException e){
		    LogUtil.log(this, LogUtil.LOG_ERROR, "Show RequirementHistory error", e);
		}
		
    	return mapping.findForward(forward);	    	
	}
	
	
	/**
	 * Create a piece of JavaScript code in order to set into JSP 
	 * the content of all possible comments for the current requirement.
     */
    private String getComments(RequirementHistoryTO rhto, Locale loc, CustomerTO cto) throws BusinessException {
        TaskDelegate tdel = new TaskDelegate();
        String response = "";
        String addInfo = "";
        
        String comment = rhto.getComment();
        if (comment!=null && !comment.trim().equals("")){
            if (cto.getBoolCanSeeTechComments()) {
                Timestamp creationDate = rhto.getDate();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(creationDate.getTime());
                
                c.set(Calendar.HOUR, 23);
                c.set(Calendar.MINUTE, 59);
                c.set(Calendar.SECOND, 59);
                rhto.setDate(new Timestamp(c.getTimeInMillis()));
                
                addInfo = "\n" + tdel.getTechCommentsFromTask(rhto, cto);
            }
            response = StringUtil.formatWordToJScript(comment + addInfo);
        }

        return response;
    }

    
    /**
	 * Refresh the deadline information.
	 */
	private void refreshDeadLine(HistReqForm frm, HttpServletRequest request, Locale loc) throws BusinessException{
	    RequirementDelegate rdel = new RequirementDelegate();
	    String mask = super.getCalendarMask(request);
	    
	    RequirementTO rto = new RequirementTO(frm.getReqId());
		rto = rdel.getRequirement(rto);
		if (rto.getSuggestedDate()!=null){
		    frm.setDeadlineSuggested(DateUtil.getDate(rto.getSuggestedDate(), mask, loc));    
		} else {
		    frm.setDeadlineSuggested(this.getBundleMessage(request, "label.requestHistory.emptySuggDeadline"));
		}
		
		Timestamp deadline = rto.getDeadlineDate();
		if (deadline!=null){
		    frm.setDeadlineDateTime(DateUtil.getDate(deadline, mask, loc));  
		} else {
		    frm.setDeadlineDateTime(this.getBundleMessage(request, "label.requestHistory.emptyEstiDeadline"));
		}
	}
}
