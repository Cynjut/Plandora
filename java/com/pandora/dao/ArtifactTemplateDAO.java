package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.ArtifactTemplateTO;
import com.pandora.CategoryTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

public class ArtifactTemplateDAO extends DataAccess {

	
    public Vector<ArtifactTemplateTO> getListByProject(String projectId) throws DataAccessException {
        Vector<ArtifactTemplateTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProject(projectId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }

    	
	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
		ArtifactTemplateTO response = new ArtifactTemplateTO();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select distinct t.id, t.name, t.description, t.category_id, " +
		    						   "t.header_html, t.body_html, t.footer_html, t.profile_view, c.name as CATEGORY_NAME " +
		    					   	   "from artifact_template t, category c where t.category_id = c.id and t.id = ?");		
		    pstmt.setString(1, to.getId());		    
			rs = pstmt.executeQuery();
			while (rs.next()) {
				response = this.populateObjectByResultSet(rs);
			}						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
	}


	private Vector<ArtifactTemplateTO> getListByProject(String projectId, Connection c) throws DataAccessException {
		Vector<ArtifactTemplateTO> response= new Vector<ArtifactTemplateTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select distinct t.id, t.name, t.description, t.category_id, " +
		    		"t.header_html, t.body_html, t.footer_html, t.profile_view, c.name as CATEGORY_NAME " +
		    		"from artifact_template t, category c " +
		            "where t.category_id = c.id and (c.project_id=? or c.project_id is null)");		
		    pstmt.setString(1, projectId);		    
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    ArtifactTemplateTO ato = this.populateObjectByResultSet(rs);
			    response.addElement(ato);
			}						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;        
    }

    
	private ArtifactTemplateTO populateObjectByResultSet(ResultSet rs) throws DataAccessException {
		ArtifactTemplateTO response = new ArtifactTemplateTO();
		response.setBody(getString(rs, "body_html"));
		response.setDescription(getString(rs, "description"));
		response.setFooter(getString(rs, "footer_html"));
		response.setHeader(getString(rs, "header_html"));
		response.setId(getString(rs, "id"));
		response.setName(getString(rs, "name"));
		response.setProfileViewer(getString(rs, "profile_view"));		
		
		CategoryTO cto = new CategoryTO(getString(rs, "category_id"));
		cto.setName(getString(rs, "CATEGORY_NAME"));
		response.setCategory(cto);
		
		return response;
	}

    
    
}
