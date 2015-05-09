package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.LeaderTO;
import com.pandora.QuestionAnswerTO;
import com.pandora.SurveyQuestionTO;
import com.pandora.SurveyTO;

import com.pandora.UserTO;
import com.pandora.delegate.SurveyDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ShowSurveyForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

public class ShowSurveyAction extends GeneralStrutsAction {
	
	public static String PARTIAL_ANSWERS = "PARTIAL_SURVEY_ANSWERS";
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showSurvey";
		
		try {
			ShowSurveyForm frm = (ShowSurveyForm)form;
			SurveyDelegate sdel = new SurveyDelegate();
			
			if (frm.getId()!=null && !frm.getId().trim().equals("")) {
				SurveyTO sto = new SurveyTO(frm.getId());
				sto.setOwner(SessionUtil.getCurrentUser(request));
				sto = sdel.getSurvey(sto);
				frm.setCurrentSurvey(sto);
				
				//check if current user could see the current answers...
				if (frm.isShow()) {
					frm.setShow(this.canSeeAnswerList(request, sto));
				}
				
				Timestamp today = DateUtil.getNow();
				if (sto!=null && ((sto.getFinalDate()!=null && sto.getFinalDate().before(today)) ||
						sto.getPublishingDate().after(today) )) {
					
					//check if the survey could be used
					this.setErrorFormSession(request, "label.formSurvey.surveyClosed", null);
					forward = "home";
					
				} else {
					frm.setSurveyTitle(sto.getName());
					frm.setSurveyDescription(sto.getDescription());
					frm.setQuestionsBody(this.getQuestionHtmlList(sto, frm, request).toString());				
				}
				frm.setAllowAnonymous(sto.getIsAnonymous().booleanValue());
				
			} else {
				forward = "home";
				request.removeAttribute(PARTIAL_ANSWERS);
			}
						
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);
	}

	
	public ActionForward selectSurvey(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showUserSurveyList";
		ShowSurveyForm frm = (ShowSurveyForm)form;
		
		Vector v = (Vector)request.getSession().getAttribute(UserDelegate.USER_SURVEY_LIST);
		String list = getHtmlCombo(request, v);
		
		frm.setHtmlList(list);	
		return mapping.findForward(forward);
	}


	public String getHtmlCombo(HttpServletRequest request, Vector v) {
		String list = "<select id=\"surveyId\" name=\"surveyId\" class=\"textBox\">";
		list = list + "<option value=\"-1\">" + super.getBundleMessage(request, "label.combo.select") + "</option>";
		if (v!=null) {
			list = list + HtmlUtil.getComboOptions("surveyId", v, "textBox", null);
		}
		list = list + "</select>";
		return list;
	}
	

	public ActionForward anonymous(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "home";
		
		try {
			ShowSurveyForm frm = (ShowSurveyForm)form;			
			request.removeAttribute(PARTIAL_ANSWERS);
			
			SurveyTO sto = this.checkAnonymous(request, form);
			if (sto!=null) {
				frm.setId(sto.getId());
				this.prepareForm(mapping, form, request, response);
				forward = "showSurvey";					
			}
			
			frm.setAnonymous(true);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(forward);
	}
	
	
	private SurveyTO checkAnonymous(HttpServletRequest request, ActionForm form) throws BusinessException{
		SurveyTO response = null;
		
		ShowSurveyForm frm = (ShowSurveyForm)form;
		
		String key = frm.getKey(); // request.getParameter("key");
		if (key!=null && !key.trim().equals("")) {
			SurveyDelegate sdel = new SurveyDelegate();
			SurveyTO sto = sdel.getSurveyByKey(key);
			frm.setAllowAnonymous(sto.getIsAnonymous().booleanValue());

			//check if survey allow anonymous fill-in
			if (sto!=null && sto.getIsAnonymous().booleanValue()) {
				Timestamp today = DateUtil.getNow();
				if ((sto.getFinalDate()!=null && sto.getFinalDate().before(today)) ||
						sto.getPublishingDate().after(today) ) {
					this.setErrorFormSession(request, "label.formSurvey.surveyClosed", null);					
				} else {
					response = sto;
				}
			}				
		}
		return response;
	}

	
	private StringBuffer getQuestionHtmlList(SurveyTO sto, ShowSurveyForm frm, HttpServletRequest request){
		StringBuffer c = new StringBuffer();
		Vector list = sto.getQuestionList();
		
		boolean showAnswerButton = frm.isShow();		
		frm.setShowMandatoryNote(false);
		
		Iterator i = list.iterator();
		String oldValue = "";
		while(i.hasNext()) {
			SurveyQuestionTO sqto = (SurveyQuestionTO)i.next();
			String subtitle = sqto.getSubTitle();
			if (subtitle!=null && !subtitle.equals(oldValue)) {
				c.append("<table width=\"60%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				c.append("  <tr class=\"gapFormBody\"><td>&nbsp;</td></tr>");
				c.append("  <tr class=\"gapFormBody\"><td>&nbsp;</td></tr>");
				c.append("  <tr>");
				c.append("    <td class=\"successfullyMessage\"><b>" +  subtitle + "</b></td>");
				c.append("  </tr>");
				c.append("  <tr class=\"gapFormBody\"><td>&nbsp;</td></tr>");
				c.append("</table>");
				oldValue = subtitle;					
			}
			
			sqto.setGenericTag(this.getPartialAnswer(sqto.getId(), request));
			c.append(sqto.getHtml(subtitle!=null, frm.isAnonymous()));
			
			if (showAnswerButton) {
				String respLbl = super.getBundleMessage(request, "label.formSurvey.currentanswerBt");
				c.append("<table width=\"60%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				c.append("  <tr>");
				c.append("    <td align=\"right\" class=\"gapFormBody\"><input type=\"button\" value=\"" + respLbl + "\" onclick=\"javascript:displayMessage('../do/showSurvey?operation=showAnswer&id=" + sto.getId() + "&questionid=" + sqto.getId() + "', 400, 300);\" class=\"button\"></td>");
				c.append("  </tr>");
				c.append("</table>");				
			}
			
			if (sqto.getIsMandatory()!=null && sqto.getIsMandatory().booleanValue()) {
				frm.setShowMandatoryNote(true);
			}
		}
		return c;
	}

	
	public ActionForward anonyanswer(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ActionForward fwd = mapping.findForward("home");
		try {
			SurveyTO sto = this.checkAnonymous(request, form);
			if (sto!=null) {
				fwd = this.answer(mapping, form, request, response);					
			}
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return fwd;
	}

	
	public ActionForward answer(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			ShowSurveyForm frm = (ShowSurveyForm)form;
			SurveyDelegate sdel = new SurveyDelegate();
			
			SurveyTO sto = new SurveyTO(frm.getId());
			sto = sdel.getSurvey(sto);
			frm.setAllowAnonymous(sto.getIsAnonymous().booleanValue());
			Timestamp now = DateUtil.getNow();

			UserTO uto = null;
			if (!frm.isAnonymous()) {
				uto = SessionUtil.getCurrentUser(request);	
			}
			Vector answerList = new Vector();
			
			boolean saveAnswer = true;
			
			Vector list = sto.getQuestionList();
			Iterator i = list.iterator();
			while(i.hasNext()) {
				SurveyQuestionTO q = (SurveyQuestionTO) i.next();
				String value = request.getParameter("field_" + q.getId());
				this.addPartialAnswer(q.getId(), value, request);
			}
			
			i = list.iterator();
			while(i.hasNext()) {
				SurveyQuestionTO q = (SurveyQuestionTO) i.next();
				String value = this.getPartialAnswer(q.getId(), request);
				
				if (value!=null && !value.trim().equals("")) {
					QuestionAnswerTO qato = new QuestionAnswerTO();
					qato.setQuestion(q);
					qato.setUser(uto);
					qato.setValue(value);
					qato.setAnswerDate(now);
					answerList.addElement(qato);
					
					
					
				} else {
					if (q.getIsMandatory()!=null && q.getIsMandatory().booleanValue()) {
						this.setErrorFormSession(request, "error.formSurvey.mandatory", null);
						saveAnswer = false;
						break;
					}
				}
			}

			if (saveAnswer) {
				sdel.saveAnswer(answerList);
				this.setSuccessFormSession(request, "message.answerSurvey");
				frm.setAnonymous(false);
			}
			
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return this.prepareForm(mapping, form, request, response);
	}

	
	private boolean canSeeAnswerList(HttpServletRequest request, SurveyTO sto) throws BusinessException {
		boolean response = false;
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		if (uto!=null) {

			UserDelegate udel = new UserDelegate();
			Vector leaderList = udel.getLeaderByProject(sto.getProject());
			Iterator i = leaderList.iterator();
			while(i.hasNext()) {
				LeaderTO leader = (LeaderTO)i.next();
				if (leader.getId().equals(uto.getId())) {
					response = true;
				}
			}	
		}
		
		return response;
	}

	
	private void addPartialAnswer(String key, String value, HttpServletRequest request){
		if (value!=null && key!=null) {
			HashMap partialAnswers = (HashMap)request.getAttribute(PARTIAL_ANSWERS);
			if (partialAnswers==null) {
				partialAnswers = new HashMap();
			}
			partialAnswers.put(key, value);
			request.setAttribute(PARTIAL_ANSWERS, partialAnswers);
		}
	}
	
	
	private String getPartialAnswer(String key, HttpServletRequest request){
		String response = null;
		HashMap partialAnswers = (HashMap)request.getAttribute(PARTIAL_ANSWERS);
		if (partialAnswers!=null && key!=null) {
			response = (String)partialAnswers.get(key);
		}
		return response;
	}
	
}
