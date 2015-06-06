package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.LeaderTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;

/**
 * This class contain all methods to handle data related with Leader entity into data base.
 */
public class LeaderDAO extends ResourceDAO {

    /**
     * Insert a new leader into data base related with project id.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    LeaderTO eto = (LeaderTO)to;
			pstmt = c.prepareStatement("insert into leader (id, project_id) values (?,?)");
			pstmt.setString(1, eto.getId());
			pstmt.setString(2, eto.getProject().getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    /**
     * Remove a leader from data base related with project id.
     */
    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    LeaderTO eto = (LeaderTO)to;
			pstmt = c.prepareStatement("delete from leader where id=? and project_id=?");
			pstmt.setString(1, eto.getId());
			pstmt.setString(2, eto.getProject().getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException{
		ResourceTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    LeaderTO filter = (LeaderTO)to;
			pstmt = c.prepareStatement("select e.id, e.project_id, u.username, u.color, u.email, u.name, " +
					 				   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, " +
					 				   "u.birth, u.company_id, u.auth_mode, u.permission, u.pic_file, u.final_date, u.creation_date, " +
										   "c.pre_approve_req, r.can_see_customer, r.can_self_alloc, r.can_see_repository, r.can_see_invoice, " +
										   "c.is_disable, c.is_req_acceptable, c.can_see_tech_comment, " +
										   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
										   "from tool_user u, customer c, resource r, leader e " +
										   "where c.id = u.id and r.id = u.id and e.id = u.id " +
										   	 "and e.project_id = r.project_id and r.project_id = c.project_id " +
											 "and e.id = ? and e.project_id=?");
			pstmt.setString(1, filter.getId());
			pstmt.setString(2, filter.getProject().getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateLeaderByResultSet(rs, c);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
		    super.closeStatement(rs, pstmt);
		}	 
		return response;
	}
	
    
    /**
     * Get a specific Leader TO from data base, based on id.
     */
    public LeaderTO getFirstLeaderByUserId(UserTO filter) throws DataAccessException{
        LeaderTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getFirstObjectByUserId(filter, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    /**
     * Get a specific Leader TO from data base, based on id.
     */
    private LeaderTO getFirstObjectByUserId(UserTO filter, Connection c) throws DataAccessException {
        LeaderTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select e.id, e.project_id, u.username, u.color, u.email, u.name, " +
			        				   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, u.birth, u.company_id, " +
			        				   "u.auth_mode, u.permission, u.pic_file, u.final_date, u.creation_date, " +
									   "c.pre_approve_req, r.can_see_customer, r.can_self_alloc, r.can_see_repository, r.can_see_invoice, " +
									   "c.is_disable, c.is_req_acceptable, c.can_see_tech_comment, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
									   "from tool_user u, customer c, resource r, leader e " +
									   "where c.id = u.id and r.id = u.id and e.id = u.id " +
									   	 "and e.project_id = r.project_id and r.project_id = c.project_id " +
										 "and e.id = ?");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateLeaderByResultSet(rs, c);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get a list of leader objects based on project id.
     */
    public Vector<LeaderTO> getLeaderListByProjectId(String projectIdList) throws DataAccessException{
        Vector<LeaderTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getLeaderListByProjectId(projectIdList, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    
    /**
     * Get a list of leader objects based on project id.
     */
    private Vector<LeaderTO> getLeaderListByProjectId(String projectIdList, Connection c) throws DataAccessException{
        Vector<LeaderTO> response= new Vector<LeaderTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select distinct e.id, e.project_id, u.username, u.color, u.email, u.name, " +
			        				   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, " +
			        				   "u.birth, u.company_id, u.auth_mode, u.permission, u.pic_file, u.final_date, u.creation_date, " +
									   "c.pre_approve_req, r.can_see_customer, r.can_self_alloc, r.can_see_repository, r.can_see_invoice, " +
									   "c.is_disable, c.is_req_acceptable, c.can_see_tech_comment, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs  " +
									   "from leader e, tool_user u, resource r, customer c " +
									   "where e.id = u.id and r.id = u.id and c.id = u.id and u.username <> '" + RootTO.ROOT_USER + "' " + 
									   	 "and e.project_id = r.project_id and r.project_id = c.project_id " +									     
										 "and e.project_id in (" + projectIdList + ")");			
			rs = pstmt.executeQuery();						
			while (rs.next()){
				response.addElement(this.populateLeaderByResultSet(rs, c));
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;        
    }

    
    /**
     * Create a new TO object based on data into result set.
     */
    protected LeaderTO populateLeaderByResultSet(ResultSet rs, Connection c) throws DataAccessException{
        LeaderTO response = null;
        
        //call parent class (ResourceDAO) and get all generic attributes
        ResourceTO parent = super.populateResourceByResultSet(rs, c);
        response = new LeaderTO(parent);
        
        //get attributes specifics of Leader.
        //....

        return response;
    }
    
   
}
