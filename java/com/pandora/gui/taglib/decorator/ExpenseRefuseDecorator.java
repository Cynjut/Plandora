package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.helper.HtmlUtil;

public class ExpenseRefuseDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "";
		String altValue = this.getBundleMessage("label.expense.tooltip.refuse");
		image ="<a href=\"javascript:refuseCost('" + columnValue + "');\" border=\"0\"> \n";
		image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/refuse.gif\" >";
		image += "</a>"; 				
		return image;
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return null;
	}

}
