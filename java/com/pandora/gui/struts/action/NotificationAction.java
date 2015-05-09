package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.FieldValueTO;
import com.pandora.NotificationFieldTO;
import com.pandora.NotificationTO;
import com.pandora.PreferenceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.alert.Notification;
import com.pandora.delegate.NotificationDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.AreaForm;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.NotificationForm;
import com.pandora.gui.struts.form.ResTaskForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;

/**
 * 
 */
public class NotificationAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showNotification";
		
		this.clearForm(form, request);
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		NotificationForm nfrm = (NotificationForm)form;
		nfrm.setSaveMethod(ResTaskForm.INSERT_METHOD, uto);
			
	    this.refresh(mapping, form, request, response);
	    this.refreshAuxiliarList(mapping, form, request, response);
	    
		return mapping.findForward(forward);		
	}
	
	
	public ActionForward refreshType(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showNotification";
	    this.refreshChannelFields(request, form, null);	    
		return mapping.findForward(forward);	    
	}


	public ActionForward saveNotification(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showNotification";
		String errorMsg = "error.formNotification.showForm";
		String succeMsg = "message.success";
		
		try {
		    NotificationForm nfrm = (NotificationForm)form;
		    NotificationDelegate ndel = new NotificationDelegate();

		    NotificationTO nto = this.getTransferObjectFromActionForm(nfrm, request);

		    //refresh attributes that is not into form
		    NotificationTO dummy = ndel.getNotificationObject(nto);
		    if (dummy!=null) {
			    nto.setLastCheck(dummy.getLastCheck());
			    nto.setNextNotification(dummy.getNextNotification());		        
		    }
			
		    //check if notification channel is valid
		    Notification nbus = getNotificationClass(nfrm.getType());
		    if (nbus!=null) {
				if (nfrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){
				    errorMsg = "error.formNotification.insert";
				    succeMsg = "message.insertNotification";
				    ndel.insertNotification(nto);
				    this.clearForm(form, request);
				} else {
				    errorMsg = "error.formNotification.update";
				    succeMsg = "message.updateNotification";
				    ndel.updateNotification(nto);
				}
				
				//set success message into http session
				this.setSuccessFormSession(request, succeMsg);
				
				//refresh lists on form...
				this.refresh(mapping, form, request, response );

			    //set the current user connected
				UserTO uto = SessionUtil.getCurrentUser(request);
			    nfrm.setSaveMethod(ResTaskForm.UPDATE_METHOD, uto);
		        
		    } else {
		        this.setErrorFormSession(request, "error.formNotification.channel.invalid", null);
		    }

			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}	    
		return mapping.findForward(forward);	    
	}

	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showNotification";

		try {	    
		    //get all Notification from data base 		    
		    NotificationDelegate ndel = new NotificationDelegate();
		    Vector nList = ndel.getNotificationList();
		    request.getSession().setAttribute("notificationList", nList);
		    		    		    
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.formNotification.showForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}

	
    private void refreshChannelFields(HttpServletRequest request, ActionForm form, NotificationTO nto) {
		try {
		    StringBuffer html = new StringBuffer();
		    String fieldIdList = "";
		    NotificationForm nfrm = (NotificationForm)form;
	    	String gap = "", helpContent = "";
	    	
		    Notification nbus = getNotificationClass(nfrm.getType());
		    if (nbus!=null && (nbus.getFieldKeys()!=null || nbus.getFields()!=null )) {
		    	
		    	if (nbus.getFieldKeys()!=null) {
			        fieldIdList = this.renderFieldsByFlatList(request, nto, html, nbus);
		    	} else if (nbus.getFields()!=null) {
				    if (nto==null) {
				        nto = this.getTransferObjectFromActionForm(nfrm, request);    
				    }
		    		fieldIdList = this.renderFieldsByFieldsList(request, nto, html, nbus);
		    	} else {
		    		fieldIdList = "";
		    	}
		        
		    	if (html.length()>0) {
		    	    helpContent = "<tr class=\"formNotes\"><td>&nbsp;</td><td>&nbsp;</td><td colspan=\"2\">" + 
		    	    		this.getBundleMessage(request, nbus.getContextHelp(), true) + "</td></tr>";
		    	    gap = "<tr class=\"gapFormBody\"><td colspan=\"4\">&nbsp;</td></tr>";
		    	}
		    	
		    } else {
		        this.setErrorFormSession(request, "error.formNotification.channel.invalid", null); 
		    }


		    nfrm.setFieldsHtml(gap + html.toString() + helpContent + gap);
		    nfrm.setFieldIdList(fieldIdList);
		    
		} catch(Exception e){
			   this.setErrorFormSession(request, "error.formNotification.showForm", e);
		}		    
    }

    
	private String renderFieldsByFieldsList(HttpServletRequest request, NotificationTO nto,
			StringBuffer html, Notification nbus) {
		
		String fieldIdList = "";
    	Locale loc = SessionUtil.getCurrentLocale(request);
    	String mask = super.getCalendarMask(request);
    	
        Iterator i = nbus.getFields().iterator();
        while(i.hasNext()) {
            FieldValueTO field = (FieldValueTO)i.next();		            
            String fieldLabel = this.getBundleMessage(request, field.getLabel(), true);

            if (field.getDomain()!=null) {
                field.setDomain(super.applyBundle(request, field.getDomain()));
            }

            String fieldValue = "";
            if (nbus.getFields()!=null) {
                fieldValue = nto.getFieldValueByKey(field.getId(), nbus, mask, loc);
            }			        
	        html.append("<tr class=\"pagingFormBody\"><td width=\"10\">&nbsp;</td>");
	        html.append("<td width=\"150\" class=\"formTitle\">" + fieldLabel + ":&nbsp;</td>");
	        html.append("<td class=\"formBody\">" + this.getHtmlField(field, fieldValue, request));
	        html.append("</td><td width=\"10\">&nbsp;</td></tr>\n");
	        
	        if (fieldIdList.trim().length()>0) {
	            fieldIdList = fieldIdList + "|";
	        }
	        fieldIdList = fieldIdList + field.getId();			        
        }
        
        return fieldIdList;
	}
    
    
	private String renderFieldsByFlatList(HttpServletRequest request,
			NotificationTO nto, StringBuffer html, Notification nbus) {
		String fieldIdList = "";
		
		for (int i = 0; i < nbus.getFieldKeys().size(); i++) {
		    String fieldKey = (String)nbus.getFieldKeys().elementAt(i);
		    String fieldLabel = "<Err>";
		    String fieldValue = "";
		    String fieldType = "";
		    if (nbus.getFieldLabels().size()>=i) {
		        fieldLabel = (String)nbus.getFieldLabels().elementAt(i);
		        fieldLabel = this.getBundleMessage(request, fieldLabel, true);
		        fieldType = (String)nbus.getFieldTypes().elementAt(i);
		    }
		    if (nto!=null) {
		        fieldValue = nto.getFieldValueByKey(fieldKey);
		    }			        
		    html.append("<tr class=\"pagingFormBody\"><td width=\"10\">&nbsp;</td>");
		    html.append("<td width=\"150\" class=\"formTitle\">" + fieldLabel + ":&nbsp;</td>");
		    html.append("<td class=\"formBody\">" + getHtmlField(fieldType, fieldKey, fieldValue, request));
		    html.append("</td><td width=\"10\">&nbsp;</td></tr>\n");
		    
		    if (fieldIdList.trim().length()>0) {
		        fieldIdList = fieldIdList + "|";
		    }
		    fieldIdList = fieldIdList + fieldKey;			        
		}
		return fieldIdList;
	}
    
	
    private String getHtmlField(FieldValueTO field, String fieldValue, HttpServletRequest request){
        String[] labelsForCalendar = new String[2];
        labelsForCalendar[0] = this.getBundleMessage(request, "label.calendar.button");
        labelsForCalendar[1] = this.getBundleMessage(request, "calendar.format");
        return HtmlUtil.getHtmlField(field, fieldValue, "notificationForm", null, labelsForCalendar);
    }
    
    
    private String getHtmlField(String fieldType, String fieldKey, String fieldValue, HttpServletRequest request){
        String response = "";
        
        if (fieldType.equals("2")) {
            String fields = "OK|" + this.getBundleMessage(request, "label.yes") + 
                          "|NOK|" + this.getBundleMessage(request, "label.no");
            response = HtmlUtil.getComboBox(fieldKey, fields, fieldValue);
        } else if (fieldType.equals("3")) {
            response = HtmlUtil.getTextBox(fieldKey, fieldValue, false, null, 100, 40, "password");
        } else {
            response = HtmlUtil.getTextBox(fieldKey, fieldValue, 100, 40);
        }
        
        return response;
    }

	public void refreshAuxiliarList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    try {
		    request.getSession().setAttribute("typeList", this.getTypeList(request, form));
        } catch (BusinessException e) {
            request.getSession().setAttribute("typeList", new Vector<TransferObject>());           
        }
	    this.refreshType(mapping, form, request, response);
	    
	    request.getSession().setAttribute("enableList", this.getEnableList(request));
	    		   
	    request.getSession().setAttribute("periodicityList", this.getPeriodicityList(request));
	}
	
    public ActionForward editNotification(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showNotification";
	    NotificationDelegate ndel = new NotificationDelegate();
		
		try {
		    NotificationForm nfrm = (NotificationForm)form;
		    NotificationTO nto = ndel.getNotificationObject(new NotificationTO(nfrm.getId()));
		    
		    this.getActionFormFromTransferObject(nto, nfrm, request);
		    
			//set current operation status for Updating	
		    nfrm.setSaveMethod(AreaForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formNotification.showForm", e);
		}
		
		return mapping.findForward(forward);				
	}


	public ActionForward removeNotification(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showNotification";
		try {
		    NotificationForm nfrm = (NotificationForm)form;
		    NotificationDelegate ndel = new NotificationDelegate();
			
			//clear form and messages
			this.clearForm(nfrm, request);
			
			NotificationTO nto = new NotificationTO();
			nto.setId(nfrm.getId());
			ndel.removeNotification(nto);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeNotification");
			this.refresh(mapping, form, request, response );
		
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formNotification.remove", e);
		}
	    
		return mapping.findForward(forward);	    
	}

	
	/**
	 * Clear all values of current form.
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showNotification";		
		this.prepareForm(mapping, form, request, response);
		return mapping.findForward(forward);		
	}
	
	
	private void clearForm(ActionForm form, HttpServletRequest request){
	    NotificationForm nfrm = (NotificationForm)form;		
	    nfrm.clear();
	    this.clearMessages(request);
	}
	
	private Vector<TransferObject> getPeriodicityList(HttpServletRequest request){
	    Vector<TransferObject> response = new Vector<TransferObject>();

	    TransferObject option1 = new TransferObject();
	    option1.setId(NotificationTO.PERIODICITY_DAILY.toString());
	    option1.setGenericTag(this.getBundleMessage(request, "periodicity.formNotification.daily"));
	    response.addElement(option1);

	    TransferObject option2 = new TransferObject();
	    option2.setId(NotificationTO.PERIODICITY_WEEKLY.toString());
	    option2.setGenericTag(this.getBundleMessage(request, "periodicity.formNotification.weekly"));
	    response.addElement(option2);

	    TransferObject option3 = new TransferObject();
	    option3.setId(NotificationTO.PERIODICITY_MONTHLY.toString());
	    option3.setGenericTag(this.getBundleMessage(request, "periodicity.formNotification.monthly"));
	    response.addElement(option3);

	    TransferObject option4 = new TransferObject();
	    option4.setId(NotificationTO.PERIODICITY_YEARLY.toString());
	    option4.setGenericTag(this.getBundleMessage(request, "periodicity.formNotification.yearly"));
	    response.addElement(option4);

	    TransferObject option5 = new TransferObject();
	    option5.setId(NotificationTO.PERIODICITY_EVENTUALLY.toString());
	    option5.setGenericTag(this.getBundleMessage(request, "periodicity.formNotification.eventualy"));
	    response.addElement(option5);
	    
	    return response;
	}
	
	
	private Vector<TransferObject> getTypeList(HttpServletRequest request, ActionForm form) throws BusinessException{
	    Vector<TransferObject> response = new Vector<TransferObject>();	    
	    UserDelegate udel = new UserDelegate();
	    UserTO uto = udel.getRoot();
	    String notifClasses = uto.getPreference().getPreference(PreferenceTO.NOTIFICATION_BUS_CLASS);
	    NotificationForm nfrm = (NotificationForm)form;
	    
	    if (notifClasses!=null) {
	        String[] classList = notifClasses.split(";");
	        if (classList!=null && classList.length>0) {
	            for (int i = 0; i<classList.length; i++) {
	                String classStr = classList[i].trim();
	                Notification nbus = getNotificationClass(classStr);
                    if (nbus!=null) {
                        if (response.isEmpty()) {
                            nfrm.setType(classStr);
                        }
                        response.add(new TransferObject(classStr, nbus.getUniqueName()));
                    } else {
                        response.add(new TransferObject("-1", classStr + "-ERR!"));
                    }
	            }
	        }
	    }
	    return response;
	}
	
	private Notification getNotificationClass(String className){
        Notification response = null;
        try {
            Class klass = Class.forName(className);
            response = (Notification)klass.newInstance();
        } catch (java.lang.NoClassDefFoundError e) {
        	LogUtil.log(this, LogUtil.LOG_ERROR, "A error occures getting notification class", e);
            response = null;
            
        } catch (Exception e) {
        	LogUtil.log(this, LogUtil.LOG_ERROR, "A error occures getting notification class", e);
            response = null;
        }
        return response;
	}
	
	
	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 */
	private void getActionFormFromTransferObject(NotificationTO to, NotificationForm frm, HttpServletRequest request){
	    frm.setId(to.getId());
	    frm.setName(to.getName());
	    frm.setDescription(to.getDescription());
	    frm.setEnableStatus(to.getFinalDate()==null?"1":"0");
	    if (to.getPeriodicityHour()!=null) {
	        frm.setHour(to.getPeriodicityHour()+"");    
	    } else {
	        frm.setHour("");
	    }
	    if (to.getPeriodicityMinute()!=null) {
	        frm.setMinute(to.getPeriodicityMinute()+"");    
	    } else {
	        frm.setMinute("");
	    }
	    
	    frm.setPeriodicity(to.getPeriodicity()+"");
	    frm.setRetryNumber(to.getRetryNumber()+"");
	    frm.setTextQuery(to.getSqlStement());
	    frm.setType(to.getNotificationClass());
	    
	    this.refreshChannelFields(request, frm, to);
	}
	
	private NotificationTO getTransferObjectFromActionForm(NotificationForm frm, HttpServletRequest request){
	    NotificationTO nto = new NotificationTO();
	    nto.setId(frm.getId());
	    nto.setName(frm.getName());
	    nto.setDescription(frm.getDescription());
	    if (frm.getEnableStatus().equals("1")) {
	        nto.setFinalDate(null);    
	    } else {
	        nto.setFinalDate(DateUtil.getNow());
	    }
	    if (frm.getHour()!=null && frm.getHour().trim().length()>0) {
	        nto.setPeriodicityHour(new Integer(frm.getHour()));    
	    } else {
	        nto.setPeriodicityHour(null);
	    }
	    if (frm.getMinute()!=null && frm.getMinute().trim().length()>0) {
	        nto.setPeriodicityMinute(new Integer(frm.getMinute()));    
	    } else {
	        nto.setPeriodicityMinute(null);
	    }	    
	    
	    if (frm.getPeriodicity()!=null && frm.getPeriodicity().trim().length()>0) {
	        nto.setPeriodicity(new Integer(frm.getPeriodicity()));    
	    } else {
	        nto.setPeriodicity(null);
	    }	    
	    
	    if (frm.getRetryNumber()!=null && frm.getRetryNumber().trim().length()>0) {
	        nto.setRetryNumber(new Integer(frm.getRetryNumber()));    
	    } else {
	        nto.setRetryNumber(null);
	    }	    
	    
	    nto.setSqlStement(frm.getTextQuery());
	    nto.setNotificationClass(frm.getType());
	    	     
	    //get the dynamic fields and set into the notification entity
	    String fieldStr = frm.getFieldIdList();
	    if (fieldStr!=null) {
		    String[]fields = fieldStr.split("\\|");
		    if (fields!=null && fields.length>0) {
			    for(int i = 0; i< fields.length; i++) {
			        String fieldValue = request.getParameter(fields[i]);
			        if (fieldValue!=null) {
				        NotificationFieldTO nfto = new NotificationFieldTO();
				        nfto.setId(frm.getId());
				        nfto.setName(fields[i]);
				        nfto.setValue(fieldValue);
				        nto.addField(nfto);			            
			        }
			    }	        		        
		    }
	    }
	    
	    return nto;
	}
	
}
