package com.pandora.gui.struts.form;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

public class ExpenseForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String expenseId;
	
	private String projectId;
	
	private String username;
	
	private String comment;
	
	private String totalExpense;

	private String removeExpenseId;
	
	private String showSaveButton = "on";
	
	private String changeProject = "on";
	
	private String showAddForm = "off";
	
	private String reportURL;

	
	public void clear(){
		this.expenseId = null;
		this.comment = null;
		this.totalExpense = null;
		this.removeExpenseId = null;
		this.showSaveButton = "on";
		this.changeProject = "on";
		this.showAddForm = "off";
		this.reportURL = null;
	}
	
	
	/////////////////////////////////////	
	public String getExpenseId() {
		return expenseId;
	}
	public void setExpenseId(String newValue) {
		this.expenseId = newValue;
	}


	/////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	/////////////////////////////////////
	public String getUsername() {
		return username;
	}
	public void setUsername(String newValue) {
		this.username = newValue;
	}

	
	/////////////////////////////////////
	public String getComment() {
		return comment;
	}
	public void setComment(String newValue) {
		this.comment = newValue;
	}

	
	/////////////////////////////////////
	public String getTotalExpense() {
		return totalExpense;
	}
	public void setTotalExpense(String newValue) {
		this.totalExpense = newValue;
	}
	
	
	/////////////////////////////////////
	public String getRemoveExpenseId() {
		return removeExpenseId;
	}
	public void setRemoveExpenseId(String newValue) {
		this.removeExpenseId = newValue;
	}
	
	
	/////////////////////////////////////	
	public String getShowSaveButton() {
		return showSaveButton;
	}
	public void setShowSaveButton(String newValue) {
		this.showSaveButton = newValue;
	}
	
	
	/////////////////////////////////////		
	public String getChangeProject() {
		return changeProject;
	}
	public void setChangeProject(String newValue) {
		this.changeProject = newValue;
	}

	
	/////////////////////////////////////		
	public String getShowAddForm() {
		return showAddForm;
	}
	public void setShowAddForm(String newValue) {
		this.showAddForm = newValue;
	}

	
	/////////////////////////////////////		
	public String getReportURL() {
		return reportURL;
	}
	public void setReportURL(String newValue) {
		this.reportURL = newValue;
	}

	
	/**
	 * Validate the form.
	 */
	@SuppressWarnings("unchecked")
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (operation!=null && operation.equals("saveExpense")){

			Vector iList = (Vector)request.getSession().getAttribute("expenseItemList");
			if (iList==null || iList.size()==0){
		        errors.add("Expenses", new ActionError("validate.expense.blankList") );
		    }

		}
	    
		return errors;
	}

}
