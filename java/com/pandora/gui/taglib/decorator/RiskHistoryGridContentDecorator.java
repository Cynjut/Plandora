package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RiskHistoryTO;
import com.pandora.helper.HtmlUtil;

/**
 */
public class RiskHistoryGridContentDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String image = "&nbsp;";
		
		RiskHistoryTO hto = (RiskHistoryTO)this.getObject();
		if (hto.getContent()!=null && !hto.getContent().trim().equals("")){
		    
		    String altValue = this.getBundleMessage("label.grid.comment");
			image = "<a href=\"javascript:viewContent('" + this.getListIndex() + "');\" border=\"0\"> \n";
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
