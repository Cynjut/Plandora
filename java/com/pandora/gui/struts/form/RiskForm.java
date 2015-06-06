package com.pandora.gui.struts.form;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.ProjectTO;

/**
 */
public class RiskForm extends GeneralStrutsForm {
    
	private static final long serialVersionUID = 1L;
	
    private ProjectTO project;
        
    private String name;
    
    private String description;
    
    private String responsible;
        
    private String projectId;
    
    private String status;
    
    private Timestamp creationDate;

    private String categoryId;

    private String probability;
    
    private String impact;
    
    private String tendency;
    
    private String strategy;
    
    private String contingency;
    
    private String showIssueConfirmation = "off";
    
    private String projectName;
    
    private boolean costImpact;
    
    private boolean qualityImpact;
    
    private boolean scopeImpact;
    
    private boolean timeImpact;
    
    private String riskType;
    
    private String visible;
    
    private String riskComment;    
    
    /**
     * Clear values of Form
     */
    public void clear(){      
        this.project = null;
        this.name = null;
        this.status = null;
        this.categoryId = "";
        this.description = null;   
        this.responsible = null;
        this.probability = null;
        this.tendency = null;
        this.strategy = null;
        this.contingency = null;
        this.showIssueConfirmation = "off";
        this.projectName = null;
        this.setSaveMethod(null, null);
        this.costImpact = false;
        this.qualityImpact = false;
        this.scopeImpact = false;
        this.timeImpact = false;
        this.visible = "0";
        this.riskComment = "";
    }  
    
    
    
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.costImpact = false;
        this.qualityImpact = false;
        this.scopeImpact = false;
        this.timeImpact = false;
	}



	/////////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }
    
    
   
    
    /////////////////////////////////////        
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    
    /////////////////////////////////////        
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
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
    
    ///////////////////////////////////////////////////       
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }    

    ///////////////////////////////////////////////////         
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    
    ///////////////////////////////////////////////////         
    public String getProbability() {
        return probability;
    }
    public void setProbability(String newValue) {
        this.probability = newValue;
    }
    
    
    ///////////////////////////////////////////////////      
    public String getImpact() {
        return impact;
    }
    public void setImpact(String newValue) {
        this.impact = newValue;
    }
    
    
    ///////////////////////////////////////////////////         
    public String getResponsible() {
        return responsible;
    }
    public void setResponsible(String newValue) {
        this.responsible = newValue;
    }
    
    
    ///////////////////////////////////////////////////         
    public String getStrategy() {
        return strategy;
    }
    public void setStrategy(String newValue) {
        this.strategy = newValue;
    }
    
    
    ///////////////////////////////////////////////////         
    public String getTendency() {
        return tendency;
    }
    public void setTendency(String newValue) {
        this.tendency = newValue;
    }
    
    
    ///////////////////////////////////////////////////         
    public String getContingency() {
        return contingency;
    }
    public void setContingency(String newValue) {
        this.contingency = newValue;
    }
    
    
    ///////////////////////////////////////////////////    
	public String getShowIssueConfirmation() {
		return showIssueConfirmation;
	}
	public void setShowIssueConfirmation(String newValue) {
		this.showIssueConfirmation = newValue;
	}


    ////////////////////////////////////////////    
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String newValue) {
		this.projectName = newValue;
	} 

	
    //////////////////////////////////////////
	public boolean getCostImpact() {
		return costImpact;
	}
	public void setCostImpact(boolean newValue) {
		this.costImpact = newValue;
	}


    //////////////////////////////////////////
	public boolean getQualityImpact() {
		return qualityImpact;
	}
	public void setQualityImpact(boolean newValue) {
		this.qualityImpact = newValue;
	}

	
    //////////////////////////////////////////
	public boolean getScopeImpact() {
		return scopeImpact;
	}
	public void setScopeImpact(boolean newValue) {
		this.scopeImpact = newValue;
	}

	
    //////////////////////////////////////////
	public boolean getTimeImpact() {
		return timeImpact;
	}
	public void setTimeImpact(boolean newValue) {
		this.timeImpact = newValue;
	}

	
    //////////////////////////////////////////	
	public String getRiskType() {
		return riskType;
	}
	public void setRiskType(String newValue) {
		this.riskType = newValue;
	}



	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
	
		if (this.operation.equals("saveRisk")) {
		    
		    if (this.name==null || this.name.trim().equals("")){
		        errors.add("Name", new ActionError("validate.formRisk.blankName") );
		    }
		    
		    if (this.description==null || this.description.trim().equals("")){
		        errors.add("Name", new ActionError("validate.formRisk.blankDesc") );
		    }
		    
		    if (this.responsible==null || this.responsible.trim().equals("")){
		        errors.add("Name", new ActionError("validate.formRisk.blankResp") );
		    }

		    if (this.riskType==null || this.riskType.trim().equals("")){
		        errors.add("Type", new ActionError("validate.formRisk.blankType") );
		    }
		    
		}
		
		return errors;
	}

    /////////////////////////////////////     
    public String getVisible() {
		return visible;
	}
	public void setVisible(String newValue) {
		this.visible = newValue;
	}


    /////////////////////////////////////    
	public String getRiskComment() {
		return riskComment;
	}
	public void setRiskComment(String newValue) {
		this.riskComment = newValue;
	}
	
	
	
}
