package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.MetaFormTO;
import com.pandora.UserTO;
import com.pandora.exception.BusinessException;
import com.pandora.delegate.MetaFormDelegate;
import com.pandora.gui.struts.form.MetaFormForm;
import com.pandora.helper.SessionUtil;

/**
 */
public class MetaFormAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showMetaForm";

		this.clearForm(form, request);
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		MetaFormForm frm = (MetaFormForm)form;
		frm.setSaveMethod(MetaFormForm.INSERT_METHOD, uto);
	
	    this.refresh(mapping, form, request, response);    

	    return mapping.findForward(forward);		
	}
    
	
	public ActionForward saveMetaForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showMetaForm";
		String errorMsg = "error.metaform.showForm";
		String succeMsg = "message.success";
		
		try {
		    MetaFormForm frm = (MetaFormForm)form;
		    MetaFormDelegate del = new MetaFormDelegate();
		    MetaFormTO to = this.getTransferObjectFromActionForm(frm, request);
			
			if (frm.getSaveMethod().equals(MetaFormForm.INSERT_METHOD)){
			    errorMsg = "error.metaform.insert";
			    succeMsg = "message.insertMetaForm";
			    del.insertMetaForm(to);
			} else {
			    errorMsg = "error.metaform.update";
			    succeMsg = "message.updateMetaForm";
			    del.updateMetaForm(to);
			}
		
			this.clear(mapping, form, request, response );				
			this.setSuccessFormSession(request, succeMsg);				

		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);			
		    frm.setSaveMethod(MetaFormForm.INSERT_METHOD, uto);
		        			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}	    
		return mapping.findForward(forward);	    
	}

	
	private void clearForm(ActionForm form, HttpServletRequest request){
	    MetaFormForm frm = (MetaFormForm)form;		
	    frm.clear();
	    this.clearMessages(request);
	}
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showMetaForm";
		try {	    		    
		    MetaFormDelegate del = new MetaFormDelegate();
		    Vector mfList = del.getMetaFormList();
		    request.getSession().setAttribute("metaFormList", mfList);		    		    
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.metaform.showForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}
	
	
    public ActionForward editMetaForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showMetaForm";
	    MetaFormDelegate del = new MetaFormDelegate();
		
		try {
		    MetaFormForm frm = (MetaFormForm)form;
		    MetaFormTO mfto = del.getObject(new MetaFormTO(frm.getId()));
		    this.getActionFormFromTransferObject(mfto, frm, request);
		    frm.setSaveMethod(MetaFormForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));		    		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.metaform.showForm", e);
		}
		
		return mapping.findForward(forward);				
	}
	
    
	public ActionForward removeMetaForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showMetaForm";
		try {
		    MetaFormForm frm = (MetaFormForm)form;
		    MetaFormDelegate del = new MetaFormDelegate();
			this.clearForm(frm, request);

			MetaFormTO mfto = new MetaFormTO(frm.getId());
			del.removeMetaForm(mfto);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeMetaForm");
			this.refresh(mapping, form, request, response );
			
		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);
		    frm.setSaveMethod(MetaFormForm.INSERT_METHOD, uto);
		
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.metaform.remove", e);
		}
	    
		return mapping.findForward(forward);	    
	}
	
    
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showMetaForm";		
		this.prepareForm(mapping, form, request, response);
		return mapping.findForward(forward);		
	}
	
    
	private void getActionFormFromTransferObject(MetaFormTO to, MetaFormForm frm, HttpServletRequest request){
	    frm.setId(to.getId());
	    frm.setName(to.getName());
        frm.setFilterColId(to.getFilterColId());
        frm.setGridNumber(to.getGridNumber());	    
	    frm.setViewableCols(to.getViewableCols());
        frm.setJsAfterLoad(to.getJsAfterLoad());
        frm.setJsAfterSave(to.getJsAfterSave());
        frm.setJsBeforeSave(to.getJsBeforeSave());
	}
	
    
	private MetaFormTO getTransferObjectFromActionForm(MetaFormForm frm, HttpServletRequest request){
	    MetaFormTO to = new MetaFormTO(frm.getId());
        to.setName(frm.getName());
        to.setFilterColId(frm.getFilterColId());
        to.setGridNumber(frm.getGridNumber());
        to.setViewableCols(frm.getViewableCols());
        to.setJsAfterLoad(frm.getJsAfterLoad());
        to.setJsAfterSave(frm.getJsAfterSave());
        to.setJsBeforeSave(frm.getJsBeforeSave());
	    return to;
	}
	
}
