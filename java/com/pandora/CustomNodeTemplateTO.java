package com.pandora;

public class CustomNodeTemplateTO extends TransferObject {

	private static final long serialVersionUID = 1L;
	
	private String nodeTemplateId;
	
	private String planningId;
	
	private String templateId;
	
	private Integer instanceId;
	
	private String name;
	
	private String description;
	
	private String projectId;
	
	private String categoryId;
	
	private String resource;

	private String questionContent;
	
	private String relatedTaskId;

	private Boolean isParentTask;
	
	private String decisionAnswer;
	
	
	public CustomNodeTemplateTO(){
	}
	

	public CustomNodeTemplateTO(NodeTemplateTO node, String reqId){
		
		if (node instanceof StepNodeTemplateTO) {
			StepNodeTemplateTO subnode = (StepNodeTemplateTO) node;
			this.setCategoryId(subnode.getCategoryId());
			this.setProjectId(subnode.getProject().getId());			
			this.setResource(subnode.getResourceId());
			this.setQuestionContent(null);
			
		} else if (node instanceof DecisionNodeTemplateTO) {
			DecisionNodeTemplateTO subnode = (DecisionNodeTemplateTO) node;
			this.setQuestionContent(subnode.getQuestionContent());
			this.setDecisionAnswer(subnode.getDecisionAnswer());
			this.setCategoryId(null);
			this.setProjectId(null);
			this.setResource(null);
		}
		
		this.setDescription(node.getDescription());
		this.setName(node.getName());
		this.setNodeTemplateId(node.getId());
		this.setPlanningId(reqId);
		this.setTemplateId(node.getPlanningId());
	}

	
	/////////////////////////////////////////
	public String getNodeTemplateId() {
		return nodeTemplateId;
	}
	public void setNodeTemplateId(String newValue) {
		this.nodeTemplateId = newValue;
	}

	
	/////////////////////////////////////////
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}

	
	/////////////////////////////////////////
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String newValue) {
		this.templateId = newValue;
	}

	
	/////////////////////////////////////////	
	public Integer getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Integer newValue) {
		this.instanceId = newValue;
	}

	/////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	/////////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}

	
	/////////////////////////////////////////	
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	/////////////////////////////////////////
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String newValue) {
		this.categoryId = newValue;
	}

	
	/////////////////////////////////////////
	public String getResource() {
		return resource;
	}
	public void setResource(String newValue) {
		this.resource = newValue;
	}

	
	/////////////////////////////////////////
	public String getQuestionContent() {
		return questionContent;
	}
	public void setQuestionContent(String newValue) {
		this.questionContent = newValue;
	}

	
	/////////////////////////////////////////
	public String getRelatedTaskId() {
		return relatedTaskId;
	}
	public void setRelatedTaskId(String newValue) {
		this.relatedTaskId = newValue;
	}


	/////////////////////////////////////////	
	public Boolean getIsParentTask() {
		return isParentTask;
	}
	public void setIsParentTask(Boolean newValue) {
		this.isParentTask = newValue;
	}

	
	/////////////////////////////////////////	
	public String getDecisionAnswer() {
		return decisionAnswer;
	}
	public void setDecisionAnswer(String newValue) {
		this.decisionAnswer = newValue;
	}
	
	
}

