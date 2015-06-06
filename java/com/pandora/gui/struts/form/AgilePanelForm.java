package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class AgilePanelForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String groupBy = "1";
	
	private String projectId;

	private String projectName;
	
	private String iterationSelected = "-1";
	
	private String categorySelected = "-1";
	
	private String reqId;
	
	private boolean hideFinishedReq = false;
	
	private boolean hideCancelTasks = false;
			
	private boolean hideTasksWithoutReq = false;
	
	private boolean hideOldIterations = false;
	
	private boolean showChart = false;
	
	private String gadgetHtmlBody;
	
	private String gadgetIconBody;
	
	private String maximizedGadgetId;
	
	private String taskId;
	
	private String resourceId;
	
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.hideFinishedReq = false;
		this.hideCancelTasks = false;
		this.hideTasksWithoutReq = false;
		this.hideOldIterations = false;
		this.showChart = false;
	}
	
	
	//////////////////////////////////////////	
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String newValue) {
		this.groupBy = newValue;
	}


	//////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	//////////////////////////////////////////
	public String getIterationSelected() {
		return iterationSelected;
	}
	public void setIterationSelected(String newValue) {
		this.iterationSelected = newValue;
	}


	//////////////////////////////////////////
	public String getCategorySelected() {
		return categorySelected;
	}
	public void setCategorySelected(String newValue) {
		this.categorySelected = newValue;
	}


	//////////////////////////////////////////
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String newValue) {
		this.projectName = newValue;
	}


	//////////////////////////////////////////
	public boolean getHideFinishedReq() {
		return hideFinishedReq;
	}
	public void setHideFinishedReq(boolean newValue) {
		this.hideFinishedReq = newValue;
	}


	//////////////////////////////////////////
	public boolean getHideCancelTasks() {
		return hideCancelTasks;
	}
	public void setHideCancelTasks(boolean newValue) {
		this.hideCancelTasks = newValue;
	}

	
	//////////////////////////////////////////	
	public boolean getHideTasksWithoutReq() {
		return hideTasksWithoutReq;
	}
	public void setHideTasksWithoutReq(boolean newValue) {
		this.hideTasksWithoutReq = newValue;
	}

	
	//////////////////////////////////////////	
	public boolean getShowChart() {
		return showChart;
	}
	public void setShowChart(boolean newValue) {
		this.showChart = newValue;
	}


	//////////////////////////////////////////	
	public boolean getHideOldIterations() {
		return hideOldIterations;
	}
	public void setHideOldIterations(boolean newValue) {
		this.hideOldIterations = newValue;
	}


	//////////////////////////////////////////
	public String getReqId() {
		return reqId;
	}
	public void setReqId(String newValue) {
		this.reqId = newValue;
	}
	
	
	//////////////////////////////////////////	
    public String getGadgetHtmlBody() {
        return gadgetHtmlBody;
    }
    public void setGadgetHtmlBody(String newValue) {
        this.gadgetHtmlBody = newValue;
    }
    
    
	//////////////////////////////////////////	    
    public String getGadgetIconBody() {
        return gadgetIconBody;
    }
    public void setGadgetIconBody(String newValue) {
        this.gadgetIconBody = newValue;
    }

    
	//////////////////////////////////////////	
	public String getMaximizedGadgetId() {
		return maximizedGadgetId;
	}
	public void setMaximizedGadgetId(String newValue) {
		this.maximizedGadgetId = newValue;
	}

	
	//////////////////////////////////////////
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String newValue) {
		this.taskId = newValue;
	}


	//////////////////////////////////////////
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}
	
    
}
