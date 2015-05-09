package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.AdditionalFieldTO;
import com.pandora.CustomFormTO;
import com.pandora.MetaFieldTO;
import com.pandora.MetaFormTO;
import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.bus.PreferenceBUS;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.MetaFormDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.CustomFormForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

public class CustomFormAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showCustomForm";		
		try {
		    this.loadPreferences(form, request);
		    
			request.getSession().setAttribute("customList", new Vector<CustomFormTO>());
			request.getSession().setAttribute("metaFormFieldList", new Vector<MetaFieldTO>());

		    this.refresh(mapping, form, request, response);    		    
		    this.clear(mapping, form, request, response);

		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", null);
		}			
		return mapping.findForward(forward);		
	}


	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showCustomForm");
	}
	
	
	@SuppressWarnings("unchecked")
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String forward = "showCustomForm";	
		this.clearMessages(request);		

		CustomFormForm frm = (CustomFormForm)form;
		frm.setSaveMethod(CustomFormForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
		frm.setAfterSuccessfullySave("off");
		
	    Vector<MetaFieldTO> fieldList = (Vector<MetaFieldTO>)request.getSession().getAttribute("metaFormFieldList");
	    Iterator<MetaFieldTO> i = fieldList.iterator();
	    while(i.hasNext()) {
	    	MetaFieldTO mto = i.next();
	    	AdditionalFieldTO afto = frm.getAdditionalField(mto.getId());
	    	if (afto!=null){
	    		afto.setValue("");	
	    	}
	    }
		
		return mapping.findForward(forward);
	}

	public ActionForward editRecord(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String forward = "showCustomForm";
		MetaFormDelegate mdel = new MetaFormDelegate();
		try {
			CustomFormForm frm = (CustomFormForm)form;
			frm.setAfterSuccessfullySave("off");
			
			CustomFormTO cfto = mdel.getRecord(new CustomFormTO(frm.getId()));
			this.getActionFormFromTransferObject(cfto, frm, request);	
			
			//set current operation status for Updating	
		    frm.setSaveMethod(CustomFormForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
			
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", null);
		}			
        
		return mapping.findForward(forward);	
	}
	
	public ActionForward saveCustomForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showCustomForm";
		String errorMsg = "error.generic.showFormError";
		String succeMsg = "message.success";
		
		try {
			CustomFormForm frm = (CustomFormForm)form;
			frm.setAfterSuccessfullySave("off");
			
			MetaFormDelegate del = new MetaFormDelegate();
		    CustomFormTO to = this.getTransferObjectFromActionForm(frm, request);
			
			if (frm.getSaveMethod().equals(CustomFormForm.INSERT_METHOD)){
			    errorMsg = "error.generic.insertFormError";
			    del.insertRecord(to);
			} else {
			    errorMsg = "error.generic.updateFormError";
			    del.updateRecord(to);
			}
		
			//this.clear(mapping, form, request, response );				
			this.setSuccessFormSession(request, succeMsg);				

			frm.setAfterSuccessfullySave("on");
			
		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);			
		    frm.setSaveMethod(CustomFormForm.INSERT_METHOD, uto);
		    
		    this.refresh(mapping, form, request, response);
		        			
		} catch(Exception e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}	    
		return mapping.findForward(forward);	    
	}
	
	public ActionForward removeRecord(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String forward = "showCustomForm";
		try {
			CustomFormForm frm = (CustomFormForm)form;
			MetaFormDelegate del = new MetaFormDelegate();
		    CustomFormTO to = this.getTransferObjectFromActionForm(frm, request);
		    del.removeRecord(to);
		    
			this.setSuccessFormSession(request, "message.success");
			this.refresh(mapping, form, request, response);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.removeFormError", e);    
		}	    		
		return mapping.findForward(forward);
	}
	
    
	public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		MetaFormDelegate mdel = new MetaFormDelegate();
		MetaFieldDelegate del = new MetaFieldDelegate();
		CustomFormForm frm = (CustomFormForm)form;
		String forward = "showCustomForm";	
		
		try {		
			if (frm.getMetaFormId()!=null && !frm.getMetaFormId().trim().equals("")) {
		
				MetaFormTO metaForm = mdel.getObject(new MetaFormTO(frm.getMetaFormId()));
			    frm.setFormTitle(metaForm.getName());
			    frm.setJsAfterLoad(metaForm.getJsAfterLoad());
			    frm.setJsAfterSave(metaForm.getJsAfterSave());
			    frm.setJsBeforeSave(metaForm.getJsBeforeSave());
			    
			    Vector<MetaFieldTO> list = del.getFieldByMetaForm(frm.getMetaFormId());
			    request.getSession().setAttribute("metaFormFieldList", list);
			    
			    int days = frm.getDaysToHide();
			    Timestamp iniRange = DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, -days);
			    Vector<CustomFormTO> recs = mdel.getRecords(frm.getMetaFormId(), iniRange);
			    request.getSession().setAttribute("customList", recs);
			    
			    request.getSession().setAttribute("customFormRowNumber", metaForm.getGridNumber());
			    
			    if (metaForm.getFilterColId()!=null && !metaForm.getFilterColId().equals("")) {
			    	request.getSession().setAttribute("customFormComboFilterId", metaForm.getFilterColId());	
			    } else {
			    	request.getSession().removeAttribute("customFormComboFilterId");
			    }
			    
			    this.savePreferences(form, request);
			    
			} else {
			    this.setErrorFormSession(request, "error.generic.showFormError", null);
			}
					    
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", null);
		}			
        
		return mapping.findForward(forward);	
    }
    
	
	private void getActionFormFromTransferObject(CustomFormTO to, CustomFormForm frm, HttpServletRequest request){
	    frm.setId(to.getId());
	    frm.setCreationDate(to.getCreationDate());
	    frm.setMetaFormId(to.getMetaForm().getId());
	    frm.setAdditionalFields(to.getAdditionalFields());
	    
	    Vector<MetaFieldTO> fieldList = new Vector<MetaFieldTO>();
	    Iterator<AdditionalFieldTO> i = to.getAdditionalFields().iterator();
	    while(i.hasNext()) {
	    	AdditionalFieldTO afto = i.next();
	    	fieldList.add(afto.getMetaField());
	    }
		request.getSession().setAttribute("metaFormFieldList", fieldList);	    
	}
    
	@SuppressWarnings("unchecked")
	private CustomFormTO getTransferObjectFromActionForm(CustomFormForm frm, HttpServletRequest request){
		CustomFormTO cto = new CustomFormTO();
        cto.setId(frm.getId());
        cto.setMetaForm(new MetaFormTO(frm.getMetaFormId()));
	    cto.setDescription(null);
	    cto.setCreationDate(DateUtil.getNow());

	    Vector<MetaFieldTO> metaFieldList = (Vector<MetaFieldTO>)request.getSession().getAttribute("metaFormFieldList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, cto);
		if (metaFieldList!=null) {
		    frm.setAdditionalFields(cto.getAdditionalFields());
		}
	    
	    return cto;
	}    
	
	
	private void loadPreferences(ActionForm form, HttpServletRequest request){
		CustomFormForm frm = (CustomFormForm)form;
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		frm.setDaysToHideRecords(pto.getPreference(PreferenceTO.CUSTOM_FORM_HIDE_DAYS));
	}

	
	private void savePreferences(ActionForm form, HttpServletRequest request) throws BusinessException{
		CustomFormForm frm = (CustomFormForm)form;
		
		PreferenceBUS pbus = new PreferenceBUS();
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		
		pto.addPreferences(new PreferenceTO(PreferenceTO.CUSTOM_FORM_HIDE_DAYS, frm.getDaysToHide()+"", uto));
		pbus.insertOrUpdate(pto);			
	}
	
}
