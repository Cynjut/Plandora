package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.RequirementStatusTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 * This class contain all methods to handle data related with requirement Status entity into data base.
 */
public class RequirementStatusDAO extends DataAccess {

    /**
     * Get a list of all Requirement Status TOs from data base.
     * @param c
     * @throws DataAccessException
     */
    public Vector getList(Connection c) throws DataAccessException {
		Vector response = new Vector();
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
     */
    protected RequirementStatusTO populateByResultSet(ResultSet rs) throws DataAccessException{
        RequirementStatusTO response = new RequirementStatusTO();        
        response.setId(getString(rs, "ID"));
        response.setName(getString(rs, "NAME"));
        response.setDescription(getString(rs, "DESCRIPTION"));
        response.setStateMachineOrder(getInteger(rs, "STATE_MACHINE_ORDER"));
        return response;
    }


}
