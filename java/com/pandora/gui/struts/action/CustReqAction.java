package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.CustomerTO;
import com.pandora.LeaderTO;
import com.pandora.MetaFieldTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.occurrence.Occurrence;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.DiscussionTopicDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.CustReqForm;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.taglib.decorator.RepositoryEntityCheckBoxDecorator;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;


/**
 * This class handle the actions performed into Customer Request form
 */
public class CustReqAction extends GeneralStrutsAction {

	/**
	 * Shows the Manage Customer Request form
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){

		String forward = "showCustRequest";
		
		try {

		    //clear current form
		    this.clearForm(form, request);
		    this.clearMessages(request);
		    
		    request.getSession().setAttribute("attachmentList", new Vector());
		    
			//get all requests from data base and put into http session (to be displayed by grid)
			this.refreshList(request, form, null);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showReportForm", e);
		}

		return mapping.findForward(forward);
	}

	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showCustRequest");
	}	
	
	
	public ActionForward refreshAfterAttach(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCustRequest";
		RequirementDelegate rdel = new RequirementDelegate();		
		try {
		    CustReqForm reqfrm = (CustReqForm)form;
		    
		    if (reqfrm.getReqNum()!=null) {
				
		    	//set current operation status for Updating	
				reqfrm.setSaveMethod(GeneralStrutsForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
				
				//create a user object used such a filter
				RequirementTO filter = new RequirementTO();
				filter.setId(reqfrm.getReqNum());
				
				//get a specific Requirement TO from data base
				RequirementTO rto = rdel.getRequirement(filter);
				
				//put the data (from DB) into html fields
				this.getActionFormFromTransferObject(rto, reqfrm, request);

				//get all requests from data base and put into http session (to be displayed by grid)
				this.refreshList(request, form, rto);

				reqfrm.setIsPreApproveRequest("off");
				reqfrm.setEstimTime("");		    	
				
		    } else {
		    	request.getSession().setAttribute("attachmentList", new Vector());
		    }
								
		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.showReportForm", e);
		}

		return mapping.findForward(forward);
	}

	
	/**
	 * Refresh requirement form 
	 */
	public ActionForward refreshRequest(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showCustRequest";
		
		try {
		    //clear current messages
		    this.clearMessages(request);
		    
			//get all requests from data base and put into http session (to be displayed by grid)
			this.refreshList(request, form, null);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showReqForm", e);
		}
	    return mapping.findForward(forward);
	}

	
	/**
	 * Event called after change the project combo  
	 */
	public ActionForward refreshProject(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showCustRequest";

		try {
		    this.refreshDinamicLists(request, form);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showReqForm", e);
		}
	    return mapping.findForward(forward);
	}
	
	
	/**
	 * Clear all values of current form.
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showCustRequest";

		try {
		    this.clearForm(form, request);
		    this.clearMessages(request);
		
            //get all requests from data base and put into http session (to be displayed by grid)
            this.refreshList(request, form, null);
            
        } catch (BusinessException e) {
            this.setErrorFormSession(request, "error.showReqForm", e);
        }
		
		return mapping.findForward(forward);		
	}	
	
	
	/**
	 * Clear all values of current form.
	 */
	private void clearForm(ActionForm form, HttpServletRequest request){
	    CustReqForm crfrm = (CustReqForm)form;
	    crfrm.clear();
		
		//set current requester
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    crfrm.setRequester(uto.getUsername());
	    crfrm.setRequesterId(uto.getId());

		//set current operation status for Insertion
	    crfrm.setSaveMethod(CustReqForm.INSERT_METHOD, uto);

		request.getSession().removeAttribute("metaFieldList");
		request.getSession().removeAttribute("discussionTopicList");
		request.getSession().removeAttribute("attachmentList");

	    String defProj = uto.getPreference().getPreference(PreferenceTO.CUSTHOME_DEF_PROJ);
	    if (!defProj.equals("0")){
	        crfrm.setProjectRelated(defProj);  
	    }
	}

	
	/**
	 * This method prepare the form for updating a requirement. This method get the 
	 * information of specific requirement from data base and put the data into the
	 * html fields.
	 */
	public ActionForward editRequest(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCustRequest";
		
	    try {
		    CustReqForm reqfrm = (CustReqForm)form;
		    reqfrm.setComment("");
			RequirementDelegate rdel = new RequirementDelegate();
			
			//clear messages of form
			this.clearMessages(request);
			
			//set current operation status for Updating	
			reqfrm.setSaveMethod(GeneralStrutsForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
			
			//create a user object used such a filter
			RequirementTO filter = new RequirementTO();
			filter.setId(reqfrm.getId());
			
			//get a specific Requirement TO from data base
			RequirementTO rto = rdel.getRequirement(filter);
			
			//put the data (from DB) into html fields
			this.getActionFormFromTransferObject(rto, reqfrm, request);

			//get all requests from data base and put into http session (to be displayed by grid)
			this.refreshList(request, form, rto);
		    this.refreshArtifacts(mapping, form, request, response);
		    
			reqfrm.setIsPreApproveRequest("off");
			reqfrm.setEstimTime("");

		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.prepareUpdateReqForm", e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, "error.prepareUpdateReqForm", e);		    
	    }

		return mapping.findForward(forward);		
	}

	
	/**
	 * This method is performed after Save button click event.<br>
	 * Is used to update or insert data of requirement into data base.
	 */
	public ActionForward saveRequirement(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    UserDelegate udel = new UserDelegate();
	    
		String forward = "showCustRequest";
		String errorMsg = "error.showReqForm";
		String succeMsg = "message.success";
		
		try {
			CustReqForm reqfrm = (CustReqForm)form;
			RequirementDelegate rdel = new RequirementDelegate();
			UserTO uto = SessionUtil.getCurrentUser(request);
			
			//save last project selected
			PreferenceTO prto = new PreferenceTO(PreferenceTO.CUSTHOME_DEF_PROJ, reqfrm.getProjectRelated(), uto); 
			uto.getPreference().addPreferences(prto);
			
			//create an RequirementTO object based on html fields
			RequirementTO rto = this.getTransferObjectFromActionForm(reqfrm, request);
									
			//set the bundle into requester user
			CustomerTO requester = rto.getRequester();
			requester.setBundle(uto.getBundle());
			requester.setProject(rto.getProject());
			requester = udel.getCustomer(requester);
			rto.setRequester(requester);

			if (reqfrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){			    
			    errorMsg = "error.insertReqForm";
			    succeMsg = "message.insertReq";
			    rto.setCreationDate(DateUtil.getNow());
			    rdel.insertRequirement(rto);
			    this.clearForm(form, request);
			} else {
			    errorMsg = "error.updateReqForm";
			    succeMsg = "message.updateReq";
			    rdel.updateRequirement(rto);
			}
			
			//set success message into http session
			this.setSuccessFormSession(request, succeMsg);
			
			//get all Requirements from data base and put into http session (to be displayed by grid)
			this.refreshList(request, form, rto);
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}
		return mapping.findForward(forward);		
	}
	

	public ActionForward refreshAfterTopicDiscussion(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCustRequest";
		
	    try {
		    CustReqForm reqfrm = (CustReqForm)form;
		    this.getDiscussionData(request, reqfrm);
		    
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showReqForm", e);
		}
		return mapping.findForward(forward);		
	}


	public ActionForward refreshAfterMetaFieldUpdate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){   
		String forward = "showCustRequest";
		return mapping.findForward(forward);		
	}	
	
