package com.pandora.gui.struts.form;


public class ResCapacityEditForm extends HosterRepositoryForm {

	private static final long serialVersionUID = 1L;
	
	private String currencySymbol;
	
	private String capacity;
	
	private String sinceDate;
	
	private String cost;

	private String editProjectId;
	
	private String editResourceId;
	
	
	/**
     * Clear values of Form
     */
    public void clear(){
    	this.cost = null;
    	this.capacity = null;
    	this.sinceDate = null;
    }

    
/*
 	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		Locale loc = uto.getLocale();
		
		
		if (operation.equals("saveCapacity")){
			
			String dateLbl = uto.getBundle().getMessage(loc, "title.resCapacity.since");
			String valueLbl = uto.getBundle().getMessage(loc, "title.resCapacity.value");
			String costLbl = uto.getBundle().getMessage(loc, "title.resCapacity.cost");

			if (this.sinceDate==null || this.sinceDate.trim().equals("")){
				errors.add("[" + dateLbl + "]", new ActionError("errors.required", "[" + dateLbl + "]") );
			} else {
				if (!StringUtil.checkChars(sinceDate, "0123456789/")) {
					errors.add("[" + dateLbl + "]", new ActionError("validate.invalidDate", "[" + dateLbl + "]") );
				}
				//FormValidationUtil.checkDate(errors, "[" + dateLbl + "]", capacityDate, loc, 2);				
			}

			if (this.capacity==null || this.capacity.trim().equals("")){
				errors.add("[" + valueLbl + "]", new ActionError("errors.required", "[" + valueLbl + "]") );
			} else {
				FormValidationUtil.checkInt(errors, "[" + valueLbl + "]", capacity);
				FormValidationUtil.checkMaxValue(errors, "[" + valueLbl + "]", capacity, 1440, loc);
				FormValidationUtil.checkMinValue(errors, "[" + valueLbl + "]", capacity, 0, loc);				
			}

			if (this.cost!=null){
				FormValidationUtil.checkFloat(errors, "[" + costLbl + "]", this.cost, loc);
				FormValidationUtil.checkMinValue(errors, "[" + costLbl + "]", this.cost, 0, loc);
			}
			
			if (this.editResourceId==null || this.editResourceId.trim().equals("")){
				errors.add("resourceId", new ActionError("errors.required", "resourceId") );
			}

			if (this.editProjectId==null || this.editProjectId.trim().equals("")){
				errors.add("projectId", new ActionError("errors.required", "projectId") );
			}
		}
		
		return errors;
	}
*/    
    
	////////////////////////////////////////////
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String newValue) {
		this.currencySymbol = newValue;
	}

	
	////////////////////////////////////////////
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String newValue) {
		this.capacity = newValue;
	}

	
	////////////////////////////////////////////
	public String getSinceDate() {
		return sinceDate;
	}
	public void setSinceDate(String newValue) {
		this.sinceDate = newValue;
	}

	
	////////////////////////////////////////////
	public String getCost() {
		return cost;
	}
	public void setCost(String newValue) {
		this.cost = newValue;
	}


	////////////////////////////////////////////
	public String getEditProjectId() {
		return editProjectId;
	}
	public void setEditProjectId(String newValue) {
		this.editProjectId = newValue;
	}


	////////////////////////////////////////////
	public String getEditResourceId() {
		return editResourceId;
	}
	public void setEditResourceId(String newValue) {
		this.editResourceId = newValue;
	}

}
