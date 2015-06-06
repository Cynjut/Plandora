package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.RequirementStatusTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

/**
 * This class contain all methods to handle data related with requirement Status entity into data base.
 */
public class RequirementStatusDAO extends DataAccess {

    /**
     * Get a list of all Requirement Status TOs from data base.
     */
    public Vector getList(Connection c) throws DataAccessException {
		Vector<RequirementStatusTO> response = new Vector<RequirementStatusTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
									   "from requirement_status");
			rs = pstmt.executeQuery();
			while (rs.next()){
				RequirementStatusTO rsto = this.populateByResultSet(rs);			    
			    response.addElement(rsto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    } 
    
    
	public RequirementStatusTO getObjectByStateMachine(Integer state) throws DataAccessException {
        Connection c = null;
        RequirementStatusTO response = null;
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


	@Override
	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        RequirementStatusTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			RequirementStatusTO rsto = (RequirementStatusTO)to;
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
									   "from requirement_status where id=?");
			pstmt.setString(1, rsto.getId());			
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}

	
	
    /**
     * This method get a RequirementStatus object from BD that has the meaning of State Machine sent into argument.
     */
    public RequirementStatusTO getObjectByStateMachine(Connection c, Integer state) throws DataAccessException{
        RequirementStatusTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
									   "from requirement_status where state_machine_order=?");
			pstmt.setInt(1, state.intValue());			
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateByResultSet(rs);
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
    protected RequirementStatusTO populateByResultSet(ResultSet rs) throws DataAccessException{
        RequirementStatusTO response = new RequirementStatusTO();        
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setDescription(getString(rs, "description"));
        response.setStateMachineOrder(getInteger(rs, "state_machine_order"));
        return response;
    }



}
