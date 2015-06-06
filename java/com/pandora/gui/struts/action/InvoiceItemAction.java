package com.pandora.gui.struts.action;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.InvoiceItemTO;
import com.pandora.InvoiceTO;
import com.pandora.TransferObject;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.InvoiceForm;
import com.pandora.gui.struts.form.InvoiceItemForm;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class InvoiceItemAction extends GeneralStrutsAction {

	public ActionForward showEditPopup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showInvoiceItemEdit";
		UserDelegate udel = new UserDelegate();
		
		try {
			Vector<TransferObject> v = new Vector<TransferObject>();
			for (int i=1; i<=4; i++){
				String label = super.getBundleMessage(request, "label.invoiceForm.itemList.type." + i);
				v.addElement(new TransferObject(i+"", label));	
			}
			request.getSession().setAttribute("invoiceTypes", v);
			
			InvoiceItemForm frm = (InvoiceItemForm)form;
			frm.clear();
			
			Locale userLoc = SessionUtil.getCurrentLocale(request);
			Locale currLoc = udel.getCurrencyLocale();	
			NumberFormat nf = NumberFormat.getCurrencyInstance(currLoc);
			String cs = nf.getCurrency().getSymbol();
			frm.setCurrencySymbol(cs);
			
			if (frm.getEditInvoiceItemId()!=null && !frm.getEditInvoiceItemId().equals("")) {
				InvoiceItemTO ito = this.getInvoiceItem(frm.getEditInvoiceItemId(), request);
				if (ito!=null) {
				    frm.setType(ito.getType().toString());
				    frm.setAmount(ito.getAmount().toString());
				    frm.setItemName(ito.getItemName());
				    frm.setId(ito.getId());
				    frm.setInvoiceId(ito.getInvoice().getId());
				    frm.setPrice(StringUtil.getFloatToString((ito.getPrice().floatValue()/100) , userLoc));
				}
			}
			
			
		} catch(Exception e){
    		this.setErrorFormSession(request, "error.invoiceForm.showForm", e);			
		}
		
		return mapping.findForward(forward);
	}

	
	
	@SuppressWarnings("unchecked")
	public ActionForward saveInvoiceItem(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			InvoiceItemForm frm = (InvoiceItemForm)form;
			
			Locale loc = SessionUtil.getCurrentLocale(request);
			
			if (frm.getEditInvoiceItemId()!=null && !frm.getEditInvoiceItemId().equals("")) {
				InvoiceItemTO ito = this.getInvoiceItem(frm.getEditInvoiceItemId(), request);
				if (ito!=null) {
				    this.fillInvoiceItem(frm, loc, ito);
				}
				
			} else {
				InvoiceItemTO ito = new InvoiceItemTO();
				Vector<InvoiceItemTO> iList = (Vector<InvoiceItemTO>)request.getSession().getAttribute("invoiceItemList");
			    ito.setId("NEW_" + (iList.size()+1));
			    ito.setInvoice(new InvoiceTO(frm.getInvoiceId()));
			    this.fillInvoiceItem(frm, loc, ito);
			    
			    iList.addElement(ito);
			    request.getSession().setAttribute("invoiceItemList", iList);		    					
			}
			
			this.summarize(request);
			
    	} catch(Exception e){
    		this.setErrorFormSession(request, "error.invoiceForm.showForm", e);
    	}

		return mapping.findForward("goToInvoiceForm");		
	}



	private void fillInvoiceItem(InvoiceItemForm frm, Locale loc, InvoiceItemTO ito) {
		ito.setType(new Integer(frm.getType()));
		ito.setAmount(new Integer(frm.getAmount()));
		ito.setItemName(frm.getItemName());
		
		ito.setPrice(StringUtil.getStringToCents(frm.getPrice(), loc));
		if (ito.getType().intValue()==1 || ito.getType().intValue()==2) {
			ito.setTypeIndex(new Integer("1"));
		} else {
			ito.setTypeIndex(new Integer("-1"));
		}
	}

	
	
	private InvoiceItemTO getInvoiceItem(String iiId, HttpServletRequest request){
		InvoiceItemTO response = null;
	    @SuppressWarnings("unchecked")
		Vector<InvoiceItemTO> iList = (Vector<InvoiceItemTO>)request.getSession().getAttribute("invoiceItemList");
	    if (iList!=null) {
		    Iterator<InvoiceItemTO> i = iList.iterator();
		    while(i.hasNext()) {
		    	InvoiceItemTO ito = (InvoiceItemTO)i.next();
		    	if (ito.getId().equals(iiId)) {
		    		response = ito;
		    		break;
		    	}
		    }	    	
	    }
		return response;
	}

	
	public void summarize(HttpServletRequest request) throws BusinessException{
		long total = 0;
		UserDelegate udel = new UserDelegate();
		Locale loc = udel.getCurrencyLocale();
		
	    @SuppressWarnings("unchecked")
		Vector<InvoiceItemTO> iList = (Vector<InvoiceItemTO>)request.getSession().getAttribute("invoiceItemList");
	    if (iList!=null) {
		    Iterator<InvoiceItemTO> i = iList.iterator();
		    while(i.hasNext()) {
		    	InvoiceItemTO ito = (InvoiceItemTO)i.next();
		    	total += (ito.calculatePrice() * ito.getAmount().intValue());
		    }
		    
		    InvoiceForm iform = (InvoiceForm)request.getSession().getAttribute("invoiceForm");
		    String totstr = StringUtil.getCurrencyValue(((float)(float)total / 100), loc);
		    iform.setTotalInvoice(totstr);
	    }
	}
	
}
