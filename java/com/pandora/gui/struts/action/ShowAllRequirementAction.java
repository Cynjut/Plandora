package com.pandora.gui.struts.action;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.CustomerTO;
import com.pandora.NodeTemplateTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.TemplateTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.RequirementHistoryDelegate;
import com.pandora.delegate.RequirementStatusDelegate;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DifferentProjectFromParentReqException;
import com.pandora.exception.ParentReqNotExistsException;
import com.pandora.exception.ParentReqSameReqException;
import com.pandora.gui.struts.form.ShowAllRequirementForm;
import com.pandora.gui.struts.form.ShowAllTaskForm;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into 'Shows all Requirements' form
 */
public class ShowAllRequirementAction extends GeneralStrutsAction {
    
	/**
	 * Shows all Requirements for specific project
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
		ProjectDelegate pdel = new ProjectDelegate(); 
		String forward = "showAllRequirement";

		try {
		    
		    //clear current form
		    ShowAllRequirementForm frm = (ShowAllRequirementForm)form;
		    frm.setShowWorkflowDiagram("off");
		    this.clearMessages(request);
		    
		    //the project loading must be full (not-lazy) because the customers and resources must be load also...  
			ProjectTO pto = new ProjectTO(frm.getProjectRelated()); 
			pto = pdel.getProjectObject(pto, false);
			
			//set current user into form object
			frm.setSaveMethod(null, SessionUtil.getCurrentUser(request));

			//get all Projects from data base and put into http session (to be displayed by combo)
			this.refreshList(request, frm);
			
			//get all Requirement Status from data base and put into http session (to be displayed by combo)
			Vector statList = this.getListOfStatusForCombo(request);
			request.getSession().setAttribute("statusList", statList);

			//get all Customers of current project from data base and put into http session (to be displayed by combo)			
			Vector custList = this.getListOfCustomersForCombo(pto, request);
			request.getSession().setAttribute("requesterList", custList);

			//get all Categories of current project from data base and put into http session (to be displayed by combo)
			Vector categList = this.getListOfCategoriesForCombo(pto, request);
			request.getSession().setAttribute("categoryList", categList);
			
			//assembly a list of priorities to be used by html combo
			Vector priorList = this.getListOfPrioritiesForCombo(request);
			request.getSession().setAttribute("priorityList", priorList);

			//assembly a list of view mode to be used by html combo
			Vector viewmodeList = this.getListOfViewModeForCombo(request, pto);
			request.getSession().setAttribute("viewModeList", viewmodeList);
			
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showAllReqForm", e);
		}

		return mapping.findForward(forward);
	}
	

	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showAllRequirement");
	}	
	

	private void refreshList(HttpServletRequest request, ShowAllRequirementForm frm)	throws BusinessException {
		RequirementDelegate rdel = new RequirementDelegate();		
		ProjectDelegate pdel = new ProjectDelegate();
		
		ProjectTO pto = new ProjectTO(frm.getProjectRelated()); 
		pto = pdel.getProjectObject(pto, false);			
		
		Vector<RequirementTO> reqList = rdel.getListByProject(pto, frm.getStatusSelected(), 
			        frm.getRequesterSelected(), frm.getPrioritySelected(), 
			        frm.getCategorySelected(), frm.getViewModeSelected() );				
		request.getSession().setAttribute("allRequirementList", reqList);
	}
	
	
	public ActionForward clickNodeTemplate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			ShowAllTaskAction taskAction = new ShowAllTaskAction();
			
			ShowAllRequirementForm frm = (ShowAllRequirementForm)form;
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        
	        PrintWriter out = response.getWriter();
	        String content = taskAction.createDiagramNodeTip(request, frm.getPlanningId()).toString();
	        out.println(content);
	        
	        out.flush();
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showAllReqForm", e);
		}

		return null;
	}

	
	public ActionForward prepareWorkflow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showAllTaskWorkflow";			    		
		ShowAllRequirementForm frm = (ShowAllRequirementForm)form;
	    frm.setShowWorkflowDiagram("off");
		return mapping.findForward(forward);
	}


	public ActionForward updateInBatch(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showAllRequirement";
		RequirementDelegate rdel = new RequirementDelegate();
		RequirementHistoryDelegate rhdel = new RequirementHistoryDelegate();
		try {
		    ShowAllRequirementForm frm = (ShowAllRequirementForm)form;			
			frm.setShowWorkflowDiagram("off");
			boolean saveProcessed = false;
			Vector<RequirementTO> reqList = (Vector<RequirementTO>) request.getSession().getAttribute("allRequirementList");
	        Iterator<RequirementTO> i = reqList.iterator();
	        while(i.hasNext()) {
	        	RequirementTO rto = (RequirementTO)i.next();
	        	boolean updateIt = false;
	        	boolean saveHistory = false;

	        	String newPriority = request.getParameter("cb_" + rto.getId() + "_priority");
	        	String newIteration = request.getParameter("cb_" + rto.getId() + "_iteration");
	        	String parentReqId = request.getParameter("tx_" + rto.getId() + "_value");

	        	if (parentReqId!=null && !parentReqId.equals(rto.getParentRequirementId())) {
	        		if (parentReqId.equals("") && rto.getParentRequirementId()==null) {
	        			updateIt = false;
	        		} else {
		        		rto.setParentRequirementId(parentReqId);
		        		updateIt = true;
	        		}
	        	}
	        	
	        	if (newPriority!=null && !newPriority.trim().equals("") &&
	        			!newPriority.equals(rto.getStrPriority())) {
	        		rto.setPriority(new Integer(newPriority));
	        		updateIt = true;
	        	}

	        	if (newIteration!=null && !newIteration.trim().equals("") &&
	        			!newIteration.equals(rto.getIteration())) {
	        		if (newIteration.trim().equals("-1") && rto.getIteration()!=null) {
	        			rto.setIteration(null);
	        		} else {
	        			rto.setIteration(newIteration);	
	        		}
	        		updateIt = true; saveHistory = true;
	        	}
	        	
	        	if (updateIt) {
	        		rto.setIsAdjustment(true);
	        		rto.setReopening(false);
	        		rdel.updateRequirement(rto);
	        		
	        		saveProcessed = true;
	        		
	        		if (saveHistory) {
	        			rhdel.insert(rto, SessionUtil.getCurrentUser(request), null);	
	        		}	        		
	        	}
	        }
	        
			if (saveProcessed) {
				this.setSuccessFormSession(request, "message.showAllReqForm.success");	
			} else {
				this.setSuccessFormSession(request, "message.showAllReqForm.notsaved");
			}
			
			this.refreshList(request, frm);
			
		} catch(DifferentProjectFromParentReqException e0) {
			this.setErrorFormSession(request, "error.showAllReqForm.diffProjReq", e0);

		} catch(ParentReqSameReqException e1) {
			this.setErrorFormSession(request, "error.showAllReqForm.ReqSameParent", e1);

		} catch(ParentReqNotExistsException e2) {
			this.setErrorFormSession(request, "error.showAllReqForm.ParentNotExists", e2);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.showAllReqForm", e);
		}

		return mapping.findForward(forward);
	}

	
	public ActionForward showWorkFlow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		try {
			ShowAllRequirementForm frm = (ShowAllRequirementForm)form;	
		    UserTO currentUser = SessionUtil.getCurrentUser(request);
		    NodeTemplateTO root = null;
		    
			String instanceId = request.getParameter("instanceId");
			String taskId = request.getParameter("planningId");
		    TemplateTO tto = tdel.getTaskTemplateByInstance(instanceId);
		    if (tto!=null) {
		    	NodeTemplateTO filter = tto.getRootNode();
		    	filter.setInstanceId(new Integer(frm.getInstanceId()));
			    root = tdel.getNodeTemplateTree(filter, taskId);

				String bgcolor = request.getParameter("bgcolor");
				if (bgcolor==null || bgcolor.trim().equals("")) {
					bgcolor = "FFFFFF";
				}
				
		    	//draw an image representation of nodes
			    BufferedImage image = tdel.drawWorkFlow(root, currentUser, bgcolor, true);
			    
			    ShowAllTaskForm satf = (ShowAllTaskForm)request.getSession().getAttribute("showAllTaskForm");
			    if (satf==null) {
			    	satf = new ShowAllTaskForm(); 
			    }
			    satf.setHtmlMap(root.getHtmlMapCoords(true));
			    satf.setWorkFlowDiagram(image);
			    request.getSession().setAttribute("showAllTaskForm", satf);			    

		    }

		} catch(Exception e) {
		    e.printStackTrace();
		}	
		return mapping.findForward("showAllRequirement");
	}
	
	/**
	 * Get a list of customers of current project to be used into combo box.
	 */
	private Vector<CustomerTO> getListOfCustomersForCombo(ProjectTO pto, HttpServletRequest request) throws BusinessException{
		Vector<CustomerTO> custList = pto.getInsertCustomers();
		Vector<CustomerTO> temp = new Vector<CustomerTO>();
		CustomerTO fakeCustomer = new CustomerTO("-1");
		fakeCustomer.setName(this.getBundleMessage(request, "label.all"));
		temp.addElement(fakeCustomer);
		temp.addAll(custList);
		return temp;
	}
	

