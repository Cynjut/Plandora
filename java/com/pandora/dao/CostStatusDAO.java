package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.CostStatusTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

public class CostStatusDAO extends DataAccess {

	
	public Vector<CostStatusTO> getCostStatusList(Integer minimumState) throws DataAccessException {
        Connection c = null;
        Vector<CostStatusTO> response = new Vector<CostStatusTO>();
		try {
			c = getConnection();
			response = this.getCostStatusList(minimumState, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
		return response;
	}

	
	public CostStatusTO getCostStatusByState(Integer state) throws DataAccessException {
        Connection c = null;
        CostStatusTO response = null;
		try {
			c = getConnection();
			response = this.getCostStatusByState(state, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
		return response;
	}
	
	
	private CostStatusTO getCostStatusByState(Integer state, Connection c) throws DataAccessException {
    	CostStatusTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
									   "from cost_status where state_machine_order=?");
			pstmt.setInt(1, state.intValue());
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


    private Vector<CostStatusTO> getCostStatusList(Integer minimumState, Connection c) throws DataAccessException {
		Vector<CostStatusTO> response= new Vector<CostStatusTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {		    
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
									   "from cost_status where state_machine_order >= ? " +
									   "order by state_machine_order");
			pstmt.setInt(1, minimumState.intValue());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    CostStatusTO to = this.populateBeanByResultSet(rs);
			    response.addElement(to);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
    	CostStatusTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
			   						   "from cost_status where id=?");
			pstmt.setString(1, to.getId());
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
    
    
    private CostStatusTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        CostStatusTO response = new CostStatusTO(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setGenericTag(response.getName());
        response.setDescription(getString(rs, "description"));
        response.setStateMachineOrder(getInteger(rs, "state_machine_order"));
        return response;
    }



}
