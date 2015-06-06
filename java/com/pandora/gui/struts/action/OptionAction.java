package com.pandora.gui.struts.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.EventTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RootTO;
import com.pandora.TaskStatusTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.auth.SystemAuthentication;
import com.pandora.bus.kb.IndexEngineBUS;
import com.pandora.bus.kb.KbIndex;
import com.pandora.delegate.IndexEngineDelegate;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.TaskStatusDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.OptionForm;
import com.pandora.gui.taglib.decorator.HideProjectDecorator;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;


/**
 * This class handle the actions performed into Option form
 */
public class OptionAction extends GeneralStrutsAction {

    /**
     * Shows the Manage Option form
     */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showOption";
		ProjectDelegate prjdel = new ProjectDelegate();
	    
	    try {

	        OptionForm frm = (OptionForm)form;
	        
	        //get current role of user into session...
	        UserTO uto = SessionUtil.getCurrentUser(request);
	        frm.setUser(uto);
	        frm.setSaveMethod(OptionForm.UPDATE_METHOD, uto);
	        frm.setNewPassword("");
	        frm.setNewPasswordConfirm("");
	        if (uto.getAuthenticationMode()!=null) {
	        	frm.setAuthenticationMode(uto.getAuthenticationMode());	
	        } else {
	        	frm.setAuthenticationMode(SystemAuthentication.class.getName());
	        }
	    
	        Vector<ProjectTO> prjList = prjdel.getProjectListByUser(uto);
			request.getSession().setAttribute("projectList", prjList);
	        
	        //set data from session into form
	        PreferenceTO pto = uto.getPreference();
	        frm.setRequNumLine(pto.getPreference(PreferenceTO.HOME_REQULIST_NUMLINE));
	        frm.setPendNumLine(pto.getPreference(PreferenceTO.HOME_PENDLIST_NUMLINE));
	        frm.setTaskNumLine(pto.getPreference(PreferenceTO.HOME_TASKLIST_NUMLINE));
	        frm.setProjNumLine(pto.getPreference(PreferenceTO.HOME_PROJLIST_NUMLINE));
	        frm.setInfoNumLine(pto.getPreference(PreferenceTO.HOME_TEAMLIST_NUMLINE));
	        
	        frm.setTopicNumLine(pto.getPreference(PreferenceTO.HOME_TOPICLIST_NUMLINE));
	        
	        frm.setShowPriorityColor(pto.getPreference(PreferenceTO.HOME_REQULIST_PRIORITY_COLOR).equalsIgnoreCase("TRUE"));
	        frm.setMaxNumOfWords(pto.getPreference(PreferenceTO.LIST_NUMWORDS));
	        frm.setPagingTOListAll(pto.getPreference(PreferenceTO.LIST_ALL_SHOW_PAGING).equals("15"));
	        frm.setShowLockedTasks(pto.getPreference(PreferenceTO.MY_TASK_SHOW_LOCK).equalsIgnoreCase("on"));
	        frm.setMyReqMaxDaysAgo(pto.getPreference(PreferenceTO.MY_REQU_DAYS_AGO));
	        frm.setMyTaskMaxDaysAgo(pto.getPreference(PreferenceTO.MY_TASK_DAYS_AGO));

	        frm.setUploadMaxFile(pto.getPreference(PreferenceTO.UPLOAD_MAX_SIZE));
	        frm.setArtifactMaxFile(pto.getPreference(PreferenceTO.ARTIFACT_MAX_SIZE));
	        frm.setCurrencyLocale(pto.getPreference(PreferenceTO.GENERAL_CURRENCY));
	        frm.setDefaultCapacity(pto.getPreference(PreferenceTO.GENERAL_DEFAULT_CAPACITY));
	        frm.setMaxMetaFieldTimeout(pto.getPreference(PreferenceTO.GENERAL_METAFIELD_TIMEOUT));
	        
	        frm.setNewVersionUrl(pto.getPreference(PreferenceTO.NEW_VERSION_URL));
	        frm.setTaskReportUrl(pto.getPreference(PreferenceTO.TASK_REPORT_URL));
	        frm.setSurveyReportUrl(pto.getPreference(PreferenceTO.SURVEY_REPORT_URL));
	        frm.setExpenseReportUrl(pto.getPreference(PreferenceTO.EXPENSE_REPORT_URL));
	        
	        frm.setWarningTaskDays(pto.getPreference(PreferenceTO.WARNING_DAY_TASK));
	        frm.setCriticalTaskDays(pto.getPreference(PreferenceTO.CRITICAL_DAY_TASK));
	        frm.setNotifiChannels(pto.getPreference(PreferenceTO.NOTIFICATION_BUS_CLASS));
	        frm.setCalendarSyncClasses(pto.getPreference(PreferenceTO.CALEND_SYNC_BUS_CLASS));
	        frm.setConverterClasses(pto.getPreference(PreferenceTO.CONVERTER_BUS_CLASS));
	        frm.setOverviewProjectClasses(pto.getPreference(PreferenceTO.OVERVIEW_PROJ_CLASS));
	        frm.setKbClasses(pto.getPreference(PreferenceTO.KB_BUS_CLASS));
	        frm.setGadgetClasses(pto.getPreference(PreferenceTO.GADGET_BUS_CLASS));
	        frm.setRepositoryClasses(pto.getPreference(PreferenceTO.REPOSITORY_BUS_CLASS));
	        frm.setAuthenticationClasses(pto.getPreference(PreferenceTO.AUTH_BUS_CLASS));
	        frm.setGadgetWidth(pto.getPreference(PreferenceTO.GADGET_WIDTH));
	        frm.setKbProgressBar(this.getIndexProgressBar(request));
	        frm.setKbIndexFolder(pto.getPreference(PreferenceTO.KB_INDEX_FOLDER));
	        frm.setOccurrenceSources(pto.getPreference(PreferenceTO.OCCURRENCE_BUS_CLASS));
	        frm.setImpExpClasses(pto.getPreference(PreferenceTO.IMP_EXP_BUS_CLASS));
	        frm.setArtifactExpClasses(pto.getPreference(PreferenceTO.ARTIFACT_EXPORT_CLASS));
	        frm.setSnipArtifactClasses(pto.getPreference(PreferenceTO.SNIP_ARTIFACT_BUS_CLASS));
	        
	        frm.setTaskInputFormat(pto.getPreference(PreferenceTO.INPUT_TASK_FORMAT));
	        
	        frm.setLdapHost(pto.getPreference(PreferenceTO.LDAP_HOST));
	        frm.setLdapPort(pto.getPreference(PreferenceTO.LDAP_PORT));
	        frm.setLdapUIDRegister(pto.getPreference(PreferenceTO.LDAP_UID_REGISTER));
	        frm.setHideProject(pto.getPreference(PreferenceTO.HIDE_PROJECT));
	        
	        frm.setShortcutName1(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "1"));
	        frm.setShortcutName2(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "2"));
	        frm.setShortcutName3(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "3"));
	        frm.setShortcutName4(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "4"));
	        frm.setShortcutName5(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "5"));
	        frm.setShortcutName6(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "6"));
	        frm.setShortcutName7(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "7"));
	        frm.setShortcutName8(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "8"));
	        frm.setShortcutName9(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "9"));
	        frm.setShortcutName10(pto.getPreference(PreferenceTO.SHORTCUT_NAME + "10"));

	        frm.setShortcutURL1(pto.getPreference(PreferenceTO.SHORTCUT_URL + "1"));
	        frm.setShortcutURL2(pto.getPreference(PreferenceTO.SHORTCUT_URL + "2"));
	        frm.setShortcutURL3(pto.getPreference(PreferenceTO.SHORTCUT_URL + "3"));
	        frm.setShortcutURL4(pto.getPreference(PreferenceTO.SHORTCUT_URL + "4"));
	        frm.setShortcutURL5(pto.getPreference(PreferenceTO.SHORTCUT_URL + "5"));
	        frm.setShortcutURL6(pto.getPreference(PreferenceTO.SHORTCUT_URL + "6"));
	        frm.setShortcutURL7(pto.getPreference(PreferenceTO.SHORTCUT_URL + "7"));
	        frm.setShortcutURL8(pto.getPreference(PreferenceTO.SHORTCUT_URL + "8"));
	        frm.setShortcutURL9(pto.getPreference(PreferenceTO.SHORTCUT_URL + "9"));
	        frm.setShortcutURL10(pto.getPreference(PreferenceTO.SHORTCUT_URL + "10"));
	        	        
	        frm.setShortcutIcon1(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "1"));
	        frm.setShortcutIcon2(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "2"));
	        frm.setShortcutIcon3(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "3"));
	        frm.setShortcutIcon4(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "4"));
	        frm.setShortcutIcon5(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "5"));
	        frm.setShortcutIcon6(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "6"));
	        frm.setShortcutIcon7(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "7"));
	        frm.setShortcutIcon8(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "8"));
	        frm.setShortcutIcon9(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "9"));
	        frm.setShortcutIcon10(pto.getPreference(PreferenceTO.SHORTCUT_ICON + "10"));

	        frm.setShortcutOpen1(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "1"));
	        frm.setShortcutOpen2(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "2"));
	        frm.setShortcutOpen3(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "3"));
	        frm.setShortcutOpen4(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "4"));
	        frm.setShortcutOpen5(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "5"));
	        frm.setShortcutOpen6(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "6"));
	        frm.setShortcutOpen7(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "7"));
	        frm.setShortcutOpen8(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "8"));
	        frm.setShortcutOpen9(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "9"));
	        frm.setShortcutOpen10(pto.getPreference(PreferenceTO.SHORTCUT_OPEN + "10"));
	        
	        
	        //set a list of taskStatus on form
	        Vector<TaskStatusTO> tslist = this.getTaskStatusOrder(request);
	        request.getSession().setAttribute("taskStatusList", tslist);	    
	        
	        Vector<TransferObject> inputList = new Vector<TransferObject>();
	        inputList.addElement(new TransferObject("1", this.getBundleMessage(request, "label.manageOption.inputTaskFormat.1")));
	        inputList.addElement(new TransferObject("2", this.getBundleMessage(request, "label.manageOption.inputTaskFormat.2")));	        
	        request.getSession().setAttribute("taskInputFormatList", inputList);	    
	      
	        Vector<TransferObject> scList = new Vector<TransferObject>();
	        scList.addElement(new TransferObject("-1", ""));
	        for (int i=1; i<=8; i++) {
	        	scList.addElement(new TransferObject(i+"", this.getBundleMessage(request, "label.shortcut.type." + i)));	
	        }
	        request.getSession().setAttribute("shorcutIconList", scList);	    

	        Vector<TransferObject> soList = new Vector<TransferObject>();
	        soList.addElement(new TransferObject("-1", ""));
	        for (int i=1; i<=2; i++) {
	        	soList.addElement(new TransferObject(i+"", this.getBundleMessage(request, "label.shortcut.opening." + i)));	
	        }
	        request.getSession().setAttribute("shorcutOpenList", soList);	    
	        
	        if (uto.getUsername().equals(RootTO.ROOT_USER)) {
	        	StringBuffer actionContent = new StringBuffer("");
	        	HashMap<String,EventTO> hm = SessionUtil.getLastEvents();
	        	if (hm!=null) {
	        		Iterator<EventTO> i = hm.values().iterator();
	        		while(i.hasNext()) {
	        			EventTO eto = (EventTO)i.next();
	        			actionContent.append(eto.getLog(uto.getLocale()));
	        		}
	        	}
		        frm.setUserActionLog(actionContent.toString());
		        
	        	StringBuffer fullActionContent = new StringBuffer("");
	        	Vector<EventTO> v = SessionUtil.getEvents();
	        	if (v!=null) {
	        		Iterator<EventTO> i = v.iterator();
	        		while(i.hasNext()) {
	        			EventTO eto = (EventTO)i.next();
	        			fullActionContent.append(eto.getLog(uto.getLocale()));
	        		}
	        	}
		        frm.setUserFullActionLog(fullActionContent.toString());	
	        }
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.prepareManageOptionForm", e);		    
	    }

	    return mapping.findForward(forward);
	}

	
	/**
	 * Get all taskStatus from database and set into each one, the current
	 * ordering related with user.
	 */
	private Vector<TaskStatusTO> getTaskStatusOrder(HttpServletRequest request) throws BusinessException{
	    TaskStatusDelegate tsdel = new TaskStatusDelegate();
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    PreferenceTO pto = uto.getPreference();
	    
        Vector<TaskStatusTO> tslist = tsdel.getTaskStatusList();
        Iterator<TaskStatusTO> i = tslist.iterator();
        while(i.hasNext()){
            TaskStatusTO tsto = i.next();
            String userOrd = pto.getPreference(PreferenceTO.HOME_TASKLIST_ORDER + "." + tsto.getId());
            tsto.setGenericTag(userOrd);
        }
        
	    return tslist;
	}

	
	
	
	/**
	 * Save the preferences values into data base
	 */
	public ActionForward saveOption(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showOption";
	    
	    try {
	        UserTO uto = SessionUtil.getCurrentUser(request);	        
	        PreferenceTO prefto= uto.getPreference();
	        OptionForm frm = (OptionForm)form;
	        
	        HashMap<String, PreferenceTO> hm = this.getPreferenceList(frm, request);	        
	        prefto.setPreferences(hm);
	        
		    //save the current preferences
		    try {	    
		        PreferenceDelegate pdel = new PreferenceDelegate(); 
	            pdel.insertOrUpdate(prefto);
	            
	            //fetch the user preference from data base 
	            uto.setPreference(pdel.getObjectByUser(uto));

	        } catch (BusinessException e) {
	            LogUtil.log(this, LogUtil.LOG_ERROR, "Error saving the preferences for user: " + uto.getId(), e);        
	        }	        
	        	        
	        //if current user is root, reset the root reference on logUtil
	        if (uto instanceof RootTO) {
	            LogUtil.resetRootRef();
	        }
	        
			//set success message into http session
			this.setSuccessFormSession(request, "message.manageOption.saveSuccess");
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.prepareManageOptionForm", e);		    
	    }

	    return mapping.findForward(forward);
	}
		

	/**
	 * Update the new user password into database.
	 */
	public ActionForward changePassword(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showOption";
	    
	    try {
	        UserDelegate udel = new UserDelegate();
	        UserTO uto = SessionUtil.getCurrentUser(request);	        
	        OptionForm frm = (OptionForm)form;
	        
	        uto.setPassword(frm.getNewPassword());
	        udel.updatePassword(uto);
	        
			//set success message into http session
			this.setSuccessFormSession(request, "message.manageOption.savePassSuccess");
	        
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.prepareManageOptionForm", e);		    
	    }

	    return mapping.findForward(forward);
	}
	
	
	public ActionForward resetIndex(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showOption";
	    
	    try {
	        UserDelegate udel = new UserDelegate();
	        PreferenceDelegate pdel = new PreferenceDelegate();
	        IndexEngineDelegate indexDel = new IndexEngineDelegate();
	        UserTO uto = SessionUtil.getCurrentUser(request);

	        if (uto.getUsername().equals(RootTO.ROOT_USER)) {
	            UserTO root = udel.getRoot();
	            PreferenceTO pref = root.getPreference();
	            String classes = pref.getPreference(PreferenceTO.KB_BUS_CLASS);
	    	    if (classes!=null) {
	    	        String[] classList = classes.split(";");
	    	        if (classList!=null && classList.length>0) {
	    	            for (int i = 0; i<classList.length; i++) {
	    	                String classStr = classList[i].trim();
	    	                KbIndex kbus = indexDel.getKbClass(classStr);
	                        if (kbus!=null) { 
	                            PreferenceTO pto = new PreferenceTO(PreferenceTO.KB_CURSOR_PREFIX + kbus.getUniqueName(), "0", root);
	                            pref.addPreferences(pto);
	                        }
	    	            }
	    	        }
	    	        
	    	        pdel.insertOrUpdate(root.getPreference());
	    	        
	    	        //reset the engine...
	    	        IndexEngineBUS ind = new IndexEngineBUS();
	    	        ind.reset();

	    	        //log event...
	                LogUtil.log(LogUtil.SUMMARY_KB_RESET, this, uto.getUsername(), LogUtil.LOG_INFO, "Knowledge Base Reset");    
	    	        
	    	    }

				//set success message into http session
				this.setSuccessFormSession(request, "error.manageOption.resetKb.done");
		    	    		        	            
	        } else {
	            this.setErrorFormSession(request, "error.manageOption.resetKb.permission", null);    
	        }

	        this.prepareForm(mapping, form, request, response);
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.manageOption.resetKb.error", e);		    
	    }

	    return mapping.findForward(forward);
	}		
	
	
	/**
	 * Generate a hashmap containing the all preferences values.
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, PreferenceTO> getPreferenceList(OptionForm frm, HttpServletRequest request) throws BusinessException{
        HashMap<String, PreferenceTO> hm = new HashMap<String, PreferenceTO>();
        PreferenceTO child = null;
        UserTO uto = SessionUtil.getCurrentUser(request);

        child = new PreferenceTO(PreferenceTO.HOME_REQULIST_NUMLINE, frm.getRequNumLine(), uto);
        hm.put(PreferenceTO.HOME_REQULIST_NUMLINE, child);

        child = new PreferenceTO(PreferenceTO.HOME_TOPICLIST_NUMLINE, frm.getTopicNumLine(), uto);
        hm.put(PreferenceTO.HOME_TOPICLIST_NUMLINE, child);
        
        child = new PreferenceTO(PreferenceTO.HOME_PENDLIST_NUMLINE, frm.getPendNumLine(), uto);
        hm.put(PreferenceTO.HOME_PENDLIST_NUMLINE, child);

        child = new PreferenceTO(PreferenceTO.HOME_TASKLIST_NUMLINE, frm.getTaskNumLine(), uto);
        hm.put(PreferenceTO.HOME_TASKLIST_NUMLINE, child);

        child = new PreferenceTO(PreferenceTO.HOME_PROJLIST_NUMLINE, frm.getProjNumLine(), uto);
        hm.put(PreferenceTO.HOME_PROJLIST_NUMLINE, child);

        child = new PreferenceTO(PreferenceTO.HOME_TEAMLIST_NUMLINE, frm.getInfoNumLine(), uto);
        hm.put(PreferenceTO.HOME_TEAMLIST_NUMLINE, child);
        
        child = new PreferenceTO(PreferenceTO.LIST_NUMWORDS, frm.getMaxNumOfWords(), uto);
        hm.put(PreferenceTO.LIST_NUMWORDS, child);

        child = new PreferenceTO(PreferenceTO.MY_REQU_DAYS_AGO, frm.getMyReqMaxDaysAgo(), uto);
        hm.put(PreferenceTO.MY_REQU_DAYS_AGO, child);

        child = new PreferenceTO(PreferenceTO.MY_TASK_DAYS_AGO, frm.getMyTaskMaxDaysAgo(), uto);
        hm.put(PreferenceTO.MY_TASK_DAYS_AGO, child);

        frm.setPagingTOListAll(request.getParameter("pagingTOListAll")!=null);
        child = new PreferenceTO(PreferenceTO.LIST_ALL_SHOW_PAGING, (frm.getPagingTOListAll()?"15":"0"), uto);
        hm.put(PreferenceTO.LIST_ALL_SHOW_PAGING, child);

        frm.setShowLockedTasks(request.getParameter("showLockedTasks")!=null);
        child = new PreferenceTO(PreferenceTO.MY_TASK_SHOW_LOCK, (frm.getShowLockedTasks()?"on":"off"), uto);
        hm.put(PreferenceTO.MY_TASK_SHOW_LOCK, child);
        
        frm.setShowPriorityColor(request.getParameter("showPriorityColor")!=null);
        child = new PreferenceTO(PreferenceTO.HOME_REQULIST_PRIORITY_COLOR, (frm.getShowPriorityColor()?"TRUE":"FALSE"), uto);
        hm.put(PreferenceTO.HOME_REQULIST_PRIORITY_COLOR, child);
        
        Iterator<TaskStatusTO> i = frm.getTaskStatusOrderingList().iterator();
        while (i.hasNext()){
            TaskStatusTO tsto = (TaskStatusTO)i.next();
            String key = PreferenceTO.HOME_TASKLIST_ORDER + "." + tsto.getId();
            child = new PreferenceTO(key, tsto.getGenericTag(), uto);
            hm.put(key, child);
        }

        String hideProjectList = "";
        Vector<ProjectTO> prjList = (Vector<ProjectTO>)request.getSession().getAttribute("projectList");
        Iterator<ProjectTO> ii = prjList.iterator();
        while (ii.hasNext()){
            ProjectTO pto = ii.next();
            String key = "cb_" + pto.getId() + "_" + HideProjectDecorator.PROJ_HIDE;
            String hideChecked = request.getParameter(key);
            if (hideChecked!=null && !hideChecked.trim().equals("")) {
            	hideProjectList = hideProjectList + pto.getId() + "|"; 
            }
        }

	    frm.setShortcutName1(request.getParameter("shortcutName1"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "1", frm.getShortcutName1(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "1", child);

	    frm.setShortcutName2(request.getParameter("shortcutName2"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "2", frm.getShortcutName2(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "2", child);
	    
	    frm.setShortcutName3(request.getParameter("shortcutName3"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "3", frm.getShortcutName3(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "3", child);

	    frm.setShortcutName4(request.getParameter("shortcutName4"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "4", frm.getShortcutName4(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "4", child);	    

	    frm.setShortcutName5(request.getParameter("shortcutName5"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "5", frm.getShortcutName5(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "5", child);	    

	    frm.setShortcutName6(request.getParameter("shortcutName6"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "6", frm.getShortcutName6(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "6", child);	    

	    frm.setShortcutName7(request.getParameter("shortcutName7"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "7", frm.getShortcutName7(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "7", child);	    

	    frm.setShortcutName8(request.getParameter("shortcutName8"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "8", frm.getShortcutName8(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "8", child);	    
	    
	    frm.setShortcutName9(request.getParameter("shortcutName9"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "9", frm.getShortcutName9(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "9", child);	    

	    frm.setShortcutName10(request.getParameter("shortcutName10"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_NAME + "10", frm.getShortcutName10(), uto);
	    hm.put(PreferenceTO.SHORTCUT_NAME + "10", child);	    
	    
	    
	    frm.setShortcutIcon1(request.getParameter("shortcutIcon1"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "1", frm.getShortcutIcon1(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "1", child);

	    frm.setShortcutIcon2(request.getParameter("shortcutIcon2"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "2", frm.getShortcutIcon2(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "2", child);
	    
	    frm.setShortcutIcon3(request.getParameter("shortcutIcon3"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "3", frm.getShortcutIcon3(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "3", child);
	    
	    frm.setShortcutIcon4(request.getParameter("shortcutIcon4"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "4", frm.getShortcutIcon4(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "4", child);

	    frm.setShortcutIcon5(request.getParameter("shortcutIcon5"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "5", frm.getShortcutIcon5(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "5", child);

	    frm.setShortcutIcon6(request.getParameter("shortcutIcon6"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "6", frm.getShortcutIcon6(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "6", child);

	    frm.setShortcutIcon7(request.getParameter("shortcutIcon7"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "7", frm.getShortcutIcon7(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "7", child);

	    frm.setShortcutIcon8(request.getParameter("shortcutIcon8"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "8", frm.getShortcutIcon8(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "8", child);

	    frm.setShortcutIcon9(request.getParameter("shortcutIcon9"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "9", frm.getShortcutIcon9(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "9", child);

	    frm.setShortcutIcon10(request.getParameter("shortcutIcon10"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_ICON + "10", frm.getShortcutIcon10(), uto);
	    hm.put(PreferenceTO.SHORTCUT_ICON + "10", child);
	    
	    String content = "";
	    if (frm.getShortcutIcon1()!=null && !frm.getShortcutIcon1().equals("") && 
	    		frm.getShortcutName1()!=null && !frm.getShortcutName1().equals("") &&
	    		frm.getShortcutOpen1()!=null && !frm.getShortcutOpen1().equals("")) {
	    	content = request.getParameter("shortcutURL1");
	    }
	    frm.setShortcutURL1(content);
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "1", frm.getShortcutURL1(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "1", child);
	    

	    content = "";
	    if (frm.getShortcutIcon2()!=null && !frm.getShortcutIcon2().equals("") && 
	    		frm.getShortcutName2()!=null && !frm.getShortcutName2().equals("") &&
	    		frm.getShortcutOpen2()!=null && !frm.getShortcutOpen2().equals("")) {
	    	content = request.getParameter("shortcutURL2");
	    }
	    frm.setShortcutURL2(content);
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "2", frm.getShortcutURL2(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "2", child);
	    
	    content = "";
	    if (frm.getShortcutIcon3()!=null && !frm.getShortcutIcon3().equals("") && 
	    		frm.getShortcutName3()!=null && !frm.getShortcutName3().equals("") &&
	    		frm.getShortcutOpen3()!=null && !frm.getShortcutOpen3().equals("")) {
	    	content = request.getParameter("shortcutURL3");
	    }
	    frm.setShortcutURL3(content);
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "3", frm.getShortcutURL3(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "3", child);

	    content = "";
	    if (frm.getShortcutIcon4()!=null && !frm.getShortcutIcon4().equals("") && 
	    		frm.getShortcutName4()!=null && !frm.getShortcutName4().equals("") &&
	    		frm.getShortcutOpen4()!=null && !frm.getShortcutOpen4().equals("")) {
	    	content = request.getParameter("shortcutURL4");
	    }
	    frm.setShortcutURL4(content);	    
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "4", frm.getShortcutURL4(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "4", child);	    
	    
	    content = "";
	    if (frm.getShortcutIcon5()!=null && !frm.getShortcutIcon5().equals("") && 
	    		frm.getShortcutName5()!=null && !frm.getShortcutName5().equals("") &&
	    		frm.getShortcutOpen5()!=null && !frm.getShortcutOpen5().equals("")) {
	    	content = request.getParameter("shortcutURL5");
	    }
	    frm.setShortcutURL5(content);	    
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "5", frm.getShortcutURL5(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "5", child);	    

	    content = "";
	    if (frm.getShortcutIcon6()!=null && !frm.getShortcutIcon6().equals("") && 
	    		frm.getShortcutName6()!=null && !frm.getShortcutName6().equals("") &&
	    		frm.getShortcutOpen6()!=null && !frm.getShortcutOpen6().equals("")) {
	    	content = request.getParameter("shortcutURL6");
	    }
	    frm.setShortcutURL6(content);	    
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "6", frm.getShortcutURL6(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "6", child);	    
	    
	    content = "";
	    if (frm.getShortcutIcon7()!=null && !frm.getShortcutIcon7().equals("") && 
	    		frm.getShortcutName7()!=null && !frm.getShortcutName7().equals("") &&
	    		frm.getShortcutOpen7()!=null && !frm.getShortcutOpen7().equals("")) {
	    	content = request.getParameter("shortcutURL7");
	    }
	    frm.setShortcutURL7(content);	    
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "7", frm.getShortcutURL7(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "7", child);	    
	    
	    content = "";
	    if (frm.getShortcutIcon8()!=null && !frm.getShortcutIcon8().equals("") && 
	    		frm.getShortcutName8()!=null && !frm.getShortcutName8().equals("") &&
	    		frm.getShortcutOpen8()!=null && !frm.getShortcutOpen8().equals("")) {
	    	content = request.getParameter("shortcutURL8");
	    }
	    frm.setShortcutURL8(content);
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "8", frm.getShortcutURL8(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "8", child);	    

	    content = "";
	    if (frm.getShortcutIcon9()!=null && !frm.getShortcutIcon9().equals("") && 
	    		frm.getShortcutName9()!=null && !frm.getShortcutName9().equals("") &&
	    		frm.getShortcutOpen9()!=null && !frm.getShortcutOpen9().equals("")) {
	    	content = request.getParameter("shortcutURL9");
	    }
	    frm.setShortcutURL9(content);
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "9", frm.getShortcutURL9(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "9", child);

	    content = "";
	    if (frm.getShortcutIcon10()!=null && !frm.getShortcutIcon10().equals("") && 
	    		frm.getShortcutName10()!=null && !frm.getShortcutName10().equals("") &&
	    		frm.getShortcutOpen10()!=null && !frm.getShortcutOpen10().equals("")) {
	    	content = request.getParameter("shortcutURL10");
	    }
	    frm.setShortcutURL10(content);	    
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_URL + "10", frm.getShortcutURL10(), uto);
	    hm.put(PreferenceTO.SHORTCUT_URL + "10", child);	    
	    
	    
	    frm.setShortcutOpen1(request.getParameter("shortcutOpen1"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "1", frm.getShortcutOpen1(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "1", child);

	    frm.setShortcutOpen2(request.getParameter("shortcutOpen2"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "2", frm.getShortcutOpen2(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "2", child);
	    
	    frm.setShortcutOpen3(request.getParameter("shortcutOpen3"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "3", frm.getShortcutOpen3(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "3", child);

	    frm.setShortcutOpen4(request.getParameter("shortcutOpen4"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "4", frm.getShortcutOpen4(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "4", child);
	    
	    frm.setShortcutOpen5(request.getParameter("shortcutOpen5"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "5", frm.getShortcutOpen5(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "5", child);	    
	    
	    frm.setShortcutOpen6(request.getParameter("shortcutOpen6"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "6", frm.getShortcutOpen6(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "6", child);
	    
	    frm.setShortcutOpen7(request.getParameter("shortcutOpen7"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "7", frm.getShortcutOpen7(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "7", child);
	    
	    frm.setShortcutOpen8(request.getParameter("shortcutOpen8"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "8", frm.getShortcutOpen8(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "8", child);	    
	    
	    frm.setShortcutOpen9(request.getParameter("shortcutOpen9"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "9", frm.getShortcutOpen9(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "9", child);
	    
	    frm.setShortcutOpen10(request.getParameter("shortcutOpen10"));
	    child = new PreferenceTO(PreferenceTO.SHORTCUT_OPEN + "10", frm.getShortcutOpen10(), uto);
	    hm.put(PreferenceTO.SHORTCUT_OPEN + "10", child);	    
	    
	    
        frm.setHideProject(hideProjectList);
        child = new PreferenceTO(PreferenceTO.HIDE_PROJECT, hideProjectList, uto);
	    hm.put(PreferenceTO.HIDE_PROJECT, child);
        
	    frm.setTaskInputFormat(request.getParameter("taskInputFormat"));
        child = new PreferenceTO(PreferenceTO.INPUT_TASK_FORMAT, frm.getTaskInputFormat(), uto);
	    hm.put(PreferenceTO.INPUT_TASK_FORMAT, child);

        child = new PreferenceTO(PreferenceTO.WARNING_DAY_TASK, frm.getWarningTaskDays(), uto);
        hm.put(PreferenceTO.WARNING_DAY_TASK, child);

        child = new PreferenceTO(PreferenceTO.CRITICAL_DAY_TASK, frm.getCriticalTaskDays(), uto);
        hm.put(PreferenceTO.CRITICAL_DAY_TASK, child);
        
        child = new PreferenceTO(PreferenceTO.NOTIFICATION_BUS_CLASS, frm.getNotifiChannels(), uto);
        hm.put(PreferenceTO.NOTIFICATION_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.CALEND_SYNC_BUS_CLASS, frm.getCalendarSyncClasses(), uto);
        hm.put(PreferenceTO.CALEND_SYNC_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.CONVERTER_BUS_CLASS, frm.getConverterClasses(), uto);
        hm.put(PreferenceTO.CONVERTER_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.OVERVIEW_PROJ_CLASS, frm.getOverviewProjectClasses(), uto);
        hm.put(PreferenceTO.OVERVIEW_PROJ_CLASS, child);

        child = new PreferenceTO(PreferenceTO.OCCURRENCE_BUS_CLASS, frm.getOccurrenceSources(), uto);
        hm.put(PreferenceTO.OCCURRENCE_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.KB_INDEX_FOLDER, frm.getKbIndexFolder(), uto);
        hm.put(PreferenceTO.KB_INDEX_FOLDER, child);
        
        child = new PreferenceTO(PreferenceTO.KB_BUS_CLASS, frm.getKbClasses(), uto);
        hm.put(PreferenceTO.KB_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.GADGET_BUS_CLASS, frm.getGadgetClasses(), uto);
        hm.put(PreferenceTO.GADGET_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.REPOSITORY_BUS_CLASS, frm.getRepositoryClasses(), uto);
        hm.put(PreferenceTO.REPOSITORY_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.AUTH_BUS_CLASS, frm.getAuthenticationClasses(), uto);
        hm.put(PreferenceTO.AUTH_BUS_CLASS, child);
        
        child = new PreferenceTO(PreferenceTO.IMP_EXP_BUS_CLASS, frm.getImpExpClasses(), uto);
        hm.put(PreferenceTO.IMP_EXP_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.ARTIFACT_EXPORT_CLASS, frm.getArtifactExpClasses(), uto);
        hm.put(PreferenceTO.ARTIFACT_EXPORT_CLASS, child);

        child = new PreferenceTO(PreferenceTO.SNIP_ARTIFACT_BUS_CLASS, frm.getSnipArtifactClasses(), uto);
        hm.put(PreferenceTO.SNIP_ARTIFACT_BUS_CLASS, child);

        child = new PreferenceTO(PreferenceTO.LDAP_HOST, frm.getLdapHost(), uto);
        hm.put(PreferenceTO.LDAP_HOST, child);

        child = new PreferenceTO(PreferenceTO.LDAP_PORT, frm.getLdapPort(), uto);
        hm.put(PreferenceTO.LDAP_PORT, child);

        child = new PreferenceTO(PreferenceTO.LDAP_UID_REGISTER, frm.getLdapUIDRegister(), uto);
        hm.put(PreferenceTO.LDAP_UID_REGISTER, child);

        child = new PreferenceTO(PreferenceTO.UPLOAD_MAX_SIZE, frm.getUploadMaxFile(), uto);
        hm.put(PreferenceTO.UPLOAD_MAX_SIZE, child);

        child = new PreferenceTO(PreferenceTO.ARTIFACT_MAX_SIZE, frm.getArtifactMaxFile(), uto);
        hm.put(PreferenceTO.ARTIFACT_MAX_SIZE, child);
        
        child = new PreferenceTO(PreferenceTO.GENERAL_CURRENCY, frm.getCurrencyLocale(), uto);
        hm.put(PreferenceTO.GENERAL_CURRENCY, child);

        child = new PreferenceTO(PreferenceTO.GENERAL_METAFIELD_TIMEOUT, frm.getMaxMetaFieldTimeout(), uto);
        hm.put(PreferenceTO.GENERAL_METAFIELD_TIMEOUT, child);

        child = new PreferenceTO(PreferenceTO.GENERAL_DEFAULT_CAPACITY, frm.getDefaultCapacity(), uto);
        hm.put(PreferenceTO.GENERAL_DEFAULT_CAPACITY, child);
        
        child = new PreferenceTO(PreferenceTO.NEW_VERSION_URL, frm.getNewVersionUrl(), uto);
        hm.put(PreferenceTO.NEW_VERSION_URL, child);       

        child = new PreferenceTO(PreferenceTO.TASK_REPORT_URL, frm.getTaskReportUrl(), uto);
        hm.put(PreferenceTO.TASK_REPORT_URL, child);       

        child = new PreferenceTO(PreferenceTO.SURVEY_REPORT_URL, frm.getSurveyReportUrl(), uto);
        hm.put(PreferenceTO.SURVEY_REPORT_URL, child);       

        child = new PreferenceTO(PreferenceTO.EXPENSE_REPORT_URL, frm.getExpenseReportUrl(), uto);
        hm.put(PreferenceTO.EXPENSE_REPORT_URL, child);       

        child = new PreferenceTO(PreferenceTO.GADGET_WIDTH, frm.getGadgetWidth(), uto);
        hm.put(PreferenceTO.GADGET_WIDTH, child);
        
        String pin = uto.getPreference().getPreference(PreferenceTO.PIN_TASK_LIST);
        child = new PreferenceTO(PreferenceTO.PIN_TASK_LIST, pin, uto);
        hm.put(PreferenceTO.PIN_TASK_LIST, child);

        return hm;
	}
	
	
	private String getIndexProgressBar(HttpServletRequest request) throws Exception{

        IndexEngineDelegate ieDel = new IndexEngineDelegate(); 
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    StringBuffer response = new StringBuffer();
	    
	    if (uto.getUsername().equals(RootTO.ROOT_USER)){

	        UserDelegate udel = new UserDelegate();
	        UserTO root = new UserTO();
	        root.setUsername(RootTO.ROOT_USER);
	        root = udel.getObjectByUsername(root);

	        String classes = root.getPreference().getPreference(PreferenceTO.KB_BUS_CLASS);
		    if (classes!=null) {
		        String[] classList = classes.split(";");
		        if (classList!=null && classList.length>0) {
		            for (int i = 0; i<classList.length; i++) {

		                String classStr = classList[i].trim();
		                KbIndex kbus = ieDel.getKbClass(classStr);
                        response.append(" <tr class=\"pagingFormBody\"><td>&nbsp;</td>");		                
	                    if (kbus!=null) {
	                        String contextLabel = this.getBundleMessage(request, kbus.getContextLabel(), true);
			                String label = this.getBundleMessage(request, "label.manageOption.kb.index") + " - " + contextLabel;
			                response.append("<td class=\"formTitle\">" + label + ":&nbsp;</td>\n");
			                response.append("<td class=\"formBody\">");      			      

	                        PreferenceTO pto = root.getPreference(); 
	                        
	                        String cursorKey = PreferenceTO.KB_CURSOR_PREFIX + kbus.getUniqueName();
	                        long cursor = new Long(pto.getPreference(cursorKey)).longValue();
	                        
	                        String maxKey = PreferenceTO.KB_MAX_PREFIX + kbus.getUniqueName();
	                        long maxId = new Long(pto.getPreference(maxKey)).longValue();
	                        
	                        response.append(HtmlUtil.getProgressBar(cursor, maxId, true));
	                    }
    
	                    response.append("</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
		            }
		        }
		    }	    
	    }
	    return response.toString();
	}
		
}
