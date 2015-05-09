package com.pandora;

import java.sql.Timestamp;

public class QuestionAnswerTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private SurveyQuestionTO question;
	
	private UserTO user;
	
	private String value;
	
	private Timestamp answerDate;
	
	
	/////////////////////////////////////////
	public SurveyQuestionTO getQuestion() {
		return question;
	}
	public void setQuestion(SurveyQuestionTO newValue) {
		this.question = newValue;
	}

	
	/////////////////////////////////////////
	public UserTO getUser() {
		return user;
	}
	public void setUser(UserTO newValue) {
		this.user = newValue;
	}

	
	/////////////////////////////////////////
	public String getValue() {
		return value;
	}
	public void setValue(String newValue) {
		this.value = newValue;
	}

	
	/////////////////////////////////////////
	public Timestamp getAnswerDate() {
		return answerDate;
	}
	public void setAnswerDate(Timestamp newValue) {
		this.answerDate = newValue;
	}
	
}
