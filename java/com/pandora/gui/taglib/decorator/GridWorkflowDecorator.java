package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.dao.TaskNodeTemplateDAO;
import com.pandora.delegate.TaskDelegate;
import com.pandora.helper.HtmlUtil;

public class GridWorkflowDecorator extends ColumnDecorator {
	
	TaskNodeTemplateDAO ntdao = new TaskNodeTemplateDAO();
	
	public String decorate(Object columnValue) {
        String image = "";
	    String altValue = this.getBundleMessage("label.showAllTaskForm.showworkflow");
	    TransferObject obj = (TransferObject)this.getObject();
	    image = this.getWorkFlowIcon(altValue, obj);
		return image;
	}

	
	public String getWorkFlowIcon(String altValue, Object obj) {
		String response = "";  
		
		if (obj instanceof ResourceTaskTO) {
			ResourceTaskTO rtto = (ResourceTaskTO) obj;
			if (rtto.getTask()!=null) {
				
			    TaskTO tto = rtto.getTask();
			    String reqId = "-1";
			    if (tto.getRequirement()!=null && tto.getRequirement().getId()!=null) {
			    	reqId = tto.getRequirement().getId();
			    }
			    
			    String entityId = null; 
			    if (tto.getTemplateInstanceId()!=null) {
			    	entityId = tto.getTemplateInstanceId().toString();
			    } else {
			    	if (tto.getParentTask()!=null) {
			    		try {
				    		TaskDelegate tdel = new TaskDelegate();
				    		TaskTO parent = tdel.getTaskObject(tto.getParentTask());
				    		if (parent.getTemplateInstanceId()!=null) {
				    			entityId = parent.getTemplateInstanceId().toString();	
				    		}
			    		} catch(Exception e) {
			    			entityId = null;
			    		}
			    	}
			    }
			    
			    if (entityId != null) {
			    	response ="<a href=\"javascript:showWorkFlow('" + entityId + "', '" + reqId + "');\" border=\"0\"> \n";
			    	response += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/view-workflow.gif\" >";
			    	response += "</a>";			    	
			    }
			    
			} else {
				response = "err";
			}
		    
		} else if (obj instanceof RequirementTO) {
			RequirementTO rto = (RequirementTO) obj;
			TaskTO tto = null;
			try {
				if (rto.getResourceTaskList()!=null) {
					for (ResourceTaskTO rtto : rto.getResourceTaskList()) {
						tto = rtto.getTask();
						if (tto!=null && tto.getTemplateInstanceId()!=null) {
							break;
						}
					}					
				}
			} catch(Exception e) {
				tto = null;
			}
		    if (tto!=null && tto.getTemplateInstanceId()!=null) {
		    	response ="<a href=\"javascript:showWorkFlow('" + tto.getTemplateInstanceId().toString() + "', '" + rto.getId() + "');\" border=\"0\"> \n";		    			    	
		    	response += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/view-workflow.gif\" >";
		    	response += "</a>";		    	    	
		    }			
		}
		return response;
	}	

	
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);    	
	}
		
	
	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}

}
