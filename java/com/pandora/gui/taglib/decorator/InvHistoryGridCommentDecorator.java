package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.InvoiceHistoryTO;
import com.pandora.helper.HtmlUtil;

public class InvHistoryGridCommentDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		
		InvoiceHistoryTO ihto = (InvoiceHistoryTO)this.getObject();
		if (ihto.getDescription()!=null && !ihto.getDescription().trim().equals("")){
		    
		    String altValue = this.getBundleMessage("label.grid.comment");
			image = "<a href=\"javascript:viewComment('" + this.getListIndex() + "');\" border=\"0\"> \n";
			image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/comment.gif\" >";
			image += "</a>";
		}
		return image;
	}

	@Override
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}
	

	@Override
	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}

}
