package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.CostTO;
import com.pandora.LeaderTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceCapacityTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.PreferenceBUS;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.CostDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ResourceCapacityDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.CostForm;
import com.pandora.gui.taglib.decorator.ExpenseReportDecorator;
import com.pandora.gui.taglib.form.NoteIcon;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class CostAction extends GeneralStrutsAction {

	private final static int COLUMN_WIDTH = 110; 
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostPanel";
		ProjectDelegate pdel = new ProjectDelegate();
		try {
			CostForm frm = (CostForm)form;
			frm.setShowEditCost("off");
			frm.setExpenseReportURL("");

			UserTO uto = SessionUtil.getCurrentUser(request);			
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
				this.loadPreferences(form, request);
				this.refresh(mapping, form, request, response);								
			} else {
				this.setErrorFormSession(request, "validate.project.userNotLeader", null);
			}
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);	
		}

		return mapping.findForward(forward);
	}

	
	public ActionForward showExpenseReport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		UserDelegate udel = new UserDelegate();		
		try {
			CostForm frm = (CostForm)form;
			UserTO root = udel.getRoot();
			String url = root.getPreference().getPreference(PreferenceTO.EXPENSE_REPORT_URL);
			url = url.replaceAll("#EXPENSE_ID#", frm.getExpenseId());
			frm.setExpenseReportURL(url);
			frm.setShowEditCost("off");
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formSurvey.showForm", e);
		}	    
		return mapping.findForward("showCostPanel");
	}	


	public ActionForward renderChart(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		CostForm frm = (CostForm)form;
		frm.setShowEditCost("on");
		frm.setExpenseReportURL("");
		return null;
	}

	
	public ActionForward showEditPanel(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostPanel";
		CostForm frm = (CostForm)form;
		frm.setShowEditCost("on");
		frm.setExpenseReportURL("");
		return mapping.findForward(forward);
	}


	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostPanel";
		try {
			CostForm frm = (CostForm)form;			
			this.refreshAuxiliaryLists(frm, request);	
			this.savePreferences(form, request);
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);	
		}
		return mapping.findForward(forward);		
	}

	
	public ActionForward removeCost(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostPanel";
		CostDelegate cdel = new CostDelegate();
		try {
			CostForm frm = (CostForm)form;
			cdel.removeCost(new CostTO(frm.getId()));
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeCost");
			this.refresh(mapping, form, request, response );
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);	
		}
		return mapping.findForward(forward);		
	}	

	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostPanel";
		try {
			CostForm frm = (CostForm)form;
			frm.setShowEditCost("off");
			frm.setExpenseReportURL("");
			this.refresh(mapping, form, request, response );
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);
	}
	
	
	public ActionForward refuseCost(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return this.refuseOrApprove("message.refuseExpense", true, form, mapping, request);
	}

	
	public ActionForward approveCost(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return this.refuseOrApprove("message.approveExpense", false, form, mapping, request);
	}
	
	
	private ActionForward refuseOrApprove(String successMsg, boolean isRefusing, 
			ActionForm form, ActionMapping mapping, HttpServletRequest request){
		String forward = "home";
		CostDelegate cdel = new CostDelegate();
		ResourceHomeAction homeAct = new ResourceHomeAction();
		try {
			CostForm frm = (CostForm)form;
			CostTO cto = cdel.getCost(new CostTO(frm.getId()));
			UserTO uto = SessionUtil.getCurrentUser(request);
			if (checkIfLeader(cto, request)) {
				if (isRefusing) {
					cdel.refuseCost(cto, uto);
				} else {
					cdel.approveCost(cto, uto);
				}
				homeAct.refreshCosts(request);
				this.setSuccessFormSession(request, successMsg);
			} else {
				this.setErrorFormSession(request, "error.expense.permission", null);
			}
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);		
	}
	
	
	private boolean checkIfLeader(CostTO cto, HttpServletRequest request){
		boolean response = false;
		UserDelegate udel = new UserDelegate();
		LeaderTO lead = null;
		try {		
			UserTO uto = SessionUtil.getCurrentUser(request);
			lead = new LeaderTO(uto.getId());
			lead.setProject(cto.getProject());
			lead = udel.getLeader(lead);
		} catch (BusinessException e) {
			lead = null;
		}
		response = (lead!=null);
		
		return response;
	}
	
	
	private void refreshAuxiliaryLists(CostForm frm, HttpServletRequest request) throws Exception {

		frm.setShowEditCost("off");
		frm.setExpenseReportURL("");
		
		if (frm.getProjectId()!=null && frm.getType()!=null) {			
			StringBuffer bodyList = new StringBuffer();
			StringBuffer leftList = this.getLeftList(frm, request, bodyList);
			frm.setCostHtmlTitle(leftList.toString());
			frm.setCostHtmlBody(bodyList.toString());
		}
		
		Vector<TransferObject> types = new Vector<TransferObject>();
		types.add(new TransferObject(CostForm.TYPE_PROJECT, super.getBundleMessage(request, "label.cost.type.1")));
		types.add(new TransferObject(CostForm.TYPE_ACCOUNT_CODE, super.getBundleMessage(request, "label.cost.type.2")));
		types.add(new TransferObject(CostForm.TYPE_CATEGORY, super.getBundleMessage(request, "label.cost.type.3")));
		request.getSession().setAttribute("costPanelTypes", types);

		Vector<TransferObject> gran = new Vector<TransferObject>();
		for(int i=1; i<=6; i++) {
			gran.add(new TransferObject(""+i, super.getBundleMessage(request, "label.cost.gran." + i)));
		}
		request.getSession().setAttribute("costPanelGran", gran);

		Vector<TransferObject> modes = new Vector<TransferObject>();
		modes.add(new TransferObject(CostForm.MODE_ALL, super.getBundleMessage(request, "label.cost.viewMode.all")));
		modes.add(new TransferObject(CostForm.MODE_ONLY_EXPENSES, super.getBundleMessage(request, "label.cost.viewMode.onlyExp")));
		modes.add(new TransferObject(CostForm.MODE_ONLY_RESOURCES, super.getBundleMessage(request, "label.cost.viewMode.onlyRes")));
		request.getSession().setAttribute("costPanelViewModes", modes);					
	}


	private StringBuffer getLeftList(CostForm frm, HttpServletRequest request, StringBuffer bodyList) throws Exception {
		CostDelegate cdel = new CostDelegate();		
		StringBuffer leftList = new StringBuffer();
		StringBuffer body = new StringBuffer();
		ExpenseReportDecorator expDec = new ExpenseReportDecorator(); 
		
		UserTO uto = SessionUtil.getCurrentUser(request);			
		Vector<TransferObject> list = this.getParentList(frm, uto);

		Timestamp iniDate = DateUtil.getDateTime(frm.getInitialDate(), uto.getCalendarMask(), uto.getLocale());
		Timestamp finalDate = DateUtil.getDateTime(frm.getFinalDate(), uto.getCalendarMask(), uto.getLocale());
		
		StringBuffer bodyTitle = new StringBuffer();
		int colNumber = this.getBodyTitle(frm, uto, bodyTitle);
		body.append("<table width=\"" + (colNumber * COLUMN_WIDTH) + "\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">" + bodyTitle + "</table>");

		//write a empty space at the top of left titles...
		leftList.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
		leftList.append("<tr class=\"tableRowOdd\" height=\"21\"><td width=\"350\">&nbsp;</td></tr>");
		leftList.append("</table>");

		Timestamp iniRange = iniDate;
		Timestamp finalRange = finalDate;
		if (!frm.getHideOutOfRange()) {
			iniRange = null;
			finalRange = null;
		}
		
		Iterator<TransferObject> it = list.iterator();
		while(it.hasNext()) {
			TransferObject to = it.next();
			Vector<CostTO> costList = null;

			String key = to.getId();
			if (frm.getType().equals(CostForm.TYPE_PROJECT)) {
				costList = cdel.getListByProject(new ProjectTO(to.getId()), false, iniRange, finalRange);	
			} else if (frm.getType().equals(CostForm.TYPE_ACCOUNT_CODE)) {
				costList = cdel.getListByAccountCode(to.getId(), iniRange, finalRange);
				key = to.hashCode()+"";
			} else if (frm.getType().equals(CostForm.TYPE_CATEGORY)) {
				costList = cdel.getListByCategory(new CategoryTO(to.getId()), iniRange, finalRange);
			}

			HashMap<String, Integer> timesheet = new HashMap<String, Integer>();
			StringBuffer sbHR = null;
			if ((frm.getViewMode().equals(CostForm.MODE_ALL) || frm.getViewMode().equals(CostForm.MODE_ONLY_RESOURCES)) && frm.getType().equals(CostForm.TYPE_PROJECT)) {
				sbHR = this.getHumanResourceCost(frm, to.getId(), uto, timesheet, iniDate, finalDate);
			}
			
			//write the html parent row...
			leftList.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");			
			leftList.append("<tr class=\"formBody\">");
			if (costList!=null && costList.size()>0) {
				leftList.append("<td height=\"23\" width=\"15\"><center><a href=\"javascript:showHideChild('" + key + "')\">" +
						"<img id=\"IMG_" + key + "\" border=\"0\" src=\"../images/minus.gif\" ></a></center></td>");	
			} else {
				leftList.append("<td height=\"23\" width=\"15\">&nbsp;</td>");
			}

			leftList.append("<td width=\"322\" class=\"capCell\" colspan=\"4\">&nbsp;<b>" + to.getGenericTag() + "</b></td></tr>\n");
			leftList.append("</table>");
			
			//write the html parent sumarized cols...
			body.append("<table width=\"" + (colNumber * COLUMN_WIDTH) + "\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
			body.append(getSummarizedLine(costList, frm, timesheet, uto, iniDate, finalDate));
			body.append("</table>");
			body.append("<div id=\"DIV_CELL_" + key + "\">");
			
			//write the html child rows...
			leftList.append("<div id=\"DIV_" + key + "\">");

			leftList.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
			if (frm.getViewMode().equals(CostForm.MODE_ALL) || frm.getViewMode().equals(CostForm.MODE_ONLY_EXPENSES)) {
				for (CostTO cto: costList) {
					leftList.append("<tr class=\"formBody\" ");
					leftList.append("onmouseout=\"hideIconCost('EDIT_"+ cto.getId() + "');hideIconCost('REMOVE_"+ cto.getId() + "');\" " +
							"onmouseover=\"showIconCost('EDIT_"+ cto.getId() + "');showIconCost('REMOVE_"+ cto.getId() + "');\">"); 
					leftList.append("<td height=\"24\" width=\"15\"><a id=\"EDIT_"+ cto.getId() + "\" href=\"javascript:editCost('"+ cto.getId() + "');\"><img src=\"../images/edit.gif\" border=\"0\"></a></td>");
					leftList.append("<td height=\"24\" width=\"15\"><a id=\"REMOVE_"+ cto.getId() + "\" href=\"javascript:removeCost('"+ cto.getId() + "');\"><img src=\"../images/remove.gif\" border=\"0\"></a></td>");
					leftList.append("<td height=\"24\" width=\"255\" class=\"capCell\">&nbsp;");
					leftList.append(cto.getName());	
					leftList.append("&nbsp;</td>\n");
					leftList.append("<td height=\"24\" width=\"45\" class=\"capCell\"><center>" + expDec.getExpenseLink(cto, uto) + "</center></td>\n</tr>\n");
					leftList.append("<script>hideIconCost('EDIT_"+ cto.getId() + "');hideIconCost('REMOVE_"+ cto.getId() + "');</script>");
					
					//write the html cols of child rows...
					body.append("<table width=\"" + (colNumber * COLUMN_WIDTH) + "\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");				
					body.append(this.getLine(cto, frm, uto, iniDate, finalDate));
					body.append("</table>");
				}				
			}

			//write the human resource costs, based to the tasks...
			if (sbHR!=null) {
				leftList.append("<tr class=\"formBody\">");
				leftList.append("<td height=\"23\" width=\"15\">&nbsp;</td>");
				leftList.append("<td height=\"23\" width=\"15\">&nbsp;</td>");
				leftList.append("<td height=\"23\" colspan=\"2\" class=\"capCell\">&nbsp;<img src=\"../images/roles.gif\" border=\"0\">&nbsp;" + super.getBundleMessage(request, "label.cost.hr") + "&nbsp;</td>\n");
				leftList.append("</tr>\n");

				body.append("<table width=\"" + (colNumber * COLUMN_WIDTH) + "\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
				body.append(sbHR);
				body.append("</table>");
			}
			
			leftList.append("</table></div>");
			body.append("</div>");
		}
		
		bodyList.append(body.toString());
		
		return leftList;
	}
	
	
	private StringBuffer getHumanResourceCost(CostForm frm, String projectId, UserTO uto, HashMap<String, Integer> timesheet, 
			Timestamp iniDate, Timestamp finalDate) throws BusinessException{
		StringBuffer response = null;		
		UserDelegate udel = new UserDelegate();
		TaskDelegate tdel = new TaskDelegate();
		ResourceCapacityDelegate rcdel = new ResourceCapacityDelegate();

		Vector<ResourceCapacityTO> rescap = rcdel.getListByResourceProject(null, projectId);
		Vector<TaskTO> taskList = tdel.getTaskListByProject(new ProjectTO(projectId), iniDate, false, false);
		if (taskList!=null && rescap!=null) {
			this.hashTaskData(frm, uto, timesheet, taskList, rescap);
			if (timesheet!=null) {
				response = new StringBuffer();

				Timestamp cursor = iniDate;
				while (cursor.before(finalDate)) {
					String cellcss = "capCellShadow";
					String valstr = "&nbsp;";
					float value = 0;
					
					Timestamp nextCursor = this.nextCursorDate(cursor, frm);
					String key = this.formatDate(cursor, frm, uto.getCalendarMask(), uto.getLocale());
					Integer val = timesheet.get(key);
					if (val!=null) {
						value = val.floatValue() / 100;
					}
					valstr = StringUtil.getCurrencyValue(value, udel.getCurrencyLocale());
					if (value>0) {
						cellcss = "capCell";	
					}
					response.append("<td height=\"23\" class=\"" + cellcss + "\" width=\"" + COLUMN_WIDTH + "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + valstr + "</td>");
					cursor = nextCursor;
				}
				response.append("</tr>");
			}	
		}
		return response;
	}


	private StringBuffer getLine(CostTO cto, CostForm frm, UserTO uto, Timestamp iniDate, Timestamp finalDate) throws Exception{
		NoteIcon note = new NoteIcon();
		UserDelegate udel = new UserDelegate();
		StringBuffer response = new StringBuffer();

		Locale currencyLoc = udel.getCurrencyLocale();
		response.append("<tr class=\"formBody\">");

		Timestamp cursor = iniDate;
		while (cursor.before(finalDate)) {
			String cellcss = "capCellShadow";
			String valstr = "&nbsp;";
			String notestr = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			
			Timestamp nextCursor = this.nextCursorDate(cursor, frm);
			Integer val = cto.summarizeInstallments(cursor, nextCursor);
			if (val!=null) {
				float value = val.floatValue() / 100;
				valstr = StringUtil.getCurrencyValue(value, currencyLoc);
				if (value>0) {
					cellcss = "capCell";
				}
			}

			if (cto.containValue(cursor, nextCursor)) {
				String desc = cto.infoInstallments(cursor, nextCursor, uto, currencyLoc);
				notestr = note.getContent(desc, null) + "&nbsp;";				
			}
			response.append("<td height=\"23\" class=\"" + cellcss + "\" width=\"" + COLUMN_WIDTH + "\">" + notestr + valstr  + "</td>");
			
			cursor = nextCursor;
		}
		response.append("</tr>");
		return response;
	}

	
	private StringBuffer getSummarizedLine(Vector<CostTO> list, CostForm frm, HashMap<String, Integer> timesheet, 
			UserTO uto, Timestamp iniDate, Timestamp finalDate) throws BusinessException{
		
		UserDelegate udel = new UserDelegate();
		StringBuffer response = new StringBuffer();

		response.append("<tr class=\"formBody\">");

		Timestamp cursor = iniDate;
		while (cursor.before(finalDate)) {
			Timestamp nextCursor = this.nextCursorDate(cursor, frm);

			int acc = 0;
			response.append("<td class=\"capCell\" height=\"23\" width=\"" + COLUMN_WIDTH + "\"><b>");

			if (frm.getViewMode().equals(CostForm.MODE_ALL) || frm.getViewMode().equals(CostForm.MODE_ONLY_EXPENSES)) {
				if (list!=null) {
					for (CostTO cto : list) {
						Integer val = cto.summarizeInstallments(cursor, nextCursor);
						if (val!=null) {
							acc = acc + val.intValue();
						}
					}
				}
			}

			if (frm.getViewMode().equals(CostForm.MODE_ALL) || frm.getViewMode().equals(CostForm.MODE_ONLY_RESOURCES)) {
				if (timesheet!=null){
					String key = this.formatDate(cursor, frm, uto.getCalendarMask(), uto.getLocale());
					Integer val = timesheet.get(key);
					if (val!=null) {
						acc = acc + val.intValue();
					}
				}
			}

			float value = ((float)acc) / 100;
			response.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			response.append(StringUtil.getCurrencyValue(value, udel.getCurrencyLocale()));		
			response.append("</b></td>");
			cursor = nextCursor;
		}
		response.append("</tr>");
		return response;
	}


	private int getBodyTitle(CostForm frm, UserTO uto, StringBuffer content){
		int numCols = 0;
		
		Timestamp iniDate = DateUtil.getDateTime(frm.getInitialDate(), uto.getCalendarMask(), uto.getLocale());
		Timestamp finalDate = DateUtil.getDateTime(frm.getFinalDate(), uto.getCalendarMask(), uto.getLocale());

		content.append("<tr class=\"tableRowEven\" height=\"21\">");

		Timestamp cursor = iniDate;
		while (cursor.before(finalDate)) {
			content.append("<td class=\"capCell\" width=\"" + COLUMN_WIDTH + "\"><center><b>" + 
					this.formatDate(cursor, frm, uto.getCalendarMask(), uto.getLocale()) + 
					"</b></center></td>");
			cursor = this.nextCursorDate(cursor, frm);
			numCols++;
		}
		content.append("</tr>");
		return numCols;
	}
	
	
	private Timestamp nextCursorDate(Timestamp cursor, CostForm frm){
		Timestamp response = null;
		int inc = Calendar.DATE; //default: daily
		int granularity = 1;     //default: daily
		String gran = frm.getGranularity();
		
		if (gran.equals("2")) {
			if (DateUtil.get(cursor, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
				inc = Calendar.WEEK_OF_MONTH;	
			} else {
				for (int i=1; i<=7; i++) {
					Timestamp prev = DateUtil.getChangedDate(cursor, Calendar.DATE, i);
					if (DateUtil.get(prev, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
						response = prev;
						break;
					}
				}				
			}
			
		} else if (gran.equals("3")) {
			Timestamp firstDay = DateUtil.getDate("01", (DateUtil.get(cursor, Calendar.MONTH)+1)+"", 
					DateUtil.get(cursor, Calendar.YEAR)+"");
			response = DateUtil.getChangedDate(firstDay, Calendar.MONTH, 1);
			
		} else if (gran.equals("4")) {
			if (DateUtil.get(cursor, Calendar.MONTH)<=2) {
				response = DateUtil.getDate("01", "04", DateUtil.get(cursor, Calendar.YEAR)+"");
			} else if (DateUtil.get(cursor, Calendar.MONTH)<=5) {
				response = DateUtil.getDate("01", "07", DateUtil.get(cursor, Calendar.YEAR)+"");
			} else if (DateUtil.get(cursor, Calendar.MONTH)<=8) {
				response = DateUtil.getDate("01", "10", DateUtil.get(cursor, Calendar.YEAR)+"");
			} else {
				response = DateUtil.getDate("01", "01", (DateUtil.get(cursor, Calendar.YEAR)+1)+"");
			}
			
		} else if (gran.equals("5")) {
			if (DateUtil.get(cursor, Calendar.MONTH)<=5) {
				response = DateUtil.getDate("01", "07", DateUtil.get(cursor, Calendar.YEAR)+"");
			} else {
				response = DateUtil.getDate("01", "01", (DateUtil.get(cursor, Calendar.YEAR)+1)+"");
			}
			
		} else if (gran.equals("6")) {
			response = DateUtil.getDate("01", "01", (DateUtil.get(cursor, Calendar.YEAR)+1)+"");
		}
		
		if (response==null) {
			response = DateUtil.getChangedDate(cursor, inc, granularity); 
		}
		
		return response;
	}	

	private String formatDate(Timestamp cursor, CostForm frm, String defaultPattern, Locale loc){
		String response = null;
		String pattern = defaultPattern;
		String gran = frm.getGranularity();
		
		if (gran.equals("3")) {
			pattern = "MMM-yyyy";
		} else if (gran.equals("4")) {
			String year = DateUtil.getDateTime(cursor, "yyyy");
			if (DateUtil.get(cursor, Calendar.MONTH)<=2) {
				response = "Q1-" + year;
			} else if (DateUtil.get(cursor, Calendar.MONTH)<=5) {
				response = "Q2-" + year;
			} else if (DateUtil.get(cursor, Calendar.MONTH)<=8) {
				response = "Q3-" + year;
			} else {
				response = "Q4-" + year;
			}			
		} else if (gran.equals("5")) {
			String year = DateUtil.getDateTime(cursor, "yyyy");
			if (DateUtil.get(cursor, Calendar.MONTH)<=5) {
				response = "1S-" + year;
			} else {
				response = "2S-" + year;
			}
		} else if (gran.equals("6")) {
			pattern = "yyyy";			
		}
		
		if (response==null) {
			response = DateUtil.getDateTime(cursor, pattern, loc); 
		}
		
		return response;
	}	
	
	
	private Vector<TransferObject> getParentList(CostForm frm, UserTO uto) throws BusinessException {
		ProjectDelegate pdel = new ProjectDelegate();
		CostDelegate cdel = new CostDelegate();
		CategoryDelegate catdel = new CategoryDelegate();
		
		Vector<TransferObject> list = new Vector<TransferObject>();
		
		if (frm.getType().equals(CostForm.TYPE_PROJECT)) {
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
			Vector<ProjectTO> projlist = pdel.getAllProjectsByParent(pto, true);
			list.add(new TransferObject(pto.getId(), pto.getName()));
			
			Iterator<ProjectTO> i = projlist.iterator();
			while(i.hasNext()) {
				ProjectTO child = i.next();
				list.add(new TransferObject(child.getId(), child.getName()));
			}
			
		} else if (frm.getType().equals(CostForm.TYPE_ACCOUNT_CODE)) {
			list = cdel.getAccountCodesByLeader(uto);
			
		} else if (frm.getType().equals(CostForm.TYPE_CATEGORY)) {
			Vector<CategoryTO> clist = catdel.getCategoryListByType(CategoryTO.TYPE_COST, new ProjectTO(frm.getProjectId()), true);
			if (clist!=null) {
				HashMap<String, String> catHash = new HashMap<String, String>();
				for (CategoryTO cTO : clist) {
					String projName = "";
					if (cTO.getProject()!=null) {
						projName = " - " +cTO.getProject().getName();
					}
					if (catHash.get(cTO.getId())==null) {
						list.add(new TransferObject(cTO.getId(), cTO.getName() + projName));
						catHash.put(cTO.getId(), cTO.getId());
					}
				}
			}
		}
		
		return list;
	}
	
	
	private void hashTaskData(CostForm frm, UserTO uto, HashMap<String, Integer> timesheet, 
			Vector<TaskTO> taskList, Vector<ResourceCapacityTO> rescapList) {
		CostDelegate cdel = new CostDelegate();
		Iterator<TaskTO> i = taskList.iterator();
		while(i.hasNext()) {
			TaskTO tto = i.next();
			if (tto.getAllocResources()!=null) {
				Iterator<ResourceTaskTO> j = tto.getAllocResources().iterator();
				while(j.hasNext()) {
					ResourceTaskTO rtto = j.next();
					if (rtto.getAllocList()!=null && rtto.getActualDate()!=null) {
						Iterator<ResourceTaskAllocTO> k = rtto.getAllocList().iterator();
						while(k.hasNext()) {
							ResourceTaskAllocTO rtato = k.next();
							
							if (rtato.getAllocTime()!=null && rtato.getSequence()!=null && rtato.getResourceTask()!=null 
									&& rtato.getResourceTask().getResource()!=null && rtato.getResourceTask().getResource().getProject()!=null) {
								
								Timestamp bucketDate = DateUtil.getChangedDate(rtto.getActualDate(), Calendar.DATE, rtato.getSequence().intValue());
								String key = this.formatDate(bucketDate, frm, uto.getCalendarMask(), uto.getLocale());
								
								Integer newVal = new Integer(0);
								Integer cost = cdel.getCost(rescapList, bucketDate, rtato.getResourceTask().getResource().getProject(), rtato.getResourceTask().getResource());
								if (cost!=null) {
									//calculate the allocated hours divided cost(cents) per hour
									//result: cost (in cents) of allocated time 
									newVal = new Integer((int)((rtato.getAllocTime().floatValue() / 60) * cost.intValue())); 
								}
								
								if (timesheet.get(key)!=null) {
									Integer currentval = timesheet.get(key);
									timesheet.put(key, new Integer(currentval.intValue() + newVal.intValue()));
								} else {
									timesheet.put(key, newVal);
								}
							}
						}
					}
				}
			}	
		}
	}
	
	
	private void loadPreferences(ActionForm form, HttpServletRequest request){
		CostForm frm = (CostForm)form;
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		
		frm.setCanShowChart(pto.getPreference(PreferenceTO.COST_FILTER_SHOW_CHART));
		frm.setGranularity(pto.getPreference(PreferenceTO.COST_FILTER_GRANULARY));
		frm.setHideOutOfRange((pto.getPreference(PreferenceTO.COST_FILTER_HIDE_OUT_RANGE)).equals("on"));
		frm.setType(pto.getPreference(PreferenceTO.COST_FILTER_TYPE));
		frm.setViewMode(pto.getPreference(PreferenceTO.COST_FILTER_VIEWMODE));
	}

	
	private void savePreferences(ActionForm form, HttpServletRequest request) throws BusinessException{
		CostForm frm = (CostForm)form;
		
		PreferenceBUS pbus = new PreferenceBUS();
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();

		pto.addPreferences(new PreferenceTO(PreferenceTO.COST_FILTER_SHOW_CHART, frm.getCanShowChart(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.COST_FILTER_GRANULARY, frm.getGranularity(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.COST_FILTER_HIDE_OUT_RANGE, (frm.getHideOutOfRange()?"on":"off"), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.COST_FILTER_TYPE, frm.getType(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.COST_FILTER_VIEWMODE, frm.getViewMode(), uto));
		pbus.insertOrUpdate(pto);			
	}
	
		
}
