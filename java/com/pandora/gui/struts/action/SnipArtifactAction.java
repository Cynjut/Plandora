package com.pandora.gui.struts.action;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.UserTO;
import com.pandora.bus.snip.SnipArtifact;
import com.pandora.bus.snip.SnipArtifactBUS;
import com.pandora.gui.struts.form.SnipArtifactForm;
import com.pandora.helper.SessionUtil;

public class SnipArtifactAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showSnipArtifact";
		try {
			SnipArtifactForm frm = (SnipArtifactForm)form;
			SnipArtifact snip = SnipArtifactBUS.getSnipArtifactClass(frm.getSnip());
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			snip.setHandler(uto);
			
			frm.setPopupTitle(super.getBundleMessage(request, snip.getUniqueName()));
			frm.setHtmlFormBody(snip.getHtmlBody(request));
			
		} catch (Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(forward);
	}

	
	public ActionForward submitSnip(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			SnipArtifactForm frm = (SnipArtifactForm)form;
			SnipArtifact snip = SnipArtifactBUS.getSnipArtifactClass(frm.getSnip());
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			snip.setHandler(uto);
			String content = snip.submit(request);
			
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        PrintWriter out = response.getWriter();  
	        out.println(content);  
	        out.flush();
			
		} catch (Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return null;
	}

}
