package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.RiskStatusTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

public class RiskStatusDAO extends DataAccess {

    public Vector getList(Connection c) throws DataAccessException {
		Vector<RiskStatusTO> response= new Vector<RiskStatusTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {		    
			pstmt = c.prepareStatement("select id, name, description, status_type from risk_status");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    RiskStatusTO to = this.populateBeanByResultSet(rs);
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
    	RiskStatusTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, description, " +
										"status_type from risk_status where id=?");
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
    
    
    private RiskStatusTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        RiskStatusTO response = new RiskStatusTO(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setDescription(getString(rs, "description"));
        response.setStatusType(getString(rs, "status_type"));
        return response;
    }    
}
