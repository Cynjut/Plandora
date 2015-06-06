package com.pandora;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class SurveyTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private String name;
	
	private String description;
	
	private Boolean isTemplate;
	
	private Boolean isAnonymous;
	
	private ProjectTO project;
	
	private Timestamp creationDate;
	
	private Timestamp finalDate;
	
	private Timestamp publishingDate;

	private Vector<SurveyQuestionTO> questionList;
	
	private String anonymousKey;
	
	private UserTO owner;
	
	private ArrayList<String> questionsToBeUpdated = new ArrayList<String>();
	
	private ArrayList<String> questionsToBeRemoved = new ArrayList<String>();
	
	
	public SurveyTO() {
	}

	public SurveyTO(String id) {
		this.setId(id);
	}
	
	
	public boolean checkAnswer() {
		boolean response = false;
		if (questionList!=null) {
			Iterator<SurveyQuestionTO> i = questionList.iterator();
			while(i.hasNext()) {
				SurveyQuestionTO qto = i.next();
				if (qto.getRelatedAnswer()!=null) {
					response = true;
					break;
				}				
			}
		}
		return response;
	}

	
	///////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	///////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}

	
	///////////////////////////////////
	public Boolean getIsTemplate() {
		return isTemplate;
	}
	public void setIsTemplate(Boolean newValue) {
		this.isTemplate = newValue;
	}

	
	///////////////////////////////////
	public Boolean getIsAnonymous() {
		return isAnonymous;
	}
	public void setIsAnonymous(Boolean newValue) {
		this.isAnonymous = newValue;
	}

	
	///////////////////////////////////
	public ProjectTO getProject() {
		return project;
	}
	public void setProject(ProjectTO newValue) {
		this.project = newValue;
	}

	
	///////////////////////////////////
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Timestamp newValue) {
		this.creationDate = newValue;
	}

	
	///////////////////////////////////
	public Timestamp getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(Timestamp newValue) {
		this.finalDate = newValue;
	}

	
	///////////////////////////////////
	public Timestamp getPublishingDate() {
		return publishingDate;
	}
	public void setPublishingDate(Timestamp newValue) {
		this.publishingDate = newValue;
	}

	
	///////////////////////////////////
	public Vector<SurveyQuestionTO> getQuestionList() {
		return questionList;
	}
	public void setQuestionList(Vector<SurveyQuestionTO> newValue) {
		this.questionList = newValue;
	}

	
	///////////////////////////////////
	public String getAnonymousKey() {
		return anonymousKey;
	}
	public void setAnonymousKey(String newValue) {
		this.anonymousKey = newValue;
	}

	
	///////////////////////////////////
	public UserTO getOwner() {
		return owner;
	}
	public void setOwner(UserTO newValue) {
		this.owner = newValue;
	}

	
	///////////////////////////////////
	public ArrayList<String> getQuestionsToBeUpdated() {
		return questionsToBeUpdated;
	}
	public void setQuestionsToBeUpdated(ArrayList<String> newValue) {
		this.questionsToBeUpdated = newValue;
	}

	
	///////////////////////////////////
	public ArrayList<String> getQuestionsToBeRemoved() {
		return questionsToBeRemoved;
	}
	public void setQuestionsToBeRemoved(ArrayList<String> newValue) {
		this.questionsToBeRemoved = newValue;
	}
	
}
