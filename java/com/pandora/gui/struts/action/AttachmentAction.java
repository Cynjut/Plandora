package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import com.pandora.delegate.UserDelegate;

import com.pandora.AttachmentTO;
import com.pandora.OccurrenceTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.PreferenceTO;
import com.pandora.RequirementTO;
import com.pandora.RiskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.AttachmentDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.MaxSizeAttachmentException;
import com.pandora.gui.struts.form.AttachmentForm;
import com.pandora.helper.SessionUtil;

/**
 */
public class AttachmentAction extends GeneralStrutsAction {

    
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showAttachment";
		try {
			this.clearForm(form, request);
			AttachmentForm frm = (AttachmentForm)form;
			frm.setPlanningId(request.getParameter("planningId"));
			frm.setSource(request.getParameter("source"));
			
		    if (frm.getSource().equals("REQ")) {
		        frm.setFwd("refreshCustAfterAttach");	        
		    } else {
		    	frm.setFwd("showAttachment");
		    }
			
			if (frm.getPlanningId()!=null && !frm.getPlanningId().equals("")) {
			    this.refresh(mapping, form, request, response);
			    this.refreshAuxiliarList(mapping, form, request, response);
			}

			UserDelegate udel = new UserDelegate();
			UserTO root = udel.getRoot();
			String maxFile = root.getPreference().getPreference(PreferenceTO.UPLOAD_MAX_SIZE);
			frm.setMaxSizeFile(Integer.parseInt(maxFile)/1024+"");
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.formAttachment.showForm", e);
		}
		
