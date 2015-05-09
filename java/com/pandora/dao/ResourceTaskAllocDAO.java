package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 * This class contain all methods to handle data related with Resource Task Alloc entity into data base.
 */
public class ResourceTaskAllocDAO extends DataAccess {

    /**
     * Get a list of Resource Task Alloc objects based on Resource Task object
     */
    public Vector<ResourceTaskAllocTO> getListByResourceTask(ResourceTaskTO rtto) throws DataAccessException {
        Vector<ResourceTaskAllocTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByResourceTask(rtto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    
    /**
     * Get a list of Resource Task Alloc objects based on Resource Task object
     */
    public Vector<ResourceTaskAllocTO> getListByResourceTask(ResourceTaskTO rtto, Connection c) throws DataAccessException {
        Vector<ResourceTaskAllocTO> response = new Vector<ResourceTaskAllocTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			String sql = "select task_id, resource_id, project_id, " +
					     "sequence, alloc_time " +
					     "from resource_task_alloc " +
					     "where task_id=? and resource_id=? and project_id=?";
			pstmt = c.prepareStatement(sql);

			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getTask().getProject().getId());
			rs = pstmt.executeQuery();

			while (rs.next()){
			    ResourceTaskAllocTO rtato = this.populateBeanByResultSet(rs);
				response.addElement(rtato); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get a Resource Task Alloc object from database
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        ResourceTaskAllocTO response = new ResourceTaskAllocTO();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)to;
		    ResourceTaskTO rtto = rtato.getResourceTask();
			pstmt = c.prepareStatement("select task_id, resource_id, project_id, sequence, alloc_time " +
					     				"from resource_task_alloc " +
					     				"where task_id=? and resource_id=? and project_id=? and sequence=?");
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getResource().getProject().getId());
			pstmt.setInt(4, rtato.getSequence().intValue());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    response = this.populateBeanByResultSet(rs);
			} 

		} catch (SQLException e) {
		    throw new DataAccessException(e);
		}finally{
		    //	Close the current result set and statement
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
     * Insert a list of Resource Task Alloc objects into data base.
     */
    public void insert(Vector v, ResourceTaskTO rtto, Connection c) throws DataAccessException {
        if (v!=null){
    		Iterator i = v.iterator();
    		while (i.hasNext()){
    		    ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)i.next();
    		    rtato.setResourceTask(rtto);
    		    this.insert(rtato, c);
    		}            
        }
    }
        
    
    /**
     * Remove all allocs and insert a new alloc into database.
     */
    public void update(TransferObject to, Connection c) throws DataAccessException {
	    ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)to;
	    this.removeByResTaskAndSeq(rtato.getResourceTask(), rtato.getSequence().intValue(), c);
	    this.insert(rtato, c);		    
    }

    
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)to;
		    ResourceTaskTO rtto = rtato.getResourceTask();

		    String sql = "insert into resource_task_alloc (task_id, resource_id, " +
		    			  "project_id, sequence, alloc_time) values (?,?,?,?,?)";

		    if (rtato.getSequence().intValue()==0) {
		    	throw new DataAccessException("Error inserting data into data base. The sequence cannot be 0.");	
		    }
		    
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getTask().getProject().getId());		
			pstmt.setInt(4, rtato.getSequence().intValue());
		    pstmt.setInt(5, rtato.getAllocTime().intValue());    

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

    
    /**
     * Remove a Resource Task Alloc object from data base.
     */
    public void removeByResourceTask(ResourceTaskTO rtto, Connection c) throws DataAccessException {
        this.removeByResTaskAndSeq(rtto, 0, c);
    }   
    

    private void removeByResTaskAndSeq(ResourceTaskTO rtto, int seq, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		
		try {
		    String sql = "delete from resource_task_alloc " +
			   			 "where task_id=? and resource_id=? " +
			   			 "and project_id=? and sequence >= " + seq ;
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getTask().getProject().getId());		
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

    
    /**
     * Create a new TO object based on data into result set.
     */
    protected ResourceTaskAllocTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        ResourceTaskAllocTO rtato = new ResourceTaskAllocTO();
        
        ResourceTO rto = new ResourceTO(getString(rs, "resource_id"));
        ProjectTO pto = new ProjectTO(getString(rs, "project_id"));
        TaskTO tto = new TaskTO(getString(rs, "task_id"));
        tto.setProject(pto);
        rto.setProject(pto);
        
        ResourceTaskTO rtto = new ResourceTaskTO();
        rtto.setTask(tto);        
        rtto.setResource(rto);
        
        rtato.setAllocTime(getInteger(rs, "alloc_time"));        
        rtato.setSequence(getInteger(rs, "sequence"));
        rtato.setResourceTask(rtto);
        
        return rtato;
    }    
}
