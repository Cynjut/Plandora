package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementTO;
import com.pandora.helper.HtmlUtil;

/**
 * 
 */
public class RequirementGridIdent extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        String content = "";
		RequirementTO rto = (RequirementTO)this.getObject();
		GridMindMapLinkDecorator link = new GridMindMapLinkDecorator();
		
		content = link.getLink(rto.getId(), null);
		
		int reopening = rto.getReopeningOccurrences().intValue();
		if (reopening>0) {
		    String altValue = this.getBundleMessage("label.resHome.reopening.hint.1");
		    if (reopening>1) {
		        altValue += " " + reopening + " " + this.getBundleMessage("label.resHome.reopening.hint.2");
		    } else {
		        altValue += ".";
		    }		    		   
		    content += " <img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/redflag.gif\" >";
		}
		
		if (rto.getAttachments()!=null && rto.getAttachments().size()>0) {
		    String altValue = this.getBundleMessage("label.resHome.attachment");
		    content += " <img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/attachment.gif\" >";
		}

		if (rto.getDiscussionTopics()!=null && rto.getDiscussionTopics().size()>0) {
		    String altValue = this.getBundleMessage("label.resHome.discussion");
		    content += " <img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/discussion.gif\" >";
		}
	
		return content;
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
		return columnValue + "";
    }
    
}
