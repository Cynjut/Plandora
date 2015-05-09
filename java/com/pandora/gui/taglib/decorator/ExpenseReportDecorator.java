package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CostTO;
import com.pandora.ExpenseTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

public class ExpenseReportDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "";
		if (getObject() instanceof ExpenseTO) {
			String altValue = this.getBundleMessage("label.expense.viewreport");
			image ="<a href=\"javascript:showReport('" + columnValue + "');\" border=\"0\"> \n";
			image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/report.gif\" >";
			image += "</a>";
		} else if (getObject() instanceof CostTO) {
			UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
			CostTO cto = (CostTO)getObject();
			image = getExpenseLink(cto, uto);
		}
		return image;
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return null;
	}
	
	public String getExpenseLink(CostTO cto, UserTO uto){
		String response = "&nbsp;";		
		if (cto!=null && cto.getExpense()!=null && cto.getExpense().getId()!=null) {
			String eid = cto.getExpense().getId();
			String altValue = uto.getBundle().getMessage(uto.getLocale(), "label.expense.viewreport");
			response ="<a class=\"gridLink\" href=\"javascript:showExpenseReport('" + eid + "');\" border=\"0\" " + HtmlUtil.getHint(altValue) + ">\n";
			response += cto.getExpense().getId();
			response += "</a>";						
		}
		return response;
	}
}
