package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.DiscussionTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

public class DiscussionDAO extends PlanningDAO {

	public Vector getDiscussionList(ProjectTO pto, CategoryTO category) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getDiscussionList(pto, category, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}

	
	private Vector getDiscussionList(ProjectTO pto, CategoryTO category, Connection c) throws DataAccessException {
		Vector response= new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select d.id, p.description, p.creation_date, p.final_date, " +
		    						   "d.project_id, d.category_id, d.name, d.owner, d.is_blocked, " +
		    						   "c.name as CATEGORY_NAME, tu.username " +
		    						   "from discussion d, planning p, category c, tool_user tu " +
		            				   "where d.id= p.id and d.category_id = c.id and tu.id = d.owner " +
		            				   "and d.project_id=? and (d.category_id =? or d.category_id = '0')");		
		    pstmt.setString(1, pto.getId());
		    pstmt.setString(2, category.getId());
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    DiscussionTO rto = this.populateObjectByResultSet(rs);
			    
			    CategoryTO cto = rto.getCategory();
			    cto.setName(getString(rs, "CATEGORY_NAME"));
			    
			    UserTO uto = rto.getOwner();
			    uto.setUsername(getString(rs, "username"));
			    
			    response.addElement(rto);
			}
						
		} catch (Exception e) {
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
	
	
    private DiscussionTO populateObjectByResultSet(ResultSet rs) throws DataAccessException {
    	DiscussionTO response = new DiscussionTO();       
        response.setId(getString(rs, "id"));
        response.setCategory(new CategoryTO(getString(rs, "category_id")) );
        response.setName(getString(rs, "name"));
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        response.setIsBlocked(getBoolean(rs, "is_blocked"));
        response.setOwner(new UserTO(getString(rs, "owner")));
        return response;
    }
	
}
