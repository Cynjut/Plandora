package com.pandora.gui.struts.form;

import java.sql.Timestamp;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.ProjectTO;

/**
 */
public class OccurrenceForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    private String source;
    
    private String sourceName;
    
    private ProjectTO project;
    
    private Vector fields;
    
    private String name;
    
    private String visible;
    
    private String fieldsHtml;
    
    private String statusComboHtml;
    
    private String projectId;
    
    private String projectName;
    
    private String fieldIdList;
    
    private String status;
    
    private Timestamp creationDate;
    
    private String selectedKpi;
    
    private String kpiWeight;
    
    private String kpiListHtml;
    
    private boolean hideClosedOccurrences;
    

    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	this.hideClosedOccurrences = false;
    }
    
    
    /**
     * Clear values of Form
     */
    public void clear(){
        this.source = null;   
        this.sourceName = null;
        this.project = null;
        this.fields = null;
        this.name = null;
        this.visible = "0";
        this.fieldsHtml = null;
        this.statusComboHtml = null;
        this.fieldIdList = null;
        this.status = null;
        this.projectName = null;
        this.setSaveMethod(null, null);
        this.id = null;
        this.kpiWeight = "1";
    }  
    
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (this.operation.equals("saveOccurrence")) {

			if (this.name==null || this.name.trim().equals("")){
		        errors.add("Name", new ActionError("validate.occurrence.blankName") );
		    }
		}
		
		return errors;
	}

	
	/////////////////////////////////////    
    public boolean getHideClosedOccurrences() {
		return hideClosedOccurrences;
	}
	public void setHideClosedOccurrences(boolean newValue) {
		this.hideClosedOccurrences = newValue;
	}


	/////////////////////////////////////
    public Vector getFields() {
        return fields;
    }
    public void setFields(Vector newValue) {
        this.fields = newValue;
    }
    
    
    /////////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }
    
    
    /////////////////////////////////////    
    public String getSource() {
        return source;
    }
    public void setSource(String newValue) {
        this.source = newValue;
    }
    
    
    /////////////////////////////////////    
    public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String newValue) {
		this.sourceName = newValue;
	}


	/////////////////////////////////////        
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    /////////////////////////////////////     
    public String getVisible() {
		return visible;
	}
	public void setVisible(String newValue) {
		this.visible = newValue;
	}


	/////////////////////////////////////     
    public String getFieldsHtml() {
        return fieldsHtml;
    }
    public void setFieldsHtml(String newValue) {
        this.fieldsHtml = newValue;
    }
    
    
    /////////////////////////////////////        
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }

    
    /////////////////////////////////////          
    public String getFieldIdList() {
        return fieldIdList;
    }
    public void setFieldIdList(String newValue) {
        this.fieldIdList = newValue;
    }
    
    /////////////////////////////////////       
    public String getStatusComboHtml() {
        return statusComboHtml;
    }
    public void setStatusComboHtml(String newValue) {
        this.statusComboHtml = newValue;
    }
    
    /////////////////////////////////////         
    public String getStatus() {
        return status;
    }
    public void setStatus(String newValue) {
        this.status = newValue;
    }
    
    ////////////////////////////////////////////    
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }

    ////////////////////////////////////////////    
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String newValue) {
		this.projectName = newValue;
	}

    ////////////////////////////////////////////  
	public String getSelectedKpi() {
		return selectedKpi;
	}
	public void setSelectedKpi(String newValue) {
		this.selectedKpi = newValue;
	}

    ////////////////////////////////////////////  	
    public String getKpiWeight() {
		return kpiWeight;
	}
	public void setKpiWeight(String newValue) {
		this.kpiWeight = newValue;
	}


	////////////////////////////////////////////
	public String getKpiListHtml() {
		return kpiListHtml;
	}
	public void setKpiListHtml(String newValue) {
		this.kpiListHtml = newValue;
	}
}