	/**
	 * Get a list of categories from data base and append a -1 to represents All Categories
	 */
	private Vector<CategoryTO> getListOfCategoriesForCombo(ProjectTO pto, HttpServletRequest request) throws BusinessException{
		HashMap<String, CategoryTO> uniqueList = new HashMap<String, CategoryTO>();
		
		CategoryDelegate catDel = new CategoryDelegate();
		Vector<CategoryTO> categList = catDel.getCategoryListByType(new Integer(1), pto, true);
		Iterator<CategoryTO> i = categList.iterator();
		while(i.hasNext()) {
			CategoryTO cto = i.next();
			if (uniqueList.get(cto.getName())==null) {
				cto.setId(cto.getName());
				uniqueList.put(cto.getName(), cto);
			}
		}
		
		Vector<CategoryTO> temp = new Vector<CategoryTO>();
		CategoryTO fakeCategory = new CategoryTO("-1");
		fakeCategory.setName(this.getBundleMessage(request, "label.all"));
		temp.addElement(fakeCategory);

		temp.addAll(uniqueList.values());
		
		return temp;
	}
	
	/**
	 * Get a list of viewing mode
	 */
	private Vector getListOfViewModeForCombo(HttpServletRequest request, ProjectTO pto) throws BusinessException{
		Vector temp = new Vector();
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		
		TransferObject listMode = new TransferObject("-1", this.getBundleMessage(request, "label.showAllReqForm.viewMode.1"));
		TransferObject hierarchyMode = new TransferObject("0", this.getBundleMessage(request, "label.showAllReqForm.viewMode.2"));
		temp.addElement(listMode);
		temp.addElement(hierarchyMode);
		
		Vector templates = tdel.getTemplateListByProject(pto.getId(), true);
		if (templates!=null) {
			HashMap hm = new HashMap();
			Iterator i = templates.iterator();
			while(i.hasNext()) {
				TemplateTO tto = (TemplateTO)i.next();
				
				if (hm.get(tto.getId())==null) {
					hm.put(tto.getId(), tto);
					tto.setGenericTag(this.getBundleMessage(request, "label.showAllReqForm.viewMode.template") + "[" +tto.getName() + "]");
					temp.addElement(tto);
				}
			}
		}
		
		return temp;
	}
	
