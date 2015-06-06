package com.pandora.gui.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.struts.form.OptionPictureForm;
import com.pandora.helper.SessionUtil;

public class OptionPictureAction extends GeneralStrutsAction {
	
	private static int MAX_PIC_SIZE = 51200;
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showOptionPicture";
		try {
			OptionPictureForm frm = (OptionPictureForm)form;
			frm.setConfirmationMsg(this.getBundleMessage(request, "error.manageOption.confirmation"));
		} catch(Exception e){
		    this.setErrorFormSession(request, "label.manageOption.picUploadError", e);
		}
		return mapping.findForward(forward);		
	}

	
	public ActionForward savePic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "goToOption";
		UserDelegate udel = new UserDelegate();
		try {
			UserTO uto = SessionUtil.getCurrentUser(request);
			OptionPictureForm frm = (OptionPictureForm)form;
	        FormFile uploadFile = frm.getTheFile();
	        
	        boolean formatOk = false;
	        String path = "";
	        if (uploadFile!=null && uploadFile.getFileName()!=null) {
	        	if (!uploadFile.getFileName().trim().equals("")) {
		        	path = uploadFile.getFileName().toUpperCase();
		            if (path.endsWith("PNG") || path.endsWith("JPG") || path.endsWith("JPEG")) {
			        	formatOk = true;
			        }	        		
	        	} else {
	        		formatOk = true;
	        	}
	        }

	        if (formatOk) {
		        if (uploadFile!=null && uploadFile.getFileData()!=null && uploadFile.getFileData().length > MAX_PIC_SIZE) {
		        	this.setErrorFormSession(request, "error.usernamePicMaxSize", null);	
		        } else {

					UserTO dbuto = udel.getUser(new UserTO(uto.getId()));
					try {
						if (uploadFile!=null && uploadFile.getFileData()!=null) {
					        uto.setFileInBytes(uploadFile.getFileData());
					        dbuto.setFileInBytes(uploadFile.getFileData());
						} else {
					        uto.setFileInBytes(null);
					        dbuto.setFileInBytes(null);						
						}
					} catch(Exception e) {
						e.printStackTrace();
					    uto.setFileInBytes(null);
					    dbuto.setFileInBytes(null);
					}
					
					udel.updatePicture(dbuto);
					this.setSuccessFormSession(request, "message.manageOption.changePic");
		        }	        	
	        } else {
	        	this.setErrorFormSession(request, "error.manageOption.picFormat", null);
	        }
	        			
		} catch(Exception e){
		    this.setErrorFormSession(request, "label.manageOption.picUploadError", e);
		}

		return mapping.findForward(forward);		
	}
	
}
