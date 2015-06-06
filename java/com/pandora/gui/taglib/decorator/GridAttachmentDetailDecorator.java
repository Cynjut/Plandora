package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

/**
 */
public class GridAttachmentDetailDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String image = "";
		LeaderTO leader = null;
		try {
		    //TODO aqui tem que ser o lider do projeto nao o lider de qualquer projeto!!
		    leader = (LeaderTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);    
		} catch(Exception e){
		    leader = null;
		}
        
        if (leader!=null) {            
		    String altValue = this.getBundleMessage("label.grid.detail");
			image +="<a href=\"javascript:openAttachmentHistPopup('" + columnValue + "');\" border=\"0\"> \n";
			image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/detailed.gif\" >";
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
