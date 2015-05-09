package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.OccurrenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 * This class contain all methods to handle data related with Task History entity into data base.
 */
public class TaskHistoryDAO extends DataAccess {

    /**
     * Return a list of Task History objects from database based on task id.
     */
    public Vector getListByTask(String taskId) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByTask(taskId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }


	/**
     * Return a list of Task History objects from database based on task id and resource id.
     */
    public Vector getListByResourceTask(String taskId, String resourceId) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByResourceTask(taskId, resourceId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    /**
     * Return a list of Task History objects from database based on requirement history object reference.
     */
    public Vector getListByRequirementHistory(RequirementHistoryTO rhto) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByRequirementHistory(rhto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }    
    

    /**
     * Return a list of Task History objects from database based on requirement id.
     */
    public Vector getHistoryByRequirementId(String reqId) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getHistoryByRequirementId(reqId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    
    /**
     * Return the newest Task History object from database based on Resource Task object.
     */
    public TaskHistoryTO getLastHistoryByTask(ResourceTaskTO rtto) throws DataAccessException {
        TaskHistoryTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getLastHistoryByTask(rtto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    /**
     * Return a list of Task History objects from database based on task id.
     */
    private Vector getListByTask(String taskId, Connection c) throws DataAccessException {
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select th.task_id, th.resource_id, th.project_id, th.task_status_id, ts.state_machine_order, " +
									      "th.creation_date, th.start_date, th.estimated_Time, th.actual_date, " +
									      "th.actual_Time, th.user_id, th.comment, u.name, u.username, ts.name as taskStatusName, " +
									      "t.requirement_id, th.iteration, o.name as ITERATION_NAME " +
									   "from tool_user u, task_status ts, task t, " +
									      "task_history th LEFT OUTER JOIN occurrence o on (o.id = th.iteration) " +
									   "where th.task_id=? " +
									      "and u.id = th.user_id " +
									      "and th.task_id = t.id " +
									      "and th.task_status_id = ts.id " +
									   "order by th.creation_date");
			pstmt.setString(1, taskId);
			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    TaskHistoryTO thto = this.populateBeanByResultSet(rs);

			    //get remaining fields...
			    TaskStatusTO tsto = thto.getStatus();
			    tsto.setName(getString(rs, "taskStatusName"));
			    tsto.setStateMachineOrder(getInteger(rs, "state_machine_order"));
			    
			    TaskTO tto = thto.getResourceTask().getTask();
			    tto.setRequirement(new RequirementTO(getString(rs, "requirement_id")));
			    
			    UserTO uto = thto.getHandler();
			    uto.setName(getString(rs, "name"));
			    uto.setUsername(getString(rs, "username"));
			    
			    response.addElement(thto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Return a list of Task History objects from database based on requirement history object reference.
     */
    private Vector getListByRequirementHistory(RequirementHistoryTO rhto, Connection c) throws DataAccessException {
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select th.task_id, th.resource_id, th.project_id, th.task_status_id, " +
									     "th.creation_date, th.start_date, th.estimated_time, th.actual_date, " +
									     "th.actual_time, th.user_id, th.comment, u.username, t.requirement_id, " +
									     "th.iteration, o.name as ITERATION_NAME " +
									   "from tool_user u, task t, " +
									     "task_history th LEFT OUTER JOIN occurrence o on (o.id = th.iteration) " +
									   "where u.id = th.user_id and th.task_id = t.id " +
									     "and t.requirement_id = ? " +
									     "and (th.creation_date <= ? or th.task_status_id = ?)" +
									   "order by th.creation_date");
			pstmt.setString(1, rhto.getRequirementId());
			pstmt.setTimestamp(2, rhto.getDate());
			pstmt.setString(3, rhto.getStatus().getId());
			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    TaskHistoryTO thto = this.populateBeanByResultSet(rs);
			    
			    UserTO uto = thto.getHandler();
			    uto.setUsername(getString(rs, "username"));
			    
			    response.addElement(thto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    /**
     * Return a list of Task History objects from database based on task id and resource id.
     */
    private Vector getListByResourceTask(String taskId, String resourceId, Connection c) throws DataAccessException {
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select th.task_id, th.resource_id, th.project_id, th.task_status_id, " +
									     "th.creation_date, th.start_date, th.estimated_time, th.actual_date, " +
									     "th.actual_time, th.user_id, th.comment, u.name, u.username, ts.name as taskStatusName, " +
									     "t.requirement_id, th.iteration, o.name as ITERATION_NAME  " +
									   "from tool_user u, task_status ts, task t, " +
									     "task_history th LEFT OUTER JOIN occurrence o on (o.id = th.iteration) " +
									   "where th.task_id=? " +
									     "and th.resource_id =? " +
									     "and u.id = th.user_id " +
									     "and th.task_id = t.id " +
									     "and th.task_status_id = ts.id " +
									   "order by th.creation_date");
			pstmt.setString(1, taskId);
			pstmt.setString(2, resourceId);
			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    TaskHistoryTO thto = this.populateBeanByResultSet(rs);

			    //get remaining fields...
			    TaskStatusTO tsto = thto.getStatus();
			    tsto.setName(getString(rs, "taskStatusName"));
			    
			    TaskTO tto = thto.getResourceTask().getTask();
			    tto.setRequirement(new RequirementTO(getString(rs, "requirement_id")));
			    
			    UserTO uto = thto.getHandler();
			    uto.setName(getString(rs, "name"));
			    uto.setUsername(getString(rs, "username"));
			    
			    response.addElement(thto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);			
		}	 
		return response;
    }
    
    
    /**
     * Return a list of Task History objects from database based on requirement id.
     */
    private Vector getHistoryByRequirementId(String reqId, Connection c) throws DataAccessException {
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select th.task_id, th.resource_id, th.project_id, th.task_status_id, " +
									     "th.creation_date, th.start_date, th.estimated_Time, th.actual_date, " +
									     "th.actual_Time, th.user_id, th.comment, u.username, ts.name as taskStatusName, " +
									     "th.iteration, o.name as ITERATION_NAME " +
									   "from tool_user u, task_status ts, task t, " +
									     "task_history th LEFT OUTER JOIN occurrence o on (o.id = th.iteration) " +
									   "where th.task_id = t.id " +
									     "and u.id = th.user_id " +
									     "and th.task_status_id = ts.id " +
									     "and t.requirement_id = ? " +
									   "order by th.creation_date");
			pstmt.setString(1, reqId);
			rs = pstmt.executeQuery();
			while (rs.next()){
			    TaskHistoryTO thto = this.populateBeanByResultSet(rs);
			    
			    //get remaining fields...
			    TaskStatusTO tsto = thto.getStatus();
			    tsto.setName(getString(rs, "taskStatusName"));
			    
			    TaskTO tto = thto.getResourceTask().getTask();
			    tto.setRequirement(new RequirementTO(reqId));
			    
			    UserTO uto = thto.getHandler();
			    uto.setUsername(getString(rs, "username"));

			    response.addElement(thto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);			
		}	 
		return response;
    }
    
    
    /**
     * Return the newest Task History object from database based on Resource Task object.
     */
    private TaskHistoryTO getLastHistoryByTask(ResourceTaskTO rtto, Connection c) throws DataAccessException {
        TaskHistoryTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select th.task_id, th.resource_id, th.project_id, th.task_status_id, " +
									   "th.creation_date, th.start_date, th.estimated_Time, th.actual_date, th.actual_Time, " +
									   "th.user_id, th.comment, th.iteration, o.name as ITERATION_NAME " +
									   "from task_history th LEFT OUTER JOIN occurrence o on (o.id = th.iteration) " +
									   "where task_id=? and resource_id=? " +
									   "order by creation_date desc");
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateBeanByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    TaskHistoryTO thto = (TaskHistoryTO)to;
			pstmt = c.prepareStatement("delete from task_history where task_id = ? " +
									  "and resource_id=? and project_id=? and creation_date=?");
			pstmt.setString(1, thto.getResourceTask().getTask().getId());
			pstmt.setString(2, thto.getResourceTask().getResource().getId());
			pstmt.setString(3, thto.getResourceTask().getResource().getProject().getId());
			pstmt.setTimestamp(4, thto.getDate());
			pstmt.executeUpdate();
		    
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }


    public void removeByResourceTask(ResourceTaskTO rtto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("delete from task_history where task_id = ? " +
									  "and resource_id=? and project_id=?");
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getResource().getProject().getId());
			pstmt.executeUpdate();
		    
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    /**
     * Insert a new Task History object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    TaskHistoryTO thto = (TaskHistoryTO)to;

			pstmt = c.prepareStatement("insert into task_history (task_id, resource_id, " +
			        				   "start_date, estimated_Time, actual_date, actual_Time, " +
									   "project_id, task_status_id, creation_date, user_id, comment, iteration) " +
									   "values (?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, thto.getResourceTask().getTask().getId());
			pstmt.setString(2, thto.getResourceTask().getResource().getId());
			pstmt.setTimestamp(3, thto.getStartDate());
			pstmt.setInt(4, thto.getEstimatedTime().intValue());
			pstmt.setTimestamp(5, thto.getActualDate());
			if (thto.getActualTime()!=null){
			    pstmt.setInt(6, thto.getActualTime().intValue());    
			} else {
			    pstmt.setNull(6, java.sql.Types.DECIMAL);
			}
			pstmt.setString(7, thto.getResourceTask().getTask().getProject().getId());
			pstmt.setString(8, thto.getStatus().getId());
			if (thto.getDate()==null) {
				pstmt.setTimestamp(9, DateUtil.getNow());	
			} else {
				pstmt.setTimestamp(9, thto.getDate());
			}
			pstmt.setString(10, thto.getHandler().getId());
			pstmt.setString(11, thto.getComment());
			if (thto.getIteration()!=null && thto.getIteration().getId()!=null 
					&& !thto.getIteration().getId().equals("-1") && !thto.getIteration().getId().equals("")){
				pstmt.setString(12, thto.getIteration().getId());	
			} else {
				pstmt.setNull(12, java.sql.Types.VARCHAR);
			}

			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);			
		}       
    }
    
    /**
     * Create a TaskHistory object based on ResourceTask object.
     */
    public TaskHistoryTO populateBeanByResourceTask(ResourceTaskTO rtto, TaskStatusTO tsto, String comment) throws DataAccessException{
        TaskHistoryTO thto = new TaskHistoryTO();
		thto.setResourceTask(rtto);
		thto.setStatus(tsto);
		thto.setComment(comment);
		thto.setActualDate(rtto.getActualDate());
		thto.setActualTime(rtto.getActualTime());
		if (rtto.getEstimatedTime()!=null) {
			thto.setEstimatedTime(rtto.getEstimatedTime());	
		} else {
			thto.setEstimatedTime(new Integer(0));
		}
		thto.setStartDate(rtto.getStartDate());
		thto.setHandler(rtto.getHandler());
		if (rtto.getTask()!=null && rtto.getTask().getIteration()!=null) {
			thto.setIteration(new OccurrenceTO(rtto.getTask().getIteration()));
		}
		return thto;
    }
    
    
    /**
     * Create a new TO object based on data into result set.
     */
    protected TaskHistoryTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        TaskHistoryTO response = new TaskHistoryTO();
        ResourceTaskTO rtto = new ResourceTaskTO();
        
        response.setActualDate(getTimestamp(rs, "actual_date"));
        response.setActualTime(getInteger(rs, "actual_time"));
        response.setEstimatedTime(getInteger(rs, "estimated_time"));
        response.setStartDate(getTimestamp(rs, "start_date"));
        response.setDate(getTimestamp(rs, "creation_date"));
        response.setComment(getString(rs, "comment"));
        response.setStatus(new TaskStatusTO(getString(rs, "task_status_id")));
        response.setHandler(new UserTO(getString(rs, "user_id")));
        
        OccurrenceTO iteration = new OccurrenceTO(getString(rs, "iteration"));
        iteration.setName(getString(rs, "ITERATION_NAME"));
        response.setIteration(iteration);
        
        ProjectTO pto = new ProjectTO(getString(rs, "project_id"));
        
        TaskTO tto = new TaskTO(getString(rs, "task_id"));
        tto.setProject(pto);
        rtto.setTask(tto);
        
        ResourceTO rto = new ResourceTO(getString(rs, "resource_id"));
        rto.setProject(pto);
        rtto.setResource(rto);        
        
        response.setResourceTask(rtto);
        
        return response;
        
    }
}
