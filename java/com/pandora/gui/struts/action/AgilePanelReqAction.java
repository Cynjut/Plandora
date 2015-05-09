package com.pandora.gui.struts.action;

import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.CustomerTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.RequirementStatusDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.AgilePanelReqForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

public class AgilePanelReqAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			AgilePanelReqForm frm = (AgilePanelReqForm)form;
			Vector list = this.getCategoryByProject(frm.getReqProjectId());
			if (list!=null) {
				request.getSession().setAttribute("agilePanelReqCategoryList", list);	
			} else {
				this.setErrorFormSession(request, "error.generic.showFormError", null);	
			}
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);		    
		}		
		return mapping.findForward("showAgilePanelReq");		
	}

	
	public ActionForward refreshCategory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			String projectId = request.getParameter("reqProjectId");
			
			String content = "";
			Vector list = this.getCategoryByProject(projectId);
			if (list!=null) {
				content = HtmlUtil.getComboBox("categoryId", list, "textBox", null);	
			}
	
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        PrintWriter out = response.getWriter();  
	        out.println(content);  
	        out.flush();
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showResHomeForm", e);
		}
		return null;
	}

	
	public ActionForward saveReq(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			AgilePanelReqForm frm = (AgilePanelReqForm)form;
			RequirementDelegate rdel = new RequirementDelegate();
			
		    RequirementStatusDelegate rsdel = new RequirementStatusDelegate();
		    RequirementStatusTO rsto = rsdel.getObjectByStateMachine(RequirementStatusTO.STATE_MACHINE_PLANNED);					    	
			
			RequirementTO rto = new RequirementTO();
			rto.setCreationDate(DateUtil.getNow());
			rto.setDescription(frm.getReqDescription());
			rto.setPriority(new Integer(frm.getPriority()));
			rto.setProject(new ProjectTO(frm.getReqProjectId()));
			rto.setRequirementStatus(rsto);
			
			rto.setCategory(new CategoryTO(frm.getCategoryId()));
			rto.setRequester(new CustomerTO(SessionUtil.getCurrentUser(request).getId()));
			rto.setIsAdjustment(false);
			if (frm.getIteration()!=null && !frm.getIteration().equals("-1")) {
				rto.setIteration(frm.getIteration());	
			}
			rto.setDiscussionTopics(new Vector());
			rdel.insertRequirement(rto);

			this.setSuccessFormSession(request, "message.insertReq");
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.insertReqForm", e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);		    
		}
		return mapping.findForward("goToAgileForm");		
	}
	
	
	private Vector getCategoryByProject(String projectId){
		Vector list = null;		
		try {
			ProjectTO pto = new ProjectTO(projectId);
			CategoryDelegate cdel = new CategoryDelegate();
			list = cdel.getCategoryListByType(CategoryTO.TYPE_REQUIREMENT, pto, false);
		} catch(Exception e) {
			list = null;
		}
		return list;
	}

}
