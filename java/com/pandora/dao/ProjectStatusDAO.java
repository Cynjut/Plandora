package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.ProjectStatusTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

/**
 * This class contain all methods to handle data related with Project Status 
 * entity into data base.
 */
public class ProjectStatusDAO extends DataAccess {

    /**
     * Get a list of Project Status objects from database.
     */
    public Vector getList(Connection c) throws DataAccessException {
		Vector<ProjectStatusTO> response= new Vector<ProjectStatusTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, note, state_machine_order " +
									   "from project_status");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ProjectStatusTO psto = this.populateObjectByResultSet(rs);
			    response.addElement(psto);
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

	
  
	public ProjectStatusTO getProjectStatus(Integer status) throws DataAccessException { 
		ProjectStatusTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getProjectStatus(status, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }
	
	
    /**
     * Get a Project Status object from database.
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException{
		ProjectStatusTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    ProjectStatusTO psto = (ProjectStatusTO)to;
			pstmt = c.prepareStatement("select id, name, note, state_machine_order " +
									   "from project_status " +
									   "where ID=?");
			pstmt.setString(1, psto.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateObjectByResultSet(rs);
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    private ProjectStatusTO getProjectStatus(Integer status, Connection c) throws DataAccessException{
		ProjectStatusTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, note, state_machine_order " +
									   "from project_status where state_machine_order=?");
			pstmt.setInt(1, status.intValue());
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateObjectByResultSet(rs);
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;    	
    }
    
    
    /**
     * Create a new TO object based on data into result set.
     */
    private ProjectStatusTO populateObjectByResultSet(ResultSet rs) throws DataAccessException {
        ProjectStatusTO response = new ProjectStatusTO(); 
        response.setId(getString(rs, "ID"));
        response.setName(getString(rs, "NAME"));
        response.setNote(getString(rs, "NOTE"));
        response.setStateMachineOrder(getInteger(rs, "STATE_MACHINE_ORDER"));
        return response;
    }

}
