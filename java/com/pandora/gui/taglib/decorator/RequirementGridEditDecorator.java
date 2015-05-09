package com.pandora.gui.taglib.decorator;

import java.util.Iterator;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CustomerTO;
import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.struts.form.ResourceHomeForm;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a edit html image.<br>
 * It works like GridEditDecorator (generic decorator) but has a special feature related with Requirement form.
 * If the status of current Requirement is different of "waiting approve" the icon cannot be displayed. 
 */
public class RequirementGridEditDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "REQ");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "";
		String altValue = this.getBundleMessage("label.grid.requestform.readonly"); 
		String imgSrc = "../images/lockedit.gif";
		
		try {
			tag = ResourceHomeForm.RO_MODE_SOURCE; //default value
			UserTO uto = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
		    
		    RequirementTO rto = (RequirementTO)this.getObject();
		    if (rto!=null && rto.getId()!=null) {
				boolean isWaiting = rto.getRequirementStatus().getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_WAITING);
				boolean isClosed = rto.getRequirementStatus().getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_CLOSE);
				
				if (uto.getId().equals(rto.getRequester().getId()) && (isWaiting || isClosed)){
				    
				    if (isClosed) {
						//retrieve data about the requirement customer from data base
						UserDelegate udel = new UserDelegate();
						CustomerTO filter = rto.getRequester();
						filter.setProject(rto.getProject());
						CustomerTO cto = udel.getCustomer(filter);
				        
				        if ( cto.getBoolIsReqAcceptable()) {
					        altValue = this.getBundleMessage("label.grid.requestform.reopen");
					        imgSrc = "../images/reopen.gif";
					        tag = ResourceHomeForm.REF_REQ_SOURCE;		            
				        } else {
						    altValue = this.getBundleMessage("label.grid.requestform.adjust");
						    imgSrc = "../images/adjustreq.gif";
						    tag = ResourceHomeForm.ADJUST_REQ_SOURCE_WOUT_PRJ;
				        }
				        
				    } else {
				        imgSrc = "../images/edit.gif";
				        altValue = this.getBundleMessage("label.grid.requestform.edit");
				        tag = ResourceHomeForm.REQ_SOURCE;
				    }
				    
			    } else {
			    	ProjectTO project = rto.getProject();
		            if (this.checkUserIsLeader(project, uto)) {
					    altValue = this.getBundleMessage("label.grid.requestform.adjust");
					    imgSrc = "../images/adjustreq.gif";
					    if (isWaiting) {
					    	tag = ResourceHomeForm.ADJUST_REQ_SOURCE;    
					    } else {
					    	tag = ResourceHomeForm.ADJUST_REQ_SOURCE_WOUT_PRJ;
					    }
		            }
			    }
				
				image ="<a href=\"javascript:edit('" + columnValue + "', '" + tag + "');\" border=\"0\"> \n";
				image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"" + imgSrc + "\" >";
				image += "</a>";		        		        		    	
		    }
			
			
		} catch(Exception e){
		    System.out.println("RequirementGridEditDecorator error: " + e);
		    image = "&nbsp;";
		}

		return image;
    }
    
    /**
     * Check if a current requester of Requirement is a leader 
     * of project related with Requirement.
     */
    private boolean checkUserIsLeader(ProjectTO pto, UserTO requester) {
        boolean isLeader = false;
        
        Vector<LeaderTO> leaders = pto.getProjectLeaders();
        if (leaders==null) {
        	//set leaders to null to induce the lazzy initialization of project leader list
        	pto.setProjectLeaders(null); 
        	leaders = pto.getProjectLeaders();
        }
        
        if (leaders!=null){
            Iterator i = leaders.iterator();
            while(i.hasNext()){
                LeaderTO lead = (LeaderTO)i.next();
                if (lead.getId().equals(requester.getId())){
                    isLeader = true;
                    break;
                }
            }            
        }
        return isLeader;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
