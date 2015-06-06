package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

/**
 * This class contain all methods to handle data related with Resource Task Alloc (planned) entity into data base.
 */
public class ResourceTaskAllocPlannedDAO extends ResourceTaskAllocDAO {


    public void updateByTaskAlloc(ResourceTaskAllocTO rtato, Integer newState, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;		
		try {			
		    ResourceTaskTO rtto = rtato.getResourceTask();

		    if (isOpen(rtto, newState)) {
			    String sql = "insert into resource_task_alloc_plann (task_id, resource_id, " +
		    			  "project_id, sequence, alloc_time) values (?,?,?,?,?)";
				pstmt = c.prepareStatement(sql);
				pstmt.setString(1, rtto.getTask().getId());
				pstmt.setString(2, rtto.getResource().getId());
				pstmt.setString(3, rtto.getTask().getProject().getId());		
				pstmt.setInt(4, rtato.getSequence().intValue());
			    pstmt.setInt(5, rtato.getAllocTime().intValue());    
				pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }	
	

    public Vector<ResourceTaskAllocTO> getListByResourceTask(ResourceTaskTO rtto, Connection c) throws DataAccessException {
        Vector<ResourceTaskAllocTO> response = new Vector<ResourceTaskAllocTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			String sql = "select task_id, resource_id, project_id, " +
					     "sequence, alloc_time " +
					     "from resource_task_alloc_plann " +
					     "where task_id=? and resource_id=? and project_id=?";
			pstmt = c.prepareStatement(sql);

			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getTask().getProject().getId());
			rs = pstmt.executeQuery();

			while (rs.next()){
			    ResourceTaskAllocTO rtato = super.populateBeanByResultSet(rs);
				response.addElement(rtato); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;

		try {
		    ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)to;
		    ResourceTaskTO rtto = rtato.getResourceTask();

		    String sql = "insert into resource_task_alloc_plann (task_id, resource_id, " +
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
			super.closeStatement(null, pstmt);
		}       
    }

    
    
    public void removeByResTaskAndSeq(ResourceTaskTO rtto, int seq, Integer newState, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		
		try {
			
			if (isOpen(rtto, newState)) {
			    String sql = "delete from resource_task_alloc_plann " +
			   			 "where task_id=? and resource_id=? " +
			   			 "and project_id=? and sequence >= " + seq ;
				pstmt = c.prepareStatement(sql);
				pstmt.setString(1, rtto.getTask().getId());
				pstmt.setString(2, rtto.getResource().getId());
				pstmt.setString(3, rtto.getTask().getProject().getId());		
				pstmt.executeUpdate();				
			}
																						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    } 	
	
    
    private boolean isOpen(ResourceTaskTO rtto, Integer newState) {
    	boolean response = false;
		if (newState!=null) {
			response = newState.equals(TaskStatusTO.STATE_MACHINE_OPEN);
		} else {
			response = (rtto!=null && rtto.getTaskStatus()!=null && rtto.getTaskStatus().isOpen());
		}		
		return response;
    }
    
}
