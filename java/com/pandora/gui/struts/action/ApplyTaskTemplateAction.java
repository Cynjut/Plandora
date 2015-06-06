package com.pandora.gui.struts.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xmlbeans.impl.util.Base64;

import com.pandora.CategoryTO;
import com.pandora.CustomNodeTemplateTO;
import com.pandora.DecisionNodeTemplateTO;
import com.pandora.LeaderTO;
import com.pandora.NodeTemplateTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RootTO;
import com.pandora.StepNodeTemplateTO;
import com.pandora.TaskStatusTO;
import com.pandora.TemplateTO;
import com.pandora.UserTO;
import com.pandora.bus.TaskNodeTemplateBUS;
import com.pandora.bus.occurrence.IterationOccurrence;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.SaveWorkflowException;
import com.pandora.gui.struts.form.ApplyTaskTemplateForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class ApplyTaskTemplateAction extends GeneralStrutsAction {

	HashMap<String, String> recursiveNodes = new HashMap<String, String>();
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showApplyTaskTemplate";
		
		try {
		    ApplyTaskTemplateForm frm = (ApplyTaskTemplateForm)form;
		    frm.clear();
		    
		    defineInstance(frm);

		    request.getSession().setAttribute("iterationList", new Vector<OccurrenceTO>());
		    request.getSession().setAttribute("projectList", new Vector<ProjectTO>());
		    request.getSession().setAttribute("categoryList", new Vector<CategoryTO>());
		    this.putResourceAllocListIntoSession(null, request);
		    
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
		    Vector<CategoryTO> cList = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, pto, false);
		    request.getSession().setAttribute("categoryList", cList);			
		    
		    OccurrenceDelegate odel = new OccurrenceDelegate();
		    Vector<OccurrenceTO> olist = odel.getOccurenceListByType(pto.getId(), IterationOccurrence.class.getName(), false);
		    OccurrenceTO oto = new OccurrenceTO("-1");
		    oto.setName(this.getBundleMessage(request, "label.combo.select"));
		    olist.add(0, oto);
		    request.getSession().setAttribute("iterationList", olist);

		    Vector<ResourceTO> ulist = new Vector<ResourceTO>();
		    UserDelegate udel = new UserDelegate();
		    Vector<ResourceTO> resourceList = udel.getResourceByProject(pto.getId(), false, true);		    

		    if (resourceList!=null && resourceList.size()>1) {
			    ResourceTO dummy = new ResourceTO("-1");
			    dummy.setName(this.getBundleMessage(request, "title.formApplyTaskTemplate.resource.select"));
			    ulist.add(dummy);
			    			    
			    for (ResourceTO rto: resourceList) {
					if (rto.getUsername().equals(RootTO.ROOT_USER)){
						resourceList.remove(rto);
					    UserTO root = udel.getRoot();
					    ResourceTO anyone = new ResourceTO(root.getId());
					    anyone.setName(this.getBundleMessage(request, "label.manageTask.anyRes"));
					    ulist.add(1, anyone);
					    break;
					}
				}
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
		    
		    Integer inst = null;
		    if (frm.getInstanceId()!=null && !frm.getInstanceId().trim().equals("")) {
		    	inst = new Integer(frm.getInstanceId());	
		    }
		    
		    this.recursiveNodes = new HashMap<String, String>();
		    this.saveCustomNodesRecursive(frm.getCurrentRoot(), frm.getTemplateId(), frm.getReqId(), inst, frm.getProjectId());
		    
		    if (tdel.checkSavedNodes(inst, frm.getTemplateId())) {
			    tdel.saveWorkflow(frm.getCurrentRoot().getId(), inst, frm.getReqId(), frm.getProjectId(), currentUser);
			    this.refresh(mapping, form, request, response);
				this.setSuccessFormSession(request, "message.formApplyTaskTemplate.saveworkflow");
		    } else {
		    	this.setSuccessFormSession(request, "message.formApplyTaskTemplate.saveworkflowErr");
		    }
		    

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
		    if (bgcolor==null || bgcolor.trim().equals("")){
		    	bgcolor = "EFEFEF";
		    }

		    NodeTemplateTO root = frm.getCurrentRoot();
		    if (root!=null) {

				//draw an image representation of nodes
			    BufferedImage image = tdel.drawWorkFlow(root, currentUser, bgcolor, false);
				frm.setHtmlMap(root.getHtmlMapCoords());
				
	            response.setContentType("image/png");  
	            response.setHeader("Cache-Control", "no-cache");  
			    sos = response.getOutputStream();
			    if (sos!=null) {			    	
			    	ByteArrayOutputStream out = new ByteArrayOutputStream();
			    	javax.imageio.ImageIO.write(image, "PNG", out);
			    	byte[] base64bytes = Base64.encode(out.toByteArray());
			    	String buffer = "data:image/png;base64," + new String(base64bytes);
			    	sos.write(buffer.getBytes());
			    }
		    }

		} catch(IIOException e) {
			e.printStackTrace();
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
		    	cntto.setIterationId(frm.getIterationId());
			    cntto.setCategoryId(frm.getCategoryId());
			    cntto.setProjectId(frm.getProjectId());
			    
			    @SuppressWarnings("unchecked")
				Vector<ResourceTaskTO> resList = (Vector<ResourceTaskTO>)request.getSession().getAttribute("resourceAllocated");
			    
			    cntto.setResource(tdel.getResourceListFromVector(resList));
			    cntto.setQuestionContent(null);
		    }
		    
		    tdel.saveCustomNodeTemplate(cntto);

		    defineInstance(frm);
		    
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
			
			Vector<ProjectTO> projList = pdel.getProjectListForManagement((LeaderTO)currentUser, true);
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
				if ((frm.getProjectId()==null || frm.getProjectId().trim().equals("")) && rto!=null && rto.getProject()!=null) {
					frm.setProjectId(rto.getProject().getId());
				} else {
					if (frm.getProjId()!=null && !frm.getProjId().equals("")) {
						frm.setProjectId(frm.getProjId());	
					}
				}
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
			
			//render diagram to get the coordinates of nodes...
		    tdel.drawWorkFlow(root, currentUser, "FFFFFF", false);
			frm.setHtmlMap(root.getHtmlMapCoords());
			
			Vector<NodeTemplateTO> tlist = tdel.getNodeListByTemplate(frm.getTemplateId(), frm.getInstanceId(), reqId);
		    request.getSession().setAttribute("nodeTemplateList", tlist);

		    ProjectTO pto =null;
			if (frm.getProjId()!=null && !frm.getProjId().trim().equals("")) {
				pto = new ProjectTO(frm.getProjId());
			} else if (frm.getProjectId()!=null && !frm.getProjectId().trim().equals("")) {
				pto = new ProjectTO(frm.getProjectId());
			}
			if (pto!=null) {
				pto = pdel.getProjectObject(pto, true);
				frm.setProjectName(pto.getName());	
			}
			
		    
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
	        @SuppressWarnings("unchecked")
			Vector<ResourceTaskTO> resList = (Vector<ResourceTaskTO>)request.getSession().getAttribute("resourceAllocated");
	        if (resList==null) {
	        	resList = new Vector<ResourceTaskTO>();
	        }

	        boolean notExists = true;
		    Iterator<ResourceTaskTO> i = resList.iterator();
		    while(i.hasNext()){
		        ResourceTaskTO buff = i.next();
		        if (buff.getResource().getId().equals(rtto.getResource().getId())) {
		            this.setErrorFormSession(request, "message.resourceIntoListExists", null);
		            notExists = false;
		            break;
		        }
		    }
	        
		    if (notExists) {
		        resList.addElement(rtto);
		        this.putResourceAllocListIntoSession(resList, request);	            		        
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

		    @SuppressWarnings("unchecked")
			Vector<ResourceTaskTO> resList = (Vector<ResourceTaskTO>)request.getSession().getAttribute("resourceAllocated");
		    String resId = request.getParameter("removeResId");
		    
		    boolean alreadyExists = false;
		    Iterator<ResourceTaskTO> i = resList.iterator();
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
	   		    this.putResourceAllocListIntoSession(resList, request);
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
        UserTO uto = udel.getUser(rto);
        if (uto.getUsername().equals(RootTO.ROOT_USER)) {
        	rto.setName(this.getBundleMessage(request, "label.manageTask.anyRes"));
        	rto.setUsername(rto.getName());
        } else {
            rto.setProject(new ProjectTO(frm.getProjectId()));
            rto = udel.getResource(rto);        	
        }
        
        rtto.setResource(rto);        
        rtto.setLabel(rto.getName());

        //update new estimated date and start date
        rtto.setEstimatedTime(this.formatTime(request, estimTime, loc));
        rtto.setStartDate(DateUtil.getDateTime(initialDate, super.getCalendarMask(request), loc));
        
        return rtto;
	}
	

	private Integer formatTime(HttpServletRequest request, String estimTime, Locale loc) {
		Integer response = 0;
		UserTO uto = SessionUtil.getCurrentUser(request);
	    String frmtInput = uto.getPreference().getPreference(PreferenceTO.INPUT_TASK_FORMAT);
	    if (!frmtInput.equals("2")) {
	    	response = new Integer((int)(StringUtil.getStringToFloat(estimTime, loc) * 60));
	    } else {
	    	response =  StringUtil.getHHMMToInteger(estimTime);		    	
	    }    		
	    return response;
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
	    frm.setEstimatedTime("0");
	    frm.setInitDate(DateUtil.getDate(DateUtil.getNow(), handler.getCalendarMask(), handler.getLocale()));
	    frm.setQuestionContent(null);
    	frm.setIterationId(null);
	    
    	frm.setNodeType(ntto.getNodeType());	
	    if (ntto.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
	    	frm.setQuestionContent(((DecisionNodeTemplateTO)ntto).getQuestionContent());
	    	putResourceAllocListIntoSession(null, request);	    	
	    } else {
	    	
	    	if ((StepNodeTemplateTO)ntto!=null) {
		    	String iteration = ((StepNodeTemplateTO)ntto).getIterationId();
		    	if (iteration!=null && !iteration.equals("-1")) {
		    		frm.setIterationId(iteration);	
		    	}	    		
	    	}

	    	this.refreshAfterProjectChange(mapping, form, request, response);
		    
	    	frm.setCategoryId(this.resolveCategory((StepNodeTemplateTO)ntto, frm.getProjectId()));
		    Vector<ResourceTaskTO> resList = tdel.getResourceListFromString(((StepNodeTemplateTO)ntto).getResourceId(), null, handler);
		    if (resList!=null && resList.size()>0) {
		    	frm.setResourceId(tdel.getResourceListFromVector(resList));	
		    }
		    putResourceAllocListIntoSession(resList, request);
	    }		
	}
	
	private void putResourceAllocListIntoSession(Vector<ResourceTaskTO> resList, HttpServletRequest request){
		if (resList!=null) {
	    	for (ResourceTaskTO rtto : resList) {
	    		if (rtto.getResource()!=null && rtto.getResource().getUsername().equals(RootTO.ROOT_USER)){ 
	    			rtto.getResource().setUsername(this.getBundleMessage(request, "label.manageTask.anyRes"));
	    			rtto.getResource().setName(rtto.getResource().getUsername());
	    			rtto.setLabel(rtto.getResource().getUsername());
	    		}
			}
	    	request.getSession().setAttribute("resourceAllocated", resList);			
		} else{
			request.getSession().setAttribute("resourceAllocated", new Vector<ResourceTaskTO>());
		}
	}
	
	private String resolveCategory(StepNodeTemplateTO step, String projectId){
		String response = step.getCategoryId();
		
		if (response!=null && response.equals(CategoryTO.DEFAULT_CATEGORY_ID) && step.getCategoryRegex()!=null) {
			try {
				CategoryDelegate cdel = new CategoryDelegate();
				Vector<CategoryTO> list = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, new ProjectTO(projectId), false);
				if (list!=null) {
					Iterator<CategoryTO> i = list.iterator();
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


	private void defineInstance(ApplyTaskTemplateForm frm) throws BusinessException {
		TaskTemplateDelegate tdel = new TaskTemplateDelegate();
		Integer instance = tdel.getInstance(frm.getTemplateId(), frm.getReqId());
		if (instance!=null) {
			frm.setInstanceId(instance.toString());	
		} else {
			frm.setInstanceId("");
		}
	}

	
	private void saveCustomNodesRecursive(NodeTemplateTO node, String templateId, String reqId, Integer instanceId, String defaultProjectId) throws BusinessException{
	    TaskTemplateDelegate tdel = new TaskTemplateDelegate();			

	    if (node!=null && this.recursiveNodes.get(node.getId())==null) {
	    	this.recursiveNodes.put(node.getId(), node.getId());
	    	
		    CustomNodeTemplateTO cntto = new CustomNodeTemplateTO();
		    cntto.setPlanningId(reqId);
		    cntto.setTemplateId(templateId);
		    cntto.setNodeTemplateId(node.getId());
	    	cntto.setInstanceId(instanceId);	

		    cntto.setDescription(node.getDescription());
		    cntto.setName(node.getName());
		    cntto.setIsParentTask(new Boolean(node.getIsParentTask()));
		    
		    if (node.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
		    	cntto.setQuestionContent(((DecisionNodeTemplateTO)node).getQuestionContent());
			    cntto.setCategoryId(null);
			    cntto.setResource(null);
			    
			    if (((DecisionNodeTemplateTO)node).getNextNode()!=null) {
			    	this.saveCustomNodesRecursive(((DecisionNodeTemplateTO)node).getNextNode(), templateId, reqId, instanceId, defaultProjectId);	
			    }
			    if (((DecisionNodeTemplateTO)node).getNextNodeIfFalse()!=null) {
			    	this.saveCustomNodesRecursive(((DecisionNodeTemplateTO)node).getNextNodeIfFalse(), templateId, reqId, instanceId, defaultProjectId);	
			    }
			    
		    } else {
		    	cntto.setIterationId(((StepNodeTemplateTO)node).getIterationId());
			    cntto.setCategoryId(((StepNodeTemplateTO)node).getCategoryId());
			    
			    String projectId = defaultProjectId;
			    if (node.getProject()!=null && node.getProject().getId()!=null && !node.getProject().getId().trim().equals("")){
			    	projectId = node.getProject().getId();
			    }
			    cntto.setProjectId(projectId);
			    
			    String resource = ((StepNodeTemplateTO)node).getResourceId();
			    resource = resource.replaceAll("DEFAULT", TaskNodeTemplateBUS.getDateOfResourceList(DateUtil.getNow()));
			    cntto.setResource(resource);
			    
			    cntto.setQuestionContent(null);
			    
			    this.saveCustomNodesRecursive(((StepNodeTemplateTO)node).getNextNode(), templateId, reqId, instanceId, defaultProjectId);
		    }
		    
		    tdel.saveCustomNodeTemplate(cntto);	    	
	    }
	    		
	}
}
