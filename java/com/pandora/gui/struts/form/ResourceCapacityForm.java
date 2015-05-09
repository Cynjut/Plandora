package com.pandora.gui.struts.form;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.UserTO;
import com.pandora.helper.FormValidationUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

//TODO DEPRECATED
public class ResourceCapacityForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	
	private String capacityDate;
	
	private String capacityValue;
	
	private String costValue;
	
	private String resourceId;

	private String projectId;
	
	
	
    /**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		Locale loc = uto.getLocale();
		
		
		if (operation.equals("save") || operation.equals("delete")){
			
			String dateLbl = uto.getBundle().getMessage(loc, "title.resCapacity.since");
			String valueLbl = uto.getBundle().getMessage(loc, "title.resCapacity.value");
			String costLbl = uto.getBundle().getMessage(loc, "title.resCapacity.cost");

			if (this.capacityDate==null || this.capacityDate.trim().equals("")){
				errors.add("[" + dateLbl + "]", new ActionError("errors.required", "[" + dateLbl + "]") );
			} else {
				if (!StringUtil.checkChars(capacityDate, "0123456789/")) {
					errors.add("[" + dateLbl + "]", new ActionError("validate.invalidDate", "[" + dateLbl + "]") );
				}
				//FormValidationUtil.checkDate(errors, "[" + dateLbl + "]", capacityDate, loc, 2);				
			}

			if (this.capacityValue==null || this.capacityValue.trim().equals("")){
				errors.add("[" + valueLbl + "]", new ActionError("errors.required", "[" + valueLbl + "]") );
			} else {
				FormValidationUtil.checkInt(errors, "[" + valueLbl + "]", capacityValue);
				FormValidationUtil.checkMaxValue(errors, "[" + valueLbl + "]", capacityValue, 1440, loc);
				FormValidationUtil.checkMinValue(errors, "[" + valueLbl + "]", capacityValue, 0, loc);				
			}

			if (this.capacityValue!=null){
				FormValidationUtil.checkFloat(errors, "[" + costLbl + "]", this.costValue, loc);
				FormValidationUtil.checkMinValue(errors, "[" + costLbl + "]", this.costValue, 0, loc);
			}
			
			if (this.resourceId==null || this.resourceId.trim().equals("")){
				errors.add("resourceId", new ActionError("errors.required", "resourceId") );
			}

			if (this.projectId==null || this.projectId.trim().equals("")){
				errors.add("projectId", new ActionError("errors.required", "projectId") );
			}

		}
		
		return errors;
	}
   
	
	//////////////////////////////////////////
	public String getCapacityDate() {
		return capacityDate;
	}
	public void setCapacityDate(String newValue) {
		this.capacityDate = newValue;
	}

	
	//////////////////////////////////////////
	public String getCapacityValue() {
		return capacityValue;
	}
	public void setCapacityValue(String newValue) {
		this.capacityValue = newValue;
	}


	//////////////////////////////////////////
	public String getCostValue() {
		return costValue;
	}
	public void setCostValue(String newValue) {
		this.costValue = newValue;
	}


	//////////////////////////////////////////
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}
	
	
	//////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	
}
