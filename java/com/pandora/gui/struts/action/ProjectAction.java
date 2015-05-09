package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CustomerFunctionTO;
import com.pandora.CustomerTO;
import com.pandora.FunctionTO;
import com.pandora.LeaderTO;
import com.pandora.MetaFieldTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectStatusTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryPolicyTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.repository.Repository;
import com.pandora.bus.repository.RepositoryBUS;
import com.pandora.delegate.CustomerFunctionDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ProjectStatusDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.IncompatibleUsrsBetweenPrjException;
import com.pandora.exception.InvalidProjectUserReplaceException;
import com.pandora.exception.ProjectCannotBeClosedException;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.ProjectForm;
import com.pandora.gui.struts.form.UserForm;
import com.pandora.gui.taglib.decorator.ProjectGridCheckBoxDecorator;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * This class handle the actions performed into Manage Project form
 */
public class ProjectAction extends GeneralStrutsAction {

	/**
	 * Get a list of user based on filter from html. The data returned by
	 * database must be displayed into combo box on form.
	 */
	public ActionForward searchUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String forward = "showProject";
		UserDelegate udel = new UserDelegate();

		try {
			this.clearMessages(request);
			ProjectForm frm = (ProjectForm) form;

			Vector kwList = StringUtil.getKeyworks(frm.getUserSearch());
			Vector userList = udel.getListByKeyword(kwList);
			if (userList == null) {
				this.setErrorFormSession(request, "error.project.userSearch",
						null);
				userList = new Vector();
			}
			request.getSession().setAttribute("UserList", userList);

		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.showProjForm", e);
		}
		return mapping.findForward(forward);
	}

	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		return mapping.findForward("showProject");
	}

	
	public ActionForward changeRepository(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ProjectForm frm = (ProjectForm) form;
		Repository rep = RepositoryBUS.getRepositoryClass(frm.getRepositoryClass());
		if (rep != null) {
			frm.setShowRepositoryUserPwd(rep.showUserPwdFields() ? "on" : "off");
		}
		return mapping.findForward("showProject");
	}

	
	public ActionForward changeRole(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		CustomerFunctionDelegate del = new CustomerFunctionDelegate();
		ProjectDelegate pdel = new ProjectDelegate();

		try {
			ProjectForm frm = (ProjectForm) form;

			String customerId = request.getParameter("customer");
			String projectId = request.getParameter("proj");
			String functionId = request.getParameter("func");

			CustomerFunctionTO cfto = new CustomerFunctionTO();
			cfto.setCreationDate(DateUtil.getNow());
			CustomerTO cto = new CustomerTO(customerId);
			cto.setProject(new ProjectTO(projectId));
			cfto.setCustomer(cto);
			cfto.setFunct(new FunctionTO(functionId));

			CustomerFunctionTO cftoDB = del.getCustomerFunctionObject(cfto);
			if (cftoDB != null) {
				del.removeCustomerFunction(cfto);
			} else {
				del.insertCustomerFunction(cfto);
			}

			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getId()), false);
			request.getSession().setAttribute("allocList", pto.getAllocUsers());

			this.setSuccessFormSession(request, "message.rolechanged");

		} catch (Exception e) {
			this.setErrorFormSession(request, "error.showProjForm", e);
		}

		return mapping.findForward("showProject");
	}

	
	/**
	 * Add a user into allocation user list.
	 */
	public ActionForward addUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String forward = "showProject";
		UserDelegate udel = new UserDelegate();

		try {
			// clear current form
			this.clearMessages(request);
			ProjectForm frm = (ProjectForm) form;

			UserTO uto = new UserTO(frm.getSelectedUserId());
			uto = udel.getUser(uto);
			CustomerTO cto = new CustomerTO(uto);
			cto.setProject(new ProjectTO(frm.getId()));
			Vector allocList = (Vector) request.getSession().getAttribute("allocList");

			if (allocList == null) {
				allocList = new Vector();
			} else {
				request.getSession().removeAttribute("allocList");
			}
			Vector temp = new Vector();
			temp.addElement(cto);
			temp.addAll(allocList);
			request.getSession().setAttribute("allocList", temp);

		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.showProjForm", e);
		}
		return mapping.findForward(forward);
	}
	

	/**
	 * Remove a user object from session list of allocation users.
	 */
	public ActionForward removeUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String forward = "showProject";

		try {
			// clear current form
			this.clearMessages(request);
			ProjectForm frm = (ProjectForm) form;

			Vector allocList = (Vector) request.getSession().getAttribute(
					"allocList");
			if (allocList != null) {
				Iterator i = allocList.iterator();
				int c = 0;
				while (i.hasNext()) {
					UserTO uto = (UserTO) i.next();
					if (uto.getId().equals(frm.getRemovedUserId())) {
						request.getSession().removeAttribute("allocList");
						allocList.remove(c);
						Vector temp = new Vector();
						temp.addAll(allocList);
						request.getSession().setAttribute("allocList", temp);
						break;
					}
					c++;
				}
			}

		} catch (NullPointerException e) {
			this.setErrorFormSession(request, "error.showProjForm", e);
		}
		return mapping.findForward(forward);
	}

	
	/**
	 * Shows the Manage User form
	 */
	public ActionForward prepareProject(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String forward = "showProject";

		try {
			// clear current form
			this.clearForm(form, request);

			// setting current locale for struts form
			Locale loc = SessionUtil.getCurrentLocale(request);
			ProjectForm frm = (ProjectForm) form;
			frm.setUserLocale(loc);

			// get all open Projects and Project Status List
			this.refreshAuxLists(form, request);

			// get all projects from data base and put into http session (to be
			// displayed by grid)
			this.refreshList(request);

		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.showProjForm", e);
		}

		return mapping.findForward(forward);
	}

	
	/**
	 * This method prepare the form for updating a project. This method get the
	 * information of specific project from data base and put the data into the
	 * html fields.
	 */
	public ActionForward editProject(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String forward = "showProject";
		try {
			// clear current message of form
			this.clearMessages(request);
			ProjectForm frm = (ProjectForm) form;

			// set current operation status for Updating
			frm.setSaveMethod(UserForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));

			// get data of projet from data base
			ProjectDelegate pdel = new ProjectDelegate();
			ProjectTO pto = this.getTransferObjectFromActionForm(frm, request);
			pto = pdel.getProjectObject(pto, false);
			frm.setCreationDate(pto.getCreationDate());

			// get all open Projects and Project Status List
			this.refreshAuxLists(form, request);

			// set data of project into html fields
			this.getActionFormFromTransferObject(pto, frm, request);

			// set data of allocated users and occurrences
			if (pto.getAllocUsers() != null) {
				request.getSession().setAttribute("allocList", pto.getAllocUsers());
			}
			if (pto.getOccurrenceList() != null) {
				request.getSession().setAttribute("projOccList", pto.getOccurrenceList());
			}

		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.showProjForm", e);
		}

		return mapping.findForward(forward);
	}
	

	/**
	 * This method is performed after Save button click event.<br>
	 * Is used to update or insert data of project into data base.
	 */
	public ActionForward saveProject(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		String forward = "showProject";
		String errorMsg = "error.showProjForm";
		String succeMsg = "message.success";

		try {
			ProjectForm frm = (ProjectForm) form;
			ProjectDelegate pdel = new ProjectDelegate();
			this.clearMessages(request);

			// create a Project object based on html fields
			ProjectTO pto = this.getTransferObjectFromActionForm(frm, request);

			if (frm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)) {
				errorMsg = "error.insertProjForm";
				succeMsg = "message.insertProj";
				pdel.insertProject(pto);
				this.clearForm(form, request);
			} else {
				errorMsg = "error.updateProjForm";
				succeMsg = "message.updateProj";
				pdel.updateProject(pto);

				// refresh data into form...
				editProject(mapping, form, request, response);
			}

			// set success message into http session
			this.setSuccessFormSession(request, succeMsg);

			// get all projects from data base and put into http session (to be
			// displayed by grid)
			this.refreshList(request);

			
		} catch (ProjectCannotBeClosedException e) {
			this.setErrorFormSession(request, "error.project.projectCannotBeClosed", e);
		} catch (InvalidProjectUserReplaceException e) {
			this.setErrorFormSession(request, "error.project.InvalidProjectUserReplace", e);
		} catch (IncompatibleUsrsBetweenPrjException e) {
			this.setErrorFormSession(request, "error.project.imcompatibleCustomers", e);
		} catch (BusinessException e) {
			this.setErrorFormSession(request, errorMsg, e);
		} catch (NullPointerException e) {
			this.setErrorFormSession(request, errorMsg, e);
		}
		return mapping.findForward(forward);
	}

	/**
	 * Refresh requirement form
	 */
	public ActionForward refreshList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String forward = "showProject";

		try {
			// clear current messages
			this.clearMessages(request);

			// get all projects from data base and put into http session (to be
			// displayed by grid)
			this.refreshList(request);

		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.showProjForm", e);
		}
		return mapping.findForward(forward);
	}

	/**
	 * Clear all values of current form.
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String forward = "showProject";
		try {
			this.clearForm(form, request);
			this.clearMessages(request);

			// get all open Projects and Project Status List
			this.refreshAuxLists(form, request);

		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.showProjForm", e);
		}

		return mapping.findForward(forward);
	}

	/**
	 * Clear all values of current form.
	 */
	private void clearForm(ActionForm form, HttpServletRequest request) {
		ProjectForm prjfrm = (ProjectForm) form;
		prjfrm.clear();
		prjfrm.setCreationDate(DateUtil.getNow());

		// set current operation status for Insertion
		prjfrm.setSaveMethod(ProjectForm.INSERT_METHOD, SessionUtil
				.getCurrentUser(request));

		// set empty to user list
		request.getSession().setAttribute("UserList", new Vector());
		request.getSession().setAttribute("allocList", new Vector());
	}

	/**
	 * Get information about all open Projects and Project Status List from data
	 * base.
	 */
	private void refreshAuxLists(ActionForm form, HttpServletRequest request)
			throws BusinessException {
		MetaFieldDelegate mfdel = new MetaFieldDelegate();
		UserDelegate udel = new UserDelegate();
		ProjectDelegate prjdel = new ProjectDelegate();

		ProjectForm frm = (ProjectForm) form;
		ProjectTO pto = this.getTransferObjectFromActionForm(frm, request);
		if (pto.getProjectStatus() == null
				|| pto.getProjectStatus().getId() == null) {
			pto = prjdel.getProjectObject(pto, true);
		}

		if (pto != null) {
			Repository rep = RepositoryBUS.getRepositoryClass(pto.getRepositoryClass());
			if (rep != null) {
				frm.setShowRepositoryUserPwd(rep.showUserPwdFields() ? "on" : "off");
			} else {
				frm.setShowRepositoryUserPwd("off");
			}
		}

		// get current user
		UserTO uto = SessionUtil.getCurrentUser(request);
		LeaderTO lto = new LeaderTO(uto);

		// get all open Projects from data base and put into http session (to be
		// displayed by combo)
		Vector prjList = null;
		if (lto.getUsername().equals(RootTO.ROOT_USER)) {
			prjList = prjdel.getProjectRoot();
		} else {
			prjList = prjdel.getProjectListByUser(lto);
		}
		
		// create a root project object to be add into combo
		Vector temp = new Vector();
		if (!lto.getUsername().equals(RootTO.ROOT_USER)) {
			ProjectTO rootParent = new ProjectTO(ProjectTO.PROJECT_ROOT_ID);
			rootParent = prjdel.getProjectObject(rootParent, true);
			temp.addElement(rootParent);
		}
		temp.addAll(prjList);
		request.getSession().setAttribute("parentPrjList", temp);

		// get Project Status List
		ProjectStatusDelegate psdel = new ProjectStatusDelegate();
		if (pto != null) {
			Vector prjStsList = psdel.getProjectStatusListByProject(pto);
			request.getSession().setAttribute("parentPrjStatusList", prjStsList);
		} else {
			if (request.getSession().getAttribute("parentPrjStatusList")==null) {
				Vector prjStsList = new Vector();
				prjStsList.add(psdel.getProjectStatus(ProjectStatusTO.STATE_MACHINE_OPEN));
				request.getSession().setAttribute("parentPrjStatusList", prjStsList);	
			}			
		}

		// clear search user combo box
		request.getSession().setAttribute("UserList", new Vector());

		// clear occurrence list
		request.getSession().setAttribute("projOccList", new Vector());

		if (!frm.getId().equals("")) {
			Vector list = mfdel.getListByProjectAndContainer(frm.getId(), null,
					MetaFieldTO.APPLY_TO_PROJECT);
			request.getSession().setAttribute("metaFieldList", list);
		} else {
			request.getSession().setAttribute("metaFieldList", new Vector());
		}

		Vector canAllocList = new Vector();
		TransferObject obj1 = new TransferObject("1", this.getBundleMessage(
				request, "label.yes"));
		canAllocList.add(obj1);
		TransferObject obj2 = new TransferObject("0", this.getBundleMessage(
				request, "label.no"));
		canAllocList.add(obj2);
		request.getSession().setAttribute("canAllocList", canAllocList);

		Vector repositoryList = new Vector();
		TransferObject rep1 = new TransferObject("-1", this.getBundleMessage(
				request, "label.none"));
		repositoryList.add(rep1);

		// get repository list by repository class names
		UserTO root = udel.getRoot();
		String repClasses = root.getPreference().getPreference(
				PreferenceTO.REPOSITORY_BUS_CLASS);
		if (repClasses != null) {
			String[] classList = repClasses.split(";");
			if (classList != null && classList.length > 0) {
				for (int i = 0; i < classList.length; i++) {
					String classStr = classList[i].trim();
					Repository repbus = RepositoryBUS
							.getRepositoryClass(classStr);
					String repName = "[" + classStr + "] - Load Error"; // default
																		// value
					String repId = "-1";
					if (repbus != null) {
						repName = repbus.getUniqueName();
						repId = classStr;
					}
					TransferObject rep2 = new TransferObject(repId, this
							.getBundleMessage(request, repName, true));
					repositoryList.add(rep2);
				}
			}
		}
		request.getSession().setAttribute("repositoryList", repositoryList);
	}

	/**
	 * Refresh grid with list of all projects from data base.
	 */
	private void refreshList(HttpServletRequest request)
			throws BusinessException {
		ProjectDelegate pdel = new ProjectDelegate();

		// get current user
		UserTO uto = SessionUtil.getCurrentUser(request);
		LeaderTO lto = new LeaderTO(uto);

		Vector projList = new Vector();
		if (uto.getUsername().toLowerCase().toLowerCase().equals(
				RootTO.ROOT_USER)) {
			projList = pdel.getProjectRoot();
		} else {
			projList = pdel.getProjectListByUser(lto);
		}
		request.getSession().setAttribute("projectList", projList);
	}

	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 */
	private void getActionFormFromTransferObject(ProjectTO to, ProjectForm frm,
			HttpServletRequest request) {
		Locale loc = SessionUtil.getCurrentLocale(request);

		frm.setDescription(to.getDescription());
		frm.setId(to.getId());
		if (to.getParentProject() != null) {
			frm.setParentProject(to.getParentProject().getId());
		} else {
			frm.setParentProject("");
		}
		frm.setProjectStatus(to.getProjectStatus().getId());
		frm.setName(to.getName());
		frm.setCanAlloc(to.getCanAlloc());
		if (to.getAllowBillable() != null) {
			frm.setAllowBillable(to.getAllowBillable().booleanValue());
		}

		frm.setRepositoryClass(to.getRepositoryClass());
		frm.setRepositoryPass(to.getRepositoryPass());
		frm.setRepositoryURL(to.getRepositoryURL());
		frm.setRepositoryUser(to.getRepositoryUser());

		if (to.getEstimatedClosureDate() != null) {
			frm.setEstimatedClosureDate(DateUtil.getDate(to
					.getEstimatedClosureDate(), super.getCalendarMask(request),
					loc));
		} else {
			frm.setEstimatedClosureDate(null);
		}

		// setting current locale for struts form
		frm.setUserLocale(loc);

		// set the additional Fields to current form
		frm.setAdditionalFields(to.getAdditionalFields());
		frm.setRepositoryPolicies(to.getRepositoryPolicies());
	}

	/**
	 * Put data of html fields into TransferObject
	 */
	private ProjectTO getTransferObjectFromActionForm(ProjectForm frm,
			HttpServletRequest request) {
		ProjectTO pto = new ProjectTO();
		Locale loc = SessionUtil.getCurrentLocale(request);

		pto.setCreationDate(frm.getCreationDate());
		if (frm.getEstimatedClosureDate() != null
				&& !frm.getEstimatedClosureDate().trim().equals("")) {
			pto.setEstimatedClosureDate(DateUtil.getDateTime(frm
					.getEstimatedClosureDate(), super.getCalendarMask(request),
					loc));
		} else {
			pto.setEstimatedClosureDate(null);
		}

		pto.setDescription(frm.getDescription());
		pto.setFinalDate(null);
		pto.setName(frm.getName());
		pto.setParentProject(new ProjectTO(frm.getParentProject()));
		pto.setProjectStatus(new ProjectStatusTO(frm.getProjectStatus()));
		pto.setCanAlloc(frm.getCanAlloc());
		pto.setAllowBillable(new Boolean(frm.getAllowBillable()));

		pto.setRepositoryClass(frm.getRepositoryClass());
		pto.setRepositoryPass(frm.getRepositoryPass());
		pto.setRepositoryURL(frm.getRepositoryURL());
		pto.setRepositoryUser(frm.getRepositoryUser());

		// populate user objects allocated into project
		pto.setInsertCustomers(this.getAllocUserVector(request,
				CustomerTO.ROLE_CUSTOMER));
		pto.setInsertResources(this.getAllocUserVector(request,
				ResourceTO.ROLE_RESOURCE));
		pto.setInsertLeaders(this.getAllocUserVector(request,
				LeaderTO.ROLE_LEADER));

		Vector metaFieldList = (Vector) request.getSession().getAttribute(
				"metaFieldList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, pto);
		if (metaFieldList != null) {
			frm.setAdditionalFields(pto.getAdditionalFields());
		}

		// set the id of project AND dispatch the process to LINK project_id
		// with her allocUser objects...
		pto.setId(frm.getId());

		pto.addRepositoryPolicy(getPolicy(pto,
				RepositoryPolicyTO.POLICY_COMMENT_MANDATORY, frm
						.getPolicyMandatoryComment()));
		pto.addRepositoryPolicy(getPolicy(pto,
				RepositoryPolicyTO.POLICY_OPEN_PROJ, frm
						.getPolicyAllowOnlyOpenProj()));
		pto.addRepositoryPolicy(getPolicy(pto,
				RepositoryPolicyTO.POLICY_PROJ_RESOURCE, frm
						.getPolicyAllowOnlyResources()));
		pto.addRepositoryPolicy(getPolicy(pto,
				RepositoryPolicyTO.POLICY_REPOS_SAME_PROJ, frm
						.getPolicyCheckRepositorySource()));
		pto.addRepositoryPolicy(getPolicy(pto,
				RepositoryPolicyTO.POLICY_ENTITY_REF, frm
						.getPolicyCheckEntityReference()));

		return pto;
	}

	private RepositoryPolicyTO getPolicy(ProjectTO pto, String type,
			boolean status) {
		RepositoryPolicyTO response = new RepositoryPolicyTO();
		response.setProject(pto);
		response.setValue(status ? "on" : "off");
		response.setPolicyType(type);
		return response;
	}

	/**
	 * Get data from form object based on allocUser vector into session and
	 * convert into vector of user objects with the attributes set.
	 */
	private Vector getAllocUserVector(HttpServletRequest request, Integer role) {
		Vector vlist = null;
		Vector sessionUserList = (Vector) request.getSession().getAttribute(
				"allocList");

		// define the appropriate column type based on role desired
		String colType = "";
		if (role.equals(CustomerTO.ROLE_CUSTOMER)) {
			colType = ProjectGridCheckBoxDecorator.CUSTOMER_COL;
		} else if (role.equals(ResourceTO.ROLE_RESOURCE)) {
			colType = ProjectGridCheckBoxDecorator.RESOURCE_COL;
		} else if (role.equals(LeaderTO.ROLE_LEADER)) {
			colType = ProjectGridCheckBoxDecorator.LEADER_COL;
		}

		// create a specific user object based on keys from array
		if (sessionUserList != null) {
			for (int i = 0; i < sessionUserList.size(); i++) {
				if (vlist == null)
					vlist = new Vector();
				UserTO uto = (UserTO) sessionUserList.elementAt(i);
				String value = request.getParameter("cb_" + uto.getId() + "_"
						+ colType);

				if (role.equals(CustomerTO.ROLE_CUSTOMER) && value != null) {
					CustomerTO cto = new CustomerTO(uto.getId());
					this.getSpecificCustomerAttr(request, cto);
					vlist.addElement(cto);
				} else if (role.equals(ResourceTO.ROLE_RESOURCE)
						&& value != null) {
					ResourceTO rto = new ResourceTO(uto.getId());
					this.getSpecificResourceAttr(request, rto);
					vlist.addElement(rto);
				} else if (role.equals(LeaderTO.ROLE_LEADER) && value != null) {
					LeaderTO eto = new LeaderTO(uto.getId());
					this.getSpecificLeaderAttr(request, eto);
					vlist.addElement(eto);
				}
			}
		}

		return vlist;
	}

	/**
	 * Get specific attributes of customer from form
	 */
	private void getSpecificCustomerAttr(HttpServletRequest request,
			CustomerTO cto) {
		String isDisab = request.getParameter("cb_" + cto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.DISABLE_USER_COL);
		cto.setIsDisabled(new Boolean(isDisab != null));

		String isReqAccept = request.getParameter("cb_" + cto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.REQ_ACCEPT_CUSTOMER_COL);
		cto.setIsReqAcceptable(new Boolean(isReqAccept != null));

		String canSeeTechComm = request.getParameter("cb_" + cto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.ALLOW_SEE_TECH_COMMENTS);
		cto.setCanSeeTechComments(new Boolean(canSeeTechComm != null));

		String canSeeDiscussion = request.getParameter("cb_" + cto.getId()
				+ "_" + ProjectGridCheckBoxDecorator.ALLOW_SEE_DISCUSSION_COL);
		cto.setCanSeeDiscussion(new Boolean(canSeeDiscussion != null));

		String canSeeOtherReqs = request.getParameter("cb_" + cto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.CAN_SEE_OTHER_REQS_COL);
		cto.setCanSeeOtherReqs(new Boolean(canSeeOtherReqs != null));

		String canOpenOtherReqs = request.getParameter("cb_" + cto.getId()
				+ "_"
				+ ProjectGridCheckBoxDecorator.CAN_OPEN_OTHEROWNER_REQS_COL);
		cto.setCanOpenOtherOwnerReq(new Boolean(canOpenOtherReqs != null));

		String preApprove = request.getParameter("cb_" + cto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.ALLOW_PRE_APPROVE_COL);
		cto.setPreApproveReq(new Boolean(preApprove != null));
	}

	/**
	 * Get specific attributes of resource from form
	 */
	private void getSpecificResourceAttr(HttpServletRequest request,
			ResourceTO rto) {

		this.getSpecificCustomerAttr(request, rto);

		String seeCust = request.getParameter("cb_" + rto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.ALLOW_SEE_CUSTOMER_COL);
		rto.setCanSeeCustomer(new Boolean(seeCust != null));

		String selfAlloc = request.getParameter("cb_" + rto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.CAN_SELF_ALLOC_COL);
		rto.setCanSelfAlloc(new Boolean(selfAlloc != null));

		String seeReposit = request.getParameter("cb_" + rto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.CAN_SEE_REPOSITORY_COL);
		rto.setCanSeeRepository(new Boolean(seeReposit != null));

		String seeInvoice = request.getParameter("cb_" + rto.getId() + "_"
				+ ProjectGridCheckBoxDecorator.CAN_SEE_INVOICE_COL);
		rto.setCanSeeInvoice(new Boolean(seeInvoice != null));

	}

	/**
	 * Get specific attributes of leader from form
	 */
	private void getSpecificLeaderAttr(HttpServletRequest request, LeaderTO eto) {
		this.getSpecificResourceAttr(request, eto);
	}

}
