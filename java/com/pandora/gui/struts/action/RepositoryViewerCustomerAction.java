package com.pandora.gui.struts.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.UserTO;
import com.pandora.bus.convert.Converter;
import com.pandora.delegate.ConverterDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.gui.struts.form.RepositoryViewerCustomerForm;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;

/**
 */
public class RepositoryViewerCustomerAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showRepositoryViewerCustomer";
		ProjectDelegate pdel = new ProjectDelegate();
	    
	    try {
			RepositoryViewerCustomerForm frm = (RepositoryViewerCustomerForm)form;
		    ProjectTO pto = null;

		    UserTO uto = SessionUtil.getCurrentUser(request);
		    Vector prjList = pdel.getProjectListByUser(uto);
			if (prjList!=null && prjList.size()>0) {
				pto = (ProjectTO)prjList.get(0);
				request.getSession().setAttribute("projectList", prjList);					
			} else {
				prjList = new Vector();
			}

			if (pto!=null && frm.getProjectId().trim().equals("ALL")) {
				frm.setProjectId(pto.getId());
			}
		    
			request.getSession().setAttribute("projectList", prjList);			
		    
			if (pto!=null) {
				this.refresh(mapping, form, request, response);
			} else {
				request.getSession().setAttribute("custRepositFileList", new Vector());	
			}
			
	    } catch (Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);		    
	    }
	    
		return mapping.findForward(forward);	
	}
  
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showRepositoryViewerCustomer");
	}	
	
	public ActionForward showViewerOutput(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
		byte[] output = null;
		String contentType = "";
		RepositoryDelegate rdel = new RepositoryDelegate();
		ProjectDelegate pdel = new ProjectDelegate();
    	RepositoryFileTO file = null;


	    try {
	    	String prjId = request.getParameter("projectId");	        
	    	String newpath = request.getParameter("newpath");
	    	
	        if (prjId!=null && newpath!=null && !prjId.trim().equals("") && newpath!=null) {
	        	ProjectTO pto = pdel.getProjectObject(new ProjectTO(prjId), false);
	        	file = rdel.getFile(pto, newpath, RepositoryFileTO.REPOSITORY_HEAD);
	        }

	        if (file!=null) {
	        	ConverterDelegate cdel = new ConverterDelegate();
	        	Converter converter = cdel.getClass(file);
	        	if (converter!=null) {
	        		contentType = converter.getContentType();
	        		file.setHandler(SessionUtil.getCurrentUser(request));
	        		output = converter.convert(file);
	        		FileOutputStream fo = new FileOutputStream(new File("c:\\index\\buff2.swf"));
	        		fo.write(output);
	        		fo.close();
	        	}
	        }
	        
		}catch(Exception e){
			output = e.getMessage().getBytes();
			
		} finally{
			ServletOutputStream sos;
			try {
				if (file!=null) {
					sos = response.getOutputStream();	
					response.setContentType(contentType);
					response.setHeader("Content-Disposition", "attachment; filename=\"a.swf\"");					
					response.setContentLength(output.length);
					sos.write(output);
					sos.close();					
				}
			} catch (IOException e2) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on Repository Viewer Popup process", e2);
			}
		}

		return null;
	}
	
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showRepositoryViewerCustomer";	    
	    try {
			RepositoryViewerCustomerForm frm = (RepositoryViewerCustomerForm)form; 

			//set additional info of repository file objects...
	        Vector filesList = extractRepositoryFiles(frm.getProjectId());
	        
			request.getSession().setAttribute("custRepositFileList", filesList);					

       } catch (Exception e) {
		    this.setErrorFormSession(request, "error.generic.showFormError", e);		    
	   }

	    return mapping.findForward(forward);
	}


	public Vector<RepositoryFileTO> extractRepositoryFiles(String projectId) throws Exception {
	    RepositoryDelegate rdel = new RepositoryDelegate();
	    ProjectDelegate pdel = new ProjectDelegate();
	    
	    Vector<RepositoryFileTO> filesList = new Vector<RepositoryFileTO>();
        ProjectTO pto = pdel.getProjectObject(new ProjectTO(projectId), false);

        Vector<RepositoryFileTO> reportList = rdel.getFilesToCustomerView(pto);
        if (reportList==null) {
        	reportList = new Vector<RepositoryFileTO>();
        }
	    
		Iterator<RepositoryFileTO> i = reportList.iterator();
		while(i.hasNext()) {
			RepositoryFileTO rfto = i.next();
			rfto.setPlanning(pto);

			String path = rfto.getPath();
			if (path.startsWith("/")){
			    path = path.substring(1);
			}
			
			//fetch file information from repository...
			RepositoryFileTO fileFromRep = rdel.getFileInfo(pto, path, RepositoryFileTO.REPOSITORY_HEAD);
			if (fileFromRep!=null) {
				rfto.setAuthor(fileFromRep.getAuthor());
				rfto.setContentType(fileFromRep.getContentType());
				rfto.setCreationDate(fileFromRep.getCreationDate());
				rfto.setRevision(fileFromRep.getRevision());
				rfto.setIsDirectory(fileFromRep.getIsDirectory());
				rfto.setName(fileFromRep.getName());
				rfto.setFileSize(fileFromRep.getFileSize());
				rfto.setPath(path);
				
				filesList.addElement(rfto);
			}
		}
		
		return filesList;
	}	
}
