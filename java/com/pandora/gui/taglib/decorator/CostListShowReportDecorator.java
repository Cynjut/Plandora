package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.helper.HtmlUtil;

public class CostListShowReportDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String link = "";
		String altValue = HtmlUtil.getHint(this.getBundleMessage("label.expense.viewreport"));
		link ="<a class=\"gridLink\" href=\"javascript:showExpenseReport('" + columnValue + "');\" " + altValue + " border=\"0\"> \n";
		link += columnValue;
		link += "</a>"; 				
		return link;
		
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return null;
	}

}
