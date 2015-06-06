package com.pandora.gui.struts.form;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.SurveyQuestionTO;
import com.pandora.UserTO;
import com.pandora.helper.FormValidationUtil;
import com.pandora.helper.SessionUtil;

public class SurveyForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	    
    private String name;

    private boolean isTemplate;

    private boolean isAnonymous;

    private String projectId;
    
    private String description;
    
    private String publishingDate;
    
    private String finalDate;
    
    private String removedQuestionId;
    
    private String showSaveConfirmation;
    
    private String showEditQuestion;
    
	private String pathContext;
	
    private String key;

    private String reportURL;
    
    private String replicationHtmlList;
    
	private ArrayList<String> questionsToBeUpdated;
	
	private ArrayList<String> questionsToBeRemoved;

	private String editQuestionId;
	
	private boolean hideClosedSurveys;
	
    /**
     * Clear values of Form
     */
    public void clear(){    
    	this.id = null;
        this.name = null;
        this.description = null;        
        this.publishingDate = null;
        this.finalDate = null;
        this.key = null;
        this.removedQuestionId = null;
        this.showSaveConfirmation = "off";
        this.showEditQuestion = "off";
        this.reportURL = null;
        this.editQuestionId = null;
        this.replicationHtmlList = null;
        this.questionsToBeRemoved = null;
        this.questionsToBeUpdated = null;
        this.isAnonymous = false;
    }
    
    
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.isTemplate = false;
        this.isAnonymous = false;
        this.hideClosedSurveys = false;
    }

    
    ////////////////////////////////////
	public boolean getIsTemplate() {
		return isTemplate;
	}
	public void setIsTemplate(boolean newValue) {
		this.isTemplate = newValue;
	}

	
    ////////////////////////////////////
	public String getAnonymousStatus() {
		return (isAnonymous?"on":"off");
	}	
	public boolean getIsAnonymous() {
		return isAnonymous;
	}
	public void setIsAnonymous(boolean newValue) {
		this.isAnonymous = newValue;
	}


	////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}


    ////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}


	////////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}


    ////////////////////////////////////////	
	public String getPublishingDate() {
		return publishingDate;
	}
	public void setPublishingDate(String newValue) {
		this.publishingDate = newValue;
	}


    ////////////////////////////////////////	
	public String getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(String newValue) {
		this.finalDate = newValue;
	}

	
    ////////////////////////////////////////	
	public String getRemovedQuestionId() {
		return removedQuestionId;
	}
	public void setRemovedQuestionId(String newValue) {
		this.removedQuestionId = newValue;
	}

	
    ////////////////////////////////////////	
	public String getShowSaveConfirmation() {
		return showSaveConfirmation;
	}
	public void setShowSaveConfirmation(String newValue) {
		this.showSaveConfirmation = newValue;
	}

	
    ////////////////////////////////////////		
    public String getShowEditQuestion() {
		return showEditQuestion;
	}
	public void setShowEditQuestion(String newValue) {
		this.showEditQuestion = newValue;
	}


	////////////////////////////////////////	
	public String getKey() {
		return key;
	}
	public void setKey(String newValue) {
		this.key = newValue;
	}

	
    ////////////////////////////////////////
	public String getReportURL() {
		return reportURL;
	}
	public void setReportURL(String newValue) {
		this.reportURL = newValue;
	}

	
    ////////////////////////////////////////
	public String getReplicationHtmlList() {
		return replicationHtmlList;
	}
	public void setReplicationHtmlList(String newValue) {
		this.replicationHtmlList = newValue;
	}

	
	///////////////////////////////////	
	public String getEditQuestionId() {
		return editQuestionId;
	}
	public void setEditQuestionId(String newValue) {
		this.editQuestionId = newValue;
	}

	
	///////////////////////////////////	
	public boolean getHideClosedSurveys() {
		return hideClosedSurveys;
	}
	public void setHideClosedSurveys(boolean newValue) {
		this.hideClosedSurveys = newValue;
	}


	///////////////////////////////////
	public ArrayList<String> getQuestionsToBeUpdated() {
		return questionsToBeUpdated;
	}
	public void addQuestionsToBeUpdated(String questionId) {
		boolean already = false;
		if (this.questionsToBeUpdated==null) {
			this.questionsToBeUpdated = new ArrayList<String>();
		} else {
			Object[] list = this.questionsToBeUpdated.toArray();
			for (int i=0 ; i<list.length; i++) {
				if (list[i].equals(questionId)) {
					already = true;
					break;
				}
			}			
		}
		if (!already) {
			this.questionsToBeUpdated.add(questionId);	
		}		
	}
	public void clearQuestionsToBeUpdated(){
		this.questionsToBeUpdated = null;
	}
	
	///////////////////////////////////
	public ArrayList<String> getQuestionsToBeRemoved() {
		return questionsToBeRemoved;
	}
	public void addQuestionsToBeRemoved(String questionId) {
		boolean already = false;
		if (this.questionsToBeRemoved==null) {
			this.questionsToBeRemoved = new ArrayList<String>();
		} else {
			Object[] list = this.questionsToBeRemoved.toArray();
			for (int i=0 ; i<list.length; i++) {
				if (list[i].equals(questionId)) {
					already = true;
					break;
				}
			}			
		}
		if (!already) {
			this.questionsToBeRemoved.add(questionId);	
		}
	}
	public void clearQuestionsToBeRemoved() {
		this.questionsToBeRemoved = null;
	}
		

	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (this.operation.equals("saveSurvey")) {
		    
			Locale loc = SessionUtil.getCurrentLocale(request);
			UserTO uto = SessionUtil.getCurrentUser(request);

		    if (this.name==null || this.name.trim().equals("")){
		       errors.add("Name", new ActionError("validate.formSurvey.blankName") );
		    }
		    
		    FormValidationUtil.checkDate(errors, "Publishing Date", this.publishingDate, loc, uto.getCalendarMask());
		    FormValidationUtil.checkDate(errors, "Final Date", this.finalDate, loc, uto.getCalendarMask());
		    
		    @SuppressWarnings("unchecked")
			Vector<SurveyQuestionTO> qList = (Vector<SurveyQuestionTO>)request.getSession().getAttribute("questionList");
		    if (qList==null || qList.size()==0) {
		    	errors.add("Name", new ActionError("validate.formSurvey.blankQuestion") );
		    }
		}	
		return errors;
	}
    
	
	public void setPathContext(String fullUrl, String contextApp) {
		this.pathContext=null;
		if (fullUrl!=null && contextApp!=null) {
			int i = fullUrl.indexOf(contextApp);
			if (i>0) {
				this.pathContext = fullUrl.substring(0, i + contextApp.length());	
			}
		}
	}

	
	
	public String getAnonymousURI(){
		String uri = "";
		if (this.isAnonymous) {
			uri = this.pathContext + "/do/showSurvey?operation=anonymous&key=" + this.key;
		}
		return uri;
	}
	
    
}
