package com.pandora.gui.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.PlanningTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.bus.artifact.HtmlArtifactExport;
import com.pandora.bus.snip.SnipArtifact;
import com.pandora.bus.snip.SnipArtifactBUS;
import com.pandora.delegate.ArtifactTemplateDelegate;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ArtifactForm;
import com.pandora.gui.struts.form.UserForm;
import com.pandora.gui.taglib.decorator.RepositoryEntityRadioBoxDecorator;
import com.pandora.helper.SessionUtil;

public class ArtifactAction extends GeneralStrutsAction {

	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showArtifact";
		ArtifactTemplateDelegate atdel = new ArtifactTemplateDelegate();
		ProjectDelegate prdel = new ProjectDelegate();
		
		try {
			String backToCaller = null;			
			ArtifactForm frm = (ArtifactForm)form;
			frm.setShowSaveAsPopup("off");
			frm.setOnlyFolders("on");
			frm.setMultiple("off");
	        frm.setSaveMethod(UserForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
	        request.getSession().removeAttribute(RepositoryEntityRadioBoxDecorator.REPOSITORY_SELECTED_PATH);

	        frm.setSnipList(this.getSnipList(request));
	        frm.setSnipHtmlDimension(this.getSnipDimension());
	        
	        UserTO uto = SessionUtil.getCurrentUser(request);
	        frm.setLang(uto.getLocale().getLanguage());

			if (frm.getPlanningId()!=null) {
				PlanningDelegate pdel = new PlanningDelegate();
				PlanningTO pto = pdel.getSpecializedObject(new PlanningTO(frm.getPlanningId()));
				if (pto instanceof TaskTO) {					
					backToCaller = "window.location='../do/manageResTask?operation=prepareForm&projectId=" + 
									frm.getProjectId() + "&resourceId=" + uto.getId() + "&taskId=" + pto.getId() + "'";
				}
			}

			frm.setBackToCaller(backToCaller);
			if (frm.getTemplateId()!=null && !frm.getTemplateId().equals("")) {
		        ProjectTO project = prdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
				frm.setBody(atdel.getTemplateContent(frm.getTemplateId(), project, uto, frm.getName()));	
			}			
			
		} catch(Exception e){
			   this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);
	}


	private String getSnipDimension() throws BusinessException {
		String response = "";
		UserDelegate udel = new UserDelegate();
		UserTO root = udel.getRoot();
		
		String classList = root.getPreference().getPreference(PreferenceTO.SNIP_ARTIFACT_BUS_CLASS);
		if (classList!=null) {
			String[] list = classList.split(";");
			if (list!=null) {
				for (int i=0; i<list.length; i++) {
					SnipArtifact sa = SnipArtifactBUS.getSnipArtifactClass(list[i].trim());
					if (i>0) {
						response = response + " else ";	
					}					
					response = response + "if (value == '" + sa.getClass().getName() + "') {\n";
					response = response + "   w = " + sa.getWidth() + "; h = " + sa.getHeight() + ";\n";
					response = response + "}";					
				}
			}
		}
		return response;
	}


	private String getSnipList(HttpServletRequest request) throws BusinessException {
		String response = "";
		UserDelegate udel = new UserDelegate();
		UserTO root = udel.getRoot();
		
		String classList = root.getPreference().getPreference(PreferenceTO.SNIP_ARTIFACT_BUS_CLASS);
		if (classList!=null) {
			String[] list = classList.split(";");
			if (list!=null) {
				response = response + "\"";
				for (int i=0; i<list.length; i++) {
					if (i>0) {
						response = response + ";";	
					}
					SnipArtifact sa = SnipArtifactBUS.getSnipArtifactClass(list[i].trim());
					response = response + super.getBundleMessage(request, sa.getUniqueName()) + "=" + sa.getClass().getName();
				}
				response = response + "\"";
			}
		}
		return response;
	}


	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showArtifact";
		ArtifactForm frm = (ArtifactForm)form;
		frm.setShowSaveAsPopup("off");
		return mapping.findForward(forward);		
	}
	
	public ActionForward saveAsArtifact(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showArtifact";
		ArtifactForm frm = (ArtifactForm)form;
		frm.setShowSaveAsPopup("on");
		return mapping.findForward(forward);		
	}

	
	public ActionForward editArtifact(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showArtifact";
		RepositoryDelegate rdel = new RepositoryDelegate();
		ProjectDelegate prdel = new ProjectDelegate();
		
		try {		
			ArtifactForm frm = (ArtifactForm)form;
			frm.setShowSaveAsPopup("off");
			frm.setOnlyFolders("on");
			frm.setMultiple("off");
	        frm.setSaveMethod(UserForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
	        frm.setBackToCaller(null);
	        
	        request.getSession().removeAttribute(RepositoryEntityRadioBoxDecorator.REPOSITORY_SELECTED_PATH);
	        
	        UserTO uto = SessionUtil.getCurrentUser(request);
	        frm.setLang(uto.getLocale().getLanguage());
			
			if (frm.getEditPath()!=null && !frm.getEditPath().equals("")) {
		        ProjectTO project = prdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
		        RepositoryFileTO rfto = rdel.getFile(project, frm.getEditPath(), frm.getEditPathRev());
		        if (rfto.getArtifactTemplateType()!=null && 
		        		rfto.getArtifactTemplateType().equals(HtmlArtifactExport.class.getName())) {
		        	String payload = new String(rfto.getFileInBytes());
		        	frm.setBody(payload);
		        	frm.setName(rfto.getName());
		        }
			}			
			
		} catch(Exception e){
			   this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(forward);
	}
	
		
}
