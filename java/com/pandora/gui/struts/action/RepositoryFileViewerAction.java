package com.pandora.gui.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.bus.convert.Converter;
import com.pandora.gui.struts.form.RepositoryFileViewerForm;
import com.pandora.helper.SessionUtil;

public class RepositoryFileViewerAction extends GeneralStrutsAction {

	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "repositoryFileViewer";
		try {
			RepositoryFileViewerForm frm = (RepositoryFileViewerForm)form;		

			String prjId = frm.getProjectId();
			String output = frm.getOutputType();			
			String newpath = frm.getNewpath();

			//Locale loc = SessionUtil.getCurrentLocale(request);
			//String locale = loc.toString();
			//if (!locale.equalsIgnoreCase("es_ES") && !locale.equalsIgnoreCase("pt_BR") && !locale.equalsIgnoreCase("ru_RU")) {
			//	locale = "en_US";
			//}
			//frm.setUserLocale(locale);
			frm.setUserLocale("en_US");
			
			if (output.equals("1")) {
				frm.setFormTitle(super.getBundleMessage(request, "title.mindMapForm") + " [" + newpath + "]");
			} else {
				frm.setFormTitle("");
			}
			
		    String uri = request.getRequestURI();
		    uri = uri.substring(0, uri.indexOf("/do") + 3);
			
			if (output.equals(Converter.OUTPUT_MIND_MAP_VIEWER+"")) {
			    frm.setServerURI(SessionUtil.getUri(request) + uri + "/showRepositoryViewerCustomer?operation=showViewerOutput&projectId=" + 
		    			prjId + "&outputType=" + output + "&newpath=" + newpath);				
			} else {
			    frm.setServerURI(SessionUtil.getUri(request) + uri + "/showRepositoryViewerCustomer?projectId=" + 
		    			prjId + "&operation=showViewerOutput&outputType=" + output + "&newpath=" + newpath);
			}
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(forward);		
	}

}
