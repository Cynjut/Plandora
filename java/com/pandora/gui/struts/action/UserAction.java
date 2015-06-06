package com.pandora.gui.struts.action;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.pandora.AreaTO;
import com.pandora.CompanyTO;
import com.pandora.DepartmentTO;
import com.pandora.FunctionTO;
import com.pandora.MetaFormTO;
import com.pandora.PreferenceTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.auth.Authentication;
import com.pandora.bus.auth.SystemAuthentication;
import com.pandora.delegate.AreaDelegate;
import com.pandora.delegate.CompanyDelegate;
import com.pandora.delegate.DepartmentDelegate;
import com.pandora.delegate.FunctionDelegate;
import com.pandora.delegate.MetaFormDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.MaxSizeAttachmentException;
import com.pandora.exception.UserNameAlreadyExistsException;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.UserForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into Manage User form
 */
public class UserAction extends GeneralStrutsAction {
    
	/**
	 * Shows the Manage User form
	 */
	public ActionForward prepareUser(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
		String forward = "showUser";
		try {
		    
		    //clear current form
		    this.clearForm(form, request);
		    this.clearMessages(request);
		    
			//get all Departments from data base and put into http session (to be displayed by combo)
			DepartmentDelegate depdel = new DepartmentDelegate();
			Vector<DepartmentTO> depList = depdel.getDepartmentList();
			request.getSession().setAttribute("departmentList", depList);

			CompanyDelegate comdel = new CompanyDelegate();
			Vector<CompanyTO> comList = comdel.getCompanyList();
			request.getSession().setAttribute("userCompany", comList);
			comList.add(0, new CompanyTO());
			
			//get all Areas from data base and put into http session (to be displayed by combo)
			AreaDelegate areaDel = new AreaDelegate();
			Vector<AreaTO> areaList = areaDel.getAreaList();
			request.getSession().setAttribute("areaList", areaList);
			
			//get all functions from data base and put into http session (to be displayed by combo)
			FunctionDelegate funcDel = new FunctionDelegate();
			Vector<FunctionTO> functionList = funcDel.getFunctionList();
			request.getSession().setAttribute("functionList", functionList);
			
			Vector<TransferObject> authModeList = this.getAuthenticationList(request);
			request.getSession().setAttribute("authModeList", authModeList);

		    request.getSession().setAttribute("enableList", this.getEnableList(request));
		    
			//get all users from data base and put into http session (to be displayed by grid)
			this.refreshList(request, form);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showUserForm", e);
		}

		return mapping.findForward(forward);
	}

	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showUser");
	}


	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    try {
			this.refreshList(request, form);
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showUserForm", e);
		}
		return mapping.findForward("showUser");
	}

	
	private Vector<TransferObject> getAuthenticationList(HttpServletRequest request)throws BusinessException {
		Vector<TransferObject> authModeList = new Vector<TransferObject>();
		UserDelegate udel = new UserDelegate();
		UserTO root = udel.getRoot();
		String authList = root.getPreference().getPreference(PreferenceTO.AUTH_BUS_CLASS);
		if (authList!=null) {
			String[] auths = authList.split(";");
			if (auths!=null && auths.length>0) {
				for (int i=0; i<auths.length; i++) {
					Authentication authClass = Authentication.getClass(auths[i].trim());
					if (authClass!=null) {
						String label = this.getBundleMessage(request, authClass.getUniqueName(), true);
						TransferObject obj = new TransferObject(authClass.getClass().getName(), label);
						authModeList.add(obj);						
					}
				}					
			}
		}
		return authModeList;
	}

	
	/**
	 * Refresh grid with list of all users from data base.
	 */
	private void refreshList(HttpServletRequest request, ActionForm form) throws BusinessException{
		UserDelegate udel = new UserDelegate();
		UserForm usrfrm = (UserForm)form;
				
		try {
			if (request.getParameter("hideDisableUsers")==null) {
				usrfrm.setHideDisableUsers(false);
			}
			
			String htmlBody = this.getPermissionGrid(request, null);
			usrfrm.setPermissionListHtmlBody(htmlBody);
			
			Vector<UserTO> userList = udel.getUserList(usrfrm.getHideDisableUsers());
			request.getSession().setAttribute("userList", userList);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * This method prepare the form for updating an user. This method get the 
	 * information of specific user from data base and put the data into the
	 * html fields.
	 */
	public ActionForward editUser(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showUser";
		
	    try {
		    UserForm usrfrm = (UserForm)form;
			UserDelegate udel = new UserDelegate();
			
			//clear messages of form
			this.clearMessages(request);
			
			//set current operation status for Updating	
			usrfrm.setSaveMethod(UserForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
			
			//create a user object used such a filter
			UserTO filter = new UserTO();
			filter.setId(usrfrm.getId());
			
			//get a specific user from data base
			UserTO uto = udel.getUser(filter);
			
			//put the data (from DB) into html fields
			this.getActionFormFromTransferObject(uto, usrfrm, request);
			if (uto.getFileInBytes()!=null && uto.getFileInBytes().length>0) {
			    usrfrm.setUpload(false);    
			} else {
			    usrfrm.setUpload(true);
			}
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.prepareUpdateUserForm", e);
	    }

		return mapping.findForward(forward);		
	}
	
	
	/**
	 * Clear all values of current form.
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showUser";
		this.clearForm(form, request);
		this.clearMessages(request);
		return mapping.findForward(forward);		
	}

	
	public ActionForward clearPicture(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showUser";
		UserForm usrfrm = (UserForm)form;
		usrfrm.setUpload(true);
		usrfrm.setTheFile(null);
		return mapping.findForward(forward);		
	}
	

	/**
	 * Clear all values of current form.
	 */
	private void clearForm(ActionForm form, HttpServletRequest request){
	    UserForm usrfrm = (UserForm)form;
		usrfrm.clear();
		
		//set current operation status for Insertion
		usrfrm.setSaveMethod(UserForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
	}
	
	
	/**
	 * This method is performed after Save button click event.<br>
	 * Is used to update or insert data of user into data base.
	 */
	public ActionForward saveUser(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    
		String forward = "showUser";
		String errorMsg = "error.showUserForm";
		String succeMsg = "message.success";
		
		try {
			UserForm usrfrm = (UserForm)form;
			UserDelegate udel = new UserDelegate();

			//create an UserTO object based on html fields
			UserTO uto = this.getTransferObjectFromActionForm(usrfrm, request);
							
			if (usrfrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){
				
				//check if username already exists
				udel.checkUserName(uto);
				
			    errorMsg = "error.insertUserForm";
			    succeMsg = "message.insertUser";
			    udel.insertUser(uto);
			    this.clearForm(form, request);
			    
				//save password in encrypted format
				udel.updatePassword(uto);
			    
			} else {
								
			    errorMsg = "error.updateUserForm";
			    succeMsg = "message.updateUser";
			    udel.updateUser(uto);
			}
						
			//set success message into http session
			this.setSuccessFormSession(request, succeMsg);
			
			//get all users from data base and put into http session (to be displayed by grid)
			this.refreshList(request, form);

		} catch(MaxSizeAttachmentException e){
		    this.setErrorFormSession(request, "error.usernamePicMaxSize", e);
		} catch(UserNameAlreadyExistsException e){
		    this.setErrorFormSession(request, "error.usernameAlreadyExists", e);		
		} catch(Exception e){
		    this.setErrorFormSession(request, errorMsg, e);
		}
		return mapping.findForward(forward);		
	}
	

	/**
	 * Remove a selected user of data base.
	 */
	public ActionForward removeUser(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){   
		String forward = "showUser";
		try {		    
			//clear form and messages
			this.clearMessages(request);
		    
			UserForm usrfrm = (UserForm)form;
			UserDelegate udel = new UserDelegate();
		    						
			//create an UserTO object based on html fields
			UserTO filter = new UserTO();
			filter.setId(usrfrm.getId());

			//get a specific user from data base
			UserTO uto = udel.getUser(filter);
			
			//remove user from data base
			udel.removeUser(uto);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeUser");		        
			
			//get all users from data base and put into http session (to be displayed by grid)
			this.refreshList(request, form);
		
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.removeUserForm", e);
		}
		return mapping.findForward(forward);
	}	

	
	/**
	 * Remove a selected user of data base.
	 */
	public ActionForward changePassword(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){   
		String forward = "showUser";
		try {
			UserForm usrfrm = (UserForm)form;
			UserDelegate udel = new UserDelegate();

		    if (usrfrm.getSaveMethod().equals(GeneralStrutsForm.UPDATE_METHOD)){

				//create an UserTO object based on html fields
				UserTO uto = this.getTransferObjectFromActionForm(usrfrm, request);
				
				//check if username already exists
				udel.checkUserName(uto);
										
				//save password in encrypted format
				udel.updatePassword(uto);
				
				//set success message into http session
				this.setSuccessFormSession(request, "message.updatePassUser");					        
		    }
		
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.passUpdForm", e);
		}
		return mapping.findForward(forward);		
	}	
	
	
	/**
	 * Put data of html fields into TransferObject 
	 */
	private UserTO getTransferObjectFromActionForm(UserForm frm, HttpServletRequest request){
		UserTO uto = new UserTO();
		uto.setId(frm.getId());
		uto.setUsername(frm.getUsername());
		uto.setColor(frm.getColor());
		uto.setEmail(frm.getEmail());
		uto.setName(frm.getName());
		uto.setPassword(frm.getPassword());
		uto.setPhone(frm.getPhone());
		uto.setArea(new AreaTO(frm.getUserArea()));
		uto.setDepartment(new DepartmentTO(frm.getUserDepartment()));
		uto.setFunction(new FunctionTO(frm.getUserFunction()));
		uto.setUsername(uto.getUsername());
		if (frm.getBirth()!=null && !frm.getBirth().trim().equals("")) {
		    Timestamp ts = DateUtil.getDateTime(frm.getBirth(), super.getCalendarMask(request), SessionUtil.getCurrentLocale(request));
		    uto.setBirth(new Date(ts.getTime()));    
		} else {
		    uto.setBirth(null);
		}
		
		if (frm.getUsername().equals(RootTO.ROOT_USER)) {
			uto.setAuthenticationMode(SystemAuthentication.class.getName());
		} else {
			uto.setAuthenticationMode(frm.getAuthenticationMode());	
		}
		
		if (frm.getEnableStatus()!=null && frm.getEnableStatus().equals("0")) {
			uto.setFinalDate(DateUtil.getNow());	
		} else {
			uto.setFinalDate(null);
		}

		if (frm.getCreationDate()!=null && !frm.getCreationDate().trim().equals("")) {
		    Timestamp ts = DateUtil.getDateTime(frm.getCreationDate(), super.getCalendarMask(request), SessionUtil.getCurrentLocale(request));
		    if (ts!=null) {
		    	uto.setCreationDate(new Timestamp(ts.getTime()));	
		    }
		} else {
		    uto.setCreationDate(null);
		}

		String permissionParsing = "";
		try {
			MetaFormDelegate metaFormDel = new MetaFormDelegate();
			Vector<MetaFormTO> list = metaFormDel.getMetaFormList();
			if (list!=null) {
				Iterator<MetaFormTO> i = list.iterator();
				while(i.hasNext()) {
					MetaFormTO mfto = i.next();
					String check = request.getParameter("cb_" + mfto.getId() + "_PERMISSION");
					if (check!=null && !check.trim().equals("")) {
						permissionParsing = permissionParsing + "[" + mfto.getId() + "] ";
						mfto.setGenericTag(mfto.getId());
					} else {
						mfto.setGenericTag(null);
					}
				}
			}			
		} catch (Exception e){
			e.printStackTrace();
		}
		uto.setPermission(permissionParsing);		
		
		if (frm.getUserCompanyId()!=null && !frm.getUserCompanyId().trim().equals("")) {
			uto.setCompany(new CompanyTO(frm.getUserCompanyId()));
		} else {
			uto.setCompany(null);
		}

		uto.setGenericTag(null);		
		if (frm.isUpload()){
			try {
		        FormFile uploadFile = frm.getTheFile();
		        uto.setFileInBytes(uploadFile.getFileData());
		        uto.setGenericTag("IS_UPLOAD");
			} catch(Exception e) {
			    uto.setFileInBytes(null);
			}
		} else {
		    uto.setFileInBytes(null);
		}
		
		UserTO to = SessionUtil.getCurrentUser(request);
		uto.setCountry(to.getCountry());
		uto.setLanguage(to.getLanguage());
		
	    return uto;
	}
	

	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 */
	private void getActionFormFromTransferObject(UserTO uto, UserForm usrfrm, HttpServletRequest request){
		usrfrm.setColor(uto.getColor());
		usrfrm.setUsername(uto.getUsername());
		usrfrm.setEmail(uto.getEmail());
		usrfrm.setName(uto.getName());
		usrfrm.setPassword(uto.getPassword());
		usrfrm.setPhone(uto.getPhone());
		usrfrm.setUserArea(uto.getArea().getId());
		usrfrm.setUserDepartment(uto.getDepartment().getId());
		usrfrm.setUsername(uto.getUsername());
		usrfrm.setUserFunction(uto.getFunction().getId());
		if (uto.getBirth()!=null) {
		    Timestamp ts = new Timestamp(uto.getBirth().getTime());
		    usrfrm.setBirth(DateUtil.getDate(ts, super.getCalendarMask(request), SessionUtil.getCurrentLocale(request)));    
		} else {
		    usrfrm.setBirth("");
		}
		if (uto.getAuthenticationMode()==null) {
			usrfrm.setAuthenticationMode(SystemAuthentication.class.getName());
		} else {
			usrfrm.setAuthenticationMode(uto.getAuthenticationMode());	
		}
		
		usrfrm.setEnableStatus(uto.getFinalDate()==null?"1":"0");
		
		if (uto.getCompany()!=null) {
			usrfrm.setUserCompanyId(uto.getCompany().getId());
		} else {
			usrfrm.setUserCompanyId("");
		}
		
		try {
			String htmlBody = getPermissionGrid(request, uto.getPermission());
			usrfrm.setPermissionListHtmlBody(htmlBody);			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private String getPermissionGrid(HttpServletRequest request, String permission) throws BusinessException{
		MetaFormDelegate metaFormDel = new MetaFormDelegate();
		Vector<MetaFormTO> list = metaFormDel.getMetaFormList();
		
		StringBuffer htmlBody = new StringBuffer();
		
		htmlBody.append("<tr class=\"tableRowHeader\"><th class=\"tableCellHeader\">&nbsp;</th>");
		htmlBody.append("<th class=\"tableCellHeader\">" + this.getBundleMessage(request, "label.grid.metaform") + "</th></tr>");
		
		Iterator<MetaFormTO> i = list.iterator();
		while(i.hasNext()) {
			MetaFormTO mfto = i.next();
			
			htmlBody.append("<tr class=\"pagingFormBody\"><td>");
			
			boolean val = false;
			if (permission!=null && permission.indexOf("[" + mfto.getId() + "]")>-1) {
				val = true;
			}
			htmlBody.append(HtmlUtil.getChkBox(val, mfto.getId(), "PERMISSION", false));
			htmlBody.append("</td><td class=\"formBody\">");
			htmlBody.append(mfto.getName());
			htmlBody.append("</td>");
			htmlBody.append("</tr>");
		}
		
		return htmlBody.toString();
	}
}
