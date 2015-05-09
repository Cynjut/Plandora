package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.AttachmentHistoryTO;
import com.pandora.helper.HtmlUtil;

/**
 */
public class AttachmentHistoryGridContentDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "&nbsp;";
		
		AttachmentHistoryTO hto = (AttachmentHistoryTO)this.getObject();
		if (tag.equals("")) {
			if (hto.getHistory()!=null && !hto.getHistory().trim().equals("")){
			    String altValue = this.getBundleMessage("label.grid.comment");
				image = "<a href=\"javascript:viewContent('" + this.getListIndex() + "');\" border=\"0\"> \n";
				image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/comment.gif\" >";
				image += "</a>";
			}		    		    
		} else if (tag.equals("status")) {
		    image = this.getBundleMessage("label.formAttachment.Status." + hto.getStatus());
		}
		return image;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
