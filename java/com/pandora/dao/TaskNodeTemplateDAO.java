package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.CustomNodeTemplateTO;
import com.pandora.DecisionNodeTemplateTO;
import com.pandora.NodeTemplateTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.StepNodeTemplateTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.TaskNodeTemplateBUS;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.SaveWorkflowException;
import com.pandora.exception.ZeroCapacityException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

public class TaskNodeTemplateDAO extends DataAccess {

	
	public NodeTemplateTO getObjectInTree(NodeTemplateTO node) throws DataAccessException {
    	NodeTemplateTO response = null;
    	HashMap cache = new HashMap();
        Connection c = null;
		try {
			c = getConnection();
			response = this.getObjectInTree(node, c, cache);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
		return response;
	}

	
	public CustomNodeTemplateTO getCustomNodeTemplate(NodeTemplateTO filter, String planningId) throws DataAccessException {
		CustomNodeTemplateTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getCustomNodeTemplate(filter, planningId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
		return response;
	}

	
    public void saveWorkflowByRequirement(NodeTemplateTO firstNode, String requirementId, UserTO createdBy) throws Exception {
        Connection c = null;
		try {
			c = getConnection(false);
			
			//create tasks in batch according to nodes template
			this.createNewTaskFromWorkflow(firstNode, requirementId, createdBy, "", c);
			
			c.commit();

		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			
			if (e instanceof ZeroCapacityException) {
				throw new ZeroCapacityException(e);
			} else {
				throw new SaveWorkflowException(e);	
			}
			
		} finally{
			this.closeConnection(c);
		}								
	}
    
    /**
     * Check if the next node is a task that must be re-opened.<br />
     * This method returns true if the task was correctly reopened. Otherwise, false.
     */
    public void reopenTask(String taskIdToBeReopened, UserTO handler, String comment, Connection c) throws DataAccessException{
        TaskDAO tdao = new TaskDAO();
        TaskTO filter = new TaskTO(taskIdToBeReopened);
		TaskTO nextNodeTask = (TaskTO)tdao.getObject(filter, c);
		if (nextNodeTask!=null && nextNodeTask.getFinalDate()!=null) {
			nextNodeTask.setHandler(handler);
			tdao.reopen(nextNodeTask, null, c);
		}
    }
    
    
    public TaskTO createNewTaskFromWorkflow(NodeTemplateTO node, String requirementId, UserTO createdBy, String comment, Connection c) throws Exception {
    	TaskTemplateDelegate tdel = new TaskTemplateDelegate();
    	TaskNodeTemplateBUS tnBus = new TaskNodeTemplateBUS();
    	TaskDAO tskdao = new TaskDAO();
    	TaskTO tto = null;
    	TaskTO nextTask = null;
    	
    	//FIXME quando se cancela todas as tarefas, o erro abaixo eh lancado e nao deveria. A checagem tera que considerar tambem o status da tarefa
    	if (node.getRelationPlanningId()!=null) {
    		throw new SaveWorkflowException("error.formApplyTaskTemplate.alreadyExists");    		
    	}
    	
    	if (node instanceof StepNodeTemplateTO) {
    		
    		//override the values of node from custom node...
    		CustomNodeTemplateTO customNode = this.getCustomNodeTemplate(node, requirementId, c);
    		tnBus.overrideNodeTemplate(node, customNode);

	    	//check if it is necessary to reopen the next task...
	    	if (customNode.getRelatedTaskId()!=null) {
	    		this.reopenTask(customNode.getRelatedTaskId(), createdBy, comment, c);
	    		
	    	} else {

		    	//create the next node of the tree...
	    		NodeTemplateTO next = node.getNextNode();
		    	if (next!=null) {
		    		
		    		//check if the nextNode must be created or must be re-opened...
		    		CustomNodeTemplateTO customNextNode = this.getCustomNodeTemplate(next, requirementId, c);
		    		if (customNextNode!=null) {
			    		if (customNextNode.getRelatedTaskId()==null) {
			    			nextTask = this.createNewTaskFromWorkflow(next, requirementId, createdBy, comment, c);	
			    		} else {
			    			nextTask = (TaskTO) tskdao.getObject(new TaskTO(customNextNode.getRelatedTaskId()), c);
			    		}		    			
		    		}
		    	}	    		
	    		//generate a new task following the workflow...
	        	tto = new TaskTO();
				StepNodeTemplateTO stepNode = (StepNodeTemplateTO) node;
				
		    	tto.setCategory(new CategoryTO(stepNode.getCategoryId()));
		    	tto.setCreatedBy(createdBy);
		    	tto.setCreationDate(DateUtil.getNow());
		    	tto.setDescription(stepNode.getDescription());
		    	tto.setHandler(createdBy);
		    	tto.setName(stepNode.getName());
		    	tto.setProject(node.getProject());
		    	tto.setRelationList(null);
		    	tto.setRequirement(null);
		    	if (requirementId!=null && !requirementId.equals("-1")) {
		    		tto.setRequirement(new RequirementTO(requirementId));	
		    	}
			   	
		    	tto.setIsParentTask(new Integer(stepNode.getIsParentTask()?1:0));
		    	tto.setIteration(null);
		    	tto.setDiscussionTopics(null);
		    	tto.setFinalDate(null);
		    	tto.setParentTask(null);
		    	
				Vector allocList;
				try {
					allocList = tdel.getResourceListFromString(stepNode.getResourceId(), createdBy);
				} catch (Exception e) {
					throw new SaveWorkflowException("message.formApplyTaskTemplate.saveworkflowErr", e);
				}
				if ((allocList!=null && allocList.size()>0) || stepNode.getIsParentTask()) {
			    	tto.setAllocResources(allocList);
			    	try {
			    		
			    		//create a new task
			    		tskdao.insert(tto, c);
			    				    		
						//save task id into customer node template object
			    		customNode.setRelatedTaskId(tto.getId());
						this.saveCustomNodeTemplate(customNode, c);
			    		
					} catch (DataAccessException e) {
						throw new SaveWorkflowException("message.formApplyTaskTemplate.saveworkflowErr", e);
					}				
				} else {
					throw new SaveWorkflowException("error.formApplyTaskTemplate.resource");	
				}
		
				//create the relationship between the task that were created.
				this.createWorkflowLink(c, tto, nextTask);	    			    		
	    	}
		}
        	    	
    	return tto;
	}


	public void createWorkflowLink(Connection c, TaskTO tto, TaskTO nextTask) throws SaveWorkflowException {
    	PlanningRelationDAO pdao = new PlanningRelationDAO();

		if (nextTask!=null && tto!=null) {
			PlanningRelationTO prto = new PlanningRelationTO();
			prto.setPlanning(tto);
			prto.setRelated(nextTask);
			prto.setPlanType(PlanningRelationTO.ENTITY_TASK);	    	
			prto.setRelatedType(PlanningRelationTO.ENTITY_TASK);
			prto.setRelationType(PlanningRelationTO.RELATION_BLOCKS);
			try {
				pdao.insertRelation(prto, c);
			} catch (DataAccessException e) {
				throw new SaveWorkflowException("message.formApplyTaskTemplate.saveworkflowErr", e);
			}	    		
		}
	}

    
	public CustomNodeTemplateTO getCustomNodeTemplate(NodeTemplateTO filter, String planningId, Connection c) throws DataAccessException {
		CustomNodeTemplateTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			
			String whereInstance = "";
			if (filter.getInstanceId()!=null) {
				whereInstance = " and instance_id=?";
			}
			
			if (planningId==null) {
				planningId = "-1";
			}
			
		    pstmt = c.prepareStatement("select node_template_id, planning_id, template_id, project_id, instance_id, " +
		    					       "name, description, category_id, resource_list, question_content, related_task_id," +
		    					       "is_parent_task, decision_answer from custom_node_template " +
		    					       "where node_template_id=? and planning_id=? and template_id=? " + whereInstance);		
			pstmt.setString(1, filter.getId());
			pstmt.setString(2, planningId);	
			pstmt.setString(3, filter.getPlanningId());
			if (filter.getInstanceId()!=null) {
				pstmt.setInt(4, filter.getInstanceId().intValue());
			}
			
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateCustomNodeTemplateByResultSet(rs);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}
		return response;
	}


	public Vector getNodeListByPlanning(String templateId) throws DataAccessException {
    	Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getNodeListByTemplate(templateId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
		return response;
	}
    
	
	public Integer getInstance(String templateId, String planningId) throws DataAccessException {
		Integer response = null;
        Connection c = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			c = getConnection();		
			
		    pstmt = c.prepareStatement("select max(instance_id) as instance_id " +
		    						   "from custom_node_template where template_id=? and planning_id=? " +
		    						   "and instance_id not in " +
		    						   		"(select distinct instance_id from " +
		    						   		            "(select instance_id, related_task_id IS NULL as has_link " +
		    						   			          "from custom_node_template) as sub " +
		    						   	     "where sub.has_link = false)");
			pstmt.setString(1, templateId);
			pstmt.setString(2, planningId);	
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = getInteger(rs, "instance_id");
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		
		return response;
	}
	
	
    private Vector getNodeListByTemplate(String templateId, Connection c) throws DataAccessException {
		Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select id, name, description, node_type, project_id, " +
		    						"planning_id, next_node_id from node_template where planning_id = ? order by id");		
			pstmt.setString(1, templateId);	
			rs = pstmt.executeQuery();
			while (rs.next()){
				NodeTemplateTO ntto = this.populateBeanByResultSet(rs);
			    response.addElement(ntto);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);			
		}	 
		return response;
	}

    
	public void saveCustomNodeTemplate(CustomNodeTemplateTO cntto) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection(false);
			this.saveCustomNodeTemplate(cntto, c);
			c.commit();

		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			throw new DataAccessException(e);			
		} finally{
			this.closeConnection(c);
		}								
	}

	/**
	 * Return a decision node template object that contain data of a decision node 
	 * and is related a specific Task. This method is used to define if a task 
	 * is linked with a decision point of a workflow.
	 */
	public DecisionNodeTemplateTO getDecisionNodeByTask(TaskTO task, Connection c) throws DataAccessException {
		DecisionNodeTemplateTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {			
		    pstmt = c.prepareStatement("select sub.id, sub.template_id, sub.next_node_id, sub.next_node_id_if_false, " + 
		    						          "sub.related_task_id, sub.question_content as QC_DEFAULT, " +
		    						          "dc.question_content as QC_CUSTOM, dc.decision_answer, sub.instance_id, " +
		    						          "sub.name as NM_DEFAULT, sub.description as DS_DEFAULT, dc.name as NM_CUSTOM, dc.description as DS_CUSTOM " +
		    						   "from ( " +
		    						          "select c.node_template_id, c.template_id, dn.next_node_id, d.next_node_id_if_false, " + 
		    						                " c.related_task_id, d.question_content, d.id, c.instance_id, dn.name, dn.description " +
		    						          "from custom_node_template c, node_template n, decision_node_template d, node_template dn " +
		    						          "where c.related_task_id=? and c.node_template_id = n.id " +
		    						            "and n.next_node_id = d.id and dn.id = d.id " +
		    						    ") as sub LEFT OUTER JOIN custom_node_template dc on (dc.node_template_id = sub.id " + 
		    						    	" and dc.template_id = sub.template_id and sub.instance_id = dc.instance_id)"); 
			pstmt.setString(1, task.getId());				
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = new DecisionNodeTemplateTO(getString(rs, "id"));
				response.setPlanningId(getString(rs, "template_id"));
				response.setInstanceId(getInteger(rs, "instance_id"));
				
				String qcdefault = getString(rs, "QC_DEFAULT");
				String qccustom = getString(rs, "QC_CUSTOM");
				if (qccustom!=null) {
					response.setQuestionContent(qccustom);
					response.setName(getString(rs, "NM_CUSTOM"));
					response.setDescription(getString(rs, "DS_CUSTOM"));
				} else {
					response.setQuestionContent(qcdefault);
					response.setName(getString(rs, "NM_DEFAULT"));
					response.setDescription(getString(rs, "DS_DEFAULT"));					
				}
				
				String nextNodeTrue = getString(rs, "next_node_id");
				if (nextNodeTrue!=null) {
					response.setNextNode(new NodeTemplateTO(nextNodeTrue));	
				}
				String nextNodeFalse = getString(rs, "next_node_id_if_false");
				if (nextNodeFalse!=null) {
					response.setNextNodeIfFalse(new NodeTemplateTO(nextNodeFalse));	
				}
				response.setDecisionAnswer(getString(rs, "decision_answer"));
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}
		return response;
	}


	public NodeTemplateTO getNodeByTask(TaskTO task, Connection c) throws DataAccessException {
		NodeTemplateTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {			
		    pstmt = c.prepareStatement("select n.id, n.name, n.description, n.node_type, n.project_id, " +
		    								"n.planning_id, n.next_node_id, c.instance_id " +
		    							"from custom_node_template c, node_template n " +
		    							"where related_task_id = ? " +
		    							  "and c.node_template_id = n.id");
			pstmt.setString(1, task.getId());				
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateBeanByResultSet(rs);
				
				Integer instanceId = getInteger(rs, "instance_id");
				response.setInstanceId(instanceId);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}
		return response;
	}
	
	
	public void saveCustomNodeTemplate(CustomNodeTemplateTO cntto, Connection c) throws DataAccessException {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		boolean update = false;

		try {
			//check if row already exists...
			if (cntto.getInstanceId()!=null) {				
			    pstmt = c.prepareStatement("select node_template_id from custom_node_template " +
				   						   "where node_template_id=? and instance_id=? and template_id=?");		
			    pstmt.setString(1, cntto.getNodeTemplateId());
			    pstmt.setInt(2, cntto.getInstanceId().intValue());	
				pstmt.setString(3, cntto.getTemplateId());
				rs = pstmt.executeQuery();
				if (rs.next()){
					update = true;
				} 
		    }			
			
			//if it exists, run an update...
			if (update) {
			    pstmt = c.prepareStatement("update custom_node_template " +
						   "set name=?, description=?, category_id=?, resource_list=?, question_content=?, " +
						   "related_task_id=?, project_id=?, planning_id=?, is_parent_task=?, decision_answer=? " +
						   "where node_template_id=? and template_id=? and instance_id=?");
				pstmt.setString(1, cntto.getName());
				pstmt.setString(2, cntto.getDescription());
				pstmt.setString(3, cntto.getCategoryId());
				pstmt.setString(4, cntto.getResource());
				pstmt.setString(5, cntto.getQuestionContent());
				pstmt.setString(6, cntto.getRelatedTaskId());
				pstmt.setString(7, cntto.getProjectId());
				pstmt.setString(8, cntto.getPlanningId());
				if (cntto.getIsParentTask()!=null) {
					pstmt.setInt(9, (cntto.getIsParentTask().booleanValue()?1:0));	
				} else {
					pstmt.setNull(9, Types.INTEGER);
				}
				pstmt.setString(10, cntto.getDecisionAnswer());
				pstmt.setString(11, cntto.getNodeTemplateId());
				pstmt.setString(12, cntto.getTemplateId());				
				pstmt.setInt(13, cntto.getInstanceId().intValue());
				pstmt.executeUpdate();
				
			} else {
				//...otherwise, run an insert...
			    pstmt = c.prepareStatement("insert into custom_node_template (" +
			    					"node_template_id, instance_id, planning_id, template_id, project_id, " +
			    					"name, description, category_id, resource_list, question_content, " +
			    					"is_parent_task, decision_answer) " +
			    					"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			    Integer instanceId = cntto.getInstanceId();
			    if (instanceId==null) {
			    	instanceId = new Integer(super.getNewId());	
			    }
	    
				pstmt.setString(1, cntto.getNodeTemplateId());
				pstmt.setInt(2, instanceId.intValue());				
				pstmt.setString(3, cntto.getPlanningId());
				pstmt.setString(4, cntto.getTemplateId());
			    pstmt.setString(5, cntto.getProjectId());
			    pstmt.setString(6, cntto.getName());
			    pstmt.setString(7, cntto.getDescription());
				pstmt.setString(8, cntto.getCategoryId());
				pstmt.setString(9, cntto.getResource());
				pstmt.setString(10, cntto.getQuestionContent());
				if (cntto.getIsParentTask()!=null) {
					pstmt.setInt(11, (cntto.getIsParentTask().booleanValue()?1:0));	
				} else {
					pstmt.setNull(11, Types.INTEGER);
				}
				pstmt.setString(12, cntto.getDecisionAnswer());				
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}			
	}

	
	public void saveDecisionAnswer(String answer, DecisionNodeTemplateTO decision, String reqId, Connection c) throws DataAccessException {
		if (reqId==null) {
			reqId = "-1";
		}
		CustomNodeTemplateTO cto = new CustomNodeTemplateTO(decision, reqId);
		cto.setDecisionAnswer(answer);
		cto.setInstanceId(decision.getInstanceId());
		this.saveCustomNodeTemplate(cto, c);
	}

	
	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
    	NodeTemplateTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {

			NodeTemplateTO filter = (NodeTemplateTO)to;
		    pstmt = c.prepareStatement("select id, name, description, node_type, project_id, " +
		    						   "planning_id, next_node_id from node_template where id = ?");		
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateBeanByResultSet(rs);

				//get specific fields...
            	response = getSpecificFields(response, c);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

	
	public void updatePlanningOfIntance(Integer instanceId, String planningId, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			if (instanceId!=null) {
			    pstmt = c.prepareStatement("update custom_node_template set planning_id=? where instance_id=?");
			    if (planningId!=null) {
			    	pstmt.setString(1, planningId);	
			    } else {
			    	pstmt.setString(1, "-1");
			    }
			    pstmt.setInt(2, instanceId.intValue());	
			    pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}			
	}

	
    private NodeTemplateTO getObjectInTree(NodeTemplateTO to, Connection c, HashMap cache) throws DataAccessException {
    	NodeTemplateTO response= null;
    	
    	//check into cache if the node is already read from data base
   		response = (NodeTemplateTO)cache.get(to.getId());

    	if (response==null) {
    		
        	response = (NodeTemplateTO) this.getObject(to, c);
        	if (response!=null) {
        		response.setInstanceId(to.getInstanceId());
            	cache.put(response.getId(), response);
            	
    			//get specific fields...
            	response = getSpecificFields(response, c);
            	
            	//if the node is 'decision node' fetch the specific next node
    			if (response.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
    				
    				if (to.getInstanceId()!=null) {
        				CustomNodeTemplateTO customDecision = this.getCustomNodeTemplate(response, to.getPlanningId(), c);
        				if (customDecision!=null) {
        					((DecisionNodeTemplateTO)response).setDecisionAnswer(customDecision.getDecisionAnswer());
        					((DecisionNodeTemplateTO)response).setQuestionContent(customDecision.getQuestionContent());    					
        				}    					
    				}
					
    				NodeTemplateTO other = ((DecisionNodeTemplateTO)response).getNextNodeIfFalse();
    				if (other!=null) {
    					other.setInstanceId(to.getInstanceId());
    					other = (NodeTemplateTO) this.getObjectInTree(other, c, cache);
    					((DecisionNodeTemplateTO)response).setNextNodeIfFalse((NodeTemplateTO)cache.get(other.getId()));

    					
    				}
    			}
    			
    			//get the next node, if necessary...
    			NodeTemplateTO nextnode = response.getNextNode();
    	        if (nextnode!=null) {
    	        	nextnode.setInstanceId(to.getInstanceId());
    	        	NodeTemplateTO nextNode = (NodeTemplateTO) this.getObjectInTree(nextnode, c, cache);
    	        	response.setNextNode(nextNode);
    	        }        		
        	}			
    	}
    	
    	return response;
    }


    private NodeTemplateTO getSpecificFields(NodeTemplateTO to, Connection c) throws DataAccessException {
		ResultSet rs = null;
		PreparedStatement pstmt = null; 

		try {

	    	if (to.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
			    pstmt = c.prepareStatement("select category_id, resource_list, category_regex " +
				   						   "from step_node_template where id = ?");		
			    pstmt.setString(1, to.getId());
			    rs = pstmt.executeQuery();
			    if (rs.next()){
			    	((StepNodeTemplateTO)to).setCategoryId(getString(rs, "category_id"));
			    	((StepNodeTemplateTO)to).setResourceId(getString(rs, "resource_list"));
			    	((StepNodeTemplateTO)to).setCategoryRegex(getString(rs, "category_regex"));
			    }
	    		
			} else if (to.getNodeType().equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {			
			    pstmt = c.prepareStatement("select question_content, next_node_id_if_false " +
				   						   "from decision_node_template where id = ?");		
				pstmt.setString(1, to.getId());
				rs = pstmt.executeQuery();
				if (rs.next()){
					((DecisionNodeTemplateTO)to).setQuestionContent(getString(rs, "question_content"));
					String otherNodeId = getString(rs, "next_node_id_if_false");
					if (otherNodeId!=null && !otherNodeId.trim().equals("")) {
						NodeTemplateTO other = new NodeTemplateTO(otherNodeId);
						((DecisionNodeTemplateTO)to).setNextNodeIfFalse(other);
					}
				}	
			}
		} catch (SQLException e){
			throw new DataAccessException(e);		
		} finally {
			closeStatement(rs, pstmt);
		}
		
    	return to;
    }
    
    
    private NodeTemplateTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
    	NodeTemplateTO response = null;
    	
        String id = getString(rs, "id");
        String type = getString(rs, "node_type");
        
        if (type.equals(NodeTemplateTO.NODE_TEMPLATE_STEP)) {
        	response = new StepNodeTemplateTO(id);	
        } else if (type.equals(NodeTemplateTO.NODE_TEMPLATE_DECISION)) {
        	response = new DecisionNodeTemplateTO(id);
        } else {
        	throw new DataAccessException("The type of Template Node is not valid");
        }
        
        response.setName(getString(rs, "name"));
        response.setDescription(getString(rs, "description"));
        response.setNodeType(type);
        response.setPlanningId(getString(rs, "planning_id"));
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        
        String nextNode = getString(rs, "next_node_id");
        if (nextNode!=null) {
        	response.setNextNode(new NodeTemplateTO(nextNode));	
        }
        
        return response;
    }
    
    
    private CustomNodeTemplateTO populateCustomNodeTemplateByResultSet(ResultSet rs) throws DataAccessException{
    	CustomNodeTemplateTO response = new CustomNodeTemplateTO();
		response.setCategoryId(getString(rs, "category_id"));
		response.setDescription(getString(rs, "description"));
		response.setName(getString(rs, "name"));
		response.setNodeTemplateId(getString(rs, "node_template_id"));
		response.setPlanningId(getString(rs, "planning_id"));
		response.setQuestionContent(getString(rs, "question_content"));
		response.setRelatedTaskId(getString(rs, "related_task_id"));				
		response.setResource(getString(rs, "resource_list"));
		response.setTemplateId(getString(rs, "template_id"));
		response.setProjectId(getString(rs, "project_id"));
		response.setInstanceId(getInteger(rs, "instance_id"));
		response.setIsParentTask(getBoolean(rs, "is_parent_task"));
		response.setDecisionAnswer(getString(rs, "decision_answer"));
    	return response;
    }


}
