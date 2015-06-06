package com.pandora.gui.struts.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.RepositoryLogTO;
import com.pandora.ResourceTO;
import com.pandora.UserTO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.NotEmptyFolderException;
import com.pandora.gui.struts.form.RepositoryViewerForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

public class RepositoryViewerAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showRepositoryViewer";
		
		RepositoryViewerForm frm = (RepositoryViewerForm)form;
		ProjectDelegate pdel = new ProjectDelegate();
		RepositoryDelegate rdel = new RepositoryDelegate();
		
		try {
			String reqUri = request.getRequestURI();	
			reqUri = reqUri.substring(0, reqUri.indexOf("/do"));
			String path = SessionUtil.getUri(request);
			frm.setUriPath(path + reqUri);
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), false);
			boolean isAllowed = (pto.isLeader(uto.getId()));
			
			if (!isAllowed ) {
				UserDelegate udel = new UserDelegate();
				ResourceTO rto = new ResourceTO(uto.getId());
				rto.setProject(pto);
				rto = udel.getResource(rto);
				isAllowed = rto.getBoolCanSeeRepository();
			}

			if (isAllowed) {
				frm.setRev(RepositoryFileTO.REPOSITORY_HEAD);
				frm.setPath("");
				frm.setEmulateCustomerViewer("off");
				request.getSession().removeAttribute("custRepositFileList");
				request.getSession().setAttribute("repositoryFileList", new Vector<RepositoryFileTO>());
								
				frm.setShowUploadButton(rdel.canUploadFile(pto)?"on":"off");
				
				this.navigate(mapping, form, request, response);
				
			} else {
				forward = "home";
				throw new BusinessException("The user doesn't has permission to access this form");
			}
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}	   
		
		return mapping.findForward(forward);		
	}
	
	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showRepositoryViewer";
		try {
		    this.clearMessages(request);
		    this.refresh(mapping, form, request, response);
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}					
		return mapping.findForward(forward);		
	}

	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {		
			RepositoryViewerForm frm = (RepositoryViewerForm)form;
			Vector<RepositoryFileTO> allItens = this.getItens(frm.getPath(), frm.getProjectId(), frm.getRev(), false, false);
			request.getSession().setAttribute("repositoryFileList", allItens);
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}					
		
		return mapping.findForward("showRepositoryViewer");		
	}
	
	
	public ActionForward toggleDownloadStatus(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			RepositoryViewerForm frm = (RepositoryViewerForm)form;
			RepositoryDelegate rdel = new RepositoryDelegate();
			
			ProjectTO pto = new ProjectTO(frm.getProjectId());
			RepositoryFileProjectTO rfpto = rdel.getFileFromDBbyId(pto, frm.getId());

			String action = RepositoryDelegate.ACTION_PUBLIC_DWL_ON;
			boolean newStatus = false;
			if (rfpto!=null) {
				Boolean d = rfpto.getIsDownloadable();
				if (d!=null) {
					newStatus = !d.booleanValue();
					if (!newStatus) {
						action = RepositoryDelegate.ACTION_PUBLIC_DWL_OFF;
					}
				}				
			}
			
			rdel.updateDownloadableStatus(pto, frm.getId(), newStatus);
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			rdel.insertHistory(uto, pto, rfpto.getFile(), action, null);
			
			frm.setOperation("navigate");
			frm.setGenericTag(null);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return navigate(mapping, form, request, response);		
	}
	
	
	public ActionForward showDownloadPopup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showPublicDownloadPopup");		
	}
	
	
	
	public ActionForward toggleCustomerViewer(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		RepositoryViewerForm frm = (RepositoryViewerForm)form;
		try {
			if (frm.getEmulateCustomerViewer()!=null && frm.getEmulateCustomerViewer().equals("off")) {
				frm.setEmulateCustomerViewer("on");
				RepositoryViewerCustomerAction custAction = new RepositoryViewerCustomerAction();			
		        Vector<RepositoryFileTO> list = custAction.extractRepositoryFiles(frm.getProjectId());
		        request.getSession().setAttribute("custRepositFileList", list);
		        request.getSession().removeAttribute("repositoryFileList");
		        
			} else {
				frm.setEmulateCustomerViewer("off");
				request.getSession().removeAttribute("custRepositFileList");
				navigate(mapping, form, request, response);
			}			
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}					
		return mapping.findForward("showRepositoryViewer");		
	}

	
	
	public Vector<RepositoryFileTO> getItens(String path, String projectId, String version, boolean includeEntitiesLink, boolean onlyFolders) throws Exception {
		Vector<RepositoryFileTO> allItens = new Vector<RepositoryFileTO>();
		
		RepositoryDelegate repdel = new RepositoryDelegate();	
		ProjectDelegate pdel = new ProjectDelegate();
		ProjectTO pto = pdel.getProjectObject(new ProjectTO(projectId), true);
		Vector<RepositoryFileTO> fileList = repdel.getFiles(pto, path, version, includeEntitiesLink);
		
		//create an 'artificial' item to able user navigating through the repository tree
		if (path!=null && !path.trim().equals("")) {
			allItens.addElement(this.getBackTreeItem(path, pto));
		}
		
		//filter the items (visibility issues, etc)
		allItens.addAll(this.filterList(fileList, pto, onlyFolders));
		
		return allItens;
	}
	
	
	public ActionForward showLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showRepositoryViewer";
		return mapping.findForward(forward);		
	}

	
	public ActionForward getFile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		ServletOutputStream sos = null;
		RepositoryDelegate del = new RepositoryDelegate();
		
		try {
			RepositoryDelegate repdel = new RepositoryDelegate();	
			ProjectDelegate pdel = new ProjectDelegate();
			
			RepositoryViewerForm frm = (RepositoryViewerForm)form;
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
			RepositoryFileTO rfto = repdel.getFile(pto, frm.getPath(), frm.getRev());

			UserTO uto = SessionUtil.getCurrentUser(request);
			del.insertHistory(uto, pto, rfto, RepositoryDelegate.ACTION_CUSTOMER_DWL, null);
			
			sos = response.getOutputStream();
			response.setContentType(rfto.getContentType());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + rfto.getName() + "\"");
			response.setContentLength(rfto.getFileSize());
			sos.write(rfto.getFileInBytes());
			
		}catch(Exception e){
			e.printStackTrace();
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		} finally {
		    try {
		        if (sos!=null) {
		            sos.close();
		        }
			} catch (IOException e2) {
			    this.setErrorFormSession(request, "error.generic.showFormError", null);    
			}
		}
		return null;		
	}


	public ActionForward removeItem(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showRepositoryViewer";
		try {
			ProjectDelegate pdel = new ProjectDelegate();
			RepositoryDelegate repdel = new RepositoryDelegate();	
			super.clearMessages(request);
			
			RepositoryViewerForm frm = (RepositoryViewerForm)form;	
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
			repdel.removeFile(pto, frm.getPath(), frm.getRev());
			
			this.refresh(mapping, form, request, response);
			
			this.setSuccessFormSession(request, "label.formRepository.remov");
		
		} catch(NotEmptyFolderException e) {
			this.setErrorFormSession(request, "label.formRepository.msg.notEmptyfolder", e);
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
					
		return mapping.findForward(forward);				
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward grant(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			RepositoryDelegate del = new RepositoryDelegate();
			RepositoryViewerForm frm = (RepositoryViewerForm)form;

			response.setContentType("text/xml");  
			response.setHeader("Cache-Control", "no-cache");  
			PrintWriter out = response.getWriter();
			
			@SuppressWarnings("rawtypes")
			Vector<RepositoryFileTO> allItens = (Vector)request.getSession().getAttribute("repositoryFileList");
			if (allItens!=null) {
				int idx = Integer.parseInt(frm.getId()); //if (allItens.size()>idx)
				RepositoryFileTO item = (RepositoryFileTO)allItens.get(idx);
				if (item!=null) {
					boolean disabled = false;
					ProjectTO pto = new ProjectTO(frm.getProjectId());
					RepositoryFileProjectTO rfp = del.getFileFromDB(pto, item.getPath());
					
					String action = RepositoryDelegate.ACTION_CUST_CDWL_ON;
					if (rfp!=null && rfp.getIsDisabled()!=null) {
						disabled = !rfp.getIsDisabled().booleanValue();
						if (disabled) {
							out.println("off.gif|" + this.getBundleMessage(request, "label.formRepository.perm.off"));
							action = RepositoryDelegate.ACTION_CUST_CDWL_OFF;
						} else {
							out.println("on.gif|" + this.getBundleMessage(request, "label.formRepository.perm.on"));	
						}						
					} else {
						out.println("on.gif|" + this.getBundleMessage(request, "label.formRepository.perm.on"));
					}
					
					del.updateDisabledStatus(new ProjectTO(frm.getProjectId()), item.getPath(), disabled);
					
					if (rfp!=null) {
						UserTO uto = SessionUtil.getCurrentUser(request);
						del.insertHistory(uto, pto, rfp.getFile(), action, null);						
					}
				}
			}
						
			out.flush();

		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return null;
	}
	
	
	private Vector<RepositoryFileTO> filterList(Vector<RepositoryFileTO> fileList, ProjectTO pto, boolean onlyFolders){
		Vector<RepositoryFileTO> response = new Vector<RepositoryFileTO>();
		
		Iterator<RepositoryFileTO> i = fileList.iterator();
		while(i.hasNext()) {
			RepositoryFileTO efto = i.next();
			if (!onlyFolders || (onlyFolders && efto.getIsDirectory().booleanValue())) {
				if (efto.getVisibility()==null) {
					efto.setPlanning(pto);
					response.addElement(efto);
				}				
			}
		}
		
		return response;
	}
	
	
	private RepositoryFileTO getBackTreeItem(String currentPath, ProjectTO pto){
		RepositoryFileTO rfto = new RepositoryFileTO();
		rfto.setAuthor("");
		rfto.setCreationDate(null);
		rfto.setFileSize(0);
		rfto.setIsDirectory(new Boolean(true));
		rfto.setIsLocked(new Boolean(false));
		rfto.setName("..");
		rfto.setPlanning(pto);
		
		int idx = currentPath.lastIndexOf("/");
		if (idx>-1) {
			rfto.setPath(pto.getRepositoryURL() + "/" + currentPath.substring(0, idx));	
		} else {
			rfto.setPath("");
		}
		rfto.setRevision(null);
		return rfto;
	}
	

	public ActionForward browse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showRepositoryBrowse";
		try {
			RepositoryViewerForm frm = (RepositoryViewerForm)form;

			super.clearMessages(request);
			
			if (frm.getRev()!=null && frm.getRev().equalsIgnoreCase("null")) {
				frm.setRev(null);
			}
			
			String path = frm.getPath();
			if (path==null) {
				path = "";
			}
			Vector<RepositoryFileTO> allItens = this.getItens(path, frm.getProjectId(), frm.getRev(), true, false);
			request.getSession().setAttribute("repositoryFileList", allItens);
			
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}					
		return mapping.findForward(forward);		
	}	

	
	public ActionForward showLogFile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showRepositoryLogViewer";
		try {
			ProjectDelegate pdel = new ProjectDelegate();
			RepositoryDelegate rdel = new RepositoryDelegate();
			RepositoryViewerForm frm = (RepositoryViewerForm)form;
			UserDelegate udel = new UserDelegate();
			
			UserTO user = SessionUtil.getCurrentUser(request);
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), false);
			Vector<RepositoryLogTO> allItens = rdel.getLogs(pto, frm.getPath(), frm.getLogrev());

			StringBuffer content = new StringBuffer();
			if (allItens!=null) {
				content.append("<table border=\"0\" cellpadding=\"5\" cellspacing=\"10\">");
				Iterator<RepositoryLogTO> i = allItens.iterator();
				while(i.hasNext()) {
					RepositoryLogTO log = i.next();
					
					String author = log.getAuthor();
					if (author!=null) {
						String imgAuthor = "../images/emptypic.png";
						UserTO uto = new UserTO();
						uto.setUsername(log.getAuthor());
						uto = udel.getObjectByUsername(uto);
						if (uto!=null) {
							imgAuthor = "../do/login?operation=getUserPic&id=" + uto.getId() + "&ts=" +DateUtil.getNow().toString(); 
						}

						String dateStr = "&nbsp;";
						if (log.getDate()!=null){
							dateStr =  DateUtil.getDateTime(log.getDate(), user.getLocale(), 2, 2);
						}

						String rev = "&nbsp;";
						if (log.getRevision()!=null) {
							rev = super.getBundleMessage(request, "label.formRepository.revision") + ": " + log.getRevision().toString(); 
						}
						
						content.append("<tr>");
						content.append("<td width=\"160\" class=\"formNotesHilight\">");
						content.append("   <table width=\"100%\" border=\"0\"><tr class=\"formNotesHilight\"><td class=\"successfullyMessage\"><center>" + author + "</center></td><td rowspan=\"3\">&nbsp;</td><td rowspan=\"3\"><img width=\"30\" height=\"40\" border=\"0\" src=\"" + imgAuthor + "\"></td></tr>");
						content.append("          <tr class=\"formNotesHilight\"><td><center>" + dateStr + "</center></td></tr>");
						content.append("          <tr class=\"formNotesHilight\"><td><center>" + rev + "</center></td></tr></table>");
						content.append("</td>");
						content.append("<td valign=\"top\" align=\"justify\" class=\"codeWhite\"><div style=\"width:370px; height:50px; overflow-y: scroll; overflow-x: hidden;\">" + log.getMessage() + "</div></td></tr>");
					}
				}
				content.append("</table>");
			}

			frm.setLogFileListHtml(content.toString());
			
		} catch(Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}					
		return mapping.findForward(forward);		
	}	
	
}
