package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.helper.HtmlUtil;

public class AttachmentGridDeleteDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
        return decorate(columnValue, "remove");		
	}

	public String decorate(Object columnValue, String tag) {
		String image = "";
		String altValue = this.getBundleMessage("label.grid.remove");
		String confirmValue = this.getBundleMessage("message.confirmRemoveAttachment");
			
		image ="<a href=\"javascript:removeAttach('" + columnValue + "'," + tag + ", '" + confirmValue + "');\" border=\"0\"> \n";
		image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/remove.gif\" >";
		image += "</a>"; 				
		return image;
	}

	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}

}