	/**
	 * Remove a selected Requirement from data base.
	 */
	public ActionForward giveUpRequest(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){   
		String forward = "showCustRequest";
		try {
			CustReqForm reqfrm = (CustReqForm)form;
			RequirementDelegate rdel = new RequirementDelegate();
			
			String fwd = request.getParameter("nextFwd");
			if (fwd!=null) {
				forward = fwd;
			}
			
			//clear form and messages
			this.clearMessages(request);
			
			//create an RequirementTO object based on html fields
			RequirementTO rto = new RequirementTO();
			rto.setId(reqfrm.getId());
			rto = rdel.getRequirement(rto);
			
			//remove Requirement from data base
			rdel.removeRequirement(rto);
			
			//get all Requirements from data base and put into http session (to be displayed by grid)
			this.clearForm(reqfrm, request);			
			this.refreshList(request, reqfrm, rto);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeReq");
					
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.removeReqForm", e);
		}
		return mapping.findForward(forward);		
	}	

	
	public ActionForward refreshArtifacts(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCustRequest";
	    RepositoryDelegate rdel = new RepositoryDelegate();
		try {
			CustReqForm reqfrm = (CustReqForm)form;		
		    if (reqfrm.getReqNum()!=null && !reqfrm.getReqNum().equals("")) {
		    	RequirementTO rto = new RequirementTO(reqfrm.getReqNum());
				Vector<RepositoryFilePlanningTO> repositoryList = rdel.getFilesFromPlanning(rto);
				request.getSession().setAttribute("repositoryList", repositoryList);
				request.getSession().setAttribute(RepositoryEntityCheckBoxDecorator.REPOSITORY_ENTITY_ID, rto.getId());
		    }			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showReqForm", e);
		}
		return mapping.findForward(forward);
	}
	
	
	/**
	 * Refresh all list of form.
	 */
	private void refreshList(HttpServletRequest request, ActionForm form, RequirementTO rto) throws BusinessException{
		RequirementDelegate rdel = new RequirementDelegate();
		ProjectDelegate pdel = new ProjectDelegate();
		UserDelegate udel = new UserDelegate(); 
		CustReqForm crfrm = (CustReqForm)form;

		//get filter from list
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		Vector<RequirementTO> reqList = new Vector<RequirementTO>();
		if (crfrm.getIsAdjustment().equals("off")){
		    reqList = rdel.getListByUser(uto, crfrm.getBoolCloseRequest(), true);
		} else if (crfrm.getIsAdjustmentWithoutProj().equals("off")) {
		    UserTO currLeader = SessionUtil.getCurrentUser(request);
		    reqList = rdel.getPendingListByUser(currLeader, true);
		} else {
		    ProjectTO pto = new ProjectTO(crfrm.getProjectRelated());
		    pto = pdel.getProjectObject(pto, false);
		    reqList = rdel.getListByProject(pto, "-1", "-1", "-2", "-1", "-1");
		}
		request.getSession().setAttribute("requirementList", reqList);
		
		//get all Projects from data base and put into http session (to be displayed by combo)
		ProjectDelegate prjdel = new ProjectDelegate();
		Vector<ProjectTO> prjList = prjdel.getProjectListByUser(uto);
		if (prjList!=null) {
			Iterator<ProjectTO> pi = prjList.iterator();
			Vector<ProjectTO> newProjList = new Vector<ProjectTO>();
			while(pi.hasNext()){
			    ProjectTO buff = pi.next();
			    if (buff.getBollCanAlloc()) {
			    	newProjList.add(buff); 
			    }
			}
			request.getSession().setAttribute("projectList", newProjList);			    
		} else {
			request.getSession().setAttribute("projectList", new Vector<ProjectTO>());			
		}
		
		//define the current project selected
		if (rto!=null) {
		    crfrm.setProjectRelated(rto.getProject().getId());
		} else {
			if (prjList!=null && !prjList.isEmpty()){
				Iterator<ProjectTO> pi = prjList.iterator();
				while(pi.hasNext()){
					ProjectTO buff = pi.next();
					if (buff.getCanAlloc()==null || buff.getCanAlloc().equals("1")) {
					    crfrm.setProjectRelated(buff.getId()); 	
				    	break;					    
					}
				}
			}		    
		}
	
		//assembly a list of priorities to be used by html combo		
		Vector priorList = this.getPriorityCombo(request);
		request.getSession().setAttribute("priorityList", priorList);
				
		//get all customer from DB based on user id (in order to get the preApproveReq status for each project)
		Vector custList = udel.getCustomerByUser(uto);
		crfrm.setPreApproveList(this.getCustomerHidden(custList, prjList, true));

		//refresh the meta fields related with current project
		this.refreshDinamicLists(request, form);
	}
	
	
	private void refreshDinamicLists(HttpServletRequest request, ActionForm form) throws BusinessException{
	    UserDelegate udel = new UserDelegate();
	    MetaFieldDelegate mfdel = new MetaFieldDelegate();	    
	    CustReqForm crfrm = (CustReqForm)form;
	    
		Vector list = mfdel.getListByProjectAndContainer(crfrm.getProjectRelated(), crfrm.getCategoryId(), MetaFieldTO.APPLY_TO_REQUIREMENT);
		request.getSession().setAttribute("metaFieldList", list);
		
		//reload the discussion topics related to the requirement
		this.getDiscussionData(request, crfrm);
		
	    //get all categories from data base and put into http session (to be displayed by combo)
		ProjectTO pto = new ProjectTO(crfrm.getProjectRelated());
	    CategoryDelegate cdel = new CategoryDelegate();
	    Vector catlist = cdel.getCategoryListByType(CategoryTO.TYPE_REQUIREMENT, pto, false);
	    request.getSession().setAttribute("categoryList", catlist);	    
		
	    //check if current user could open a requirement of current project in the name of someone else
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    CustomerTO cto = new CustomerTO(uto);
	    cto.setProject(pto);
	    cto = udel.getCustomer(cto);
	    if (cto!=null) {
	    	crfrm.setCanChangeRequester(cto.getBoolCanOpenOtherOwnerReq()?"on":"off");	
	    }
	    
	    //create a fake resource to be the first option into combo
		ResourceTO dummy = new ResourceTO("-1");
		dummy.setName(this.getBundleMessage(request, "label.request.chooseRes"));
		Vector projResList = new Vector();
		projResList.add(dummy);

		//get all resources of selected project		
		Vector resourceList = udel.getResourceByProject(pto.getId(), false, true);
		projResList.addAll(resourceList);
		request.getSession().setAttribute("projResList", projResList);

		//get all customer of selected project		
		Vector customerList = udel.getCustomerByProject(pto, false);
		request.getSession().setAttribute("projCustomerList", customerList);

		if (crfrm.getSaveMethod().equals(CustReqForm.INSERT_METHOD)) {
		    crfrm.setIsPreApproveRequest("on");
		} else {
		    crfrm.setIsPreApproveRequest("off");
		}
	}

	
	private void getDiscussionData(HttpServletRequest request, CustReqForm crfrm) throws BusinessException{
	    DiscussionTopicDelegate dtdel = new DiscussionTopicDelegate();		
		if (crfrm.getId()!=null) {
			Vector listDt = dtdel.getListByPlanning(crfrm.getId());
			request.getSession().setAttribute("discussionTopicList", listDt);			
		}		
	}

