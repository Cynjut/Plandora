package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.AdditionalFieldTO;
import com.pandora.CategoryTO;
import com.pandora.DecisionNodeTemplateTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.RequirementTriggerTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.RequirementBUS;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.MandatoryMetaFieldDataException;
import com.pandora.helper.DBUtil;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

/**
 * This class contain all methods to handle data related with Task entity into data base.
 */
public class TaskDAO extends PlanningDAO {

	AdditionalFieldDAO afdao = new AdditionalFieldDAO();
	
	DiscussionTopicDAO dtdao = new DiscussionTopicDAO();
	
    /**
     * Get a list of Task objects based on Requirement object.
     */
    public Vector<TaskTO> getTaskListByRequirement(RequirementTO rto, ProjectTO pto, boolean evenClose) throws DataAccessException {
        Vector<TaskTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getTaskListByRequirement(rto, pto, evenClose, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    /**
     * Get all tasks (child tasks) based to the parent task
     */
    public Vector<TaskTO> getSubTasksList(TaskTO parenttask) throws DataAccessException {
        Vector<TaskTO> response = null;
        Connection c = null;
		try {
			c = getConnection();		    
			response = this.getSubTasksList(parenttask, false, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    

    
    /**
     * Cancel Task from data base.
     */
    public void cancel(TransferObject to, boolean reopenRelatedReq) throws DataAccessException {
		Connection c = null;
		try {
			c = getConnection(false);
			this.cancel(to, reopenRelatedReq, c);
			c.commit();
		} catch(Exception e){
			try {
				c.rollback();
			} catch (Exception er) {
				throw new DataAccessException(e);
			}
			throw new DataAccessException(e);
			
		} finally{
			this.closeConnection(c);
		}		    	
    }

    
    public void updateByResource(TransferObject to) throws DataAccessException {
		Connection c = null;
		try {
			c = getConnection(false);
			this.updateByResource(to, c);
			c.commit();
		} catch(Exception e){
			try {
				c.rollback();
			} catch (Exception er) {
				throw new DataAccessException(e);
			}
			throw new DataAccessException(e);
			
		} finally{
			this.closeConnection(c);
		}		    	    	
    }

    
    
    /**
     * Update some fields of task (name, description and category).
     * This method is related to the update of resource task by the task owner. 
     * @throws Exception 
     */
    public void updateByOwner(TransferObject to) throws Exception {
		Connection c = null;
		try {
			c = getConnection(false);
			this.updateByOwner(to, c);
			c.commit();
		} catch(Exception e){
			try {
				c.rollback();
			} catch (Exception er) {
				throw new DataAccessException(e);
			}
			if (e instanceof MandatoryMetaFieldDataException) {
				throw e;
			} else {	
				throw new DataAccessException(e);
			}
			
		} finally{
			this.closeConnection(c);
		}		    	    	
    }
    
    /**
     * Get a list of Task objects that can be used for parent task.
     * The argument 'rto' is the requirement of task used such a filter. This filter could be null.
     */
    public Vector<TaskTO> getAvailableParentTaskList(RequirementTO rto, String projectId) throws DataAccessException {
        Vector<TaskTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getAvailableParentTaskList(rto, projectId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }


    /**
     * Get a list of tasks based on project object.
     */
    public Vector<TaskTO> getTaskListByProject(String projectList, Timestamp iniRange, Timestamp finalRange) throws DataAccessException {
        Vector<TaskTO> response = null;
        Connection c = null;
		try {
			c = getConnection(); 			
			response = this.getTaskListByProject(projectList, iniRange, finalRange, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    /**
     * Insert a list of Task objects into data base using the
     * same transaction.
     */
    public void insert(Vector<TaskTO> list) throws DataAccessException {
		Connection c = null;
		try {
			c = getConnection(false);
			this.insert(list, c);
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
     * Insert a list of Task objects into data base using the
     * same transaction.
     */
    public void insert(Vector<TaskTO> list, Connection c) throws DataAccessException {
		PlanningRelationDAO prdao = new PlanningRelationDAO();
		try {
			Iterator<TaskTO> i = list.iterator();
			while(i.hasNext()){
			    TaskTO tto = i.next();
			    this.insert(tto, c);
			}

			//if the tasks contain relationships, insert then all...
			Iterator<TaskTO> j = list.iterator();
			while(j.hasNext()){
			    TaskTO tto = j.next();
			    if (tto.getRelationList()!=null) {
					Iterator<PlanningRelationTO> k = tto.getRelationList().iterator();
					while(k.hasNext()){
						PlanningRelationTO prto = k.next();
						prdao.insertRelation(prto, c);
					}
			    }
			}
		} catch(Exception e){
			throw new DataAccessException(e);
		}		        
    }
    
    
    public String copyReqIteration(String requirementId, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		String iteration = null;
		
		try {    	
			RequirementDAO rdao = new RequirementDAO();
			RequirementTO rto = (RequirementTO) rdao.getObject(new RequirementTO(requirementId));

			if (rto!=null && rto.getIteration()!=null && !rto.getIteration().trim().equals("")) {				
				iteration = rto.getIteration();
				pstmt = c.prepareStatement("update planning set iteration=? where id in (select id from task where requirement_id=?) and iteration is null");
				pstmt.setString(1, iteration);
				pstmt.setString(2, rto.getId());
				pstmt.executeUpdate();				
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}
		return iteration;
    }

    
    /**
     * Get a list of tasks based on project object.
     */
    private Vector<TaskTO> getTaskListByProject(String projectList, Timestamp iniRange, Timestamp finalRange, Connection c) throws DataAccessException {
        Vector<TaskTO> response = new Vector<TaskTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
			if (finalRange==null) {
				finalRange = DateUtil.getNow();
			}
			
			DbQueryDelegate qdel = new DbQueryDelegate();
			String dbname = qdel.getDBProductName();
			
		    String sql = "select distinct t.id, t.name, t.project_id, t.requirement_id, t.category_id, t.task_id, t.is_parent_task, t.created_by, p.description, " +
			   				     "p.creation_date, p.final_date, p.iteration, pr.name as project_name, t.is_unpredictable, c.name as CATEGORY_NAME " +
			   			 "from category c, planning p, project pr, task t left outer join ( " +
			   			    "select min(COALESCE(sub.actualSlot, sub.estimSlot)) as firstslot, sub.task_id, sub.project_id, sub.resource_id from ( " +
			   			         "select " + DBUtil.addDate(dbname, "rt.actual_date", "rta.sequence-1") + " as actualSlot, " +
			   			       			     DBUtil.addDate(dbname, "rt.start_date",  "rta.sequence-1") + " as estimSlot, rt.task_id, rt.project_id, rt.resource_id " +
			   			         "from resource_task rt, resource_task_alloc rta " +
			   			         "where rt.resource_id = rta.resource_id and rt.project_id = rta.project_id and rt.task_id = rta.task_id " +
			   			           "and rt.project_id in (" + projectList + ") " +
			   			     ") as sub ";
		    
		    if (iniRange!=null && finalRange!=null) {
			    sql = sql + "where (sub.actualSlot is not null and sub.actualSlot >= ? and sub.actualSlot <= ?) " +
	   			               "or (sub.estimSlot is not null  and sub.estimSlot >= ? and sub.estimSlot <= ?) ";		    	
		    }
		    sql = sql + "group by sub.task_id, sub.project_id, sub.resource_id " +
			   			 ") as sub2 on (sub2.task_id = t.id and sub2.task_id = t.id and sub2.project_id = t.project_id) " +		    
			   			 "where t.id = p.id and t.project_id = pr.id and pr.id in (" + projectList + ") and c.id = t.category_id " +
			   			 "order by p.creation_date";

			pstmt = c.prepareStatement(sql);
			if (iniRange!=null && finalRange!=null) {			
		        pstmt.setTimestamp(1, iniRange);
		        pstmt.setTimestamp(2, finalRange);
		        pstmt.setTimestamp(3, iniRange);
		        pstmt.setTimestamp(4, finalRange);
		    }
		    
			rs = pstmt.executeQuery();
			while (rs.next()){
			    TaskTO tto = this.populateBeanByResultSet(rs);

			    CategoryTO cto = tto.getCategory();
			    cto.setName(getString(rs, "CATEGORY_NAME"));
			    
			    ProjectTO prto = tto.getProject();
			    prto.setName(getString(rs, "project_name"));

			    //get Allocated Resources related with task
			    ResourceTaskDAO rtdao = new ResourceTaskDAO(); 
			    tto.setAllocResources(rtdao.getListByTask(tto, c, true, false));

			    //consider only tasks with resources related (not canceled)
			    if ((tto.hasResourceTask()) || tto.isParentTask()){
			        response.addElement(tto);    
			    }
			 } 
						
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    /**
     * Get a list of Task objects based on Requirement object.
     */
    public Vector<TaskTO> getTaskListByRequirement(RequirementTO rto, ProjectTO pto, boolean evenClose, Connection c) throws DataAccessException {
        Vector<TaskTO> response = new Vector<TaskTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    
		    String closeConstraint = "";
		    if (!evenClose){
		        closeConstraint = "and p.FINAL_DATE is null ";    
		    }
		    
		    String reqConstraint = "";
		    if (rto!=null){
		        reqConstraint = "AND t.requirement_id=? ";    
		    }
		    
			pstmt = c.prepareStatement("select t.id, t.name, t.project_id, t.requirement_id, " +
									   "t.category_id, t.task_id, t.is_parent_task, t.created_by, p.description, " +
									   "p.creation_date, p.final_date, p.iteration, c.name as CATEGORY_NAME, cnt.instance_id, t.is_unpredictable " +
									   "from planning p, category c, task t LEFT OUTER JOIN " +
									   "(select instance_id, related_task_id  from custom_node_template) as cnt on cnt.related_task_id = t.id " +
									   "where t.id = p.id and t.project_id=? " +
									   "and t.category_id = c.id " + closeConstraint + reqConstraint +
									   "order by t.task_id, t.is_parent_task");
			pstmt.setString(1, pto.getId());
		    if (rto!=null){
		        pstmt.setString(2, rto.getId());    
		    }

			rs = pstmt.executeQuery();
			while (rs.next()){
			    TaskTO tto = this.populateBeanByResultSet(rs);
			    tto.setTemplateInstanceId(getInteger(rs, "instance_id"));
			    
			    //get remaining field...
			    CategoryTO cto = tto.getCategory();
			    cto.setName(getString(rs, "CATEGORY_NAME"));
			    
			    //Vector<ResourceTaskTO> list = rtdao.getListByTask(tto, c, false, true);
			    //if (list!=null && list.size()>0) {
					response.addElement(tto); 			    	
			    //}
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			this.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    private Vector<TaskTO> getAvailableParentTaskList(RequirementTO rto, String projectId, Connection c) throws DataAccessException {
        Vector<TaskTO> response = new Vector<TaskTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    String sql = "select t.id, t.name, t.requirement_id, t.project_id, " +
					     "t.task_id, t.category_id, t.is_parent_task, t.created_by, p.description, " +
					     "p.creation_date, p.final_date, p.iteration, t.is_unpredictable " +
					     "from task t, planning p " +
					     "WHERE t.id = p.id " +
					     "and p.FINAL_DATE is null " +
					     "and t.is_parent_task=1 and t.project_id=? ";
		    if (rto!=null){
		        sql+= "AND (t.requirement_id=? OR t.requirement_id is null)";
		    }
		    
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, projectId);
			if (rto!=null){
			    pstmt.setString(2, rto.getId());
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    TaskTO tto = this.populateBeanByResultSet(rs);			    			    
				response.addElement(tto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get Task object from data base.
     */
    public TransferObject getObject(TransferObject to, Connection c)  throws DataAccessException {
        TaskTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		RequirementDAO rdao = new RequirementDAO();
		TaskNodeTemplateDAO ntdao = new TaskNodeTemplateDAO();
		AttachmentDAO atDAO = new AttachmentDAO();
		
		try {
		    TaskTO tto = (TaskTO)to; 
			pstmt = c.prepareStatement("select t.id, t.name, t.project_id, t.requirement_id, t.category_id, " +
									   "c.billable as CATEGORY_BILLABLE, t.task_id, t.is_parent_task, t.created_by, p.description, " +
									   "p.creation_date, p.final_date, p.iteration, cnt.instance_id, t.is_unpredictable " +
									   "from planning p, category c, task t LEFT OUTER JOIN " +
									   "(select instance_id, related_task_id from custom_node_template) as cnt on cnt.related_task_id = t.id " +
									   "WHERE t.id = p.id and t.category_id = c.id " +
									   "AND t.id=?");			
			pstmt.setString(1, tto.getId());

			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateBeanByResultSet(rs);
			    response.setHandler(tto.getHandler());
			    
			    response.setTemplateInstanceId(getInteger(rs, "instance_id"));

			    CategoryTO cat = response.getCategory();
			    Integer billableCat = getInteger(rs, "CATEGORY_BILLABLE");
			    if (billableCat!=null) {
				    cat.setIsBillable(new Boolean(billableCat.intValue()==1));			    	
			    } else {
			    	cat.setIsBillable(new Boolean(false));
			    }
			    
			    //get related requirement...if necessary
			    RequirementTO rto = response.getRequirement();
			    if (rto!=null) {
			        rto = (RequirementTO)rdao.getObject(rto, c);
			        response.setRequirement(rto);
			    }
			    			    
			    //get Allocated Resources related with task
			    ResourceTaskDAO rtdao = new ResourceTaskDAO(); 
			    response.setAllocResources(rtdao.getListByTask(response, c, false, false));
			    
			    if (response.isParentTask()) {
			        this.loadSubTasksList(response, c);
			    }
			    
			    //get the additional fields and discussion topics
			    response.setAdditionalFields(afdao.getListByPlanning(response, null, c));
			    response.setDiscussionTopics(dtdao.getListByPlanning(response, c));
			    response.setAttachments(atDAO.getListByPlanningId(response.getId(), null)); 
			    
			    //check if related task contain a decision node linked...
			    DecisionNodeTemplateTO decision = ntdao.getDecisionNodeByTask(response, c);
			    if (decision!=null) {
			    	response.setDecisionNode(decision);
			    }
			    
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			this.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
	public void loadSubTasksList(TaskTO parenttask) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection();		    
			this.loadSubTasksList(parenttask, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
	}

	
    private void loadSubTasksList(TaskTO parentTaskTO, Connection c) throws DataAccessException{
		Vector<TaskTO> list = this.getSubTasksList(parentTaskTO, true, c);
		Iterator<TaskTO> i = list.iterator();
		while(i.hasNext()) {
			TaskTO child = i.next();
			parentTaskTO.addChild(child);
		}
    }    

    
    private Vector<TaskTO> getSubTasksList(TaskTO parentTaskTO, boolean isLeanLoading, Connection c) throws DataAccessException{
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		Vector<TaskTO> response = new Vector<TaskTO>();
		
		try {
			pstmt = c.prepareStatement("select t.id, t.name, t.project_id, t.requirement_id, " +
									   "t.category_id, t.task_id, t.is_parent_task, t.created_by, p.description, " +
									   "p.creation_date, p.final_date, p.iteration, t.is_unpredictable " +
									   "from task t, planning p " +
									   "WHERE t.id = p.id " +
									   "AND t.task_id=?");
			pstmt.setString(1, parentTaskTO.getId());

			rs = pstmt.executeQuery();
			while (rs.next()){
				TaskTO child = null;
			    if (isLeanLoading) {
			    	child = this.populateBeanByResultSet(rs);	
				    child.setHandler(parentTaskTO.getHandler());
			    } else {
			    	child = new TaskTO(getString(rs, "id"));
			    	child = (TaskTO) this.getObject(child, c);
			    }
			    response.addElement(child);			    
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}
		return response;
    }    
    
    
    /**
     * Insert a new Task object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
        RequirementBUS rbus = new RequirementBUS();
        RequirementDAO rdao = new RequirementDAO(); 
		PreparedStatement pstmt = null;
		
		try {
		    //create a new id
		    String newId = this.getNewId();
		    TaskTO tto = (TaskTO)to;
		    tto.setId(newId);
		    			
			int isParentTask = (tto.isParentTask()?1:0);

			//if necessary, update the id of new task object into additional fields
	        if (tto.getAdditionalFields()!=null) {
	        	Iterator<AdditionalFieldTO> i = tto.getAdditionalFields().iterator();
	        	while (i.hasNext()) {
	        		AdditionalFieldTO afto = i.next();
	        		PlanningTO plato = afto.getPlanning();
	        		if (plato.getId()==null || plato.getId().trim().equals("")) {
	        			plato.setId(newId);	
	        		}
	        	}
	        }

		    //insert data into parent entity (PlanningDAO)
		    super.insert(tto, c);
		    
		    //insert data of task
			pstmt = c.prepareStatement("insert into task (id, name, project_id, requirement_id, category_id, " +
									   "task_id, is_parent_task, created_by, is_unpredictable) " +
									   "values (?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, newId);
			pstmt.setString(2, tto.getName());
			pstmt.setString(3, tto.getProject().getId());
			if (tto.getRequirementId()!=null && !tto.getRequirementId().equals("")) {
			    pstmt.setString(4, tto.getRequirementId());    
			}else {
			    pstmt.setNull(4, java.sql.Types.VARCHAR);
			}
	        pstmt.setString(5, tto.getCategory().getId());    
			pstmt.setString(6, tto.getParentTaskId());
			pstmt.setInt(7, isParentTask);
			pstmt.setString(8, tto.getCreatedBy().getId());			
			if (tto.getIsUnpredictable()!=null) {
				pstmt.setInt(9, tto.getIsUnpredictable().booleanValue()?1:0);	
			} else {
				pstmt.setInt(9, 0);
			}
			pstmt.executeUpdate();

			//check if the related requirement iteration must be copied to task...
			if (tto.getRequirementId()!=null && !tto.getRequirementId().equals("")) {
				String reqIteration = this.copyReqIteration(tto.getRequirementId(), c);
				if (reqIteration!=null) {
					tto.setIteration(reqIteration);
				}
			}
			
			//insert the resource task objects related
			ResourceTaskDAO rtdao = new ResourceTaskDAO();
			Vector<ResourceTaskTO> allocations = tto.getAllocResources();
			rtdao.insert(allocations, tto, c);

			if (tto.getRequirementId()!=null && !tto.getRequirementId().equals("")) {

		        //create a new status of requirement to warn the customer the requested requirement is "planned"				
				RequirementTO currReq = tto.getRequirement();
	            RequirementTriggerTO rtgto = new RequirementTriggerTO();
		        try {
		            rtgto = rbus.getRequirementToChangeStatus(tto, TaskStatusTO.STATE_MACHINE_OPEN, null);
	            } catch (BusinessException e) {
	                throw new DataAccessException(e);
	            }
	            rdao.changeStatus(currReq, tto.getHandler(), rtgto, c);
			}
		
		} catch(MandatoryMetaFieldDataException e){
			throw e;
			
		} catch(Exception e){
			throw new DataAccessException(e);

		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    /**
     * Update data of Task object into data base.
     */
    public void update(TransferObject to, Connection c) throws DataAccessException {
        RequirementBUS rbus = new RequirementBUS();
        RequirementDAO rdao = new RequirementDAO(); 
		PreparedStatement pstmt = null; 
		try {
		    TaskTO tto = (TaskTO)to;
		    			
			int isParentTask = (tto.isParentTask()?1:0);
			
		    //update data into parent entity (PlanningDAO)
		    super.update(tto, c);

		    //update data of task
			pstmt = c.prepareStatement("update task set name=?, project_id=?, " +
							   		   "requirement_id=?, category_id=?, task_id=?, is_parent_task=?, is_unpredictable=? " +
							   		   "where id=?");
			pstmt.setString(1, tto.getName());
			pstmt.setString(2, tto.getProject().getId());
			if (tto.getRequirement()!=null){
			    pstmt.setString(3, tto.getRequirementId());    
			}else {
			    pstmt.setNull(3, java.sql.Types.VARCHAR);
			}			
			pstmt.setString(4, tto.getCategory().getId());
			pstmt.setString(5, tto.getParentTaskId());
			pstmt.setInt(6, isParentTask);
			if (tto.getIsUnpredictable()!=null) {
				pstmt.setInt(7, tto.getIsUnpredictable().booleanValue()?1:0); 
			} else {
				pstmt.setInt(7, 0);
			}			
			pstmt.setString(8, tto.getId());			
			pstmt.executeUpdate();

			if (tto.getRequirementId()!=null && !tto.getRequirementId().equals("")) {
				
				//check if the related requirement has the appropriate status				
				RequirementTO currReq = tto.getRequirement();
	        	RequirementTO rto = rbus.getRequirement(currReq);
	        	if (rto.getRequirementStatus().getStateMachineOrder().equals(RequirementStatusTO.STATE_MACHINE_WAITING)) {
	        		RequirementTriggerTO rtgto = new RequirementTriggerTO();
		            rtgto = rbus.getRequirementToChangeStatus(tto, TaskStatusTO.STATE_MACHINE_OPEN, null);
		            rdao.changeStatus(rto, tto.getHandler(), rtgto, c);			    
	        	}
	        	
	        	this.copyReqIteration(tto.getRequirementId(), c);
			}	
			
        	//check if task is part of workflow, and if true, put the requirement id into all
        	//tasks of workflow including custom_node_template
        	if (tto.getTemplateInstanceId()!=null) {
        		this.updateTaskByTemplateInstance(tto.getTemplateInstanceId(), tto.getRequirementId(), c);	        	
        	}
			
		} catch (MandatoryMetaFieldDataException e) {
			throw e;
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    private void updateByOwner(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    TaskTO tto = (TaskTO)to;
		    			
		    //update data into parent entity (PlanningDAO)
		    super.update(tto, c);

		    //update data of task
			pstmt = c.prepareStatement("update task set name=?, category_id=? where id=?");
			pstmt.setString(1, tto.getName());
			pstmt.setString(2, tto.getCategory().getId());
			pstmt.setString(3, tto.getId());			
			pstmt.executeUpdate();
		
		} catch (MandatoryMetaFieldDataException e) {
			throw e;
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    public void updateByResource(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    TaskTO tto = (TaskTO)to;
		    			
			pstmt = c.prepareStatement("update task set task_id=?, requirement_id=? where id=?");
			if (tto.getParentTask()!=null && !tto.getParentTaskId().equals("-1")) {
				pstmt.setString(1, tto.getParentTaskId());	
			} else {
				pstmt.setNull(1, java.sql.Types.VARCHAR);
			}
			
			if (tto.getRequirementId()!=null) {
				pstmt.setString(2, tto.getRequirementId());	
			} else {
				pstmt.setNull(2, java.sql.Types.VARCHAR);
			}

			pstmt.setString(3, tto.getId());			
			pstmt.executeUpdate();

			//update the iteration related to the task...
			pstmt = c.prepareStatement("update planning set iteration=? where id=?");
			if (tto.getIteration()!=null && !tto.getIteration().equals("-1")) {
				pstmt.setString(1, tto.getIteration());	
			} else {
				pstmt.setNull(1, java.sql.Types.VARCHAR);
			}
			pstmt.setString(2, tto.getId());			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    
    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    TaskTO tto = (TaskTO)to;
			pstmt = c.prepareStatement("delete from task_history where task_id = ?");
			pstmt.setString(1, tto.getId());			
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from resource_task_alloc where task_id = ?");
			pstmt.setString(1, tto.getId());			
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from resource_task_alloc_plann where task_id = ?");
			pstmt.setString(1, tto.getId());			
			pstmt.executeUpdate();	
			
			pstmt = c.prepareStatement("delete from plan_relation where plan_related_id=? or planning_id=?");
			pstmt.setString(1, tto.getId());
			pstmt.setString(2, tto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from repository_file_plan where planning_id=?");
			pstmt.setString(1, tto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from additional_field where planning_id=?");
			pstmt.setString(1, tto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from resource_task where task_id = ?");
			pstmt.setString(1, tto.getId());			
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from task where id = ?");
			pstmt.setString(1, tto.getId());			
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from planning where id = ?");
			pstmt.setString(1, tto.getId());			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}    	
    }
    
    
    public void cancel(TransferObject to, boolean reopenRelatedReq, Connection c) throws DataAccessException {
        RequirementBUS rbus = new RequirementBUS();
        RequirementDAO rdao = new RequirementDAO(); 
		ResourceTaskDAO rtdao = new ResourceTaskDAO();

        TaskTO tto = (TaskTO)to;
	    			
        //if the current requirement don't have remaining open tasks, 
		//set the status of requirement to "wait approve"
		RequirementTO currReq = tto.getRequirement(); 
        if (currReq!=null){
        	
        	boolean changeReqStatus = true;
            Vector<TaskTO> list = this.getTaskListByRequirement(currReq, tto.getProject(), false, c);
            Iterator<TaskTO> i = list.iterator();
            while(i.hasNext()) {
            	TaskTO t = i.next();
            	if (!t.getId().equals(tto.getId())) {
            		changeReqStatus = false;
            		break;
            	}
            }            	
        
            if (changeReqStatus) {
                RequirementTriggerTO rtgto = new RequirementTriggerTO();
    	        try {
    	        	if (reopenRelatedReq) {
    		            rtgto = rbus.getRequirementToChangeStatus(tto, TaskStatusTO.STATE_MACHINE_CANCEL, RequirementStatusTO.STATE_MACHINE_WAITING);		        		
    	        	} else {
    		            rtgto = rbus.getRequirementToChangeStatus(tto, TaskStatusTO.STATE_MACHINE_CANCEL, RequirementStatusTO.STATE_MACHINE_CLOSE);
    	        	}
                } catch (BusinessException e) {
                    throw new DataAccessException(e);
                }
                rdao.changeStatus(currReq, tto.getHandler(), rtgto, c);           		            	
            }            
        }
		
	    //update data into parent entity (PlanningDAO)
	    tto.setFinalDate(DateUtil.getNow());
	    super.update(tto, c);
	    
	    //retrieve from data base current resource task list related with task...
		tto.setAllocResources(rtdao.getListByTask(tto, c, true, false));

    	Iterator<ResourceTaskTO> i = tto.getAllocResources().iterator();
    	while (i.hasNext()) {
    		ResourceTaskTO rtto = i.next();
    		rtto.setHandler(tto.getHandler());
    		rtdao.changeStatus(rtto, TaskStatusTO.STATE_MACHINE_CANCEL, tto.getComment(), false, c);
    	}
	}
    
	public void reopen(TaskTO tto, String comment, Connection c) throws DataAccessException {
		ResourceTaskDAO rsdao = new ResourceTaskDAO();
		
	    //clear final data of task... 
	    tto.setFinalDate(null);
	    super.update(tto, c);

    	Iterator<ResourceTaskTO> i = tto.getAllocResources().iterator();
    	while (i.hasNext()) {
    		ResourceTaskTO rtto = i.next();
    		rtto.setHandler(tto.getHandler());
    		rsdao.changeStatus(rtto, TaskStatusTO.STATE_MACHINE_REOPEN, comment, false, c);
    	}
	}

	
    /**
     * Create a fake task and persist into data base
     */
    public void createPreApprovedTask(RequirementTO rto, UserTO createdBy, Connection c) throws DataAccessException{
        TaskTO tto = new TaskTO();
        
        //create a fake list of resource alloc
        ResourceTaskTO rtto = new ResourceTaskTO();
        rtto.setEstimatedTime(rto.getEstimatedTime());
        rtto.setHandler(rto.getRequester());
        rtto.setResource(new ResourceTO(rto.getPreApprovedReqResource()));
        rtto.setStartDate(rto.getSuggestedDate());
        rtto.setTask(tto);
        //rtto.setTaskStatus();
        Vector<ResourceTaskTO> alloc = new Vector<ResourceTaskTO>();
        alloc.addElement(rtto);
        
        tto.setAllocResources(alloc);
        tto.setCategory(new CategoryTO(CategoryTO.DEFAULT_CATEGORY_ID));
        tto.setCreationDate(DateUtil.getNow());
        tto.setDescription(rto.getDescription());
        tto.setIsParentTask(new Integer(0));
        tto.setHandler(rto.getRequester());
        tto.setCreatedBy(createdBy);
        
        tto.setName(StringUtil.trunc(rto.getDescription(), 50));
        tto.setParentTask(null);
        tto.setProject(rto.getProject());
        tto.setRequirement(rto);
        insert(tto, c);
    }
    
    
    private void updateTaskByTemplateInstance(Integer instanceId, String newReqId, Connection c) throws DataAccessException{
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		TaskNodeTemplateDAO tntdao = new TaskNodeTemplateDAO();
		try {
			
			//get a list of tasks with the same template instance... 
			pstmt = c.prepareStatement("select t.id from task t, custom_node_template cn " +
									   "where cn.related_task_id = t.id and cn.instance_id=?");
			pstmt.setInt(1, instanceId.intValue());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String taskId = rs.getString("id");

				//update the same requirement id for each element of list...				
				pstmt = c.prepareStatement("update task set requirement_id=? where id=? ");
				if (newReqId!=null) {
					pstmt.setString(1, newReqId);	
				} else {
					pstmt.setNull(1, Types.VARCHAR);					
				}
				pstmt.setString(2, taskId);
				pstmt.executeUpdate();									
			}

			//update requirement references into custom_node_template...			
			tntdao.updatePlanningOfIntance(instanceId, newReqId, c);				
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}    	
    }

    
    /**
     * Create a new TO object based on data into result set.
     */
    protected TaskTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        TaskTO response = new TaskTO();
        
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        response.setIsParentTask(getInteger(rs, "is_parent_task"));
        response.setCreatedBy(new UserTO(getString(rs, "created_by")));
        response.setIteration(getString(rs, "iteration"));

        String reqId = getString(rs, "requirement_id");
        if (reqId!=null){
            response.setRequirement(new RequirementTO(reqId));
        }
        
        response.setCategory(new CategoryTO(getString(rs, "category_id")));
        
        String parentTaskId = getString(rs, "task_id");
        if (parentTaskId!=null){
            response.setParentTask(new TaskTO(parentTaskId));
        }
        
        Integer unpredict = getInteger(rs, "is_unpredictable");
        if (unpredict!=null) {
        	response.setIsUnpredictable(new Boolean(unpredict.intValue()==1));
        } else {
        	response.setIsUnpredictable(new Boolean(false));
        }
        
        return response;
    }



}
