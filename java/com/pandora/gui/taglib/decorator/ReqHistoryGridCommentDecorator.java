package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementHistoryTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a Requirement History grid cell with a 'view comment' icon.
 */
public class ReqHistoryGridCommentDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String image = "&nbsp;";
		
		RequirementHistoryTO rhto = (RequirementHistoryTO)this.getObject();
		if (rhto.getComment()!=null && !rhto.getComment().trim().equals("")){
		    
		    String altValue = this.getBundleMessage("label.grid.comment");
			image = "<a href=\"javascript:viewComment('" + this.getListIndex() + "');\" border=\"0\"> \n";
			image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/comment.gif\" >";
			image += "</a>";
		}
		return image;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        return decorate(columnValue);
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
