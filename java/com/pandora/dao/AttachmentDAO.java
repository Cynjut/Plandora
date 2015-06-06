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
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 */
public class AttachmentDAO extends DataAccess {


    public Vector<AttachmentTO> getListByPlanningId(String planningId, String projectId) throws DataAccessException {
        Vector<AttachmentTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByPlanningId(planningId, projectId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }

	public Vector<AttachmentTO> getListByProject(ProjectTO projectTO) throws DataAccessException {
        Vector<AttachmentTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProject(projectTO, c);
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
			super.closeStatement(rs, pstmt);
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
		    if (response!=null) {
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
				if (response.getHandler()!=null && response.getHandler().getId()!=null) {
					response.setComment("DOWNLOAD!");
					this.populateHistory(response, c);					
				}
		    }
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} catch (IOException e) {
			throw new DataAccessException(e);
        }finally{
			try {
			    if (bis!=null) bis.close();
			    if (bos!=null) bos.close();
			    super.closeStatement(rs, pstmt);
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
			super.closeStatement(null, pstmt);
		}        
    }
	

	public void updateBinaryFileFromExternalDataAttach(String attachId, String externalDataAttachId, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		ByteArrayInputStream bis = null;		
		try {
		    pstmt = c.prepareStatement("select binary_file from external_data_attach where attachment_id=?");		
			pstmt.setString(1, externalDataAttachId);
			rs = pstmt.executeQuery();						
			if (rs.next()){
			    bis = (ByteArrayInputStream)rs.getBinaryStream("binary_file");
			    
			    pstmt2 = c.prepareStatement("update attachment set binary_file=? where id=?");
			    pstmt2.setBinaryStream(1, bis , bis.available());
			    pstmt2.setString(2, attachId);
			    pstmt2.executeUpdate();
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			try {
			    if (bis!=null) bis.close();
				super.closeStatement(rs, pstmt);
				super.closeStatement(null, pstmt2);
			} catch (Exception ec) {
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
			super.closeStatement(null, pstmt);
		}               
    }

    
    public void insertFromExternalDataAttach(AttachmentTO ato, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		ByteArrayInputStream bis = null;		
		
		try {		    
			String externalDataAttachId = ato.getId();

		    pstmt = c.prepareStatement("select binary_file from external_data_attach where attachment_id=?");		
			pstmt.setString(1, externalDataAttachId);
			rs = pstmt.executeQuery();						
			if (rs.next()){
			    bis = (ByteArrayInputStream)rs.getBinaryStream("binary_file");

			    String longName = ato.getName();
			    if (longName!=null && longName.length()>50) {
			    	longName = ato.getName().substring(longName.length() - 50);
			    }
			    ato.setId(this.getNewId());
			    pstmt2 = c.prepareStatement("insert into attachment(id, comment, name, " +
			            	"status, type, visibility, creation_date, planning_id, content_type, binary_file) " +
							"values (?,?,?,?,?,?,?,?,?,?)");
				pstmt2.setString(1, ato.getId());
				pstmt2.setString(2, ato.getComment());
				pstmt2.setString(3, longName);
				pstmt2.setString(4, ato.getStatus());
				pstmt2.setString(5, ato.getType());
				pstmt2.setString(6, ato.getVisibility());
				pstmt2.setTimestamp(7, DateUtil.getNow());
				pstmt2.setString(8, ato.getPlanning().getId());
				pstmt2.setString(9, ato.getContentType());
				pstmt2.setBinaryStream(10, bis, bis.available());
				pstmt2.executeUpdate();
				
			    //create and insert into data base a new History object
				this.populateHistory(ato, c);				
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			try {
			    if (bis!=null) bis.close();
				super.closeStatement(rs, pstmt);
				super.closeStatement(null, pstmt2);
			} catch (Exception ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}               
    }
    
    
	public void migrateAttachment(String fromProjectId, String toPlanningId, UserTO handler, Connection c) throws SQLException {
		PreparedStatement pstmt = null;
	    pstmt = c.prepareStatement("update attachment set planning_id=? " +
	    						   "where id in (select distinct attachment_id " +
	    						                  "from attachment_history h, ( " +
	    						                     "select a.id, max(h.creation_date) as last_date " +
	    						                       "from attachment a, attachment_history h " +
	    						                      "where h.attachment_id = a.id and a.planning_id=? " +
	    						                      "group by a.id) as sub " +
	    						                  "where h.attachment_id = sub.id and h.creation_date = sub.last_date " +
	    						                    "and h.user_id = ?)");
		pstmt.setString(1, toPlanningId);
		pstmt.setString(2, fromProjectId);
		pstmt.setString(3, handler.getId());
		pstmt.executeUpdate();		
	}

    
    private Vector<AttachmentTO> getListByPlanningId(String planningId, String projectId, Connection c) throws DataAccessException {
		Vector<AttachmentTO> response= new Vector<AttachmentTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select id, planning_id, name, status, " +
		    		"visibility, type, content_type, creation_date, comment from attachment " +
            		"where (status=? or status=?) and (planning_id=? " + (projectId!=null?"or planning_id=?":"") + ")");

		    pstmt.setString(1, AttachmentTO.ATTACH_STATUS_AVAILABLE);
		    pstmt.setString(2, AttachmentTO.ATTACH_STATUS_RECOVERED);
		    pstmt.setString(3, planningId);
		    if (projectId!=null) {
		    	pstmt.setString(4, projectId);	
		    }
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    AttachmentTO ato = this.populateObjectByResultSet(rs);
			    response.addElement(ato);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;        
    }
    

    private Vector<AttachmentTO> getListByProject(ProjectTO pto, Connection c) throws DataAccessException {
		Vector<AttachmentTO> response= new Vector<AttachmentTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select id, planning_id, name, status, visibility, type, content_type, creation_date, comment " +
		    						   "from attachment " +
		    						   "where (status=? or status=?) " +
		    						   "and (planning_id in (select id from requirement where project_id = ?) or " +
		            				        "planning_id in (select id from artifact where project_id = ?) or " +
		            				        "planning_id=?)");
		    pstmt.setString(1, AttachmentTO.ATTACH_STATUS_AVAILABLE);
		    pstmt.setString(2, AttachmentTO.ATTACH_STATUS_RECOVERED);
		    pstmt.setString(3, pto.getId());
		    pstmt.setString(4, pto.getId());
		    pstmt.setString(5, pto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    AttachmentTO ato = this.populateObjectByResultSet(rs);
			    response.addElement(ato);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
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

    public Vector<AttachmentTO> getListUntilID(String initialId, String finalId) throws DataAccessException { 
        Vector<AttachmentTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListUntilID(initialId, finalId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }     

    
    private Vector<AttachmentTO> getListUntilID(String initialId, String finalId, Connection c) throws DataAccessException{
        Vector<AttachmentTO> response = new Vector<AttachmentTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    String sql = "select id, planning_id, name, status, visibility, type, content_type, creation_date, comment " +
		    			 "from attachment where id > " + initialId + " and id <= " + finalId;
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()){
				AttachmentTO rto = this.populateObjectByResultSet(rs);
			    response.addElement(rto);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
	public long getMaxId() throws DataAccessException {
		long response= -1;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getMaxId(c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
		return response;
	}

	private long getMaxId(Connection c) throws DataAccessException {
        long response= -1;
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
			String sql = "select id as maxid from attachment " +
					     "where creation_date = (select max(creation_date) from attachment order by 1 desc)";
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()){
			    String maxId = getString(rs, "maxid");
			    if (maxId!=null) {
			        response = new Long(maxId).longValue();    
			    }
			} 						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}
    
}
