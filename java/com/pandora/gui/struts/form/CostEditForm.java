package com.pandora.gui.struts.form;

public class CostEditForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String editCostId;
	
	private String costValue;
	
	private String name;
	
	private String description;
	
	private String projectId;
	
	private String categoryId;
	
	private String accountCode;
	
	private String costType = "1";
	
	private String accountCodeSelected;
	
	private String installmentHtmlBody;
	
	private String installmentHtmlValidation;
	
	private String removeInstId;
	
	private String changeInstValue;
	
	private String changeInstId;
	
	private String changeInstType;
	
	private String forwardAfterSave;
	
	private String usedByExpenseForm = "off";
	
	
	public void clear(){
		this.editCostId = null;
		this.costValue = null;
		this.name = null;
		this.description = null;		
		this.categoryId = null;
		this.accountCode = null;
		this.costType = "1";		
		this.removeInstId = "";
		this.changeInstValue = "";
		this.changeInstId = "";
		this.changeInstType = "";
	}
	
	
	//////////////////////////////////////////    
    public String getCostType() {
		return costType;
	}
	public void setCostType(String newValue) {
		this.costType = newValue;
	}
	
	
	//////////////////////////////////////////
	public String getEditCostId() {
		return editCostId;
	}
	public void setEditCostId(String newValue) {
		this.editCostId = newValue;
	}

	
    //////////////////////////////////////////
	public String getCostValue() {
		return costValue;
	}
	public void setCostValue(String newValue) {
		this.costValue = newValue;
	}

	
    //////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
    //////////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}

	
    //////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}


    //////////////////////////////////////////
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String newValue) {
		this.categoryId = newValue;
	}


    //////////////////////////////////////////
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String newValue) {
		this.accountCode = newValue;
	}
	
	
    //////////////////////////////////////////
	public String getAccountCodeSelected() {
		return accountCodeSelected;
	}
	public void setAccountCodeSelected(String newValue) {
		this.accountCodeSelected = newValue;
	}
	
	
    //////////////////////////////////////////	
	public String getInstallmentHtmlBody() {
		return installmentHtmlBody;
	}
	public void setInstallmentHtmlBody(String newValue) {
		this.installmentHtmlBody = newValue;
	}
	

    //////////////////////////////////////////	
	public String getInstallmentHtmlValidation() {
		return installmentHtmlValidation;
	}
	public void setInstallmentHtmlValidation(String newValue) {
		this.installmentHtmlValidation = newValue;
	}


	//////////////////////////////////////////	
	public String getRemoveInstId() {
		return removeInstId;
	}
	public void setRemoveInstId(String newValue) {
		this.removeInstId = newValue;
	}

	
	//////////////////////////////////////////	
	public String getChangeInstValue() {
		return changeInstValue;
	}
	public void setChangeInstValue(String newValue) {
		this.changeInstValue = newValue;
	}
	
	
	//////////////////////////////////////////		
	public String getChangeInstId() {
		return changeInstId;
	}
	public void setChangeInstId(String newValue) {
		this.changeInstId = newValue;
	}
	
	
	//////////////////////////////////////////		
	public String getChangeInstType() {
		return changeInstType;
	}
	public void setChangeInstType(String newValue) {
		this.changeInstType = newValue;
	}
	
	
	//////////////////////////////////////////		
	public String getForwardAfterSave() {
		return forwardAfterSave;
	}
	public void setForwardAfterSave(String newValue) {
		this.forwardAfterSave = newValue;
	}

	
	//////////////////////////////////////////		
	public String getUsedByExpenseForm() {
		return usedByExpenseForm;
	}
	public void setUsedByExpenseForm(String newValue) {
		this.usedByExpenseForm = newValue;
	}	    
	
}
