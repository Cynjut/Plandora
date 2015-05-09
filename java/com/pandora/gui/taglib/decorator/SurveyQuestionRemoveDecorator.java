package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.helper.HtmlUtil;

public class SurveyQuestionRemoveDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "";
		String altValue = this.getBundleMessage("label.grid.remove");
		image ="<a href=\"javascript:removeQuestion('" + columnValue + "');\" border=\"0\"> \n";
		image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/remove.gif\" >";
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
