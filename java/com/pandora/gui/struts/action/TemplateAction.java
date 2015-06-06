package com.pandora.gui.struts.action;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.DecisionNodeTemplateTO;
import com.pandora.NodeTemplateTO;
import com.pandora.ProjectTO;
import com.pandora.StepNodeTemplateTO;
import com.pandora.TemplateTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.TemplateForm;
import com.pandora.gui.struts.form.UserForm;
import com.pandora.helper.SessionUtil;

public class TemplateAction extends GeneralStrutsAction {

	private static final String DIAGRAM_NODE = "DIAGRAM_NODE";
	
	private static final String CURSOR_NODE  = "CURSOR_NODE";
	
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		TaskTemplateDelegate ttdel = new TaskTemplateDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
		ProjectDelegate pdel = new ProjectDelegate();
		
		String forward = "showTemplate";	    
		try {
			this.clearForm(form, request);		

		    this.clearMessages(request);
		    Vector<TransferObject> enableList = new Vector<TransferObject>();
		    enableList.addElement(new TransferObject("1", this.getBundleMessage(request, "label.yes")));
		    enableList.addElement(new TransferObject("0", this.getBundleMessage(request, "label.no")));
		    request.getSession().setAttribute("enableList", enableList);
		    
		    Vector<CategoryTO> catList = cdel.getCategoryListByType(CategoryTO.TYPE_WORKFLOW, new ProjectTO(ProjectTO.PROJECT_ROOT_ID), false);
		    request.getSession().setAttribute("categoryList", catList);

			Vector<TemplateTO> list = ttdel.getTemplateListByProject(null, true);
		    request.getSession().setAttribute("templateList", list);	
		    
			Vector<ProjectTO> prjList = pdel.getProjectList();
			ProjectTO dummy = new ProjectTO("-1");
			dummy.setName(this.getBundleMessage(request, "label.category.anyProject"));			
			prjList.insertElementAt(dummy, 0);
			request.getSession().setAttribute("projectList", prjList);
		
			request.getSession().setAttribute("taskCategoryList", new Vector<CategoryTO>());
			request.getSession().setAttribute("templateNodeList", new Vector<TemplateTO>());
			
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);		
	}

	
	public ActionForward saveTemplate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showTemplate";
		return mapping.findForward(forward);		
	}

	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showTemplate");		
	}
	
	
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		try {
			this.clearForm(form, request);			
		} catch (Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward("showTemplate");		
	}

	
	public ActionForward editTemplate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		TaskTemplateDelegate ttdel = new TaskTemplateDelegate();
		String forward = "showTemplate";	    
		try {
			TemplateForm tfrm = (TemplateForm)form;
			TemplateTO tto = ttdel.getTaskTemplate(tfrm.getId());
			
			NodeTemplateTO root = ttdel.getNodeTemplateTree(tto.getRootNode(), null);
			request.getSession().setAttribute(DIAGRAM_NODE, root);
			
			this.getActionFormFromTransferObject(tto, tfrm, request);
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);		
	}

	
	public ActionForward removeTemplate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showTemplate");		
	}

	
	public ActionForward refreshList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showTemplate");		
	}


	public ActionForward commitNode(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){

		TemplateForm tfrm = (TemplateForm)form;
		
		NodeTemplateTO newNode = null;
		if (tfrm.getNodeType().equals("STEP")) {
			newNode = new StepNodeTemplateTO(null);
			newNode.setNodeType(NodeTemplateTO.NODE_TEMPLATE_STEP);	
			((StepNodeTemplateTO)newNode).setCategoryId(tfrm.getStepCategory());
			((StepNodeTemplateTO)newNode).setCategoryRegex(tfrm.getStepCategoryRegex());
			((StepNodeTemplateTO)newNode).setResourceId(tfrm.getStepResourceList());
		} else {
			newNode = new DecisionNodeTemplateTO(null);
			newNode.setNodeType(NodeTemplateTO.NODE_TEMPLATE_DECISION);
			if (tfrm.getDecisionIfFalseNextNodeId()!=null && !tfrm.getDecisionIfFalseNextNodeId().equals("")) {
				((DecisionNodeTemplateTO)newNode).setNextNodeIfFalse(new NodeTemplateTO(tfrm.getDecisionIfFalseNextNodeId()));	
			} else {
				((DecisionNodeTemplateTO)newNode).setNextNodeIfFalse(null);
			}
			((DecisionNodeTemplateTO)newNode).setQuestionContent(tfrm.getDecisionQuestion());
		}
		newNode.setName(tfrm.getNodeName());
		newNode.setDescription(tfrm.getNodeDescription());
		newNode.setProject(new ProjectTO(tfrm.getNodeRelatedProjectId()));
		if (tfrm.getNodeNextNodeId()!=null && !tfrm.getNodeNextNodeId().equals("")) {
			newNode.setNextNode(new NodeTemplateTO(tfrm.getNodeNextNodeId()));	
		} else {
			newNode.setNextNode(null);
		}

		NodeTemplateTO root = (NodeTemplateTO)request.getSession().getAttribute(DIAGRAM_NODE);
		if (root==null) {
			request.getSession().setAttribute(DIAGRAM_NODE, newNode);
		} else {
			NodeTemplateTO node = (NodeTemplateTO)request.getSession().getAttribute(CURSOR_NODE);
			if (node.getNextNode()==null) {
				node.setNextNode(newNode);	
			} else if (node.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
				node = (DecisionNodeTemplateTO)request.getSession().getAttribute(CURSOR_NODE);
				if (((DecisionNodeTemplateTO)node).getNextNodeIfFalse()==null) {
					((DecisionNodeTemplateTO)node).setNextNodeIfFalse(newNode);
				}
			}
		}	
		request.getSession().setAttribute(CURSOR_NODE, newNode);
		
		tfrm.setShowCommitRevert("off");
		
		if (root==null || root.getNextNode()==null || root.getNextNode() instanceof StepNodeTemplateTO) {
			tfrm.setShowDecision("on");	
		} else {
			tfrm.setShowDecision("off");
		}
		
		return mapping.findForward("showTemplate");		
	}


	public ActionForward revertNode(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		TemplateForm tfrm = (TemplateForm)form;
		tfrm.clear();
		tfrm.setShowCommitRevert("off");
		return mapping.findForward("showTemplate");		
	}

	
	public ActionForward addStep(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		TemplateForm tfrm = (TemplateForm)form;
		tfrm.clear();
		tfrm.setNodeName("(...)");				
		tfrm.setNodeType("STEP");
		tfrm.setShowCommitRevert("on");
		return mapping.findForward("showTemplate");		
	}
	

	public ActionForward addDecision(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		TemplateForm tfrm = (TemplateForm)form;
		tfrm.clear();
		tfrm.setNodeName("(...)");				
		tfrm.setNodeType("DECISION");
		tfrm.setShowCommitRevert("on");
		return mapping.findForward("showTemplate");		
	}

	
	public ActionForward editTemplateNode(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		TaskTemplateDelegate ttdel = new TaskTemplateDelegate();
		String forward = "showTemplate";	    
		try {
			
			NodeTemplateTO root = (NodeTemplateTO)request.getSession().getAttribute(DIAGRAM_NODE);
			String templateId = root.getPlanningId();
			
			TemplateForm tfrm = (TemplateForm)form;
			NodeTemplateTO node = new NodeTemplateTO(tfrm.getId());
			node = ttdel.getNodeTemplate(node, null, null);

			if (node!=null) {
				if (node.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
					tfrm.setStepCategoryRegex(((StepNodeTemplateTO)node).getCategoryRegex());
					tfrm.setStepCategory(((StepNodeTemplateTO)node).getCategoryId());
					tfrm.setStepResourceList(((StepNodeTemplateTO)node).getResourceId());
					tfrm.setNodeType("STEP");
					
				} else if (node.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
					tfrm.setDecisionQuestion(((DecisionNodeTemplateTO)node).getQuestionContent());
					if (((DecisionNodeTemplateTO)node).getNextNodeIfFalse()!=null) {
						tfrm.setDecisionIfFalseNextNodeId(((DecisionNodeTemplateTO)node).getNextNodeIfFalse().getId());	
					} else {
						tfrm.setDecisionIfFalseNextNodeId("");
					}
					tfrm.setNodeType("DECISION");
				}
				
				tfrm.setNodeName(node.getName());
				tfrm.setNodeDescription(node.getDescription());
				tfrm.setNodeId(node.getId());
				tfrm.setNodeRelatedTemplateId(templateId);
				if (node.getProject()!=null && node.getProject().getId()!=null) {
					tfrm.setNodeRelatedProjectId(node.getProject().getId());
					this.refreshTaskCategory(node.getProject().getId(), request);
				} else {
					this.refreshTaskCategory(null, request);
				}
				if (node.getNextNode()!=null) {
					tfrm.setNodeNextNodeId(node.getNextNode().getId());	
				} else {
					tfrm.setNodeNextNodeId("");
				}
				
				//set current cursor...
				request.getSession().setAttribute(CURSOR_NODE, node);
				
				this.refreshNodeList(request);
			}
			
			
		} catch (BusinessException e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);		
	}
	
	public ActionForward renderImage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		TaskTemplateDelegate ttdel = new TaskTemplateDelegate();
		ServletOutputStream sos = null;
		try {
			this.clearMessages(request);
			TemplateForm tfrm = (TemplateForm)form;			
			NodeTemplateTO root = (NodeTemplateTO)request.getSession().getAttribute(DIAGRAM_NODE);
		    String bgcolor = request.getParameter("bgcolor");
		    
		    if (root!=null && bgcolor!=null && !bgcolor.trim().equals("")) {
		    	
		    	UserTO uto = SessionUtil.getCurrentUser(request);
		    	
				//draw an image representation of nodes
			    BufferedImage image = ttdel.drawWorkFlow(root, uto, bgcolor, false);
				tfrm.setHtmlMap(root.getHtmlMapCoords());
				
	            response.setContentType("image/png");  
	            response.setHeader("Cache-Control", "no-cache");  
			    sos = response.getOutputStream();
			    javax.imageio.ImageIO.write(image, "PNG", sos);		    
		    }
			
		} catch (Exception e) {
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return null;		
	}

	private void getActionFormFromTransferObject(TemplateTO to, TemplateForm frm, HttpServletRequest request){
		frm.setName(to.getName());
		frm.setCategory(to.getCategory().getId());
		frm.setEnable(to.getFinalDate()==null?"1":"0");
	}
	
	//private TemplateTO getTransferObjectFromActionForm(TemplateForm frm, HttpServletRequest request){
	//	TemplateTO to = new TemplateTO();
	//	return to;
	//}

	private void refreshTaskCategory(String projectId, HttpServletRequest request) throws BusinessException{
		CategoryDelegate cdel = new CategoryDelegate();
	    Vector<CategoryTO> taskCatList = cdel.getCategoryListByType(CategoryTO.TYPE_TASK, new ProjectTO(projectId), false);
	    request.getSession().setAttribute("taskCategoryList", taskCatList);		
	}

	private void refreshNodeList(HttpServletRequest request) throws BusinessException{
		HashMap<String,NodeTemplateTO> hmList = new HashMap<String,NodeTemplateTO>();
		Vector<NodeTemplateTO> nodeList = new Vector<NodeTemplateTO>();
		
		NodeTemplateTO emptyNode = new NodeTemplateTO("");
		emptyNode.setGenericTag(super.getBundleMessage(request, "title.formApplyTaskTemplate.type.end"));
		nodeList.add(emptyNode);
		
		NodeTemplateTO root = (NodeTemplateTO)request.getSession().getAttribute(DIAGRAM_NODE);
		if (root!=null) {
			this.refreshNodeList(hmList, root, request);
			Iterator<NodeTemplateTO> i = hmList.values().iterator();
			while(i.hasNext()) {
				NodeTemplateTO node = (NodeTemplateTO)i.next();
				nodeList.add(node);
			}
		}
		request.getSession().setAttribute("templateNodeList", nodeList);		
	}

	private void refreshNodeList(HashMap<String,NodeTemplateTO> hmList, NodeTemplateTO currentNode, HttpServletRequest request) throws BusinessException{
		if (currentNode!=null && hmList.get(currentNode.getId())==null) {
			
			if (currentNode.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {

				String label = super.getBundleMessage(request, "title.formApplyTaskTemplate.type.step");
				currentNode.setGenericTag("["  + label + "] " + currentNode.getName());
				hmList.put(currentNode.getId(), currentNode);
				
				this.refreshNodeList(hmList, ((StepNodeTemplateTO)currentNode).getNextNode(), request);
			} else if (currentNode.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
				
				String label = super.getBundleMessage(request, "title.formApplyTaskTemplate.type.decision");
				currentNode.setGenericTag("["  + label + "] " + currentNode.getName());
				hmList.put(currentNode.getId(), currentNode);
				
				this.refreshNodeList(hmList, ((DecisionNodeTemplateTO)currentNode).getNextNode(), request);
				this.refreshNodeList(hmList, ((DecisionNodeTemplateTO)currentNode).getNextNodeIfFalse(), request);
			}					
		}
	}
	

	
	private void clearForm(ActionForm form, HttpServletRequest request) throws BusinessException{
		TemplateForm frm = (TemplateForm)form;
		frm.clear();
		request.getSession().removeAttribute(DIAGRAM_NODE);
		frm.setSaveMethod(UserForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
		refreshNodeList(request);
	}
		
}