	/**
	 * Get a list of requirement Status to be used into combo box.
	 */
	private Vector getListOfStatusForCombo(HttpServletRequest request) throws BusinessException{
		RequirementStatusDelegate rsdel = new RequirementStatusDelegate();
		Vector statList = rsdel.getRequirementStatusList();
		Vector temp = new Vector();
		
		RequirementStatusTO statusAllFake = new RequirementStatusTO();
		statusAllFake.setId("-2");
		statusAllFake.setName(super.getBundleMessage(request, "label.all"));
		
		RequirementStatusTO statusAll2Fake = new RequirementStatusTO();
		statusAll2Fake.setId("-1");
		statusAll2Fake.setName(super.getBundleMessage(request, "label.showAllReqForm.allExceptFinishing"));
		
		temp.addElement(statusAllFake);
		temp.addElement(statusAll2Fake);
		temp.addAll(statList);
	    
		return temp;
	}

	/**
	 * Get a list of priorities to be used into combo box.
	 */
	private Vector getListOfPrioritiesForCombo(HttpServletRequest request) throws BusinessException{
		CustReqAction cra = new CustReqAction();
		Vector temp = new Vector();
		
		Vector priorList = cra.getPriorityCombo(request);
	    
		TransferObject toAllFake = new TransferObject();
		toAllFake.setId("-2");
		toAllFake.setGenericTag(super.getBundleMessage(request, "label.all2"));

		TransferObject toAllFake2 = new TransferObject();
		toAllFake2.setId("-1");
		toAllFake2.setGenericTag(super.getBundleMessage(request, "label.showAllReqForm.allExceptLowest"));
				
		temp.addElement(toAllFake);
		temp.addElement(toAllFake2);
		temp.addAll(priorList);
	    
		return temp;
	}
}
