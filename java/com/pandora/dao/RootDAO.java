package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pandora.LeaderTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;

/**
 * This class contain all methods to handle data related with Root entity into data base.
 */
public class RootDAO extends LeaderDAO {
    
    /**
     * Get a specific Root TO from data base, based on id.
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
	    RootTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {

		    UserTO filter = (UserTO)to;
			pstmt = c.prepareStatement("select ro.ID, ro.PROJECT_ID, u.USERNAME, u.COLOR, u.EMAIL, u.NAME, " +
			        				   "u.PHONE, u.PASSWORD, u.DEPARTMENT_ID, u.AREA_ID, u.FUNCTION_ID, " +
			        				   "u.COUNTRY, u.LANGUAGE, u.birth, u.company_id, u.auth_mode, u.permission, u.pic_file, " +
			        				   "u.final_date, u.creation_date, c.pre_approve_req, r.can_see_customer, " +
			        				   "c.is_disable, c.is_req_acceptable, c.can_see_tech_comment, " +
			        				   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs, " +
			        				   "r.can_self_alloc, r.can_see_repository, r.can_see_invoice " +
			        				   "from root ro, leader e, tool_user u, resource r, customer c " +
			        				   "WHERE ro.ID = u.ID AND e.ID = u.ID AND r.ID = u.ID " +
			        				   "AND ro.project_id = e.project_id AND e.project_id = r.project_id " +
			        				   "AND r.project_id = c.project_id " +
									   "AND ro.ID = ?");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateRootByResultSet(rs, c);
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
    protected RootTO populateRootByResultSet(ResultSet rs, Connection c) throws DataAccessException{
        RootTO response = null;
        
        //call parent class (LeaderDAO) and get all generic attributes
        LeaderTO parent = super.populateLeaderByResultSet(rs, c);
        response = new RootTO(parent);
        
        //get attributes specifics of Root.
        //....

        return response;
    }    
}
