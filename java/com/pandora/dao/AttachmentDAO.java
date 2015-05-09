package com.pandora.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.AttachmentHistoryTO;
import com.pandora.AttachmentTO;
import com.pandora.PlanningTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 */
public class AttachmentDAO extends DataAccess {


    public Vector getListByPlanningId(String planningId) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByPlanningId(planningId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }

    
    public AttachmentTO getAttachmentFile(AttachmentTO ato) throws DataAccessException {
        Connection c = null;
        AttachmentTO response = null;
		try {
			c = getConnection();
			response = this.getAttachmentFile(ato, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
		return response;
    }

    
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        AttachmentTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {

		    AttachmentTO filter = (AttachmentTO)to;
		    pstmt = c.prepareStatement("select id, planning_id, name, " +
		    		"status, visibility, type, content_type, creation_date, comment from attachment " +
		    		"where id=?");		
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateObjectByResultSet(rs);
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
    
    
    private AttachmentTO getAttachmentFile(AttachmentTO to, Connection c) throws DataAccessException {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		AttachmentTO response = null;
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
		    
		    response = (AttachmentTO) this.getObject(to);
		    response.setHandler(to.getHandler());
		    
		    pstmt = c.prepareStatement("select binary_file from attachment where id=?");		
			pstmt.setString(1, to.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
			    bis = (ByteArrayInputStream)rs.getBinaryStream("binary_file");
			    response.setBinaryFile(bis , bis.available());
			    
			    int bytesRead = 0;
			    byte[] buffer = new byte[bis.available()];  
			    bos = new ByteArrayOutputStream();
			    while ((bytesRead = bis.read(buffer)) != -1) {
			        bos.write(buffer, 0, bytesRead);
			    }
			    response.setFileInBytes(buffer, buffer.length);
			          			    
			} 

		    //store a new History object into DB
			response.setComment("DOWNLOAD!");
			this.populateHistory(response, c);
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} catch (IOException e) {
			throw new DataAccessException(e);
        }finally{
			try {
			    if (bis!=null) bis.close();
			    if (bos!=null) bos.close();
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (Exception ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}
		return response;
    }
    
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    AttachmentTO ato = (AttachmentTO)to;
		    pstmt = c.prepareStatement("update attachment set comment=?, status=?, " +
		    		                   "type=?, visibility=?, creation_date=?, planning_id=? " +
		    						   "where id=?");
			pstmt.setString(1, ato.getComment());
			pstmt.setString(2, ato.getStatus());
			pstmt.setString(3, ato.getType());
			pstmt.setString(4, ato.getVisibility());
			pstmt.setTimestamp(5, ato.getCreationDate());
			pstmt.setString(6, ato.getPlanning().getId());
			pstmt.setString(7, ato.getId());
			pstmt.executeUpdate();
					
		    //create and insert into data base a new History object
			this.populateHistory(ato, c);
			
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

    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    AttachmentTO ato = (AttachmentTO)to;
		    ato.setId(this.getNewId());
		    ato.setCreationDate(DateUtil.getNow());

		    pstmt = c.prepareStatement("insert into attachment(id, comment, name, " +
		            	"status, type, visibility, creation_date, planning_id, content_type, binary_file) " +
						"values (?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, ato.getId());		    
			pstmt.setString(2, ato.getComment());
			pstmt.setString(3, ato.getName());
			pstmt.setString(4, ato.getStatus());
			pstmt.setString(5, ato.getType());
			pstmt.setString(6, ato.getVisibility());
			pstmt.setTimestamp(7, ato.getCreationDate());
			pstmt.setString(8, ato.getPlanning().getId());
			pstmt.setString(9, ato.getContentType());
			pstmt.setBinaryStream(10, ato.getBinaryFile(), ato.getFileSize());
			pstmt.executeUpdate();
			
		    //create and insert into data base a new History object
			this.populateHistory(ato, c);
			
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

    
    private Vector getListByPlanningId(String planningId, Connection c) throws DataAccessException {
		Vector response= new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select id, planning_id, name, status, " +
		    		"visibility, type, content_type, creation_date, comment from attachment " +
		            "where planning_id=? and status='1'");		
		    pstmt.setString(1, planningId);		    
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    AttachmentTO ato = this.populateObjectByResultSet(rs);
			    response.addElement(ato);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}
		return response;        
    }
    
    private void populateHistory(AttachmentTO ato, Connection c) throws DataAccessException{
        AttachmentHistoryDAO hdao = new AttachmentHistoryDAO();
        
        AttachmentHistoryTO hto = new AttachmentHistoryTO();
        hto.setHistory(ato.getFieldToString());
	    hto.setCreationDate(DateUtil.getNow());
	    hto.setAttachment(ato);
	    hto.setStatus(ato.getStatus());
	    hto.setUser(ato.getHandler());

        hdao.insert(hto, c);	        
    }
    
    private AttachmentTO populateObjectByResultSet(ResultSet rs) throws DataAccessException{
        AttachmentTO ato = new AttachmentTO();
        
        ato.setComment(getString(rs, "comment"));
        ato.setCreationDate(getTimestamp(rs, "creation_date"));
        ato.setContentType(getString(rs, "content_type"));
        ato.setId(getString(rs, "id"));
        ato.setName(getString(rs, "name"));       
        ato.setStatus(getString(rs, "status"));
        ato.setType(getString(rs, "type"));
        ato.setVisibility(getString(rs, "visibility"));
        
        PlanningTO pto = new PlanningTO();
        pto.setId(getString(rs, "planning_id"));
        ato.setPlanning(pto);
        
        return ato;
    }

}
