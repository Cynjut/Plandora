package com.pandora.gui.struts.form;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.NodeTemplateTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.StepNodeTemplateTO;
import com.pandora.bus.TaskNodeTemplateBUS;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;

public class ApplyTaskTemplateForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String projId; //the project the was selected when the template form was called
	
	private String projectId;
	
	private String templateId;
	
	private String instanceId;
	
	private String name;
	
	private String description;
	
	private String categoryId;
	
	private String resourceId;
	
	private String reqId;
	
	private NodeTemplateTO currentRoot;
	
	private String initDate;
	
	private String estimatedTime;
	
	private String questionContent;
	
	private String nodeType;
	
	private String htmlMap;
	
	private String categoryName;
	
    /** if current node is a parent task... */
    private boolean isParentTask;
	
	
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	this.isParentTask = false;
	}


	/**
     * Clear values of Tranfer Object
     */
    public void clear(){
	    this.setId(null);
	    this.setCurrentRoot(null);
	    this.setHtmlMap(null);
	    this.setName(null);
	    this.setDescription(null);
	    this.setCategoryId(null);
	    this.setProjectId(null);
	    this.setResourceId(null);
	    this.setEstimatedTime(null);
	    this.setInitDate(null);
	    this.setQuestionContent(null);
	    this.setCategoryName("");
	    this.isParentTask = false;
    } 
	
    
	////////////////////////////////////		
	public String getProjId() {
		return projId;
	}
	public void setProjId(String newValue) {
		this.projId = newValue;
	}


	////////////////////////////////////	
    public NodeTemplateTO getCurrentRoot() {
        return currentRoot;
    }
    public void setCurrentRoot(NodeTemplateTO newValue) {
        this.currentRoot = newValue;
    }
    
	////////////////////////////////////
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }

	////////////////////////////////////    
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
	////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
	////////////////////////////////////    
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
    
    
	////////////////////////////////////    
    public String getTemplateId() {
        return templateId;
    }
    public void setTemplateId(String newValue) {
        this.templateId = newValue;
    }
    

	////////////////////////////////////    
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String newValue) {
		this.instanceId = newValue;
	}


	////////////////////////////////////    
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String newValue) {
		this.resourceId = newValue;
	}
	
	
	////////////////////////////////////
	public String getInitDate() {
		return initDate;
	}
	public void setInitDate(String newValue) {
		this.initDate = newValue;
	}
	
	
	////////////////////////////////////
	public String getEstimatedTime() {
		return estimatedTime;
	}
	public void setEstimatedTime(String newValue) {
		this.estimatedTime = newValue;
	}
	
	
	////////////////////////////////////
	public String getReqId() {
		return reqId;
	}
	public void setReqId(String newValue) {
		this.reqId = newValue;
	}
	
	
	////////////////////////////////////	
	public String getQuestionContent() {
		return questionContent;
	}
	public void setQuestionContent(String newValue) {
		this.questionContent = newValue;
	}
	
	
	/////////////////////////////////////////
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String newValue) {
		this.nodeType = newValue;
	}
	
	
	/////////////////////////////////////////	
	public String getHtmlMap() {
		return htmlMap;
	}
	public void setHtmlMap(String newValue) {
		this.htmlMap = newValue;
	}
	
	
	/////////////////////////////////////////	
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String newValue) {
		this.categoryName = newValue;
	}


	/////////////////////////////////////////	
	public boolean getIsParentTask() {
		return isParentTask;
	}
	public void setIsParentTask(boolean newValue) {
		this.isParentTask = newValue;
	}


	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
	
		if (this.operation.equals("updateNode")) {
		    
		    if (this.name==null || this.name.trim().equals("")){
			    errors.add("Nome", new ActionError("validate.manageTask.blankName") );
		    }

		    if (this.templateId==null || this.templateId.trim().equals("") ||
		    		this.reqId==null || this.reqId.trim().equals("") ||
		    			this.id==null || this.id.trim().equals("")){
			    errors.add("Nome", new ActionError("validate.formApplyTaskTemplate.id") );
		    }		

		    NodeTemplateTO me = this.currentRoot.getNode(this.id, false);
		    if (nodeType.equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {

			    if (!this.isParentTask) {

			    	Vector resList = (Vector)request.getSession().getAttribute("resourceAllocated");
				    if (resList==null || resList.size()==0){
				       errors.add("ResourceList", new ActionError("validate.formApplyTaskTemplate.resource") );
				       
				    } else {

				    	//validate if the 'start date' of resources are earlier than the previous node of tree
					    NodeTemplateTO parent = this.currentRoot.getNode(this.id, true);			    	
					    if (me!=null && parent!=null) {
					    	String parentType = parent.getNodeType();
					    	Vector dateList = null;
						    if (parentType.equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
						    	dateList = getStartDate(parent);
						    	
						    } else if (parentType.equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
						    	NodeTemplateTO granpa = parent.getNode(parent.getId(), true);
							    if (granpa.equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
							    	dateList = getStartDate(granpa);	
							    }
						    }
						    if (dateList!=null && dateList.size()>0) {
						    	for (int i=0; i<dateList.size(); i++) {
									String date = (String)dateList.elementAt(i);
									Timestamp refDate = TaskNodeTemplateBUS.getDateOfResourceList(date);								
								    Iterator j = resList.iterator();
								    while(j.hasNext()){
								        ResourceTaskTO buff = (ResourceTaskTO)j.next();
								        if (buff.getStartDate().before(refDate)) {
								        	errors.add("ResourceList", new ActionError("validate.formApplyTaskTemplate.invalidDate", buff.getResource().getName()) );
								            break;
								        }
								    }
						    	}			    		
						    }
					    }

					    if (me.getNextNode()!=null && 
					    		Integer.parseInt(me.getNextNode().getId()) > Integer.parseInt(me.getId())) {
					    
						    String nextNodeType = me.getNextNode().getNodeType();
						    
						    //if the next node is a decision, it cannot contain more than one resource into it
						    if (nextNodeType.equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
						    	if (resList.size()>1) {
						    		 errors.add("ResourceList", new ActionError("validate.formApplyTaskTemplate.resourceDecision") );	 
						    	}
						    } else if (nextNodeType.equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
						    	
						    	//validate if the 'start date' of current node are latest than the next node of tree
						    	NodeTemplateTO next = me.getNextNode();
						    	Vector dateList = getStartDate(next);
							    if (dateList!=null && dateList.size()>0) {
							    	for (int i=0; i<dateList.size(); i++) {
										String date = (String)dateList.elementAt(i);
										Timestamp refDate = TaskNodeTemplateBUS.getPreDefinedDate(date);
										if (refDate==null) {
											refDate = TaskNodeTemplateBUS.getDateOfResourceList(date);
										}
										
									    Iterator j = resList.iterator();
									    while(j.hasNext()){
									        ResourceTaskTO buff = (ResourceTaskTO)j.next();
									        if (refDate.before(buff.getStartDate())) {
									        	errors.add("ResourceList", new ActionError("validate.formApplyTaskTemplate.invalidDate2", buff.getResource().getName()) );
									            break;
									        }
									    }
							    	}			    		
							    }
						    }				    	
					    }				    
				    }

				    //check if the allocated resources are related to the project of template node selected  
				    UserDelegate udel = new UserDelegate();
				    Iterator j = resList.iterator();
				    while(j.hasNext()){
				        ResourceTaskTO buff = (ResourceTaskTO)j.next();
				        ResourceTO projectResource = buff.getResource();
				        projectResource.setProject(new ProjectTO(this.projectId));
					    try {
					    	projectResource = udel.getResource(projectResource);
						} catch (BusinessException e) {
							projectResource = null;
						}
				        if (projectResource==null) {
				        	errors.add("ResourceList", new ActionError("validate.formApplyTaskTemplate.resourceProj", buff.getResource().getName()) );			        	
				            break;
				        }
				    }
				    
			    } else {
			    	//check if the current node is before the decision and states a "parent task"
			    	if (me!=null) {
			    		NodeTemplateTO parent = me.getNextNode();
			    		if (parent.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
			    			errors.add("Nome", new ActionError("validate.formApplyTaskTemplate.isparent") );	
			    		}
			    	}
			    }
			    
		    } else if (nodeType.equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
			    if (this.questionContent==null || this.questionContent.trim().equals("")){
			    	errors.add("questionContent", new ActionError("validate.formApplyTaskTemplate.resource") );
			    }
		    }
		    	
		    
		    
		    
		}
		
		return errors;
	}
    
	
	private Vector getStartDate(NodeTemplateTO node){
		Vector response = new Vector();
		StepNodeTemplateTO step = (StepNodeTemplateTO)node;
		if (step.getResourceId()!=null) {
			String[] resources = step.getResourceId().split(";");
			for (int i=0; i<resources.length; i++) {
				String token = resources[i].trim();
				String[] fields = token.split("\\|");
				if (fields!=null && fields.length==3) {
					response.add(fields[1]);	
				}
			}			
		}
		return response;
	}
}
