package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CostInstallmentTO;
import com.pandora.CostStatusTO;
import com.pandora.CostTO;
import com.pandora.ExpenseTO;

public class ExpenseStatusDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		return getStatus();
	}

	private String getStatus() {
		String response = "ERR";
		int waitingCount = 0;
		int budgetedCount = 0;
		int paidCount = 0;
		int cancelCount = 0;
		
		if (this.getObject()!=null) {
			ExpenseTO obj = (ExpenseTO)this.getObject();
			if (obj.getExpensesItems()!=null) {
				for (CostTO cto : obj.getExpensesItems()) {
					if (cto.getInstallments()!=null){
						for (CostInstallmentTO cito : cto.getInstallments()) {
							if (cito.getCostStatus()!=null && cito.getCostStatus().getStateMachineOrder()!=null) {
								Integer status = cito.getCostStatus().getStateMachineOrder();
								
								if (status.equals(CostStatusTO.STATE_MACHINE_WAITING)) {
									waitingCount++;
								} else if (status.equals(CostStatusTO.STATE_MACHINE_BUDGETED)) {
									budgetedCount++;
								} else if (status.equals(CostStatusTO.STATE_MACHINE_CANCELED)) {
									cancelCount++;
								} else if (status.equals(CostStatusTO.STATE_MACHINE_PAID)) {
									paidCount++;
								}
							}
						}
					}
				}
			}
			
			if (waitingCount>0) {
				response = super.getBundleMessage("label.expense.status.0");
			} else if (budgetedCount>0) {
				response = super.getBundleMessage("label.expense.status.1");
			} else if (paidCount>0 && cancelCount>0) {
				response = super.getBundleMessage("label.expense.status.4");
			} else if (cancelCount>0 && paidCount==0) {
				response = super.getBundleMessage("label.expense.status.3");
			} else if (paidCount>0 && cancelCount==0) {
				response = super.getBundleMessage("label.expense.status.2");
			} else {
				response = "err";
			}
		}
		return response;
	}

	
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	
	public String contentToSearching(Object columnValue) {
		return getStatus();
	}
	

}
