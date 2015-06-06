package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.CustomerTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.RequirementTriggerTO;
import com.pandora.RequirementWithTasksTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.RequirementBUS;
import com.pandora.bus.ResourceTaskBUS;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

/**
 * This class contain all methods to handle data related with requirement entity into data base.
 */
public class RequirementDAO extends PlanningDAO {

    AdditionalFieldDAO afdao = new AdditionalFieldDAO();
    
    DiscussionTopicDAO dtdao = new DiscussionTopicDAO();
    
    /**
     * Get a list of all requirement TOs from data base based on user id and 
     * boolean flag that must be used by searching.
     */    
    public Vector<RequirementTO> getListByUser(UserTO uto, boolean hideClosed, boolean sharingView) throws DataAccessException{
        Vector<RequirementTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByUser(uto, hideClosed, sharingView, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }


    public void insertList(Vector<RequirementTO> rlist) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection(false);
			for (RequirementTO rto : rlist) {
				this.insert(rto, c);
			}
			c.commit();
		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);			
		}    	
    }
    
    
    public void changeRequirementStatus(ResourceTaskTO rtto, Integer newState, String taskComment) throws BusinessException, DataAccessException{
        Connection c = null;
		PreparedStatement pstmt = null;
		try {
			c = getConnection(false);
			this.changeRequirementStatus(rtto, newState, taskComment, c);
			c.commit();
		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new DataAccessException(e);
		} finally{
			this.closeStatement(null, pstmt);
			this.closeConnection(c);			
		}    	
    }
    

	public Vector<RequirementWithTasksTO> getRequirementWithTaskList(ProjectTO pto, String iterationId, boolean hideFinishedReqs, boolean hideOldIterations) throws DataAccessException {
        Vector<RequirementWithTasksTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getRequirementWithTaskList(pto, iterationId, hideFinishedReqs, hideOldIterations, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
	}    


	public Vector getListUntilID(String initialId, String finalId) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListUntilID(initialId, finalId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
	
    private Vector<RequirementWithTasksTO> getRequirementWithTaskList(ProjectTO pto, String iterationId, boolean hideFinishedReqs, boolean hideOldIterations, Connection c) throws DataAccessException {
        Vector<RequirementWithTasksTO> response = new Vector<RequirementWithTasksTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		HashMap<String, RequirementWithTasksTO> hm = new HashMap<String, RequirementWithTasksTO>();

		try {
			
			String iterationWhere = "";
			if (!iterationId.equals("-1")){
				if (hideOldIterations) {
					iterationWhere = "and p.iteration = ? ";	
				} else {
					iterationWhere = "and r.id in (select distinct requirement_id from requirement_history where iteration=?) ";
				}
			}
			
			String closedReqsWhere = "";
			if (hideFinishedReqs){
				closedReqsWhere = "and p.final_date is null ";
			}

			//WARNING: the query below must return all tasks of request. The class AgilePanelReqDecorator demand this... (albertopereto 18/09/2009)
		    String sql = "select r.id as REQ_ID, p.description, r.priority, p.iteration, r.user_id, " +
		    		            "r.requirement_status_id, t.id as TASK_ID, t.name, rt.resource_id, " +
		    		            "rt.task_status_id, ts.state_machine_order as TASK_STATE, t.is_unpredictable, " +
		    		            "rs.state_machine_order, tu.username, rt.estimated_time, rt.actual_time " +
		    		     "from planning p, requirement_status rs, requirement r " +
		    		     	  "LEFT OUTER JOIN task t on t.requirement_id = r.id " +
		    		     	  "LEFT OUTER JOIN resource_task rt on t.id = rt.task_id " +
		    		     	  "LEFT OUTER JOIN task_status ts on rt.task_status_id = ts.id " +
		    		     	  "LEFT OUTER JOIN tool_user tu on tu.id = rt.resource_id " +
		    		     "where p.id = r.id and r.project_id = ? and rs.state_machine_order not in (202, 200) " +
		    		      "and (t.is_parent_task is null or t.is_parent_task=0) " +
		    		      "and rs.id = r.requirement_status_id " + iterationWhere + closedReqsWhere + 
		    		      "order by r.priority desc";
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, pto.getId());
			if (!iterationId.equals("-1")){
				pstmt.setString(2, iterationId);
			}
			rs = pstmt.executeQuery();
			while (rs.next()){
				String reqId = super.getString(rs, "REQ_ID"); 
				RequirementWithTasksTO rwto = (RequirementWithTasksTO)hm.get(reqId);
				if (rwto==null) {
					rwto = new RequirementWithTasksTO();

					rwto.setId(reqId);
					rwto.setDescription(super.getString(rs, "description"));
					rwto.setPriority(super.getInteger(rs, "priority"));
					rwto.setIteration(super.getString(rs, "iteration"));
					rwto.setRequester(new CustomerTO(super.getString(rs, "user_id")));
					rwto.setProject(pto);
					
				    RequirementStatusTO rsto = new RequirementStatusTO();
				    rsto.setStateMachineOrder(super.getInteger(rs, "state_machine_order"));
				    rsto.setId(super.getString(rs, "requirement_status_id"));
				    rwto.setRequirementStatus(rsto);
					
					hm.put(reqId, rwto);
					response.add(rwto);
				}
				
				String taskId = super.getString(rs, "TASK_ID");
				String statId = super.getString(rs, "task_status_id");
				if (taskId!=null && statId!=null) {
					TaskTO tto = new TaskTO(taskId);
					tto.setName(super.getString(rs, "name"));
					Integer unpredint = super.getInteger(rs, "is_unpredictable");
					if (unpredint!=null) {
						tto.setIsUnpredictable(new Boolean(unpredint.intValue()==1));	
					} else {
						tto.setIsUnpredictable(new Boolean(false));
					}
					tto.setProject(pto);
					ResourceTO rto = new ResourceTO(super.getString(rs, "resource_id"));
					rto.setUsername(super.getString(rs, "username"));
					TaskStatusTO tsto = new TaskStatusTO(statId);
					tsto.setStateMachineOrder(super.getInteger(rs, "TASK_STATE"));
					
					ResourceTaskTO rtto = new ResourceTaskTO();
					rtto.setEstimatedTime(super.getInteger(rs, "estimated_time"));
					rtto.setActualTime(super.getInteger(rs, "actual_time"));
					
					rtto.setTask(tto);
					rtto.setResource(rto);
					rtto.setTaskStatus(tsto);
					
					rwto.addResourceTask(rtto);					
				}
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}

    
    private Vector<RequirementTO> getListUntilID(String initialId, String finalId, Connection c) throws DataAccessException{
        Vector<RequirementTO> response = new Vector<RequirementTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    String sql = "select r.id, p.description, p.creation_date, p.iteration, r.suggested_date, " +
		    		"p.final_date, r.project_id, r.user_id, r.requirement_status_id, " +
		    		"r.deadline_date, r.priority, r.reopening, u.username, u.name as USER_FULL_NAME, " +
		    		"rs.name, rs.state_machine_order, r.category_id, c.name as CATEGORY_NAME " +
		    		"from requirement r, tool_user u, planning p, requirement_status rs, category c " +
        			"WHERE r.ID > '" + initialId + "' and r.ID <= '" + finalId + "' " +
        			"AND rs.id = r.requirement_status_id " +
        			"AND r.id = p.id " +
        			"AND c.id = r.category_id " +
        			"AND u.id = r.user_id " +
	    		    "order by r.id desc";
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()){
			    RequirementTO rto = this.populateReqByResultSet(rs, false);
				
				CustomerTO cto = new CustomerTO();
				cto.setUsername(getString(rs, "username"));
				cto.setName(getString(rs, "USER_FULL_NAME"));
				rto.setRequester(cto);
				
				response.addElement(rto);				
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get a list of all Requirement TOs from data base based on user and project.
     */
    public Vector<RequirementTO> getListByUserProject(UserTO uto, ProjectTO pto) throws DataAccessException{
        Vector<RequirementTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByUserProject(uto, pto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    /**
     * Get a list of all Requirement TOs from data base based on user id and 
     * boolean flag that must be used by searching.
     */
    private Vector<RequirementTO> getListByUser(UserTO uto, boolean hideClosed, boolean sharingView, Connection c) throws DataAccessException{
        Vector<RequirementTO> response= new Vector<RequirementTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		CustomerDAO cdao = new CustomerDAO();
		int daysAgo = -7;
		
		try {
		    if (!hideClosed) {
		        PreferenceTO prto = uto.getPreference();
		        String value = prto.getPreference(PreferenceTO.MY_REQU_DAYS_AGO);
		        daysAgo = Integer.parseInt(value) * (-1);
		    }
		    
		    String userSql = "r.user_id='" + uto.getId() + "' and r.project_id in (select project_id from customer where id = '" + uto.getId() + "' and (is_disable=0 or is_disable is null) ) ";
		    if (sharingView) {
		    	Vector<CustomerTO> canSeeReqList = cdao.getCanOtherReqCustomerList(uto, c);
		    	if (canSeeReqList!=null && canSeeReqList.size()>0) {
			    	userSql = this.getOtherCustomer(uto, canSeeReqList);
			    	userSql = userSql + " or (r.user_id='" + uto.getId() + "')";		    		
		    	}
		    }
	    	
		    String sql = "select r.id, p.description, p.creation_date, p.iteration, r.suggested_date, " +
            			    "p.final_date, r.project_id, r.priority, r.user_id, r.requirement_status_id, " +
            			    "r.deadline_date, r.reopening, rs.name, rs.state_machine_order, " +
            			    "pr.name as PROJECT_NAME, r.category_id, c.name as CATEGORY_NAME, u.username " +
            			"from requirement r, requirement_status rs, planning p, project pr, category c, tool_user u " +
            			"where pr.id = r.project_id and rs.id = r.requirement_status_id " +
            			  "and r.id = p.id and c.id = r.category_id and u.id = r.user_id " +
		    		      "and (p.final_date is null or p.final_date > ?) " +	    		      
		    		      "and (" + userSql + ") " +
		    		    "order by r.id desc";
			pstmt = c.prepareStatement(sql);
		    Timestamp ts = DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, daysAgo);
		    pstmt.setTimestamp(1, ts); 	
			rs = pstmt.executeQuery();
			while (rs.next()){
			    RequirementTO rto = this.populateReqByResultSet(rs, true);

			    //get remaining fields...
			    ProjectTO pto = rto.getProject();
			    pto.setName(getString(rs, "PROJECT_NAME"));
			    //pto.setProjectLeaders(ldao.getLeaderListByProjectId(pto, c));
			    UserTO requester = rto.getRequester();
			    requester.setUsername(getString(rs, "username"));

			    //get the additional fields of requirement
			    rto.setAdditionalFields(afdao.getListByPlanning(rto, null, c));

			    //get the discussion topics of requirement
			    rto.setDiscussionTopics(dtdao.getListByPlanning(rto, c));
			    
				response.addElement(rto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


	private String getOtherCustomer(UserTO uto, Vector<CustomerTO> canSeeReqList) {
		String userSql = "";
		boolean first = false;		
		Iterator<CustomerTO> i = canSeeReqList.iterator();
		while(i.hasNext()) {
			CustomerTO other = i.next();
			if (!other.getId().equals(uto.getId())) {
				if (!first) {
					first = true;
				} else {
					userSql = userSql + " or ";
				}
				userSql = userSql + "(r.user_id='" + other.getId() + "' and r.project_id='" + other.getProject().getId() + "')";
			}
		}
		return userSql;
	}
	

    /**
     * Get a list of all Requirement TOs from data base based on user and project.
     */
    private Vector<RequirementTO> getListByUserProject(UserTO uto, ProjectTO pto, Connection c) throws DataAccessException{
        Vector<RequirementTO> response= new Vector<RequirementTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    String sql = "select r.id, p.description, p.creation_date, p.iteration, r.suggested_date, " +
            			"p.final_date, r.project_id, r.priority, r.user_id, r.requirement_status_id, " +
            			"r.deadline_date, r.reopening, rs.name, rs.state_machine_order, " +
            			"r.category_id, c.name as CATEGORY_NAME " +
            			"from requirement r, requirement_status rs, planning p, category c " +
            			"WHERE r.user_id=? " +
            			"AND r.project_id=? " +
            			"AND c.id = r.category_id " +
            			"AND rs.id = r.requirement_status_id " +
            			"AND r.id = p.id";
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, uto.getId());
			pstmt.setString(2, pto.getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
			    RequirementTO rto =this.populateReqByResultSet(rs, true);			    
				response.addElement(rto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    public Vector<RequirementTO> getThinListByProject(ProjectTO pto, String categoryId) throws DataAccessException{
        Vector<RequirementTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getThinListByProject(pto, categoryId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;    	
    }

    
    /**
     * Get a list of all Requirement TOs from data base based on project id.
     */
    public Vector<RequirementTO> getListByProject(String idsList, String status, String requester, String priority, 
    		String categoryName, String templateId) throws DataAccessException {
        Vector<RequirementTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProject(idsList, status, requester, priority, categoryName, templateId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    public Vector<RequirementTO> getThinListByProject(ProjectTO pto, String categoryId, Connection c) throws DataAccessException{
        Vector<RequirementTO> response= new Vector<RequirementTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
		    String sql = "select r.id, p.creation_date, p.final_date " +
		    		     "  from requirement r, planning p " +
						 " where r.id = p.id and r.project_id=?";
		    if (categoryId!=null && !categoryId.trim().equals("") && !categoryId.trim().equals("-1")) {
		    	sql = sql + " and r.category_id=?";
		    }
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, pto.getId());
		    if (categoryId!=null && !categoryId.trim().equals("") && !categoryId.trim().equals("-1")) {
		    	pstmt.setString(2, categoryId);
		    }
			
			rs = pstmt.executeQuery();
			while (rs.next()){
				String id = super.getString(rs, "id");
			    RequirementTO rto = new RequirementTO(id);
			    rto.setCreationDate(super.getTimestamp(rs, "creation_date"));
			    rto.setFinalDate(super.getTimestamp(rs, "final_date"));
			    response.addElement(rto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}
		return response;    	
    }
    
    
    /**
     * Get a list of all Requirement TOs from data base based on project id.
     */
    private Vector<RequirementTO> getListByProject(String idsList, String status, String requester, String priority, 
            String categoryName, String templateId, Connection c) throws DataAccessException{
        Vector<RequirementTO> response= new Vector<RequirementTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		ResourceTaskDAO rtdao = new ResourceTaskDAO();

		try {
		    String sql = "select r.id, p.description, p.creation_date, p.iteration, r.suggested_date, r.project_id, " +
					    "r.user_id, p.final_date, r.priority, r.requirement_status_id, u.username, " +
					    "r.deadline_date, r.reopening, rs.name, rs.state_machine_order, " +
					    "r.category_id, c.name as CATEGORY_NAME, pr.name as PROJECT_NAME " +
						"from requirement r, requirement_status rs, planning p, tool_user u, category c, project pr " +
						"where r.requirement_status_id = rs.id " +
						"AND c.id = r.category_id AND pr.id = r.project_id " +
						"AND r.id = p.id AND r.user_id = u.id AND r.project_id in (" + idsList + ")";
		    
		    if (status.equals("-1")){
		        sql+=" AND p.final_date is NULL";
		    } else if (status.equals("-2")){
		        sql+="";
		    } else {
		        sql+=" AND r.REQUIREMENT_STATUS_ID='" + status + "'";
		    }

		    if (priority.equals("-1")){
		        sql+=" AND r.priority <> 1";
		    } else if (priority.equals("-2")){
		        sql+="";
		    } else {
		        sql+=" AND r.priority=" + priority;
		    }
		    
		    if (!requester.equals("-1")){
		        sql+=" AND r.USER_ID='" + requester + "' ";
		    }

		    if (!categoryName.equals("-1")){
		        sql+=" AND c.name='" + categoryName + "'";
		    }
		    
		    if (templateId!=null && !templateId.trim().equals("")) {
		    	sql+=" AND r.id in (select t.requirement_id from custom_node_template c, task t " +
		    			"where t.id =c.related_task_id and t.requirement_id is not null " +
		    			"and c.template_id='" + templateId + "')"; 
		    }
		    
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
						
			while (rs.next()){
			    RequirementTO rto = this.populateReqByResultSet(rs, true);

			    //get remaining field...
			    UserTO reqUto = rto.getRequester();
			    reqUto.setUsername(getString(rs, "USERNAME"));
			    
			    //get a list of resource task related. (maybe it returns null)
			    rto.setResourceTaskList(rtdao.getListByRequirement(rto, c));
			    ProjectTO pto = rto.getProject();
			    pto.setName(getString(rs, "PROJECT_NAME"));
			    rto.setProject(pto);
			    
			    //get the additional fields of requirement
			    rto.setAdditionalFields(afdao.getListByPlanning(rto, null, c));
			    
			    //get the discussion topics of requirement
			    rto.setDiscussionTopics(dtdao.getListByPlanning(rto, c));

			    response.addElement(rto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get a list of all Requirement TOs from data base based on filters (Project, Requester user and keyword).
     */
    public Vector<RequirementTO> getListByFilter(ProjectTO pto, CustomerTO cto, Vector<String> kwList) throws DataAccessException {
        Vector<RequirementTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByFilter(pto, cto, kwList, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    /**
     * Get a list of all Requirement TOs from data base based on filters (Project, Requester user and keyword).
     */
    private Vector<RequirementTO> getListByFilter(ProjectTO pto, CustomerTO cto, Vector<String> kwList, Connection c) throws DataAccessException{
        Vector<RequirementTO> response= new Vector<RequirementTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    //if the requester user was used such as a filter...
		    String userWhere = "";
		    if (cto!=null){
		        userWhere = " and r.USER_ID='" + cto.getId() + "'";
		    }

		    //if the keyword was used such as a filter...		    
		    Vector<String> vfields = new Vector<String>();
		    vfields.addElement("p.description");
		    vfields.addElement("p.id");
		    String keyWhere = StringUtil.getSQLKeywordsByFields(kwList, vfields);
		    if (!keyWhere.equals("")){
		        keyWhere = " AND (" + keyWhere + ")";
		    }

		    String sql = "select p.id, p.description, p.creation_date, p.iteration, r.suggested_date, r.project_id, " +
					    "r.USER_ID, p.final_date, r.priority, r.REQUIREMENT_STATUS_ID, u.USERNAME, " +
					    "r.deadline_date, r.reopening, rs.name, rs.state_machine_order, " +
					    "r.category_id, c.name as CATEGORY_NAME " +
						"from requirement r, requirement_status rs, planning p, planning pp, " +
						"project pr, tool_user u, category c " +
						"where r.REQUIREMENT_STATUS_ID = rs.ID " +
						"and r.ID = p.ID " +
						"and r.PROJECT_ID = pp.ID " +
						"and r.PROJECT_ID = pr.ID " +
						"AND c.id = r.category_id " +
						"and pp.ID = pr.ID " +
						"and r.USER_ID = u.id " +
						"and r.project_id=?" + userWhere + keyWhere;
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();
						
			while (rs.next()){
			    RequirementTO rto =this.populateReqByResultSet(rs, true);

			    //get remaining field...
			    UserTO reqUto = rto.getRequester();
			    reqUto.setUsername(getString(rs, "USERNAME"));
			    
				response.addElement(rto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    /**
     * Get a list of all pending Requirement TOs into data base based on leader user.
     */
    public Vector<RequirementTO> getPendingListByUser(UserTO uto, boolean exceptCurrUserReq) throws DataAccessException {
        Vector<RequirementTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getPendingListByUser(uto, exceptCurrUserReq, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);			
		}
        return response;
    }
    
    
    private Vector<RequirementTO> getPendingListByUser(UserTO uto, boolean exceptCurrUserReq, Connection c) throws DataAccessException{
        Vector<RequirementTO> response= new Vector<RequirementTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
		    String sql = "select r.id, p.description, p.creation_date, p.iteration, r.suggested_date, r.project_id, " +
		    		     "r.user_id, r.deadline_date, r.priority, p.final_date, pr.name, r.REQUIREMENT_STATUS_ID, r.reopening, " +
		    		     "u.username, rs.name as STATUS_NAME, r.category_id, c.name as CATEGORY_NAME " +
            			"from requirement r, requirement_status rs, planning p, " +
            			"planning pp, project pr, leader e, tool_user u, category c, customer cu " +
            			"where r.REQUIREMENT_STATUS_ID = rs.ID and cu.id = e.id and pp.ID = cu.project_id " +
            			"and r.ID = p.ID and r.PROJECT_ID = pp.ID and r.PROJECT_ID = pr.ID " +
            			"and pp.ID = e.project_id and c.id = r.category_id and r.USER_ID = u.id " +
            			"and pp.final_date is null and rs.state_machine_order=1 " +
            			"and e.id = ? and (cu.is_disable is null or cu.is_disable=0) ";
		    //if true, exclude from the list, the requests of current user (uto)
		    if (exceptCurrUserReq) {
		        sql = sql + "and r.user_id <> ?";    
		    }
		    sql = sql + " order by r.priority desc";
		    
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, uto.getId());
			if (exceptCurrUserReq) {
			    pstmt.setString(2, uto.getId());
			}
			rs = pstmt.executeQuery();
			while (rs.next()){
			    RequirementTO rto = this.populateReqByResultSet(rs, false);
			    
			    //get remaining field...
			    ProjectTO pto = rto.getProject();
			    pto.setName(getString(rs, "NAME"));
			    
			    UserTO reqUto = rto.getRequester();
			    reqUto.setUsername(getString(rs, "USERNAME"));
			    
			    //set value of RequirementStatus...(for sure is "Waiting")
			    RequirementStatusTO rsto = rto.getRequirementStatus();
			    rsto.setStateMachineOrder(RequirementStatusTO.STATE_MACHINE_WAITING);
			    rsto.setName(getString(rs, "STATUS_NAME"));
			    
			    //get the additional fields of requirement
			    rto.setAdditionalFields(afdao.getListByPlanning(rto, null, c));
			    
			    //get the discussion topics of requirement
			    rto.setDiscussionTopics(dtdao.getListByPlanning(rto, c));
			    
				response.addElement(rto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get a specific Requirement TO from data base, based on id.
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
		RequirementTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {
		    
		    //select a requirementTO from database
		    RequirementTO filter = (RequirementTO)to;
		    String sql = "select r.id, p.description, p.creation_date, p.iteration, r.suggested_date, " +
		    			 "p.final_date, r.project_id, r.user_id, r.requirement_status_id, " +
		    			 "r.deadline_date, r.priority, r.reopening, u.username, u.name as USER_FULL_NAME, " +
		    			 "rs.name, rs.state_machine_order, r.category_id, c.name as CATEGORY_NAME " +
		    			 "from requirement r, tool_user u, planning p, requirement_status rs, category c " +
		    			 "WHERE r.id =? " +
		    			 "AND u.id = r.user_id " +
		    			 "AND rs.id = r.requirement_status_id " +
		    			 "AND c.id = r.category_id " +
		    			 "AND r.id = p.id";
			pstmt = c.prepareStatement(sql);		    
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();
						
			if (rs.next()){
				response = this.populateReqByResultSet(rs, true);
				
			    //get remaining fields...
				CustomerTO cto = response.getRequester();
			    cto.setUsername(getString(rs, "USERNAME"));
			    cto.setName(getString(rs, "USER_FULL_NAME"));
			    
			    //get the additional fields and discussion topics of request
			    response.setAdditionalFields(afdao.getListByPlanning(response, null, c));
			    response.setDiscussionTopics(dtdao.getListByPlanning(response, c));
			    
			    response.setRequester(cto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
  
    /**
     * Insert a new Requirement object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    //create a new id
		    String newId = this.getNewId();
		    RequirementTO rto = (RequirementTO)to;
		    rto.setId(newId);
		    
		    //get the requirement status from data base (first state of State Machine)
		    RequirementStatusTO rsto = null;
		    if (rto.getRequirementStatus()==null) {
			    RequirementStatusDAO rsdao = new RequirementStatusDAO();
			    rsto = rsdao.getObjectByStateMachine(c, RequirementStatusTO.STATE_MACHINE_WAITING);					    	
		    } else {
		    	rsto = rto.getRequirementStatus();
		    }
		    
		    //insert data into parent entity (PlanningDAO)
		    super.insert(rto, c);
		    
		    //insert data of requirement
			pstmt = c.prepareStatement("INSERT INTO requirement (id, suggested_date, " +
									   "project_id, user_id, requirement_status_id, deadline_date, " +
									   "priority, category_id, reopening) " +
									   "values (?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, newId);
			pstmt.setTimestamp(2, rto.getSuggestedDate());
			pstmt.setString(3, rto.getProject().getId());
			pstmt.setString(4, rto.getRequester().getId());			
			pstmt.setString(5, rsto.getId());
			pstmt.setTimestamp(6, rto.getDeadlineDate());
			pstmt.setInt(7, rto.getPriority().intValue());
			pstmt.setString(8, rto.getCategory().getId());
			pstmt.setInt(9, 0);
			pstmt.executeUpdate();

		    //create and insert into data base a new Requirement History object
		    RequirementHistoryDAO rhdao = new RequirementHistoryDAO(); 
		    RequirementHistoryTO rhto = new RequirementHistoryTO();
		    rhto.setRequirementId(newId);
		    rhto.setStatus(rsto);
		    rhto.setResource(rto.getRequester());
	        rhto.setComment(null);
	        if (rto.getIteration()!=null && rto.getIteration().equals("")) {
	        	rhto.setIteration(null);	
	        } else {
	        	rhto.setIteration(rto.getIteration());	
	        }
		    
		    rhdao.insert(rhto, c);

			//if is a Pre Approved Task...
			if (rto.canPreApprove()){
			    TaskDAO tdao = new TaskDAO();
			    tdao.createPreApprovedTask(rto, rto.getRequester(), c);
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }


    public void updatelite(RequirementTO to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("update planning set iteration=? where id=?");
			pstmt.setString(1, to.getIteration());
			pstmt.setString(2, to.getId());	
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}  
    }
    
    
    /**
     * Update Requirement object into data base, except id and requirement_status_id colluns
     */
    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		TaskDAO tdao = new TaskDAO();
		try {
		    RequirementTO rto = (RequirementTO)to;
		    RequirementStatusTO rsto = null;
		    String reopenSql = "";
		    
			if (rto.isReopening()) {
			    RequirementStatusDAO rsdao = new RequirementStatusDAO();
			    rsto = rsdao.getObjectByStateMachine(c, RequirementStatusTO.STATE_MACHINE_WAITING);
			    reopenSql = ", requirement_status_id=?, reopening=? ";
			}
		    
		    //update parent data (PlanningDAO)
		    super.update(rto, c);
		    		    
		    //update data of requirement
			pstmt = c.prepareStatement("update requirement set suggested_date=?, project_id=?, " +
									   "user_id=?, deadline_date=?, priority=?, category_id=? " + reopenSql +
									   "where id=?");
			pstmt.setTimestamp(1, rto.getSuggestedDate());
			pstmt.setString(2, rto.getProject().getId());
			pstmt.setString(3, rto.getRequester().getId());
			pstmt.setTimestamp(4, rto.getDeadlineDate());
			pstmt.setInt(5, rto.getPriority().intValue());
			pstmt.setString(6, rto.getCategory().getId());
			if (rto.isReopening()) {
			    pstmt.setString(7, rsto.getId());
			    pstmt.setInt(8, rto.getReopeningOccurrences().intValue());
			    pstmt.setString(9, rto.getId());
			} else {
			    pstmt.setString(7, rto.getId());    
			}
		    
			pstmt.executeUpdate();
			
			if (rto.isReopening()) {
			    //insert into data base a new Requirement History object			    
			    RequirementHistoryDAO rhdao = new RequirementHistoryDAO(); 
			    RequirementHistoryTO rhto = new RequirementHistoryTO();
			    rhto.setRequirementId(rto.getId());
			    rhto.setStatus(rsto);
			    rhto.setResource(rto.getRequester());
		        rhto.setComment(rto.getAdditionalComment());    
			    rhdao.insert(rhto, c);			    
			}

			String parentIds = rto.getParentRequirementId();
			if (parentIds!=null) {
				PlanningRelationDAO prdao = new PlanningRelationDAO();
				Vector<PlanningRelationTO> relationList = rto.getRelationList();
				if (relationList!=null) {
					Iterator<PlanningRelationTO> i = relationList.iterator();
					while(i.hasNext()) {
						PlanningRelationTO relation = i.next();
						if (relation.getRelationType().equals(PlanningRelationTO.RELATION_PART_OF)) {
							prdao.removeRelation(relation, c);
						}
					}
				}
								
				if (!parentIds.equals("")) {
	            	String[] ids = parentIds.split(";");	            	
	            	for (int i=0; i<ids.length; i++) {
	            		if (!ids[i].trim().equals("")) {
							PlanningRelationTO relation = new PlanningRelationTO();
							relation.setPlanning(rto);
							relation.setRelated(new RequirementTO(ids[i].trim()));
							relation.setPlanType(PlanningRelationTO.ENTITY_REQ);
							relation.setRelatedType(PlanningRelationTO.ENTITY_REQ);
							relation.setRelationType(PlanningRelationTO.RELATION_PART_OF);
							prdao.insertRelation(relation, c);	            			
	            		}
	            	}
				}
			}
			
			tdao.copyReqIteration(rto.getId(), c);
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    /**
     * Cancel a Requirement into data base. Update the status of requirement and 
     * set a final date to requirement.
     */
    public void remove(TransferObject to, Connection c) throws DataAccessException {
	    RequirementTO rto = (RequirementTO)to;
	    RequirementTriggerTO rtgto = new RequirementTriggerTO(null, RequirementStatusTO.STATE_MACHINE_CANCEL);
	    this.changeStatus(rto, rto.getRequester(), rtgto, c);
    }


    /**
     * Remove the requirement definitily from DB.
     * Used by Agile Board form.
     */
	public void deleteFromDB(RequirementTO rto) throws DataAccessException {
        Connection c = null;
		PreparedStatement pstmt = null;
		try {
			c = getConnection(false);
			
			pstmt = c.prepareStatement("delete from requirement_history where requirement_id=?");
			pstmt.setString(1, rto.getId());
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from plan_relation where plan_related_id=? or planning_id=?");
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, rto.getId());
			pstmt.executeUpdate();	
			
			pstmt = c.prepareStatement("delete from additional_field where planning_id=?");
			pstmt.setString(1, rto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from requirement where id=?");
			pstmt.setString(1, rto.getId());
			pstmt.executeUpdate();
			
			pstmt = c.prepareStatement("delete from planning where id = ?");
			pstmt.setString(1, rto.getId());			
			pstmt.executeUpdate();
			
			c.commit();
			
		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new DataAccessException(e);

		} finally{
			this.closeStatement(null, pstmt);
			this.closeConnection(c);			
		}
	}
    
	
    /**
     * Refuse a Requirement. Set the current status of requirement to 'refuse' and
     * create a new record into requirement history with a comment. 
     * @param rto
     * @param uto
     * @param comment
     * @throws DataAccessException
     */
    public void refuse(RequirementTO rto, UserTO uto, String comment) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection(false);
			RequirementTriggerTO rtgto = new RequirementTriggerTO(comment, RequirementStatusTO.STATE_MACHINE_REFUSE);
			this.changeStatus(rto, uto, rtgto, c);
			c.commit();
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
     * Change the status of Requirement (replace status of requirement and 
     * create a new record into history).
     */
    public void changeStatus(RequirementTO rto, UserTO uto, RequirementTriggerTO rtgto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {

		    //load current requirement object from data base
	        rto = (RequirementTO)this.getObject(rto, c);
	        Integer currState = rto.getRequirementStatus().getStateMachineOrder();
			
	        Integer state = rtgto.getNewState();
		    if (!currState.equals(state) && state!=null){
		        
			    //for the 'refuse' (by leader), 'cancel' and 'close' status, set a final date for requirement
			    if (state.equals(RequirementStatusTO.STATE_MACHINE_REFUSE) || 
			            state.equals(RequirementStatusTO.STATE_MACHINE_CANCEL) ||
			            	state.equals(RequirementStatusTO.STATE_MACHINE_CLOSE)){
			        rto.setFinalDate(DateUtil.getNow());
			        super.update(rto, c); //update parent data with final date (PlanningDAO)
			    }
				
				//replace current requirement status to new status
		        RequirementStatusDAO rsdao = new RequirementStatusDAO();
				RequirementStatusTO rsto = rsdao.getObjectByStateMachine(c, state);
				if (rsto==null){
				    throw new DataAccessException("The Requirement Status related with StateMachine='" + state.toString() + "' cannot be null into data base.");
				}
		
			    //create and insert into data base a new Requirement History object
				RequirementHistoryDAO rhdao = new RequirementHistoryDAO();
				RequirementHistoryTO rhto = new RequirementHistoryTO();
				rhto.setRequirementId(rto.getId());
				rhto.setResource(uto);
				rhto.setStatus(rsto);
				rhto.setComment(rtgto.getComment());
				rhdao.insert(rhto, c);

			    //update status of requirement to new status
				pstmt = c.prepareStatement("update requirement set requirement_status_id=? where id=?");
				pstmt.setString(1, rsto.getId());
				pstmt.setString(2, rto.getId());			
				pstmt.executeUpdate();
				
				//check if current requirement contain parent reqs...
				Vector<PlanningRelationTO> relations = rto.getRelationList();
				if (relations!=null) {
					Vector<PlanningRelationTO> parent = PlanningRelationTO.getRelation(relations, PlanningRelationTO.RELATION_PART_OF, rto.getId(), true);
					if (parent!=null) {
						ResourceTaskBUS rtbus = new ResourceTaskBUS();
						Iterator<PlanningRelationTO> i = parent.iterator();
						while(i.hasNext()) {
							PlanningRelationTO prto = i.next();
							RequirementTO parentReq = new RequirementTO(prto.getRelated().getId());
							parentReq.setProject(rto.getProject());
							
					        //if there are remaining non-closed task or child req, the parent cannot be closed.                    
					        if (state.equals(RequirementStatusTO.STATE_MACHINE_CLOSE) 
					        		|| state.equals(RequirementStatusTO.STATE_MACHINE_CANCEL)){				        	
				        		if (!rtbus.thereAreRemainingNonClosedNodes(parentReq,  null, rto)) {
				        			changeStatus(parentReq, uto, rtgto, c);
				        		}
					        } else {
					        	changeStatus(parentReq, uto, rtgto, c);	
					        }
						}
					}					
				}
		    }
		
		} catch (BusinessException e) {
			throw new DataAccessException(e);  
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		} 		
    }

    
    /**
     * Create a new TO object based on data into result set.
     * @param rs
     * @return
     * @throws DataAccessException
     */
    protected RequirementTO populateReqByResultSet(ResultSet rs, boolean isRemainStatusValues) throws DataAccessException{
        RequirementTO response = new RequirementTO();
	    AttachmentDAO atDAO = new AttachmentDAO();
	    
        CustomerTO cto = new CustomerTO();
        ProjectTO pto = new ProjectTO();
        CategoryTO cato = new CategoryTO();
        RequirementStatusTO rsto = new RequirementStatusTO();
        
        response.setId(getString(rs, "id"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setDescription(getString(rs, "description"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        response.setSuggestedDate(getTimestamp(rs, "suggested_date"));
        response.setDeadlineDate(getTimestamp(rs, "deadline_date"));
        response.setPriority(getInteger(rs, "priority"));
        response.setReopeningOccurrences(getInteger(rs, "reopening"));
        response.setIteration(getString(rs, "iteration"));
        
        cto.setId(getString(rs, "USER_ID"));
        response.setRequester(cto);
        
        cato.setId(getString(rs, "CATEGORY_ID"));
        cato.setName(getString(rs, "CATEGORY_NAME"));
        response.setCategory(cato);
        
        pto.setId(getString(rs, "project_id"));
        response.setProject(pto);
        
        rsto.setId(getString(rs, "REQUIREMENT_STATUS_ID"));
        if (isRemainStatusValues){
    	    rsto.setName(getString(rs, "name"));
    	    rsto.setStateMachineOrder(getInteger(rs, "state_machine_order"));            
        }
        response.setRequirementStatus(rsto);
        
        response.setAttachments(atDAO.getListByPlanningId(response.getId(), null)); //obs: the projectId argument here is null because, the system must display only attachments linked with req id

        return response;
    }


    /**
     * This method contain the rules to change the requirement status  
     * after task status changing.
     */
    public void changeRequirementStatus(ResourceTaskTO rtto, Integer newState, String taskComment, Connection c) throws BusinessException, DataAccessException{
        boolean changeReqState = true;
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
        RequirementBUS rbus = new RequirementBUS(); 
        RequirementDAO rdao = new RequirementDAO();
        
        TaskTO tto = rtto.getTask();
        RequirementTO currReq = tto.getRequirement();

        //if all tasks of requirement is CLOSED, call the updating of 
        //related requirement status (see State Machine of system documentation)                    
        if (newState.equals(TaskStatusTO.STATE_MACHINE_CLOSE) || newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL)){
            changeReqState = (!rtbus.thereAreRemainingNonClosedNodes(currReq,  rtto, null));
        }

        if (changeReqState) {
            RequirementTriggerTO rtgto = rbus.getRequirementToChangeStatus(tto, newState, null);
            rdao.changeStatus(currReq, tto.getHandler(), rtgto, c);                                
        }        
    }


}
