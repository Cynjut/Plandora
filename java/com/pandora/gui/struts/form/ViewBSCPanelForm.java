package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class ViewBSCPanelForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

    /** Project related with KPI */
    private String projectId; 
	
	private String bscTable;

	private String refDate;
	
	private String categoryId;
	
	private String gadgetHtmlBody;
	
	private String gadgetPropertyBody;
	
	private String kpiId;
	
	private boolean showOnlyCurrentProject;
	
	private boolean showOnlyOpenedKpi;
	
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.showOnlyCurrentProject = false;
		this.showOnlyOpenedKpi = false;
	}


	//////////////////////////////////////////
	public boolean getShowOnlyCurrentProject() {
		return showOnlyCurrentProject;
	}
	public void setShowOnlyCurrentProject(boolean newValue) {
		this.showOnlyCurrentProject = newValue;
	}
	
    //////////////////////////////////////////////
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
	
	//////////////////////////////////////////
	public String getBscTable() {
		return bscTable;
	}
	public void setBscTable(String newValue) {
		this.bscTable = newValue;
	}

	
	//////////////////////////////////////////	
	public String getRefDate() {
		return refDate;
	}
	public void setRefDate(String newValue) {
		this.refDate = newValue;
	}

	
	//////////////////////////////////////////
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String newValue) {
		this.categoryId = newValue;
	}
		
	
	//////////////////////////////////////////	
    public String getGadgetHtmlBody() {
        return gadgetHtmlBody;
    }
    public void setGadgetHtmlBody(String newValue) {
        this.gadgetHtmlBody = newValue;
    }
    
    
	//////////////////////////////////////////	    
    public String getGadgetPropertyBody() {
        return gadgetPropertyBody;
    }
    public void setGadgetPropertyBody(String newValue) {
        this.gadgetPropertyBody = newValue;
    }

    
    //////////////////////////////////////////
    public String getKpiId() {
        return kpiId;
    }
    public void setKpiId(String newValue) {
        this.kpiId = newValue;
    }

    
    //////////////////////////////////////////
	public boolean getShowOnlyOpenedKpi() {
		return showOnlyOpenedKpi;
	}
	public void setShowOnlyOpenedKpi(boolean newValue) {
		this.showOnlyOpenedKpi = newValue;
	}
    
    
}
