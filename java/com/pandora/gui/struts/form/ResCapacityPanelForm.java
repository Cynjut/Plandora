package com.pandora.gui.struts.form;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.helper.FormValidationUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class ResCapacityPanelForm extends GeneralStrutsForm {

	public static final String MODE_ALL = "ALL";
	
	public static final String MODE_ONLY_CAP = "ONLY_CAP";
	
	
	private HashMap hmUserlist = new HashMap();
	
	private HashMap hmProjectlist = new HashMap();
	
	private HashMap hmProjectlistToWork = new HashMap();

	
	private static final long serialVersionUID = 1L;

	private String type = "PRJ";
	
	private String projectId;
	
	private String resourceId;
	
	private String capacityHtmlBody;
	
	private String capacityHtmlTitle;

	private String initialDate;
	
	private String finalDate;
	
	private boolean hideDisabledUsers;
	
	private String elementLabel;
	
	private int granularity = 14;
	
	private String unitCapacity = "1";
	
	private int maxLimit = 480;
		
	private String viewMode = MODE_ALL;
	
	private String showEditCapacity = "off";
	
	private String chartResourceId = null;
	
	private String chartProjectId = null;
	
	
	public String getCanShowChart(){
		return (chartResourceId!=null && chartProjectId!=null && 
				!chartResourceId.equals("") && !chartProjectId.equals("") ? "on":"off");
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.hideDisabledUsers = false;
	}
	
	
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (this.operation.equals("refresh")) {
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			Locale loc = SessionUtil.getCurrentLocale(request);
			String date1Lbl = uto.getBundle().getMessage(loc, "label.resCapacity.initialDate");
			String date2Lbl = uto.getBundle().getMessage(loc, "label.resCapacity.FinalDate");
			
			if (this.initialDate==null || this.initialDate.trim().equals("")){
				errors.add("[" + date1Lbl + "]", new ActionError("errors.required", "[" + date1Lbl + "]") );
			} else {
				if (!StringUtil.checkChars(initialDate, "0123456789/")) {
					errors.add("[" + date1Lbl + "]", new ActionError("validate.invalidDate", "[" + date1Lbl + "]") );
				}			
			}

			if (this.finalDate==null || this.finalDate.trim().equals("")){
				errors.add("[" + date2Lbl + "]", new ActionError("errors.required", "[" + date2Lbl + "]") );
			} else {
				if (!StringUtil.checkChars(finalDate, "0123456789/")) {
					errors.add("[" + date2Lbl + "]", new ActionError("validate.invalidDate", "[" + date2Lbl + "]") );
				}			
			}
		}
		
		return errors;
	}
	
	
	////////////////////////////////////////
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}

	
	////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	////////////////////////////////////////
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}
	
	
	////////////////////////////////////////
	public String getCapacityHtmlBody() {
		return capacityHtmlBody;
	}
	public void setCapacityHtmlBody(String newValue) {
		this.capacityHtmlBody = newValue;
	}
	
	
	////////////////////////////////////////	
	public String getCapacityHtmlTitle() {
		return capacityHtmlTitle;
	}
	public void setCapacityHtmlTitle(String newValue) {
		this.capacityHtmlTitle = newValue;
	}

	
	////////////////////////////////////////
	public String getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(String newValue) {
		this.initialDate = newValue;
	}
	
	
	////////////////////////////////////////
	public String getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(String newValue) {
		this.finalDate = newValue;
	}
	
	
	////////////////////////////////////////	
	public String getElementLabel() {
		return elementLabel;
	}
	public void setElementLabel(String newValue) {
		this.elementLabel = newValue;
	}
	
	
	////////////////////////////////////////
	public int getGranularity() {
		return granularity;
	}
	public void setGranularity(int newValue) {
		this.granularity = newValue;
	}
	
	////////////////////////////////////////	
	public String getViewMode() {
		return viewMode;
	}
	public void setViewMode(String newValue) {
		this.viewMode = newValue;
	}
	
	////////////////////////////////////////	
	public String getUnitCapacity() {
		return unitCapacity;
	}
	public void setUnitCapacity(String newValue) {
		this.unitCapacity = newValue;
	}
	
	////////////////////////////////////////		
	public int getMaxLimit() {
		return maxLimit;
	}
	public void setMaxLimit(int newValue) {
		this.maxLimit = newValue;
	}
	
	////////////////////////////////////////		
	public HashMap getHmUserlist() {
		return hmUserlist;
	}
	public void addHmUserlist(String key, UserTO newValue) {
		this.hmUserlist.put(key, newValue);
	}
	
		
	////////////////////////////////////////		
	public HashMap getHmProjectlist() {
		return hmProjectlist;
	}
	public void addHmProjectlist(String key, ProjectTO newValue) {
		this.hmProjectlist.put(key, newValue);
	}
	
	
	////////////////////////////////////////		
	public HashMap getHmProjectWorklist() {
		return hmProjectlistToWork;
	}
	public void addHmProjectWorklist(String key, ProjectTO newValue) {
		this.hmProjectlistToWork.put(key, newValue);
	}
	
	
	////////////////////////////////////////		
	public String getShowEditCapacity() {
		return showEditCapacity;
	}
	public void setShowEditCapacity(String newValue) {
		this.showEditCapacity = newValue;
	}
	
	
	////////////////////////////////////////		
	public String getChartResourceId() {
		return chartResourceId;
	}
	public void setChartResourceId(String newValue) {
		this.chartResourceId = newValue;
	}
	
	
	////////////////////////////////////////	
	public String getChartProjectId() {
		return chartProjectId;
	}
	public void setChartProjectId(String newValue) {
		this.chartProjectId = newValue;
	}
	
	
	////////////////////////////////////////		
	public boolean getHideDisabledUsers() {
		return hideDisabledUsers;
	}
	public void setHideDisabledUsers(boolean newValue) {
		this.hideDisabledUsers = newValue;
	}
	
	
}
