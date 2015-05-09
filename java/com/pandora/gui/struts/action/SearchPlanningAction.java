package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CustomerTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.UserTO;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.RequirementStatusDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.SearchPlanningForm;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;


/**
 * This class handle the actions performed into Searching form
 */
public class SearchPlanningAction extends GeneralStrutsAction {

    /**
     * Shows the Searching form
     */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){

		String forward = "showSearch";
		SearchPlanningForm srfrm = (SearchPlanningForm)form;

	    try {
	        this.clearMessages(request);
	        
	        Vector custList = new Vector();
	        Vector reqStatusList = new Vector();
	        if (srfrm.getType().equals(PlanningTO.PLANNING_REQUIREMENT)) {
	        	
		        //get the list of all customer of project to be set into filter combo
				UserDelegate udel = new UserDelegate();
				ProjectTO pto = new ProjectTO(srfrm.getProjectId());
				custList = udel.getCustomerByProject(pto, true);
				
				RequirementStatusDelegate rsdel = new RequirementStatusDelegate();
				reqStatusList = rsdel.getRequirementStatusList();
	        }
			request.getSession().setAttribute("customerList", custList);
			request.getSession().setAttribute("reqStatusList", reqStatusList);
			
			//set empty to grid of requirements  
			request.getSession().setAttribute("requirementList", new Vector());
			srfrm.setHasOptionsList("empty");
			
	    } catch(BusinessException e){
	        this.setErrorFormSession(request, "error.showSearchReqForm", e);
	    }

		return mapping.findForward(forward);
	}

	/**
	 * Do the search into data base and set the vector result into http session.  
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    
	    String forward = "showSearch";
	    Vector list = new Vector();
	    SearchPlanningForm srfrm = (SearchPlanningForm)form;	    

	    try {
	        if (srfrm.getProjectId()!=null){
			    
			    Vector kwList = StringUtil.getKeyworks(srfrm.getKeyword());
			    
			    if (srfrm.getType().equals(PlanningTO.PLANNING_REQUIREMENT)) {
				    ProjectTO pto = new ProjectTO(srfrm.getProjectId());			    	
				    RequirementDelegate rdel = new RequirementDelegate();
				    
				    CustomerTO cto = null;
				    if (srfrm.getCustomerId()!=null && !srfrm.getCustomerId().equals("")){
				        cto = new CustomerTO(srfrm.getCustomerId());
				    } 
				    Vector dbList = rdel.getListByFilter(pto, cto, kwList);
				    list = this.filterByStatus(dbList, srfrm);
				    
			    } else {
			        UserTO uto = SessionUtil.getCurrentUser(request);
			    	PlanningDelegate pdel = new PlanningDelegate();
			    	list = pdel.getListByKeyword(kwList, null, uto);
			    }
			    
			    if (list!=null && list.size()>0){
			        srfrm.setHasOptionsList("filled");    
			    }
	        }
		
	        request.getSession().setAttribute("requirementList", list);
	        
	    } catch(BusinessException e){
	        this.setErrorFormSession(request, "error.showSearchReqForm", e);
	    }
	    
	    return mapping.findForward(forward);
	}
	
	private Vector filterByStatus(Vector allList, SearchPlanningForm srfrm){
		Vector response = new Vector();
		if (allList!=null) {
			String stat = srfrm.getReqStatus();
			Iterator i  = allList.iterator();
			while(i.hasNext()) {
				RequirementTO rto = (RequirementTO)i.next();
				RequirementStatusTO rsto = rto.getRequirementStatus();
				if (stat.equals(rsto.getId())) {
					response.addElement(rto);
					
				} else if (stat.equals("-1")) {
					Integer stateMachine = rsto.getStateMachineOrder();
					if (!stateMachine.equals(RequirementStatusTO.STATE_MACHINE_CANCEL) &&
							!stateMachine.equals(RequirementStatusTO.STATE_MACHINE_CLOSE)) {
						response.addElement(rto);	
					}
				}
			}
		}
		return response;
	}
}
