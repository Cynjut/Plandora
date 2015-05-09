package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.ResourceCapacityTO;
import com.pandora.UserTO;
import com.pandora.delegate.ResourceCapacityDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.struts.form.ResCapacityEditForm;
import com.pandora.gui.struts.form.ResCapacityPanelForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class ResCapacityEditAction extends GeneralStrutsAction {

	public ActionForward showEditPopup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityEdit";
		UserDelegate udel = new UserDelegate();
		ResourceCapacityDelegate rdel = new ResourceCapacityDelegate();

		try {
			ResCapacityEditForm frm = (ResCapacityEditForm)form;
			UserTO uto = SessionUtil.getCurrentUser(request);
			Timestamp scDt = DateUtil.getDateTime(frm.getSinceDate(), "yyyyMMdd", uto.getLocale());
			frm.setSinceDate(DateUtil.getDate(scDt, uto.getCalendarMask(), uto.getLocale()));

			ResCapacityPanelForm panel = (ResCapacityPanelForm)request.getSession().getAttribute("resCapacityPanelForm");
			if (panel!=null) {
				request.getSession().setAttribute("ResCapEditProjectList", panel.getHmProjectlist().values());
				request.getSession().setAttribute("ResCapEditResourceList", panel.getHmUserlist().values());
			}

			Locale currLoc = udel.getCurrencyLocale();	
			NumberFormat nf = NumberFormat.getCurrencyInstance(currLoc);
			String cs = nf.getCurrency().getSymbol();
			frm.setCurrencySymbol(cs);
			
			Vector capList = rdel.getListByResourceProject(frm.getEditResourceId(), frm.getEditProjectId());
			if (capList!=null) {
				ResCapacityPanelAction capAction = new ResCapacityPanelAction();
				ResourceCapacityTO rcto = capAction.getCapacity(scDt, frm.getEditProjectId(), frm.getEditResourceId(), capList);
				frm.setCapacity(rcto.getCapacity()+"");
				if (rcto.getCostPerHour()!=null) {
					String strCost = StringUtil.getFloatToString((rcto.getCostPerHour().floatValue()/100), "0.##", uto.getLocale());
					frm.setCost(strCost);
				} else {
					frm.setCost(StringUtil.getFloatToString(0, "0.##", uto.getLocale()));
				}
				
			}
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);			
		}
		
		return mapping.findForward(forward);
	}


	public ActionForward showInsertPopup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityEdit";
		UserDelegate udel = new UserDelegate();

		try {

			ResCapacityEditForm frm = (ResCapacityEditForm)form;
			frm.clear();

			ResCapacityPanelForm panel = (ResCapacityPanelForm)request.getSession().getAttribute("resCapacityPanelForm");
			if (panel!=null) {
				request.getSession().setAttribute("ResCapEditProjectList", panel.getHmProjectlist().values());
				request.getSession().setAttribute("ResCapEditResourceList", panel.getHmUserlist().values());
				frm.setEditProjectId(panel.getProjectId());
				frm.setEditResourceId(panel.getResourceId());
			}

			Locale currLoc = udel.getCurrencyLocale();	
			NumberFormat nf = NumberFormat.getCurrencyInstance(currLoc);
			String cs = nf.getCurrency().getSymbol();
			frm.setCurrencySymbol(cs);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);			
		}
		
		return mapping.findForward(forward);
	}
	
	
	public ActionForward saveCapacity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "goToCapacityForm";
		ResourceCapacityDelegate rdel = new ResourceCapacityDelegate();
		try {
			ResCapacityEditForm frm = (ResCapacityEditForm)form;
			ResourceCapacityTO rcto = this.getTransferObjectFromActionForm(frm, request);
			rdel.insertOrUpdate(rcto);			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);			
		}
		return mapping.findForward(forward);
	}
	
	
	private ResourceCapacityTO getTransferObjectFromActionForm(ResCapacityEditForm frm, HttpServletRequest request){
		ResourceCapacityTO rcto = new ResourceCapacityTO();
		rcto.setCapacity(new Integer(frm.getCapacity()));
		UserTO uto = SessionUtil.getCurrentUser(request);

		if (frm.getCost()!=null && !frm.getCost().trim().equals("")) {
			float f = StringUtil.getStringToFloat(frm.getCost(), uto.getLocale());
			int costInCents = (int) (f * 100);
			rcto.setCostPerHour(new Integer(costInCents));
		} else {
			rcto.setCostPerHour(null);
		}
		rcto.setProjectId(frm.getEditProjectId());
		rcto.setResourceId(frm.getEditResourceId());

		Timestamp dateCap = DateUtil.getDateTime(frm.getSinceDate(), super.getCalendarMask(request), uto.getLocale());
		rcto.setYear(new Integer(DateUtil.get(dateCap, Calendar.YEAR)));
		rcto.setDay(new Integer(DateUtil.get(dateCap, Calendar.DATE)));
		rcto.setMonth(new Integer(DateUtil.get(dateCap, Calendar.MONTH)+1));

		return rcto;
	}	
		
}
