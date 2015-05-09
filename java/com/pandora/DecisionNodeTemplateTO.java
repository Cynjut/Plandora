package com.pandora;


public class DecisionNodeTemplateTO extends NodeTemplateTO {

	public static final String LABEL_YES = "label.yes";
	
	public static final String LABEL_NO  = "label.no";
	
	
	private static final long serialVersionUID = 1L;

	private String questionContent;
	
	private NodeTemplateTO nextNodeIfFalse;

	private String decisionAnswer;
	
	
	public DecisionNodeTemplateTO(String newId) {
		this.setId(newId);
	}
	
	
	//////////////////////////////////////
	public String getQuestionContent() {
		return questionContent;
	}
	public void setQuestionContent(String newValue) {
		this.questionContent = newValue;
	}

	
	//////////////////////////////////////
	public NodeTemplateTO getNextNodeIfFalse() {
		return nextNodeIfFalse;
	}
	public void setNextNodeIfFalse(NodeTemplateTO newValue) {
		this.nextNodeIfFalse = newValue;
	}
	
	
	/////////////////////////////////////////	
	public String getDecisionAnswer() {
		return decisionAnswer;
	}
	public void setDecisionAnswer(String newValue) {
		this.decisionAnswer = newValue;
	}
		
}
