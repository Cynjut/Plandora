package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.CostInstallmentTO;
import com.pandora.CostStatusTO;
import com.pandora.CostTO;
import com.pandora.ExpenseTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.CostDelegate;
import com.pandora.delegate.CostStatusDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.exception.DifferentCostAccountNumberException;
import com.pandora.gui.struts.exception.DifferentCostCategoryException;
import com.pandora.gui.struts.form.CostListForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

public class CostListAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostList";
		ProjectDelegate pdel = new ProjectDelegate();
		try {
			CostListForm frm = (CostListForm)form;

			UserTO uto = SessionUtil.getCurrentUser(request);	
			frm.setSaveMethod(null, uto);

			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), false);
			boolean isAllowed = (pto.isLeader(uto.getId()));

			if (!isAllowed ) {
				UserDelegate udel = new UserDelegate();
				ResourceTO rto = new ResourceTO(uto.getId());
				rto.setProject(pto);
				rto = udel.getResource(rto);
				isAllowed = rto.getBoolCanSeeInvoice();
			}

			if (isAllowed) {
				frm.setInitialDate(DateUtil.getDate(DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, -30), uto.getCalendarMask(), uto.getLocale()));
				frm.setFinalDate(DateUtil.getDate(DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, 90), uto.getCalendarMask(), uto.getLocale()));
				//this.loadPreferences(form, request);
				this.refreshForm(form, request);								
			} else {
				this.setErrorFormSession(request, "validate.project.userNotLeader", null);
			}

		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);	
		}

		return mapping.findForward(forward);
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward updateInBatch(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostList";
		CostDelegate cstdel = new CostDelegate();
		try {
			CostListForm frm = (CostListForm)form;
			UserTO uto = SessionUtil.getCurrentUser(request);
			
			boolean saveProcessed = false;
			this.validateBeforeUpdateInBatch(request);

			Vector<CostInstallmentTO> costs = (Vector<CostInstallmentTO>) request.getSession().getAttribute("prjCostList");
			if (costs!=null) {
				for (CostInstallmentTO cito : costs) {
		        	boolean updateCost = false;
		        	boolean updateInst = false;

		        	String newCategory = request.getParameter("cb_" + cito.getId() + "_category");
		        	String newAccCode = request.getParameter("cb_" + cito.getId() + "_acccode");
		        	String newStatus = request.getParameter("cb_" + cito.getId() + "_status");
					
		        	if (newCategory!=null && !newCategory.equals(cito.getCost().getCategory().getName())) {
		        		ProjectTO pto = new ProjectTO(frm.getProjectId());
		        		CategoryTO newCatTO = this.getCategoryFromName(pto, newCategory);
		        		if (newCatTO!=null) {
		        			cito.getCost().setCategory(newCatTO);
		        			updateCost = true;
		        		}
		        	}

		        	if (newAccCode!=null && !newAccCode.equals(cito.getCost().getAccountCode())) {
		        		cito.getCost().setAccountCode(newAccCode);
		        		updateCost = true;
		        	}

		        	if (newStatus!=null && !newStatus.trim().equals("") &&
		        			!newStatus.equals(cito.getCostStatus().getId())) {
		        		CostStatusTO newSt = getStatus(newStatus, request);
		        		cito.setCostStatus(newSt);
		    			ExpenseTO eto = cito.getCost().getExpense();
		    			if (eto!=null) {
		    				eto.setRefuserAprroverId(uto.getId());	
		    			}
		        		updateInst = true;
		        	}

		        	if (updateCost) {
		        		cstdel.liteUpdateCost(cito.getCost());
		        		cstdel.insertCostHistory(cito.getCost(), cito);	
		        		saveProcessed = true;
		        	}

		        	if (updateInst) {
		        		cstdel.liteUpdateInstallment(cito, uto);
		        		saveProcessed = true;
		        	}
		        	
				}
			}

			if (saveProcessed) {
				this.setSuccessFormSession(request, "message.costlist.success");	
			} else {
				this.setSuccessFormSession(request, "message.costlist.notsaved");
			}
			
			this.refreshForm(form, request);
		
			
		} catch(DifferentCostCategoryException e){
			this.setErrorFormSession(request, "validate.costlist.diffCat", e);
		} catch(DifferentCostAccountNumberException e){
			this.setErrorFormSession(request, "validate.costlist.diffAcc", e);
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}

		return mapping.findForward(forward);
	}


	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostList";
		this.refreshForm(form, request);		
		return mapping.findForward(forward);
	}


	private void refreshForm(ActionForm form, HttpServletRequest request) {
		CostDelegate cdel = new CostDelegate();
		CostStatusDelegate csdel = new CostStatusDelegate();
		
		try {
			CostListForm frm = (CostListForm)form;
			ProjectTO pto = new ProjectTO(frm.getProjectId());
			UserTO uto = SessionUtil.getCurrentUser(request);
			
			Timestamp iniDate = DateUtil.getDateTime(frm.getInitialDate(), uto.getCalendarMask(), uto.getLocale());
			Timestamp finalDate = DateUtil.getDateTime(frm.getFinalDate(), uto.getCalendarMask(), uto.getLocale());
			
			Vector<CategoryTO> catlist = this.getListOfCategoriesForCombo(pto, request);
			request.getSession().setAttribute("costCategoryList", catlist);
			
			Vector<TransferObject> stsList = new Vector<TransferObject>();
			stsList.add(new TransferObject("0", super.getBundleMessage(request, "label.all")));
			stsList.add(new TransferObject("-1", super.getBundleMessage(request, "label.costlist.statlist.1")));
			stsList.add(new TransferObject("-2", super.getBundleMessage(request, "label.costlist.statlist.2")));
			request.getSession().setAttribute("costStatusFilterList", stsList);
			
			Vector<TransferObject> acclist = cdel.getAccountCodesByLeader(uto);
			request.getSession().setAttribute("allCostAccountCodeList", acclist);
			
			Vector<CostStatusTO> cslist = csdel.getCostStatusList(CostStatusTO.STATE_MACHINE_WAITING);
			request.getSession().setAttribute("allCostStatusList", cslist );			
			
			Vector<CostTO> clist = cdel.getListByProject(pto, true, iniDate, finalDate);
			Vector<CostInstallmentTO> list = this.filterInstallment(clist, frm);
			request.getSession().setAttribute("prjCostList", list);			
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);	
		}
	}
	

	public ActionForward showExpenseReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		UserDelegate udel = new UserDelegate();
		CostListForm frm = (CostListForm)form;
		
		try {	
			UserTO root = udel.getRoot();
			String url = root.getPreference().getPreference(PreferenceTO.EXPENSE_REPORT_URL);
			url = url.replaceAll("#EXPENSE_ID#", frm.getExpenseId());
			frm.setExpenseReportURL(url);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}	    
		return mapping.findForward("showCostList");
	}	
	
	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showCostList");
	}

	
	private Vector<CostInstallmentTO> filterInstallment(Vector<CostTO> clist, CostListForm frm) throws BusinessException {
		Vector<CostInstallmentTO> response = new Vector<CostInstallmentTO>();
		if (clist!=null) {
			
			UserDelegate udel = new UserDelegate();
			Locale loc = udel.getCurrencyLocale();
			
			for (CostTO cto : clist) {
				boolean category = (frm.getCategoryName()==null || frm.getCategoryName().equals("-1") || cto.getCategory().getName().equals(frm.getCategoryName()));
				
				if (cto.getInstallments()!=null && category) {
					
					for (CostInstallmentTO cito : cto.getInstallments()) {
						
						CostStatusTO csto = cito.getCostStatus();
						boolean isWait = csto.getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_WAITING);
						boolean isPaid = csto.getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_PAID);
						boolean isCanc = csto.getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_CANCELED);
						
						boolean status = (frm.getStatusId()==null || frm.getStatusId().equals("0")	|| (frm.getStatusId().equals("-1") && isWait) ||
										 (frm.getStatusId().equals("-2") && !isPaid && !isCanc) );

						if (status) {
							cito.setShowCurrencyLocale(loc);
							response.addElement(cito);							
						}
					}
				}
			}			
		}
		return response;
	}
	
	
	private CategoryTO getCategoryFromName(ProjectTO pto, String name) throws BusinessException{
		CategoryTO response = null;
		CategoryDelegate catDel = new CategoryDelegate();
		Vector<CategoryTO> categList = catDel.getCategoryListByType(CategoryTO.TYPE_COST, pto, true);
		if (categList!=null) {
			for (CategoryTO cto : categList) {
				if (cto.getName().equals(name)) {
					response = cto;
				}
			}
		}
		return response;
	}

	
	@SuppressWarnings("unchecked")
	private CostStatusTO getStatus(String statusId, HttpServletRequest request) throws BusinessException{
		CostStatusTO response = null;
		Vector<CostStatusTO> csList = (Vector<CostStatusTO>)request.getSession().getAttribute("allCostStatusList");
		if (csList!=null) {
			for (CostStatusTO csto : csList) {
				if (csto.getId().equals(statusId)) {
					response = csto;
				}
			}
		}
		return response;
	}

	
	private Vector<CategoryTO> getListOfCategoriesForCombo(ProjectTO pto, HttpServletRequest request) throws BusinessException{
		HashMap<String, CategoryTO> uniqueList = new HashMap<String, CategoryTO>();
		
		CategoryDelegate catDel = new CategoryDelegate();
		Vector<CategoryTO> categList = catDel.getCategoryListByType(CategoryTO.TYPE_COST, pto, true);
		if (categList!=null) {
			for (CategoryTO cto : categList) {
				if (uniqueList.get(cto.getName())==null) {
					cto.setId(cto.getName());
					cto.setGenericTag(cto.getName());
					uniqueList.put(cto.getName(), cto);					
				}
			}
		}
		
		Vector<CategoryTO> temp = new Vector<CategoryTO>();
		temp.addAll(uniqueList.values());
		request.getSession().setAttribute("allCostCategoryList", temp);
		
		temp = new Vector<CategoryTO>();
		CategoryTO fakeCategory = new CategoryTO("-1");
		fakeCategory.setName(this.getBundleMessage(request, "label.all"));
		temp.addElement(fakeCategory);

		temp.addAll(uniqueList.values());
		
		return temp;
	}


	
	@SuppressWarnings("unchecked")
	private void validateBeforeUpdateInBatch(HttpServletRequest request) throws DifferentCostCategoryException, DifferentCostAccountNumberException{
		HashMap<String, String> categoryHm = new HashMap<String, String>();
		HashMap<String, String> accountHm = new HashMap<String, String>();
		
		Vector<CostInstallmentTO> costs = (Vector<CostInstallmentTO>) request.getSession().getAttribute("prjCostList");
		if (costs!=null) {
			for (CostInstallmentTO cito : costs) {
	        	String newCategory = request.getParameter("cb_" + cito.getId() + "_category");
	        	String newAccCode = request.getParameter("cb_" + cito.getId() + "_acccode");
	        	String costId = cito.getCost().getId();
	        	
	        	String cat = categoryHm.get(costId); 
	        	if (cat==null) {
	        		categoryHm.put(costId, newCategory);
	        	} else {
	        		if (!cat.equals(newCategory)) {
	        			throw new DifferentCostCategoryException();
	        		}
	        	}
	        	
	        	
	        	String acc = accountHm.get(costId); 
	        	if (acc==null) {
	        		accountHm.put(costId, newAccCode);
	        	} else {
	        		if (!acc.equals(newAccCode)) {
	        			throw new DifferentCostAccountNumberException();
	        		}
	        	}
	        	
			}
		}
	}
	
	
}
