package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CustomerTO;
import com.pandora.LeaderTO;
import com.pandora.ResourceTO;
import com.pandora.UserTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a customized checkBox into grid cell.
 */
public class ProjectGridCheckBoxDecorator extends ColumnDecorator {
    
    /** Constant used to specify into html form, the checkBox that define if current user is a customer */
    public final static String CUSTOMER_COL = "CUSTOMER_COL";
    
    /** Constant used to specify into html form, the checkBox that define if current user is a resource */
    public final static String RESOURCE_COL = "RESOURCE_COL";
    
    /** Constant used to specify into html form, the checkBox that define if current user is a leader */
    public final static String LEADER_COL = "LEADER_COL";
    
    /** Constant used to specify into html form, the checkBox that define if current user is allowed to alloc him self */
    public final static String ALLOW_PRE_APPROVE_COL = "ALLOW_PRE_APPROVE_COL";
    
    /** Constant used to specify into html form, the checkBox that define if current user is allowed to see the customer of Requirement */
    public final static String ALLOW_SEE_CUSTOMER_COL = "ALLOW_SEE_CUSTOMER_COL";

    /** Constant used to specify into html form, the checkBox that define if current user is allowed to see the technical comments of resources */
    public final static String ALLOW_SEE_TECH_COMMENTS = "ALLOW_SEE_TECH_COMMENTS";

    /** Constant used to specify into html form, the checkBox that define if current user is allowed to see the discussion topics of requirement */
    public final static String ALLOW_SEE_DISCUSSION_COL = "ALLOW_SEE_DISCUSSION_COL";
    
    /** Constant used to specify into html form, the checkBox that define if current user is disabled of current project */
    public final static String DISABLE_USER_COL = "DISABLE_USER_COL";

    /** Constant used to specify into html form, the checkBox that define if current user is able to accept a Requirement of current project */
    public final static String REQ_ACCEPT_CUSTOMER_COL = "REQ_ACCEPT_CUSTOMER_COL";
    
    /** Constant used to specify into html form, the checkBox that define if current user is able to view the Requirements of other users of project */
    public final static String CAN_SEE_OTHER_REQS_COL = "CAN_SEE_OTHER_REQS_COL";

    /** Constant used to specify into html form, the checkBox that define if current user is able to manage reqs 'in the name of' someone else. */
    public final static String CAN_OPEN_OTHEROWNER_REQS_COL = "CAN_OPEN_OTHEROWNER_REQS_COL";
    
    public final static String CAN_SELF_ALLOC_COL = "CAN_SELF_ALLOC_COL";
   
    public final static String CAN_SEE_REPOSITORY_COL = "CAN_SEE_REPOSITORY_COL";
    
    public final static String CAN_SEE_INVOICE_COL = "CAN_SEE_INVOICE_COL";
    
    
    
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
		String response = "&nbsp;";
		
		UserTO uto = (UserTO)this.getObject();
		
		if (tag.equals(CUSTOMER_COL)){
		    if (uto instanceof CustomerTO) {
		        response = HtmlUtil.getChkBox(true, uto.getId(), tag, true);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, true);
            }
		    
		} else if (tag.equals(RESOURCE_COL)){
		    if (uto instanceof ResourceTO) {
		        response = HtmlUtil.getChkBox(true, uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }
		    
		} else if (tag.equals(LEADER_COL)){
		    if (uto instanceof LeaderTO) {
		        response = HtmlUtil.getChkBox(true, uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }
		    
		} else if (tag.equals(ALLOW_PRE_APPROVE_COL)){
		    if (uto instanceof CustomerTO) {
		        CustomerTO cto = (CustomerTO) uto;
		        response = HtmlUtil.getChkBox(cto.getBoolPreApproveReq(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }
		    
		} else if (tag.equals(ALLOW_SEE_CUSTOMER_COL)){
		    if (uto instanceof ResourceTO || uto instanceof LeaderTO) {
		        ResourceTO rto = (ResourceTO) uto;
		        response = HtmlUtil.getChkBox(rto.getBoolCanSeeCustomer(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }

		} else if (tag.equals(ALLOW_SEE_DISCUSSION_COL)){
		    if (uto instanceof CustomerTO) {
		        CustomerTO cto = (CustomerTO) uto;
		        response = HtmlUtil.getChkBox(cto.getBoolCanSeeDiscussion(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }

		} else if (tag.equals(CAN_SEE_OTHER_REQS_COL)){
		    if (uto instanceof CustomerTO) {
		        CustomerTO cto = (CustomerTO) uto;
		        response = HtmlUtil.getChkBox(cto.getBoolCanSeeOtherReqs(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }

		} else if (tag.equals(CAN_OPEN_OTHEROWNER_REQS_COL)){
		    if (uto instanceof CustomerTO) {
		        CustomerTO cto = (CustomerTO) uto;
		        response = HtmlUtil.getChkBox(cto.getBoolCanOpenOtherOwnerReq(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }

		} else if (tag.equals(CAN_SELF_ALLOC_COL)){
		    if (uto instanceof ResourceTO || uto instanceof LeaderTO) {
		        ResourceTO rto = (ResourceTO) uto;
		        response = HtmlUtil.getChkBox(rto.getBoolCanSelfAlloc(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }

		} else if (tag.equals(CAN_SEE_REPOSITORY_COL)){
		    if (uto instanceof ResourceTO || uto instanceof LeaderTO) {
		        ResourceTO rto = (ResourceTO) uto;
		        response = HtmlUtil.getChkBox(rto.getBoolCanSeeRepository(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }
		    
		} else if (tag.equals(CAN_SEE_INVOICE_COL)){
		    if (uto instanceof ResourceTO || uto instanceof LeaderTO) {
		        ResourceTO rto = (ResourceTO) uto;
		        response = HtmlUtil.getChkBox(rto.getBoolCanSeeInvoice(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }
		    
		} else if (tag.equals(DISABLE_USER_COL)){
		    if (uto instanceof CustomerTO) {
		        CustomerTO cto = (CustomerTO) uto;
		        response = HtmlUtil.getChkBox(cto.getBoolIsDisabled(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }

		} else if (tag.equals(REQ_ACCEPT_CUSTOMER_COL)){
		    if (uto instanceof CustomerTO) {
		        CustomerTO cto = (CustomerTO) uto;
		        response = HtmlUtil.getChkBox(cto.getBoolIsReqAcceptable(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, true);
            }

		} else if (tag.equals(ALLOW_SEE_TECH_COMMENTS)){
		    if (uto instanceof CustomerTO) {
		        CustomerTO cto = (CustomerTO) uto;
		        response = HtmlUtil.getChkBox(cto.getBoolCanSeeTechComments(), uto.getId(), tag, false);
            } else {
                response = HtmlUtil.getChkBox(false, uto.getId(), tag, false);
            }
		   		    
		}		     

		return response;
    }
    
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
    

}
