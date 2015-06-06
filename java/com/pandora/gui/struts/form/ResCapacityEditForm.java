package com.pandora.gui.struts.form;


public class ResCapacityEditForm extends HosterRepositoryForm {

	private static final long serialVersionUID = 1L;
	
	private String currencySymbol;
	
	private String capacity;
	
	private String sinceDate;
	
	private String cost;

	private String editProjectId;
	
	private String editResourceId;
	
	private String commaDecimalSeparator;
	
	private String granularityLabel;

	private String canRemoveCap;
	
	/**
     * Clear values of Form
     */
    public void clear(){
    	this.cost = null;
    	this.capacity = null;
    	this.sinceDate = null;
    }

        
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
	


	////////////////////////////////////////////
	public String getCommaDecimalSeparator() {
		return commaDecimalSeparator;
	}
	public void setCommaDecimalSeparator(String newValue) {
		this.commaDecimalSeparator = newValue;
	}


	////////////////////////////////////////////
	public String getGranularityLabel() {
		return granularityLabel;
	}
	public void setGranularityLabel(String newValue) {
		this.granularityLabel = newValue;
	}

	
	////////////////////////////////////////////
	public String getCanRemoveCap() {
		return canRemoveCap;
	}
	public void setCanRemoveCap(String newValue) {
		this.canRemoveCap = newValue;
	}	
		
}
