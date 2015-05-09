package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.DiscussionTopicTO;
import com.pandora.PlanningTO;
import com.pandora.UserTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

public class DiscussionTopicDAO extends DataAccess {
    
	
    public Vector getListByPlanning(String planning) throws DataAccessException {
        Vector response = null;
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

    
    public Vector<DiscussionTopicTO> getListByUser(UserTO uto) throws DataAccessException {
        Vector<DiscussionTopicTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByUser(uto, c);
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
    public Vector getListByPlanning(PlanningTO pto, Connection c) throws DataAccessException {
        Vector response= new Vector();
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
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
		return response;
    }
    

    public Vector<DiscussionTopicTO> getListByUser(UserTO uto, Connection c) throws DataAccessException {
        Vector<DiscussionTopicTO> response= new Vector<DiscussionTopicTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
			pstmt = c.prepareStatement("select dt.id,dt.planning_id, dt.content, dt.parent_topic, " +
					"dt.user_id, dt.creation_date, sub.last_upd, (sub.cnt-1) as reply, tu.username " +
					"from discussion_topic dt, tool_user tu, " +
					"(select max(creation_date) as last_upd, count(*) as cnt, " +
							"planning_id from discussion_topic group by planning_id) as sub " +
					"where dt.parent_topic is null and sub.planning_id = dt.planning_id " +
					  "and dt.user_id = tu.id " +
					  "and dt.planning_id in (select distinct planning_id from discussion_topic where user_id=?)");
			pstmt.setString(1, uto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    DiscussionTopicTO dtto = this.populateByResultSet(rs);

			    dtto.setLastUpd(getTimestamp(rs, "last_upd"));
			    dtto.setReplyNumber(getInteger(rs, "reply"));
			    
			    UserTO user = dtto.getUser();
			    user.setUsername(getString(rs, "username"));
			    
				response.addElement(dtto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
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
     * Insert a list of Discussion Topics into data base. 
     * But first, remove all objects from data base
     */
    public void insert(Vector topics, PlanningTO me, Connection c) throws DataAccessException {
        this.removeByPlanning(me, c);
        if (topics!=null) {
            Iterator i =  topics.iterator();
            while(i.hasNext()) {
                DiscussionTopicTO dtto = (DiscussionTopicTO)i.next();
                this.insert(dtto, c);
            }
        }
    }
    
    /**
     * Remove all topics related with current Planning object
     */
    private void removeByPlanning(PlanningTO pto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {

			pstmt = c.prepareStatement("delete from discussion_topic where planning_id=?");
			pstmt.setString(1, pto.getId());
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
