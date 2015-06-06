package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.struts.action.projectpanel.ProjectInfoProjectPanel;
import com.pandora.gui.struts.form.ProjectPanelPopupForm;

public class ProjectPanelPopupAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showProjectPanelPopup";
		StringBuffer fields = new StringBuffer("");
		ProjectPanelPopupForm frm = (ProjectPanelPopupForm)form;
		UserDelegate udel = new UserDelegate();
		ProjectPanelAction ppac = new ProjectPanelAction();
		
		try {

			Vector<ProjectPanelAction> panels = ppac.getPanels();
			if (panels!=null && frm.getPanelBoxId()!=null) {
				for (ProjectPanelAction ppa : panels) {
					if (ppa!=null && ppa.getPanelId().equals(frm.getPanelBoxId())) {
						
						String leaderLbl = super.getBundleMessage(request, "label.importExport.role_2");
						String resourceLbl = super.getBundleMessage(request, "label.importExport.role_1");
						String customerLbl = super.getBundleMessage(request, "label.importExport.role_0");
						
						String disableStr = "";
						String checkResourceStr = "", checkCustomerStr = "";
						if (ppa instanceof ProjectInfoProjectPanel) {
							disableStr = "disabled=\"1\" checked ";
						} else {
							disableStr = "";
							String keyResource = PreferenceTO.OVERVIEW_PROJ_PREFIX + frm.getProjectId() + "." + ppa.getPanelId() + ".1";
							String keyCustomer = PreferenceTO.OVERVIEW_PROJ_PREFIX + frm.getProjectId() + "." + ppa.getPanelId() + ".0";
							UserTO root = udel.getRoot();
							String prefRes = root.getPreference().getPreference(keyResource);
							if (prefRes!=null && prefRes.equalsIgnoreCase("on")) {
								checkResourceStr = "checked ";
							}
							String prefCus = root.getPreference().getPreference(keyCustomer);								
							if (prefCus!=null && prefCus.equalsIgnoreCase("on")) {
								checkCustomerStr = "checked ";
							}
						}

						fields.append("<input type=\"checkbox\" onclick=\"changeRole(2, '" + ppa.getPanelId()+ "', '" + frm.getProjectId() + "');\" id=\"chk_2_" + ppa.getPanelId()+ "\" disabled=\"1\" name=\"chk_2_" + ppa.getPanelId()+ "\" checked value=\"on\">" + leaderLbl + "</br>");
						fields.append("<input type=\"checkbox\" onclick=\"changeRole(1, '" + ppa.getPanelId()+ "', '" + frm.getProjectId() + "');\" id=\"chk_1_" + ppa.getPanelId()+ "\" " + disableStr + checkResourceStr + "name=\"chk_1_" + ppa.getPanelId()+ "' value=\"on\">" + resourceLbl + "</br>");
						fields.append("<input type=\"checkbox\" onclick=\"changeRole(0, '" + ppa.getPanelId()+ "', '" + frm.getProjectId() + "');\" id=\"chk_0_" + ppa.getPanelId()+ "\" " + disableStr + checkCustomerStr + "name=\"chk_0_" + ppa.getPanelId()+ "' value=\"on\">" + customerLbl + "</br>");
						
						frm.setPanelBoxHtml(fields.toString());
					}
				}
			}
			
		}catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);
	}
	
	public ActionForward changeViewPanel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showProjectPanelPopup";
		UserDelegate udel = new UserDelegate();
		PreferenceDelegate pfdel = new PreferenceDelegate();
		try {
			ProjectPanelPopupForm frm = (ProjectPanelPopupForm)form;
			
			String key = PreferenceTO.OVERVIEW_PROJ_PREFIX + frm.getProjectId() + "." + frm.getPanelBoxId() + "." + frm.getGenericTag();
			UserTO root = udel.getRoot();
			String pref = root.getPreference().getPreference(key);

		    PreferenceTO pto = new PreferenceTO(key, (pref.equals("") || pref.equalsIgnoreCase("off"))?"on":"off", root);
		    root.getPreference().addPreferences(pto);
			pto.addPreferences(pto);
			pfdel.insertOrUpdate(pto);

		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);	    
	}
	
}
