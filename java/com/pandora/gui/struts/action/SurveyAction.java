package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ReportTO;
import com.pandora.SurveyQuestionTO;
import com.pandora.SurveyTO;
import com.pandora.UserTO;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.SurveyDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.SurveyContainAnswerException;
import com.pandora.gui.struts.form.SurveyForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;


public class SurveyAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showSurvey";
		this.clearForm(form, request);
		this.reset(form);
		
		SurveyForm frm = (SurveyForm)form;
		
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    String hideClosedSur = uto.getPreference().getPreference(PreferenceTO.SURVEY_HIDE_CLOSED);
	    frm.setHideClosedSurveys(hideClosedSur!=null && hideClosedSur.equals("on"));
		
		this.refresh(mapping, form, request, response);
		request.getSession().setAttribute("questionList", new Vector<SurveyQuestionTO>());
		frm.setPathContext(request.getHeader("referer"), request.getContextPath());
		
	    frm.setSaveMethod(SurveyForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));

		return mapping.findForward(forward);		
	}

	
	public ActionForward showReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ActionForward fwd = null;
		
		try {	    
			SurveyForm frm = (SurveyForm)form;
			UserDelegate udel = new UserDelegate();
			UserTO root = udel.getRoot();
			String url = root.getPreference().getPreference(PreferenceTO.SURVEY_REPORT_URL);
			url = url.replaceAll(ReportTO.PROJECT_ID, frm.getProjectId());
			url = url.replaceAll("#SURVEY_ID#", frm.getId());
			
			fwd = this.refresh(mapping, form, request, response); 
			frm.setReportURL(url);
			frm.setShowEditQuestion("off");
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}
		return fwd;	
	}

	
	public ActionForward showReplicationPopup(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		this.reset(form);
		return mapping.findForward("showReplicationSurvey");
	}

	
	public ActionForward replicate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		SurveyDelegate del = new SurveyDelegate();
		
		try {
			SurveyForm frm = (SurveyForm)form;
			this.reset(form);
			
			//fetch the source survey from data base...
			SurveyTO source = del.getSurvey(new SurveyTO(frm.getId()));
			if (source!=null) {
				source.setId(null); 
				source.setAnonymousKey(null);
				source.setOwner(SessionUtil.getCurrentUser(request));
				source.setCreationDate(DateUtil.getNow());
				
				String label = super.getBundleMessage(request, "message.formSurvey.replicSurvey.copy");
				String copyName = label + " " + source.getName();
				if (copyName!=null && copyName.length()>=50) {
					copyName = copyName.substring(0, 49);					
				}
				source.setName(copyName);
				
				source.setProject(new ProjectTO(frm.getProjectId()));
				
				Vector<SurveyQuestionTO> qlist = source.getQuestionList();
				Iterator<SurveyQuestionTO> i = qlist.iterator();
				while(i.hasNext()) {
					SurveyQuestionTO q = i.next();
					q.setId("NEW_");
				}
				
				del.insertSurvey(source);

				this.refresh(mapping, form, request, response);
				this.setSuccessFormSession(request, "message.insertSurvey");				
			}

		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}
		
		return mapping.findForward("showSurvey");
	}
	
	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		this.reset(form);
		return mapping.findForward("showSurvey");
	}
	
	
	public ActionForward showEditQuestion(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		SurveyForm frm = (SurveyForm)form;
		
		String editQuestionId = frm.getEditQuestionId();
		if (editQuestionId!=null && !editQuestionId.trim().equals("")) {
			frm.addQuestionsToBeUpdated(editQuestionId);
		}
		
		frm.setShowEditQuestion("on");
		return mapping.findForward("showSurvey");
	}
	
	
	public ActionForward removeQuestion(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showSurvey";

	    try {
		    this.clearMessages(request);
		    SurveyForm frm = (SurveyForm)form;
		    this.reset(form);

		    @SuppressWarnings({ "rawtypes", "unchecked" })
			Vector<SurveyQuestionTO> qList = (Vector)request.getSession().getAttribute("questionList");
		    if (qList!=null && frm.getRemovedQuestionId()!=null){
		        Iterator<SurveyQuestionTO> i = qList.iterator();
		        while(i.hasNext()){
		            SurveyQuestionTO qto = i.next();
		            if (qto.getId().equals(frm.getRemovedQuestionId())){
		                request.getSession().removeAttribute("questionList");
		                qList.remove(qto);
		                Vector<SurveyQuestionTO> temp = new Vector<SurveyQuestionTO>();
		                temp.addAll(qList);
		                request.getSession().setAttribute("questionList", temp);
	
		                frm.addQuestionsToBeRemoved(qto.getId());
		                
		                break;
		            }	            
		        }
		    }	    
	    } catch(Exception e){
	        this.setErrorFormSession(request, "error.formSurvey.showForm", e);
	    }
	    return mapping.findForward(forward);
	}
	
	

	public ActionForward saveSurvey(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showSurvey";
		String errorMsg = "error.formSurvey.showForm";
		String succeMsg = "message.success";

		try {
			SurveyForm frm = (SurveyForm)form;
			this.reset(form);

			SurveyDelegate del = new SurveyDelegate();
		    SurveyTO sto = this.getTransferObjectFromActionForm(frm, request);

		    if (frm.getSaveMethod().equals(SurveyForm.INSERT_METHOD)){
			    errorMsg = "error.formSurvey.insert";
			    succeMsg = "message.insertSurvey";
			    del.insertSurvey(sto);
			} else {
			    errorMsg = "error.formSurvey.update";
			    succeMsg = "message.updateSurvey";
			    del.updateSurvey(sto);
			}
		
			this.clear(mapping, form, request, response );				
			this.setSuccessFormSession(request, succeMsg);				

		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);			
		    frm.setSaveMethod(SurveyForm.INSERT_METHOD, uto);
		        			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}	    
		return mapping.findForward(forward);	    
	}


	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showSurvey";
	    PreferenceDelegate pdel = new PreferenceDelegate();
		try {
			SurveyForm frm = (SurveyForm)form;
			this.reset(form);
		    SurveyDelegate del = new SurveyDelegate();

		    //save the new preference
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    PreferenceTO pto = new PreferenceTO(PreferenceTO.SURVEY_HIDE_CLOSED, frm.getHideClosedSurveys()?"on":"off", uto);
			uto.getPreference().addPreferences(pto);
			pto.addPreferences(pto);
            pdel.insertOrUpdate(pto);	
            
		    if (frm.getProjectId()!=null && !frm.getProjectId().equals("")) {
				Vector<SurveyTO> sList = del.getSurveyList(frm.getProjectId(), frm.getHideClosedSurveys());
				request.getSession().setAttribute("surveyList", sList);		    	

				Vector<SurveyTO> repList = del.getSurveyListByUser(uto, false);
				ShowSurveyAction saction = new ShowSurveyAction();
				String content = saction.getHtmlCombo(request, repList);
				frm.setReplicationHtmlList(content);
				
		    } else {
		    	request.getSession().setAttribute("surveyList", new Vector<SurveyTO>());
		    }
				
			
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}

	
	public ActionForward editSurvey(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showSurvey";
	    SurveyDelegate del = new SurveyDelegate();
		
		try {
			SurveyForm frm = (SurveyForm)form;
			this.reset(form);

			SurveyTO sto = del.getSurvey(new SurveyTO(frm.getId()));
		    
	    	frm.setShowSaveConfirmation(sto.checkAnswer()?"on":"off");		    
		    this.getActionFormFromTransferObject(sto, frm, request);
		    
		    request.getSession().setAttribute("questionList", sto.getQuestionList());
		    
			//set current operation status for Updating	
		    frm.setSaveMethod(SurveyForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}
		
		return mapping.findForward(forward);				
	}
    

	public ActionForward removeSurvey(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showSurvey";
		try {
			SurveyForm frm = (SurveyForm)form;
			this.reset(form);

			SurveyDelegate del = new SurveyDelegate();
			
			SurveyTO sto = new SurveyTO();
			sto.setId(frm.getId());
			del.removeSurvey(sto);
			
			this.clearForm(frm, request);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.deleteSurvey");
			this.refresh(mapping, form, request, response );
			
		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);
		    frm.setSaveMethod(SurveyForm.INSERT_METHOD, uto);
		
		} catch (SurveyContainAnswerException e) {
			this.setErrorFormSession(request, "error.formSurvey.answerremove", e);
			
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}

		
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showSurvey";
		SurveyForm frm = (SurveyForm)form;
		frm.clearQuestionsToBeRemoved();
		frm.clearQuestionsToBeUpdated();
		this.prepareForm(mapping, form, request, response);
		return mapping.findForward(forward);		
	}

	
	private void clearForm(ActionForm form, HttpServletRequest request){
	    SurveyForm frm = (SurveyForm)form;		
	    frm.clear();
	    this.clearMessages(request);
	}


	private void getActionFormFromTransferObject(SurveyTO to, SurveyForm frm, HttpServletRequest request){
		Locale loc = SessionUtil.getCurrentLocale(request);
	    frm.setId(to.getId());
	    frm.setDescription(to.getDescription());
	    if (to.getFinalDate()!=null) {
		    frm.setFinalDate(DateUtil.getDate(to.getFinalDate(), super.getCalendarMask(request), loc));
	    } else {
	    	frm.setFinalDate(null);	
	    }
	    frm.setIsAnonymous(to.getIsAnonymous().booleanValue());
	    frm.setIsTemplate(to.getIsTemplate().booleanValue());
	    frm.setName(to.getName());
	    frm.setProjectId(to.getProject().getId());
	    if (to.getPublishingDate()!=null) {
	    	frm.setPublishingDate(DateUtil.getDate(to.getPublishingDate(), super.getCalendarMask(request), loc));	
	    } else {
	    	frm.setPublishingDate(null);
	    }
	    frm.setKey(to.getAnonymousKey());
	    frm.clearQuestionsToBeRemoved();
	    frm.clearQuestionsToBeUpdated();
	    to.setQuestionsToBeRemoved(null);
	    to.setQuestionsToBeUpdated(null);
	}

	
	private SurveyTO getTransferObjectFromActionForm(SurveyForm frm, HttpServletRequest request){
		SurveyTO sto = new SurveyTO();
		Locale loc = SessionUtil.getCurrentLocale(request);
        sto.setId(frm.getId());
        sto.setAnonymousKey("");
        sto.setCreationDate(DateUtil.getNow());
        sto.setDescription(frm.getDescription());
        sto.setFinalDate(DateUtil.getDateTime(frm.getFinalDate(), super.getCalendarMask(request), loc));
        sto.setIsAnonymous(new Boolean(frm.getIsAnonymous()));
        sto.setIsTemplate(new Boolean(false));
        sto.setName(frm.getName());
        sto.setProject(new ProjectTO(frm.getProjectId()));
        sto.setPublishingDate(DateUtil.getDateTime(frm.getPublishingDate(), super.getCalendarMask(request), loc));
        sto.setOwner(SessionUtil.getCurrentUser(request));
        sto.setQuestionsToBeRemoved(frm.getQuestionsToBeRemoved());
        sto.setQuestionsToBeUpdated(frm.getQuestionsToBeUpdated());
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
		Vector<SurveyQuestionTO> qList = (Vector)request.getSession().getAttribute("questionList");
        sto.setQuestionList(qList);

        return sto;
	}

	
	private void reset(ActionForm form){
		SurveyForm frm = (SurveyForm)form;
		frm.setReportURL("");
		frm.setShowEditQuestion("off");
		frm.setEditQuestionId("");
	}
}
