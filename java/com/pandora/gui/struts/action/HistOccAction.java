package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.OccurrenceHistoryTO;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.HistOccForm;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

/**
 */
public class HistOccAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response) {	
		String forward = "showHistory";
		
        try {		
            HistOccForm frm = (HistOccForm) form;
			this.clearMessages(request);
			
			//get all Information about request history 
			OccurrenceDelegate odel = new OccurrenceDelegate();
			Vector<OccurrenceHistoryTO> list = odel.getHistory(frm.getOccId());
			request.getSession().setAttribute("occHistoryList", list);
									
		} catch(BusinessException e){
		    LogUtil.log(this, LogUtil.LOG_ERROR, "Show OccurrenceHistory error", e);
		}
		
		return mapping.findForward(forward);
	}

	
	public ActionForward viewContent(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    
	    String forward = "showOccContent";
        try {		
	    	
            HistOccForm frm = (HistOccForm) form;
	    	@SuppressWarnings("rawtypes")
			Vector occList = (Vector)request.getSession().getAttribute("occHistoryList");

			String selectedItem = frm.getSelectedIndex();
			if (selectedItem!=null) {
			    int index = Integer.parseInt(selectedItem);
			    OccurrenceHistoryTO ohto = (OccurrenceHistoryTO)occList.get(index);
			    frm.setHistoryContent(this.getContent(ohto));
			}
						
		} catch(BusinessException e){
		    LogUtil.log(this, LogUtil.LOG_ERROR, "Show OccurrenceHistory error", e);
		}
		
    	return mapping.findForward(forward);	    	
	}
	
	
    private String getContent(OccurrenceHistoryTO ohto) throws BusinessException {
        String response = "";        
        String content = ohto.getContent();
        if (content!=null && !content.trim().equals("")){
            response = StringUtil.formatWordToJScript(content);
        }

        return response;
    }
    
}
