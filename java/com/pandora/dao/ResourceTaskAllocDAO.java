package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.ResourceDateAllocTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DBUtil;

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

    public HashMap<String, ResourceTaskAllocTO> getHashAlloc(Connection c, String projectFilter, String resourceFilter, Timestamp iniDate,	Timestamp finalDate) throws DataAccessException {
    	HashMap<String, ResourceTaskAllocTO> response = new HashMap<String, ResourceTaskAllocTO>();
    	DbQueryDelegate qdel = new DbQueryDelegate();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {

			String dbname = qdel.getDBProductName();
			pstmt = c.prepareStatement("select sub.bucket_date, sub.resource_id, sub.project_id, sum(sub.alloc_time) as alloc_time, null as task_id, 1 as sequence from ( " +
										   "select " + DBUtil.addDate(dbname, "rt.actual_date", "rta.sequence-1") + " as bucket_date, " +
					                               "rt.resource_id, rt.project_id, rt.task_id, rta.alloc_time " +
								             "from resource_task rt, resource_task_alloc rta, planning p " + 
								            "where rt.task_id = rta.task_id and rt.resource_id = rta.resource_id " +
								              "and rt.project_id = rta.project_id and rt.task_id = p.id and rt.actual_date is not null and " +
								              "rta.alloc_time > 0 and rta.project_id in (" + projectFilter + ") and rta.resource_id in (" + resourceFilter + ") " +
								        ") as sub " +
								        "where sub.bucket_date >= ? and sub.bucket_date <= ? " +
								        "group by sub.bucket_date, sub.resource_id, sub.project_id");  
			pstmt.setTimestamp(1, iniDate);			
			pstmt.setTimestamp(2, finalDate);			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ResourceTaskAllocTO rtato = this.populateBeanByResultSet(rs);
			    String key = rtato.getAllocKey();
				response.put(key, rtato);
			} 
						
		} catch (Exception e) {
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
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

        
    /**
     * Insert a list of Resource Task Alloc objects into data base.
     */
    public void insert(Vector<ResourceTaskAllocTO> v, ResourceTaskTO rtto, Integer newState, Connection c) throws DataAccessException {
        if (v!=null){
    		Iterator<ResourceTaskAllocTO> i = v.iterator();
    		while (i.hasNext()){
    		    ResourceTaskAllocTO rtato = i.next();
    		    rtato.setNewState(newState);
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
	    this.removeByResTaskAndSeq(rtato.getResourceTask(), rtato.getSequence().intValue(), null, c);
	    this.insert(rtato, c);		    
    }

    
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResourceTaskAllocPlannedDAO allocPlandao = new ResourceTaskAllocPlannedDAO();
		
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
			
			allocPlandao.updateByTaskAlloc(rtato, rtato.getNewState(), c);
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    /**
     * Remove a Resource Task Alloc object from data base.
     */
    public void removeByResourceTask(ResourceTaskTO rtto, Integer newState, Connection c) throws DataAccessException {
        this.removeByResTaskAndSeq(rtto, 0, newState, c);
    }   
    

    private void removeByResTaskAndSeq(ResourceTaskTO rtto, int seq, Integer newState, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResourceTaskAllocPlannedDAO allocPlandao = new ResourceTaskAllocPlannedDAO();
		
		try {
		    String sql = "delete from resource_task_alloc " +
			   			 "where task_id=? and resource_id=? " +
			   			 "and project_id=? and sequence >= " + seq ;
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getTask().getProject().getId());		
			pstmt.executeUpdate();
			
			allocPlandao.removeByResTaskAndSeq(rtto, seq, newState, c);
																			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
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
        
        try {
        	Timestamp bucket = getTimestamp(rs, "bucket_date");
        	rtto.setActualDate(bucket);
        }catch(Exception e) {
        }
        
        return rtato;
    }
    
    
    public Vector<ResourceDateAllocTO> getResourceDateAllocList(ResourceTO rto, Timestamp inicialDate, Timestamp finalDate) throws DataAccessException {
        Vector<ResourceDateAllocTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getResourceDateAllocList(rto, inicialDate, finalDate, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    
    protected Vector<ResourceDateAllocTO> getResourceDateAllocList(ResourceTO rto, Timestamp inicialDate, Timestamp finalDate, Connection c) throws DataAccessException{
        Vector<ResourceDateAllocTO> response = new Vector<ResourceDateAllocTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			DbQueryDelegate qdel = new DbQueryDelegate();
			String dbname = qdel.getDBProductName();
            
			String sql = "select sum(t.alloc_time) as alloc_time, t.date, t.resource_id from " +
							"( " +
							"    select total.resource_id, " +
							"    total.date, total.alloc_time " +
							"    from " +
							"    ( " +
							"        select rt.resource_id, rta.alloc_time, rta.task_id,  " +
							DBUtil.addDate(dbname, "rt.actual_date", "rta.sequence-1") + " as date " +
							"        from " +
							"        ( " +
							"            resource_task rt inner join resource_task_alloc rta " +
							"            on rt.task_id = rta.task_id and rt.resource_id = rta.resource_id and rt.project_id = rta.project_id " +
							"        ) " +
							"        where " +
							"        rt.task_status_id <> '1' " +
							"        and rta.alloc_time >= 0 " +
							"        and rt.actual_date is not null " +
							"        and rt.resource_id = ? " +
							"    ) total " +
							") t " +
							"where t.date >= ? " +
							"and t.date <= ? " +
							"group by t.date, t.resource_id " +
							"order by t.date";
			pstmt = c.prepareStatement(sql);

			pstmt.setString(1, rto.getId());
			pstmt.setTimestamp(2, inicialDate);
			pstmt.setTimestamp(3, finalDate);
			rs = pstmt.executeQuery();

			while (rs.next()){
			    ResourceDateAllocTO rdato = new ResourceDateAllocTO(getString(rs, "resource_id"));
			    rdato.setAllocTime(getInteger(rs, "alloc_time"));
			    rdato.setDate(getDate(rs, "date"));
				response.addElement(rdato); 
			} 
						
		} catch (DataAccessException e) {
			throw new DataAccessException(e);
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}catch (BusinessException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    
    	
    }


}
