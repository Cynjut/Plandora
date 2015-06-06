package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.QuestionAlternativeTO;
import com.pandora.SurveyQuestionTO;
import com.pandora.SurveyTO;
import com.pandora.TransferObject;
import com.pandora.delegate.SurveyDelegate;
import com.pandora.gui.struts.form.SurveyQuestionForm;

public class SurveyQuestionAction extends GeneralStrutsAction {

	public ActionForward showEditPopup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showSurveyQuestionEdit";
		
		try {
			Vector<TransferObject> list = new Vector<TransferObject>();
			for (int i=0; i<=2; i++) {
				list.addElement(new TransferObject(i+"", super.getBundleMessage(request, "label.formSurvey.type." + i)));
			}
			request.getSession().setAttribute("surveyQuestionTypes", list);

			
			Vector<TransferObject> posList = new Vector<TransferObject>();
			for (int i=1; i<=15; i++) {
				posList.addElement(new TransferObject(i+"", i+""));
			}
			request.getSession().setAttribute("positionList", posList);


			Vector<TransferObject> manList = new Vector<TransferObject>();
			manList.addElement(new TransferObject("1", super.getBundleMessage(request, "label.yes")));
			manList.addElement(new TransferObject("0", super.getBundleMessage(request, "label.no")));
			request.getSession().setAttribute("mandatoryList", manList);

			SurveyQuestionForm frm = (SurveyQuestionForm)form;
			if (frm.getEditQuestionId()!=null && !frm.getEditQuestionId().equals("")) {
				SurveyQuestionTO qto = this.getSurveyQuestion(frm.getEditQuestionId(), request);
				if (qto!=null) {
				    frm.setType(qto.getQuestionType());
				    frm.setSurveyId(qto.getSurvey().getId());
				    frm.setSubtitle(qto.getSubTitle());
				    frm.setContent(qto.getContent());
				    frm.setPosition(qto.getPosition()+"");
				    
				    if (qto.getAlterativesList()!=null) {
						SurveyDelegate sdel = new SurveyDelegate();
						boolean answersExists = sdel.checkIfThereAreAnswers(qto);
						if (!answersExists) {
					    	String domain = "";
					    	Iterator<QuestionAlternativeTO> it = qto.getAlterativesList().iterator();
					    	while(it.hasNext()) {
					    		QuestionAlternativeTO qato = it.next();
					    		if (!domain.trim().equals("")){
					    			domain = domain + "|";	
					    		}
					    		domain = domain + qato.getContent();
					    	}
					    	frm.setDomain(domain);							
						} else {
							frm.setDomain("");
						}
						
				    } else {
				    	frm.setDomain("");	
				    }
				    frm.setMandatory(qto.getIsMandatory().booleanValue()?"1":"0");
				    frm.setId(qto.getId());					
				}
			}
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);		
		}
		
		return mapping.findForward(forward);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ActionForward saveQuestion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){

		try {
			SurveyQuestionTO qto = null;
	    	SurveyQuestionForm frm = (SurveyQuestionForm)form;
			if (frm.getEditQuestionId()!=null && !frm.getEditQuestionId().equals("")) {				
				qto = this.getSurveyQuestion(frm.getEditQuestionId(), request);
				if (qto!=null) {
				    qto.setSubTitle(frm.getSubtitle());
				    qto.setContent(frm.getContent());
				    qto.setPosition(new Integer(frm.getPosition()));
				    qto.setIsMandatory(new Boolean(frm.getMandatory().equals("1")));					
				}
				
			} else {
			    qto = new SurveyQuestionTO();
			    Vector<SurveyQuestionTO> questionList = (Vector)request.getSession().getAttribute("questionList");
			    			    
			    qto.setQuestionType(frm.getType());
			    qto.setSurvey(new SurveyTO(frm.getSurveyId()));
			    qto.setSubTitle(frm.getSubtitle());
			    qto.setContent(frm.getContent());
			    qto.setPosition(new Integer(frm.getPosition()));
			    qto.setIsMandatory(new Boolean(frm.getMandatory().equals("1")));
			    qto.setId("NEW_" + (questionList.size()+1));
			    
			    questionList.addElement(qto);
			    request.getSession().setAttribute("questionList", questionList);		    					
			}
			
			if (qto!=null) {
				SurveyDelegate sdel = new SurveyDelegate();
				boolean answersExists = sdel.checkIfThereAreAnswers(qto);
				if (!answersExists) {
					Vector alternativeList = new Vector();
				    if (frm.getType().equals(SurveyQuestionTO.QUESTION_TYPE_MULTI)) {
				    	String[] options = frm.getDomain().split("\\|");
				    	for (int i=0; i< options.length; i++) {
				    		QuestionAlternativeTO qa = new QuestionAlternativeTO();
				    		qa.setQuestion(qto);
				    		qa.setSequence(new Integer(i+1));
				    		qa.setContent(options[i].trim());
				    		alternativeList.addElement(qa);
				    	}
				    }
				    qto.setAlterativesList(alternativeList);									
				}
			}
			
    	} catch(Exception e){
    		this.setErrorFormSession(request, "error.formSurvey.showForm", e);
    	}

		return mapping.findForward("goToSurveyForm");		
	}
		
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private SurveyQuestionTO getSurveyQuestion(String qId, HttpServletRequest request){
		SurveyQuestionTO response = null;
	    Vector<SurveyQuestionTO> questionList = (Vector)request.getSession().getAttribute("questionList");
	    if (questionList!=null) {
		    Iterator<SurveyQuestionTO> i = questionList.iterator();
		    while(i.hasNext()) {
		    	SurveyQuestionTO qto = i.next();
		    	if (qto.getId().equals(qId)) {
		    		response = qto;
		    		break;
		    	}
		    }	    	
	    }
		return response;
	}
}
