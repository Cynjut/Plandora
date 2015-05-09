package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.AttachmentHistoryTO;
import com.pandora.AttachmentTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 */
public class AttachmentHistoryDAO extends DataAccess {
    
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    AttachmentHistoryTO hto = (AttachmentHistoryTO)to;
		    
			pstmt = c.prepareStatement("insert into attachment_history (attachment_id, status, " +
									    "creation_date, user_id, history) values (?,?,?,?,?)");
			pstmt.setString(1, hto.getAttachment().getId());
			pstmt.setString(2, hto.getStatus());
			pstmt.setTimestamp(3, hto.getCreationDate());
			pstmt.setString(4, hto.getUser().getId());
			pstmt.setString(5, hto.getHistory());
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


    public Vector getListByAttachment(String attachmentId) throws DataAccessException{
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByAttachment(attachmentId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }    
    
    
    private Vector getListByAttachment(String attachmentId, Connection c) throws DataAccessException{
        Vector response= new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
		    String sql = "select ah.attachment_id, ah.status, ah.creation_date, " +
		    					"ah.user_id, ah.history, u.name as USER_NAME " +
		    		     "from attachment_history ah, tool_user u " +
		    			 "where u.id = ah.user_id and ah.attachment_id = ? " +
		    			 "order by ah.creation_date";
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, attachmentId);
			rs = pstmt.executeQuery();
			while (rs.next()){
			    AttachmentHistoryTO hto = this.populateByResultSet(rs);		        
				response.addElement(hto);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
		return response;
    }
    
    protected AttachmentHistoryTO populateByResultSet(ResultSet rs) throws DataAccessException{
        AttachmentHistoryTO response = new AttachmentHistoryTO();

	    response.setAttachment(new AttachmentTO(getString(rs, "attachment_id")));
	    response.setStatus(getString(rs, "status"));
	    response.setHistory(getString(rs, "history"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));

        UserTO uto = new UserTO(getString(rs, "user_id"));
        uto.setName(getString(rs, "USER_NAME"));
        response.setUser(uto);
        
        return response;
    }


}
