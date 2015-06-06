package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CompanyTO;
import com.pandora.delegate.CompanyDelegate;
import com.pandora.gui.struts.form.CompanyForm;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.helper.SessionUtil;


public class CompanyAction extends GeneralStrutsAction {

	CompanyDelegate cdel = new CompanyDelegate();

	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCompany";
		this.clearForm(form, request);

		this.refresh(mapping, form, request, response);
		return mapping.findForward(forward);
	}
	
	
	public ActionForward editCompany(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showCompany";

	    try {
	    	CompanyForm cfrm = (CompanyForm)form;
			this.clearMessages(request);
	        
			//set current operation status for Updating	
			cfrm.setSaveMethod(CompanyForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
	        
			//get a company object from data base
			CompanyTO filter = new CompanyTO();
			filter.setId(cfrm.getId());
			CompanyTO cto = cdel.getCompany(filter);
			
			//put the data into html fields
			this.getActionFormFromTransferObject(cto, cfrm, request);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);		    
	    }

	    return mapping.findForward(forward);
	}


	public ActionForward saveCompany(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showCompany";
		String errorMsg = "error.generic.showFormError";
		String succeMsg = "message.success";
		
		try {
			CompanyForm cfrm = (CompanyForm)form;

			//create an object based on html fields
			CompanyTO cto = this.getTransferObjectFromActionForm(cfrm, request);
					
			if (cfrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){
			    errorMsg = "error.generic.insertFormError";
			    succeMsg = "message.company.insert";
			    cdel.insertCompany(cto);
			    this.clearForm(form, request);
			} else {
			    errorMsg = "error.generic.updateFormError";
			    succeMsg = "message.company.update";
			    cdel.updateCompany(cto);
			}
			
			//set success message into http session
			this.setSuccessFormSession(request, succeMsg);
			
			//refresh lists on form...
			this.refresh(mapping, form, request, response);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, errorMsg, e);
		}

		return mapping.findForward(forward);		
	}

	
	public ActionForward removeCompany(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showCompany";

		try {
			CompanyForm cfrm = (CompanyForm)form;
						
			//create an object based on html fields and remove it
			CompanyTO cto = new CompanyTO();
			cto.setId(cfrm.getId());
			cdel.removeCompany(cto);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.company.remove");
			
			//clear form and messages
			this.clearMessages(request);
			this.clearForm(cfrm, request);
			
			//refresh lists on form...
			this.refresh(mapping, form, request, response);
		
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.removeFormError", e);
		}

		return mapping.findForward(forward);		
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showCompany";
	    this.clearForm(form, request);
		this.clearMessages(request);
		return mapping.findForward(forward);		
	}
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
	        	HttpServletRequest request, HttpServletResponse response){
	    String forward = "showCompany";
	    try {
		    //create a list of companies
	        Vector<CompanyTO> cList = cdel.getCompanyList();
		    request.getSession().setAttribute("companyList", cList);
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);		    
	    }

		return mapping.findForward(forward);
	}


   private CompanyTO getTransferObjectFromActionForm(CompanyForm cfrm, HttpServletRequest request) {
	   CompanyTO response = new CompanyTO();
       response.setId(cfrm.getId());
       response.setFullName(cfrm.getFullName());
       response.setName(cfrm.getName());
       response.setCompanyNumber(cfrm.getCompanyNumber());
       response.setAddress(cfrm.getAddress());
       response.setCity(cfrm.getCity());       
       response.setStateProvince(cfrm.getStateProvince());
       response.setCountry(cfrm.getCountry());      
       return response;
   }
	
   private void getActionFormFromTransferObject(CompanyTO cto, CompanyForm cfrm, HttpServletRequest request) {
       cfrm.setId(cto.getId());
       cfrm.setName(cto.getName());
       cfrm.setFullName(cto.getFullName());
       cfrm.setCompanyNumber(cto.getCompanyNumber());
       cfrm.setAddress(cto.getAddress());
       cfrm.setCity(cto.getCity());       
       cfrm.setStateProvince(cto.getStateProvince());
       cfrm.setCountry(cto.getCountry());
   }	
	
	
	private void clearForm(ActionForm form, HttpServletRequest request){
		CompanyForm frm = (CompanyForm)form;
		frm.clear();		
		frm.setSaveMethod(CompanyForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
	}
	
}
