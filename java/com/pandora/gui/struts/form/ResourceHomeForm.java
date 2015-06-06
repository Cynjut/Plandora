package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * This class handle the data of Resource Home Form
 */
public class ResourceHomeForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** 'My Projects' grid identification */
    public static final String PRJ_SOURCE = "PRJ";
    
    /** 'My Tasks' grid identification */
    public static final String TSK_SOURCE = "TSK";
    
    /** 'My Requirements' grid identification */
    public static final String REQ_SOURCE = "REQ";

    /** 'My Team' grid identification */
    public static final String TOPIC_SOURCE = "TPC";
    
    
    /** 'My Requirements' grid identification by Refuse form */
    public static final String REFUSE_REQ_SOURCE = "REFUSE_REQ";

    /** Requirement ids identification into 'Pending Requirements' grid */
    public static final String ADJUST_REQ_SOURCE = "ADJUST_REQ";

    /** Requirement ids identification into 'Pending Requirements' grid */
    public static final String ADJUST_REQ_SOURCE_WOUT_PRJ = "ADJUST_REQ_WOUT_PRJ";

    public static final String RO_MODE_SOURCE = "RO_MODE";
    
    public static final String REF_REQ_SOURCE = "REF_REQ";
    
    
    /** show or hide the closed requests into grid */
    private String hideClosedRequests = "on";
    
    /** show or hide the closed tasks into grid */
    private String hideClosedTasks = "on";
    
    private String showWorkflowDiagram = "off";
    
    /** type of grid of form Project, Task, Requirement, etc */
    private String source = null;

    /** id of current user connected */
    private String resourceId;
    
    /** show or hide the pending requests list depending on current user role. <br> 
     * show = "on" / hide = any other value
     **/
    private String showPendingReq = null;
    
    private String showPendingCosts = null;
    
    private String taskPanelStyle;
    
    private String pendingPanelStyle;
    
    private String requestPanelStyle;
    
    private String projectPanelStyle;
    
    private String forumPanelStyle;
    
    private String panelId;
    
    private String gadgetHtmlBody;
    
    private String gagclass;
    
    private String showHideGadgetColumn;

    private String htmlEDIIcon;
    
    private String containGagdet;
    
    private String showHideGadgetLabel;
    
    private String maximizedGadgetId;
    
    private String planningId;
    
    private String shorcutsHtmlBody;
    
    private String expenseReportURL;
    
    private String topicComment;
    
    /**
     * Clear values of Form
     */
    public void clear(){
        this.hideClosedRequests = "on";
        this.hideClosedTasks = "on";
        this.source = null;
        this.panelId = null;
        this.gadgetHtmlBody = "";
        this.shorcutsHtmlBody = "";
        this.showHideGadgetColumn = "on";
        this.containGagdet = "off";
        this.showHideGadgetLabel = "";
        this.showWorkflowDiagram = "off";
        this.expenseReportURL = "";
    }

	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		ActionErrors errors = new ActionErrors();

		/* example
		if (this.operation.equals("saveUser")){
		    
			if (this.name==null || this.name.trim().equals("")){             	
			    errors.add("Nome", new ActionError("validate.blankName") );
			}
		}
		*/

		return errors;
	}

    
	public boolean getBoolCloseRequest(){
	    return (this.hideClosedRequests.equals("on")?true:false);
	}

	public boolean getBoolCloseTask(){
	    return (this.hideClosedTasks.equals("on")?true:false);
	}
	
	
    ////////////////////////////////////////////		
    public String getTaskPanelStyle() {
        return taskPanelStyle;
    }
    public void setTaskPanelStyle(String newValue) {
        this.taskPanelStyle = newValue;
    }
    
    
    ////////////////////////////////////////////	    
    public String getForumPanelStyle() {
        return forumPanelStyle;
    }
    public void setForumPanelStyle(String newValue) {
        this.forumPanelStyle = newValue;
    }
    
    
    ////////////////////////////////////////////	    
    public String getPendingPanelStyle() {
        return pendingPanelStyle;
    }
    public void setPendingPanelStyle(String newValue) {
        this.pendingPanelStyle = newValue;
    }
    
    
    ////////////////////////////////////////////	    
    public String getProjectPanelStyle() {
        return projectPanelStyle;
    }
    public void setProjectPanelStyle(String newValue) {
        this.projectPanelStyle = newValue;
    }
    
    
    ////////////////////////////////////////////	    
    public String getRequestPanelStyle() {
        return requestPanelStyle;
    }
    public void setRequestPanelStyle(String newValue) {
        this.requestPanelStyle = newValue;
    }
    
    
    ////////////////////////////////////////////	
    public String getSource() {
        return source;
    }
    public void setSource(String newValue) {
        this.source = newValue;
    }
   
    ////////////////////////////////////////////    
    public String getHideClosedRequests() {
        return hideClosedRequests;
    }
    public void setHideClosedRequests(String newValue) {
        this.hideClosedRequests = newValue;
    }
	
    ////////////////////////////////////////////	
    public String getShowPendingReq() {
        return showPendingReq;
    }
    public void setShowPendingReq(String newValue) {
        this.showPendingReq = newValue;
    }

    
    ////////////////////////////////////////////	   
    public String getShowPendingCosts() {
		return showPendingCosts;
	}
	public void setShowPendingCosts(String newValue) {
		this.showPendingCosts = newValue;
	}

	
	////////////////////////////////////////////	    
    public String getHideClosedTasks() {
        return hideClosedTasks;
    }
    public void setHideClosedTasks(String newValue) {
        this.hideClosedTasks = newValue;
    }
    
    
    ////////////////////////////////////////////
    public String getGadgetHtmlBody() {
		return gadgetHtmlBody;
	}
	public void setGadgetHtmlBody(String newValue) {
		this.gadgetHtmlBody = newValue;
	}


    ////////////////////////////////////////////	
	public String getShorcutsHtmlBody() {
		return shorcutsHtmlBody;
	}
	public void setShorcutsHtmlBody(String newValue) {
		this.shorcutsHtmlBody = newValue;
	}
	

	////////////////////////////////////////////    
    public String getResourceId() {
        return resourceId;
    }
    public void setResourceId(String newValue) {
        this.resourceId = newValue;
    }    
    

    ////////////////////////////////////////////    
	public String getPanelId() {
		return panelId;
	}
	public void setPanelId(String newValue) {
		this.panelId = newValue;
	}

	
    ////////////////////////////////////////////
	public String getShowHideGadgetColumn() {
		return showHideGadgetColumn;
	}
	public void setShowHideGadgetColumn(String newValue) {
		this.showHideGadgetColumn = newValue;
	}

    ////////////////////////////////////////////
	public String getContainGagdet() {
		return containGagdet;
	}
	public void setContainGagdet(String newValue) {
		this.containGagdet = newValue;
	}
	
	////////////////////////////////////////////	
	public String getShowHideGadgetLabel() {
		return showHideGadgetLabel;
	}

	public void setShowHideGadgetLabel(String newValue) {
		this.showHideGadgetLabel = newValue;
	}

	
	////////////////////////////////////////////
	public String getGagclass() {
		return gagclass;
	}
	public void setGagclass(String newValue) {
		this.gagclass = newValue;
	}

	
	////////////////////////////////////////////
	public String getMaximizedGadgetId() {
		return maximizedGadgetId;
	}
	public void setMaximizedGadgetId(String maximizedGadgetId) {
		this.maximizedGadgetId = maximizedGadgetId;
	}


    ////////////////////////////////////////////		
	public String getShowWorkflowDiagram() {
		return showWorkflowDiagram;
	}
	public void setShowWorkflowDiagram(String newValue) {
		this.showWorkflowDiagram = newValue;
	}

	
    ////////////////////////////////////////////	
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}
	
	
    ////////////////////////////////////////////
	public String getExpenseReportURL() {
		return expenseReportURL;
	}
	public void setExpenseReportURL(String newValue) {
		this.expenseReportURL = newValue;
	}


    ////////////////////////////////////////////
	public String getTopicComment() {
		return topicComment;
	}
	public void setTopicComment(String newValue) {
		this.topicComment = newValue;
	}

	
    ////////////////////////////////////////////
	public String getHtmlEDIIcon() {
		return htmlEDIIcon;
	}
	public void setHtmlEDIIcon(String newValue) {
		this.htmlEDIIcon = newValue;
	}
	
	
	
}
