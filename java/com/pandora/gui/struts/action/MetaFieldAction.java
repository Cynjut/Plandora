package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.MetaFieldTO;
import com.pandora.MetaFormTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.MetaFormDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.AreaForm;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.MetaFieldForm;
import com.pandora.gui.struts.form.ResTaskForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into MetaField form
 */
public class MetaFieldAction extends GeneralStrutsAction {

	/**
	 * Shows the Meta Field form
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showMetaField";
		
		try {
		    MetaFieldForm mffrm = (MetaFieldForm)form;
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    
		    //get all Projects from data base and put into http session (to be displayed by combo)
		    ProjectDelegate pdel = new ProjectDelegate();
		    Vector<ProjectTO> prjList = pdel.getProjectList();
		    
		    ProjectTO allPrj = new ProjectTO("0");
		    allPrj.setName(this.getBundleMessage(request, "label.all"));
		    prjList.add(allPrj);

		    request.getSession().setAttribute("projectList", prjList);

		    this.refresh(mapping, form, request, response);

		    //set the current user connected
		    mffrm.setSaveMethod(ResTaskForm.INSERT_METHOD, uto);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formMetaField.showForm", e);
		}
		
		return mapping.findForward(forward);		
	}

	
	/**
	 * Update or Insert data of MetaField into data base
	 */
	public ActionForward saveMetaField(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){

	    String forward = "showMetaField";
		String errorMsg = "error.formMetaField.showForm";
		String succeMsg = "message.success";
		
		try {
		    MetaFieldForm mffrm = (MetaFieldForm)form;
		    MetaFieldDelegate mfdel = new MetaFieldDelegate();

			//create an MetaField object based on html fields
		    MetaFieldTO mfto = this.getTransferObjectFromActionForm(mffrm, request);
									
			if (mffrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){
			    errorMsg = "error.formMetaField.insert";
			    succeMsg = "message.insertMetaField";
			    mfdel.insertMetaField(mfto);
			    this.clearForm(form, request);
			} else {
			    errorMsg = "error.formMetaField.update";
			    succeMsg = "message.updateMetaField";
			    mfdel.updateMetaField(mfto);
			}
			
			//set success message into http session
			this.setSuccessFormSession(request, succeMsg);
			
			//refresh lists on form...
			this.refresh(mapping, form, request, response );

		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);
		    mffrm.setSaveMethod(ResTaskForm.UPDATE_METHOD, uto);
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}
		return mapping.findForward(forward);		

	}

	
	/**
	 * Get a specific Meta Field object from data base and display into form. 
	 */
	public ActionForward editMetaField(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showMetaField";	    
	    MetaFieldDelegate mfdel = new MetaFieldDelegate();
		
		try {
		    MetaFieldForm mffrm = (MetaFieldForm)form;
		    MetaFieldTO mfto = mfdel.getMetaFieldObject(new MetaFieldTO(mffrm.getId()));
		    
		    this.getActionFormFromTransferObject(mfto, mffrm, request);
		    this.refreshApplyCombo(mapping, form, request, response);
		    
			//set current operation status for Updating	
		    mffrm.setSaveMethod(AreaForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formMetaField.showForm", e);
		}
		
		return mapping.findForward(forward);		
	}

	/**
	 * Remove a Meta Field object from data base 
	 */
	public ActionForward removeMetaField(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showMetaField";

		try {
		    MetaFieldForm mffrm = (MetaFieldForm)form;
		    MetaFieldDelegate mfdel = new MetaFieldDelegate();
			
		    //create an Meta Field TO object based on html fields
			MetaFieldTO mfto = new MetaFieldTO();
			mfto.setId(mffrm.getId());
		    
			//clear form and messages
			this.clearForm(mffrm, request);
			
			//remove MetaField from data base
			mfdel.removeMetaField(mfto);
			
			/* TODO
			 * org.postgresql.util.PSQLException: ERRO: atualização ou exclusão em tabela "meta_field" viola restrição de chave estrangeira "addinfo_field_meta_field" em "additional_field" Detalhe: Chave (id)=(41990) ainda é referenciada pela tabela "additional_field"
			 * */
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeMetaField");
			this.refresh(mapping, form, request, response );
		
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formMetaField.remove", e);
		}
		return mapping.findForward(forward);		
	}

	
	/**
	 * Clear all values of current form.
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showMetaField";
		try {
		
			this.clearForm(form, request);
			
			//set current operation status for Insertion
			MetaFieldForm mffrm = (MetaFieldForm)form;
		    mffrm.setSaveMethod(AreaForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
			
		    refreshApplyCombo(mapping, form, request, response);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formMetaField.showForm", e);
		}
	    
		return mapping.findForward(forward);		
	}	

	/**
	 * Refresh data of form
	 */
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) throws BusinessException{
	    String forward = "showMetaField";
	    
	    //get all MetaField from data base 		    
	    MetaFieldDelegate mfdel = new MetaFieldDelegate();
	    Vector<MetaFieldTO> mfList = mfdel.getMetaFieldList();
	    request.getSession().setAttribute("metaFieldList", mfList);	   
	    
	    //create a list of options to display into applyTo combo box
	    request.getSession().setAttribute("applyToList", this.getApplyToList(request));
	    
	    //create a list of options to display into Type combo box
	    request.getSession().setAttribute("typeList", this.getTypeList(request));
	    
	    //refresh category list
	    this.refreshProject(mapping, form, request, response);
		this.refreshApplyCombo(mapping, form, request, response);
	    
	    return mapping.findForward(forward); 
	}
	
	/**
	 * This method is called when the project comboBox is changed  
	 */
	public ActionForward refreshProject(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) throws BusinessException{
	    String forward = "showMetaField";
	    MetaFieldForm mffrm = (MetaFieldForm)form;
	    
	    Integer type = null;
	    if (mffrm.getApplyTo()!=null) {
	        if (mffrm.getApplyTo().equals(MetaFieldTO.APPLY_TO_REQUIREMENT+"")) {
	            type = CategoryTO.TYPE_REQUIREMENT;
	        } else if (mffrm.getApplyTo().equals(MetaFieldTO.APPLY_TO_TASK+"")) {
	            type = CategoryTO.TYPE_TASK;
	        } else if (mffrm.getApplyTo().equals(MetaFieldTO.APPLY_TO_OCCURRENCE+"")) {
	        	type = CategoryTO.TYPE_OCCURRENCE;
	        }
	    }
	    
	    //get all Category objects based on project id selected
	    if (type!=null) {
		    CategoryDelegate cdel = new CategoryDelegate();
		    ProjectTO pto = new ProjectTO(mffrm.getProjectId());
		    Vector<CategoryTO> cList = cdel.getCategoryListByType(type, pto, false);
		    request.getSession().setAttribute("categoryList", cList);	        
	    } else {
	        request.getSession().setAttribute("categoryList", new Vector<CategoryTO>());
	    }
	    
	    this.checkMetaFormSelection(mffrm);
	    
	    return mapping.findForward(forward);
	}

	
	/**
	 * This method is called when the applyList comboBox is changed  
	 */	
	public ActionForward refreshApplyCombo(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) throws BusinessException{
	    String forward = "showMetaField";
	    MetaFieldForm mffrm = (MetaFieldForm)form;
	    
        mffrm.setIsDisableProject("off");
	    
	    if (mffrm.getApplyTo()!=null && mffrm.getApplyTo().equals(MetaFieldTO.APPLY_TO_PROJECT+"")) {
		    mffrm.setIsDisableCategory("on");
		    mffrm.setCategoryId(null);
	    } else {
	        mffrm.setIsDisableCategory("off");
	    }
	    
	    this.checkMetaFormSelection(mffrm);
	    this.refreshProject(mapping, form, request, response);
	    
	    return mapping.findForward(forward);	    
	}
	
	
	private void clearForm(ActionForm form, HttpServletRequest request){
	    MetaFieldForm mffrm = (MetaFieldForm)form;		
	    mffrm.clear();
	    this.clearMessages(request);
	}
	
	
	private void checkMetaFormSelection(ActionForm form){
	    MetaFieldForm mffrm = (MetaFieldForm)form;
	    
	    if (mffrm.getApplyTo()!=null && mffrm.getApplyTo().startsWith("F")) {
	        mffrm.setIsDisableCategory("on");
	        mffrm.setIsDisableProject("on");
	        mffrm.setCategoryId(null);
	        mffrm.setProjectId(ProjectTO.PROJECT_ROOT_ID);
	    }
	}
	
	
	private Vector<TransferObject> getTypeList(HttpServletRequest request){
	    Vector<TransferObject> typeList = new Vector<TransferObject>();
	    
	    for (int i=1; i<=6; i++) {
		    TransferObject option = new TransferObject();
		    option.setId(i+"");
		    option.setGenericTag(this.getBundleMessage(request, "label.formMetaField.type" + i));
		    typeList.addElement(option);	        
	    }
	    TransferObject option = new TransferObject("9", "Grid");
	    typeList.addElement(option);
	    
	    return typeList;
	}
	
	
	private Vector<TransferObject> getApplyToList(HttpServletRequest request){
	    Vector<TransferObject> applyToList = new Vector<TransferObject>();
	    MetaFormDelegate mfrmDel = new MetaFormDelegate();
	    try {	    
		    TransferObject option1 = new TransferObject();
		    option1.setId(MetaFieldTO.APPLY_TO_REQUIREMENT.toString());
		    option1.setGenericTag(this.getBundleMessage(request, "label.formMetaField.applyTo.requirement"));
		    applyToList.addElement(option1);
		    
		    TransferObject option2 = new TransferObject();
		    option2.setId(MetaFieldTO.APPLY_TO_TASK.toString());
		    option2.setGenericTag(this.getBundleMessage(request, "label.formMetaField.applyTo.task"));
		    applyToList.addElement(option2);
	
		    TransferObject option3 = new TransferObject();
		    option3.setId(MetaFieldTO.APPLY_TO_PROJECT.toString());
		    option3.setGenericTag(this.getBundleMessage(request, "label.formMetaField.applyTo.project"));
		    applyToList.addElement(option3);

		    TransferObject option4 = new TransferObject();
		    option4.setId(MetaFieldTO.APPLY_TO_RISK.toString());
		    option4.setGenericTag(this.getBundleMessage(request, "label.formMetaField.applyTo.risk"));
		    applyToList.addElement(option4);

		    TransferObject option5 = new TransferObject();
		    option5.setId(MetaFieldTO.APPLY_TO_INVOICE.toString());
		    option5.setGenericTag(this.getBundleMessage(request, "label.formMetaField.applyTo.invoice"));
		    applyToList.addElement(option5);

		    TransferObject option6 = new TransferObject();
		    option6.setId(MetaFieldTO.APPLY_TO_EXPENSE.toString());
		    option6.setGenericTag(this.getBundleMessage(request, "label.formMetaField.applyTo.expense"));
		    applyToList.addElement(option6);

		    TransferObject option7 = new TransferObject();
		    option7.setId(MetaFieldTO.APPLY_TO_COST.toString());
		    option7.setGenericTag(this.getBundleMessage(request, "label.formMetaField.applyTo.cost"));
		    applyToList.addElement(option7);

		    TransferObject option8 = new TransferObject();
		    option8.setId(MetaFieldTO.APPLY_TO_OCCURRENCE.toString());
		    option8.setGenericTag(this.getBundleMessage(request, "label.formMetaField.applyTo.occurrence"));
		    applyToList.addElement(option8);
		    
		    //get a list of meta forms and put into list
		    Vector<MetaFormTO> v = mfrmDel.getMetaFormList();
            if (v!=null) {
                Iterator<MetaFormTO> i = v.iterator();
                while(i.hasNext()) {
                    MetaFormTO mfrmto = i.next();
                    mfrmto.setGenericTag(mfrmto.getName());
                    mfrmto.setId("F" + mfrmto.getId());
                    applyToList.add(mfrmto);
                }
            }
            
        } catch (BusinessException e) {
            e.printStackTrace();
        }
	    
	    return applyToList;
	}

	
	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 */
	private void getActionFormFromTransferObject(MetaFieldTO to, MetaFieldForm frm, HttpServletRequest request){
	    frm.setId(to.getId());
	    frm.setName(to.getName());
	    frm.setDomain(to.getDomain());
	    frm.setType(to.getType().toString());
	    frm.setProjectId(to.getProject().getId());
	    
	    if (to.getOrder()!=null) {
	    	frm.setOrder(to.getOrder().toString());	
	    } else {
	    	frm.setOrder(null);
	    }
	    
	    
	    if (to.getApplyTo().equals(MetaFieldTO.APPLY_TO_CUSTOM_FORM)) {
	        frm.setApplyTo("F" + to.getMetaform().getId());
	        frm.setMetaFormId(to.getMetaform().getId());
	    } else {
	        frm.setApplyTo(to.getApplyTo().toString());
	        frm.setMetaFormId(null);
	    }
	    
	    if (to.getCategory()!=null) {
	        frm.setCategoryId(to.getCategory().getId());
	    } else {
	        frm.setCategoryId("0");
	    }
	    
	    if (to.getDisableDate()!=null) {
	        frm.setIsDisabledRequest("on");
	    } else {
	        frm.setIsDisabledRequest("off");
	    }
	    
	    if (to.getMetaform()!=null) {
	        frm.setMetaFormId(to.getMetaform().getId());    
	    }
	    
	    if (to.isMandatory() != null) {
	        frm.setIsMandatory(to.isMandatory()); 
	    }

	    if (to.getIsQualifier() != null) {
	        frm.setIsQualifier(to.getIsQualifier()); 
	    }
	    
	    frm.setHelpContent(to.getHelpContent());
	}


	/**
	 * Put data of html fields into TransferObject 
	 */
	private MetaFieldTO getTransferObjectFromActionForm(MetaFieldForm frm, HttpServletRequest request){
	    MetaFieldTO mfto = new MetaFieldTO();
	    mfto.setId(frm.getId());
	    mfto.setName(frm.getName());
	    mfto.setType(new Integer(frm.getType()));
    	mfto.setOrder(frm.getOrder() != null && !frm.getOrder().trim().equals("") ? new Integer(frm.getOrder()) : null);	
	    
	    if (frm.getApplyTo().startsWith("F")) {
	        mfto.setApplyTo(MetaFieldTO.APPLY_TO_CUSTOM_FORM);
	        mfto.setMetaform(new MetaFormTO(frm.getApplyTo().substring(1)));
	    } else {
	        mfto.setApplyTo(new Integer(frm.getApplyTo()));
		    mfto.setMetaform(null);
	    }
	    
	    mfto.setDomain(frm.getDomain());
	    mfto.setProject(new ProjectTO(frm.getProjectId()));
	    
	    if (frm.getCategoryId()!=null && !frm.getCategoryId().equals("") && !frm.getCategoryId().equals("0")) {
	        CategoryTO cto = new CategoryTO(frm.getCategoryId());
	        mfto.setCategory(cto);
	    } else {
	        mfto.setCategory(null);
	    }
	    
	    if (frm.getIsDisabledRequest()!=null && frm.getIsDisabledRequest().equals("on")) {
	        mfto.setDisableDate(DateUtil.getNow());
	    }
	    
	    if(frm.getIsMandatory() != null) {
	    	mfto.setIsMandatory(frm.getIsMandatory());
	    }
	    if(frm.getIsQualifier() != null) {
	    	mfto.setIsQualifier(frm.getIsQualifier());
	    }
	    
	    mfto.setHelpContent(frm.getHelpContent());
	    
	    return mfto;
	}
	
}
