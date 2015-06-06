package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.DiscussionTO;
import com.pandora.ProjectTO;
import com.pandora.TeamInfoTO;
import com.pandora.UserTO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.DataAccessException;

public class DiscussionDAO extends PlanningDAO {

	public Vector<DiscussionTO> getDiscussionList(ProjectTO pto, CategoryTO category) throws DataAccessException {
		Vector<DiscussionTO> response = null;
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


	public Vector<TeamInfoTO> getTeamInfo(UserTO uto, Timestamp iniDate) throws DataAccessException {
		Vector<TeamInfoTO> response = new Vector<TeamInfoTO>();
        Connection c = null;
		try {
			c = getConnection();
			response = this.getTeamInfo(uto, iniDate, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}
	
	
	private Vector<DiscussionTO> getDiscussionList(ProjectTO pto, CategoryTO category, Connection c) throws DataAccessException {
		Vector<DiscussionTO> response= new Vector<DiscussionTO>();
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
			super.closeStatement(rs, pstmt);
		}
		return response;
	}

	
	private Vector<TeamInfoTO> getTeamInfo(UserTO uto, Timestamp iniDate, Connection c) throws DataAccessException{
		Vector<TeamInfoTO> response = new Vector<TeamInfoTO>();
		ProjectDelegate pdel = new ProjectDelegate();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			
			Vector<ProjectTO> projList = pdel.getProjectListForWork(uto, false, true);
			String projectList = "";
			Iterator<ProjectTO> i = projList.iterator();
			while(i.hasNext()) {
				ProjectTO child = i.next();
				if (!projectList.equals("")) {
					projectList = projectList + ", ";
				}
				projectList = projectList + "'" + child.getId() + "'";
			}
			
			if (projectList!=null && !projectList.trim().equals("")) {
			    pstmt = c.prepareStatement(
			    		"select sub.task_id as id, sub.name, sub.status, sub.comment, sub.user_id, sub.creation_date, " + 
			    		"sub.type, sub.parent_id, sub.parent_topic, sub.req_project, sub.task_project, sub.occ_project, " +
			    		"sub.risk_project, u.username, u.name as fullname, sub.replyuser, pr.name as req_project_name, pt.name as task_project_name, " +
			    		"po.name as occ_project_name, pk.name as rsk_project_name " +
			    		"from tool_user u, ( " +
			    			"select h.task_id, t.name as name, s.id as status, h.comment, h.resource_id as user_id, h.creation_date, 'TSK' as type, " + 
			    			"null as parent_id, null as parent_topic, null as req_project, h.project_id as task_project, null as occ_project, null as risk_project, null as replyuser " +
			    			"from task_history h, task_status s, task t " +
			    			"where h.task_status_id = s.id and h.creation_date >= ? and h.comment is not null and t.id = h.task_id " +
			    		"union " +
			    			"select h.requirement_id, p.description, s.id, h.comment, h.user_id, h.creation_date, 'REQ', null, null, r.project_id, null, null, null, null " +
			    			"from requirement_history h, requirement_status s, planning p, requirement r " +
			    			"where h.requirement_status_id = s.id and h.creation_date >= ? " +
			    			"and s.state_machine_order in (1, 200, 201, 202) " +
			    			"and p.id = h.requirement_id and r.id = h.requirement_id " +
			    		"union " +
			    			"select o.id, o.name, h.occurrence_status, null, h.user_id, h.creation_date, 'OCC', null, null, null, null, o.project_id, null, o.source " +
			    			"from occurrence_history h, occurrence o " +
			    			"inner join planning p on p.id = o.id " +
			    			"where h.creation_date >= ? and o.id = h.occurrence_id " +
			    			"and h.history is not null and h.history <> '' and p.visible='1' " +
			    		"union " +
			    			"select a.id, a.name, null, h.history, h.user_id, h.creation_date, 'ATT', a.planning_id, null, " +
			    			"r.project_id as req_project, t.project_id as task_project, o.project_id as occ_project, k.project_id as risk_project, null " +
			    			"from attachment_history h, attachment a " +
			    			"LEFT OUTER JOIN (select id, project_id from requirement) as r on r.id = a.planning_id " + 
			    			"LEFT OUTER JOIN (select id, project_id from task) as t on t.id = a.planning_id " +
			    			"LEFT OUTER JOIN (select id, project_id from risk) as k on k.id = a.planning_id " +
			    			"LEFT OUTER JOIN (select id, project_id from occurrence) as o on o.id = a.planning_id " +
			    			"where h.attachment_id = a.id and h.creation_date >= ? and h.history is not null and h.history <> '' " +
			    		"UNION " + 
			    			"select r.id, r.name, s.status_type, r.strategy, h.user_id, h.creation_date, 'RSK', null, null, null, null, null, r.project_id, null " +
			    			"from risk_history h, risk r inner join planning p on p.id = r.id, risk_status s " +
			    			"where h.risk_id = r.id and h.risk_status_id=s.id " +
			    			"and h.creation_date >= ? and r.name is not null and r.name <> '' and p.visible='1'  " +		    			
			    		"UNION " +		    		
			    			"select null as id, repository_file, hist_type, comment, user_id, creation_date, 'REP', null, null, project_id, null, null, null, null " +
			    			"from repository_history " +		    		
			    			"where creation_date >= ? and repository_file <> '' " +
			    		"UNION " +
			    			"select d.id, null, null, d.content, d.user_id, d.creation_date, 'TPC', d.planning_id, d.parent_topic, " +
			    			"r.project_id as req_project, t.project_id as task_project, o.project_id as occ_project, k.project_id as risk_project, dd.username " +
			    			"from discussion_topic d " +
			    			"LEFT OUTER JOIN (select id, project_id from requirement) as r on r.id = d.planning_id " + 
			    			"LEFT OUTER JOIN (select id, project_id from task) as t on t.id = d.planning_id " +
			    			"LEFT OUTER JOIN (select id, project_id from risk) as k on k.id = d.planning_id " +
			    			"LEFT OUTER JOIN (select id, project_id from occurrence) as o on o.id = d.planning_id " +
			    			"LEFT OUTER JOIN (select u.username, dd.id from discussion_topic dd, tool_user u where u.id = dd.user_id) as dd on dd.id = d.parent_topic " +
			    			"where d.creation_date >= ? and d.content is not null and d.content <> '' " +	
			    		") as sub " +
			    		"LEFT OUTER JOIN (select id, name from project) as pr on pr.id = sub.req_project " +
			    		"LEFT OUTER JOIN (select id, name from project) as pt on pt.id = sub.task_project " +
			    		"LEFT OUTER JOIN (select id, name from project) as po on po.id = sub.occ_project " +
			    		"LEFT OUTER JOIN (select id, name from project) as pk on pk.id = sub.risk_project " +
			    		"where u.id = sub.user_id and " +
			    		   "((sub.req_project in (" + projectList + ")) " +
			    		   "or (sub.task_project in (" + projectList + ")) " +
			    		   "or (sub.occ_project in (" + projectList + ")) " +
			    		   "or (sub.risk_project in (" + projectList + "))) " +
			    		"order by sub.creation_date desc");
			    pstmt.setTimestamp(1, iniDate); pstmt.setTimestamp(2, iniDate); pstmt.setTimestamp(3, iniDate);
			    pstmt.setTimestamp(4, iniDate); pstmt.setTimestamp(5, iniDate); pstmt.setTimestamp(6, iniDate);
			    pstmt.setTimestamp(7, iniDate);
				rs = pstmt.executeQuery();
				while (rs.next()) {
				    response.addElement(this.populateTeamInfoByResultSet(rs));
				}
			}
			
		} catch (Exception e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
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

    
    private TeamInfoTO populateTeamInfoByResultSet(ResultSet rs) throws DataAccessException {
    	TeamInfoTO response = new TeamInfoTO();       
        response.setId(getString(rs, "id"));
        
        String projectId = getString(rs, "req_project");
        String projectName = getString(rs, "req_project_name");
        if (projectId==null) {
        	projectId = getString(rs, "task_project");
        	projectName = getString(rs, "task_project_name");
        	if (projectId==null) {
        		projectId = getString(rs, "occ_project");
        		projectName = getString(rs, "occ_project_name");
        		if (projectId==null) {
        			projectId = getString(rs, "risk_project");
        			projectName = getString(rs, "rsk_project_name");
        		}
        	}
        }
        
        ProjectTO pto = new ProjectTO(projectId);
        pto.setName(projectName);
        response.setProject(pto);
        
        response.setName(getString(rs, "name"));
        response.setStatus(getString(rs, "status"));
        response.setComment(getString(rs, "comment"));
        response.setUser(new UserTO(getString(rs, "user_id")));
        
        String fullname = getString(rs, "fullname");
        if (response.getUser()!=null) {
        	response.getUser().setName(fullname);
        }

        String username = getString(rs, "username");
        if (response.getUser()!=null) {
        	response.getUser().setUsername(username);
        }
        
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setType(getString(rs, "type"));
        response.setParentId(getString(rs, "parent_id"));
        response.setParentTopic(getString(rs, "parent_topic"));
        response.setParentTopicUser(getString(rs, "replyuser"));
        
        
        return response;
    }
    
    

	 
}
