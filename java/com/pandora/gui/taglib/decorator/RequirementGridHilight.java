package com.pandora.gui.taglib.decorator;

import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PlanningRelationTO;
import com.pandora.RequirementTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator set the background color for requirement description 
 * depending on the requirement priority.
 */
public class RequirementGridHilight extends ColumnDecorator {

	
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        String response = "";
		RequirementTO rto = (RequirementTO)this.getObject();
		if (rto!=null) {
			response = response.concat(rto.getDescription());			
		}
	    return response;
    }
	
    
   	public String getPreContent(Object columnValue, String tag) {
   		String response = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>";
   		String iniIdent = "";
   		String colorTd = "";
        
		RequirementTO rto = (RequirementTO)this.getObject();
		if (rto!=null) {			
			UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);

			String color = HtmlUtil.getTextByPriority(uto, rto.getPriority());
			if (color!=null) {
				colorTd = "bgcolor=\"" + color + "\"";				
			}					
			
			if (tag!=null) {
				Vector<PlanningRelationTO> relations = rto.getRelationList();
				Vector<PlanningRelationTO> parent = PlanningRelationTO.getRelation(relations, PlanningRelationTO.RELATION_PART_OF, rto.getId(), true);
				if (parent!=null && parent.size()>0) {
					iniIdent = "<td width=\"" + (rto.getGridLevel() * 20) + "\" valign=\"top\"><img border=\"0\" align=\"right\" src=\"../images/treenode.gif\" /></td>";
				}
			}

			response = response + iniIdent + "<td class=\"tableCell\" " + colorTd + ">";
		}
		return response;
   	}
   	

   	public String getPostContent(Object columnValue, String tag) {
   		String response = "";

   		RequirementTO rto = (RequirementTO)this.getObject();		
		if (rto!=null) {			
			UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
			
			String color = HtmlUtil.getTextByPriority(uto, rto.getPriority());						
			if (color!=null) {
				response = response + "</td>";				
			}
			response = response + "</td></tr></table>";								
		}
		return response;   		
   	}


   	
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
    	return decorate(columnValue, null);
    }

    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
        return decorate(columnValue);
    }
    
}
