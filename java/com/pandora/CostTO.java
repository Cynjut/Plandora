package com.pandora;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Vector;

import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;


public class CostTO extends PlanningTO {

	private static final long serialVersionUID = 1L;

	private CategoryTO category;
	
	private String name;
	
	private ProjectTO project;
		
	private String accountCode;

	private ExpenseTO expense;
	
	private Vector<CostInstallmentTO> installments;
	

	public CostTO() {
	}

	public CostTO(String newid) {
		this.setId(newid);
	}

	
	public String infoInstallments(Timestamp inidate, Timestamp finaldate, UserTO uto, Locale currencyLoc) {
		String response = "";
		if (installments!=null) {
			
			String dtPattern = uto.getCalendarMask();
			Locale loc = uto.getLocale();
			
			for (CostInstallmentTO cito : installments) {
				if (cito.getValue()!=null && cito.getDueDate()!=null && cito.getCostStatus()!=null) {
					if ( (inidate == null || !cito.getDueDate().before(inidate)) && 
						 (finaldate == null || cito.getDueDate().before(finaldate))) {
						
						String status = uto.getBundle().getMessage(loc, "label.cost.status.nopaid");	
						if (cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_WAITING)) {
							status = uto.getBundle().getMessage(loc, "label.cost.status.wait");
						} else if (cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_BUDGETED)) {
							status = uto.getBundle().getMessage(loc, "label.cost.status.budge");
						} else if (cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_PAID)) {
							status = uto.getBundle().getMessage(loc, "label.cost.status.paid");
						} else if (cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_CANCELED)) {
							status = uto.getBundle().getMessage(loc, "label.cost.status.cancel");
						}
						
						float value = ((float)cito.getValue()) / 100;
						response = response + "<li>" + DateUtil.getDate(cito.getDueDate(), dtPattern, loc) + " - " +    
									StringUtil.getCurrencyValue(value, currencyLoc) + " - <b>" + status + "</b></li>";	
					}
				}
			}
		}
		return response;
	}

	public boolean containValue(Timestamp inidate, Timestamp finaldate) {
		boolean response = false;
		if (installments!=null) {
			for (CostInstallmentTO cito : installments) {
				if (cito.getValue()!=null && cito.getDueDate()!=null && cito.getCostStatus()!=null) {
					if ( (inidate == null || !cito.getDueDate().before(inidate)) && 
						 (finaldate == null || cito.getDueDate().before(finaldate))){
						response = true;
						break;
					}
				}
			}
		}
		return response;
	}
	
	
	public Long summarizeInstallments(Timestamp inidate, Timestamp finaldate) {
		Long response = null;
		if (installments!=null) {
			long acc = 0;
			for (CostInstallmentTO cito : installments) {
				if (cito.getValue()!=null && cito.getDueDate()!=null && cito.getCostStatus()!=null) {
					if ( (inidate == null || !cito.getDueDate().before(inidate)) && 
						 (finaldate == null || cito.getDueDate().before(finaldate)) &&
						 !cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_CANCELED) &&
						 !cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_WAITING)) {
						acc+=cito.getValue().longValue();	
					}
				}
			}
			response = new Long(acc);
		}
		return response;
	}

	public Long getTotalValue() {
		Long response = null;
		if (installments!=null) {
			long acc = 0;
			for (CostInstallmentTO cito : installments) {
				acc+=cito.getValue().longValue();	
			}
			response = new Long(acc);
		}
		return response;
	}
	
	public Timestamp getFirstInstallmentDate() {
		Timestamp response = null;
		if (installments!=null) {
			CostInstallmentTO first = installments.firstElement();
			if (first!=null && first.getDueDate()!=null) {
				response = first.getDueDate();
			}
		}
		return response;
	}


	public boolean isOpen() {
		boolean response = true;
		if (installments!=null) {
			for (CostInstallmentTO cito : installments) {
				if (cito.getCostStatus()!=null && cito.getCostStatus().getStateMachineOrder()!=null && (
						 cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_CANCELED) ||
						 cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_PAID)) ) {
					response = false;
					break;
				}
			}
		}
		return response ;
	}
	
	
	public String getExpenseId(){
		String response = "";
		if (this.expense!=null && this.expense.getId()!=null) {
			response = this.expense.getId();
		}
		return response;
	}
	
	//////////////////////////////////	
	public Vector<CostInstallmentTO> getInstallments() {
		return installments;
	}
	public void setInstallments(Vector<CostInstallmentTO> newValue) {
		this.installments = newValue;
	}
	
	
	//////////////////////////////////
	public CategoryTO getCategory() {
		return category;
	}
	public void setCategory(CategoryTO newValue) {
		this.category = newValue;
	}

	
	//////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	//////////////////////////////////
	public ProjectTO getProject() {
		return project;
	}
	public void setProject(ProjectTO newValue) {
		this.project = newValue;
	}

	
	//////////////////////////////////
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String newValue) {
		this.accountCode = newValue;
	}

	
	//////////////////////////////////
	public ExpenseTO getExpense() {
		return expense;
	}
	public void setExpense(ExpenseTO newValue) {
		this.expense = newValue;
	}

}
