package com.pandora;

public class QuestionAlternativeTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private SurveyQuestionTO question;
	 
	private Integer sequence;
	
	private String content;

	
	private int answerNumber = 0; //this field is not persistent	
		
	//////////////////////////////////////
	public SurveyQuestionTO getQuestion() {
		return question;
	}
	public void setQuestion(SurveyQuestionTO newValue) {
		this.question = newValue;
	}
		 
	//////////////////////////////////////	 
	public Integer getSequence() {
		return sequence;
	}	
	public void setSequence(Integer newValue) {
		this.sequence = newValue;
	}
		 
	//////////////////////////////////////	 
	public String getContent() {
		return content;
	}
	public void setContent(String newValue) {
		this.content = newValue;
	}
	
	/////////////////////////////////////////	
	public int getAnswerNumber() {
		return answerNumber;
	}
	public void addAnswerNumber() {
		this.answerNumber++;
	}	
}
