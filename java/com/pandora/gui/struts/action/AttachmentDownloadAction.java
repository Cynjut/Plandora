package com.pandora.gui.struts.action;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.AttachmentTO;
import com.pandora.UserTO;
import com.pandora.delegate.AttachmentDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;

/**
 */
public class AttachmentDownloadAction extends GeneralStrutsAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
	    
		String fileName = "";
		String contentType = "";
		ServletOutputStream sos = null;
		
		try {
			String id = request.getParameter("id");
		    AttachmentDelegate del = new AttachmentDelegate();
		    UserTO uto = SessionUtil.getCurrentUser(request);

			//load the file (array of bytes) from data base		    
			AttachmentTO ato = new AttachmentTO();
			ato.setId(id);
			ato.setHandler(uto);
			ato = del.getAttachmentFile(ato);
			
			contentType = ato.getContentType();
			fileName = ato.getName();
			
			//put response content into Standard output
			try {
			    if (ato.getFileSize()>0){
					sos = response.getOutputStream();
					response.setContentType(contentType);
					response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
					response.setContentLength(ato.getFileSize());
					sos.write(ato.getFileInBytes());
			    } else {
			        throw new BusinessException("Zero-bytes attachment error.");       
			    }
			} catch (IOException e2) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on attachment download process", e2);				
			}
			
		}catch(Exception e){
			this.setErrorFormSession(request, "error.formAttachment.showForm", e);
		} finally {
		    try {
		        if (sos!=null) {
		            sos.close();
		        }
			} catch (IOException e2) {
			    this.setErrorFormSession(request, "error.formAttachment.showForm", null);    
			}
		}

		return null;	    
	}
    
}
