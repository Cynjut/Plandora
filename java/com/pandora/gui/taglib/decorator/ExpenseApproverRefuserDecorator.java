package com.pandora.gui.taglib.decorator;

import java.util.Locale;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CostInstallmentTO;
import com.pandora.CostStatusTO;
import com.pandora.CostTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.taglib.form.NoteIcon;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

public class ExpenseApproverRefuserDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		NoteIcon note = new NoteIcon();
		String content = "&nbsp;";
		UserDelegate udel = new UserDelegate();
		try {
			CostTO cto = (CostTO)getObject();
			Locale currencyLoc = udel.getCurrencyLocale();
			UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
			
			if (uto!=null && currencyLoc!=null && cto.getInstallments()!=null) {
				String description = "";
				
				for (CostInstallmentTO cito : cto.getInstallments()) {
					if (cito.getValue()!=null && cito.getDueDate()!=null && cito.getCostStatus()!=null) {

						Integer state = cito.getCostStatus().getStateMachineOrder();
						
						String permissionStr = "";
						if (cito.getApprover()!=null) {
							UserTO leader = udel.getUser(cito.getApprover());
							if (state.equals(CostStatusTO.STATE_MACHINE_CANCELED)) {
								permissionStr = " - <b>" + super.getBundleMessage("label.expense.refusedBy") + " " + leader.getUsername() + "</b>";   
							} else if (!state.equals(CostStatusTO.STATE_MACHINE_WAITING)) {
								permissionStr = " - <b>" + super.getBundleMessage("label.expense.approvedBy") + " " + leader.getUsername() + "</b>";
							}
						}

						String status = super.getBundleMessage("label.cost.status.nopaid");	
						if (state.equals(CostStatusTO.STATE_MACHINE_WAITING)) {
							status = super.getBundleMessage("label.cost.status.wait");
						} else if (state.equals(CostStatusTO.STATE_MACHINE_BUDGETED)) {
							status = super.getBundleMessage("label.cost.status.budge");
						} else if (state.equals(CostStatusTO.STATE_MACHINE_PAID)) {
							status = super.getBundleMessage("label.cost.status.paid");
						} else if (state.equals(CostStatusTO.STATE_MACHINE_CANCELED)) {
							status = super.getBundleMessage("label.cost.status.cancel");
						}
							
						float value = ((float)cito.getValue()) / 100;
						description = description + "<li>" + DateUtil.getDate(cito.getDueDate(), uto.getCalendarMask(), uto.getLocale()) + " - " +    
										StringUtil.getCurrencyValue(value, currencyLoc) + " - " + status + permissionStr + "</li>";	
					}
					content = note.getContent(description, "info");	
				}
			}			
		} catch(Exception e){
			e.printStackTrace();
		}
		return content;
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return null;
	}
}
