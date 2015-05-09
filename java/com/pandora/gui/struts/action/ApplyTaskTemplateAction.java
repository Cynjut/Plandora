package com.pandora.gui.struts.action;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.CustomNodeTemplateTO;
import com.pandora.DecisionNodeTemplateTO;
import com.pandora.LeaderTO;
import com.pandora.NodeTemplateTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.StepNodeTemplateTO;
import com.pandora.TaskStatusTO;
import com.pandora.TemplateTO;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.SaveWorkflowException;
import com.pandora.exception.ZeroCapacityException;
import com.pandora.gui.struts.form.ApplyTaskTemplateForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class ApplyTaskTemplateAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showApplyTaskTemplate";
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		
		try {
		    ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;
		    frm.clear();
		    
		    //fetch the id of current workflow instance
		    Integer instance = tdel.getInstance(frm.getTemplateId(), frm.getReqId());
		    if (instance!=null) {
		    	frm.setInstanceId(instance.toString());	
		    } else {
		    	frm.setInstanceId("");
		    }

		    request.getSession().setAttribute("projectList", new Vector());
		    request.getSession().setAttribute("categoryList", new Vector());
		    request.getSession().setAttribute("resourceAllocated", new Vector());
		    
			this.refresh(mapping, form, request, response);
			
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(forward);
	}

	
	public ActionForward refreshAfterProjectChange(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showApplyTaskTemplate";
		try {
		    ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;
		    ProjectTO pto = new ProjectTO(frm.getProjectId());
		    frm.setResourceId("-1");
		    
		    CategoryDelegate cdel = new CategoryDelegate();
		    Vector cList = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, pto, false);
		    request.getSession().setAttribute("categoryList", cList);			
		    		    
		    Vector ulist = new Vector();
		    UserDelegate udel = new UserDelegate();
		    Vector resourceList = udel.getResourceByProject(pto.getId(), false, true);		    

		    if (resourceList!=null && resourceList.size()>1) {
			    ResourceTO dummy = new ResourceTO("-1");
			    dummy.setName(this.getBundleMessage(request, "title.formApplyTaskTemplate.resource.select"));
			    ulist.add(dummy);			    	
		    }
		    
		    ulist.addAll(resourceList);		
		    request.getSession().setAttribute("resourceList", ulist);
		    
		} catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);
	}

	
	public ActionForward saveWorkflow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showApplyTaskTemplate";
		String errLabel = "message.formApplyTaskTemplate.saveworkflowErr";
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		try {
			
		    ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;

		    //save the workflow starting at the root node...
		    UserTO currentUser = SessionUtil.getCurrentUser(request);		    
		    tdel.saveWorkflow(frm.getCurrentRoot().getId(), new Integer(frm.getInstanceId()), frm.getReqId(), currentUser);

		    this.refresh(mapping, form, request, response);
		    
			//set success message into http session
			this.setSuccessFormSession(request, "message.formApplyTaskTemplate.saveworkflow");

		} catch(ZeroCapacityException e){
			this.setErrorFormSession(request, "error.manageTask.zeroCapacity", e);			
		} catch(SaveWorkflowException es){
			if (es!=null && es.getMsgInBundle()!=null) {
				errLabel = es.getMsgInBundle();
			}
			this.setErrorFormSession(request, errLabel, es);			
		} catch(Exception e){
			this.setErrorFormSession(request, errLabel , e);
		}
		return mapping.findForward(forward);	    
	}

	
	public ActionForward renderImage(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){

	    ServletOutputStream sos = null;
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		
		try {
		    ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;		    
		    UserTO currentUser = SessionUtil.getCurrentUser(request);
		    
		    String bgcolor = request.getParameter("bgcolor");
		    
		    NodeTemplateTO root = frm.getCurrentRoot();
		    if (root!=null && bgcolor!=null && !bgcolor.trim().equals("") ) {
		    	
				//draw an image representation of nodes
			    BufferedImage image = tdel.drawWorkFlow(root, currentUser, bgcolor, false);
				frm.setHtmlMap(root.getHtmlMapCoords());
				
	            response.setContentType("image/png");  
	            response.setHeader("Cache-Control", "no-cache");  
			    sos = response.getOutputStream();
			    javax.imageio.ImageIO.write(image, "PNG", sos);		    
		    }

		} catch(Exception e) {
		    e.printStackTrace();
		}
	    
	    return null;
	}
	
	
	public ActionForward editTemplateNode(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showApplyTaskTemplate";    
		try {
			ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;
		    TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		    
		    NodeTemplateTO filter = new NodeTemplateTO(frm.getId());
			String reqId = frm.getReqId();
			if (frm.getInstanceId()!=null && frm.getInstanceId().equals("") && reqId!=null && reqId.equals("-1")){
				reqId = null;
			}		    
		    NodeTemplateTO ntto = tdel.getNodeTemplate(filter, frm.getInstanceId(), reqId);
		    this.getActionFormFromTransferObject(ntto, mapping, form, request, response);
		    		    
		} catch(Exception e){
		   this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);	    
	}

	
	public ActionForward updateNode(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showApplyTaskTemplate";    
		try {
			ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;			
		    TaskTemplateDelegate tdel = new TaskTemplateDelegate();			
	
		    CustomNodeTemplateTO cntto = new CustomNodeTemplateTO();
		    cntto.setPlanningId(frm.getReqId());
		    cntto.setTemplateId(frm.getTemplateId());
		    cntto.setNodeTemplateId(frm.getId());
		    if (frm.getInstanceId()!=null && !frm.getInstanceId().trim().equals("")) {
		    	cntto.setInstanceId(new Integer(frm.getInstanceId()));	
		    }

		    cntto.setDescription(frm.getDescription());
		    cntto.setName(frm.getName());
		    cntto.setIsParentTask(new Boolean(frm.getIsParentTask()));
		    
		    if (frm.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
		    	cntto.setQuestionContent(frm.getQuestionContent());
			    cntto.setCategoryId(null);
			    cntto.setResource(null);
		    } else {
		    	
			    cntto.setCategoryId(frm.getCategoryId());
			    cntto.setProjectId(frm.getProjectId());
			    Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");
			    cntto.setResource(tdel.getResourceListFromVector(resList));
			    cntto.setQuestionContent(null);
		    }
		    
		    tdel.saveCustomNodeTemplate(cntto);

		    Integer instance = tdel.getInstance(frm.getTemplateId(), frm.getReqId());
		    if (instance!=null) {
		    	frm.setInstanceId(instance.toString());	
		    } else {
		    	frm.setInstanceId("");
		    }
		    
			//set success message into http session
			this.setSuccessFormSession(request, "message.formApplyTaskTemplate.save");

			this.refresh(mapping, form, request, response);			

		} catch(Exception e){
		   this.setErrorFormSession(request, "message.formApplyTaskTemplate.saveErr", e);
		}
		return mapping.findForward(forward);	    
	}
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showApplyTaskTemplate";
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
	    ProjectDelegate pdel = new ProjectDelegate();
	    
		try {
			ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;
			UserTO currentUser = SessionUtil.getCurrentUser(request);
			
			Vector projList = pdel.getProjectListForManagement((LeaderTO)currentUser, true);
			request.getSession().setAttribute("projectList", projList);
			
			//get template tree from data base
			TemplateTO tto = tdel.getTaskTemplate(frm.getTemplateId());
			NodeTemplateTO filter = tto.getRootNode();
			if (frm.getInstanceId()!=null && !frm.getInstanceId().trim().equals("")) {
				filter.setInstanceId(new Integer(frm.getInstanceId()));	
			}
			
			RequirementTO rto = null;
			String reqId = frm.getReqId();
			if (frm.getInstanceId()!=null && frm.getInstanceId().equals("") && reqId!=null && reqId.equals("-1")){
				reqId = null;
			} else {
				RequirementDelegate rd = new RequirementDelegate();
				rto = rd.getRequirement(new RequirementTO(reqId));
				this.refreshAfterProjectChange(mapping, form, request, response);
			}
			
			NodeTemplateTO root = tdel.getNodeTemplateTree(filter, reqId);
			frm.setCurrentRoot(root);
			if (rto!=null) {
				frm.setProjectId(rto.getProject().getId());
			} else {
				if (root.getProject()==null || root.getProject().getId()==null) {
					frm.setProjectId(frm.getProjId());
				} else {
					frm.setProjectId(root.getProject().getId());	
				}						
			}
			
			if (tto.getCategory()!=null && !tto.getCategory().getName().equals("")) {
				String ofLabel = super.getBundleMessage(request, "label.of");
				frm.setCategoryName(" " + ofLabel + " " + tto.getCategory().getName());	
			} else {
				frm.setCategoryName("");
			}
			
			//render diagram to get the coordenates of nodes...
		    tdel.drawWorkFlow(root, currentUser, "FFFFFF", false);
			frm.setHtmlMap(root.getHtmlMapCoords());
			
		    Vector tlist = tdel.getNodeListByTemplate(frm.getTemplateId(), frm.getInstanceId(), reqId);
		    request.getSession().setAttribute("nodeTemplateList", tlist);

		    if (frm.getId()!=null && !frm.getId().equals("")) {
			    NodeTemplateTO ntto = root.getNode(frm.getId(), false);
			    if (ntto!=null) {
			    	this.getActionFormFromTransferObject(ntto, mapping, form, request, response);	
			    }
		    }
		    
		} catch(Exception e){
		   this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    
		return mapping.findForward(forward);	    
	}
	
	
	public ActionForward clearAll(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showApplyTaskTemplate";
	    try {
	    	ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;
	    	frm.setInstanceId("");
	    	this.refresh(mapping, form, request, response);
	    	
		} catch(Exception e){
	        this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return mapping.findForward(forward);
	}

	
	public ActionForward showConfirmation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showClearWFConfirm";
		return mapping.findForward(forward);		
	}
	
	public ActionForward addResource(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showApplyTaskTemplate";
	    try {
	     
	    	ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;
		    Locale loc = SessionUtil.getCurrentLocale(request);

		    ResourceTaskTO rtto = this.getResTaskFromVector(frm, request, loc);
	        Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");
	        if (resList==null) {
	        	resList = new Vector();
	        }
	        
	        boolean notExists = true;
		    Iterator i = resList.iterator();
		    while(i.hasNext()){
		        ResourceTaskTO buff = (ResourceTaskTO)i.next();
		        if (buff.getResource().getId().equals(rtto.getResource().getId())) {
		            this.setErrorFormSession(request, "message.resourceIntoListExists", null);
		            notExists = false;
		            break;
		        }
		    }
	        
		    if (notExists) {
		        resList.addElement(rtto);
		        request.getSession().setAttribute("resourceAllocated", resList);		            		        
		    }

		} catch(Exception e){
	        this.setErrorFormSession(request, "error.generic.showFormError", e);
		}	    
	    return mapping.findForward(forward);	
	}
	
	
	public ActionForward removeResource(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showApplyTaskTemplate";
	    try {

		    Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");
		    String resId = request.getParameter("removeResId");
		    
		    boolean alreadyExists = false;
		    Iterator i = resList.iterator();
		    while(i.hasNext()){
		        ResourceTaskTO rtto = (ResourceTaskTO)i.next();
		        if (rtto.getResource().getId().equals(resId)) {
			        Integer state = null;
			        if (rtto.getTaskStatus()!=null) {
			            state = rtto.getTaskStatus().getStateMachineOrder();    
			        }
			        if (state!=null && (state.equals(TaskStatusTO.STATE_MACHINE_PROGRESS) || 
			                state.equals(TaskStatusTO.STATE_MACHINE_CLOSE) ||
			                state.equals(TaskStatusTO.STATE_MACHINE_CANCEL) ||
			                state.equals(TaskStatusTO.STATE_MACHINE_HOLD))) {
			            this.setErrorFormSession(request, "message.resourceCannotBeRemoved", null);
			            alreadyExists = true;
			        }
		        }		            
		    }
		    
	       	if (!alreadyExists) {
	   		    i = resList.iterator();
	   		    while(i.hasNext()){
	   		        ResourceTaskTO rtto = (ResourceTaskTO)i.next();
	   		        if (rtto.getResource().getId().equals(resId)) {
	   	        	    resList.remove(rtto);
	   	        	    break;
	   		        }
	   		    }
	   		    request.getSession().setAttribute("resourceAllocated", resList);    		    
	       	}
		    
		} catch(Exception e){
	        this.setErrorFormSession(request, "error.generic.showFormError", e);
		}	    
	    return mapping.findForward(forward);	
	}

	
	private ResourceTaskTO getResTaskFromVector(ApplyTaskTemplateForm frm, HttpServletRequest request, Locale loc) throws BusinessException{
        ResourceTaskTO rtto = new ResourceTaskTO();
        UserDelegate udel = new UserDelegate(); 
        
        String resourceId = frm.getResourceId();
        String initialDate = frm.getInitDate();
        String estimTime = frm.getEstimatedTime();
        
        //get the resource object from database
        ResourceTO rto = new ResourceTO(resourceId);
        rto.setProject(new ProjectTO(frm.getProjectId()));
        rto = udel.getResource(rto);
        
        rtto.setResource(rto);        
        //rtto.setTask(new TaskTO(frm.getId()));
        rtto.setLabel(rto.getName());
        
        //update new estimated date and start date
        rtto.setEstimatedTime(new Integer((int)(StringUtil.getStringToFloat(estimTime, loc) * 60)));
        rtto.setStartDate(DateUtil.getDateTime(initialDate, super.getCalendarMask(request), loc));
        
        return rtto;
	}
	
	private void getActionFormFromTransferObject(NodeTemplateTO ntto, ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		UserTO handler = SessionUtil.getCurrentUser(request);
	    TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		
		ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;
	    frm.setDescription(ntto.getDescription());
	    frm.setId(ntto.getId());
	    frm.setName(ntto.getName());
	    frm.setTemplateId(ntto.getPlanningId());
	    
		if (ntto.getProject()!=null && ntto.getProject().getId()!=null) {
		    frm.setProjectId(ntto.getProject().getId());	
		}		
	    
	    frm.setIsParentTask(ntto.getIsParentTask());
	    
	    frm.setResourceId(null);
	    frm.setEstimatedTime(null);
	    frm.setInitDate(null);
	    frm.setQuestionContent(null);
	    
    	frm.setNodeType(ntto.getNodeType());	
	    if (ntto.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
	    	frm.setQuestionContent(((DecisionNodeTemplateTO)ntto).getQuestionContent());
	    	request.getSession().setAttribute("resourceAllocated", new Vector());
	    } else {
		    
	    	this.refreshAfterProjectChange(mapping, form, request, response);
		    
	    	frm.setCategoryId(this.resolveCategory((StepNodeTemplateTO)ntto, frm.getProjectId()));		    	
		    Vector resList = tdel.getResourceListFromString(((StepNodeTemplateTO)ntto).getResourceId(), handler);
		    if (resList!=null && resList.size()>0) {
		    	frm.setResourceId(tdel.getResourceListFromVector(resList));	
		    }
		    request.getSession().setAttribute("resourceAllocated", resList);
	    }		
	}
	
	
	private String resolveCategory(StepNodeTemplateTO step, String projectId){
		String response = step.getCategoryId();
		
		if (response!=null && response.equals(CategoryTO.DEFAULT_CATEGORY_ID) && step.getCategoryRegex()!=null) {
			try {
				CategoryDelegate cdel = new CategoryDelegate();
				Vector list = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, new ProjectTO(projectId), false);
				if (list!=null) {
					Iterator i = list.iterator();
					Pattern pattern = Pattern.compile(step.getCategoryRegex());
					
					while(i.hasNext()) {
						CategoryTO cto = (CategoryTO)i.next();
						Matcher matcher = pattern.matcher(cto.getName());
		                if (matcher.find()) {
		                	response = cto.getId();
		                	break;
			            }
					}
				}
			} catch(Exception e){
				response = step.getCategoryId();
			}
		}
		
		return response;
	}
	
}
