package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import com.pandora.PreferenceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 * This class contain the methods to access information about 
 * Preference entity into data base.
 */
public class PreferenceDAO extends DataAccess {

    /**
     * Get from data base all preferences occurrences based on user object.
     */
    public PreferenceTO getObjectByUser(UserTO uto) throws DataAccessException{
        PreferenceTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getObjectByUser(uto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    /**
     * Get from data base all preferences occurrences based on user object.
     */
    public PreferenceTO getObjectByUser(UserTO uto, Connection c) throws DataAccessException {
        PreferenceTO response= new PreferenceTO();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, value from preference where user_id=?");
			pstmt.setString(1, uto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    PreferenceTO to = new PreferenceTO();
			    to.setId(getString(rs, "id"));
			    to.setValue(getString(rs, "value"));
			    to.setUser(uto);
			    
			    response.addPreferences(to);
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
     * Get from data base a preference object based on user object and id.
     * @param uto
     * @param c
     * @return
     * @throws DataAccessException
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        PreferenceTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {

		    PreferenceTO pto = (PreferenceTO) to;
			pstmt = c.prepareStatement("select * from preference where id=? and user_id=?");
			pstmt.setString(1, pto.getId());
			pstmt.setString(2, pto.getUser().getId());
			rs = pstmt.executeQuery();
			if (rs.next()) {
			    response = new PreferenceTO();
			    response.setId(getString(rs, "id"));
			    response.setValue(getString(rs, "value"));
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
     * For each preference of hash list, verify if need to insert or update data. 
     * @param pto
     * @throws DataAccessException
     */
    public void insertOrUpdate(PreferenceTO pto) throws DataAccessException {
        Connection c = null;
		try {
		    if (pto.getPreferences()!=null){
				c = getConnection(false);
				Iterator<PreferenceTO> i = pto.getPreferences().values().iterator();
				while(i.hasNext()){
				    PreferenceTO ppto = i.next();
			    	if (ppto.getValue()!=null) {
					    if (this.getObject(ppto, c)!=null){
					        this.update(ppto, c);
					    } else {
				    		this.insert(ppto, c);	
					    }				        
			    	}				    
				}
				c.commit();		        
		    }
		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			throw new DataAccessException(e);
			
		} finally{
			this.closeConnection(c);
		}		
    }

    
    /**
     * Insert a new Preference object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {

		    PreferenceTO pto = (PreferenceTO)to;
			pstmt = c.prepareStatement("insert into preference (id, value, user_id) values (?,?,?)");
			pstmt.setString(1, pto.getId());
			pstmt.setString(2, pto.getValue());
			pstmt.setString(3, pto.getUser().getId());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }

    
    /**
     * Update a Preference object into data base.
     */
    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {

		    PreferenceTO pto = (PreferenceTO)to;
			pstmt = c.prepareStatement("update preference set value=? where Id=? and user_id=?");
			pstmt.setString(1, pto.getValue());
			pstmt.setString(2, pto.getId());
			pstmt.setString(3, pto.getUser().getId());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			closeStatement(null, pstmt);
		}               
    }
    
}