	/**
	 * Define the value of reqAccept or PreApprove html hidden based on 
	 * current attribute status of customer for each project. 
	 */
	private String getCustomerHidden(Vector custList, Vector prjList, boolean isPreApproveAttribute){
	    String response = "";
	    Iterator i = prjList.iterator();
	    while(i.hasNext()){
	        ProjectTO pto = (ProjectTO)i.next();
	        
	        boolean value = false;
	        Iterator j = custList.iterator();
	        while(j.hasNext()){
	            CustomerTO cto = (CustomerTO)j.next();
	            if (cto.getProject().getId().equals(pto.getId())){
	                if (isPreApproveAttribute) {
	                    value = cto.getBoolPreApproveReq();
	                }
	                break;
	            }
	        }

            if (value){
                response = response + "1";
            } else {
                response = response + "0";
            }
	    }
	    return response;
	}

	
	/**
	 * Create a list of priority objects
	 */
	public Vector<TransferObject> getPriorityCombo(HttpServletRequest request){
	    Vector<TransferObject> response = new Vector<TransferObject>();
	    for(int i=0; i<=5; i++){
	        TransferObject to = new TransferObject();
	        to.setId(i+"");
	        to.setGenericTag(this.getResources(request).getMessage(request.getLocale(), "label.requestPriority." + i));
	        response.addElement(to);
	    }
	    return response;
	}

	
	/**
	 * Put data of html fields into TransferObject 
	 * @param frm
	 * @return
	 */
	private RequirementTO getTransferObjectFromActionForm(CustReqForm frm, HttpServletRequest request){
	    RequirementTO rto = new RequirementTO();
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    Locale loc = SessionUtil.getCurrentLocale(request);
	    
		rto.setId(frm.getId());
		rto.setDescription(frm.getDescription());
		rto.setProject(new ProjectTO(frm.getProjectRelated()));
		rto.setCategory(new CategoryTO(frm.getCategoryId()));
		rto.setRequester(new CustomerTO(frm.getRequesterId()));
		rto.setSuggestedDate(DateUtil.getDateTime(frm.getSuggestedDate(), super.getCalendarMask(request), uto.getLocale()));
		if (frm.getDeadlineDate()!=null) {
		    rto.setDeadlineDate(DateUtil.getDateTime(frm.getDeadlineDate(), super.getCalendarMask(request), uto.getLocale()));    
		} else {
		    rto.setDeadlineDate(null);
		}
		rto.setPriority(new Integer(frm.getPriority()));
		rto.setIsAdjustment(!frm.getIsAdjustment().equals("off"));
		rto.setIteration(frm.getIteration());
		
		if (frm.getIsPreApproveRequest().equals("on")){
		    rto.setEstimatedTime(new Integer((int)(StringUtil.getStringToFloat(frm.getEstimTime(), loc) * 60)));
		    rto.setPreApprovedReqResource(frm.getSelectedResource());
		} else {
		    rto.setEstimatedTime(null);
		    rto.setPreApprovedReqResource(null);
		}
		rto.setCreationDate(frm.getCreationDate());		
		
		Vector metaFieldList = (Vector)request.getSession().getAttribute("metaFieldList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, rto);
		if (metaFieldList!=null) {
		    frm.setAdditionalFields(rto.getAdditionalFields());
		}
		
		Vector discussionTopicList = (Vector)request.getSession().getAttribute("discussionTopicList");
		rto.setDiscussionTopics(discussionTopicList);
		
		//comment for reopening...
		if (frm.getIsReopenRequirement()!=null && frm.getIsReopenRequirement().equals("on")) {
		    rto.setReopening(true);
		    rto.setAdditionalComment(frm.getComment());
		    
		    try {
			    //retrieve from data base the number of reopening...
			    RequirementDelegate rdel = new RequirementDelegate();
			    RequirementTO filter = new RequirementTO(rto.getId());
			    filter = rdel.getRequirement(filter);
			    rto.setReopeningOccurrences(filter.getReopeningOccurrences());		        
		    } catch (Exception e){
		        rto.setReopeningOccurrences(new Integer(0));
		    }
		    
		} else {
		    rto.setReopening(false);
		}
		
		//the method bellow CANNOT be set here!!! because user cannot change current status
		//rto.setRequirementStatus()
		
	    return rto;
	}

	
	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 * @param uto
	 * @return
	 */
	private void getActionFormFromTransferObject(RequirementTO rto, CustReqForm frm, HttpServletRequest request) throws BusinessException{
		OccurrenceDelegate ocdel = new OccurrenceDelegate();
		UserDelegate udel = new UserDelegate();
		
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    frm.setId(rto.getId());
	    frm.setReqNum(rto.getId());
	    frm.setDescription(rto.getDescription());
	    frm.setProjectRelated(rto.getProject().getId());
	    frm.setCategoryId(rto.getCategory().getId());
	    frm.setRequester(rto.getRequester().getUsername());
	    frm.setRequesterId(rto.getRequester().getId());
	    
	    frm.setPriority(rto.getPriority()+"");
	    frm.setPreviousPriority(frm.getPriority());
	    
	    CustomerTO cto = new CustomerTO(uto.getId());
	    cto.setProject(rto.getProject());
	    cto = udel.getCustomer(cto);
	    if (cto.getBoolCanSeeDiscussion()) {
	    	frm.setCanSeeDiscussion("on");	
	    } else {
	    	frm.setCanSeeDiscussion("off");
	    }

	    if (uto instanceof ResourceTO) {
	    	frm.setCanSeeArtifacts("on");
	    } else {
	    	frm.setCanSeeArtifacts("off");
	    }
	    
	    if (rto.getSuggestedDate()!=null){
	        frm.setSuggestedDate(DateUtil.getDate(rto.getSuggestedDate(), uto.getCalendarMask(), uto.getLocale()));    
	    } else {
	        frm.setSuggestedDate("");   
	    }
	    if (rto.getDeadlineDate()!=null){
	        frm.setDeadlineDate(DateUtil.getDate(rto.getDeadlineDate(), uto.getCalendarMask(), uto.getLocale()));    
	    } else {
	        frm.setDeadlineDate("");   
	    }
	    frm.setCreationDate(rto.getCreationDate());
	    	    
	    //set the additional Fields to current form
	    frm.setAdditionalFields(rto.getAdditionalFields());
	    
	    //check if current user is the leader of requirement project
	    frm.setIsRequesterLeader("off");
	    Vector allLeaders = udel.getLeaderByProject(rto.getProject());
	    if (allLeaders!=null) {
	        Iterator i = allLeaders.iterator();
	        while(i.hasNext()) {
	            LeaderTO lto = (LeaderTO)i.next();
	            if (lto.getId().equals(rto.getRequester().getId())) {
	                frm.setIsRequesterLeader("on");
	                break;
	            }
	        }
	    }
	    
	    
	    if (rto.getIteration()!=null && !rto.getIteration().trim().equals("") 
	    		&& !rto.getIteration().trim().equals("-1")) {
	    	//get the occurrence iteration from data base
	    	OccurrenceTO oto = new OccurrenceTO(rto.getIteration());
	    	oto = ocdel.getOccurrenceObject(oto);
	    	if (oto!=null) {
		    	if (!oto.getStatus().equals(Occurrence.STATE_START) &&
			    		!oto.getStatus().equals(Occurrence.STATE_FINAL_1) &&
			    		!oto.getStatus().equals(Occurrence.STATE_FINAL_2)) {
			    	frm.setIsPriorityChangeAllowed("off");
			    } else {
			    	frm.setIsPriorityChangeAllowed("on");
			    }	    		
	    	}
	    	frm.setIteration(rto.getIteration());
	    }
	    
		request.getSession().setAttribute("attachmentList", rto.getAttachments());
	}

}
