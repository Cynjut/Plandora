package com.pandora.gui.struts.form;

public class TemplateForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
    
    private String name;

    private String enable;
    
    private String category;
    
    private String htmlMap;
    
    private String nodeType = "NONE";
    
    private String showDecision = "off";
    private String editNode = "off";
    private String showCommitRevert = "off";
    
    private String nodeId;
    private String nodeName;
    private String nodeDescription;
    private String nodeRelatedTemplateId;
    private String nodeRelatedProjectId;
    private String nodeNextNodeId;

    private String stepCategory;
    private String stepResourceList;
    private String stepCategoryRegex;
    
    private String decisionQuestion;
    private String decisionIfFalseNextNodeId;

    
	public void clear() {
	    name = null;
	    enable = null;	    
	    category = null;
	    htmlMap = null;
	    nodeType = "NONE";
	    showDecision = "off";
	    showCommitRevert = "off";
	    editNode = "off";
	    nodeId = null;
	    nodeName = null;
	    nodeDescription = null;
	    nodeRelatedTemplateId = null;
	    nodeRelatedProjectId = null;
	    nodeNextNodeId = null;
	    stepCategory = null;
	    stepResourceList = null;
	    stepCategoryRegex = null;
	    decisionQuestion = null;
	    decisionIfFalseNextNodeId = null;
	}
    
    
    ////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
    ////////////////////////////////////////
	public String getEnable() {
		return enable;
	}
	public void setEnable(String newValue) {
		this.enable = newValue;
	}
	
	
    ////////////////////////////////////////
	public String getCategory() {
		return category;
	}
	public void setCategory(String newValue) {
		this.category = newValue;
	}
	
	
    ////////////////////////////////////////
	public String getHtmlMap() {
		return htmlMap;
	}
	public void setHtmlMap(String newValue) {
		this.htmlMap = newValue;
	}

	
    ////////////////////////////////////////
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String newValue) {
		this.nodeType = newValue;
	}
	
	
    ////////////////////////////////////////
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	
    ////////////////////////////////////////
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String newValue) {
		this.nodeName = newValue;
	}
	
	
    ////////////////////////////////////////
	public String getNodeDescription() {
		return nodeDescription;
	}
	public void setNodeDescription(String newValue) {
		this.nodeDescription = newValue;
	}
	
    ////////////////////////////////////////
	public String getNodeRelatedTemplateId() {
		return nodeRelatedTemplateId;
	}
	public void setNodeRelatedTemplateId(String newValue) {
		this.nodeRelatedTemplateId = newValue;
	}
	
    ////////////////////////////////////////
	public String getNodeRelatedProjectId() {
		return nodeRelatedProjectId;
	}
	public void setNodeRelatedProjectId(String newValue) {
		this.nodeRelatedProjectId = newValue;
	}
	
    ////////////////////////////////////////
	public String getNodeNextNodeId() {
		return nodeNextNodeId;
	}
	public void setNodeNextNodeId(String newValue) {
		this.nodeNextNodeId = newValue;
	}
	
    ////////////////////////////////////////
	public String getStepCategory() {
		return stepCategory;
	}
	public void setStepCategory(String newValue) {
		this.stepCategory = newValue;
	}
	
    ////////////////////////////////////////
	public String getStepResourceList() {
		return stepResourceList;
	}
	public void setStepResourceList(String newValue) {
		this.stepResourceList = newValue;
	}
	
    ////////////////////////////////////////
	public String getStepCategoryRegex() {
		return stepCategoryRegex;
	}
	public void setStepCategoryRegex(String newValue) {
		this.stepCategoryRegex = newValue;
	}
	
	
    ////////////////////////////////////////
	public String getDecisionQuestion() {
		return decisionQuestion;
	}
	public void setDecisionQuestion(String newValue) {
		this.decisionQuestion = newValue;
	}
	
	
    ////////////////////////////////////////
	public String getDecisionIfFalseNextNodeId() {
		return decisionIfFalseNextNodeId;
	}
	public void setDecisionIfFalseNextNodeId(String newValue) {
		this.decisionIfFalseNextNodeId = newValue;
	}

	
    ////////////////////////////////////////
	public String getShowDecision() {
		return showDecision;
	}
	public void setShowDecision(String newValue) {
		this.showDecision = newValue;
	}


	////////////////////////////////////////
	public String getEditNode() {
		return editNode;
	}
	public void setEditNode(String newValue) {
		this.editNode = newValue;
	}

	
	////////////////////////////////////////
	public String getShowCommitRevert() {
		return showCommitRevert;
	}
	public void setShowCommitRevert(String newValue) {
		this.showCommitRevert = newValue;
	}
	
	
}
