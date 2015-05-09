package com.pandora.gui.struts.action;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.ConnectorDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.GanttViewerForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed by form to open the Gantt chart.
 */
public class GanttViewerAction extends GeneralStrutsAction {

	/**
	 * Show Gantt view
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){

	    String forward = "showGantt";
		try {
		    RequirementDelegate rdel = new RequirementDelegate();
		    ConnectorDelegate cdel = new ConnectorDelegate();
		    GanttViewerForm gvfrm = (GanttViewerForm)form;
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    gvfrm.clear();
		    
		    gvfrm.setLanguage(uto.getLanguage());
		    gvfrm.setCountry(uto.getCountry());
		    gvfrm.setUsername(uto.getUsername());
		    gvfrm.setPassword(cdel.generatePublicKey(request, uto));
		    
		    String hst = request.getServerName();
		    String prt = request.getServerPort()+"";
		    if (!prt.equals("0")) {
		        prt = ":" + prt; 
		    } else {
		        prt = "";
		    }
		    
		    String uri = request.getRequestURI();
		    uri = uri.substring(0, uri.indexOf("/do") + 3);
		    
		    gvfrm.setServerURI("http://" + hst + prt + uri + "/connectorListener");
		    
		    if (gvfrm.getType()!=null){
		        if (gvfrm.getType().equals(GanttViewerForm.PROJECT_GANTT)){
		            Vector list = this.getGanttByProject(gvfrm, request);
		            setFormDataByTaskList(list, gvfrm, request);
		            
		        } else if (gvfrm.getType().equals(GanttViewerForm.SHOW_REQ_GANTT)){
		            Vector list = this.getGanttByRequirement(gvfrm, request);
		            RequirementTO rto = new RequirementTO(gvfrm.getRequirementId());
		            rto = rdel.getRequirement(rto);
		            gvfrm.setProjectId(rto.getProject().getId());
		            setFormDataByTaskList(list, gvfrm, request);
		            
		        } else if (gvfrm.getType().equals(GanttViewerForm.SHOW_RES_GANTT)){
		            Vector list = this.getGanttByResource(gvfrm, request);
		            setFormDataByResourceTaskList(list, gvfrm, request);
		        }

		    }
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showGanttForm", e);
		}

		return mapping.findForward(forward);
	}


	/**
	 * Get a list of tasks objects from database by projectId
	 */
	private Vector getGanttByProject(GanttViewerForm frm, HttpServletRequest request) throws BusinessException {	    
	    TaskDelegate tdel = new TaskDelegate();
	    ProjectTO pto = new ProjectTO(frm.getProjectId());
	    Timestamp oneMonthAgo = DateUtil.getChangedDate(DateUtil.getNow(), Calendar.MONTH, -1);	    
	    return tdel.getTaskListByProject(pto, oneMonthAgo, true, true);
	}
	
	/**
	 * Get a list of tasks objects from database by requirementId
	 */
	private Vector getGanttByRequirement(GanttViewerForm frm, HttpServletRequest request) throws BusinessException {
	    RequirementDelegate rdel = new RequirementDelegate();
	    TaskDelegate tdel = new TaskDelegate();
	    RequirementTO rto = new RequirementTO(frm.getRequirementId());
	    rto = rdel.getRequirement(rto);
	    return tdel.getTaskListByRequirement(rto, rto.getProject(), true);
	}

	/**
	 * Get a list of tasks objects from database by resourceId
	 */
	private Vector getGanttByResource(GanttViewerForm frm, HttpServletRequest request) throws BusinessException {	    
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
	    ResourceTO rto = new ResourceTO(frm.getResourceId());	    
	    return rtdel.getTaskListByResource(rto, true);
	}
	
