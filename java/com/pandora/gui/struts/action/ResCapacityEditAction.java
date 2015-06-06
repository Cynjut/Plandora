package com.pandora.gui.struts.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
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
			
			Timestamp finalDate = null;
			int gran = 0;
			ResCapacityPanelForm panel = (ResCapacityPanelForm)request.getSession().getAttribute("resCapacityPanelForm");
			if (panel!=null) {
				gran = panel.getGranularity();
				request.getSession().setAttribute("ResCapEditProjectList", panel.getHmProjectlist().values());
				request.getSession().setAttribute("ResCapEditResourceList", panel.getHmUserlist().values());
				finalDate = DateUtil.getDateTime(panel.getFinalDate(), uto.getCalendarMask(), uto.getLocale());
			}

			Locale currLoc = udel.getCurrencyLocale();	
			NumberFormat nf = NumberFormat.getCurrencyInstance(currLoc);
			String cs = nf.getCurrency().getSymbol();
			frm.setCurrencySymbol(cs);
			
			String floatVal = StringUtil.getFloatToString((float)0.1, uto.getLocale());
			if (floatVal!=null && floatVal.indexOf(",")>-1) {
				frm.setCommaDecimalSeparator("on");	
			} else {
				frm.setCommaDecimalSeparator("off");
			}
			
			Vector<ResourceCapacityTO> capList = rdel.getListByResourceProject(frm.getEditResourceId(), frm.getEditProjectId(), uto);
			if (capList!=null) {
				
				ResourceCapacityTO rcto = null;
				ResCapacityPanelAction capAction = new ResCapacityPanelAction();
				if (gran!=0 && finalDate!=null) {
					Timestamp nextCursor = capAction.nextCursorDate(scDt, gran, finalDate);
					rcto = capAction.getCapacity(scDt, nextCursor, frm.getEditProjectId(), frm.getEditResourceId(), capList, panel.getGranularity(), finalDate);
					frm.setGranularityLabel(this.getGranularityLabel(gran, request));					
				}
				
				if (rcto!=null) {
					String capValStr = "0";
					if (rcto.getCapacity()!=null) {
						
						float capHr = (float)(rcto.getCapacity().floatValue() / (float)60);
						BigDecimal bd = new BigDecimal(capHr).setScale(2, RoundingMode.HALF_EVEN);
						capHr = bd.floatValue();
						capValStr = StringUtil.getFloatToString(capHr, "0.#", uto.getLocale());
					}
					frm.setCapacity(capValStr);
					
					if (rcto.getCostPerHour()!=null) {
						String strCost = StringUtil.getFloatToString((rcto.getCostPerHour().floatValue()/100), "0.##", uto.getLocale());
						frm.setCost(strCost);
					} else {
						frm.setCost(StringUtil.getFloatToString(0, "0.##", uto.getLocale()));
					}					
				}				
			}
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);			
		}
		
		return mapping.findForward(forward);
	}


	private String getGranularityLabel(int gran, HttpServletRequest request) {
		String response = "";
		if (gran==-1) {
			response = super.getBundleMessage(request, "label.resCapacity.gran.label.3");
		} else if (gran==7){
			response = super.getBundleMessage(request, "label.resCapacity.gran.label.2");
		} else if (gran==14){
			response = super.getBundleMessage(request, "label.resCapacity.gran.label.1");
		} else if (gran==1){
			response = super.getBundleMessage(request, "label.resCapacity.gran.label.4");			
		}
		return response;
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

	
	public ActionForward removeCapacity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "goToCapacityForm";
		try {
			ResourceCapacityDelegate rdel = new ResourceCapacityDelegate();
			ResCapacityEditForm frm = (ResCapacityEditForm)form;
			UserTO uto = SessionUtil.getCurrentUser(request);
			Timestamp dateCap = DateUtil.getDateTime(frm.getSinceDate(), super.getCalendarMask(request), uto.getLocale());

			ResourceCapacityTO rcto = new ResourceCapacityTO();
			rcto.setDay(DateUtil.get(dateCap, Calendar.DATE));
			rcto.setMonth(DateUtil.get(dateCap, Calendar.MONTH)+1);
			rcto.setYear(DateUtil.get(dateCap, Calendar.YEAR));
			rcto.setProjectId(frm.getEditProjectId());
			rcto.setResourceId(frm.getEditResourceId());
			
			rdel.remove(rcto);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);			
		}

		
		return mapping.findForward(forward);
	}
	
	
	public ActionForward saveCapacity(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		String forward = "goToCapacityForm";
		ResourceCapacityDelegate rdel = new ResourceCapacityDelegate();
		try {
			ResCapacityEditForm frm = (ResCapacityEditForm)form;
			
			int gran = 0;
			ResCapacityPanelForm panel = (ResCapacityPanelForm)request.getSession().getAttribute("resCapacityPanelForm");
			if (panel!=null) {
				UserTO uto = SessionUtil.getCurrentUser(request);
				gran = panel.getGranularity();
				Timestamp finalDate = DateUtil.getDateTime(panel.getFinalDate(), uto.getCalendarMask(), uto.getLocale());

				boolean saveFirstValid = false;
				ArrayList<ResourceCapacityTO> list = this.getTransferObjectFromActionForm(frm, request, gran, finalDate);
				for (ResourceCapacityTO rcto : list) {
					rdel.remove(rcto);

					int dw = DateUtil.get(rcto.getDate(), Calendar.DAY_OF_WEEK);
					if (list.size()==1 || (dw!=Calendar.SATURDAY && dw!=Calendar.SUNDAY && !saveFirstValid)) {
						rdel.insertOrUpdate(rcto);
						saveFirstValid = true;
					}
				}
			}
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);			
		}
		return mapping.findForward(forward);
	}
	
	
	private ArrayList<ResourceCapacityTO> getTransferObjectFromActionForm(ResCapacityEditForm frm, HttpServletRequest request, int granularity, Timestamp finalDate) throws Exception{
		ArrayList<ResourceCapacityTO> response = new ArrayList<ResourceCapacityTO>();
		ResCapacityPanelAction capAction = new ResCapacityPanelAction();
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		String capStrVal = frm.getCapacity();
		if (StringUtil.checkIsFloat(capStrVal, uto.getLocale())) {
			
			Timestamp dateCap = DateUtil.getDateTime(frm.getSinceDate(), super.getCalendarMask(request), uto.getLocale());
			if (dateCap!=null) {
				Timestamp nextCursor = capAction.nextCursorDate(dateCap, granularity, finalDate);
				if (nextCursor!=null) {

					int numSlots = capAction.getNumberSlots(dateCap, nextCursor);
					float val = StringUtil.getStringToFloat(capStrVal, uto.getLocale());
					int capMinutes = (int)(val * 60);
					
					Timestamp cursor = dateCap;
					while(cursor.before(nextCursor)) {
						ResourceCapacityTO rcto = new ResourceCapacityTO();
						rcto.setYear(new Integer(DateUtil.get(cursor, Calendar.YEAR)));
						rcto.setDay(new Integer(DateUtil.get(cursor, Calendar.DATE)));
						rcto.setMonth(new Integer(DateUtil.get(cursor, Calendar.MONTH)+1));
						rcto.setProjectId(frm.getEditProjectId());
						rcto.setResourceId(frm.getEditResourceId());
						
						int dw = DateUtil.get(rcto.getDate(), Calendar.DAY_OF_WEEK);
						if (numSlots>1 && (dw==Calendar.SATURDAY || dw==Calendar.SUNDAY)) {
							rcto.setCapacity(0);
						} else {
							rcto.setCapacity(new Integer(capMinutes / numSlots));						
						}

						if (frm.getCost()!=null && !frm.getCost().trim().equals("")) {
							float f = StringUtil.getStringToFloat(frm.getCost(), uto.getLocale());
							int costInCents = (int) (f * 100);
							rcto.setCostPerHour(new Integer(costInCents));
						} else {
							rcto.setCostPerHour(null);
						}
						response.add(rcto);
						cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, 1);
					}
					
				} else {
					throw new Exception("The value of capacity [" + capStrVal + "] cannot be edited.");	
				}
			} else {
				throw new Exception("The value of capacity [" + capStrVal + "] cannot be edited.");	
			}
		} else {
			throw new Exception("The value of capacity [" + capStrVal + "] is invalid");
		}
		
		return response;
	}		
}
