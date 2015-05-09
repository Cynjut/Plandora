package com.pandora.gui.struts.form;

import java.awt.image.BufferedImage;

import com.pandora.PreferenceTO;

/**
 * This class handle the data of Show All Tasks Form
 */
public class ShowAllTaskForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** Id of project related with task */
    private String projectRelated;
    
    /** Id of resource selected by combo. Value = -1 is All resources of project */    
    private String resourceSelected = "-1";
    
    /** Id of status selected by combo. Value = -2 is All status; Value = -1 is All status except Close and Canceled */
    private String statusSelected = "-1";
    
    private String htmlMap = null;
      
    private String showWorkflowDiagram = "off";
    private String instanceId;
    private String planningId;
    private BufferedImage workFlowDiagram;
    
    public void clear(){      
        this.showWorkflowDiagram = "off";
    }  
    
	public String getShowUpdateInBatch() {
    	String response = "off";
    	if (this.getShowIteration().equals("on") || this.getShowBillableTask().equals("on")) {
    		response = "on";
    	}
    	return response;
    }

    
    ////////////////////////////////////////////
    public String getShowIteration() {
        return super.getCurrentUser().getPreference().getPreference(PreferenceTO.HOME_TASKLIST_SW_TSK_ITERAT);
    }
    
    public String getShowBillableTask() {
        return super.getCurrentUser().getPreference().getPreference(PreferenceTO.LIST_ALL_TSK_BILLABLE);
    }
    
    ////////////////////////////////////////////    
    public String getProjectRelated() {
        return projectRelated;
    }
    public void setProjectRelated(String newValue) {
        this.projectRelated = newValue;
    }

    ////////////////////////////////////////////        
    public String getResourceSelected() {
        return resourceSelected;
    }
    public void setResourceSelected(String newValue) {
        this.resourceSelected = newValue;
    }
    
    ////////////////////////////////////////////        
    public String getStatusSelected() {
        return statusSelected;
    }
    public void setStatusSelected(String newValue) {
        this.statusSelected = newValue;
    }


	
    ////////////////////////////////////////////	
	public String getHtmlMap() {
		return htmlMap;
	}
	public void setHtmlMap(String newValue) {
		this.htmlMap = newValue;
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

	
    ////////////////////////////////////////////	
	public BufferedImage getWorkFlowDiagram() {
		return workFlowDiagram;
	}
	public void setWorkFlowDiagram(BufferedImage newValue) {
		this.workFlowDiagram = newValue;
	}
	
	
	
}
