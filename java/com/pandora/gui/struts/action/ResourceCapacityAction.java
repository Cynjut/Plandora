package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.ResourceCapacityTO;
import com.pandora.UserTO;
import com.pandora.delegate.GadgetDelegate;
import com.pandora.delegate.ResourceCapacityDelegate;
import com.pandora.gui.struts.form.ResourceCapacityForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

//TODO DEPRECATED
public class ResourceCapacityAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResourceCapacity";
		return mapping.findForward(forward);
	}


	public ActionForward renderChart(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			ResourceCapacityForm rcfrm = (ResourceCapacityForm)form;
			GadgetDelegate del = new GadgetDelegate();
			Vector params = new Vector();
			
			params.addElement(rcfrm.getResourceId());
			params.addElement(rcfrm.getProjectId());
			del.renderContent(request, response, "com.pandora.gui.struts.action.ResourceCapacityChart", params);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
	    return null;
	}

	
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResourceCapacity";
		ResourceCapacityDelegate rdel = new ResourceCapacityDelegate();
		try {		
			ResourceCapacityForm rcfrm = (ResourceCapacityForm)form;
			ResourceCapacityTO rcto = this.getTransferObjectFromActionForm(rcfrm, request);
			rdel.insertOrUpdate(rcto);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);
	}

	
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResourceCapacity";
		ResourceCapacityDelegate rdel = new ResourceCapacityDelegate();
		try {		
			ResourceCapacityForm rcfrm = (ResourceCapacityForm)form;
			ResourceCapacityTO rcto = this.getTransferObjectFromActionForm(rcfrm, request);
			rdel.remove(rcto);
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);
	}

	
	private ResourceCapacityTO getTransferObjectFromActionForm(ResourceCapacityForm frm, HttpServletRequest request){
		ResourceCapacityTO rcto = new ResourceCapacityTO();
		rcto.setCapacity(new Integer(frm.getCapacityValue()));
		
		if (frm.getCostValue()!=null && !frm.getCostValue().trim().equals("")) {
			int costInCents = (int) ((Float.parseFloat(frm.getCostValue())) * 100);
			rcto.setCostPerHour(new Integer(costInCents));
		} else {
			rcto.setCostPerHour(null);
		}
		rcto.setProjectId(frm.getProjectId());
		rcto.setResourceId(frm.getResourceId());

		UserTO uto = SessionUtil.getCurrentUser(request);
		Timestamp dateCap = DateUtil.getDateTime(frm.getCapacityDate(), super.getCalendarMask(request), uto.getLocale());
		rcto.setYear(new Integer(DateUtil.get(dateCap, Calendar.YEAR)));
		rcto.setDay(new Integer(DateUtil.get(dateCap, Calendar.DATE)));
		rcto.setMonth(new Integer(DateUtil.get(dateCap, Calendar.MONTH)+1));

		return rcto;
	}

}
