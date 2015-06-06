package com.pandora.gui.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.MaxSizeAttachmentException;
import com.pandora.gui.struts.form.RepositoryUploadForm;
import com.pandora.helper.SessionUtil;

public class RepositoryUploadAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showRepositoryUpload";
		try {
			RepositoryUploadForm frm = (RepositoryUploadForm)form;
			frm.setNewFolder(frm.getPath());
			
			UserDelegate udel = new UserDelegate();
			UserTO root = udel.getRoot();
			String maxFile = root.getPreference().getPreference(PreferenceTO.UPLOAD_MAX_SIZE);
			frm.setMaxFileSize(Integer.parseInt(maxFile)/1024+"");
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.formAttachment.showForm", e);
		}
		
		return mapping.findForward(forward);		
	}
	

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			RepositoryDelegate rdel = new RepositoryDelegate();
			RepositoryUploadForm frm = (RepositoryUploadForm)form;
        	UserTO uto = SessionUtil.getCurrentUser(request);
        	
        	String repAction = RepositoryDelegate.ACTION_UPLOAD_FILE;
        	String historyPath = frm.getPath();
			if (frm.getFolderCreation()!=null && frm.getFolderCreation().equals("on")) {

				if (historyPath!=null && !historyPath.trim().equals("")) {
					historyPath = historyPath + "/" + frm.getNewFolder();	
				} else {
					historyPath = frm.getNewFolder();
				}

				rdel.createFolder(uto, historyPath, frm.getProjectId(), frm.getComment());
				this.setSuccessFormSession(request, "label.formRepository.folder");
				repAction = RepositoryDelegate.ACTION_UPLOAD_FOLDER;
				
			} else {
		        FormFile uploadFile = frm.getTheFile();
		        		        
		        if (uploadFile!=null && uploadFile.getFileData()!=null && uploadFile.getFileData().length>0) {
		        	
					if (historyPath!=null && !historyPath.trim().equals("")) {
						historyPath = historyPath + "/" + uploadFile.getFileName();	
					} else {
						historyPath = uploadFile.getFileName();
					}
		        	
					rdel.uploadFile(uto, frm.getPath(), frm.getProjectId(), uploadFile.getFileData(), 
							uploadFile.getContentType(), uploadFile.getFileName(), frm.getComment());
					this.setSuccessFormSession(request, "label.formRepository.upload");
		        } else {
				    this.setErrorFormSession(request, "label.formRepository.msg.upload", null);	        	
		        }				
			}
			
			if (frm.getProjectId()!=null) {
				rdel.insertHistory(uto, new ProjectTO(frm.getProjectId()), historyPath, repAction, frm.getComment());	
			}

			
		} catch(MaxSizeAttachmentException e){
		    this.setErrorFormSession(request, "error.formAttachment.maxSize", e);			
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}							

		return mapping.findForward("goToRepository");		
	}
	
}
