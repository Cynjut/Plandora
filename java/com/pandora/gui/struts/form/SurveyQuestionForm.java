package com.pandora.gui.struts.form;

public class SurveyQuestionForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String surveyId;
	
	private String type;

	private String domain;
	
	private String position;
	
	private String content;
	
	private String subtitle;
	
	private String mandatory;
	
	private String editQuestionId;
	
	
	//////////////////////////////////////////////
	public String getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(String newValue) {
		this.surveyId = newValue;
	}

	//////////////////////////////////////////////
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}

	
	//////////////////////////////////////////////
	public String getDomain() {
		return domain;
	}
	public void setDomain(String newValue) {
		this.domain = newValue;
	}

	
	//////////////////////////////////////////////
	public String getPosition() {
		return position;
	}
	public void setPosition(String newValue) {
		this.position = newValue;
	}

	
	//////////////////////////////////////////////
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String newValue) {
		this.subtitle = newValue;
	}

	
	//////////////////////////////////////////////
	public String getMandatory() {
		return mandatory;
	}
	public void setMandatory(String newValue) {
		this.mandatory = newValue;
	}

	
	//////////////////////////////////////////////
	public String getContent() {
		return content;
	}
	public void setContent(String newValue) {
		this.content = newValue;
	}

	
	//////////////////////////////////////////////
	public String getEditQuestionId() {
		return editQuestionId;
	}
	public void setEditQuestionId(String newValue) {
		this.editQuestionId = newValue;
	}
	
	
	//////////////////////////////////////////////	
	public String getEditDomain() {
		String response = "off";
		if (editQuestionId==null || editQuestionId.trim().equals("")){
			response = "on";
		} else {
			if (this.domain!=null && !this.domain.trim().equals("")) {
				response = "on";	
			}
		}
		return response;
	}
	
	
}
