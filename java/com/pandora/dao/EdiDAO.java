package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.pandora.EdiTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

public class EdiDAO extends DataAccess {

	public HashMap<String, EdiTO> getUserEdiUUID(UserTO uto) throws DataAccessException {
		HashMap<String, EdiTO> response = null;
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getUserEdiUUID(uto, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;
	}


    /**
     * Insert a new area into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			EdiTO rto = (EdiTO)to;
			pstmt = c.prepareStatement("insert into user_edi_link (user_id, edi_id, last_update, edi_uuid) values (?,?,?,?)");
			pstmt.setString(1, rto.getUserId());
			pstmt.setString(2, rto.getEdiId());
			pstmt.setTimestamp(3, rto.getUpdateDate());
			pstmt.setString(4, rto.getEdiUUID());
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

	
	public EdiTO getEdiFromUUID(String uuid) throws DataAccessException {
		EdiTO response = null;
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getUserEdiUUID(uuid, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;
	}
    
    
	
	private HashMap<String, EdiTO> getUserEdiUUID(UserTO uto, Connection c) throws DataAccessException {
		HashMap<String, EdiTO> response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select user_id, edi_id, last_update, edi_uuid " +
									   "from user_edi_link where user_id=?");
			pstmt.setString(1, uto.getId());			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (response==null) {
					response = new HashMap<String, EdiTO>();
				}
				EdiTO rto = this.populateBeanByResultSet(rs);
			    response.put(rto.getEdiId(), rto);
			}			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}
	
	private EdiTO getUserEdiUUID(String uuid, Connection c) throws DataAccessException {
		EdiTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select user_id, edi_id, last_update, edi_uuid " +
									   "from user_edi_link where edi_uuid=?");
			pstmt.setString(1, uuid);			
			rs = pstmt.executeQuery();
			if (rs.next()) {
				response = this.populateBeanByResultSet(rs);
			}			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}
	
	
    private EdiTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
    	EdiTO response = new EdiTO();
        response.setUserId(getString(rs, "user_id"));
        response.setEdiId(getString(rs, "edi_id"));
        response.setEdiUUID(getString(rs, "edi_uuid"));
        response.setUpdateDate(getTimestamp(rs, "last_update"));
    	return response;
    }	

}
