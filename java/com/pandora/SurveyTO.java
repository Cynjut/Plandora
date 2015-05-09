package com.pandora;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.helper.HtmlUtil;

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

	private Vector questionList;
	
	private String anonymousKey;
	
	private UserTO owner;
	
	private ArrayList questionsToBeUpdated = new ArrayList();
	
	private ArrayList questionsToBeRemoved = new ArrayList();
	
	
	public SurveyTO() {
	}

	public SurveyTO(String id) {
		this.setId(id);
	}
	
	
	public boolean checkAnswer() {
		boolean response = false;
		if (questionList!=null) {
			Iterator i = questionList.iterator();
			while(i.hasNext()) {
				SurveyQuestionTO qto = (SurveyQuestionTO)i.next();
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
	public Vector getQuestionList() {
		return questionList;
	}
	public void setQuestionList(Vector newValue) {
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
	public ArrayList getQuestionsToBeUpdated() {
		return questionsToBeUpdated;
	}
	public void setQuestionsToBeUpdated(ArrayList newValue) {
		this.questionsToBeUpdated = newValue;
	}

	
	///////////////////////////////////
	public ArrayList getQuestionsToBeRemoved() {
		return questionsToBeRemoved;
	}
	public void setQuestionsToBeRemoved(ArrayList newValue) {
		this.questionsToBeRemoved = newValue;
	}

	
	
	
}
