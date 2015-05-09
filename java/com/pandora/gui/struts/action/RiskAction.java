
package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.MetaFieldTO;
import com.pandora.ProjectTO;
import com.pandora.RiskStatusTO;
import com.pandora.RiskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RiskDelegate;
import com.pandora.delegate.RiskStatusDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.RiskForm;
import com.pandora.helper.SessionUtil;

/**
 */
public class RiskAction extends GeneralStrutsAction {
    
    
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		ProjectDelegate pdel = new ProjectDelegate();
		String forward = "showRisk";

		try {
			this.clearForm(form, request);
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			RiskForm rfrm = (RiskForm)form;
			rfrm.setSaveMethod(RiskForm.INSERT_METHOD, uto);
		
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(rfrm.getProjectId()), true);
			rfrm.setProject(pto);
			rfrm.setProjectName(pto.getName());
			
		    this.refresh(mapping, form, request, response);    
		    this.refreshAuxiliarList(mapping, form, request, response);			

		} catch(Exception e){
		    this.setErrorFormSession(request, "error.formRisk.showForm", e);
		}	    
	    
		return mapping.findForward(forward);		
	}
	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showRisk");
	}
	
	public ActionForward saveRisk(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		ActionForward fwd = null;
		boolean materialized = false;
		RiskForm frm = (RiskForm)form;
		
		try {
			RiskDelegate rdel = new RiskDelegate();
			fwd = mapping.findForward("showRisk");
			RiskTO rto = this.getTransferObjectFromActionForm(frm, request);
						
			materialized = rdel.isMaterializedRisk(rto);
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.formRisk.showForm", e);
		}	    
			
		if (materialized) {
			frm.setShowIssueConfirmation("on");
		} else {
			fwd = updateOrInsertRisk(mapping, form,	request, response);
		}
		
		return fwd;		
	}

	public ActionForward updateOrInsertRisk(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showRisk";
		String errorMsg = "error.formRisk.showForm";
		String succeMsg = "message.success";
		CategoryDelegate cdel = new CategoryDelegate();
		
		try {
		    RiskForm rfrm = (RiskForm)form;
		    rfrm.setShowIssueConfirmation("off");
		    RiskDelegate rdel = new RiskDelegate();
		    RiskTO rto = this.getTransferObjectFromActionForm(rfrm, request);

			CategoryTO cto = rto.getCategory();
			cto = cdel.getCategory(cto);
			rto.setCategory(cto);
		    
		    String createIssue = request.getParameter("create_issue");
		    rto.setCreateIssueLinked(createIssue!=null && 
		    		createIssue.equals("on") && rdel.isMaterializedRisk(rto));		    
		    
			if (rfrm.getSaveMethod().equals(RiskForm.INSERT_METHOD)){
			    errorMsg = "error.formRisk.insert";
			    succeMsg = "message.insertRisk";
			    rdel.insertRisk(rto);
			} else {
			    errorMsg = "error.formRisk.update";
			    succeMsg = "message.updateRisk";
			    rdel.updateRisk(rto);
			}
		
			this.clear(mapping, form, request, response );				
			this.setSuccessFormSession(request, succeMsg);				

		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);			
		    rfrm.setSaveMethod(RiskForm.INSERT_METHOD, uto);
		    		        			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}	    
		return mapping.findForward(forward);	    
	}


	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showRisk";

		try {
		    RiskForm rfrm = (RiskForm)form;
		    RiskDelegate rdel = new RiskDelegate();
		    ProjectTO pto = new ProjectTO(rfrm.getProjectId());
		    
		    Vector oList = rdel.getRiskList(pto.getId());
		    request.getSession().setAttribute("riskList", oList);
		    
		    CategoryDelegate cdel = new CategoryDelegate();
		    Vector cList = cdel.getCategoryListByType(CategoryTO.TYPE_RISK, pto, false);
		    request.getSession().setAttribute("categoryList", cList);
		    
		    RiskStatusDelegate rsdel = new RiskStatusDelegate();
		    Vector sList = rsdel.getRiskStatusList();
		    request.getSession().setAttribute("riskStatusList", sList);

		    request.getSession().setAttribute("metaFieldList", new Vector());
		    
		    rfrm.setShowIssueConfirmation("off");
		    
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.formRisk.showForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}
	

	public void refreshAuxiliarList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
            request.getSession().setAttribute("tendencyList", this.getTendencyCombo(request));
            request.getSession().setAttribute("probabilityList", this.getProbabilityCombo(request));
            request.getSession().setAttribute("impactList", this.getImpactCombo(request));
	}

	
	public ActionForward showConfirmation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showIssueRiskConfirm";
		return mapping.findForward(forward);		
	}

	
	
    public ActionForward editRisk(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showRisk";
	    RiskDelegate rdel = new RiskDelegate();
		
		try {
			MetaFieldDelegate mfdel = new MetaFieldDelegate();
		    RiskForm frm = (RiskForm)form;
		    RiskTO rto = rdel.getRisk(new RiskTO(frm.getId()));
		    
			if (frm.getId()!=null && !frm.getId().equals("")) {
				Vector list = mfdel.getListByProjectAndContainer(frm.getId(), frm.getCategoryId(), MetaFieldTO.APPLY_TO_RISK);
				request.getSession().setAttribute("metaFieldList", list);		    
			} else {
			    request.getSession().setAttribute("metaFieldList", new Vector());
			}
		    
		    this.getActionFormFromTransferObject(rto, frm, request);
		    		    
			//set current operation status for Updating	
		    frm.setSaveMethod(RiskForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		    
		    frm.setShowIssueConfirmation("off");
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formRisk.showForm", e);
		}
		
		return mapping.findForward(forward);				
	}
    

	public ActionForward removeRisk(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showRisk";
		try {
		    RiskForm rfrm = (RiskForm)form;
		    RiskDelegate rdel = new RiskDelegate();
			
			this.clearForm(rfrm, request);
			
			RiskTO rto = new RiskTO();
			rto.setId(rfrm.getId());
			rdel.removeRisk(rto);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeRisk");
			this.refresh(mapping, form, request, response );
			
		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);
		    rfrm.setSaveMethod(RiskForm.INSERT_METHOD, uto);
		    rfrm.setShowIssueConfirmation("off");
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formRisk.remove", e);
		}
	    
		return mapping.findForward(forward);	    
	}

		
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showRisk";		
		this.prepareForm(mapping, form, request, response);
		return mapping.findForward(forward);		
	}
	

	private void clearForm(ActionForm form, HttpServletRequest request){
	    RiskForm rfrm = (RiskForm)form;		
	    rfrm.clear();
	    
	    //set the relationship of risk
	    request.getSession().setAttribute("riskRelationshipList", new Vector());
	    
	    this.clearMessages(request);
	}
	
	
	private void getActionFormFromTransferObject(RiskTO to, RiskForm frm, HttpServletRequest request){
	    frm.setId(to.getId());
	    frm.setCategoryId(to.getCategory().getId());
	    frm.setContingency(to.getContingency());
	    frm.setCreationDate(to.getCreationDate());
	    frm.setDescription(to.getDescription());
	    frm.setName(to.getName());
	    frm.setProbability(to.getProbability());
	    frm.setImpact(to.getImpact());
	    frm.setProject(to.getProject());
	    frm.setProjectId(to.getProject().getId());
	    frm.setResponsible(to.getResponsible());
	    frm.setStatus(to.getStatus().getId());
	    frm.setStrategy(to.getStrategy());
	    frm.setTendency(to.getTendency());   
	    frm.setAdditionalFields(to.getAdditionalFields());	    
	    
	    frm.setCostImpact(to.getCostImpact());
	    frm.setTimeImpact(to.getTimeImpact());
	    frm.setQualityImpact(to.getQualityImpact());
	    frm.setScopeImpact(to.getScopeImpact());
	    
	    if (to.getProject()!=null) {
	    	frm.setProjectName(to.getProject().getName());	
	    } else {
	    	frm.setProjectName("");
	    }	    
	    
	    if (to.getRiskType()!=null) {
	    	frm.setRiskType(to.getRiskType().toString());	
	    } else {
	    	frm.setRiskType(RiskTO.RISK_TYPE_THREAT+"");	
	    }
	    
	    //set the relationship of risk
	    request.getSession().setAttribute("riskRelationshipList", to.getRelationList());
	}
	
	
	private RiskTO getTransferObjectFromActionForm(RiskForm frm, HttpServletRequest request){
	    RiskTO rto = new RiskTO();
        rto.setId(frm.getId());
        rto.setCategory(new CategoryTO(frm.getCategoryId()));
        rto.setContingency(frm.getContingency());
        rto.setName(frm.getName());
        rto.setProbability(frm.getProbability());
        rto.setProject(new ProjectTO(frm.getProjectId()));
        rto.setResponsible(frm.getResponsible());
        rto.setStrategy(frm.getStrategy());
        rto.setImpact(frm.getImpact());
        rto.setTendency(frm.getTendency());
        rto.setStatus(new RiskStatusTO(frm.getStatus()));        
	    rto.setDescription(frm.getDescription());
	    rto.setCreationDate(frm.getCreationDate());	    
	    
	    rto.setCostImpact(frm.getCostImpact());
	    rto.setTimeImpact(frm.getTimeImpact());
	    rto.setQualityImpact(frm.getQualityImpact());
	    rto.setScopeImpact(frm.getScopeImpact());
	    rto.setRiskType(new Integer(frm.getRiskType()));
	    
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    rto.setHandler(uto);

		Vector metaFieldList = (Vector)request.getSession().getAttribute("metaFieldList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, rto);	    
		if (metaFieldList!=null) {
		    frm.setAdditionalFields(rto.getAdditionalFields());
		}

	    return rto;
	}

	
	public Vector getProbabilityCombo(HttpServletRequest request){
	    Vector response = new Vector();
	    for(int i=1; i<=5; i++){
	        TransferObject to = new TransferObject();
	        to.setId(i+""); 
	        to.setGenericTag(this.getBundleMessage(request, "label.formRisk.probability." + i));
	        response.addElement(to);
	    }
	    return response;
	}

	public Vector getImpactCombo(HttpServletRequest request){
	    Vector response = new Vector();
	    for(int i=1; i<=5; i++){
	        TransferObject to = new TransferObject();
	        to.setId(i+""); 
	        to.setGenericTag(this.getBundleMessage(request, "label.formRisk.impact." + i));
	        response.addElement(to);
	    }
	    return response;
	}
	
	public Vector getTendencyCombo(HttpServletRequest request){
	    Vector response = new Vector();
	    for(int i=1; i<=3; i++){
	        TransferObject to = new TransferObject();
	        to.setId(i+"");
	        to.setGenericTag(this.getBundleMessage(request, "label.formRisk.tendency." + i));
	        response.addElement(to);
	    }
	    return response;
	}	
	
}
