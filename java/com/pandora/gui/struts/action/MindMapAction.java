package com.pandora.gui.struts.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RiskTO;
import com.pandora.RootTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.gui.struts.form.MindMapForm;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class MindMapAction extends GeneralStrutsAction {

	private long nodeIdSeq = 1;
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
		String forward = "viewMindMap";
		MindMapForm frm = (MindMapForm)form;	
		String uri = request.getRequestURI();
		String rootId = request.getParameter("entityid");
	    frm.setServerURI("http://" + SessionUtil.getUri(request) + uri + "?operation=loadMindMap&entityid=" + rootId);
	    frm.setFormTitle(this.getBundleMessage(request, "title.mindMapForm"));
		return mapping.findForward(forward);
	}

	
	public ActionForward loadMindMap(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		StringBuffer content = new StringBuffer("");
		PlanningDelegate del = new PlanningDelegate();
		
	    try {
	    	String root = request.getParameter("entityid");	        

	        if (root!=null && !root.trim().equals("")) {
	        	this.nodeIdSeq = 1;
	        	
		    	PlanningTO pto = del.getSpecializedObject(new PlanningTO(root));
		    	
		    	content.append("<map version=\"0.8.1\">\n");
		    	content.append("<node CREATED=\"1227270466718\" ID=\"Freemind_Link_" + this.getNextNodeId() + 
		    				"\" MODIFIED=\"1227270466718\" TEXT=\"" + this.getTitle(request, pto) + "\">\n");
		    	content.append("<font BOLD=\"true\" NAME=\"Verdana\" SIZE=\"16\"/>");
		    	content.append(this.getUserRelation(pto, true));
		    	content.append(this.getNodeConnection(request, pto, 0));
		    	content.append("</node>\n");
		    	content.append("</map>");		    	
	        }
	    	
		}catch(Exception e){
			content.append(e.getMessage());

		} finally{
			//put response content into Standard output
			ServletOutputStream sos;
			try {		    
				sos = response.getOutputStream();	
				response.setContentType("text/xml; charset=ISO-8859-1");
				response.setContentLength(content.toString().length());
				sos.write(content.toString().getBytes());
				sos.close();

			} catch (IOException e2) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on Connector Listener process", e2);
			}
		}

		return null;
	}
	
	
	private String getNodeConnection(HttpServletRequest request, PlanningTO pto, int level) throws Exception {
	    String response = "";
	    PlanningDelegate del = new PlanningDelegate();
	    
    	Vector<PlanningRelationTO> list = pto.getRelationList();
    	if (list!=null && level<=2 ) {
    	    Iterator<PlanningRelationTO> i = list.iterator();
    	    while(i.hasNext()) {
    	        PlanningRelationTO prto = (PlanningRelationTO)i.next();
    	        
    	        if (prto.getPlanning().getId().equals(pto.getId())) {
    	            PlanningTO right = del.getSpecializedObject(prto.getRelated());
    	            response = response + this.getNodeTag(request, right, true, prto.getRelationType());
    	            //level++;
    	            //response = response + getNodeConnection(request, right, level);
    	            //level--;
    	            response = response + "</node>";
    	        } else {
    	            PlanningTO left = del.getSpecializedObject(prto.getPlanning());
    	            response = response + this.getNodeTag(request, left, false, prto.getRelationType());
    	            //level++;
    	            //response = response + getNodeConnection(request, left, level);
    	            //level--;
    	            response = response + "</node>";		    	            
    	        }
    	    }
    	}
    	
    	return response;
	}
	
	
	private String getNodeTag(HttpServletRequest request, PlanningTO node, boolean isRight, String relationType) throws Exception {
		StringBuffer buff = new StringBuffer();
		
		String relationLabel = StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.relationTag.relation." + relationType));
		
		buff.append("<node CREATED=\"1227270466718\" ID=\"Freemind_Link_" + this.getNextNodeId() + 
				"\" MODIFIED=\"1227270466718\" POSITION=\"" + (isRight?"right":"left") + "\" STYLE=\"fork\" " + 
						"TEXT=\"" +	relationLabel + "\"><edge STYLE=\"linear\" WIDTH=\"1\" /><font BOLD=\"false\" NAME=\"Verdana\" SIZE=\"8\"/>");		
		
		buff.append("<node CREATED=\"1227270466718\" ID=\"Freemind_Link_" + this.getNextNodeId() +
					"\" MODIFIED=\"1227270466718\" POSITION=\"" + (isRight?"right":"left") + 
					"\" STYLE=\"bubble\" LINK=\"../do/viewMindMap?operation=prepareForm&entityid=" + node.getId() + "\" " + 
					"TEXT=\"" +	this.getTitle(request, node) + "\"><edge STYLE=\"linear\" WIDTH=\"1\" />");		
		buff.append("<hook><text>" + this.getNodeTip(request, node) + "</text></hook>");		
		buff.append(this.getUserRelation(node, isRight));
		buff.append("</node>");
		
		return buff.toString();	
	}
	
	private String getUserRelation(PlanningTO node, boolean isRight) throws Exception {	
		String content = "";

		if (node instanceof RequirementTO) {
			RequirementTO req = (RequirementTO)node;
			String key = "Freemind_Link_" + this.getNextNodeId();
			content = content + this.getActor(key, isRight, req.getRequester());
			
		} else if (node instanceof TaskTO) {
			TaskTO tsk = (TaskTO)node;
			if (tsk.getAllocResources()!=null) {
				Iterator<ResourceTaskTO> i = tsk.getAllocResources().iterator();
				while(i.hasNext()) {
					ResourceTaskTO rtto = i.next();
					String key = "Freemind_Link_" + this.getNextNodeId();
					content = content + this.getActor(key, isRight, rtto.getResource());
				}
			}
			
		} else if (node instanceof RiskTO) {
			RiskTO rsk = (RiskTO)node;
			if (rsk.getResponsible()!=null && !rsk.getResponsible().trim().equals("")) {
				UserTO uto = new UserTO("0");
				uto.setUsername(rsk.getResponsible());
				String key = "Freemind_Link_" + this.getNextNodeId();
				content = content + this.getActor(key, isRight, uto);				
			}
		}
		return content;
	}

	private String getNodeTip(HttpServletRequest request, PlanningTO node) throws Exception {
		String content = "";

		content = content + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.manageTask.name")) + ": " + StringUtil.formatWordToFreeMind(node.getName());
		if (node.getDescription()!=null && !node.getDescription().equals("")) {
			content = content + " \n" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.manageTask.desc")) + ": " + StringUtil.formatWordToFreeMind(node.getDescription());	
		}
		
		if (node instanceof RequirementTO) {			
			RequirementTO req = (RequirementTO)node;
			if (req.getRequester()!=null) {
				content = content + " \n" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.requestPriority")) + ": "
				 						 + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.requestPriority." + req.getPriority().intValue()));
				if (req.getRequirementStatus()!=null){
					String reqst = req.getRequirementStatus().getName();
					content = content + " \n" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.requestStatus")) + ": " + reqst;
				}
			}
			
		} else if (node instanceof TaskTO) {
			TaskTO tsk = (TaskTO)node;
			if (tsk.getAllocResources()!=null && tsk.getAllocResources().size()>0) {
				Iterator<ResourceTaskTO> i = tsk.getAllocResources().iterator();
				content = content + " \n" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.manageTask.taskStatus")) + ": ";
				while(i.hasNext()) {
					ResourceTaskTO rtto = (ResourceTaskTO)i.next();
					if (tsk.getAllocResources().size()==1) {
						content = content +  StringUtil.formatWordToFreeMind(rtto.getTaskStatus().getName());
					} else {
						content = content +  StringUtil.formatWordToFreeMind(rtto.getTaskStatus().getName() + " (" + rtto.getResource().getUsername() + ") ");	
					}
				}
			}
		}
		return content;
	}
	
	
	private String getTitle(HttpServletRequest request, PlanningTO node) {
		String content = this.getBundleMessage(request, "title.mindMapForm." + node.getEntityType()) + ": " + node.getId();
		content = StringUtil.formatWordToFreeMind(content);
	    return content;
	}

	
	private String getActor(String key, boolean isRight, UserTO actor) {
		String response = "";
		if (!actor.getUsername().equals(RootTO.ROOT_USER)) {
			response = "<node CREATED=\"1227270466718\" ID=\"" + key + "\" MODIFIED=\"1227270466718\" POSITION=\"" + 
				(isRight?"right":"left") + "\" STYLE=\"bubble\" TEXT=\"" + actor.getUsername() + "\">" +
				"<font BOLD=\"true\" ITALIC=\"true\" NAME=\"Verdana\" SIZE=\"12\"/></node>"; 
		}
		return response;
	}
	
	private long getNextNodeId(){
		return ++nodeIdSeq;
	}
	
}
