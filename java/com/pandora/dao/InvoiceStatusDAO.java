package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.InvoiceStatusTO;
import com.pandora.exception.DataAccessException;

public class InvoiceStatusDAO extends DataAccess {

	
    public Vector<InvoiceStatusTO> getList(Connection c) throws DataAccessException {
		Vector<InvoiceStatusTO> response= new Vector<InvoiceStatusTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
									   "from invoice_status");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    InvoiceStatusTO isto = this.populateByResultSet(rs);
			    response.addElement(isto);
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    

	public InvoiceStatusTO getInitialStatus() throws DataAccessException {
        Connection c = null;
        InvoiceStatusTO response =null;
		try {
			c = getConnection();
			response = this.getInitialStatus(c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
		return response;
	}    
	
	
	private InvoiceStatusTO getInitialStatus(Connection c) throws DataAccessException{
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		InvoiceStatusTO response = null;
		try {
			pstmt = c.prepareStatement("select id, name, description, state_machine_order " +
									   "from invoice_status where state_machine_order = ?");
			pstmt.setInt(1, InvoiceStatusTO.STATE_MACHINE_NEW);			
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

	
    private InvoiceStatusTO populateByResultSet(ResultSet rs) throws DataAccessException{
    	InvoiceStatusTO response = new InvoiceStatusTO();        
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setDescription(getString(rs, "description"));
        response.setStateMachineOrder(getInteger(rs, "state_machine_order"));
        return response;
    }

}
