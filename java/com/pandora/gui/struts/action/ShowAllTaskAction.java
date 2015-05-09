package com.pandora.gui.struts.action;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.NodeTemplateTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.TemplateTO;
import com.pandora.UserTO;
import com.pandora.bus.occurrence.IterationOccurrence;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.delegate.TaskHistoryDelegate;
import com.pandora.delegate.TaskStatusDelegate;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ShowAllTaskForm;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into 'Shows all Tasks' form
 */
public class ShowAllTaskAction extends GeneralStrutsAction {

	
	/**
	 * Shows all Task for specific project
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
		ProjectDelegate pdel = new ProjectDelegate();
		String forward = "showAllTask";
		
		try {
		    
		    //clear current form
		    ShowAllTaskForm frm = (ShowAllTaskForm)form;
	    	frm.setShowWorkflowDiagram("off");
	    	
		    this.clearMessages(request);
			ProjectTO pto = new ProjectTO(frm.getProjectRelated()); 
			pto = pdel.getProjectObject(pto, false);
			
			//set current user into form object
			frm.setSaveMethod(null, SessionUtil.getCurrentUser(request));
			
			//get all resource Tasks from data base and put into http session (to be displayed by combo)
			refreshList(pto, frm, request);

			this.refreshAuxiliarList(pto, request);

		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.showAllTaskForm", e);
		}

		return mapping.findForward(forward);
	}

	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showAllTask");
	}	
	
	private void refreshAuxiliarList(ProjectTO pto, HttpServletRequest request) throws BusinessException{
		if (pto!=null) {
			//get all Task Status from data base and put into http session (to be displayed by combo)
			Vector statList = this.getListOfStatusForCombo(request);
			request.getSession().setAttribute("statusList", statList);

			//get all Resources of current project from data base and put into http session (to be displayed by combo)			
			Vector custList = this.getListOfResourcesForCombo(pto, request);
			request.getSession().setAttribute("resourceList", custList);
					
			OccurrenceDelegate odel = new OccurrenceDelegate();
			Vector iterations = odel.getOccurenceListByType(pto.getId(), IterationOccurrence.class.getName());
			request.getSession().setAttribute("iterationList", iterations);			
		}
	}
		
	private void refreshList(ProjectTO pto, ShowAllTaskForm frm, HttpServletRequest request) throws BusinessException{
		ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
		Vector<ResourceTaskTO> rtList = rtdel.getListByProject(pto, frm.getStatusSelected(), frm.getResourceSelected(), true);
		request.getSession().setAttribute("allResTaskList", rtList);		
	}

	public ActionForward showWorkFlow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String fwd = "showAllTask";
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		try {
		    ShowAllTaskForm frm = (ShowAllTaskForm)form;	
		    UserTO currentUser = SessionUtil.getCurrentUser(request);
		    NodeTemplateTO root = null;
		    
			String instanceId = request.getParameter("instanceId");
			String entityId = request.getParameter("planningId");
			
			String strFwd = request.getParameter("fwd");			
			if (strFwd!=null && !strFwd.trim().equals("")) {
				fwd = strFwd;
			}
			
		    TemplateTO tto = tdel.getTaskTemplateByInstance(instanceId);
		    if (tto!=null) {
		    	NodeTemplateTO filter = tto.getRootNode();
		    	filter.setInstanceId(new Integer(frm.getInstanceId()));
			    root = tdel.getNodeTemplateTree(filter, entityId);

				String bgcolor = request.getParameter("bgcolor");
				if (bgcolor==null || bgcolor.trim().equals("")) {
					bgcolor = "FFFFFF";
				}
				
		    	//draw an image representation of nodes
			    BufferedImage image = tdel.drawWorkFlow(root, currentUser, bgcolor, true);
			    frm.setHtmlMap(root.getHtmlMapCoords(true));

			    frm.setWorkFlowDiagram(image);
			    
			    this.refreshAuxiliarList(filter.getProject(), request);
		    }
		} catch(Exception e) {
		    e.printStackTrace();
		}	
		return mapping.findForward(fwd);
	}

	
	public ActionForward clickNodeTemplate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			ShowAllTaskForm frm = (ShowAllTaskForm)form;
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        
	        PrintWriter out = response.getWriter();
	        String content = this.createDiagramNodeTip(request, frm.getPlanningId()).toString();
	        out.println(content);
	        
	        out.flush();
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showAllTaskForm", e);
		}

		return null;
	}


	public StringBuffer createDiagramNodeTip(HttpServletRequest request, String taskId) throws BusinessException {
		TaskDelegate tdel = new TaskDelegate();
		TaskTO tto = new TaskTO(taskId);
		tto = tdel.getTaskObject(tto);
		StringBuffer content = new StringBuffer("");
		
		if (tto!=null) {
			tto.setHandler(SessionUtil.getCurrentUser(request));
			content.append(tto.getName() + "<br />");
			
			ProjectTO pto = tto.getProject();
			ProjectDelegate pdel = new ProjectDelegate();
			pto = pdel.getProjectObject(pto, true);
			String prjLbl = super.getBundleMessage(request, "label.manageTask.project");
			content.append("<b>" + prjLbl + "</b>: " + pto.getName()  + "<br />");
			
			String envolved = tto.getInvolvedResources(true);	
			if (envolved!=null && !envolved.trim().equals("")) {
				String envLbl = super.getBundleMessage(request, "label.showAllReqForm.grid.showResources");
				content.append("<b>" + envLbl + "</b>: " + envolved);				
			}

			if (tto.isParentTask()) {
				Vector children = tdel.getSubTasksList(tto);
				if (children!=null) {
					String envLbl = null;
					Iterator i = children.iterator();
					while(i.hasNext()) {
						TaskTO child = (TaskTO)i.next();
						child.setHandler(tto.getHandler());
						String childEnvolved = child.getInvolvedResources(true);
						if (childEnvolved!=null && !childEnvolved.trim().equals("")) {
							
							if (envLbl == null) {
								envLbl = super.getBundleMessage(request, "label.showAllReqForm.grid.showResources");
								content.append("<b>" + envLbl + "</b>: " + childEnvolved);
							} else {
								content.append("; " + childEnvolved);	
							}
										
						}
					}
									
				}
			}
			
			int openNum = tdel.getReopenTimes(tto);
			if (openNum>0) {
				content.append("<br />");
		    	String reopenLbl = super.getBundleMessage(request, "label.showAllTaskForm.reopenLbl");
		    	String timesLbl = super.getBundleMessage(request, "label.showAllTaskForm.reopenTimes");
		    	content.append("<b>" + reopenLbl + "</b>: " + openNum + " " + timesLbl);				
			}
			
		} else {
		    content.append(super.getBundleMessage(request, "title.formApplyTaskTemplate.legend.notyet"));	
		}
		
		return content;
	}
	
	
	public ActionForward updateInBatch(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){

		String forward = "showAllTask";
		TaskDelegate tdel = new TaskDelegate();
		TaskHistoryDelegate thdel = new TaskHistoryDelegate();
		ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
		
		try {
		    ShowAllTaskForm frm = (ShowAllTaskForm)form;			
			frm.setShowWorkflowDiagram("off");
			boolean saveProcessed = false;

			Vector tskList = (Vector)request.getSession().getAttribute("allResTaskList");
	        Iterator i = tskList.iterator();
	        while(i.hasNext()) {
	        	ResourceTaskTO rtto = (ResourceTaskTO)i.next();
	        	TaskTO tto = rtto.getTask();

	        	boolean updateTask = false;
	        	boolean updateResTask = false;
	        	String newIteration = request.getParameter("cb_" + tto.getId() + "_iteration");
	        	String newBillable = request.getParameter("cb_" + rtto.getId() + "_billable");
	        	
	        	if (newIteration!=null && !newIteration.trim().equals("") &&
	        			!newIteration.equals(tto.getIteration())) {
	        		tto = tdel.getTaskObject(tto);	
	        		if (newIteration.trim().equals("-1") && tto.getIteration()!=null) {
	        			tto.setIteration(null);
	        		} else {
	        			tto.setIteration(newIteration);	
	        		}
	        		updateTask = true;
	        	}
	        	
	        	if (newBillable!=null && !newBillable.trim().equals("") && (rtto.getBillableStatus()==null ||
	        			!newBillable.equals(rtto.getBillableStatus().booleanValue()?"1":"0"))) {
	        		rtto.setBillableStatus(new Boolean(newBillable.equals("1")));
	        		updateResTask = true;
	        	}
	        	
	        	if (updateTask) {
	        		tdel.updateByResource(tto);
	        		rtto.setTask(tto);
	        		rtto.setHandler(SessionUtil.getCurrentUser(request));
	        		thdel.insert(rtto, null, null); //insert a new history entry...
	        		saveProcessed = true;
	        	}
	        	
	        	if (updateResTask) {
	        		rtdel.updateByResource(rtto);
	        		saveProcessed = true;
	        	}
	        }
	        
			if (saveProcessed) {
				this.setSuccessFormSession(request, "message.showAllTaskForm.success");
				
				ProjectDelegate pdel = new ProjectDelegate();			
				ProjectTO pto = new ProjectTO(frm.getProjectRelated());
				pto = pdel.getProjectObject(pto, false);
				this.refreshList(pto, frm, request);
				
			} else {
				this.setSuccessFormSession(request, "message.showAllTaskForm.notsaved");
			}
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.showAllTaskForm", e);
		}

		return mapping.findForward(forward);
	}
	
	
	public ActionForward prepareWorkflow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showAllTaskWorkflow";			    		
	    ShowAllTaskForm frm = (ShowAllTaskForm)form;
	    frm.setShowWorkflowDiagram("off");
		return mapping.findForward(forward);
	}

	
	public ActionForward renderImage(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    ServletOutputStream sos = null;
		try {
		    ShowAllTaskForm frm = (ShowAllTaskForm)form;	
			if (frm.getWorkFlowDiagram()!=null) {
	            response.setContentType("image/png");  
	            response.setHeader("Cache-Control", "no-cache");  
			    sos = response.getOutputStream();
			    javax.imageio.ImageIO.write(frm.getWorkFlowDiagram(), "PNG", sos);
		    }
		} catch(Exception e) {
		    e.printStackTrace();
		}
	    return null;
	}

	
	/**
	 * Get a list of task Status to be used into combo box.
	 */
	private Vector getListOfStatusForCombo(HttpServletRequest request) throws BusinessException{
		TaskStatusDelegate tsdel = new TaskStatusDelegate();
		Vector statList = tsdel.getTaskStatusList();
		Vector temp = new Vector();
		
		RequirementStatusTO statusAllFake = new RequirementStatusTO();
		statusAllFake.setId("-2");
		statusAllFake.setName(super.getBundleMessage(request, "label.all"));
		
		RequirementStatusTO statusAll2Fake = new RequirementStatusTO();
		statusAll2Fake.setId("-1");
		statusAll2Fake.setName(super.getBundleMessage(request, "label.showAllTaskForm.allExceptFinishing"));
		
		temp.addElement(statusAllFake);
		temp.addElement(statusAll2Fake);
		temp.addAll(statList);
	    
		return temp;
	}

	
	/**
	 * Get a list of resources of current project to be used into combo box.
	 */
	private Vector<ResourceTO> getListOfResourcesForCombo(ProjectTO pto, HttpServletRequest request) throws BusinessException{
		Vector<ResourceTO> temp = new Vector<ResourceTO>();
	    if (pto!=null) {
			Vector<ResourceTO> resList = pto.getInsertResources();
			ResourceTO allResources = new ResourceTO("-1");
			allResources.setName(super.getBundleMessage(request, "label.all"));
			temp.addElement(allResources);
			temp.addAll(resList);	    	
	    }
	    
		return temp;
	}
	
}
