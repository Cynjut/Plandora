package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.InvoiceItemTO;
import com.pandora.InvoiceStatusTO;
import com.pandora.InvoiceTO;
import com.pandora.MetaFieldTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.InvoiceDelegate;
import com.pandora.delegate.InvoiceStatusDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.MetaFieldNumericTypeException;
import com.pandora.gui.struts.form.InvoiceForm;
import com.pandora.gui.struts.form.SurveyForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class InvoiceAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    CategoryDelegate cdel = new CategoryDelegate();
	    InvoiceStatusDelegate isdel = new InvoiceStatusDelegate(); 
		String forward = "showInvoice";
		ProjectDelegate pdel = new ProjectDelegate();
		
		try {
			InvoiceForm frm = (InvoiceForm)form;
			frm.clear();
			
			UserTO uto = SessionUtil.getCurrentUser(request);			
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), false);
			frm.setProjectName(pto.getName());
			boolean isAllowed = (pto.isLeader(uto.getId()));
			
			if (!isAllowed ) {
				UserDelegate udel = new UserDelegate();
				ResourceTO rto = new ResourceTO(uto.getId());
				rto.setProject(pto);
				rto = udel.getResource(rto);
				isAllowed = rto.getBoolCanSeeInvoice();
			}
			
			if (isAllowed) {
			    Vector<CategoryTO> catlist = cdel.getCategoryListByType(CategoryTO.TYPE_INVOICE, pto, false);
				request.getSession().setAttribute("categoryList", catlist);
				
				Vector<InvoiceStatusTO> islist = isdel.getList();
				request.getSession().setAttribute("invoiceStatusList", islist);
				
				request.getSession().setAttribute("invoiceList", new Vector<InvoiceTO>());
				request.getSession().setAttribute("invoiceItemList", new Vector<InvoiceItemTO>());
				request.getSession().setAttribute("metaFieldList", new Vector<MetaFieldTO>());	   
				
				this.refresh(mapping, form, request, response);
				
			} else {
				forward = "home";
				throw new BusinessException("The user doesn't have permission to access this form");
			}
			
		} catch(Exception e){
		   this.setErrorFormSession(request, "error.invoiceForm.showForm", e);
		}	    
	    
		return mapping.findForward(forward);		
	}

	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		this.reset(form);
		return mapping.findForward("showInvoice");		
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showInvoice";
		InvoiceStatusDelegate isd = new InvoiceStatusDelegate();

		try {
			InvoiceForm frm = (InvoiceForm)form;
			frm.clear();
			InvoiceStatusTO isto = isd.getInitialStatus();
			
			frm.setId("");
			frm.setInvoiceStatusId(isto.getId());
			
			frm.clearInvoiceItemsToBeRemoved();
			frm.clearInvoiceItemsToBeUpdated();
			this.prepareForm(mapping, form, request, response);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.invoiceForm.showForm", e);
		}
		
		return mapping.findForward(forward);		
	}

	
	public ActionForward showEditInvoiceItem(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){		
		InvoiceForm frm = (InvoiceForm)form;
		String editItemId = frm.getEditInvoiceItemId();
		if (editItemId!=null && !editItemId.trim().equals("")) {
			frm.addInvoiceItemsToBeUpdated(editItemId);
		}
		frm.setShowEditInvoiceItem("on");
	    return mapping.findForward("showInvoice");		
	}
	
	
	@SuppressWarnings("unchecked")
	public ActionForward removeInvoiceItem(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){		
		String forward = "showInvoice";
	    try {
		    this.clearMessages(request);
		    InvoiceForm frm = (InvoiceForm)form;
		    this.reset(form);

		    @SuppressWarnings("rawtypes")
			Vector<InvoiceItemTO> iList = (Vector)request.getSession().getAttribute("invoiceItemList");
		    if (iList!=null && frm.getRemovedInvoiceItemId()!=null){
		        Iterator<InvoiceItemTO> i = iList.iterator();
		        while(i.hasNext()){
		            InvoiceItemTO ito = i.next();
		            if (ito.getId().equals(frm.getRemovedInvoiceItemId())){
		                request.getSession().removeAttribute("invoiceItemList");
		                iList.remove(ito);
		                Vector<InvoiceItemTO> temp = new Vector<InvoiceItemTO>();
		                temp.addAll(iList);
		                request.getSession().setAttribute("invoiceItemList", temp);
		                frm.addInvoiceItemsToBeRemoved(ito.getId());		                
		                break;
		            }	            
		        }
		        
		        //summarize total net...
		        InvoiceItemAction action  =new InvoiceItemAction();
		        action.summarize(request);
		    }	    
		    
	    } catch(Exception e){
	        this.setErrorFormSession(request, "error.invoiceForm.showForm", e);
	    }
	    return mapping.findForward(forward);
	}
	
	
	public ActionForward saveInvoice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showInvoice";
		String errorMsg = "error.invoiceForm.showForm";
		String succeMsg = "message.success";

		try {
			InvoiceForm frm = (InvoiceForm)form;
			this.reset(form);

			InvoiceDelegate del = new InvoiceDelegate();
		    InvoiceTO to = this.getTransferObjectFromActionForm(frm, request);
		    
		    if (frm.getSaveMethod().equals(InvoiceForm.INSERT_METHOD)) {
			    errorMsg = "error.invoiceForm.insert";
			    succeMsg = "message.insertInvoice";
			    del.insertInvoice(to);
			} else {
			    errorMsg = "error.invoiceForm.update";
			    succeMsg = "message.updateInvoice";
			    del.updateInvoice(to);
			}
		
			this.clear(mapping, form, request, response );				
			this.setSuccessFormSession(request, succeMsg);				

		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);			
		    frm.setSaveMethod(InvoiceForm.INSERT_METHOD, uto);
		        			
		} catch(MetaFieldNumericTypeException e){
			this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}	    
		return mapping.findForward(forward);	    
	}

	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showInvoice";
	    
		try {
		    InvoiceForm frm = (InvoiceForm)form;
			this.reset(form);
		    InvoiceDelegate del = new InvoiceDelegate();
		    
		    if (frm.getProjectId()!=null) {
				Vector<InvoiceTO> sList = del.getInvoiceList(frm.getProjectId(), false);
				request.getSession().setAttribute("invoiceList", sList);		    			    	
		    }
		    
		    
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    frm.setSaveMethod(InvoiceForm.INSERT_METHOD, uto);
				
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.invoiceForm.showForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}
	
	
	public ActionForward editInvoice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showInvoice";
	    InvoiceDelegate del = new InvoiceDelegate();
	    MetaFieldDelegate mfdel = new MetaFieldDelegate();
	    
		try {
			InvoiceForm frm = (InvoiceForm)form;
			this.reset(form);
			InvoiceTO ito = del.getInvoice(new InvoiceTO(frm.getId()));		    
		    this.getActionFormFromTransferObject(ito, frm, request);
		    
			if (frm.getId()!=null && !frm.getId().equals("")) {
				Vector<MetaFieldTO> list = mfdel.getListByProjectAndContainer(frm.getProjectId(), frm.getCategoryId(), MetaFieldTO.APPLY_TO_INVOICE);
				request.getSession().setAttribute("metaFieldList", list);		    
			} else {
			    request.getSession().setAttribute("metaFieldList", new Vector<MetaFieldTO>());
			}
		    
		    request.getSession().setAttribute("invoiceItemList", ito.getItemsList());
		    frm.setSaveMethod(InvoiceForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.invoiceForm.showForm", e);
		}
		return mapping.findForward(forward);				
	}
	
	
	public ActionForward removeInvoice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			InvoiceForm frm = (InvoiceForm)form;
			this.reset(form);

			InvoiceDelegate del = new InvoiceDelegate();
			
			InvoiceTO ito = new InvoiceTO();
			ito.setId(frm.getId());
			del.removeInvoice(ito);
			
			frm.clear();
			super.clearMessages(request);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeInvoice");
			this.refresh(mapping, form, request, response );
			
		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);
		    frm.setSaveMethod(SurveyForm.INSERT_METHOD, uto);
		
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.invoiceForm.remove", e);
		}
		
	    return mapping.findForward("showInvoice");
	}
	
	
	private void getActionFormFromTransferObject(InvoiceTO to, InvoiceForm frm, HttpServletRequest request) throws BusinessException{
		UserDelegate udel = new UserDelegate();
		
		Locale loc = SessionUtil.getCurrentLocale(request);
	    frm.setId(to.getId());
	    frm.setCategoryId(to.getCategory().getId());
	    frm.setContact(to.getContact());
	    frm.setDescription(to.getDescription());
	    if (to.getDueDate()!=null){
	    	frm.setDueDate(DateUtil.getDate(to.getDueDate(), super.getCalendarMask(request), loc));	
	    }
	    if (to.getInvoiceDate()!=null){
	    	frm.setInvoiceDate(DateUtil.getDate(to.getInvoiceDate(), super.getCalendarMask(request), loc));	
	    }
	    frm.setInvoiceStatusId(to.getInvoiceStatus().getId());
	    frm.setInvoiceNumber(to.getInvoiceNumber());
	    frm.setName(to.getName());
	    frm.setProjectId(to.getProject().getId());
	    frm.setProjectName(to.getProject().getName());
	    frm.setPurchaseOrder(to.getPurchaseOrder());
	    
		Locale currLoc = udel.getCurrencyLocale();	
	    String total = StringUtil.getCurrencyValue((to.getTotal().floatValue()/100), currLoc);
	    frm.setTotalInvoice(total);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private InvoiceTO getTransferObjectFromActionForm(InvoiceForm frm, HttpServletRequest request) throws MetaFieldNumericTypeException{
		InvoiceTO ito = new InvoiceTO();
		Locale loc = SessionUtil.getCurrentLocale(request);
        ito.setId(frm.getId());
        ito.setCreationDate(DateUtil.getNow());
        ito.setDescription(frm.getDescription());
        ito.setName(frm.getName());
        ito.setCategory(new CategoryTO(frm.getCategoryId()));
        ito.setContact(frm.getContact());
        ito.setDueDate(DateUtil.getDateTime(frm.getDueDate(), super.getCalendarMask(request), loc));
        ito.setFinalDate(null);
        ito.setInvoiceDate(DateUtil.getDateTime(frm.getInvoiceDate(), super.getCalendarMask(request), loc));
        ito.setInvoiceNumber(frm.getInvoiceNumber());
        ito.setInvoiceStatus(new InvoiceStatusTO(frm.getInvoiceStatusId()));
        ito.setProject(new ProjectTO(frm.getProjectId()));
        ito.setPurchaseOrder(frm.getPurchaseOrder());
        
        ito.setHandler(SessionUtil.getCurrentUser(request));
        
        ito.setItemsToBeRemoved(frm.getInvoiceItemsToBeRemoved());
        ito.setItemsToBeUpdated(frm.getInvoiceItemsToBeUpdated());
        
		Vector<InvoiceItemTO> iList = (Vector)request.getSession().getAttribute("invoiceItemList");
        ito.setItemsList(iList);
        
		Vector<MetaFieldTO> metaFieldList = (Vector)request.getSession().getAttribute("metaFieldList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, ito);	    
		if (metaFieldList!=null) {
		    frm.setAdditionalFields(ito.getAdditionalFields());
		}
        
		return ito;
	}
	
	
	private void reset(ActionForm form){
		InvoiceForm frm = (InvoiceForm)form;
		frm.setShowEditInvoiceItem("off");
		frm.setEditInvoiceItemId("");
	}
	
}
