package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.AreaTO;
import com.pandora.delegate.AreaDelegate;
import com.pandora.exception.AreaNameAlreadyExistsException;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.AreaForm;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into Area form
 */
public class AreaAction extends GeneralStrutsAction{

	/**
	 * Shows the Manage Area (form)
	 */
	public ActionForward screenArea(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showArea";
		try {
			AreaDelegate nameAreaDel = new AreaDelegate();
		
			Vector<AreaTO> areaList = nameAreaDel.getAreaList();			
			request.getSession().setAttribute("areaList", areaList);
				
		} catch(BusinessException e){
			forward = "error";
			this.setErrorFormSession(request, "error.showAreaForm", e);		
		}
	
		return mapping.findForward(forward);
	}
	
	/**
	 * This method prepare the form for updating an user. This method get the 
	 * information of specific user from data base and put the data into the
	 * html fields.
	 */
	public ActionForward editArea(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showArea";
	    try {
		    AreaForm areafrm = (AreaForm)form;
			AreaDelegate adel = new AreaDelegate();
		
			//clear messages of form
			this.clearMessages(request);
		
			//set current operation status for Updating	
			areafrm.setSaveMethod(AreaForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		
			//create a area object used such a filter
			AreaTO filter = new AreaTO();
			filter.setId(areafrm.getId());
		
			//get a specific area from data base
			AreaTO ato = adel.getArea(filter);
		
			//put the data (from DB) into html fields
			this.getActionFormFromTransferObject(ato, areafrm);
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.prepareUpdateAreaForm", e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, "error.prepareUpdateAreaForm", e);
	    }
		return mapping.findForward(forward);		
	}
	
	/**
	 * Refresh grid with list of all users from data base.
	 */
	private void refreshAreaList(HttpServletRequest request) throws BusinessException{
		AreaDelegate udel = new AreaDelegate();
		Vector<AreaTO> areaList = udel.getAreaList();
		request.getSession().setAttribute("areaList", areaList);
	}
	
	/**
	 * Clear all values of current form.
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showArea";
		this.clearForm(form, request);
		this.clearMessages(request);
		return mapping.findForward(forward);		
	}
	
	
	/**
	 * Clear all values of current form.
	 */
	private void clearForm(ActionForm form, HttpServletRequest request){
	    AreaForm arfrm = (AreaForm)form;
		arfrm.clear();
		
		//set current operation status for Insertion
		arfrm.setSaveMethod(AreaForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
	}

	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showArea";
		try {
			this.refreshAreaList(request);	
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showAreaForm", e);		    
		}
		return mapping.findForward(forward);		
	}

	/**
	 * This method is performed after Save button click event.<br>
	 * Is used to update or insert data of user into data base.
	 */
	public ActionForward saveArea(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showArea";
		String errorMsg = "error.showAreaForm";
		String succeMsg = "message.success";
		
		try {
			AreaForm afrm = (AreaForm)form;
			AreaDelegate adel = new AreaDelegate();

			//create an AreaTO object based on html fields
			AreaTO uto = this.getTransferObjectFromActionForm(afrm);
			
			//check if area already exists
			adel.checkAreaName(uto);
						
			if (afrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){			    
			    errorMsg = "error.generic.insertFormError";
			    succeMsg = "message.insertArea";
			    adel.insertArea(uto);
			    this.clearForm(form, request);
			} else {
			    errorMsg = "error.generic.updateFormError";
			    succeMsg = "message.updateArea";
			    adel.updateArea(uto);
			}
			
			//set success message into http session
			this.setSuccessFormSession(request, succeMsg);
			
			//get all users from data base and put into http session (to be displayed by grid)
			this.refreshAreaList(request);
			
		} catch(AreaNameAlreadyExistsException e){
		    this.setErrorFormSession(request, "error.areanameAlreadyExists", e);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(Exception e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}
		return mapping.findForward(forward);		
	}
	
	/**
	 * Remove a selected area of data base.
	 */
	public ActionForward removeArea(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){   
		String forward = "showArea";
		try {
			AreaForm afrm = (AreaForm)form;
			AreaDelegate adel = new AreaDelegate();
			
			//create an UserTO object based on html fields
			AreaTO uto = new AreaTO();
			uto.setId(afrm.getId());
			
			//remove user from data base
			adel.removeArea(uto);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeArea");
			
			//get all users from data base and put into http session (to be displayed by grid)
			this.refreshAreaList(request);

			//clear form and messages
			this.clearMessages(request);
			this.clearForm(afrm, request);
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.generic.removeFormError", e);
		}
		return mapping.findForward(forward);		

	}	
	
	/**
	 * Put data of html fields into TransferObject 
	 */
	private AreaTO getTransferObjectFromActionForm(AreaForm frm){
		AreaTO uto = new AreaTO();
		uto.setId(frm.getId());
		uto.setName(frm.getName());
		uto.setDescription(frm.getDescription());
		return uto;
	}
	
	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 */
	private void getActionFormFromTransferObject(AreaTO ato, AreaForm areafrm){
		areafrm.setId(ato.getId());
		areafrm.setName(ato.getName());
		areafrm.setDescription(ato.getDescription());
	}
	
	
}