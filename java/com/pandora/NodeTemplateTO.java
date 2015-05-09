package com.pandora;

import java.util.HashMap;

public class NodeTemplateTO extends TransferObject {

	public static final String NODE_TEMPLATE_STEP     = "1";
	
	public static final String NODE_TEMPLATE_DECISION = "2";
	
	private static final long serialVersionUID = 1L;

	private String name;
	
	private String additionalInfo;
	
	private String description;
	
	private String nodeType;
	
	private String planningId;
		
	private NodeTemplateTO nextNode;

	private String coords;
	
	private ProjectTO project;
	
	/** This attribute is used only to Workflow Saving feature **/
	private String relationPlanningId;
	
	/** This attribute is used only to Workflow Saving feature **/
	private Integer instanceId;
		
	private boolean isFromCached = false;
	
	private boolean isParentTask;
	
	
	/**
	 * Constructor
	 */
	public NodeTemplateTO(String newId) {
		this.setId(newId);
		isFromCached = false;
	}

	
	/**
	 * Constructor
	 */
	public NodeTemplateTO() {
		isFromCached = false;
	}
	
	
	/////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	/////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}

	
	/////////////////////////////////
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String newValue) {
		this.nodeType = newValue;
	}

	
	/////////////////////////////////
	public String getPlanningId() {
		return planningId;
	}
	public void setPlanningId(String newValue) {
		this.planningId = newValue;
	}

		
	/////////////////////////////////
	public NodeTemplateTO getNextNode() {
		return nextNode;
	}
	public void setNextNode(NodeTemplateTO newValue) {
		this.nextNode = newValue;
	}


	/////////////////////////////////
	public String getCoords() {
		return coords;
	}
	public void setCoords(String newValue) {
		this.coords = newValue;
	}

	
	/////////////////////////////////	
	public ProjectTO getProject() {
		return project;
	}
	public void setProject(ProjectTO newValue) {
		this.project = newValue;
	}

	
	/////////////////////////////////		
	public String getRelationPlanningId() {
		return relationPlanningId;
	}
	public void setRelationPlanningId(String newValue) {
		this.relationPlanningId = newValue;
	}

	
	/////////////////////////////////			
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String newValue) {
		this.additionalInfo = newValue;
	}

	
	/////////////////////////////////				
	public Integer getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Integer newValue) {
		this.instanceId = newValue;
	}
	
	
	/////////////////////////////////////////	
	public boolean getIsParentTask() {
		return isParentTask;
	}
	public void setIsParentTask(boolean newValue) {
		this.isParentTask = newValue;
	}
	
	
	public boolean isFromCached(){
		return isFromCached;
	}
	
	public String getHtmlMapCoords() {
		return this.getHtmlMapCoords(true);
	}

	public String getHtmlMapCoords(boolean showHRef) {
		HashMap cache = new HashMap();
		return this.getHtmlMapCoords(cache, showHRef);
	}
	
	public NodeTemplateTO getNode(String nodeId, boolean getTheParent) {
		HashMap cache = new HashMap();
		return this.getNode(cache, nodeId, null, getTheParent);
	}
	
	
	private String getHtmlMapCoords(HashMap cache, boolean showHRef) {
		String response = "";
		
		if (cache.get(this.getId())==null) {
			cache.put(this.getId(), this.getId());  //the cache is used to know witch nodes were already loaded considering circle references
			
			if (this.coords!=null && !this.coords.trim().equals("")) {
				response = response + "<area shape=\"rect\" coords=\"" + this.coords + "\" ";
				if (showHRef) {
					response = response + "href=\"javascript:clickNodeTemplate('" + this.getId() + "','" + this.getRelationPlanningId() + "');\" ";	
				} else {
					response = response + "href=\"#\" "; 
				}
				if (this.additionalInfo!=null) {
					response = response + "title=\"" + this.additionalInfo + "\" alt=\"" + this.additionalInfo + "\" ";	
				}
				response = response + "/>\n";
			}
			
			if (this.nextNode!=null) {
				response = response + this.nextNode.getHtmlMapCoords(cache, showHRef);
			}
			
			if (this instanceof DecisionNodeTemplateTO) {
				DecisionNodeTemplateTO decision = ((DecisionNodeTemplateTO) this);
				if (decision.getNextNodeIfFalse()!=null) {
					response = response + decision.getNextNodeIfFalse().getHtmlMapCoords(cache, showHRef);	
				}
			}			
		}
		
		return response;
	}
	
	private NodeTemplateTO getNode(HashMap cache, String nodeId, NodeTemplateTO parent, boolean getTheParent) {
		NodeTemplateTO response = null;
		
		if (cache.get(this.getId())==null) {
			cache.put(this.getId(), this.getId());  //the cache is used to know witch nodes were already loaded considering circle references
			
			if (this.getId().equals(nodeId)) {
				if (getTheParent) {
					response = parent;
				} else {
					response = this;	
				}
				
			} else {
				
				if (this.nextNode!=null) {
					response = this.nextNode.getNode(cache, nodeId, this, getTheParent);
				}
				
				if (response==null && this instanceof DecisionNodeTemplateTO) {
					DecisionNodeTemplateTO decision = ((DecisionNodeTemplateTO) this);
					if (decision.getNextNodeIfFalse()!=null) {
						response = decision.getNextNodeIfFalse().getNode(cache, nodeId, this, getTheParent);
					}
				}			
			}
		} else {
			isFromCached = true;
			System.out.println(this.getId() + ": cached");
		}
		
		return response;
	}

	
	public void copyFromClone(NodeTemplateTO clone){
		if (clone!=null) {
			this.setAdditionalInfo(clone.getAdditionalInfo());
			this.setCoords(clone.getCoords());
			this.setDescription(clone.getDescription());
			this.setGenericTag(clone.getGenericTag());
			this.setName(clone.getName());
			this.setNextNode(clone.getNextNode());
			this.setNodeType(clone.getNodeType());
			this.setPlanningId(clone.getPlanningId());
			this.setProject(clone.getProject());
			this.setRelationPlanningId(clone.getRelationPlanningId());
			this.setInstanceId(clone.getInstanceId());
			this.setIsParentTask(clone.getIsParentTask());			
		}
	}

}
