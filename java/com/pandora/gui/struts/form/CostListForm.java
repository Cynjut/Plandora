package com.pandora.gui.struts.form;

import com.pandora.PreferenceTO;


public class CostListForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String projectId;
	
	private String categoryName;
	
	private String statusId;
	
	private String initialDate;
	
	private String finalDate;
	
	private String expenseId;
	
	private String expenseReportURL;
	
	
	
	public String getShowUpdateInBatch() {
    	String response = "off";
    	if (this.getShowAccountNumber().equals("on") || this.getShowStatus().equals("on") || this.getShowCategory().equals("on")) {
    		response = "on";
    	}
    	return response;
    }

    ////////////////////////////////////////////
    public String getShowAccountNumber() {
    	return super.getCurrentUser().getPreference().getPreference(PreferenceTO.LIST_ALL_COST_SW_ACCCD);
    }

    ////////////////////////////////////////////
    public String getShowStatus() {
        return super.getCurrentUser().getPreference().getPreference(PreferenceTO.LIST_ALL_COST_SW_STAT);
    }
    
    ////////////////////////////////////////////
    public String getShowCategory() {
        return super.getCurrentUser().getPreference().getPreference(PreferenceTO.LIST_ALL_COST_SW_CATEG);
    }

    
    
	/////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	
	/////////////////////////////////////////	
	public String getStatusId() {
		return statusId;
	}
	public void setStatusId(String newValue) {
		this.statusId = newValue;
	}

	
	/////////////////////////////////////////
	public String getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(String newValue) {
		this.initialDate = newValue;
	}

	
	/////////////////////////////////////////
	public String getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(String newValue) {
		this.finalDate = newValue;
	}
	
	
	/////////////////////////////////////////	
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String newValue) {
		this.categoryName = newValue;
	}

	
	/////////////////////////////////////////
	public String getExpenseId() {
		return expenseId;
	}
	public void setExpenseId(String newValue) {
		this.expenseId = newValue;
	}

	
	/////////////////////////////////////////
	public String getExpenseReportURL() {
		return expenseReportURL;
	}
	public void setExpenseReportURL(String newValue) {
		this.expenseReportURL = newValue;
	}

	
	
}
