package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RepositoryFileProjectTO;
import com.pandora.RepositoryFileTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

public class RepositoryDAO extends DataAccess {

	public RepositoryFileProjectTO getFileFromDB(ProjectTO pto, String path) throws DataAccessException {
		RepositoryFileProjectTO response = null;
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getObjectFromProject(pto, path, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
	}
	
	
	public Vector<RepositoryFilePlanningTO> getFilesFromEntity(PlanningTO pto) throws DataAccessException {
		Vector<RepositoryFilePlanningTO> response = new Vector<RepositoryFilePlanningTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getListFromEntity(pto, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
	}	
	

	public Vector<RepositoryFilePlanningTO> getEntitiesFromFile(RepositoryFileTO rfto) throws DataAccessException {
		Vector<RepositoryFilePlanningTO> response = new Vector<RepositoryFilePlanningTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getEntitiesFromFile(rfto, null, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
	}
	
	public void updateDisabledStatus(ProjectTO pto, String path, boolean disabled, String artifactTemplateType) throws DataAccessException {
        Connection c = null;
    	try {
    		c = getConnection(false);
    		this.updateDisabledStatus(pto, path, disabled, artifactTemplateType, c);
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


	public void updateRepositoryFilePlan(String path, String entityId, ProjectTO pto, String artifactTemplateType, boolean removeIfAlreadyExists) throws DataAccessException {
        Connection c = null;
    	try {
    		c = getConnection(false);
    		this.updateRepositoryFilePlan(path, entityId, pto, artifactTemplateType, removeIfAlreadyExists, c);
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


	public void breakRepositoryEntityLink(String pathId, String entityId) throws DataAccessException {
        Connection c = null;
    	try {
    		c = getConnection(false);
    		this.breakRepositoryEntityLink(pathId, entityId, c);
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
	
	
	
	public Vector<RepositoryFileTO> getFilesToCustomerView(ProjectTO pto) throws DataAccessException {
		Vector<RepositoryFileTO> response = new Vector<RepositoryFileTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getFilesToCustomerView(pto, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
	}

	
    private Vector<RepositoryFileTO> getFilesToCustomerView(ProjectTO pto, Connection c) throws DataAccessException {
		Vector<RepositoryFileTO> response = new Vector<RepositoryFileTO>();
    	ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
		    pstmt = c.prepareStatement("select rfp.repository_file_id, rfp.project_id, " +
		    						          "rfp.is_disable, rfp.last_update, rf.repository_file_path, rf.artifact_template_type " +
		    						   "from repository_file_project rfp, repository_file rf " +
		    						   "where rfp.repository_file_id = rf.id " +
		    						     "and rfp.project_id=? and rfp.is_disable=0 " +
		    						   "order by rf.repository_file_path");		
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
				RepositoryFileProjectTO rfpto = this.populateObjectByResultSet(rs); 
				response.addElement(rfpto.getFile());
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}


	private void updateRepositoryFilePlan(String path, String entityId, ProjectTO pto, 
			String artifactTemplateType, boolean removeIfAlreadyExists, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			
			if (path!=null && entityId!=null) {
				RepositoryFileTO rfto = new RepositoryFileTO();
				rfto.setPath(path);
				Vector<RepositoryFilePlanningTO> list = this.getEntitiesFromFile(rfto, entityId, c);
				if (list==null || list.size()==0) {

					//check if it is necessary to save a new repository file...
					String repFileId = saveRepositoryFile(path, artifactTemplateType, c);
										
					pstmt = c.prepareStatement("insert into repository_file_plan (repository_file_id, planning_id) values (?, ?)");
					pstmt.setString(1, repFileId);
					pstmt.setString(2, entityId);
					pstmt.executeUpdate();

					if (pto!=null) {
						pstmt = c.prepareStatement("insert into repository_file_project (repository_file_id, project_id, is_disable, last_update) values (?, ?, ?, ?)");
						pstmt.setString(1, repFileId);
						pstmt.setString(2, pto.getId());
						pstmt.setInt(3, 0); //default value
						pstmt.setTimestamp(4, DateUtil.getNow());
						pstmt.executeUpdate();						
					}
					
				} else if (list.size()==1){
					if (removeIfAlreadyExists) {
						RepositoryFilePlanningTO rf = (RepositoryFilePlanningTO)list.elementAt(0);
						this.breakRepositoryEntityLink(rf.getFile().getId(), entityId, c);						
					}

				} else {
					throw new DataAccessException("Error updating status of repository. [" + path + "] [" + entityId + "]");
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        		
	}


	private String saveRepositoryFile(String path, String artifactTemplateType, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String repFileId = null;
		try {
								
		    pstmt = c.prepareStatement("select id from repository_file where  repository_file_path=?");		
			pstmt.setString(1, path);
			rs = pstmt.executeQuery();						
			if (!rs.next()){
			
				//insert the repository file into data base
				repFileId =  super.getNewId();
				pstmt = c.prepareStatement("insert into repository_file (id, repository_file_path, artifact_template_type) values (?, ?, ?)");
				pstmt.setString(1, repFileId);
				pstmt.setString(2, path);
				pstmt.setString(3, artifactTemplateType);
				pstmt.executeUpdate();
				
			} else {
				repFileId = getString(rs, "id");
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}        		
		
		return repFileId;
	}
	
	
	private void breakRepositoryEntityLink(String pathId, String entityId, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			if (pathId!=null && entityId!=null) {
				pstmt = c.prepareStatement("delete from repository_file_plan where repository_file_id=? and planning_id=?");
				pstmt.setString(1, pathId);
				pstmt.setString(2, entityId);
				pstmt.executeUpdate();					
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        		
	}
	
	
	private void updateDisabledStatus(ProjectTO pto, String path, boolean disabled, 
			String artifactTemplateType, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {

			//check if the repository file is already into data base...
			RepositoryFileProjectTO rfpto = getObjectFromProject(pto, path, c);
			String repFileId = null;
			if (rfpto==null) {

				//check if it is necessary to save a new repository file...
				repFileId = saveRepositoryFile(path, artifactTemplateType, c);
				
				//insert a new repository file project record
				pstmt = c.prepareStatement("insert into repository_file_project (repository_file_id, project_id, is_disable, last_update)" +
											"values (?, ?, ?, ?)");
				pstmt.setString(1, repFileId);
				pstmt.setString(2, pto.getId());
				pstmt.setInt(3, (disabled?1:0));
				pstmt.setTimestamp(4, DateUtil.getNow());
				pstmt.executeUpdate();				
				
			} else {
				repFileId = rfpto.getId();
				
				//update the status of repository file project record
				pstmt = c.prepareStatement("update repository_file_project set is_disable=?, last_update=? " +
										    "where project_id=? and repository_file_id=?");
				pstmt.setInt(1, (disabled?1:0));
				pstmt.setTimestamp(2, DateUtil.getNow());
				pstmt.setString(3, pto.getId());
				pstmt.setString(4, repFileId);
				pstmt.executeUpdate();				
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
		
	}


	public RepositoryFileProjectTO getObjectFromProject(ProjectTO pto, String path, Connection c) throws DataAccessException {
    	RepositoryFileProjectTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 

		try {
		    pstmt = c.prepareStatement("select rfp.repository_file_id, rfp.project_id, " +
		    						          "rfp.is_disable, rfp.last_update, rf.repository_file_path, rf.artifact_template_type " +
		    						   "from repository_file_project rfp, repository_file rf " +
		    						   "where rfp.repository_file_id = rf.id " +
		    						     "and rfp.project_id=? and rf.repository_file_path = ?");		
			pstmt.setString(1, pto.getId());
			pstmt.setString(2, path);
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
	
	
	private Vector<RepositoryFilePlanningTO> getListFromEntity(PlanningTO pto, Connection c) throws DataAccessException {
		Vector<RepositoryFilePlanningTO> response = new Vector<RepositoryFilePlanningTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
		    pstmt = c.prepareStatement("select rfp.repository_file_id, rfp.planning_id, rf.repository_file_path, rf.artifact_template_type " +
		    						   "from repository_file_plan rfp, repository_file rf, planning p " +
		    						   "where rfp.repository_file_id = rf.id " +
		    						     "and rfp.planning_id = p.id and p.id=?");		
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
				response.addElement(this.populateEntityByResultSet(rs));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

	
	private Vector<RepositoryFilePlanningTO> getEntitiesFromFile(RepositoryFileTO rfto, String entityId, Connection c) throws DataAccessException {
		Vector<RepositoryFilePlanningTO> response = new Vector<RepositoryFilePlanningTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
			String entityWhere = "";
			if (entityId!=null){
				entityWhere = " and p.id = '" + entityId + "'";
			}
			
		    pstmt = c.prepareStatement("select rfp.repository_file_id, rfp.planning_id, rf.repository_file_path, rf.artifact_template_type " +
		    						   "from repository_file_plan rfp, repository_file rf, planning p " +
		    						   "where rfp.repository_file_id = rf.id " +
		    						     "and rfp.planning_id = p.id and rf.repository_file_path=?" + entityWhere);		
			pstmt.setString(1, rfto.getPath());
			rs = pstmt.executeQuery();						
			while (rs.next()){
				response.addElement(this.populateEntityByResultSet(rs));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

	
	private RepositoryFilePlanningTO populateEntityByResultSet(ResultSet rs) throws DataAccessException {
		RepositoryFilePlanningTO response = new RepositoryFilePlanningTO();

		response.setId(getString(rs, "repository_file_id"));
		
		PlanningTO pto = new PlanningTO();
		pto.setId(getString(rs, "planning_id"));
		response.setEntity(pto);		
		response.setFile(this.populateRepositoryFile(rs, response.getId(), pto));

		return response;
	}

		
	private RepositoryFileProjectTO populateObjectByResultSet(ResultSet rs) throws DataAccessException {
		RepositoryFileProjectTO response = new RepositoryFileProjectTO();
		
		response.setProject(new ProjectTO(getString(rs, "project_id")));
		response.setLastUpdate(getTimestamp(rs, "last_update"));
		response.setId(getString(rs, "repository_file_id"));
		Integer i = getInteger(rs, "is_disable");
		if (i!=null) {
			response.setIsDisabled(new Boolean(i.intValue()==1));	
		} else {
			response.setIsDisabled(new Boolean(false));
		}
		
		response.setFile(this.populateRepositoryFile(rs, response.getId(), response.getProject()));
	
		return response;
	}


	private RepositoryFileTO populateRepositoryFile(ResultSet rs, String id, PlanningTO planning) throws DataAccessException {
		RepositoryFileTO rf = new RepositoryFileTO();
		if (id==null) {
			rf.setId(getString(rs, "id"));	
		} else {
			rf.setId(id);	
		}
		rf.setPath(getString(rs, "repository_file_path"));
		rf.setArtifactTemplateType(getString(rs, "artifact_template_type"));
		rf.setPlanning(planning);
		return rf;
	}

}
