package com.pandora.gui.struts.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.pandora.LeaderTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceCapacityTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.PreferenceBUS;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ResourceCapacityDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ResCapacityPanelForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class ResCapacityPanelAction extends GeneralStrutsAction {
	
	private HashMap<String, Integer> hmMaxLimit = new HashMap<String, Integer>();
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showResCapacityPanel";		
		try {
			ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			if (uto instanceof LeaderTO) {
				frm.setInitialDate(DateUtil.getDate(DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, -15), uto.getCalendarMask(), uto.getLocale()));
				frm.setFinalDate(DateUtil.getDate(DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, 60), uto.getCalendarMask(), uto.getLocale()));

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
			Vector<TransferObject> list = new Vector<TransferObject>();
			ResCapacityPanelForm frm = (ResCapacityPanelForm)form;
			UserTO uto = SessionUtil.getCurrentUser(request);
			
			if (frm.getHmProjectlist()==null || frm.getHmProjectlist().size()==0) {
				this.loadProjectAndResouces(frm, request);				
			}			
			
			if (uto instanceof LeaderTO) {
				if (frm.getType()==null || frm.getType().equals("PRJ")) {
					list.addAll(frm.getHmProjectlist().values());
					frm.setType("PRJ");
				} else {
					list.addAll(frm.getHmUserlist().values());
				}
				
				request.getSession().setAttribute("resCapacityList", list);
				
				if (list!=null && list.size()>0) {
					Iterator<TransferObject> i = list.iterator();
					TransferObject first = i.next();
					if (frm.getType().equals("PRJ") && (frm.getProjectId()==null || frm.getProjectId().equals(""))) {
						frm.setProjectId(first.getId());
					} else if (frm.getType().equals("RES") && (frm.getResourceId()==null || frm.getResourceId().equals(""))) {
						frm.setResourceId(first.getId());	
					}
					
					this.refreshBody(frm, request);
					this.savePreferences(frm, request);					
				}				
				
			} else {
				request.getSession().setAttribute("resCapacityList", new Vector<TransferObject>());
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
			hmMaxLimit = new HashMap<String, Integer>();
			
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

	
	private void loadProjectAndResouces(ResCapacityPanelForm frm, HttpServletRequest request) throws BusinessException{
		ProjectDelegate pdel = new ProjectDelegate();
		UserDelegate udel = new UserDelegate();

		UserTO uto = SessionUtil.getCurrentUser(request);
		this.loadPreferences(frm, request);
		
		Vector<ProjectTO> prjlist = pdel.getProjectListForManagement((LeaderTO)uto, false);
		Iterator<ProjectTO> j = prjlist.iterator();
		while(j.hasNext()) {
			ProjectTO to = j.next();
			Vector<ProjectTO> childProjects = pdel.getProjectListByParent(to, false);
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
			Vector<ResourceTO> userlist = udel.getResourceByProject(pto.getId(), false, true);
			Iterator<ResourceTO> i = userlist.iterator();
			while(i.hasNext()) {
				ResourceTO res = i.next();
				if (res!=null && !res.getUsername().equals(RootTO.ROOT_USER)) {
					if ((frm.getHideDisabledUsers() && res.getFinalDate()==null) || !frm.getHideDisabledUsers()) {
						frm.addHmUserlist(res.getId(), res);			
					}
				}
			}			
		}
		
		Vector<ProjectTO> prjWlist = pdel.getProjectListForWork(uto, true, false);
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
		
		Vector<TransferObject> modes = new Vector<TransferObject>();
		modes.add(new TransferObject(ResCapacityPanelForm.MODE_CAP_COST, super.getBundleMessage(request, "label.resCapacity.viewMode.all")));
		modes.add(new TransferObject(ResCapacityPanelForm.MODE_CAP_USED, super.getBundleMessage(request, "label.resCapacity.viewMode.used")));
		modes.add(new TransferObject(ResCapacityPanelForm.MODE_ONLY_CAP, super.getBundleMessage(request, "label.resCapacity.viewMode.onlycap")));
		
		request.getSession().setAttribute("resCapacityModes", modes);		
	}
	
	
	
	private void refreshBody(ResCapacityPanelForm frm, HttpServletRequest request) throws BusinessException{
		UserDelegate udel = new UserDelegate();
		StringBuffer sb = new StringBuffer();
		StringBuffer titleSb = new StringBuffer();
		int[] summCapacity = new int[5000];
		int[] summCost = new int[5000];
		int[] summUsed = new int[5000];
		Vector<TransferObject> elementList = new Vector<TransferObject>();
		int index = 0;
		int colNumber = 0;

		this.setSubTitle(frm);
		
		//get capacity data from DB
		Vector<ResourceCapacityTO> clist = this.getCapacityData(frm, request);
		
		//retrieve the project or resource subset from capacity list
		HashMap<String,String> elements = this.retrieveElementsFromCapacity(frm, clist);

		UserTO uto = SessionUtil.getCurrentUser(request);
		Timestamp iniDate = DateUtil.getDateTime(frm.getInitialDate(), uto.getCalendarMask(), uto.getLocale());
		Timestamp finalDate = DateUtil.getDateTime(frm.getFinalDate(), uto.getCalendarMask(), uto.getLocale());
		Locale currencyLoc = udel.getCurrencyLocale();

		String lblRed = super.getBundleMessage(request, "label.resCapacity.viewMode.redbullet");
		String lblYel = super.getBundleMessage(request, "label.resCapacity.viewMode.yelbullet");
		String lblGre = super.getBundleMessage(request, "label.resCapacity.viewMode.grebullet");
				
		//retrieve used capacity from DB
		HashMap<String,ResourceTaskAllocTO> alloc = this.getUsedCapacity(elements, frm, iniDate, finalDate);
		
		sb.append(this.getTitle(frm, iniDate, finalDate, request, true));
		titleSb.append(this.getTitle(frm, iniDate, finalDate, request, false));
			
		Iterator<String> j = elements.values().iterator();
		while(j.hasNext()) {

			boolean allZero = true;
			StringBuffer rowTitle = new StringBuffer();
			StringBuffer rowBody = new StringBuffer();
			StringBuffer otherLine = new StringBuffer();

			String elementId = (String)j.next();
			String label = this.getLabel(frm, frm.getType()==null || frm.getType().equals("PRJ"), elementId);
			Timestamp projectFinalDate = null;
			
			index = 0;
			Timestamp cursor = new Timestamp(iniDate.getTime());
			if (label!=null) {
				elementList.add(new TransferObject(elementId, label));

				String resourceId = null;
				String projectId = null;
				if (frm.getType()==null || frm.getType().equals("PRJ")) {
					projectId = frm.getProjectId();
					resourceId = elementId;
				} else {
					projectId = elementId;						
					resourceId = frm.getResourceId();
				}
				
				String hi = "height=\"24\"";
				if (frm.getViewMode()!=null && !frm.getViewMode().equals(ResCapacityPanelForm.MODE_ONLY_CAP)) {
					hi = "height=\"49\"";
				}
				rowTitle.append("<tr class=\"formBody\">");
				rowTitle.append("<td " + hi + " class=\"capCell\" align=\"right\">" + label + "&nbsp;&nbsp;</td>");
				rowTitle.append("</tr>");

				projectFinalDate = this.getProjectFinalDate(frm, projectId);
				
				rowBody.append("<tr class=\"formBody\">");
				otherLine.append("<tr class=\"formBody\">");
				
				while (cursor.before(finalDate) || cursor.equals(finalDate)) {
					Timestamp nextCursor = this.nextCursorDate(cursor, frm.getGranularity(), finalDate);
					ResourceCapacityTO rcto = this.getCapacity(cursor, nextCursor, projectId, resourceId, clist, frm.getGranularity(), finalDate);

					if (rcto!=null) {
						
						int cap = 0, cost = 0, used = 0;
						boolean nonClosedPrjIntoRange = (projectFinalDate==null || (projectFinalDate!=null && projectFinalDate.after(cursor)));
						
						if (nonClosedPrjIntoRange) {
							cap = rcto.getCapacity().intValue();
							if (rcto.getCostPerHour()!=null) {
								cost = rcto.getCostPerHour().intValue();
							}							
						}
						
						if (frm.getViewMode().equals(ResCapacityPanelForm.MODE_CAP_USED)) {
							used = this.getCapacityUsed(alloc, cursor, nextCursor, projectId, resourceId, frm.getGranularity());
						} 

						this.summarize(summCapacity, summCost, summUsed, index, cap, cost, used);
						rowBody.append(this.renderCell(cap, frm, rcto, cursor, request, uto.getLocale(), finalDate, clist, nonClosedPrjIntoRange));	

						if (frm.getViewMode()!=null) {
							if (frm.getViewMode().equals(ResCapacityPanelForm.MODE_CAP_COST)) {
								otherLine.append("<td height=\"24\" class=\"capCell\" align=\"center\">" + StringUtil.getCurrencyValue((float)((float)cost/100), currencyLoc) + "</td>");
							} else 	if (frm.getViewMode().equals(ResCapacityPanelForm.MODE_CAP_USED)) {
								otherLine.append("<td height=\"24\" class=\"capCell\" align=\"center\">" + this.formatCapacityUsed(used, uto, cap, lblRed, lblYel, lblGre) + "</td>");
							} 
						}
						
						if (allZero){
							allZero = (cap==0 && used==0 && cost==0);	
						}
						
					} else {
						rowBody.append("<td height=\"24\" class=\"capCell\">&nbsp;</td>");
						otherLine.append("<td class=\"capCell\" height=\"24\">&nbsp;</td>");
					}
					
					cursor.setTime(nextCursor.getTime());
					index++;
					if (colNumber<index) {
						colNumber = index;
					}
				}
				
				rowBody.append("<td>&nbsp;</td></tr>");
				otherLine.append("<td>&nbsp;</td></tr>");
				
				if (frm.getViewMode()!=null && !frm.getViewMode().equals(ResCapacityPanelForm.MODE_ONLY_CAP)) {
					rowBody.append(otherLine);		
				}
			}
			
			//check if the current row must be hidden...
			if ((frm.getHideZeroValues() && !allZero) || !frm.getHideZeroValues()) {
				titleSb.append(rowTitle);
				sb.append(rowBody);
			}
			
		}
		
		request.getSession().setAttribute("resCapacityElements", elementList);	
		sb.append(getFooter(frm, summCapacity, summCost, summUsed, iniDate, finalDate, request, uto, true));
		titleSb.append(getFooter(frm, summCapacity, summCost, summUsed, iniDate, finalDate, request, uto, false));
		
		frm.setCapacityHtmlBody("<table width=\"" + (colNumber * 70) + "\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">" + sb.toString() + "</table>");
		frm.setCapacityHtmlTitle("<table width=\"220\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">" +	titleSb.toString() + "</table>");
		frm.setShowEditCapacity("off");
	}
	
	private Timestamp getProjectFinalDate(ResCapacityPanelForm frm, String projectId) {
		Timestamp response = null;
		
		ProjectTO pto = (ProjectTO)frm.getHmProjectlist().get(projectId);
		if (pto!=null && pto.getFinalDate()!=null) {
			response = pto.getFinalDate();
		} else {
			pto = (ProjectTO)frm.getHmProjectWorklist().get(projectId);
			if (pto!=null && pto.getFinalDate()!=null) {
				response = pto.getFinalDate();
			}
		}
		return response;
	}
	
	private int getCapacityUsed(HashMap<String,ResourceTaskAllocTO> alloc, Timestamp cursor, Timestamp nextCursor, String projectId, String resourceId, int granularity) {
		Timestamp ref = this.getDateRef(cursor, nextCursor, granularity);
		int accTime = 0;
		while(ref.before(nextCursor)) {
			
			String key = ResourceTaskAllocTO.getAllocKey(ref, projectId, resourceId);
			ResourceTaskAllocTO rtato = alloc.get(key);
			if (rtato!=null) {
				accTime+=rtato.getAllocTime();
			}
			
			ref = DateUtil.getChangedDate(ref, Calendar.DATE, 1);
		}
		return accTime;
	}			
			
	
	private String formatCapacityUsed(int usedTime, UserTO uto, int cap, String lblRed, String lblYellow, String lblGreen) {
		String response = "";

		float timeHour = (float)(((float)usedTime)/60);
		String hourStr = StringUtil.getFloatToString(timeHour, "0.#h", uto.getLocale());
		
		if (timeHour>0) {
			response = "<b>" + hourStr + "</b>";	
		} else {
			response = hourStr;
		}

		String bullet = "redballon.gif";
		String tip = lblRed;
		float prop = 0;
		if (usedTime>0 && usedTime>cap) {
			prop = (float)((float)cap / (float)usedTime) * 100;
		} else if (cap>0 && cap>usedTime) {
			prop = (float)((float)usedTime / (float)cap) * 100;			
		} else if (cap==0 && usedTime==0) {
			prop = 100;
		}
		
		if (prop>=95){
			bullet = "greenballon.gif";
			tip = lblGreen;
		} else if (prop>=75 && prop<95){
			bullet = "yellowballon.gif";
			tip = lblYellow; 
		}
        response = response + "&nbsp;&nbsp;<img border=\"0\" " + HtmlUtil.getHint(tip) + " src=\"../images/" + bullet + "\" >";
        
		return response;
	}
	
	private Timestamp getDateRef(Timestamp cursor, Timestamp nextCursor, int granularity){
		Timestamp ref = cursor;
		if (granularity == 7) {
			ref = DateUtil.getChangedDate(nextCursor, Calendar.DATE, -7);
		} else if (granularity == -1) {
			ref = DateUtil.getChangedDate(nextCursor, Calendar.MONTH, -1);
		} else if (granularity == 14) {
			ref = DateUtil.getChangedDate(nextCursor, Calendar.DATE, -15);
		}
		return ref;	
	}

	private HashMap<String, ResourceTaskAllocTO> getUsedCapacity(HashMap<String, String> elements, ResCapacityPanelForm frm, Timestamp iniDate, Timestamp finalDate) throws BusinessException {
		ResourceTaskDelegate rtadel = new ResourceTaskDelegate();
		HashMap<String, ResourceTaskAllocTO> response = new HashMap<String, ResourceTaskAllocTO>();

		if (frm.getViewMode()!=null && frm.getViewMode().equals(ResCapacityPanelForm.MODE_CAP_USED)) {
			String projectFilter = "'" + frm.getProjectId() + "'";
			String resourceFilter = "'" + frm.getResourceId()+ "'";
			
			if (frm.getType()==null || frm.getType().equals("PRJ")) {
				resourceFilter = this.getElementsByComma(elements);
			} else {
				projectFilter = this.getElementsByComma(elements);						
			}
			
			response = rtadel.getHashAlloc(projectFilter, resourceFilter, iniDate, finalDate);
		}		

		return response;
	}


	private String getElementsByComma(HashMap<String, String> elements) {
		String response = "";
		if (elements!=null) {
			for (String elementId : elements.values()) {
				if (!response.equals("")) {
					response = response + ", ";	
				}
				response = response + "'" + elementId + "'";
			}
		}
		return response;
	}



	private StringBuffer renderCell(int cap, ResCapacityPanelForm frm, ResourceCapacityTO rcto,	Timestamp cursor, 
			HttpServletRequest request, Locale loc, Timestamp finalDate, Vector<ResourceCapacityTO> clist, boolean nonClosedPrjIntoRange){
		StringBuffer sb = new StringBuffer();

		String cssclass = "capCellHighlight";
		if (cap <= this.getMaxLimit(cursor, frm.getGranularity(), finalDate)) {
			cssclass = "capCell";
		}

		float capHour = (float)((float)cap / (float)60);
		BigDecimal bd = new BigDecimal(capHour).setScale(2, RoundingMode.HALF_EVEN);
		capHour = bd.floatValue();
		String capHrStr = StringUtil.getFloatToString(capHour, "0.#h", loc);
		
		sb.append("<td height=\"24\" class=\"" + cssclass + "\" align=\"center\">");
		String cellid = DateUtil.getDateTime(cursor, "yyyyMMdd") + "_" + rcto.getProjectId() + "_" + rcto.getResourceId();
		
		boolean isCapacityCursor = this.cursorContainCapacity(cursor, rcto.getProjectId(), rcto.getResourceId(), clist);
		String content = capHrStr;
		if (isCapacityCursor) {
			content = "<font color=\"#22B14C\"><b>" + capHrStr + "</b></font>";
		}

		ProjectTO pto = (ProjectTO)frm.getHmProjectlist().get(rcto.getProjectId());
		if (pto!=null && !pto.getId().equals("0") && nonClosedPrjIntoRange) {
			
			sb.append("<div onmouseout=\"hideIconEdit('EDIT_" + cellid + "');\" onmouseover=\"showIconEdit('EDIT_" + cellid + "');\">");			
			sb.append("<table width=\"100%\" height=\"17\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"table-layout:fixed; border:none\">");
			sb.append("<tr><td class=\"tableCell\"><center>" + content + "</center></td>");
			sb.append("<td width=\"14\" class=\"tableCell\">");
			sb.append("<div id=\"EDIT_" + cellid + "\">");
			sb.append("<a href=\"javascript:editCap('" + DateUtil.getDateTime(cursor, "yyyyMMdd") + "', '" + rcto.getProjectId() + "', '" + rcto.getResourceId() + "', '" + (isCapacityCursor?"1":"0") + "');\">");
			sb.append("<img src=\"../images/edit.gif\" border=\"0\" /></a>");
			sb.append("</div></td></tr></table>");
			sb.append("<script>hideIconEdit('EDIT_" + cellid + "');</script>");
			sb.append("</div>");
			
		} else {
			String tip = super.getBundleMessage(request, "label.resCapacity.blockEdit");
			if (!nonClosedPrjIntoRange) {
				tip = super.getBundleMessage(request, "label.resCapacity.blockEditClosedPrj");
			}
			sb.append("<div onmouseout=\"hideIconEdit('BLKEDIT_" + cellid + "');\" onmouseover=\"showIconEdit('BLKEDIT_" + cellid + "');\">");			
			sb.append("<table width=\"100%\" height=\"17\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"table-layout:fixed; border:none\">");
			sb.append("<tr><td class=\"tableCell\"><center>" + content + "</center></td>");
			sb.append("<td width=\"14\" class=\"tableCell\">");
			sb.append("<div id=\"BLKEDIT_" + cellid + "\">");
			sb.append("<img src=\"../images/warn.gif\" border=\"0\" " + HtmlUtil.getHint(tip) + "/>");
			sb.append("</div></td></tr></table>");
			sb.append("<script>hideIconEdit('BLKEDIT_" + cellid + "');</script>");
			sb.append("</div>");
		}
		sb.append("</td>");
		
		return sb;
	}
	
	
	private boolean cursorContainCapacity(Timestamp cursor,	String projectId, String resourceId, Vector<ResourceCapacityTO> clist) {
		boolean response = false;
		if (clist!=null) {
			Timestamp latest = DateUtil.getNow();
			for (ResourceCapacityTO rcto : clist) {
				if (cursor.equals(rcto.getDate()) && rcto.getProjectId().equals(projectId) && rcto.getResourceId().equals(resourceId)) {
					response = true;
				}
				if (latest.after(rcto.getDate())) {
					latest = rcto.getDate();
				}
			}
			if (response == true) {
				response = latest.before(cursor);
			}
		}
		return response;
	}


	private void summarize(int[] summCapacity, int[] summCost, int[] summUsed, int index, int cap, int cost, int used) {
		if (summCapacity.length > index) {			
			summCapacity[index] = summCapacity[index] + cap;
			summCost[index] = summCost[index] + cost;
			summUsed[index] = summUsed[index] + used;
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

	
	private StringBuffer getFooter(ResCapacityPanelForm frm, int[] sumCap, int[] sumCost, int[] summUsed, Timestamp iniDate, 
			Timestamp finalDate, HttpServletRequest request, UserTO uto, boolean isBody) throws BusinessException {
		StringBuffer sb = new StringBuffer();
		StringBuffer sbCost = new StringBuffer();
		StringBuffer sbUsed = new StringBuffer();
		UserDelegate udel = new UserDelegate();
		
		String lblRed = super.getBundleMessage(request, "label.resCapacity.viewMode.redbullet");
		String lblYel = super.getBundleMessage(request, "label.resCapacity.viewMode.yelbullet");
		String lblGre = super.getBundleMessage(request, "label.resCapacity.viewMode.grebullet");

		Locale loc = uto.getLocale();
		int index = 0;		
		sb.append("<tr class=\"formBody\">");
		sbCost.append("<tr class=\"formBody\">");
		sbUsed.append("<tr class=\"formBody\">");

		if (!isBody) {
			sb.append("<td height=\"24\" class=\"capCell\" align=\"right\"><b>" + super.getBundleMessage(request, "label.resCapacity.captotal") + "&nbsp;&nbsp;</b></td>");
			sbCost.append("<td height=\"24\" class=\"capCell\" align=\"right\"><b>" + super.getBundleMessage(request, "label.resCapacity.costtotal") + "&nbsp;&nbsp;</b></td>");
			sbUsed.append("<td height=\"24\" class=\"capCell\" align=\"right\"><b>" + super.getBundleMessage(request, "label.resCapacity.actualtotal") + "&nbsp;&nbsp;</b></td>");
		} else {
			Timestamp cursor = new Timestamp(iniDate.getTime());
			while (cursor.before(finalDate)) {
				
				if (frm.getType()!=null && frm.getType().equals("RES") && sumCap[index] > this.getMaxLimit(cursor, frm.getGranularity(), finalDate)) {
					sb.append("<td height=\"24\" class=\"capCellHighlight\" align=\"center\">");
				} else {
					sb.append("<td height=\"24\" class=\"capCell\" align=\"center\">");	
				}
				
				if (sumCap.length > index) {
					
					float totalCap = (sumCap[index] / (float)60);
					String capHrStr = StringUtil.getFloatToString(totalCap, "0.#h", loc);
					sb.append(capHrStr);	
					
				} else {
					sb.append("&nbsp;");
				}
				
				sb.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>");

				
				if (sumCost.length > index) {
					float f = Float.parseFloat(sumCost[index]+"");
					sbCost.append("<td height=\"24\" class=\"capCell\" align=\"center\">" + 
							StringUtil.getCurrencyValue((float)(f/100), udel.getCurrencyLocale()) + "</td>");				
				} else {
					sbCost.append("<td height=\"24\" class=\"capCell\" align=\"center\">&nbsp;</td>");				
				}
				
				
				if (summUsed.length > index && sumCap.length>index) {
					sbUsed.append("<td height=\"24\" class=\"capCell\" align=\"center\">" + 
							this.formatCapacityUsed(summUsed[index], uto, sumCap[index], lblRed, lblYel, lblGre) + "</td>");				
				} else {
					sbUsed.append("&nbsp;");
				}
				
				cursor = this.nextCursorDate(cursor, frm.getGranularity(), finalDate);
				index++;
			}
			sb.append("</tr>");
			sbCost.append("</tr>");
			sbUsed.append("</tr>");
		}
		
		if (frm.getViewMode()!=null) {
			if (frm.getViewMode().equals(ResCapacityPanelForm.MODE_CAP_COST)) {
				sb.append(sbCost);
			} else 	if (frm.getViewMode().equals(ResCapacityPanelForm.MODE_CAP_USED)) {
				sb.append(sbUsed);
			} 
		}
		
		return sb;
	}

	
	private int getMaxLimit(Timestamp cursor, int gran, Timestamp finalDate) {
		int response = 0;
		
		Integer maxLimit = hmMaxLimit.get(DateUtil.getDateTime(cursor, "yyyyMMdd"));
		if (maxLimit==null) {
			Timestamp nextCursor = this.nextCursorDate(cursor, gran, finalDate);
			if (nextCursor!=null) {
				int slots = getNumberSlots(cursor, nextCursor);
				response = slots * 8 * 60;
				hmMaxLimit.put(DateUtil.getDateTime(cursor, "yyyyMMdd"), new Integer(response));
			}
		} else {
			response = maxLimit.intValue();
		}
		
		return response;
	}

	
	public int getNumberSlots(Timestamp iniInterval, Timestamp finalInteval) {
		int response = 0;
		if (iniInterval.before(finalInteval)) {
			int diff = DateUtil.getSlotBetweenDates(iniInterval, finalInteval);
		
			Timestamp cursor = new Timestamp(iniInterval.getTime());
			while(cursor.before(finalInteval)) {
				
				if (diff>1) {
					int dayweek = DateUtil.get(cursor, Calendar.DAY_OF_WEEK);
					if (dayweek!=Calendar.SATURDAY && dayweek!=Calendar.SUNDAY) {
						response++;	
					}					
				} else {
					response++;
				}
				
				cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, 1);
			}
		} else {
			response = 1;
		}
		return response;
	}	


	public ResourceCapacityTO getCapacity(Timestamp cursor, Timestamp nextCursor, String projectId, String resourceId, Vector<ResourceCapacityTO> clist, int granularity, Timestamp finalDate) {
		ResourceCapacityTO response = null;
		Vector<ResourceCapacityTO> accCapacity = new Vector<ResourceCapacityTO>();		

		
		Timestamp ref = this.getDateRef(cursor, nextCursor, granularity);
		while(ref.before(nextCursor)) {

			ResourceCapacityTO rescap = null;
			Iterator<ResourceCapacityTO> i = clist.iterator();
			while(i.hasNext()) {
				ResourceCapacityTO rcto = (ResourceCapacityTO)i.next();
				if (rcto.getProjectId().equals(projectId) && rcto.getResourceId().equals(resourceId)) {
					if (!ref.before(rcto.getDate())) {
						rescap = new ResourceCapacityTO(rcto);
						response = new ResourceCapacityTO(rcto);
						response.setCapacity(0);
					} else {
						break;
					}
				}
			}
			
			if (rescap!=null && response!=null){
				int dayweek = DateUtil.get(ref, Calendar.DAY_OF_WEEK);
				if ((dayweek!=Calendar.SATURDAY && dayweek!=Calendar.SUNDAY) || (rescap.getDate().equals(cursor) && granularity==1)) {
					accCapacity.add(rescap);						
				}
			}
			
			ref = DateUtil.getChangedDate(ref, Calendar.DATE, 1);
		}					
		
		int accCap = -1;
		int accHour = -1;
		for (ResourceCapacityTO rcto : accCapacity) {
			if (rcto.getCapacity()!=null) {
				if (accCap==-1) accCap = 0;				
				accCap += rcto.getCapacity().intValue();	
			}
			if (rcto.getCostPerHour()!=null) {
				if (accHour==-1) accHour = 0;
				accHour = rcto.getCostPerHour().intValue();	
			}
		}
		
		if (accCap>-1) {
			response.setCapacity(new Integer(accCap));	
		}
		if (accHour>-1) {
			response.setCostPerHour(new Integer(accHour));	
		}

		if (response!=null && response.getDate().after(finalDate)){
			response = null;
		}
		
		return response;
	}
	
	
	private StringBuffer getTitle(ResCapacityPanelForm frm, Timestamp iniDate, Timestamp finalDate, 
			HttpServletRequest request, boolean isbody){
		StringBuffer sb = new StringBuffer();
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		sb.append("<tr class=\"tableRowEven\" height=\"20\">");
		if (!isbody) {				
			String lbl = "";
			if (frm.getType()==null || frm.getType().equals("PRJ")) {
				lbl = super.getBundleMessage(request, "title.resCapacity.grid.2");	
			} else {
				lbl = super.getBundleMessage(request, "title.resCapacity.grid.1");
			}
			sb.append("<td height=\"24\" class=\"capCell\" width=\"200\" valign=\"center\"><b><center>" + lbl + "</center></b></td>");			
		} else {
			Timestamp cursor = new Timestamp(iniDate.getTime());
			while (cursor.before(finalDate) || cursor.equals(finalDate)) {
				sb.append("<td width=\"70\" height=\"24\" class=\"capCell\" valign=\"center\"><center>" + 
						this.formatDate(cursor, frm, uto.getCalendarMask()) + 
						"</center></td>");
				cursor = this.nextCursorDate(cursor, frm.getGranularity(), finalDate);
			}
		}
		sb.append("</tr>");
		
		return sb;
	}
	
	
	public Timestamp nextCursorDate(Timestamp cursor, int gran, Timestamp finalDate){
		Timestamp response = null;
		int inc = Calendar.DATE; //default: Daily
		int granularity = 1;     //default: Daily
		
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
		} else {
			if (response.after(finalDate) && !response.equals(finalDate)) {
				response = DateUtil.getChangedDate(finalDate, Calendar.DATE, 1);
			}			
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


	private Vector<ResourceCapacityTO> getCapacityData(ResCapacityPanelForm frm, HttpServletRequest request) throws BusinessException {
		ResourceCapacityDelegate rcdel = new ResourceCapacityDelegate();
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		String resId = null;
		String projId = null;
		if (frm.getType()==null || frm.getType().equals("PRJ")) {
			projId = frm.getProjectId();
		} else {
			resId = frm.getResourceId();
		}
		
		return rcdel.getListByResourceProject(resId, projId, uto);
	}
	
	private void loadPreferences(ResCapacityPanelForm frm, HttpServletRequest request){
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		frm.setViewMode(pto.getPreference(PreferenceTO.RES_CAP_VIEWMODE));
		frm.setGranularity(Integer.parseInt(pto.getPreference(PreferenceTO.RES_CAP_GRANULARITY)));
		frm.setHideDisabledUsers((pto.getPreference(PreferenceTO.RES_CAP_HIDE_DSBLE_USERS)).equals("on"));
	}

	
	private void savePreferences(ResCapacityPanelForm frm, HttpServletRequest request) throws BusinessException{
		PreferenceBUS pbus = new PreferenceBUS();
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		pto.addPreferences(new PreferenceTO(PreferenceTO.RES_CAP_VIEWMODE, frm.getViewMode(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.RES_CAP_GRANULARITY, frm.getGranularity()+"", uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.RES_CAP_HIDE_DSBLE_USERS, (frm.getHideDisabledUsers()?"on":"off"), uto));
		pbus.insertOrUpdate(pto);			
	}
	
}
