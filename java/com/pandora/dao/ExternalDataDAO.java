package com.pandora.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.AttachmentTO;
import com.pandora.ExternalDataTO;
import com.pandora.PlanningTO;
import com.pandora.RequirementTO;
import com.pandora.TransferObject;
import com.pandora.bus.AttachmentBUS;
import com.pandora.exception.DataAccessException;

public class ExternalDataDAO extends DataAccess {
	

	public ExternalDataTO getExternalData(String externalId) throws DataAccessException {
		ExternalDataTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getExternalData(externalId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
		return response;
	}
	
	
	private ExternalDataTO getExternalData(String externalId, Connection c) throws DataAccessException {
		ExternalDataTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select id, external_id, external_sys, planning_id, external_host, external_accont, " +
		    						       "msg_date, event_date, destination, source, summary, body, priority, due_date " +
		    						   "from external_data where external_id=?");
			pstmt.setString(1, externalId);	
			rs = pstmt.executeQuery();
			if (rs.next()) {
				response = this.populateObjectByResultSet(rs, c);
				PlanningTO pto = null;
				if (response.getPlanningId()!=null) {
					pto = new PlanningTO(response.getPlanningId());
				}
				Vector<AttachmentTO> atts = this.getAttachmentsByObject(externalId, pto, c);
				response.setAttachments(atts);
			}
	
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}


