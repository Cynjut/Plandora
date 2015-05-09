package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

public class AttachmentGridDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "???");        
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "";
		UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);	    
	    RequirementTO rto = (RequirementTO)this.getObject();
		
		if (uto.getId().equals(rto.getRequester().getId())){		
			String altValue = this.getBundleMessage("title.attachTagLib.show");
	    	image = "<a href=\"#\" onclick=\"displayMessage('../do/manageAttachment?operation=prepareForm&planningId=" + columnValue + "&source=" + tag + "', 600, 385);return false;\" border=\"0\"> \n";    
			image += "<center><img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/attachment.gif\" ></center>";
			image += "</a>"; 				
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
