package com.pandora;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.helper.HtmlUtil;

public class SurveyQuestionTO extends TransferObject {

	public static final String QUESTION_TYPE_MULTI     = "0";
	
	public static final String QUESTION_TYPE_TEXT      = "1";
	
	public static final String QUESTION_TYPE_TEXT_AREA = "2";
	
	
	private static final long serialVersionUID = 1L;

	private String questionType;
	
	private String content;
	
	private String subTitle;

	private Integer position;
	
	private Vector<QuestionAlternativeTO> alterativesList;
	
	private SurveyTO survey;
	
	private QuestionAnswerTO relatedAnswer;
	
	private Boolean isMandatory;
	
	
	////////////////////////////////////////
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String newValue) {
		this.questionType = newValue;
	}

	
	////////////////////////////////////////	
	public String getContent() {
		return content;
	}
	public void setContent(String newValue) {
		this.content = newValue;
	}

	
	////////////////////////////////////////	
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String newValue) {
		this.subTitle = newValue;
	}
	
	
	////////////////////////////////////////		
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer newValue) {
		this.position = newValue;
	}
	
	
	////////////////////////////////////////	
	public Vector<QuestionAlternativeTO> getAlterativesList() {
		return alterativesList;
	}
	public void setAlterativesList(Vector<QuestionAlternativeTO> newValue) {
		this.alterativesList = newValue;
	}
	
	
	////////////////////////////////////////		
	public SurveyTO getSurvey() {
		return survey;
	}
	public void setSurvey(SurveyTO newValue) {
		this.survey = newValue;
	}
	
	
	////////////////////////////////////////
	public QuestionAnswerTO getRelatedAnswer() {
		return relatedAnswer;
	}
	public void setRelatedAnswer(QuestionAnswerTO newValue) {
		this.relatedAnswer = newValue;
	}
	

	////////////////////////////////////////
	public Boolean getIsMandatory() {
		return isMandatory;
	}
	public void setIsMandatory(Boolean newValue) {
		this.isMandatory = newValue;
	}
	
	
	public String getHtml(boolean identation, boolean isAnonymous){
		StringBuffer buff = new StringBuffer();

		String answer = "";
		if (this.relatedAnswer!=null && !isAnonymous) {
			answer = this.relatedAnswer.getValue();
		}
		
		if (this.getGenericTag()!=null && (answer==null || answer.trim().equals(""))) {
			answer = this.getGenericTag();
		}
		
		if (identation) {
			buff.append("<table width=\"60%\" border=\"0\"><tr><td width=\"20\" class=\"gapFormBody\">&nbsp;</td><td>");
			buff.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");			
		} else {
			buff.append("<table width=\"60%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");	
		}
		
		buff.append("  <tr class=\"formBody\">\n");
		buff.append("    <td class=\"tableCell\" align=\"justify\" colspan=\"2\"><b>" +  this.getContent());
		if (this.isMandatory!=null && this.isMandatory.booleanValue()) {
			buff.append("&nbsp;(*)&nbsp;");	
		}
		buff.append("    </b></td>\n");
		buff.append("  </tr>\n");

		if (this.getQuestionType().equals(QUESTION_TYPE_MULTI)) {
			buff.append(this.getHtmlForMultipleChoice(answer));
		} else if (this.getQuestionType().equals(QUESTION_TYPE_TEXT_AREA)) {
			buff.append("  <tr class=\"tableCell\">\n");			
			buff.append("<td class=\"tableCell\" colspan=\"2\"><textarea name=\"field_" +  this.getId() + "\" cols=\"60\" rows=\"4\" class=\"textBox\">" + answer + "</textarea></td>\n");
			buff.append("  </tr>\n");			
		} else if (this.getQuestionType().equals(QUESTION_TYPE_TEXT)) {
			buff.append("  <tr class=\"tableCell\">\n");			
			buff.append("<td class=\"tableCell\" colspan=\"2\">"+ HtmlUtil.getTextBox("field_" +  this.getId(), answer, 255, 40));
			buff.append("  </tr>\n");						
		}
		
		
		buff.append("<tr class=\"gapFormBody\"><td colspan=\"2\">&nbsp;</td></tr>\n");
		buff.append("</table>\n");

		if (identation) {
			buff.append("</td></tr></table>");			
		}

		return buff.toString();
	}
	
	
	private StringBuffer getHtmlForMultipleChoice(String answer){
		StringBuffer buff = new StringBuffer();
		
		Vector<QuestionAlternativeTO> list = this.getAlterativesList();
		Iterator<QuestionAlternativeTO> i = list.iterator();
		while(i.hasNext()) {
			QuestionAlternativeTO qato = i.next();
			buff.append("  <tr class=\"tableCell\">\n");			
			buff.append("     <td class=\"tableCell\" width=\"20\"><input type=\"radio\" name=\"field_" +  this.getId() + "\" VALUE=\"" +  qato.getSequence() + "\"  " + (qato.getSequence().toString().equals(answer)?"checked":"")  + " /></td>\n");
			buff.append("     <td class=\"tableCell\">" +  qato.getContent() + "</td>\n");
			buff.append("  </tr>\n");						
		}		
		return buff;
	}
}