	private Vector<AttachmentTO> getAttachmentsByObject(String externalId, PlanningTO pnto, Connection c) throws DataAccessException {
		Vector<AttachmentTO> response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("select external_data_id, attachment_id, content_type, creation_date, file_size, file_name, binary_file " +
		    						   "from external_data_attach where external_data_id=?");
			pstmt.setString(1, externalId);	
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (response==null) {
					response = new Vector<AttachmentTO>();
				}
				response.add(this.populateAttachByResultSet(rs, pnto, c));
			}
	
		} catch (Exception e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}


	@Override
	public void insert(TransferObject to, Connection c)	throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			ExternalDataTO edto = (ExternalDataTO)to;
			String newId = super.getNewId();
			
		    pstmt = c.prepareStatement("insert into external_data (id, external_id, external_sys, planning_id, " +
		    						   "msg_date, event_date, destination, source, summary, body, priority, due_date, " +
		    						   "external_accont, external_host) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, newId);
			pstmt.setString(2, edto.getExternalId());
			pstmt.setString(3, edto.getExternalSys());
			pstmt.setString(4, edto.getPlanningId());
			pstmt.setTimestamp(5, edto.getMessageDate());
			pstmt.setTimestamp(6, edto.getEventDate());
			pstmt.setString(7, edto.getDestination());
			pstmt.setString(8, edto.getSource());
			pstmt.setString(9, edto.getSummary());
			pstmt.setString(10, edto.getBody());
			pstmt.setInt(11, edto.getPriority());
			pstmt.setTimestamp(12, edto.getDueDate());
			pstmt.setString(13, edto.getExternalAccount());
			pstmt.setString(14, edto.getExternalHost());
			pstmt.executeUpdate();
	
			if (edto.getAttachments()!=null) {
				for (AttachmentTO atto : edto.getAttachments()) {
					this.insertAttachment(newId, atto, c);
				}
			}
			
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
		}
	}


	@Override
	public void update(TransferObject to, Connection c)	throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			ExternalDataTO edto = (ExternalDataTO)to;			
		    pstmt = c.prepareStatement("update external_data set external_sys=?, planning_id=?, " +
		    						   "msg_date=?, event_date=?, destination=?, source=?, summary=?, body=?, priority=?, due_date=?, " +
		    						   "external_accont=?, external_host=? where external_id=?");
			pstmt.setString(1, edto.getExternalSys());
			pstmt.setString(2, edto.getPlanningId());
			pstmt.setTimestamp(3, edto.getMessageDate());
			pstmt.setTimestamp(4, edto.getEventDate());
			pstmt.setString(5, edto.getDestination());
			pstmt.setString(6, edto.getSource());
			pstmt.setString(7, edto.getSummary());
			pstmt.setString(8, edto.getBody());
			pstmt.setInt(9, edto.getPriority());
			pstmt.setTimestamp(10, edto.getDueDate());
			pstmt.setString(11, edto.getExternalAccount());
			pstmt.setString(12, edto.getExternalHost());
			pstmt.setString(13, edto.getExternalId());		
			pstmt.executeUpdate();

		    pstmt = c.prepareStatement("delete from external_data_attach where external_data_id=?");
		    pstmt.setString(1, edto.getId());
		    pstmt.executeUpdate();
			
			if (edto.getAttachments()!=null) {
				for (AttachmentTO atto : edto.getAttachments()) {
					this.insertAttachment(edto.getId(), atto, c);
				}
			}
			
			
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
		}
	}


	public void createRequirement(ExternalDataTO edto, RequirementTO rto) throws DataAccessException {
		RequirementDAO rdao = new RequirementDAO();
        Connection c = null;
        PreparedStatement pstmt = null;
        
		try {
			c = getConnection(false);
			this.insert(edto, c);
			rdao.insert(rto, c);

			if (edto.getAttachments()!=null) {
				for (AttachmentTO ato : edto.getAttachments()) {
					this.insertAttachment(rto, c, ato);		
				}
			}
			
		    pstmt = c.prepareStatement("update external_data set planning_id=? where external_id=?");
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, edto.getExternalId());		
			pstmt.executeUpdate();
			
			c.commit();
			
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
			this.closeConnection(c);
		}
	}
	
	
	public void updateRequirement(ExternalDataTO edto, RequirementTO rto) throws DataAccessException {
		RequirementDAO rdao = new RequirementDAO();
		AttachmentDAO adao = new AttachmentDAO();
        Connection c = null;
        PreparedStatement pstmt = null;
        
		try {
			c = getConnection(false);
			this.update(edto, c);
			rdao.update(rto, c);
			
			if (edto.getAttachments()!=null) {
				Vector<AttachmentTO> list = adao.getListByPlanningId(rto.getId(), rto.getProject().getId());
				for (AttachmentTO ato : edto.getAttachments()) {
					String currentId = this.getIdFromList(list, ato.getName());
					if (currentId==null) {
						this.insertAttachment(rto, c, ato);	
					} else {
						adao.updateBinaryFileFromExternalDataAttach(currentId, ato.getId(), c);
					}
				}
			}
			
			c.commit();
			
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
			this.closeConnection(c);
		}
	}


	private void insertAttachment(RequirementTO rto, Connection c, AttachmentTO ato) throws DataAccessException {
		AttachmentDAO adao = new AttachmentDAO();
		ato.setPlanning(rto);
		ato.setStatus(AttachmentTO.ATTACH_STATUS_AVAILABLE);
		ato.setType(AttachmentBUS.getTypeFromMime(ato.getContentType()));
		ato.setVisibility(AttachmentTO.VISIBILITY_PUBLIC);
		ato.setComment(ato.getComment());
		ato.setHandler(rto.getRequester());
		adao.insertFromExternalDataAttach(ato, c);
	}
	
	
	private String getIdFromList(Vector<AttachmentTO> list, String fileName){
		String attachmentId = null;
		for (AttachmentTO ato : list) {
			if (ato.getName()!=null && ato.getName().equals(fileName)) {
				attachmentId = ato.getId();
				break;
			}
		}
		return attachmentId;
	}

	
	
	private void insertAttachment(String externalDataId, AttachmentTO atto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    pstmt = c.prepareStatement("insert into external_data_attach (external_data_id, attachment_id, " +
		    		"content_type, creation_date, file_size, file_name, binary_file) values (?,?,?,?,?,?,?)");
			pstmt.setString(1, externalDataId);
			pstmt.setString(2, atto.getId());
			pstmt.setString(3, atto.getContentType());
			pstmt.setTimestamp(4, atto.getCreationDate());
			pstmt.setInt(5, new Integer(atto.getFileSize()));
			pstmt.setString(6, atto.getName());
			pstmt.setBinaryStream(7, atto.getBinaryFile(), atto.getFileSize());
			pstmt.executeUpdate();
	
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
		}
	}
	
	
	private ExternalDataTO populateObjectByResultSet(ResultSet rs, Connection c) throws DataAccessException {
		ExternalDataTO response = new ExternalDataTO();
		response.setBody(getString(rs, "body"));
		response.setDestination(getString(rs, "destination"));
		response.setEventDate(getTimestamp(rs, "event_date"));
		response.setExternalId(getString(rs, "external_id"));
		response.setExternalSys(getString(rs, "external_sys"));
		response.setId(getString(rs, "id"));
		response.setMessageDate(getTimestamp(rs, "msg_date"));
		response.setPlanningId(getString(rs, "planning_id"));
		response.setSource(getString(rs, "source"));
		response.setSummary(getString(rs, "summary"));
		response.setPriority(getInteger(rs, "priority"));
		response.setDueDate(getTimestamp(rs, "due_date"));
		response.setExternalAccount(getString(rs, "external_accont"));
		response.setExternalHost(getString(rs, "external_host"));
		
		return response;
	}


	private AttachmentTO populateAttachByResultSet(ResultSet rs, PlanningTO pnto, Connection c) throws Exception {
		AttachmentTO response = new AttachmentTO();
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		
		response.setComment("");
		response.setContentType(getString(rs, "content_type"));
		response.setCreationDate(getTimestamp(rs, "creation_date"));
		response.setId(getString(rs, "attachment_id"));
		response.setName(getString(rs, "file_name"));
		response.setPlanning(pnto);
		response.setStatus(AttachmentTO.ATTACH_STATUS_AVAILABLE);
		response.setType(AttachmentBUS.getTypeFromMime(response.getContentType()));
		response.setVisibility(AttachmentTO.VISIBILITY_PUBLIC);
		
	    bis = (ByteArrayInputStream)rs.getBinaryStream("binary_file");
	    if (bis!=null) {
		    response.setBinaryFile(bis, bis.available());
		    
		    int bytesRead = 0;
		    byte[] buffer = new byte[bis.available()];  
		    bos = new ByteArrayOutputStream();
		    while ((bytesRead = bis.read(buffer)) != -1) {
		        bos.write(buffer, 0, bytesRead);
		    }
		    response.setFileInBytes(buffer, buffer.length);	    	
	    }
		
		return response;
	}

	
}
