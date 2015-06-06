package com.pandora.gui.struts.form;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.pandora.NotificationTO;
import com.pandora.helper.FormValidationUtil;
import com.pandora.helper.SessionUtil;

/**
 * 
 */
public class NotificationForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private String description;
    
    private String enableStatus;
    
    private String type;
    
    private String textQuery;
    
    private String retryNumber;
    
    private String fieldsHtml;
    
    private String fieldIdList;
    
    private String periodicity;
    
    private String hour;
    
    private String minute;
    
    private boolean hideClosedAgents;
    
    /**
     * Clear values of Notification Form
     */
    public void clear(){
        this.name = null;        
        this.type = null;
        this.description = null;
        this.enableStatus = null;
        this.textQuery = null;
        this.retryNumber = null;
        this.fieldsHtml = null;
        this.fieldIdList = null;
        this.periodicity = null;
        this.hour = null;
        this.minute = null;
        this.setSaveMethod(null, null);
    }    


    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	this.hideClosedAgents = false;
	}
    
    
    ///////////////////////////////////////////    
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    
    ///////////////////////////////////////////
    public String getEnableStatus() {
        return enableStatus;
    }
    public void setEnableStatus(String newValue) {
        this.enableStatus = newValue;
    }
    
    
    ///////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    ///////////////////////////////////////////    
    public String getRetryNumber() {
        return retryNumber;
    }
    public void setRetryNumber(String newValue) {
        this.retryNumber = newValue;
    }
    
    
    ///////////////////////////////////////////    
    public String getTextQuery() {
        return textQuery;
    }
    public void setTextQuery(String newValue) {
        this.textQuery = newValue;
    }
    
    
    ///////////////////////////////////////////    
    public String getType() {
        return type;
    }
    public void setType(String newValue) {
        this.type = newValue;
    }
    
    ///////////////////////////////////////////      
    public String getFieldsHtml() {
        return fieldsHtml;
    }
    public void setFieldsHtml(String newValue) {
        this.fieldsHtml = newValue;
    }
    
    ///////////////////////////////////////////         
    public String getFieldIdList() {
        return fieldIdList;
    }
    public void setFieldIdList(String newValue) {
        this.fieldIdList = newValue;
    }
    
    ///////////////////////////////////////////         
    public String getPeriodicity() {
        return periodicity;
    }
    public void setPeriodicity(String newValue) {
        this.periodicity = newValue;
    }
    
    ///////////////////////////////////////////       
    public String getHour() {
        return hour;
    }
    public void setHour(String newValue) {
        this.hour = newValue;
    }
    
    ///////////////////////////////////////////       
    public String getMinute() {
        return minute;
    }
    public void setMinute(String newValue) {
        this.minute = newValue;
    }
    
    
    //////////////////////////////////////////  
	public boolean getHideClosedAgents() {
		return hideClosedAgents;
	}
	public void setHideClosedAgents(boolean newValue) {
		this.hideClosedAgents = newValue;
	}
	
	
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		Locale loc = SessionUtil.getCurrentLocale(request);
		
		if (this.operation.equals("saveNotification")) {    

			MessageResources mr = this.getCurrentUser().getBundle();		    
		    if (this.periodicity.equals(NotificationTO.PERIODICITY_EVENTUALLY+"")) {
		        if (this.hour!=null && !this.hour.trim().equals("")) {
		            errors.add("Hour", new ActionError("validate.formNotification.eventHour") );    		            
		        }
		    } else {
			    if (this.hour==null || this.hour.trim().equals("")){
			        errors.add("Hour", new ActionError("validate.formNotification.blankHour") );
			    } else {
				    FormValidationUtil.checkInt(errors, mr.getMessage(loc, "label.formNotification.hour"), this.hour);
				    FormValidationUtil.checkMaxValue(errors, mr.getMessage(loc, "label.formNotification.hour"), this.hour, 23, loc);			    
			    }			    		        
		    }
		    
		    if (this.minute==null || this.minute.trim().equals("")){
		        errors.add("Minute", new ActionError("validate.formNotification.blankMinute") );
		    } else {
		        FormValidationUtil.checkInt(errors, mr.getMessage(loc, "label.formNotification.minute"), this.minute);
		        if (!this.periodicity.equals(NotificationTO.PERIODICITY_EVENTUALLY+"")) {
		            FormValidationUtil.checkMaxValue(errors, mr.getMessage(loc, "label.formNotification.minute"), this.minute, 59, loc);    
		        } else {
		            FormValidationUtil.checkMinValue(errors, mr.getMessage(loc, "label.formNotification.minute"), this.minute, 10, loc);
		        }
		    }
			
		    if (this.name==null || this.name.trim().equals("")){
		        errors.add("Name", new ActionError("validate.formNotification.blankName") );
		    }

		    if (this.retryNumber==null || this.retryNumber.trim().equals("")){
		        errors.add("Retry Number", new ActionError("validate.formNotification.blankRetry") );
		    } else {
		        FormValidationUtil.checkInt(errors, "Retry Number", this.retryNumber);	            		        
		    }
		    
		    if (this.textQuery==null || this.textQuery.trim().equals("")){
		        errors.add("SQL", new ActionError("validate.formNotification.blankSQL") );
		    }
		    
		    if (this.fieldIdList!=null && this.fieldIdList.trim().length()>0) {
		        String[] fields = this.fieldIdList.split("\\|");
		        for (int i = 0 ; i < fields.length; i++) {
		            String field = request.getParameter(fields[i]);
			        if (field==null || field.trim().length()==0) {
			            errors.add("Fields", new ActionError("validate.formNotification.blankField") );
			            break;
			        } 
		        }
		    }
		        
		}
		
		return errors;
	}
    
}
