package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class CostForm extends GeneralStrutsForm {

	public static final String MODE_ALL            = "ALL";
	public static final String MODE_ONLY_EXPENSES  = "ONLY_EXP";
	public static final String MODE_ONLY_RESOURCES = "ONLY_RES";

	public static final String TYPE_PROJECT        = "PRJ";
	public static final String TYPE_ACCOUNT_CODE   = "ACC";
	public static final String TYPE_CATEGORY       = "CAT";
	
	private static final long serialVersionUID = 1L;
	
	private String projectId;
	
	private String accountCode;
	
	private String granularity = "3";
	
	private String initialDate;
	
	private String finalDate;
	
	private String viewMode = "ALL";

	private String canShowChart;
	
	private boolean hideOutOfRange = false;
	
	private String costHtmlTitle;
	
	private String costHtmlBody;

	private String showEditCost = "off";
	
	private String type = "PRJ";
	
	private String expenseId;
	
	private String expenseReportURL;
	
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.hideOutOfRange = false;
	}
	
	
	/////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	/////////////////////////////////////////
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String newValue) {
		this.accountCode = newValue;
	}

	
	/////////////////////////////////////////
	public String getGranularity() {
		return granularity;
	}
	public void setGranularity(String newValue) {
		this.granularity = newValue;
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
	public String getViewMode() {
		return viewMode;
	}
	public void setViewMode(String newValue) {
		this.viewMode = newValue;
	}
	
	
	/////////////////////////////////////////	
	public String getCanShowChart() {
		return canShowChart;
	}
	public void setCanShowChart(String newValue) {
		this.canShowChart = newValue;
	}
	
	
	/////////////////////////////////////////	
	public boolean getHideOutOfRange() {
		return hideOutOfRange;
	}
	public void setHideOutOfRange(boolean newValue) {
		this.hideOutOfRange = newValue;
	}
	
	
	/////////////////////////////////////////	
	public String getCostHtmlTitle() {
		return costHtmlTitle;
	}
	public void setCostHtmlTitle(String newValue) {
		this.costHtmlTitle = newValue;
	}
	
	
	/////////////////////////////////////////	
	public String getCostHtmlBody() {
		return costHtmlBody;
	}
	public void setCostHtmlBody(String newValue) {
		this.costHtmlBody = newValue;
	}
	
	
	/////////////////////////////////////////		
	public String getShowEditCost() {
		return showEditCost;
	}
	public void setShowEditCost(String newValue) {
		this.showEditCost = newValue;
	}
	
	
	/////////////////////////////////////////			
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
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
