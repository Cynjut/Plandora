package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.RiskHistoryTO;
import com.pandora.delegate.RiskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.HistRiskForm;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

public class HistRiskAction extends GeneralStrutsAction {

    
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response) {	
		String forward = "showHistory";
		
        try {		
            HistRiskForm frm = (HistRiskForm) form;
			this.clearMessages(request);

			RiskDelegate rdel = new RiskDelegate();
			Vector list = rdel.getHistory(frm.getRiskId());
			request.getSession().setAttribute("riskHistoryList", list);
									
		} catch(BusinessException e){
		    LogUtil.log(this, LogUtil.LOG_ERROR, "Show RiskHistory error", e);
		}
		
		return mapping.findForward(forward);
	}

	
	public ActionForward viewContent(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    
	    String forward = "showRiskContent";
        try {		
	    	
            HistRiskForm frm = (HistRiskForm) form;
	    	Vector riskList = (Vector)request.getSession().getAttribute("riskHistoryList");

			String selectedItem = frm.getSelectedIndex();
			if (selectedItem!=null) {
			    int index = Integer.parseInt(selectedItem);
			    RiskHistoryTO rhto = (RiskHistoryTO)riskList.get(index);
			    frm.setHistoryContent(this.getContent(rhto));
			}
						
		} catch(BusinessException e){
		    LogUtil.log(this, LogUtil.LOG_ERROR, "Show RiskHistory error", e);
		}
		
    	return mapping.findForward(forward);	    	
	}
	
	
    private String getContent(RiskHistoryTO rhto) throws BusinessException {
        String response = "";        
        String content = rhto.getContent();
        if (content!=null && !content.trim().equals("")){
            response = StringUtil.formatWordToJScript(content);
        }

        return response;
    }
}
