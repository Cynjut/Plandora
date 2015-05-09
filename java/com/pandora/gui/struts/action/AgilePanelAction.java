package com.pandora.gui.struts.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.ResourceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.PreferenceBUS;
import com.pandora.bus.gadget.Gadget;
import com.pandora.bus.gadget.GadgetBUS;
import com.pandora.bus.gadget.IterationBurndownChartGadget;
import com.pandora.bus.occurrence.IterationOccurrence;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.AgilePanelForm;
import com.pandora.helper.SessionUtil;


public class AgilePanelAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showAgilePanel";		
		try {
			UserTO uto = SessionUtil.getCurrentUser(request);
			ProjectDelegate pdel = new ProjectDelegate();
			AgilePanelForm frm = (AgilePanelForm)form;
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), false);
			boolean isAllowed = (pto.isLeader(uto.getId()));
			
			if (!isAllowed ) {
				UserDelegate udel = new UserDelegate();
				ResourceTO rto = new ResourceTO(uto.getId());
				rto.setProject(pto);
				rto = udel.getResource(rto);
				isAllowed = rto.getBoolCanSelfAlloc();
			}
			
			if (isAllowed) {
				this.loadPreferences(form, request);
				this.refreshLists(form, request);
				this.saveGadgetProject(pto, request);
			} else {
				forward = "home";				
				throw new BusinessException("The user doesn't has permission to access this form");
			}
				
		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);		
		}					
		return mapping.findForward(forward);
	}

	
	public ActionForward showReqPopup(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		AgilePanelForm frm =(AgilePanelForm)form; 
		frm.setMaximizedGadgetId("");	
		return mapping.findForward("showAgilePanelReq");
	}

	
	public ActionForward closeMaximizeGadget(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		AgilePanelForm frm =(AgilePanelForm)form;
		this.loadPreferences(form, request);		
		frm.setMaximizedGadgetId("");	
		return mapping.findForward("showAgilePanel");
	}	

	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		this.loadPreferences(form, request);
		return mapping.findForward("showAgilePanel");
	}
	

	public ActionForward saveTask(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			AgilePanelForm frm =(AgilePanelForm)form; 
			frm.setMaximizedGadgetId("");				
			this.refreshLists(form, request);
		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);		
		}					
		return mapping.findForward("showAgilePanel");
	}

	
	public ActionForward removeReq(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			RequirementDelegate rdel = new RequirementDelegate();
			AgilePanelForm frm = (AgilePanelForm)form;
			rdel.deleteRequirement(frm.getReqId());
			frm.setMaximizedGadgetId("");	
			
			this.loadPreferences(form, request);
			this.refreshLists(form, request);			
					
		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);		
		}					
		return mapping.findForward("showAgilePanel");
	}

	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){		
		String forward = "showAgilePanel";
		try {
			AgilePanelForm frm =(AgilePanelForm)form; 
			frm.setMaximizedGadgetId("");	
			
			this.savePreferences(form, request);
			
			this.refreshLists(form, request);
			
		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);		
		}			
		return mapping.findForward(forward);
	}
	
	
	
	private void refreshLists(ActionForm form,	HttpServletRequest request) throws BusinessException{		
		AgilePanelForm frm = (AgilePanelForm)form;
		frm.setMaximizedGadgetId("");	
		
		ProjectDelegate pdel = new ProjectDelegate();
		ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
		frm.setProjectName(pto.getName());
		Vector<ProjectTO> projChildList = pdel.getProjectListByParent(pto, true);
		projChildList.add(0, pto);
		request.getSession().setAttribute("projectList", projChildList);
		
		CustReqAction cra = new CustReqAction();
		Vector<TransferObject> priorList = cra.getPriorityCombo(request);
		request.getSession().setAttribute("priorityList", priorList);
		
		OccurrenceDelegate odel = new OccurrenceDelegate();
		Vector<OccurrenceTO> iterations = odel.getOccurenceListByType(frm.getProjectId(), IterationOccurrence.class.getName());
		OccurrenceTO oto = new OccurrenceTO("-1");
		oto.setName(super.getBundleMessage(request, "label.all2"));
		iterations.add(0, oto);
		request.getSession().setAttribute("iterationList", iterations);
		
		Vector<CategoryTO> catList = getListOfCategoriesForCombo(pto, request);
		request.getSession().setAttribute("categoryList", catList);
		
		RequirementDelegate del = new RequirementDelegate();
		Vector<RequirementWithTasksTO> reqList = del.getRequirementWithTaskList(frm.getProjectId(), 
				frm.getIterationSelected(), frm.getHideFinishedReq(), frm.getHideOldIterations());

		if (!frm.getHideTasksWithoutReq()) {
			RequirementWithTasksTO rwto = this.getTasksWithoutReq(frm, reqList);		
			reqList.addElement(rwto);
		}
		
		request.getSession().setAttribute("reqTaskBoardList", reqList);

		Vector<TransferObject> groupList = new Vector<TransferObject>();
		groupList.addElement(new TransferObject("1", super.getBundleMessage(request, "label.agilePanelForm.group1")));
		groupList.addElement(new TransferObject("2", super.getBundleMessage(request, "label.agilePanelForm.group2")));
		groupList.addElement(new TransferObject("3", super.getBundleMessage(request, "label.agilePanelForm.group3")));
		groupList.addElement(new TransferObject("4", super.getBundleMessage(request, "label.agilePanelForm.group4")));		
		request.getSession().setAttribute("groupByList", groupList);
		
		if (frm.getShowChart()) {
	    	this.refreshGadgets(request, frm);	
		} else {
			frm.setGadgetHtmlBody(null);	
		}
	}


	private RequirementWithTasksTO getTasksWithoutReq(AgilePanelForm frm, Vector reqList)
			throws BusinessException {
		RequirementWithTasksTO response = new RequirementWithTasksTO();
		
		ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
		Vector list = rtdel.getTasksWithoutReq(frm.getProjectId(), frm.getIterationSelected(), frm.getHideOldIterations());
		
		RequirementStatusTO rsto = new RequirementStatusTO();
		rsto.setStateMachineOrder(RequirementStatusTO.STATE_MACHINE_CLOSE);
		response.setRequirementStatus(rsto);
		response.setResourceTaskList(list);
		
		return response;
	}
	
	
	private void loadPreferences(ActionForm form, HttpServletRequest request){
		AgilePanelForm frm = (AgilePanelForm)form;
		
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		
		frm.setIterationSelected(pto.getPreference(PreferenceTO.AGILE_FILTER_ITERATION));
		frm.setGroupBy(pto.getPreference(PreferenceTO.AGILE_FILTER_GROUPING));
		frm.setHideFinishedReq((pto.getPreference(PreferenceTO.AGILE_FILTER_HIDE_REQ)).equals("on"));
		frm.setHideCancelTasks((pto.getPreference(PreferenceTO.AGILE_FILTER_HIDE_TSK)).equals("on"));
		frm.setHideTasksWithoutReq((pto.getPreference(PreferenceTO.AGILE_FILTER_HIDE_TSKREQ)).equals("on"));
		frm.setShowChart((pto.getPreference(PreferenceTO.AGILE_FILTER_SHOW_CHART)).equals("on"));
		frm.setHideOldIterations((pto.getPreference(PreferenceTO.AGILE_FILTER_HIDE_OLD_ITER)).equals("on"));
	}

	
	private void savePreferences(ActionForm form, HttpServletRequest request) throws BusinessException{
		AgilePanelForm frm = (AgilePanelForm)form;
		
		PreferenceBUS pbus = new PreferenceBUS();
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		
		pto.addPreferences(new PreferenceTO(PreferenceTO.AGILE_FILTER_ITERATION, frm.getIterationSelected(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.AGILE_FILTER_GROUPING, frm.getGroupBy(), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.AGILE_FILTER_HIDE_REQ, (frm.getHideFinishedReq()?"on":"off"), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.AGILE_FILTER_HIDE_TSK, (frm.getHideCancelTasks()?"on":"off"), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.AGILE_FILTER_HIDE_TSKREQ, (frm.getHideTasksWithoutReq()?"on":"off"), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.AGILE_FILTER_HIDE_OLD_ITER, (frm.getHideOldIterations()?"on":"off"), uto));
		pto.addPreferences(new PreferenceTO(PreferenceTO.AGILE_FILTER_SHOW_CHART, (frm.getShowChart()?"on":"off"), uto));		
		pbus.insertOrUpdate(pto);			
	}
	
	
	private void refreshGadgets(HttpServletRequest request, AgilePanelForm frm) throws BusinessException {
	    StringBuffer content =  new StringBuffer("");
	    ResourceHomeAction resHome = new ResourceHomeAction();
	    
		String loadingLabel = super.getBundleMessage(request, "label.manageOption.gadget.loading");
		Gadget gad = GadgetBUS.getGadgetClass("com.pandora.bus.gadget.IterationBurndownChartGadget");
		
		int gadWidth = 385;
		content.append(gad.gadgetToHtml(request, gadWidth, 120, loadingLabel));
		frm.setGadgetHtmlBody("<td width=\"" + gadWidth + "\" valign=\"top\" align=\"center\">" + content.toString() + "</td>");

		String iconsHtml = resHome.getMaximizedGadgetHtml(gad, request);
		if (gad.getFieldsId()!=null && gad.getFieldsId().size()>0) {
			iconsHtml = iconsHtml + resHome.getGadgetPropertyHtml(gad, request, "goToAgilePanel");	
		}
		
		frm.setGadgetIconBody(iconsHtml);
	}

	
	private void saveGadgetProject(ProjectTO project, HttpServletRequest request) throws BusinessException{
		PreferenceBUS pbus = new PreferenceBUS();
		UserTO uto = SessionUtil.getCurrentUser(request);
		PreferenceTO pto = uto.getPreference();
		Gadget gad = GadgetBUS.getGadgetClass("com.pandora.bus.gadget.IterationBurndownChartGadget");
		
		pto.addPreferences(new PreferenceTO(gad.getId() + "." + IterationBurndownChartGadget.ITERATION_BURNDOWN_PROJECT, 
		        project.getId(), uto));
		pbus.insertOrUpdate(pto);			
	}
	
	private Vector<CategoryTO> getListOfCategoriesForCombo(ProjectTO pto, HttpServletRequest request) throws BusinessException{
		HashMap<String, CategoryTO> uniqueList = new HashMap<String, CategoryTO>();
		
		CategoryDelegate catDel = new CategoryDelegate();
		Vector<CategoryTO> categList = catDel.getCategoryListByType(CategoryTO.TYPE_REQUIREMENT, pto, true);
		Iterator<CategoryTO> i = categList.iterator();
		while(i.hasNext()) {
			CategoryTO cto = i.next();
			if (uniqueList.get(cto.getName())==null) {
				cto.setId(cto.getName());
				uniqueList.put(cto.getName(), cto);
			}
		}
		
		Vector<CategoryTO> temp = new Vector<CategoryTO>();
		CategoryTO fakeCategory = new CategoryTO("-1");
		fakeCategory.setName(this.getBundleMessage(request, "label.all"));
		temp.addElement(fakeCategory);

		temp.addAll(uniqueList.values());
		
		return temp;
	}
	
}
