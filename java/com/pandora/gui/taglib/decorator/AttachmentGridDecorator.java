package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
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
		UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
		
		// Selection the decorate for task or requirement
		if( tag.equalsIgnoreCase("TSK")){
			ResourceTaskTO res = (ResourceTaskTO)this.getObject();
			
			if(res!=null && res.getTask()!=null){
				String idTask = res.getTask().getId();
				image = renderImage(idTask, tag);
			}	
		}else{		
			RequirementTO rto = (RequirementTO)this.getObject();

			boolean isRequester = (uto.getId().equals(rto.getRequester().getId()));
			if (isRequester) {
				image = renderImage(columnValue, tag);
			} else {
				boolean isLeader = (rto.getProject()!=null && uto.isLeader(rto.getProject()));
				if (isLeader) {
					image = renderImage(columnValue, tag);
				}
			}
	    
		}
		return image;
    }

	private String renderImage(Object columnValue, String tag) {
		String image;
		String altValue = this.getBundleMessage("title.attachTagLib.show");
		image = "<a href=\"javascript:displayMessage('../do/manageAttachment?operation=prepareForm&planningId=" + columnValue + "&source=" + tag + "', 600, 385);\" border=\"0\"> \n";		
		image += "<center><img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/attachment.gif\" ></center>";
		image += "</a>";
		return image;
	}

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
        return columnValue+"";
    }
    
	
}
