package com.pandora.gui.struts.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.AttachmentTO;
import com.pandora.bus.GeneralTimer;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.AttachmentDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.LogUtil;

/**
 * This servlet expose a public link to attachments downloads.
 * Check the web.xml in order to change the [enable] property.
 */
public class PublicFileDownload extends HttpServlet{

	private static final long serialVersionUID = 1L;

	public void init() {
		String enable = "";
		try {
			enable = getInitParameter("enable");
			if (enable!=null && !enable.trim().equalsIgnoreCase("off")){
				SystemSingleton.getInstance().setPublicDownload("on");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "attachment download listener is ON...");
				System.out.println("attachment download listener is ON...");
				GeneralTimer.getInstance();
			} else {
				SystemSingleton.getInstance().setPublicDownload("off");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "attachment download listener is OFF...");
			}
		} catch (BusinessException e) {
			LogUtil.log(this, LogUtil.LOG_ERROR, "error at startup parameters enable_public_download=" + enable, e);
		}
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);
	}


	private void process(HttpServletRequest req, HttpServletResponse resp) {
		try {			
			String enable = SystemSingleton.getInstance().getPublicDownload(); 
			if (enable!=null && enable.trim().equalsIgnoreCase("on")) {
				String id = req.getParameter("id");
				if (id!=null) {
					this.performDownload(resp, id);
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.log(this, LogUtil.LOG_ERROR, "the file donwload failed.", e);
		}
	}

	
	private void performDownload(HttpServletResponse resp, String id) throws Exception {
		ServletOutputStream sos = null;
		AttachmentDelegate del = new AttachmentDelegate();
		
		try {
	    	AttachmentTO ato = del.getAttachmentFile(new AttachmentTO(id));
			if (ato!=null && ato.getVisibility()!=null && ato.getStatus()!=null &&  
					ato.getVisibility().equals(AttachmentTO.VISIBILITY_PUBLIC) &&
					(ato.getStatus().equals(AttachmentTO.ATTACH_STATUS_AVAILABLE) || 
							ato.getStatus().equals(AttachmentTO.ATTACH_STATUS_RECOVERED))) {
			
				String contentType = ato.getContentType();
				String fileName = ato.getName();
				
			    if (ato.getFileSize()>0){			    	
					sos = resp.getOutputStream();
					resp.setContentType(contentType);
					resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
					resp.setContentLength(ato.getFileSize());
					sos.write(ato.getFileInBytes());
			    } else {
			        throw new BusinessException("Zero-bytes attachment error.");       
			    }
			}			
		} catch(Exception e) {
			throw e;
			
		} finally {
		    try {
		        if (sos!=null) {
		            sos.close();
		        }
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

}
