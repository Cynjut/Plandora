package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.DepartmentTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 * This class contain the methods to access information about 
 * Department entity into data base.
 */
public class DepartmentDAO extends DataAccess {
    
    /**
     * Get a list of all Department TOs from data base.
     * @param c
     * @throws DataAccessException
     */
    public Vector getList(Connection c) throws DataAccessException {
		Vector response= new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    //get a list of objects from data base
			pstmt = c.prepareStatement("select id, name, description from department order by name");
			rs = pstmt.executeQuery();
						
			//Prepare data to return
			while (rs.next()){
			    DepartmentTO dto = this.populateBeanByResultSet(rs);
			    response.addElement(dto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			//Close the current result set and statement
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
		return response;
    }
    
    
    /**
     * Return a DepartmentTO filled with data from ResultSet. 
     * @return
     * @throws DataAccessException
     */
    private DepartmentTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        DepartmentTO response = new DepartmentTO();
        
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setDescription(getString(rs, "description"));
        
        return response;
    }
    
}
