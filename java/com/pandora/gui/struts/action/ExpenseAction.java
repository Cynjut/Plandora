package com.pandora.gui.struts.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.AdditionalFieldTO;
import com.pandora.CostTO;
import com.pandora.ExpenseTO;
import com.pandora.MetaFieldTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.delegate.ExpenseDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.MetaFieldNumericTypeException;
import com.pandora.exception.PaidExpenseCannotBeExcludedException;
import com.pandora.gui.struts.form.ExpenseForm;
import com.pandora.gui.struts.form.UserForm;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class ExpenseAction extends GeneralStrutsAction {


	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showExpense";
		try {
			ExpenseForm frm = (ExpenseForm)form;
			this.clearMessages(request);
			this.clearList(request);
		    
			frm.setSaveMethod(UserForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));

			UserTO uto = SessionUtil.getCurrentUser(request);
			frm.setUsername(uto.getName());
			request.getSession().setAttribute("metaFieldList", new Vector<MetaFieldTO>());
			
			String hideClosed = uto.getPreference().getPreference(PreferenceTO.COSTS_HIDE_CLOSED);
			frm.setHideClosedCosts(hideClosed!=null && hideClosed.equals("on"));
			
			this.refreshAuxiliaryLists(frm, request);
			frm.setShowSaveButton("on");
			
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);
	} 


	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showExpense";
		try {
			ExpenseForm frm = (ExpenseForm)form;
			this.refreshAuxiliaryLists(frm, request);			
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);
	}

	
	public ActionForward changeProject(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showExpense";
		try {
			ExpenseForm frm = (ExpenseForm)form;
			this.refreshAuxiliaryLists(frm, request);			
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);
	}	

	public ActionForward showAddCost(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ExpenseForm frm = (ExpenseForm)form;
		try {
			ExpenseTO eto = new ExpenseTO("");
			this.retrieveMetaFieldValue(request, frm, eto);				
			frm.setShowAddForm("on");			
		} catch(MetaFieldNumericTypeException e){
			this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch (Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward("showExpense");
	}
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showExpense";
		try {
			ExpenseForm frm = (ExpenseForm)form;
			this.refreshAuxiliaryLists(frm, request);			
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);
	}
	
	
	@SuppressWarnings("unchecked")
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showExpense";
		
		ExpenseForm frm = (ExpenseForm)form;
		frm.clear();

	    Vector<MetaFieldTO> fieldList = (Vector<MetaFieldTO>)request.getSession().getAttribute("metaFieldList");
	    if (fieldList!=null) {
		    for (MetaFieldTO mto : fieldList) {
		    	AdditionalFieldTO afto = frm.getAdditionalField(mto.getId());
		    	if (afto!=null){
		    		afto.setValue("");	
		    	}				
			}	
	    }
		
		clearList(request);
		this.prepareForm(mapping, form, request, response);
		return mapping.findForward(forward);		
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward removeItem(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showExpense";
		try {
			ExpenseForm frm = (ExpenseForm)form;
			HashMap<String, CostTO> expenseItems = (HashMap<String, CostTO>) request.getSession().getAttribute("expenseItemHash");
			if (expenseItems!=null && frm.getRemoveExpenseId()!=null) {
				expenseItems.remove(frm.getRemoveExpenseId());
			}
			
			this.refreshAuxiliaryLists(frm, request);			
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);		
	}


	public ActionForward saveExpense(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showExpense";		
		String errorMsg = "error.expense.showForm";
		String succeMsg = "message.success";
		ExpenseDelegate edel = new ExpenseDelegate();
		
		try {
		    ExpenseForm frm = (ExpenseForm)form;
		    frm.setShowAddForm("off");
		    frm.setReportURL("");
		    ExpenseTO eto = this.getTransferObjectFromActionForm(frm, request);
		    
			if (frm.getSaveMethod().equals(ExpenseForm.INSERT_METHOD)){
			    errorMsg = "error.expense.insert";
			    succeMsg = "message.insertExpense";
			    edel.insertExpense(eto);
			} else {
				ExpenseTO dbto = edel.getExpense(eto);
				eto.setCreationDate(dbto.getCreationDate());
				
			    errorMsg = "error.expense.update";
			    succeMsg = "message.updateExpense";
			    edel.updateExpense(eto);
			}
		
			this.clear(mapping, form, request, response );				
			this.setSuccessFormSession(request, succeMsg);				

		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);			
		    frm.setSaveMethod(ExpenseForm.INSERT_METHOD, uto);
		    		        			
		} catch(MetaFieldNumericTypeException e){
			this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch(Exception e){
		    this.setErrorFormSession(request, errorMsg, e);
		}	    
		return mapping.findForward(forward);	    
	}
	
	
    public ActionForward editExpense(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showExpense";
	    ExpenseDelegate edel = new ExpenseDelegate();
		try {
			ExpenseForm frm = (ExpenseForm)form;
			frm.setExpenseId(frm.getId());
			
		    ExpenseTO eto = edel.getExpense(new ExpenseTO(frm.getExpenseId()));
			frm.setChangeProject(eto.isWaiting()?"on":"off");
			this.refreshAuxiliaryLists(frm, request);
			
			if (eto.getExpensesItems()!=null) {
				request.getSession().setAttribute("expenseItemList", eto.getExpensesItems());
				HashMap<String, CostTO> hm = new HashMap<String, CostTO>();
				for (CostTO cto : eto.getExpensesItems()) {
					hm.put(cto.getId(), cto);
				}
				request.getSession().setAttribute("expenseItemHash", hm);
			} else {
				request.getSession().setAttribute("expenseItemHash", new HashMap<String, CostTO>());
				request.getSession().setAttribute("expenseItemList", new Vector<CostTO>());
			}
		    
		    this.getActionFormFromTransferObject(eto, frm, request);
		    frm.setSaveMethod(ExpenseForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		    this.summarizeItems(frm, request);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.expense.showForm", e);
		}
		
		return mapping.findForward(forward);				
	}
    

	public ActionForward removeExpense(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showExpense";
		try {
			ExpenseForm frm = (ExpenseForm)form;
			frm.setShowAddForm("off");
			frm.setReportURL("");
			frm.setExpenseId(frm.getId());
			ExpenseDelegate edel = new ExpenseDelegate();
			
			ExpenseTO eto = new ExpenseTO(frm.getExpenseId());
			edel.removeExpense(eto);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeExpense");
			this.refresh(mapping, form, request, response );
			
		    //set the current user connected
			UserTO uto = SessionUtil.getCurrentUser(request);
		    frm.setSaveMethod(ExpenseForm.INSERT_METHOD, uto);
		    
		} catch(PaidExpenseCannotBeExcludedException e){    
			this.setErrorFormSession(request, "error.expense.paidRemove", e);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.expense.remove", e);
		}
	    
		return mapping.findForward(forward);	    
	}

	public ActionForward showReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ActionForward fwd = null;	
		try {
			fwd = this.navigate(mapping, form, request, response);	
			ExpenseForm frm = (ExpenseForm)form;
			UserDelegate udel = new UserDelegate();
			UserTO root = udel.getRoot();
			String url = root.getPreference().getPreference(PreferenceTO.EXPENSE_REPORT_URL);
			url = url.replaceAll("#EXPENSE_ID#", frm.getExpenseId());
			frm.setReportURL(url);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}
		return fwd;	
	}
	
	
	private void refreshAuxiliaryLists(ExpenseForm frm, HttpServletRequest request) throws BusinessException {
		ProjectDelegate pdel = new ProjectDelegate();
		ExpenseDelegate edel = new ExpenseDelegate();
		MetaFieldDelegate mfdel = new MetaFieldDelegate();
	    PreferenceDelegate pfdel = new PreferenceDelegate();
	    
	    //save the new preference
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    PreferenceTO pref = new PreferenceTO(PreferenceTO.COSTS_HIDE_CLOSED, frm.getHideClosedCosts()?"on":"off", uto);
		uto.getPreference().addPreferences(pref);
		pref.addPreferences(pref);
		pfdel.insertOrUpdate(pref);
		
		//if project is already closed, the save button must be hidden
		if (frm.getProjectId()!=null && !frm.getProjectId().trim().equals("")) {
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
			if (pto.getFinalDate()!=null) {
				frm.setShowSaveButton("off");	
			} else {
				frm.setShowSaveButton("on");
			}
		} else {
			frm.setShowSaveButton("off");
		}
		
		frm.setShowAddForm("off");
		frm.setReportURL("");
		
		Vector<ProjectTO> prjList = pdel.getProjectListForWork(SessionUtil.getCurrentUser(request), true, true);
		request.getSession().setAttribute("expenseProjectList", prjList);
		this.checkMissingProject(frm.getProjectId(), request);
		
		Vector<ExpenseTO> expList = edel.getExpenseList(uto.getId(), frm.getHideClosedCosts());
		request.getSession().setAttribute("expenseList", expList);
		
		Vector<MetaFieldTO> mflist = mfdel.getListByProjectAndContainer(frm.getProjectId(), null, MetaFieldTO.APPLY_TO_EXPENSE);
		request.getSession().setAttribute("metaFieldList", mflist);
		
		this.summarizeItems(frm, request);
	}


	@SuppressWarnings("unchecked")
	private void summarizeItems(ExpenseForm frm, HttpServletRequest request) throws BusinessException {
		UserDelegate udel = new UserDelegate();
		long acc = 0;
		Locale currLoc = udel.getCurrencyLocale();
		
		HashMap<String, CostTO> expenseItems = (HashMap<String, CostTO>) request.getSession().getAttribute("expenseItemHash");
		if (expenseItems==null || expenseItems.size()==0) {
			expenseItems = new HashMap<String,CostTO>();
			request.getSession().setAttribute("expenseItemList", new Vector<CostTO>());
		} else {
			Vector<CostTO> exp = new Vector<CostTO>();
			Iterator<CostTO> i = expenseItems.values().iterator();
			while(i.hasNext()) {
				CostTO cto = i.next();
				if (cto.getTotalValue()!=null) {
					acc += cto.getTotalValue().longValue();	
				}
				exp.add(cto);
			}
			request.getSession().setAttribute("expenseItemList", exp);
		}
		
		String totalStr = StringUtil.getCurrencyValue((float)((float)acc/100), currLoc);
		frm.setTotalExpense(totalStr);
	}	

	
	@SuppressWarnings("unchecked")
	private ExpenseTO getTransferObjectFromActionForm(ExpenseForm frm,	HttpServletRequest request) throws BusinessException {
	    ExpenseTO eto = new ExpenseTO();
	    
	    UserTO uto = SessionUtil.getCurrentUser(request);
        eto.setId(frm.getExpenseId());
        eto.setProject(new ProjectTO(frm.getProjectId()));
        eto.setUser(uto);
	    eto.setDescription(frm.getComment());
	    eto.setExpensesItems((Vector<CostTO>)request.getSession().getAttribute("expenseItemList"));

	    this.retrieveMetaFieldValue(request, frm, eto);				
	    
		return eto;
	}
	
	
	private void getActionFormFromTransferObject(ExpenseTO to, ExpenseForm frm, HttpServletRequest request) throws BusinessException{
	    frm.setExpenseId(to.getId());
	    frm.setUsername(to.getUser().getName());
	    frm.setComment(to.getDescription());
	    frm.setProjectId(to.getProject().getId());
	    frm.setAdditionalFields(to.getAdditionalFields());
	    this.checkMissingProject(to.getProject().getId(), request);
	}

	
    //if related project_id is not into project_list (probably because the user was disabled)
    //insert the missing project at project combo
	@SuppressWarnings("unchecked")
	private void checkMissingProject(String currentProjectId, HttpServletRequest request) throws BusinessException{
		ProjectDelegate pdel = new ProjectDelegate();
		
		if (currentProjectId!=null && !currentProjectId.trim().equals("")) {
		    boolean found = false;
	    	Vector<ProjectTO> prjList = (Vector<ProjectTO>)request.getSession().getAttribute("expenseProjectList");
	    	for (ProjectTO pto : prjList) {
				if (pto.getId().equals(currentProjectId)) {
					found = true;
					break;
				}
			}
	    	if (!found){
	    		ProjectTO newPrj = new ProjectTO(currentProjectId); 
	    		newPrj = pdel.getProjectObject(newPrj, true);
	    		prjList.add(newPrj);
	    		request.getSession().setAttribute("expenseProjectList", prjList);
	    	}					
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void retrieveMetaFieldValue(HttpServletRequest request, ExpenseForm frm, ExpenseTO eto) throws MetaFieldNumericTypeException {
		Vector<MetaFieldTO> metaFieldList = (Vector<MetaFieldTO>)request.getSession().getAttribute("metaFieldList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, eto);	    
		if (metaFieldList!=null) {
			frm.setAdditionalFields(eto.getAdditionalFields());
		}
	}

	
	private void clearList(HttpServletRequest request) {
		request.getSession().setAttribute("expenseItemHash", new HashMap<String, CostTO>());
		request.getSession().setAttribute("expenseItemList", new Vector<CostTO>());
		request.getSession().setAttribute("expenseList", new Vector<ExpenseTO>());
    	request.getSession().setAttribute("metaFieldList", new Vector<MetaFieldTO>());	
	}
	
}