		return mapping.findForward(forward);		
	}
	

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = null;
		String errorMsg = "error.formAttachment.showForm";
		String succeMsg = "message.success";
		
		try {
		    AttachmentForm frm = (AttachmentForm)form;
		    forward = frm.getFwd();
		    
		    if (frm.getId()!=null) {
		    	
			    AttachmentDelegate del = new AttachmentDelegate();
			    
			    if (frm.getStatus()==null || frm.getStatus().equals("")) {
			        frm.setStatus("1");
			    }
			    AttachmentTO ato = this.getTransferObjectFromActionForm(frm, request);
				
			    errorMsg = "error.formAttachment.save";
			    succeMsg = "message.saveAttachment";
			    
				if (frm.isUpload()){
				    
			        FormFile uploadFile = frm.getTheFile();
			        ato.setContentType(uploadFile.getContentType()); 
			        ato.setName(uploadFile.getFileName());
			        ato.setFileInBytes(uploadFile.getFileData(), uploadFile.getFileSize());
			        
				    del.insertAttachment(ato);
				} else {
				    del.updateAttachment(ato);
				}
			
				this.clear(mapping, form, request, response );				
				this.setSuccessFormSession(request, succeMsg);		    	
		    }
			
		} catch(MaxSizeAttachmentException e){
		    this.setErrorFormSession(request, "error.formAttachment.maxSize", e);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(Exception e){
		    this.setErrorFormSession(request, errorMsg, e);
		}

		return mapping.findForward(forward);	    
	}

	
	public void refreshAuxiliarList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
        request.getSession().setAttribute("typeList", this.getTypeCombo(request));
        request.getSession().setAttribute("visibilityList", this.getVisibilityCombo(request));
	}
	

	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showAttachment";

		try {	    		    
		    AttachmentForm frm = (AttachmentForm)form;
		    AttachmentDelegate del = new AttachmentDelegate();
		    
		    Vector list = del.getAttachmentByPlanning(frm.getPlanningId());
		    request.getSession().setAttribute("attachmentList", list);
		    
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.formAttachment.showForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}


    public ActionForward editAttachment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showAttachment";
	    AttachmentDelegate del = new AttachmentDelegate();
		
		try {
		    AttachmentForm frm = (AttachmentForm)form;
		    AttachmentTO ato = del.getAttachment(new AttachmentTO(frm.getId()));
		    this.getActionFormFromTransferObject(ato, frm, request);
		    frm.setUpload(false);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formRisk.showForm", e);
		}
		
		return mapping.findForward(forward);				
	}
    

	public ActionForward removeAttachment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = null;

		try {
		    AttachmentForm frm = (AttachmentForm)form;
		    AttachmentDelegate del = new AttachmentDelegate();
		    UserTO uto = SessionUtil.getCurrentUser(request);			
		    forward = frm.getFwd();
			    
			AttachmentTO ato = new AttachmentTO();
			ato.setId(frm.getId());
			ato = del.getAttachment(ato);
			ato.setHandler(uto);
			
			del.removeAttachment(ato);

			frm.setSaveMethod(AttachmentForm.INSERT_METHOD, uto);
			this.clearForm(frm, request);
			
			this.setSuccessFormSession(request, "message.removeAttachment");		
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formAttachment.remove", e);
		}
	    
		return mapping.findForward(forward);	    
	}

		
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showAttachment";		
		this.prepareForm(mapping, form, request, response);
		AttachmentForm frm = (AttachmentForm)form;
		frm.setUpload(true);
		return mapping.findForward(forward);	
	}
	
	
	private void clearForm(ActionForm form, HttpServletRequest request){
	    AttachmentForm frm = (AttachmentForm)form;		
	    frm.clear();
	    this.clearMessages(request);
	}
	
	
	private void getActionFormFromTransferObject(AttachmentTO to, AttachmentForm frm, HttpServletRequest request){
	    frm.setComment(to.getComment());
	    frm.setCreationDate(to.getCreationDate());
	    frm.setId(to.getId());
	    frm.setName(to.getName());
	    frm.setPlanningId(to.getPlanning().getId());
	    frm.setStatus(to.getStatus());
	    frm.setType(to.getType());
	    frm.setUpload(false);
	    frm.setVisibility(to.getVisibility());
	}
	
	
	private AttachmentTO getTransferObjectFromActionForm(AttachmentForm frm, HttpServletRequest request){
	    AttachmentTO ato = new AttachmentTO();
	    
	    ato.setId(frm.getId());
	    ato.setCreationDate(frm.getCreationDate());
	    ato.setComment(frm.getComment());
	    ato.setName(frm.getName());
	    
	    PlanningTO pto = null;
	    if (frm.getSource().equals("REQ")) {
	        pto = new RequirementTO();	        
	    } else if (frm.getSource().equals("TSK")) {
	        pto = new TaskTO();
	    } else if (frm.getSource().equals("PRJ")) {
	        pto = new ProjectTO();
	    } else if (frm.getSource().equals("OCU")) {
	        pto = new OccurrenceTO();
	    } else if (frm.getSource().equals("RSK")) {
	        pto = new RiskTO();
	    }
	    pto.setId(frm.getPlanningId());
	    ato.setPlanning(pto);
	    
	    ato.setStatus(frm.getStatus());
	    ato.setType(frm.getType());
	    ato.setVisibility(frm.getVisibility());
	    ato.setVisibilityLabel(this.getBundleMessage(request, "label.formAttachment.visibility." + frm.getVisibility()));

	    UserTO uto = SessionUtil.getCurrentUser(request);
	    ato.setHandler(uto);

	    return ato;
	}


	public Vector getTypeCombo(HttpServletRequest request){
	    Vector response = new Vector();
	    for(int i=1; i<=13; i++){
	        TransferObject to = new TransferObject();
	        to.setId(this.getBundleMessage(request, "label.formAttachment.type.id." + i)); 
	        to.setGenericTag(this.getBundleMessage(request, "label.formAttachment.type." + i));
	        response.addElement(to);
	    }
	    return response;
	}

	
	public Vector getVisibilityCombo(HttpServletRequest request){
	    Vector response = new Vector();
	    for(int i=3; i>=1; i--){
	        TransferObject to = new TransferObject();
	        to.setId(i+""); 
	        to.setGenericTag(this.getBundleMessage(request, "label.formAttachment.visibility." + i));
	        response.addElement(to);
	    }
	    return response;
	}
	
    
}
