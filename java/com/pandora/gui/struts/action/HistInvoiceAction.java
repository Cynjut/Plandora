package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.InvoiceHistoryTO;
import com.pandora.delegate.InvoiceDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.HistInvoiceForm;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

public class HistInvoiceAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {	
		String forward = "showInvHistory";

		try {		
			HistInvoiceForm frm = (HistInvoiceForm) form;
			this.clearMessages(request);

			//get all Information about request history 
			InvoiceDelegate del = new InvoiceDelegate();
			Vector histList = del.getHistory(frm.getInvId());
			request.getSession().setAttribute("invHistoryList", histList);

		} catch(Exception e){
			LogUtil.log(this, LogUtil.LOG_ERROR, "Show InvoiceHistory error", e);
		}
		return mapping.findForward(forward);
	}
	
	
	public ActionForward viewComment(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {	
		String forward = "showInvComment";
        try {		
	    	HistInvoiceForm frm = (HistInvoiceForm) form;
	    	
	    	Vector invList = (Vector)request.getSession().getAttribute("invHistoryList");
			String selectedItem = frm.getSelectedIndex();
			if (selectedItem!=null) {
			    int index = Integer.parseInt(selectedItem);
			    InvoiceHistoryTO ihto = (InvoiceHistoryTO)invList.get(index);
			    frm.setHistoryContent(this.getContent(ihto));
			}
						
		} catch(Exception e){
		    LogUtil.log(this, LogUtil.LOG_ERROR, "Show InvoiceHistory error", e);
		}
		
    	return mapping.findForward(forward);	    	
		
		
	}
		

    private String getContent(InvoiceHistoryTO ihto) throws BusinessException {
        String response = "";        
        String content = ihto.getDescription();
        if (content!=null && !content.trim().equals("")){
            response = StringUtil.formatWordToJScript(content);
        }

        return response;
    }
}
