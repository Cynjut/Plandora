package com.pandora.gui.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.gui.struts.form.ShortCutForm;
import com.pandora.helper.SessionUtil;

public class ShortCutAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showShortCutPopup";
		try {
			ShortCutForm frm = (ShortCutForm)form;
			
			String content = this.getHtmlCombo(request, "iconType", true);
			frm.setHtmlTypeList(content);
	
			content = this.getHtmlCombo(request, "opening", false);
			frm.setHtmlOpenList(content);

		} catch(Exception e){
		   this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return mapping.findForward(forward);		
	}

	
	public ActionForward saveShortCut(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "home";
		PreferenceDelegate pdel = new PreferenceDelegate();
		try {

			ShortCutForm frm = (ShortCutForm)form;
			forward = frm.getGotoAfterSave();
			if (forward==null || forward.trim().equals("")) {
				forward = "home";	
			}
			
			int emptySlot = 1;
			UserTO uto = SessionUtil.getCurrentUser(request);
			for (int i = 1; i<=10; i++) {
				String slot = uto.getPreference().getPreference(PreferenceTO.SHORTCUT_NAME + i);
				if (slot==null || slot.trim().equals("")) {
					emptySlot = i;
					break;
				}
			}

			PreferenceTO preferences = new PreferenceTO();
			preferences.addPreferences(new PreferenceTO(PreferenceTO.SHORTCUT_NAME+emptySlot, frm.getShorcutTitle(), uto));
			preferences.addPreferences(new PreferenceTO(PreferenceTO.SHORTCUT_ICON+emptySlot, frm.getIconType(), uto));
			preferences.addPreferences(new PreferenceTO(PreferenceTO.SHORTCUT_URL+emptySlot, frm.getShortcutURI(), uto));
			preferences.addPreferences(new PreferenceTO(PreferenceTO.SHORTCUT_OPEN+emptySlot, frm.getOpening(), uto));
			pdel.insertOrUpdate(preferences);
			
			this.setSuccessFormSession(request, "message.shortcut.updateOK");
			
	        PreferenceTO refreshedPreferences = pdel.getObjectByUser(uto);
	        uto.setPreference(refreshedPreferences);
			
		} catch(Exception e){
			   this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);		
	}
	
	
	public String getHtmlCombo(HttpServletRequest request, String comboId, boolean isTypeField) {
		String list = "<select id=\"" + comboId + "\" name=\"" + comboId + "\" class=\"textBox\">";
		if (isTypeField) {
			for (int i=1; i<=8; i++) {
				list = list + "<option value=\"" + i + "\">" + super.getBundleMessage(request, "label.shortcut.type." + i) + "</option>\n";	
			}			
		} else {
			for (int i=1; i<=2; i++) {
				list = list + "<option value=\"" + i + "\">" + super.getBundleMessage(request, "label.shortcut.opening." + i) + "</option>\n";	
			}
		}
		list = list + "</select>";
		return list;
	}
	
}
