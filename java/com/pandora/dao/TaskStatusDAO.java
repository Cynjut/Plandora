package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 * This class contain all methods to handle data related with Task Status entity into data base.
 */
public class TaskStatusDAO extends DataAccess {

    /**
     * Get a TaskStatus object from data base related with a Resource Task object.
     */
    public TaskStatusTO getObjectByResourceTask(ResourceTaskTO rtto) throws DataAccessException{    
        TaskStatusTO response = null;
	    Connection c = null;
		try {
			c = getConnection();
			response = this.getObjectByResourceTask(rtto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
	    return response;
    }
    
    /**
     * This method get a TaskStatus object from BD that has the meaning of State Machine 
     * sent by argument.
     */
    public TaskStatusTO getObjectByStateMachine(Integer state) throws DataAccessException{    
        TaskStatusTO response = null;
	    Connection c = null;
		try {
			c = getConnection();
			response = this.getObjectByStateMachine(c, state);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
	    return response;
    }
    
    /**
     * Get a TaskStatus object from data base related with a Resource Task object.
     */
    private TaskStatusTO getObjectByResourceTask(ResourceTaskTO rtto, Connection c) throws DataAccessException{
        TaskStatusTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select ts.id, ts.name, ts.description, ts.state_machine_order " +
			        			       "from task_status ts, resource_task rt " +
			        				   "WHERE rt.task_status_id = ts.id " +
			        				   "AND rt.task_id=? and rt.resource_id=? and rt.project_id=?");
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getResource().getProject().getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			//Close the current result set and statement
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
		return response;        
    }
    
    /**
     * This method get a TaskStatus object from BD that has the meaning of State Machine 
     * sent by argument.
     */
    public TaskStatusTO getObjectByStateMachine(Connection c, Integer state) throws DataAccessException{
        TaskStatusTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
									   "from task_status where state_machine_order=?");
			pstmt.setInt(1, state.intValue());			
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			//Close the current result set and statement
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
		return response;
    }

    
    /**
     * Get a list of Task Status objects from database.
     * @param c
     * @return
     * @throws DataAccessException
     */
    public Vector getList(Connection c) throws DataAccessException {
		Vector response= new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select ID, NAME, DESCRIPTION, STATE_MACHINE_ORDER " +
									   "from task_status");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    TaskStatusTO tsto = this.populateByResultSet(rs);
			    response.addElement(tsto);
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			//Close the current result set and statement
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
		return response;
    }
    
    /**
     * Get a Task Status object from database.
     * @param c
     * @return
     * @throws DataAccessException
     */
    public TransferObject getObject(TransferObject to, Connection c)  throws DataAccessException {
        TaskStatusTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select ID, NAME, DESCRIPTION, STATE_MACHINE_ORDER " +
									   "from task_status " +
									   "WHERE ID=?");
			pstmt.setString(1, to.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateByResultSet(rs);
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			//Close the current result set and statement
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
		return response;
    }
    
    /**
     * Create a new TO object based on data into result set.
     * @param rs
     * @return
     * @throws DataAccessException
     */
    protected TaskStatusTO populateByResultSet(ResultSet rs) throws DataAccessException{
        TaskStatusTO response = new TaskStatusTO();        
        response.setId(getString(rs, "ID"));
        response.setName(getString(rs, "NAME"));
        response.setDescription(getString(rs, "DESCRIPTION"));
        response.setStateMachineOrder(getInteger(rs, "STATE_MACHINE_ORDER"));
        return response;
    }    
    
}
