package com.pandora.gui.struts.form;

import com.pandora.PreferenceTO;

/**
 * This class handle the data of Show All Request Form
 */
public class ShowAllRequirementForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** Id of project related with requirement */
    private String projectRelated;

    /** Id of customer requester selected by combo. Value -1 = All requesters */    
    private String requesterSelected = "-1";
    
    /** Id of status selected by combo. Value -2 = All status; Value -1 = All status except Close and Canceled */
    private String statusSelected = "-1";

    /** View mode selected by combo. Value -1 = List Mode */
    private String viewModeSelected = "-1";
 
    
    private String showWorkflowDiagram = "off";
    private String instanceId;
    private String planningId;
    
    
    public void clear(){      
        this.showWorkflowDiagram = "off";
    }  
    
    
    ////////////////////////////////////////////    
    public String getProjectRelated() {
        return projectRelated;
    }
    public void setProjectRelated(String newValue) {
        this.projectRelated = newValue;
    }


    ////////////////////////////////////////////        
    public String getRequesterSelected() {
        return requesterSelected;
    }
    public void setRequesterSelected(String newValue) {
        this.requesterSelected = newValue;
    }

    
    ////////////////////////////////////////////        
    public String getStatusSelected() {
        return statusSelected;
    }
    public void setStatusSelected(String newValue) {
        this.statusSelected = newValue;
    }
    
    
    ////////////////////////////////////////////
    public String getViewModeSelected() {
		return viewModeSelected;
	}
	public void setViewModeSelected(String newValue) {
		this.viewModeSelected = newValue;
	}


    ////////////////////////////////////////////
    public String getShowPriority() {
    	return super.getCurrentUser().getPreference().getPreference(PreferenceTO.LIST_ALL_REQ_SW_PRIORITY);
    }

    ////////////////////////////////////////////
    public String getShowIteration() {
        return super.getCurrentUser().getPreference().getPreference(PreferenceTO.LIST_ALL_REQ_SW_ITERATION);
    }
    
    ////////////////////////////////////////////
    public String getShowParentReq() {
        return super.getCurrentUser().getPreference().getPreference(PreferenceTO.LIST_ALL_REQ_SW_PARENT_REQ);
    }
    
    
    ////////////////////////////////////////////		
	public String getShowWorkflowDiagram() {
		return showWorkflowDiagram;
	}
	public void setShowWorkflowDiagram(String newValue) {
		this.showWorkflowDiagram = newValue;
	}

    ////////////////////////////////////////////			
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String newValue) {
		this.instanceId = newValue;
	}

	
    ////////////////////////////////////////////			
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}
	
}
