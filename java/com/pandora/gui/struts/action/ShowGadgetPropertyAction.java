package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.FieldValueTO;
import com.pandora.PreferenceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.gadget.Gadget;
import com.pandora.bus.gadget.GadgetBUS;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ShowGadgetPropertyForm;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

public class ShowGadgetPropertyAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showProperty";
		UserDelegate udel = new UserDelegate();
	
		try {
			String param = request.getParameter("gagid");
			if (param!=null && param.trim().length()>0) {
				UserTO uto = SessionUtil.getCurrentUser(request);				
				ShowGadgetPropertyForm frm = (ShowGadgetPropertyForm)form;
				
				UserTO root = udel.getRoot(); 
				String classList = root.getPreference().getPreference(PreferenceTO.GADGET_BUS_CLASS);
				Gadget gad = GadgetBUS.getGadgetClassByTokenizerString(classList, param, uto);
				
				String content = this.getHtmlPropertiesFields(gad, request, null);
				if (content!=null) {
					frm.setFieldsHtml(content);
				} else {
					frm.setFieldsHtml("");
				}
			}
		} catch(Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}

		return mapping.findForward(forward);		
	}

	
	public ActionForward saveForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = request.getParameter("forwardAfterSave");
		UserDelegate udel = new UserDelegate();
		PreferenceDelegate pdel = new PreferenceDelegate();
		ShowGadgetPropertyForm frm = (ShowGadgetPropertyForm)form;
		
		try {
			String param = frm.getGagid();
			if (param!=null && param.trim().length()>0) {		
				UserTO uto = SessionUtil.getCurrentUser(request);
				
				UserTO root = udel.getRoot(); 
				String classList = root.getPreference().getPreference(PreferenceTO.GADGET_BUS_CLASS);
				Gadget gad = GadgetBUS.getGadgetClassByTokenizerString(classList, param, uto);
				
				if (gad!=null) {
					Vector<FieldValueTO> fields = gad.getFields();
					if (fields!=null) {			
						PreferenceTO pref = uto.getPreference();
						
						Iterator<FieldValueTO> i = fields.iterator();
						while(i.hasNext()) {
							FieldValueTO field = i.next();
							String choose = request.getParameter(field.getId());
							if (choose!=null) {
								PreferenceTO newPto = new PreferenceTO(gad.getId() + "." + field.getId(), choose, uto);
								pref.addPreferences(newPto);								
							}
						}
						
						pdel.insertOrUpdate(pref);					
					}					
				}
			}
		} catch(Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(forward);		
	}

	
	public ActionForward maximize(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showGadgetMaximized";
		ShowGadgetPropertyForm frm = (ShowGadgetPropertyForm)form;
		try {
			frm.setShowGadgetCall("callGadget_" + frm.getGagid() + "('maximized_gadget', 670, 410);");
		} catch(Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(forward);		
	}
	
	
	public String getHtmlPropertiesFields(Gadget gad, HttpServletRequest request, 
			Vector<TransferObject> currentValues) throws BusinessException{
		String content = null;
		Vector<FieldValueTO> fields = null;
		String fieldListIds = "";

		UserTO uto = SessionUtil.getCurrentUser(request);
		String gadId = gad.getId();

		if (gad.canReloadFields()) {
			if (currentValues==null) {
				fields = gad.getFields();
				currentValues = new Vector<TransferObject>();
				Iterator<FieldValueTO> j = fields.iterator();
				while(j.hasNext()) {
					FieldValueTO f = j.next();
					String val = uto.getPreference().getPreference(gadId + "." + f.getId());
					currentValues.add(new TransferObject(f.getId(), val));
				}				
			}
			fields = gad.getFields(currentValues);
		} else {
			fields = gad.getFields();
		}

		if (fields!=null) {

			content = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> " +
		    		  "<tr class=\"pagingFormBody\">" +
		      		  "<td width=\"10\">&nbsp;</td>" +
		      		  "<td width=\"80\">&nbsp;</td>" + 
		      		  "<td>&nbsp;</td>" + 
		      		  "<td width=\"10\">&nbsp;</td>" + 
		      		  "</tr>\n";

			//concat ids of all fields..
			if (gad.canReloadFields()) {
				Iterator<FieldValueTO> j = fields.iterator();
				while(j.hasNext()) {
					FieldValueTO field = j.next();
					fieldListIds = fieldListIds + field.getId() + ";"; 
				}				
			}

			Iterator<FieldValueTO> i = fields.iterator();
			while(i.hasNext()) {
				FieldValueTO field = i.next();
				
				String selectedValue = "";
				if (currentValues==null) {
					selectedValue = uto.getPreference().getPreference(gadId + "." + field.getId());	
				} else {
					selectedValue = Gadget.getSelected(field.getId(), currentValues);
				}
				
				String objContent = "";
				if (field.getType().equals(FieldValueTO.FIELD_TYPE_COMBO)) {
					field.translateDomain(uto);
				}
				
				String jscript = null;
				if (gad.canReloadFields()) {
					jscript = "javascript:reloadFields('" + gad.getId() + "', '" + fieldListIds + "');";
				}
				
				objContent = HtmlUtil.getHtmlField(field, selectedValue, "showGadgetPropertyForm", null, null, jscript);	
				content = content + "<tr class=\"pagingFormBody\"> \n" +
			    		     "<td>&nbsp;</td>\n" +
			    		     "<td class=\"formTitle\">" + super.getBundleMessage(request, field.getLabel()) + ":&nbsp;</td>\n" +
			    		     "<td>" + objContent + "</td>\n" +
			    		     "<td>&nbsp;</td>\n" +
			    		     "</tr>\n";
			}
			
			content = content  + "</table>\n";
		}
		
		return content;
	}
	
}
