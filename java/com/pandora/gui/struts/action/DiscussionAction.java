package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.DiscussionDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.DiscussionForm;
import com.pandora.gui.struts.form.RiskForm;
import com.pandora.helper.SessionUtil;

public class DiscussionAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showDiscussion";

		//this.clearForm(form, request);
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		DiscussionForm frm = (DiscussionForm)form;
		frm.setSaveMethod(RiskForm.INSERT_METHOD, uto);
	
	    this.refresh(mapping, form, request, response);    
	    //this.refreshAuxiliarList(mapping, form, request, response);
	    
		return mapping.findForward(forward);		
	}
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showDiscussion";

		try {	    		    
			DiscussionForm frm = (DiscussionForm)form;
		    DiscussionDelegate ddel = new DiscussionDelegate();
		    ProjectTO pto = null;
		    Vector cList = new Vector();
		    Vector dList = new Vector();
		    
		    if (frm.getProjectId()!=null && !frm.getProjectId().equals("")) {
		    	pto = new ProjectTO(frm.getProjectId());	
		    }
		    
		    CategoryTO cto = new CategoryTO();
		    CategoryDelegate cdel = new CategoryDelegate();
		    if (pto!=null) {
		    	cList = cdel.getCategoryListByType(CategoryTO.TYPE_DISCUSSION, pto, false);
		    	if (cList.size()>0) {
		    		cto = (CategoryTO)cList.elementAt(0);	
		    	}
		    	
			    dList = ddel.getDiscussionList(pto, cto);
		    }
		    
		    request.getSession().setAttribute("discussionList", dList);		    
		    request.getSession().setAttribute("categoryList", cList);


		} catch(BusinessException e){
		  this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    
		return mapping.findForward(forward);	    
	}
	
}
