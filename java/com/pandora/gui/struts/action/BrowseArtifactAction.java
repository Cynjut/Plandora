package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.ArtifactTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.PlanningBUS;
import com.pandora.bus.artifact.ArtifactExport;
import com.pandora.bus.artifact.ArtifactTemplateBUS;
import com.pandora.bus.repository.Repository;
import com.pandora.bus.repository.RepositoryBUS;
import com.pandora.delegate.ArtifactTemplateDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.MaxSizeAttachmentException;
import com.pandora.exception.RepositoryPolicyException;
import com.pandora.gui.struts.form.ArtifactForm;
import com.pandora.gui.struts.form.BrowseArtifactForm;
import com.pandora.gui.taglib.decorator.RepositoryEntityRadioBoxDecorator;
import com.pandora.helper.SessionUtil;

public class BrowseArtifactAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showBrowseArtifact";
		RepositoryViewerAction repAction = new RepositoryViewerAction();
		ProjectDelegate pdel = new ProjectDelegate();
		UserDelegate udel = new UserDelegate();
		try {
			BrowseArtifactForm frm = (BrowseArtifactForm)form;
			if (frm.getRev()!=null && frm.getRev().equalsIgnoreCase("null")) {
				frm.setRev(null);
			}
			
			String path = frm.getPath();
			if (path==null) {
				path = "";
			}
			
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
			if (pto.getRepositoryClass()!=null) {
				Repository rep = RepositoryBUS.getRepositoryClass(pto.getRepositoryClass());
				if (rep!=null) {
					frm.setShowUserPwd(rep.showUserPwdFields()?"on":"off");
					UserTO currentUser = SessionUtil.getCurrentUser(request);
					frm.setArtifactSaveUser(currentUser.getUsername());
					frm.setArtifactSavePwd("");
				}
			}			
			
			Vector<RepositoryFileTO> allItens = repAction.getItens(path, frm.getProjectId(), frm.getRev(), true, true);
			request.getSession().setAttribute("artifactsFolderList", allItens);
			
			//generate the combo box of artifact export classes...
			Vector<TransferObject> exportList = new Vector<TransferObject>();
			UserTO root = udel.getRoot();
			String classesList = root.getPreference().getPreference(PreferenceTO.ARTIFACT_EXPORT_CLASS);
			if (classesList!=null) {
				String[] clist = classesList.split(";");
				if (clist!=null) {
					for (int i=0; i<clist.length; i++) {
						String className = clist[i];
						ArtifactExport exp = ArtifactTemplateBUS.getArtifactExpClass(className.trim());
						String lbl = super.getBundleMessage(request, exp.getUniqueName(), true);
						TransferObject to = new TransferObject(className.trim(), lbl);
						exportList.add(to);
					}
				}
			}			
			request.getSession().setAttribute("artifactExportList", exportList);
			
			
		} catch(Exception e) {
			e.printStackTrace();
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}					
		return mapping.findForward(forward);		
	}
	

	public ActionForward saveArtifact(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ArtifactTemplateDelegate atdel = new ArtifactTemplateDelegate();
		BrowseArtifactForm frm = (BrowseArtifactForm)form;			
		RepositoryDelegate del = new RepositoryDelegate();
		
		try {
			ArtifactTO artifact = new ArtifactTO();
			artifact.setExportType(frm.getArtifactSaveType());
			artifact.setProjectId(frm.getProjectId());
			artifact.setLogMessage(frm.getArtifactSaveLog());
			artifact.setUser(frm.getArtifactSaveUser());
			artifact.setPass(frm.getArtifactSavePwd());			
			
			String path = (String) request.getSession().getAttribute(RepositoryEntityRadioBoxDecorator.REPOSITORY_SELECTED_PATH);
			artifact.setPath(path);
			
			UserTO handler = SessionUtil.getCurrentUser(request);
			artifact.setHandler(handler);

			ArtifactForm afrm = (ArtifactForm)request.getSession().getAttribute("artifactForm");
			artifact.setFileName(afrm.getName());			
			artifact.setTemplateId(afrm.getTemplateId());
			
			String body = afrm.getBody();
			
			String reqUri = request.getRequestURI();	
			reqUri = reqUri.substring(0, reqUri.indexOf("/do"));
			
			body = body.replaceAll("src=\"../file", "src=\"" + SessionUtil.getUri(request) + reqUri + "/file");
			artifact.setBody(body);
			

			String planningId = PlanningBUS.extractPlanningIdFromComment(frm.getArtifactSaveLog());
			if (planningId==null) {
				planningId = afrm.getPlanningId();
				if (planningId!=null && !planningId.trim().equals("")) {
					String log = frm.getArtifactSaveLog();
					log = "[#" + planningId + "] " + log;
					frm.setArtifactSaveLog(log);
				}
			}
			artifact.setPlanningId(planningId);			
					
			atdel.commitArtifact(artifact);
			
			if (path==null) {
				path = afrm.getName();
			} else {
				path = path + "/" + afrm.getName();
			}
			del.insertHistory(handler, new ProjectTO(frm.getProjectId()), path, RepositoryDelegate.ACTION_SAVE_ARTIFACT, frm.getArtifactSaveLog());

		    this.setSuccessFormSession(request, "label.formRepository.saved");			

		} catch(RepositoryPolicyException e){
			this.setErrorFormSession(request, e.getErrorMessage(), e);
		    
		} catch(MaxSizeAttachmentException e){
			this.setErrorFormSession(request, "error.artifactTag.maxSize", e);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.insertFormError", e);
		}
		
		return mapping.findForward("goToArtifact");		
	}

}
