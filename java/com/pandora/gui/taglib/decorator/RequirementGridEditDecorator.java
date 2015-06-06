package com.pandora.gui.taglib.decorator;

import java.util.HashMap;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CustomerTO;
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
    @SuppressWarnings("unchecked")
	public String decorate(Object columnValue, String tag) {
		String image = "";
		String altValue = this.getBundleMessage("label.grid.requestform.readonly"); 
		String imgSrc = "../images/lockedit.gif";
		
		try {
			tag = ResourceHomeForm.RO_MODE_SOURCE; //default value
			UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
		    
		    RequirementTO rto = (RequirementTO)this.getObject();
		    if (rto!=null && rto.getId()!=null) {
				boolean isWaiting = rto.getRequirementStatus().getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_WAITING);
				boolean isClosed = rto.getRequirementStatus().getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_CLOSE);
				
				if (uto.getId().equals(rto.getRequester().getId()) && (isWaiting || isClosed)){
				    
				    if (isClosed) {
				    	
						//retrieve data about the requirement customer from data base
				    	HashMap<String,CustomerTO> hm = (HashMap<String,CustomerTO>)super.getSession().getAttribute("REQ_GRID_EDIT_CUSTOMER_LIST_" + rto.getProject().getId());
				    	if (hm==null) {
				    		hm = new HashMap<String, CustomerTO>();
							UserDelegate udel = new UserDelegate();
							Vector<CustomerTO> customerList = udel.getCustomerByProject(rto.getProject(), true);
							for (CustomerTO cto : customerList) {
								if (hm.get(cto.getId())==null) {
									hm.put(cto.getId(), cto);
								}
							}
							super.getSession().setAttribute("REQ_GRID_EDIT_CUSTOMER_LIST_" + rto.getProject().getId(), hm);
				    	}

						CustomerTO cto = hm.get(rto.getRequester().getId());
				        if ( cto!=null && cto.getBoolIsReqAcceptable()) {
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
		            if (uto.isLeader(project)) {
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
    
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
