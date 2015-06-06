package com.pandora.gui.struts.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.bus.GeneralTimer;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.LogUtil;

public class PublicArtifactDownload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void init() {
		String enable = "";
		try {
			enable = getInitParameter("enable");
			if (enable!=null && !enable.trim().equalsIgnoreCase("off")){
				SystemSingleton.getInstance().setPublicArtifactDownload("on");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "artifact download listener is ON...");
				System.out.println("artifact download listener is ON...");
				GeneralTimer.getInstance();
			} else {
				SystemSingleton.getInstance().setPublicArtifactDownload("off");
				LogUtil.log(this, LogUtil.LOG_DEBUG, "artifact download listener is OFF...");
			}
		} catch (BusinessException e) {
			LogUtil.log(this, LogUtil.LOG_ERROR, "error at startup parameters enable_public_artifact_download=" + enable, e);
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
			String enable = SystemSingleton.getInstance().getPublicArtifactDownload(); 
			if (enable!=null && enable.trim().equalsIgnoreCase("on")) {
				String id = req.getParameter("id");
				if (id!=null && !id.trim().equals("")) {
					String projId = req.getParameter("prj");
					if (projId!=null) {
						this.performDownload(resp, id, projId);	
					}
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.log(this, LogUtil.LOG_ERROR, "the file donwload failed.", e);
		}
	}

	
	private void performDownload(HttpServletResponse resp, String fileId, String projectId) throws Exception {
		ServletOutputStream sos = null;
		RepositoryDelegate del = new RepositoryDelegate();
		ProjectDelegate pdel = new ProjectDelegate();
		
		try {
			
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(projectId), true);
			if (pto!=null) {
				RepositoryFileProjectTO rfpto = del.getFileFromDBbyId(pto, fileId);
				if (rfpto!=null && rfpto.getIsDownloadable()!=null && rfpto.getIsDownloadable().booleanValue()) {
					RepositoryFileTO rf = del.getFile(pto, rfpto.getFile().getPath(), RepositoryFileTO.REPOSITORY_HEAD);

					del.insertHistory(null, pto, rf, RepositoryDelegate.ACTION_CUSTOMER_DWL, null);
					
					LogUtil.log(this, LogUtil.LOG_INFO, "the artifact [" + rf.getPath() + "] prj:[" + pto.getId() + "] was downloaded!");

					String contentType = rf.getContentType();
					String fileName = rf.getName();
					
				    if (rf.getFileSize()>0){			    	
						sos = resp.getOutputStream();
						resp.setContentType(contentType);
						resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
						resp.setContentLength(rf.getFileSize());
						sos.write(rf.getFileInBytes());
				    } else {
				        throw new BusinessException("Zero-bytes attachment error.");       
				    }
				}							
			}
			
		} catch(Exception e) {
			e.printStackTrace();
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