	/**
	 * Set data into struts gantt form based on task objects list <br>
	 * Creates the PARAM commands to open the gantt applet
	 */
	private void setFormDataByTaskList(Vector taskList, GanttViewerForm frm, HttpServletRequest request) throws BusinessException{
		UserDelegate udel = new UserDelegate();
	    int jobNum = 0, allocNum = 0, depNum = 0;
	    StringBuffer resBody = new StringBuffer();
	    StringBuffer jobBody = new StringBuffer();
	    StringBuffer layerBody = new StringBuffer();
	    StringBuffer allocBody = new StringBuffer();
	    Vector depBody = new Vector();
	    
        UserTO uto = SessionUtil.getCurrentUser(request);
        if (udel.userIsLeader(uto, new ProjectTO(frm.getProjectId()))) {
            frm.setEditable("TRUE");
        }

	    //set initial date
        Timestamp iniDate = DateUtil.getNow();
        if (taskList!=null && taskList.size()>0){
            iniDate = this.setInitialDateParam(true, taskList, frm, request);
            
		    //set slot number
		    this.setSlotNumParam(true, taskList, frm);
		
		    //set "RESNUM" PARAM
		    frm.setResNum(taskList.size()+"");
		    
        } else {
            frm.setSlots("20");
		    frm.setResNum("1");
        }
	    
	    //get a list of resource objects based on Project id
	    Vector resList = udel.getResourceByProject(frm.getProjectId(), true, false);
	    
	    //add a special resource into the list 
	    UserTO root = udel.getRoot();
	    ResourceTO resourceRoot = new ResourceTO(root.getId());
	    String defLabel = this.getBundleMessage(request, "label.manageTask.define");
	    resourceRoot.setUsername(defLabel);
	    resourceRoot.setName(defLabel);
	    resourceRoot.setColor("C2C2C2");
	    resList.addElement(resourceRoot);
	    
	    
	    //set "RES_?" and "JOB_?" PARAM
	    if (taskList!=null && taskList.size()>0){
		    for(int i=0; i<taskList.size(); i++) {
		        TaskTO tto = (TaskTO)taskList.elementAt(i);
		        resBody.append(tto.getResBodyFormat(i+1));

		        String jobStr = tto.getJobBodyFormat(jobNum);
		        if (!jobStr.equals("")){
			        jobBody.append(tto.getJobBodyFormat(jobNum));
			        jobNum+=tto.getAllocResources().size();
			        
			        allocNum+= tto.getAllocBodyFormat(allocBody, allocNum, iniDate);
		        }
		        
		        if (tto.getRelationList()!=null && tto.getRelationList().size()>0 ) {
		        	Vector temp = tto.getRelationshipBodyFormat(depNum);
		        	depBody.addAll(temp);
		        	depNum = depNum + depBody.size();
		        }
		    }
		    frm.setResBody(resBody.toString());
		    frm.setJobBody(jobBody.toString());
		    frm.setAllocUnitBody(allocBody.toString());

		    //set "JOBNUM" PARAM
		    frm.setJobNum(jobNum+"");

		    //set "ALLOCNUM" PARAM
		    frm.setAllocUnitNum(allocNum+"");

		    //set "LAYER_?" PARAM
		    for(int i=0; i<resList.size(); i++) {
		        ResourceTO rto = (ResourceTO)resList.elementAt(i);
		        layerBody.append(rto.getLayerBodyFormat(i+1));
		    }
		    frm.setLayerBody(layerBody.toString());

		    //set "LAYERNUM" PARAM
		    frm.setLayerNum(resList.size()+"");	        

		    //set "DEPNUM" PARAM
		    frm.setDepedenceNum(depBody.size()+"");	        

		    //set "DEP_?" PARAM
		    frm.setDepedenceBody(depBody);	        
	    }
	}

	
	private void setFormDataByResourceTaskList(Vector resTaskList, GanttViewerForm frm, HttpServletRequest request) throws BusinessException{
	    int jobNum = 0, allocNum = 0;
	    StringBuffer resBody = new StringBuffer();
	    StringBuffer jobBody = new StringBuffer();
	    StringBuffer layerBody = new StringBuffer();
	    StringBuffer allocBody = new StringBuffer();
	    
	    if (resTaskList!=null && resTaskList.size()>0){
	        
		    //set initial date	        
		    Timestamp iniDate = this.setInitialDateParam(false, resTaskList, frm, request);
		    
		    //set slot number
		    this.setSlotNumParam(false, resTaskList, frm);
		    
		    //set "RESNUM" PARAM
		    frm.setResNum(resTaskList.size()+"");

		    //set "RES_?" and "JOB_?" PARAM	    
		    for(int i=0; i<resTaskList.size(); i++) {
		        ResourceTaskTO rtto = (ResourceTaskTO)resTaskList.elementAt(i);
		        TaskTO tto = rtto.getTask();
		        resBody.append(tto.getResBodyFormat(i+1));
		        
		        jobNum++;
		        jobBody.append(rtto.getJobBodyFormat(jobNum));
		        
                allocBody.append(rtto.getAllocBodyFormat(rtto.getAllocList().size(), iniDate));		        
		    }
		    frm.setResBody(resBody.toString());
		    frm.setJobBody(jobBody.toString());
		    frm.setAllocUnitBody(allocBody.toString());

		    //set "JOBNUM" PARAM
		    frm.setJobNum(jobNum+"");

		    //set "ALLOCNUM" PARAM
		    frm.setAllocUnitNum(allocNum+"");

		    //set "LAYER_?" PARAM
		    UserDelegate udel = new UserDelegate();
		    ResourceTO rto = new ResourceTO(frm.getResourceId());
		    rto = (ResourceTO) udel.getUser(rto);
	        layerBody.append(rto.getLayerBodyFormat(1));
		    frm.setLayerBody(layerBody.toString());

		    //set "LAYERNUM" PARAM
		    frm.setLayerNum("1");	        
	    }	    
	}
	
	
	/**
	 * Calculate the start date and set into Form
	 */
	private Timestamp setInitialDateParam(boolean isFromTaskList, Vector list, GanttViewerForm frm, HttpServletRequest request) throws BusinessException{
	    TaskDelegate tdel = new TaskDelegate();
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
	    Timestamp iniDate = null;
	    
	    Locale loc = SessionUtil.getCurrentLocale(request);
	    if (isFromTaskList){
	        iniDate = tdel.getDateFromTaskList(list, true);    
	    } else {
	        iniDate = rtdel.getDateFromResTaskList(list, true);
	    }
	    frm.setInitialDate(DateUtil.getDate(iniDate, super.getCalendarMask(request), loc));
	    return iniDate;
	}
	
	
	/**
	 * Calculate the number slots between tasks into Form
	 */
	private void setSlotNumParam(boolean isFromTaskList, Vector list, GanttViewerForm frm) throws BusinessException{	    
	    TaskDelegate tdel = new TaskDelegate();
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
	    Timestamp iniDate  = null;
	    Timestamp finalDate = null;
	    
	    if (isFromTaskList){
		    iniDate = tdel.getDateFromTaskList(list, true);
		    finalDate = tdel.getDateFromTaskList(list, false);		    	        
	    } else {
		    iniDate = rtdel.getDateFromResTaskList(list, true);
		    finalDate = rtdel.getDateFromResTaskList(list, false);
	    }
	    frm.setSlots((DateUtil.getSlotBetweenDates(iniDate, finalDate)+30)+"");	    
	}

}
