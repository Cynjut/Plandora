package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.pandora.ProjectHistoryTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 * This class contain all methods to handle data related with Project History entity into data base.
 */
public class ProjectHistoryDAO extends DataAccess {

    /**
     * Insert a new Project History object into data base.
     * @param to
     * @param c
     * @throws DataAccessException 
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    ProjectHistoryTO phto = (ProjectHistoryTO)to;

			pstmt = c.prepareStatement("INSERT INTO project_history (project_id, " +
									   "project_status_id, creation_date) " +
									   "values (?,?,?)");
			pstmt.setString(1, phto.getProjectId());
			pstmt.setString(2, phto.getStatus().getId());
			pstmt.setTimestamp(3, DateUtil.getNow());
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
}
