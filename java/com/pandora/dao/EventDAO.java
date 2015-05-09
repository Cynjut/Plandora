package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.pandora.EventTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

/**
 * 
 */
public class EventDAO extends DataAccess {

    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    EventTO eto = (EventTO)to;		    
			pstmt = c.prepareStatement("insert into event_log (summary, description, " +
									   "creation_date, username) values (?, ?, ?, ?)");
			pstmt.setString(1, eto.getSummary());
			pstmt.setString(2, eto.getDescription());
			pstmt.setTimestamp(3, eto.getCreationDate());
			pstmt.setString(4, eto.getUsername());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			try {
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    System.out.println("DB Closing statement error: " + ec);
			} 		
		}       
    }
    
}
