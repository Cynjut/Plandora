package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.LeaderTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceCapacityTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.PreferenceBUS;
import com.pandora.delegate.GadgetDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ResourceCapacityDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ResCapacityPanelForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class ResCapacityPanelAction extends GeneralStrutsAction {
	
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityPanel";		
		try {
			ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			if (uto instanceof LeaderTO) {
				frm.setInitialDate(DateUtil.getDate(DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, -30), uto.getCalendarMask(), uto.getLocale()));
				frm.setFinalDate(DateUtil.getDate(DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, 90), uto.getCalendarMask(), uto.getLocale()));

				this.loadProjectAndResouces(frm, request);
				this.refresh(mapping, form, request, response);				
			} else {
				this.setErrorFormSession(request, "validate.project.userNotLeader", null);	
			}
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);
	}


	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityPanel";
		
		try {			
			Collection<TransferObject> list = null;
			ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
			UserTO uto = SessionUtil.getCurrentUser(request);
			
			if (frm.getHmProjectlist()==null || frm.getHmProjectlist().size()==0) {
				this.loadProjectAndResouces(frm, request);				
			}			
			
			if (uto instanceof LeaderTO) {
				if (frm.getType()==null || frm.getType().equals("PRJ")) {
					list = frm.getHmProjectlist().values();
					frm.setType("PRJ");
				} else {
					list = frm.getHmUserlist().values();	
				}
				
				request.getSession().setAttribute("resCapacityList", list);
				
				Iterator<TransferObject> i = list.iterator();
				TransferObject first = i.next();
				if (frm.getType().equals("PRJ") && (frm.getProjectId()==null || frm.getProjectId().equals(""))) {
					frm.setProjectId(first.getId());
				} else if (frm.getType().equals("RES") && (frm.getResourceId()==null || frm.getResourceId().equals(""))) {
					frm.setResourceId(first.getId());	
				}
				
				this.refreshBody(frm, request);
				this.savePreferences(frm, request);
				
			} else {
				request.getSession().setAttribute("resCapacityList", new Vector());
			}

			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}

		return mapping.findForward(forward);
	}

	
	public ActionForward refreshBody(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityPanel";
		try {
			ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
			
			if (frm.getHmProjectlist()==null || frm.getHmProjectlist().size()==0) {
				this.loadProjectAndResouces(frm, request);				
				this.refresh(mapping, form, request, response);
			}
			
			this.refreshBody(frm, request);
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);
	}

	
	public ActionForward showEditPanel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityPanel";
		ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
		frm.setShowEditCapacity("on");
		return mapping.findForward(forward);
	}

	
	public ActionForward showChart(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityPanel";
		ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
		frm.setShowEditCapacity("off");
		return mapping.findForward(forward);
	}

	
	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityPanel";
		try {
			ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
			frm.setShowEditCapacity("off");
			this.refreshBody(frm, request);			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}		
		return mapping.findForward(forward);
	}

	
	public ActionForward renderChart(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
			GadgetDelegate del = new GadgetDelegate();
			
			Vector<String> params = new Vector<String>();
			params.addElement(frm.getChartResourceId());
			params.addElement(frm.getChartProjectId());
			
			del.renderContent(request, response, "com.pandora.gui.struts.action.ResourceCapacityChart", params);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
	    return null;
	}
	
	private void loadProjectAndResouces(ResCapacityPanelForm frm, HttpServletRequest request) throws BusinessException{
		ProjectDelegate pdel = new ProjectDelegate();
		UserDelegate udel = new UserDelegate();

		UserTO uto = SessionUtil.getCurrentUser(request);
		this.loadPreferences(frm, request);
		
		Vector<ProjectTO> prjlist = pdel.getProjectListForManagement((LeaderTO)uto, true);
		Iterator<ProjectTO> j = prjlist.iterator();
		while(j.hasNext()) {
			ProjectTO to = j.next();
			Vector<ProjectTO> childProjects = pdel.getProjectListByParent(to, true);
			Iterator<ProjectTO> k = childProjects.iterator();
			while(k.hasNext()) {
				ProjectTO child = k.next();
				frm.addHmProjectlist(child.getId(), child);
			}
			frm.addHmProjectlist(to.getId(), to);
		}

		Iterator<ProjectTO> ip = frm.getHmProjectlist().values().iterator();
		while(ip.hasNext()) {
			ProjectTO pto = (ProjectTO)ip.next();
			Vector userlist = udel.getResourceByProject(pto.getId(), false, true);
			Iterator i = userlist.iterator();
			while(i.hasNext()) {
				UserTO res = (UserTO)i.next();
				if (res!=null && !res.getUsername().equals(RootTO.ROOT_USER)) {
					if ((frm.getHideDisabledUsers() && res.getFinalDate()==null) || !frm.getHideDisabledUsers()) {
						frm.addHmUserlist(res.getId(), res);			
					}
				}
			}			
		}
		
		Vector<ProjectTO> prjWlist = pdel.getProjectListForWork(uto, true);
		Iterator<ProjectTO> k= prjWlist.iterator();
		while(k.hasNext()) {
			ProjectTO to = (ProjectTO)k.next();
			frm.addHmProjectWorklist(to.getId(), to);
		}

		Vector<TransferObject> types = new Vector<TransferObject>();
		types.add(new TransferObject("PRJ", super.getBundleMessage(request, "label.resCapacity.type.1")));
		types.add(new TransferObject("RES", super.getBundleMessage(request, "label.resCapacity.type.2")));
		request.getSession().setAttribute("resCapacityTypes", types);

		Vector<TransferObject> gran = new Vector<TransferObject>();
		gran.add(new TransferObject("1", super.getBundleMessage(request, "label.resCapacity.gran.4")));	
		gran.add(new TransferObject("7", super.getBundleMessage(request, "label.resCapacity.gran.2")));		
		gran.add(new TransferObject("14", super.getBundleMessage(request, "label.resCapacity.gran.1")));
		gran.add(new TransferObject("-1", super.getBundleMessage(request, "label.resCapacity.gran.3")));
		request.getSession().setAttribute("resCapacityGran", gran);

		Vector<TransferObject> unit = new Vector<TransferObject>();
		unit.add(new TransferObject("1", super.getBundleMessage(request, "title.resCapacity.unit.1")));
		request.getSession().setAttribute("resCapacityUnit", unit);	

		Vector<TransferObject> limit = new Vector<TransferObject>();
		limit.add(new TransferObject("480", super.getBundleMessage(request, "label.resCapacity.limit.1")));
		request.getSession().setAttribute("resCapacityLimit", limit);	
		
		Vector<TransferObject> modes = new Vector<TransferObject>();
		modes.add(new TransferObject(ResCapacityPanelForm.MODE_ALL, super.getBundleMessage(request, "label.resCapacity.viewMode.all")));
		modes.add(new TransferObject(ResCapacityPanelForm.MODE_ONLY_CAP, super.getBundleMessage(request, "label.resCapacity.viewMode.onlycap")));
		request.getSession().setAttribute("resCapacityModes", modes);		
	}
	
	
	private void refreshBody(ResCapacityPanelForm frm, HttpServletRequest request) throws BusinessException{
		UserDelegate udel = new UserDelegate();
		StringBuffer sb = new StringBuffer();
		StringBuffer titleSb = new StringBuffer();
		int[] summCapacity = new int[5000];
		int[] summCost = new int[5000];
		Vector<TransferObject> elementList = new Vector<TransferObject>();
		int index = 0;
		int colNumber = 0;

		this.setSubTitle(frm);
		
		//get capacity data from DB
		Vector<ResourceCapacityTO> clist = this.getCapacityData(frm);
		
		//retrieve the project or resource subset from capacity list
		HashMap<String,String> elements = this.retrieveElementsFromCapacity(frm, clist);		
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		Timestamp iniDate = DateUtil.getDateTime(frm.getInitialDate(), uto.getCalendarMask(), uto.getLocale());
		Timestamp finalDate = DateUtil.getDateTime(frm.getFinalDate(), uto.getCalendarMask(), uto.getLocale());
		String charIconLbl = super.getBundleMessage(request, "title.resCapacity");
		Locale currencyLoc = udel.getCurrencyLocale();
		
		sb.append(this.getTitle(frm, iniDate, finalDate, request, true));
		titleSb.append(this.getTitle(frm, iniDate, finalDate, request, false));
			
		Iterator<String> j = elements.values().iterator();
		while(j.hasNext()) {
			String elementId = (String)j.next();
			String label = this.getLabel(frm, frm.getType()==null || frm.getType().equals("PRJ"), elementId);
			
			index = 0;
			Timestamp cursor = iniDate;
			StringBuffer otherLine = new StringBuffer();
			if (label!=null) {
				elementList.add(new TransferObject(elementId, label));

				titleSb.append("<tr class=\"formBody\">");
				titleSb.append("<td class=\"capCell\" align=\"center\">");
				titleSb.append(this.getChartIcon(frm, elementId, charIconLbl));
				titleSb.append("</td>");
				titleSb.append("<td class=\"capCell\" align=\"right\">" + label + "&nbsp;&nbsp;</td>");
				titleSb.append("</tr>");
				
				if (frm.getViewMode()!=null && frm.getViewMode().equals(ResCapacityPanelForm.MODE_ALL)) {
					titleSb.append("<tr height=\"20\">");
					titleSb.append("<td>&nbsp;</td>");				
					titleSb.append("<td>&nbsp;</td>");
					titleSb.append("</tr>");		
				}
				
				sb.append("<tr class=\"formBody\">");
				otherLine.append("<tr class=\"formBody\">");				
				while (cursor.before(finalDate)) {

					String resourceId = null;
					String projectId = null;
					if (frm.getType()==null || frm.getType().equals("PRJ")) {
						projectId = frm.getProjectId();
						resourceId = elementId;
					} else {
						projectId = elementId;						
						resourceId = frm.getResourceId();
					}

					ResourceCapacityTO rcto = this.getCapacity(cursor, projectId, resourceId, clist);
					if (rcto!=null) {

						int cap = rcto.getCapacity().intValue();
						int cost = 0;
						if (rcto.getCostPerHour()!=null) {
							cost = rcto.getCostPerHour().intValue();
						}

						this.summarize(summCapacity, summCost, index, cap, cost);
						sb.append(this.renderCell(cap, frm, rcto, cursor));							
						otherLine.append("<td class=\"capCell\" align=\"center\">" + 
								StringUtil.getCurrencyValue((float)((float)cost/100), currencyLoc) + "</td>");
					} else {
						sb.append("<td class=\"capCell\">&nbsp;</td>");
						otherLine.append("<td class=\"capCell\" height=\"20\">&nbsp;</td>");
					}
					
					cursor = this.nextCursorDate(cursor, frm);
					index++;
					if (colNumber<index) {
						colNumber = index;
					}
				}
				sb.append("<td>&nbsp;</td></tr>");
				otherLine.append("<td>&nbsp;</td></tr>");
				
				if (frm.getViewMode()!=null && frm.getViewMode().equals(ResCapacityPanelForm.MODE_ALL)) {
					sb.append(otherLine);		
				}
			}
		}
		request.getSession().setAttribute("resCapacityElements", elementList);	
		sb.append(getFooter(frm, summCapacity, summCost, iniDate, finalDate, request, true));
		titleSb.append(getFooter(frm, summCapacity, summCost, iniDate, finalDate, request, false));
		
		frm.setCapacityHtmlBody("<table width=\"" + (colNumber * 70) + "\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">" + sb.toString() + "</table>");
		frm.setCapacityHtmlTitle("<table width=\"220\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">" +	titleSb.toString() + "</table>");
		frm.setShowEditCapacity("off");
	}

	
	private StringBuffer getChartIcon(ResCapacityPanelForm frm, String elementId, String charIconLbl){
		StringBuffer sb = new StringBuffer();
		String resourceId = null;
		String projectId = null;
		
		if (frm.getType()==null || frm.getType().equals("PRJ")) {
			projectId = frm.getProjectId();
			resourceId = elementId;
		} else {
			projectId = elementId;						
			resourceId = frm.getResourceId();
		}
		
		sb.append("<a href=\"javascript:showResCapacityChart('" + resourceId + "', '" + projectId + "');\" border=\"0\">"); 
		sb.append("<img border=\"0\" title=\"" + charIconLbl + "\" alt=\"" + charIconLbl + "\" src=\"../images/linechart.gif\" ></a>");
		
		return sb;
	}
	
	
	private StringBuffer renderCell(int cap, ResCapacityPanelForm frm, ResourceCapacityTO rcto,	Timestamp cursor){
		StringBuffer sb = new StringBuffer();

		String cssclass = "capCellHighlight";
		if (cap <= frm.getMaxLimit()) {
			cssclass = "capCell";
		}

		ProjectTO pto = (ProjectTO)frm.getHmProjectlist().get(rcto.getProjectId());
		if (pto!=null && !pto.getId().equals("0")) {
			String cellid = DateUtil.getDateTime(cursor, "yyyyMMdd") + "_" + rcto.getProjectId() + "_" + rcto.getResourceId();
			
			sb.append("<td class=\"" + cssclass + "\" align=\"center\">");	

			sb.append("<div onmouseout=\"hideIconEdit('EDIT_" + cellid + "');\" onmouseover=\"showIconEdit('EDIT_" + cellid + "');\">");	
			sb.append("<table width=\"100%\" height=\"17\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"table-layout:fixed; border:none\">");
			sb.append("<tr><td class=\"tableCell\"><center>" + cap + "</center></td>");
			sb.append("<td width=\"14\">");
			sb.append("<div id=\"EDIT_" + cellid + "\">");
			sb.append("<a href=\"javascript:editCap('" + DateUtil.getDateTime(cursor, "yyyyMMdd") + "', '" + rcto.getProjectId() + "', '" + rcto.getResourceId() + "');\">");
			sb.append("<img src=\"../images/edit.gif\" border=\"0\"></a>");
			sb.append("</div></td></tr></table>");
			sb.append("<script>hideIconEdit('EDIT_" + cellid + "');</script>");
			sb.append("</div>");
			
			sb.append("</td>");
			
		} else {
			sb.append("<td class=\"" + cssclass + "\" align=\"center\"><center>" + cap + "&nbsp;&nbsp;&nbsp;&nbsp;</center></td>");
		}

		return sb;
	}
	
	
	private void summarize(int[] summCapacity, int[] summCost,	int index, int cap, int cost) {
		if (summCapacity.length > index) {			
			summCapacity[index] = summCapacity[index] + cap;
			summCost[index] = summCost[index] + cost;
		}
	}


	private void setSubTitle(ResCapacityPanelForm frm) throws BusinessException {
		String titleLbl = "";
		if (frm.getType()==null || frm.getType().equals("PRJ")) {
			titleLbl = this.getLabel(frm, false, frm.getProjectId()); 
		} else {
			titleLbl = this.getLabel(frm, true, frm.getResourceId());	
		}
		frm.setElementLabel(titleLbl);
	}

	
	private StringBuffer getFooter(ResCapacityPanelForm frm, int[] sumCap, int[] sumCost, Timestamp iniDate, 
			Timestamp finalDate, HttpServletRequest request, boolean isBody) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		StringBuffer sbCost = new StringBuffer();
		UserDelegate udel = new UserDelegate();
		
		int index = 0;		
		sb.append("<tr class=\"formBody\">");
		sbCost.append("<tr class=\"formBody\">");

		if (!isBody) {
			sb.append("<td>&nbsp;</td>");
			sbCost.append("<td>&nbsp;</td>");
			
			sb.append("<td class=\"capCell\" align=\"right\"><b>" + super.getBundleMessage(request, "label.resCapacity.captotal") + "&nbsp;&nbsp;</b></td>");
			sbCost.append("<td class=\"capCell\" align=\"right\"><b>" + super.getBundleMessage(request, "label.resCapacity.costtotal") + "&nbsp;&nbsp;</b></td>");			
		} else {
			Timestamp cursor = iniDate;
			while (cursor.before(finalDate)) {
				
				if (frm.getType()!=null && frm.getType().equals("RES") && sumCap[index] > frm.getMaxLimit()) {
					sb.append("<td class=\"capCellHighlight\" align=\"center\">");
				} else {
					sb.append("<td class=\"capCell\" align=\"center\">");	
				}
				
				if (sumCap.length > index) {
					sb.append(sumCap[index]);	
				} else {
					sb.append("&nbsp;");
				}
				
				sb.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>");

				
				if (sumCost.length > index) {
					float f = Float.parseFloat(sumCost[index]+"");
					sbCost.append("<td class=\"capCell\" align=\"center\">" + 
							StringUtil.getCurrencyValue((float)(f/100), udel.getCurrencyLocale()) + "</td>");				
				} else {
					sbCost.append("<td class=\"capCell\" align=\"center\">&nbsp;</td>");				
				}
				
				cursor = this.nextCursorDate(cursor, frm);
				index++;
			}
			sb.append("</tr>");
			sbCost.append("</tr>");			
		}
		
		if (frm.getViewMode()!=null && frm.getViewMode().equals(ResCapacityPanelForm.MODE_ALL)) {
			sb.append(sbCost);		
		}
		
		return sb;
	}

	
	public ResourceCapacityTO getCapacity(Timestamp reference, String projectId, String resourceId, Vector<ResourceCapacityTO> clist) {
		ResourceCapacityTO response = null;
		Iterator<ResourceCapacityTO> i = clist.iterator();
		while(i.hasNext()) {
			ResourceCapacityTO rcto = (ResourceCapacityTO)i.next();
			if (rcto.getProjectId().equals(projectId) && rcto.getResourceId().equals(resourceId)) {
				if (!reference.before(rcto.getDate())) {
					response = rcto;
				} else {
					break;
				}
			}
		}
		return response;
	}
	
	
	private StringBuffer getTitle(ResCapacityPanelForm frm, Timestamp iniDate, Timestamp finalDate, 
			HttpServletRequest request, boolean isbody){
		StringBuffer sb = new StringBuffer();
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		sb.append("<tr class=\"tableRowEven\" height=\"20\">");
		if (!isbody) {			
			sb.append("<td class=\"capCell\" width=\"30\">&nbsp;</td>");		
			String lbl = "";
			if (frm.getType()==null || frm.getType().equals("PRJ")) {
				lbl = super.getBundleMessage(request, "title.resCapacity.grid.2");	
			} else {
				lbl = super.getBundleMessage(request, "title.resCapacity.grid.1");
			}
			sb.append("<td class=\"capCell\" width=\"200\" valign=\"center\"><b><center>" + lbl + "</center></b></td>");			
		} else {
			Timestamp cursor = iniDate;
			while (cursor.before(finalDate)) {
				sb.append("<td width=\"70\" class=\"capCell\" valign=\"center\"><center>" + 
						this.formatDate(cursor, frm, uto.getCalendarMask()) + 
						"</center></td>");
				cursor = this.nextCursorDate(cursor, frm);						
			}
		}
		sb.append("</tr>");
		
		return sb;
	}
	
	
	private Timestamp nextCursorDate(Timestamp cursor, ResCapacityPanelForm frm){
		Timestamp response = null;
		int inc = Calendar.DATE; //default: Daily
		int granularity = 1;     //default: Daily
		int gran = frm.getGranularity();
		
		if (gran==7) { //Weekly
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
			
		} else if (gran==-1) { //Monthly
			Timestamp firstDay = DateUtil.getDate("01", (DateUtil.get(cursor, Calendar.MONTH)+1)+"", 
					DateUtil.get(cursor, Calendar.YEAR)+"");
			response = DateUtil.getChangedDate(firstDay, Calendar.MONTH, 1);
			
		} else if (gran==14) { //Fortnightly
			if (DateUtil.get(cursor, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
				granularity = 14;
			} else {
				boolean firstWeekPassed = false;
				for (int i=1; i<=15; i++) {
					Timestamp prev = DateUtil.getChangedDate(cursor, Calendar.DATE, i);
					if (DateUtil.get(prev, Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
						if (firstWeekPassed) {
							response = prev;
							break;						
						} else {
							firstWeekPassed = true;
						}
					}
				}				
			}
		}
		
		if (response==null) {
			response = DateUtil.getChangedDate(cursor, inc, granularity); 
		}
		
		return response;
	}	
	
	private String formatDate(Timestamp cursor, ResCapacityPanelForm frm, String defaultPattern){
		String response = null;
		String pattern = defaultPattern;
		int gran = frm.getGranularity();
		
		if (gran==-1) {
			pattern = "MMM-yyyy";
		}		
		response = DateUtil.getDateTime(cursor, pattern); 
		
		return response;
	}	
	
	
	private String getLabel(ResCapacityPanelForm frm, boolean isProjectType, String elementId) throws BusinessException{
		String label = null;

		if (elementId!=null) {
			if (isProjectType) {
				UserTO uto = (UserTO)frm.getHmUserlist().get(elementId);
				if (uto!=null && !uto.getUsername().equals(RootTO.ROOT_USER)) {
					if ((frm.getHideDisabledUsers() && uto.getFinalDate()==null) || !frm.getHideDisabledUsers()) {
						label = uto.getName();		
					}
				}
			} else {
				ProjectTO pto = (ProjectTO)frm.getHmProjectWorklist().get(elementId);
				if (pto!=null && !pto.getId().equals("0")) {
					label = pto.getName();	
				} else {
					pto = (ProjectTO)frm.getHmProjectlist().get(elementId);
					if (pto!=null && !pto.getId().equals("0")) {
						label = pto.getName();	
					}
				}
			}				
		}
		return label;
	}
	
	
	private HashMap<String, String> retrieveElementsFromCapacity(ResCapacityPanelForm frm,	Vector<ResourceCapacityTO> clist) {		
		HashMap<String, String> elements = new HashMap<String, String>();
		Iterator<ResourceCapacityTO> i = clist.iterator();		
		while(i.hasNext()) {
			ResourceCapacityTO to = (ResourceCapacityTO)i.next();
			if (frm.getType()==null || frm.getType().equals("PRJ")) {
				elements.put(to.getResourceId(), to.getResourceId());
			} else {
				elements.put(to.getProjectId(), to.getProjectId());
			}
		}
		return elements;
	}


	private Vector<ResourceCapacityTO> getCapacityData(ResCapacityPanelForm frm) throws BusinessException {
		ResourceCapacityDelegate rcdel = new ResourceCapacityDelegate();
		
		String resId = null;
		String projId = null;
		if (frm.getType()==null || frm.getType().equals("PRJ")) {
			projId = frm.getProjectId();
		} else {
			resId = frm.getResourceId();
		}
		
		return rcdel.getListByResourceProject(resId, projId);
	}
	
	private void loadPreferences(ResCapacityPanelForm frm, HttpServletRequest request){
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		frm.setMaxLimit(Integer.parseInt(pto.getPreference(PreferenceTO.RES_CAP_MAXLIMIT)));
		frm.setUnitCapacity(pto.getPreference(PreferenceTO.RES_CAP_UNIT));
		frm.setViewMode(pto.getPreference(PreferenceTO.RES_CAP_VIEWMODE));
		frm.setGranularity(Integer.parseInt(pto.getPreference(PreferenceTO.RES_CAP_GRANULARITY)));
		frm.setHideDisabledUsers((pto.getPreference(PreferenceTO.RES_CAP_HIDE_DSBLE_USERS)).equals("on"));
	}

	
	private void savePreferences(ResCapacityPanelForm frm, HttpServletRequest request) throws BusinessException{
		PreferenceBUS pbus = new PreferenceBUS();
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		pto.addPreferences(new PreferenceTO(PreferenceTO.RES_CAP_MAXLIMIT, frm.getMaxLimit()+"", uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.RES_CAP_UNIT, frm.getUnitCapacity(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.RES_CAP_VIEWMODE, frm.getViewMode(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.RES_CAP_GRANULARITY, frm.getGranularity()+"", uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.RES_CAP_HIDE_DSBLE_USERS, (frm.getHideDisabledUsers()?"on":"off"), uto));
		pbus.insertOrUpdate(pto);			
	}
	
}
