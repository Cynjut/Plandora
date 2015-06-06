package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pandora.ArtifactTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

public class ArtifactDAO extends PlanningDAO {

	
	public void updateArtifact(String path, ArtifactTO ato) throws DataAccessException {
        Connection c = null;
    	try {
    		c = getConnection(false);
    		this.updateArtifact(path, ato, c);
    		c.commit();
    	} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
				e.printStackTrace();
			}
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
	}

	
    public ArtifactTO getObjectById(String artifactId) throws DataAccessException {
    	ArtifactTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getObjectById(artifactId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }
	
	
	private void updateArtifact(String path, ArtifactTO ato, Connection c) throws DataAccessException {
		RepositoryDAO rdao = new RepositoryDAO();
		AttachmentDAO adao = new AttachmentDAO();
		PreparedStatement pstmt = null;
		try {			
			if (path!=null) {

				//check if it is necessary to save a new repository file...
				String repFileId = rdao.saveRepositoryFile(path, c);

				ato.setRepositoryFile(new RepositoryFileTO(repFileId));
				ArtifactTO dbArt = (ArtifactTO)this.getObject(ato, c);
				if (dbArt==null){
					
				    ato.setId(this.getNewId());
				    ato.setCreationDate(DateUtil.getNow());
				    ato.setDescription(ato.getLogMessage());

				    //insert data into parent entity (PlanningDAO)
				    super.insert(ato, c);

					pstmt = c.prepareStatement("insert into artifact (id, repository_file_id, last_update, " +
											   "artifact_template_type, project_id) values (?, ?, ?, ?, ?)");
					pstmt.setString(1, ato.getId());
					pstmt.setString(2, repFileId);
					pstmt.setTimestamp(3, DateUtil.getNow());
					pstmt.setString(4, ato.getExportType());
					pstmt.setString(5, ato.getProjectId());
					pstmt.executeUpdate();
					
					rdao.insertRepositoryFileProject(new ProjectTO(ato.getProjectId()), true, repFileId, c);
					
				} else {
					dbArt.setDescription(ato.getLogMessage());
				    super.update(dbArt, c);
				    
					pstmt = c.prepareStatement("update artifact set last_update=? where id=?");
					pstmt.setTimestamp(1, DateUtil.getNow());
					pstmt.setString(2, dbArt.getId());
					pstmt.executeUpdate();
				}
				
				//get all attachments that is current linked with project
				if (ato.getProjectId()!=null && ato.getHandler()!=null && ato.getHandler().getId()!=null) {
					adao.migrateAttachment(ato.getProjectId(), ato.getId(), ato.getHandler(), c);	
				}
			}	
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        		
	}

	
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        ArtifactTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			ArtifactTO filter = (ArtifactTO)to;
		    pstmt = c.prepareStatement("select p.description, p.creation_date, p.final_date, p.iteration, a.last_update, " +
						                      "a.id, a.artifact_template_type, r.repository_file_path, a.repository_file_id, " +
						                      "a.project_id " +
						               "from artifact a, planning p, repository_file r " +
						               "where a.id = p.id and a.repository_file_id = r.id and a.repository_file_id=?");		
			pstmt.setString(1, filter.getRepositoryFile().getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateObjectByResultSet(rs, c);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


    private ArtifactTO getObjectById(String artifactId, Connection c) throws DataAccessException {
        ArtifactTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select p.description, p.creation_date, p.final_date, p.iteration, a.last_update, " +
						                      "a.id, a.artifact_template_type, r.repository_file_path, a.repository_file_id, " +
						                      "a.project_id " +
						               "from artifact a, planning p, repository_file r " +
						               "where a.id = p.id and a.repository_file_id = r.id and a.id=?");		
			pstmt.setString(1, artifactId);
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateObjectByResultSet(rs, c);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
	private ArtifactTO populateObjectByResultSet(ResultSet rs, Connection c) throws DataAccessException{
		ArtifactTO response = new ArtifactTO();
        response.setId(getString(rs, "id"));
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        response.setIteration(getString(rs, "iteration"));
        response.setPath(getString(rs, "repository_file_path"));
        response.setType(getString(rs, "artifact_template_type"));
        response.setLastUpdate(getTimestamp(rs, "last_update"));
        response.setProjectId(getString(rs, "project_id"));
        
        RepositoryFileTO rfto = new  RepositoryFileTO(getString(rs, "repository_file_id"));
        rfto.setPath(response.getPass());
        response.setRepositoryFile(rfto);
        
		return response;
	}
}
