package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.OccurrenceHistoryTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;


public class OccurrenceHistoryDAO extends DataAccess {

    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    OccurrenceHistoryTO ohto = (OccurrenceHistoryTO)to;
		    
			pstmt = c.prepareStatement("insert into occurrence_history (occurrence_id, " +
									    "occurrence_status, occurrence_status_label, creation_date, " +
									    "user_id, history) values (?,?,?,?,?,?)");
			pstmt.setString(1, ohto.getOccurrenceId());
			pstmt.setString(2, ohto.getOccurrenceStatus());
			pstmt.setString(3, ohto.getOccurrenceStatusLabel());
			pstmt.setTimestamp(4, ohto.getCreationDate());
			pstmt.setString(5, ohto.getUser().getId());
			pstmt.setString(6, ohto.getContent());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			try {
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}       
    }


    public Vector<OccurrenceHistoryTO> getListByOccurrence(String occId) throws DataAccessException{
        Vector<OccurrenceHistoryTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByOccurrence(occId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }    
    
    
    private Vector<OccurrenceHistoryTO> getListByOccurrence(String occId, Connection c) throws DataAccessException{
        Vector<OccurrenceHistoryTO> response= new Vector<OccurrenceHistoryTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    String sql = "select oh.occurrence_id, oh.occurrence_status, oh.occurrence_status_label, " +
		    		     "oh.creation_date, oh.history, oh.user_id, u.name from occurrence_history oh, tool_user u " +
		    			 "where u.id = oh.user_id and oh.occurrence_id = ? " +
		    			 "order by oh.creation_date";
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, occId);
			rs = pstmt.executeQuery();
						
			while (rs.next()){
			    OccurrenceHistoryTO ohto = this.populateByResultSet(rs);		        
				response.addElement(ohto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    protected OccurrenceHistoryTO populateByResultSet(ResultSet rs) throws DataAccessException{
        OccurrenceHistoryTO response = new OccurrenceHistoryTO();
        
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setContent(getString(rs, "history"));
        response.setOccurrenceId(getString(rs, "occurrence_id"));
        response.setOccurrenceStatus(getString(rs, "occurrence_status"));
        response.setOccurrenceStatusLabel(getString(rs, "occurrence_status_label"));
        
        UserTO uto = new UserTO(getString(rs, "user_id"));
        uto.setName(getString(rs, "name"));
        response.setUser(uto);
        
        return response;
    }
        
}
