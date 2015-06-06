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

import com.pandora.AttachmentTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
import com.pandora.RiskTO;
import com.pandora.RootTO;
import com.pandora.TaskTO;
import com.pandora.UserTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.TaskDelegate;
import com.pandora.gui.struts.form.MindMapForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class MindMapAction extends GeneralStrutsAction {

	private long nodeIdSeq = 1;
	
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
		String forward = "viewMindMap";
	    RepositoryDelegate rdel = new RepositoryDelegate();
	    PlanningDelegate del = new PlanningDelegate();
	    
		try {
			MindMapForm frm = (MindMapForm)form;	
			String uri = request.getRequestURI();
			String rootId = request.getParameter("entityid");
		    frm.setServerURI(SessionUtil.getUri(request) + uri + "?operation=loadMindMap&entityid=" + rootId + "&ts=" + DateUtil.getDateTime(DateUtil.getNow(), "yyyyMMddhhmmss"));
		    frm.setFormTitle(this.getBundleMessage(request, "title.mindMapForm"));
		    frm.setPlanningId(rootId);
		    
		    PlanningTO pto = del.getSpecializedObject(new PlanningTO(rootId));
		    frm.setProjectId(pto.getPlanningProject().getId());
		    
			Vector<RepositoryFilePlanningTO> list = rdel.getFilesFromPlanning(pto);
			request.getSession().setAttribute("mindMapArtifactList", list);	    
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	    
		return mapping.findForward(forward);
	}


	public ActionForward loadMindMap(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		StringBuffer content = new StringBuffer("");
		PlanningDelegate del = new PlanningDelegate();
		TaskDelegate tdel = new TaskDelegate();
		String userEncoding = SystemSingleton.getInstance().getDefaultEncoding();
		
	    try {
	    	String root = request.getParameter("entityid");	        

	    	UserTO uto = SessionUtil.getCurrentUser(request);
	    	userEncoding = uto.getBundle().getMessage(uto.getLocale(), "encoding");
	    	
	        if (root!=null && !root.trim().equals("")) {
	        	this.nodeIdSeq = 1;
	    		
		    	//content.append("<?xml version=\"1.0\" encoding=\"" + userEncoding + "\"?>\n");
		    	content.append("<map version=\"0.8\">\n");
	        	
		    	PlanningTO pto = del.getSpecializedObject(new PlanningTO(root));
		    	if (pto!=null) {
			    	content.append("<node CREATED=\"1227270466718\" ID=\"Freemind_Link_" + this.getNextNodeId() + 
			    				"\" MODIFIED=\"1227270466718\" TEXT=\"" + this.getTitle(request, pto) + "\">\n");
					
		    		content.append(this.getNodeTip(request, pto));
					
					if (pto instanceof TaskTO) {
						TaskTO parentTask = ((TaskTO)pto).getParentTask();
				    	if (parentTask!=null) {
				    		parentTask = tdel.getTaskObject(parentTask);
				    		content.append(this.getNodeTag(request, parentTask, true, PlanningRelationTO.RELATION_PART_OF));
				    		content.append("</node>");
				    	}
				    	
				    	if (((TaskTO) pto).isParentTask()) {
				    		if (((TaskTO) pto).getChildTasks()!=null && ((TaskTO) pto).getChildTasks().size()>0) {
					    		for (TaskTO child : ((TaskTO) pto).getChildTasks().values()) {
						    		content.append(this.getNodeTag(request, child, true, PlanningRelationTO.RELATION_COMPOSED_BY));
								}				    			
				    		}
				    	}
					}
					
					if (pto.getAttachments()!=null && pto.getAttachments().size()>0){
						content.append(this.getAttachments(request, pto, true));	
					}
			    	
			    	content.append("<font BOLD=\"true\" NAME=\"Verdana\" SIZE=\"16\"/>");
			    	content.append(this.getNodeConnection(request, pto, 0));
			    	content.append(this.getUserRelation(pto, true));			    	
			    	content.append("</node>\n");		    		
		    	}
		    	
		    	content.append("</map>");	
	        }
	    
		}catch(Exception e){
			content.append(e.getMessage());

		} finally{
			//put response content into Standard output
			ServletOutputStream sos;
			try {		    
				sos = response.getOutputStream();	
				response.setContentType("text/xml; charset=" + userEncoding);
				response.setContentLength(content.toString().length());
				sos.write(content.toString().getBytes());
				sos.close();

			} catch (IOException e2) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on Connector Listener process", e2);
			}
		}

		return null;
	}
	
	
	private Object getAttachments(HttpServletRequest request,PlanningTO pto, boolean isRight) {
		StringBuffer buff = new StringBuffer();
		Vector<AttachmentTO> list = pto.getAttachments();
		
		buff.append("<node CREATED=\"1227270466718\" ID=\"Freemind_Link_" + this.getNextNodeId() +
				"\" MODIFIED=\"1227270466718\" POSITION=\"" + (isRight?"right":"left") + 
				"\" STYLE=\"bubble\" TEXT=\"" +	StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "title.attachTagLib.List")) + "\">\n" +
						"<edge STYLE=\"linear\" WIDTH=\"1\" />\n");
		for (AttachmentTO ato : list) {
			buff.append("<node CREATED=\"1227270466718\" ID=\"Freemind_Link_" + this.getNextNodeId() +
					"\" MODIFIED=\"1227270466718\" POSITION=\"" + (isRight?"right":"left") +
					"\" LINK=\"javascript:downloadAttachment('" + ato.getId() + "');" +
					"\" STYLE=\"bubble\" TEXT=\"" +	StringUtil.formatWordToFreeMind(ato.getName()) + "\"/>\n");
		}
		buff.append("</node>\n");
		
		return buff;
	}


	private String getNodeConnection(HttpServletRequest request, PlanningTO pto, int level) throws Exception {
	    String response = "";
	    PlanningDelegate del = new PlanningDelegate();
	    
    	Vector<PlanningRelationTO> list = pto.getRelationList();
    	if (list!=null && level<=2 ) {
    		for (PlanningRelationTO prto : list) {
    	        if (prto.getPlanning().getId().equals(pto.getId())) {
    	            PlanningTO right = del.getSpecializedObject(prto.getRelated());
    	            response = response + this.getNodeTag(request, right, true, prto.getRelationType());
    	            response = response + "</node>";
    	        } else {
    	            PlanningTO left = del.getSpecializedObject(prto.getPlanning());
    	            response = response + this.getNodeTag(request, left, false, prto.getRelationType());
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
				"\" MODIFIED=\"1227270466718\" POSITION=\"" + (isRight?"right":"left") + "\" STYLE=\"linear\" " + 
						"TEXT=\"" +	relationLabel + "\"><edge STYLE=\"linear\" WIDTH=\"1\" /><font BOLD=\"false\" NAME=\"Verdana\" SIZE=\"8\"/>");		
		
		buff.append("<node CREATED=\"1227270466718\" ID=\"Freemind_Link_" + this.getNextNodeId() +
					"\" MODIFIED=\"1227270466718\" POSITION=\"" + (isRight?"right":"left") + 
					"\" STYLE=\"bubble\" LINK=\"../do/viewMindMap?operation=prepareForm&entityid=" + node.getId() + "\" " + 
					"TEXT=\"" +	this.getTitle(request, node) + "\"><edge STYLE=\"linear\" WIDTH=\"1\" />");
		
		buff.append(this.getNodeTip(request, node));
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
			if (tsk.getAllocResources()!=null && !tsk.isParentTask()) {
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
		
		content = content + "<node CREATED=\"1134591658270\" ID=\"Freemind_Link_" + this.getNextNodeId() + "\" MODIFIED=\"1227270466718\" TEXT=\"info\">";
		content = content + "<edge STYLE=\"linear\" WIDTH=\"1\" />";
		
		if (!(node instanceof RequirementTO)) {
			content = content + "<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.manageTask.name")) + "\" VALUE=\"" + StringUtil.formatWordToFreeMind(node.getName()) + "\" />";
		}
		
		if (node instanceof RequirementTO) {			
			RequirementTO req = (RequirementTO)node;
			if (req.getRequester()!=null) {
				content = content + "\n<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.requestPriority")) + "\" VALUE=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.requestPriority." + req.getPriority().intValue())) + "\" />";				
				if (req.getRequirementStatus()!=null){
					String reqst = req.getRequirementStatus().getName();
					content = content + "\n<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.requestStatus")) + "\" VALUE=\"" + reqst + "\" />";					
				}
			}
		} else if (node instanceof RiskTO) {
			RiskTO rsk = (RiskTO)node;
			if (rsk.getProbability()!=null) {
				content = content + "\n<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.formRisk.probability")) + 
						"\" VALUE=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.formRisk.probability." + rsk.getProbability())) + "\" />";	
			}
			if (rsk.getImpact()!=null) {
				content = content + "\n<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.formRisk.impact")) + 
						"\" VALUE=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.formRisk.impact." + rsk.getImpact())) + "\" />";	
			}
			if (rsk.getTendency()!=null) {
				content = content + "\n<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.formRisk.tendency")) + 
						"\" VALUE=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.formRisk.tendency." + rsk.getTendency())) + "\" />";	
			}			
		} else if (node instanceof TaskTO) {
			TaskTO tsk = (TaskTO)node;
			
			if (tsk.isParentTask()) {
				content = content + "\n<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "title.mindMapForm.TSK")) + 
						"\" VALUE=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.manageTask.taskParent")) + "\" />"; 
			}
			//if (tsk.getCategory()!=null) {
			//	CategoryDelegate cdel =  new CategoryDelegate();
			//	CategoryTO cto = cdel.getCategory(tsk.getCategory());
			//	content = content + "\n<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.manageTask.category")) + 
			//			"\" VALUE=\"" + StringUtil.formatWordToFreeMind(cto.getName()) + "\" />"; 
			//}			
			if (tsk.getAllocResources()!=null && tsk.getAllocResources().size()>0) {
				Iterator<ResourceTaskTO> i = tsk.getAllocResources().iterator();
				content = content + "\n<attribute NAME=\"" + StringUtil.formatWordToFreeMind(this.getBundleMessage(request, "label.manageTask.taskStatus")) + "\" VALUE=\""; 
				while(i.hasNext()) {
					ResourceTaskTO rtto = (ResourceTaskTO)i.next();
					if (tsk.getAllocResources().size()==1) {
						content = content +  StringUtil.formatWordToFreeMind(rtto.getTaskStatus().getName()) + "\" />";
					} else {
						content = content +  StringUtil.formatWordToFreeMind(rtto.getTaskStatus().getName() + " (" + rtto.getResource().getUsername() + ") ") + "\" />";	
					}
				}
			}

		}	
		content = content + "</node>";

		if (node.getDescription()!=null && !node.getDescription().equals("")) {
			String desc = node.getDescription();
			if (desc.length()>400) {
				desc = desc.substring(0, 399) + "...";
			}
			content = content + "\n<node BACKGROUND_COLOR=\"#ffffcc\" CREATED=\"1120439971523\" ID=\"Freemind_Link_" + this.getNextNodeId() + "\" MODIFIED=\"1176043944190\" STYLE=\"bubble\" " +
					"TEXT=\"" + StringUtil.formatWordToFreeMind(desc) + "\"/>";
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
