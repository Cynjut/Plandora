package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.DiscussionTopicTO;
import com.pandora.PlanningTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;

public class DiscussionTopicDAO extends DataAccess {
    
	
    public Vector<DiscussionTopicTO> getListByPlanning(String planning) throws DataAccessException {
    	Vector<DiscussionTopicTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			PlanningTO pto = new PlanningTO();
			pto.setId(planning);
			response = this.getListByPlanning(pto, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }
    
    
	/**
     * Get a list of Discussion Data from data base based on current planning object.
     */
    public Vector<DiscussionTopicTO> getListByPlanning(PlanningTO pto, Connection c) throws DataAccessException {
        Vector<DiscussionTopicTO> response= new Vector<DiscussionTopicTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
			pstmt = c.prepareStatement("select dt.id, dt.planning_id, dt.content, dt.parent_topic, " +
										"dt.user_id, dt.creation_date, u.username " +
								       "from discussion_topic dt, tool_user u " +
								       "where dt.planning_id=?" +
								         "and dt.user_id = u.id");
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    DiscussionTopicTO dtto = this.populateByResultSet(rs);
			    
			    UserTO uto = dtto.getUser();
			    uto.setUsername(getString(rs, "username"));
			    
				response.addElement(dtto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
    	DiscussionTopicTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select dt.id, dt.planning_id, dt.content, dt.parent_topic, " +
									   "dt.user_id, dt.creation_date, u.username " +
									   "from discussion_topic dt, tool_user u " +
									   "where dt.id=? and dt.user_id = u.id");
			pstmt.setString(1, to.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Insert a list of Discussion Topics into data base. 
     * But first, remove all objects from data base
     */
    public void insert(Vector<DiscussionTopicTO> topics, PlanningTO me, Connection c) throws DataAccessException {
    	
        this.removeByPlanning(me, c);
        
        if (topics!=null) {
            Iterator<DiscussionTopicTO> i =  topics.iterator();
            while(i.hasNext()) {
            	DiscussionTopicTO dtto = i.next();
            	
            	//create a clone of discussion topic and save new record without 'parent topic' link...
            	DiscussionTopicTO clone = dtto.getClone();
            	clone.setParentTopic(null);
                this.insert(clone, c);                
            }

            //after that, save only the links in order to avoid issues with FK constraints.
            Iterator<DiscussionTopicTO> j =  topics.iterator();
            while(j.hasNext()) {
            	DiscussionTopicTO dtto = j.next();
            	if (dtto.getParentTopic()!=null) {
            		this.update(dtto, c);	
            	}
            }
        }
    }
    

    
    @Override
	public void remove(TransferObject to, Connection c)	throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			
			pstmt = c.prepareStatement("update discussion_topic set parent_topic=null where parent_topic=?");
			pstmt.setString(1, to.getId());
			pstmt.executeUpdate();
			
			pstmt = c.prepareStatement("delete from discussion_topic where id=?");
			pstmt.setString(1, to.getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
	}


	/**
     * Remove all topics related with current Planning object
     */
    private void removeByPlanning(PlanningTO pto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			//get a list of topics to be removed...
			pstmt = c.prepareStatement("select distinct id from discussion_topic where planning_id=?");
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
				String item = getString(rs, "id");
				DiscussionTopicTO toBeRemoved = new DiscussionTopicTO(item);
				this.remove(toBeRemoved, c);
			} 
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    
    
    /**
     * Insert a new Discussion Topic into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			DiscussionTopicTO dtto = (DiscussionTopicTO)to;
			if (dtto.getId()==null || dtto.getId().trim().equals("")) {
				dtto.setId(this.getNewId());	
			}
		    
			pstmt = c.prepareStatement("insert into discussion_topic (id, planning_id, content, " +
									   "parent_topic, user_id, creation_date) values (?,?,?,?,?,?)");
			pstmt.setString(1, dtto.getId());
			pstmt.setString(2, dtto.getPlanningId());
			pstmt.setString(3, dtto.getContent());
			if (dtto.getParentTopic()!=null && dtto.getParentTopic().getId()!=null) {
				pstmt.setString(4, dtto.getParentTopic().getId());	
			} else {
				pstmt.setNull(4, java.sql.Types.VARCHAR);
			}
			pstmt.setString(5, dtto.getUser().getId());
			pstmt.setTimestamp(6, dtto.getCreationDate());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }


    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			DiscussionTopicTO dtto = (DiscussionTopicTO)to;
			pstmt = c.prepareStatement("update discussion_topic set planning_id=?, content=?, " +
									   "parent_topic=?, user_id=?, creation_date=? where id=?");
			pstmt.setString(1, dtto.getPlanningId());
			pstmt.setString(2, dtto.getContent());
			if (dtto.getParentTopic()!=null && dtto.getParentTopic().getId()!=null) {
				pstmt.setString(3, dtto.getParentTopic().getId());	
			} else {
				pstmt.setNull(3, java.sql.Types.VARCHAR);
			}
			pstmt.setString(4, dtto.getUser().getId());
			pstmt.setTimestamp(5, dtto.getCreationDate());
			pstmt.setString(6, dtto.getId());
			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    
    /**
     * Create a new TO object based on data into result set.
     */
    protected DiscussionTopicTO populateByResultSet(ResultSet rs) throws DataAccessException{
    	String id = getString(rs, "id");
    	DiscussionTopicTO response = new DiscussionTopicTO(id);
        
        response.setPlanningId(getString(rs, "planning_id"));
        response.setContent(getString(rs, "content"));
        String parent = getString(rs, "parent_topic");
        if (parent!=null && !parent.trim().equals("")) {
        	response.setParentTopic(new DiscussionTopicTO(parent));	
        }
        response.setUser(new UserTO(getString(rs, "user_id")));
        response.setCreationDate(getTimestamp(rs, "creation_date"));

        return response;
    }
    
}
