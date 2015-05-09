package com.pandora.gui.struts.form;

/**
 * This class handle the data of View Report Form
 */
public class ViewBSCForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** Project related with KPI */
    private String projectId; 

    /** Name of Project related with KPI */
    private String projectName; 
    
    /** The initial range to be used for KPI */
    private String initialDate;
    
    /** The final range to be used for KPI */
    private String finalDate;
    
    /** Text data in html format */
    private String finantialTable;
    
    /** Text data in html format */
    private String customerTable;
    
    /** Text data in html format */
    private String processTable;
    
    /** Text data in html format */
    private String learningTable;
    
    /** category related */
    private String categoryId;

    
    //////////////////////////////////////////////
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }

    //////////////////////////////////////////////
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String newValue) {
        this.projectName = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getFinalDate() {
        return finalDate;
    }
    public void setFinalDate(String newValue) {
        this.finalDate = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getInitialDate() {
        return initialDate;
    }
    public void setInitialDate(String newValue) {
        this.initialDate = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getCustomerTable() {
        return customerTable;
    }
    public void setCustomerTable(String newValue) {
        this.customerTable = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getFinantialTable() {
        return finantialTable;
    }
    public void setFinantialTable(String newValue) {
        this.finantialTable = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getLearningTable() {
        return learningTable;
    }
    public void setLearningTable(String newValue) {
        this.learningTable = newValue;
    }
    
    //////////////////////////////////////////////    
    public String getProcessTable() {
        return processTable;
    }
    public void setProcessTable(String newValue) {
        this.processTable = newValue;
    }
    
    //////////////////////////////////////////////      
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }
}
