package com.pandora.gui.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * This class handle the actions performed into Area form
 */
public class DepartmentAction extends GeneralStrutsAction{

	/**
	 * Shows the Department form
	 */
	public ActionForward manageDepartment(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){

		String forward = "showDepartment";

		return mapping.findForward(forward);
	}
}