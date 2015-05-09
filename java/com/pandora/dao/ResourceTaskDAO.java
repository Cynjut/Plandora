package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.CustomNodeTemplateTO;
import com.pandora.DecisionNodeTemplateTO;
import com.pandora.LeaderTO;
import com.pandora.NodeTemplateTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskStatusTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.ResourceTaskBUS;
import com.pandora.bus.kb.IndexEngineBUS;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.AcceptTaskInsertionException;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.ZeroCapacityDBException;
import com.pandora.exception.ZeroCapacityException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 * This class contain all methods to handle data related with Resource Task entity into data base. 
 */
public class ResourceTaskDAO extends DataAccess {

    
    /**
     * Get a list of Resource Task objects based on Task object
     */
    public Vector getListByTask(TaskTO tto, boolean exceptCancelItem) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByTask(tto, c, exceptCancelItem);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    
    public void updateByResource(ResourceTaskTO rtto) throws DataAccessException {
		Connection c = null;
		try {
			c = getConnection(false);
			this.updateByResource(rtto, c);
			c.commit();
		} catch(Exception e){
			try {
				c.rollback();
			} catch (Exception er) {
				throw new DataAccessException(e);
			}
			throw new DataAccessException(e);
			
		} finally{
			this.closeConnection(c);
		}		    	    	
    }	
    
       
    /**
     * Get a list of Resource Task objects based on Task object
     */
    public Vector getListByTask(TaskTO tto, Connection c, boolean exceptCancelItem) throws DataAccessException {
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String sql = "select rt.task_id, rt.resource_id, rt.project_id, rt.billable, " +
					   "rt.start_date, rt.estimated_time, rt.actual_date, rt.actual_Time, rt.task_status_id, " +
					   "u.name as USER_NAME, u.username, ts.state_machine_order, ts.name as STATUS_NAME, " +
					   "u.color as USER_COLOR " +
					   "from resource_task rt, tool_user u, task_status ts, resource r " +
					   "where u.id = rt.resource_id " +
					   "and u.id = r.id and rt.project_id = r.project_id " +
					   "and rt.task_status_id = ts.id " +
					   "and rt.task_id=? and rt.project_id=?";
		    if (exceptCancelItem){
		        sql+=" and ts.state_machine_order<>" + TaskStatusTO.STATE_MACHINE_CANCEL;
		    }
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, tto.getId());
			pstmt.setString(2, tto.getProject().getId());
			rs = pstmt.executeQuery();
			
			while (rs.next()){
			    ResourceTaskTO rtto = this.populateBeanByResultSet(rs);

			    //get remaining fields...
			    ResourceTO rto = rtto.getResource();
			    rto.setName(getString(rs, "USER_NAME"));
			    rto.setUsername(getString(rs, "username"));
			    rto.setColor(getString(rs, "USER_COLOR"));
			    
			    TaskStatusTO tsto = rtto.getTaskStatus();
			    tsto.setStateMachineOrder(getInteger(rs, "state_machine_order"));
			    tsto.setName(getString(rs, "STATUS_NAME"));

			    //get allocations for current Resource task
			    ResourceTaskAllocDAO rtadao = new ResourceTaskAllocDAO();
			    rtto.setAllocList(rtadao.getListByResourceTask(rtto, c));
			    
			    rtto.setHandler(tto.getHandler());
			    if (tto.getComment()!=null) {
			        rtto.setThirdPartComment(tto.getComment());    
			    }
			    rtto.setTask(tto);
			    
				response.addElement(rtto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    /**
     * Get a list of resource Task objects related with a requirement.<br>
     * Consider only active tasks.
     */
    public Vector getListByRequirement(RequirementTO rqto, Connection c) throws DataAccessException {
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String sql = "select distinct rt.task_id, rt.resource_id, rt.project_id, rt.billable, " +
						 "rt.start_date, rt.estimated_time, rt.actual_date, rt.actual_Time, " +
						 "rt.task_status_id, u.username, ts.state_machine_order " +
					     "from requirement rq, task t, resource_task rt, tool_user u, task_status ts " +
					     "where t.requirement_id = rq.id " +
					     "and rt.project_id = rq.project_id " +
					     "and rt.project_id = t.project_id " +
					     "and rt.task_id = t.id " +
					     "and rt.resource_id = u.id " +
					     "and rt.task_status_id = ts.id " +
					     "and rq.id=?";
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, rqto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ResourceTaskTO rtto = this.populateBeanByResultSet(rs);
			    
			    //get remaining fields...
			    ResourceTO rto = rtto.getResource();
			    rto.setUsername(getString(rs, "username"));
			    TaskStatusTO tsto = rtto.getTaskStatus();
			    tsto.setStateMachineOrder(getInteger(rs, "state_machine_order"));
			    
				response.addElement(rtto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    /**
     * Get a list of resource tasks based on resource object
     */
    public Vector<ResourceTaskTO> getTaskListByResource(ResourceTO rto, boolean hideClosed) throws DataAccessException {
        Vector<ResourceTaskTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getTaskListByResource(rto, hideClosed, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;        
    }

    /**
     * Get a list of resource tasks based on resource object and project object.
     */
    public Vector getTaskListByResourceProject(ResourceTO rto, ProjectTO pto) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getTaskListByResourceProject(rto, pto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;        
    }   
    
    
    private Vector<ResourceTaskTO> getUnsignedTasks(Connection c, ResourceTO rto, Timestamp ts) throws Exception {
    	Vector<ResourceTaskTO> response = new Vector<ResourceTaskTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		UserDelegate udel = new UserDelegate();
		ProjectDelegate pdel = new ProjectDelegate();
		
		//get a list of project related to the current user..
		Vector<ProjectTO> projectList = pdel.getProjectListForWork(new UserTO(rto.getId()), true);
		
		UserTO uto = udel.getRoot();

		pstmt = c.prepareStatement(this.getTaskListByResourceQuery());
		pstmt.setString(1, uto.getId());
		pstmt.setString(2, uto.getId());
	    pstmt.setTimestamp(3, ts);
		
		rs = pstmt.executeQuery();
		while (rs.next()){
		    ResourceTaskTO rrto = this.populateBeanByResultSet(rs);

		    for (int i=0; i<projectList.size(); i++) {
		    	ProjectTO pto = (ProjectTO)projectList.elementAt(i);
		    	if (rrto.getResource().getProject().getId().equals(pto.getId())){

				    //get remaining field...
				    getRemainingFields(rs, rrto);
				    
				    if (rrto.getTaskStatus()!=null && !rrto.getTaskStatus().isFinish()) {
				    	response.addElement(rrto);	
				    }
					
					break;
		    	}
		    }
		    
		}
    
		return response;
    }
    
    
    private void updateByResource(ResourceTaskTO rtto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("update resource_task set billable=? where " +
									   "task_id=? and resource_id=? and project_id=?");
		    if (rtto.getBillableStatus()!=null) {
		    	pstmt.setInt(1, rtto.getBillableStatus().booleanValue()?1:0);	
		    } else {
		    	pstmt.setInt(1, 0);
		    }
		    pstmt.setString(2, rtto.getTask().getId());
		    pstmt.setString(3, rtto.getResource().getId());
		    pstmt.setString(4, rtto.getTask().getProject().getId());		
		    pstmt.executeUpdate();
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    /**
     * Get a list of resource tasks based on resource object
     */
    private Vector<ResourceTaskTO> getTaskListByResource(ResourceTO rto, boolean hideClosed, Connection c) throws DataAccessException {
        Vector<ResourceTaskTO> response = new Vector<ResourceTaskTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int daysAgo = -7;
		try {
		    
			PreferenceTO prto = rto.getPreference();
		    if (!hideClosed) {
		    	if (prto==null) {
		    		PreferenceDAO pdao = new PreferenceDAO();
		    		prto = pdao.getObjectByUser(rto, c);
		    	}		    
		        String value = prto.getPreference(PreferenceTO.MY_TASK_DAYS_AGO);
		        daysAgo = Integer.parseInt(value) * (-1);
		    }
		    boolean showUnsignedTasks = true;
		    
			pstmt = c.prepareStatement(this.getTaskListByResourceQuery());  
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, rto.getId());
		    Timestamp ts = DateUtil.getChangedDate(DateUtil.getNow(), Calendar.DATE, daysAgo);
		    pstmt.setTimestamp(3, ts);
		    
		    if (showUnsignedTasks) {
			    Vector<ResourceTaskTO> unsigned = this.getUnsignedTasks(c, rto, ts);
			    if (unsigned!=null && unsigned.size()>0) {
			    	response.addAll(unsigned);
			    }		    	
		    }
		    
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ResourceTaskTO rrto = this.populateBeanByResultSet(rs);
			    rrto.setHandler(rto);
		
				//get remaining field...
			    getRemainingFields(rs, rrto);
			    
				response.addElement(rrto); 
			} 
						
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
	private void getRemainingFields(ResultSet rs, ResourceTaskTO rrto) throws DataAccessException, BusinessException {
		TaskStatusTO tsto = rrto.getTaskStatus();
		tsto.setName(getString(rs, "TASK_STATUS_NAME"));
		tsto.setStateMachineOrder(getInteger(rs, "state_machine_order"));
		
		ResourceTO rto = rrto.getResource();
		rto.setUsername(getString(rs, "username"));
		
		TaskTO tto = rrto.getTask();
		rrto.setTask(tto);
		tto.setName(getString(rs, "TASK_NAME"));
		tto.setIteration(getString(rs, "TASK_ITERATION"));
		
		String parent = getString(rs, "PARENT_TASK_ID");
		if (parent!=null) {
			tto.setParentTask(new TaskTO(parent));	
		}
		
		String createdById = getString(rs, "created_by");
		tto.setCreatedBy(new UserTO(createdById));
		
		CategoryTO cto = new CategoryTO();
		cto.setId(getString(rs, "category_id"));
		cto.setName(getString(rs, "CATEGORY_NAME"));
		Integer bill = getInteger(rs, "BILLABLE_CATEGORY");
		if (bill!=null) {
			cto.setIsBillable(new Boolean(bill.intValue()==1));		
		} else {
			cto.setIsBillable(new Boolean(false));
		}
		
		tto.setCategory(cto);

		ProjectTO pto = new ProjectTO();
		pto.setId(getString(rs, "project_id"));
		pto.setName(getString(rs, "PROJECT_NAME"));
		tto.setProject(pto);
		
		String req = getString(rs, "requirement_id");
		RequirementTO reqto = new RequirementTO(req);
		reqto.setIteration(getString(rs, "iteration"));
		reqto.setDescription(getString(rs, "REQ_DESC"));
		tto.setRequirement(reqto);		
		
		tto.setTemplateInstanceId(getInteger(rs, "instance_id"));
	}

    
    /**
     * Return a query that return the list of resource tasks based on resource object 
     */
    private String getTaskListByResourceQuery(){
        String response = "";
        response = "select rt.estimated_time, rt.start_date, rt.task_status_id, rt.resource_id, rt.billable, " +
		        	    "rt.actual_date, rt.actual_Time, t.id as TASK_ID, t.project_id, p.final_date, " +
					    "c.name as CATEGORY_NAME, ts.name as TASK_STATUS_NAME, ts.state_machine_order, c.billable as BILLABLE_CATEGORY, " +
					    "po.value, pr.name as PROJECT_NAME, u.username, t.requirement_id, t.created_by, t.category_id," +
					    "t.task_id as PARENT_TASK_ID, t.name as TASK_NAME, req.iteration, tpl.instance_id, p.iteration as TASK_ITERATION," +
					    "req.REQ_DESC " +
					"from resource_task rt, tool_user u, " +
						 "task t LEFT OUTER JOIN (select id, iteration, description as REQ_DESC from planning) as req on req.id = t.requirement_id, " +
					     "planning p LEFT OUTER JOIN (select instance_id, related_task_id from custom_node_template) as tpl on p.id = tpl.related_task_id, " +
					     "category c, project pr, " +
					     "task_status ts LEFT OUTER JOIN (select substr(id,21) as task_status_id, value from preference where user_id=? and id like 'home.taskList.order.%' order by value) as po on ts.id = po.task_status_id " + 
                    "where t.id = p.id  " +
						"and t.category_id = c.id " +
						"and rt.project_id = t.project_id " +
						"and rt.task_id = t.id " +
						"and rt.task_status_id = ts.id " +
						"and rt.project_id = pr.id " +
						"and t.is_parent_task = 0 " + 
						"and rt.resource_id = ? " +
						"and rt.resource_id = u.id " + 
						"and (p.final_date is null or p.final_date > ?) " +
						"order by po.value asc, ts.state_machine_order asc, rt.start_date desc";  
        
        return response;
    }
    
    /**
     * Get a list of resource tasks based on resource object and project object.
     */
    private Vector getTaskListByResourceProject(ResourceTO rto, ProjectTO pto, Connection c) throws DataAccessException {
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("select rt.estimated_time, rt.start_date, rt.task_status_id, rt.resource_id, " +
			        				     "rt.actual_date, rt.actual_Time, rt.billable, " +
  									     "t.id as TASK_ID, t.name, t.project_id, t.category_id, p.final_date, " +
									     "c.name as CATEGORY_NAME, ts.name as TASK_STATUS_NAME, " +
									     "ts.state_machine_order " +
									   "from resource_task rt, task t, planning p, category c, task_status ts " +
									   "WHERE t.id = p.id " +
									     "and t.category_id = c.id " +
									     "and rt.project_id = t.project_id " +
									     "and rt.task_id = t.id " +
									     "and rt.task_status_id = ts.id " +
									     "and t.is_parent_task = 0 " +
									     "and rt.resource_id = ? and rt.project_id = ?");  
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, pto.getId());			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ResourceTaskTO rrto = this.populateBeanByResultSet(rs);
				response.addElement(rrto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    /**
     * Get all taskResources related with a project object and some search criteria.
     */
    public Vector<ResourceTaskTO> getListByProject(ProjectTO pto, String statusSel, String resourceSel) throws DataAccessException {
        Vector<ResourceTaskTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProject(pto, statusSel, resourceSel, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;        
    }

    
    /**
     * Get all taskResources related with a project object and some search criteria.
     */
    private Vector<ResourceTaskTO> getListByProject(ProjectTO pto, String statusSel, String resourceSel, Connection c) throws DataAccessException {
        Vector<ResourceTaskTO> response = new Vector<ResourceTaskTO>();
        AdditionalFieldDAO afdao = new AdditionalFieldDAO();

		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String sql = "select rt.task_id, rt.resource_id, rt.project_id, rt.billable, " +
					   "rt.start_date, rt.estimated_time, rt.actual_date, rt.actual_Time, rt.task_status_id, " +
					   "u.username as USER_NAME, ts.state_machine_order, ts.name as TASK_STATS_NAME, p.iteration as TASK_ITERATION, " +
					   "t.name as task_name, t.category_id, c.name as CATEGORY_NAME, c.billable as CATEGORY_BILLABLE, t.requirement_id, " +
					   "cnt.instance_id, rp.iteration, t.task_id as PARENT_TASK_ID, rp.REQ_DESC " +
					   "from resource_task rt, tool_user u, task_status ts, planning p, category c, task t LEFT OUTER JOIN " +
					   "(select instance_id, related_task_id  from custom_node_template) as cnt on cnt.related_task_id = t.id " +
					   "LEFT OUTER JOIN (select id, iteration, description as REQ_DESC from planning ) as rp on rp.id = t.requirement_id " +
					   "where u.id = rt.resource_id " +
					   "and t.id = rt.task_id and t.id = p.id " +
					   "and t.project_id = rt.project_id " +
					   "and rt.task_status_id = ts.id and c.id = t.category_id " +
					   "and rt.project_id=?";
						
			
		    if (statusSel.equals("-1")){
		        sql+=" and ts.state_machine_order<>" + TaskStatusTO.STATE_MACHINE_CANCEL;
		        sql+=" and ts.state_machine_order<>" + TaskStatusTO.STATE_MACHINE_CLOSE;
		    } else if (statusSel.equals("-2")){
		        sql+="";
		    } else {
		        sql+=" and rt.task_status_id='" + statusSel + "'";
		    }
		    
		    if (!resourceSel.equals("-1")){
		        sql+=" and rt.resource_id='" + resourceSel + "' ";
		    }
			
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();
			
			while (rs.next()){
			    ResourceTaskTO rtto = this.populateBeanByResultSet(rs);
			    
			    //get remaining fields...
			    ResourceTO rto = rtto.getResource();
			    rto.setUsername(getString(rs, "USER_NAME"));
			    
			    TaskStatusTO tsto = rtto.getTaskStatus();
			    tsto.setStateMachineOrder(getInteger(rs, "state_machine_order"));
			    tsto.setName(getString(rs, "TASK_STATS_NAME"));
			    
			    TaskTO tto = rtto.getTask();
			    tto.setAdditionalFields(afdao.getListByPlanning(tto, null, c));
			    
			    tto.setName(getString(rs, "TASK_NAME"));
			    tto.setProject(pto);
			    tto.setIteration(getString(rs, "TASK_ITERATION"));
			    
			    RequirementTO req = new RequirementTO(getString(rs, "requirement_id"));
			    req.setIteration(getString(rs, "iteration"));
			    req.setDescription(getString(rs, "REQ_DESC"));
			    tto.setRequirement(req);
			    tto.setTemplateInstanceId(getInteger(rs, "instance_id"));
			    String parentTaskId = getString(rs, "PARENT_TASK_ID");
			    if (parentTaskId!=null) {
			    	tto.setParentTask(new TaskTO(parentTaskId));	
			    }
			    
			    CategoryTO cat = new CategoryTO(getString(rs, "category_id"));
			    cat.setName(getString(rs, "CATEGORY_NAME"));
			    tto.setCategory(cat);
				Integer bill = getInteger(rs, "CATEGORY_BILLABLE");
				if (bill!=null) {
					cat.setIsBillable(new Boolean(bill.intValue()==1));		
				} else {
					cat.setIsBillable(new Boolean(false));
				}
			    
				response.addElement(rtto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get a Resource Task object from database
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        ResourceTaskTO response = new ResourceTaskTO();
        TaskNodeTemplateDAO ntdao = new TaskNodeTemplateDAO();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    ResourceTaskTO rtto = (ResourceTaskTO)to;
			pstmt = c.prepareStatement("select rt.task_id, rt.resource_id, rt.project_id, rt.billable, " +
									   "rt.start_date, rt.estimated_time, rt.task_status_id, ts.state_machine_order, " +
									   "rt.actual_date, rt.actual_Time, u.name as userFullName " +
									   "from resource_task rt, task_status ts, tool_user u, resource r " +
									   "where rt.task_status_id = ts.id " +
									   "and rt.resource_id = r.id and rt.project_id = r.project_id " +
									   "and rt.resource_id = u.id " +
									   "and rt.task_id=? and rt.resource_id=? and rt.project_id=?");
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getResource().getProject().getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    response = this.populateBeanByResultSet(rs);
			    
			    //get remaining fields...
			    TaskStatusTO tsto = response.getTaskStatus();
			    tsto.setStateMachineOrder(getInteger(rs, "state_machine_order"));
			    ResourceTO rto = response.getResource();
			    rto.setName(getString(rs, "userFullName"));
			    
			    //check if related task contain a decision node linked...
			    DecisionNodeTemplateTO decision = ntdao.getDecisionNodeByTask(response.getTask(), c);
			    if (decision!=null) {
			    	response.getTask().setDecisionNode(decision);
			    }
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


    /**
     * Insert a list of Resource Task object into data base.
     */
    public void insert(Vector v, TaskTO tto, Connection c) throws Exception {
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
      
        if (v!=null && v.size()>0){
            
            //create list of allocated slots
            v = rtbus.generateAllocation(v);
       
    		Iterator i = v.iterator();
    		while (i.hasNext()){
    		    ResourceTaskTO rtto = (ResourceTaskTO)i.next();
    		    if (tto!=null) {
    		        rtto.setTask(tto);    
    		    }
    		    this.insert(rtto, c);
    		}            
        }
    }
    
    
    /**
     * Insert a new Resource Task object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    ResourceTaskTO rtto = (ResourceTaskTO)to;
		    
		    //get the task status from data base (first state of State Machine)
		    TaskStatusDAO tsdao = new TaskStatusDAO();
		    TaskStatusTO tsto = rtto.getTaskStatus();
		    if (tsto==null || tsto.getId()==null || tsto.getId().trim().equals("")) {
		    	tsto = tsdao.getObjectByStateMachine(c, TaskStatusTO.STATE_MACHINE_OPEN);
			    rtto.setTaskStatus(tsto);		    	
		    }
		    		    
		    //insert data of resource task
			pstmt = c.prepareStatement("insert into resource_task (task_id, resource_id, project_id, " +
									   "start_date, estimated_time, task_status_id, actual_date, actual_time, billable) " +
									   "values (?,?,?,?,?,?,?,?,?)");
			
			pstmt.setString(1, rtto.getTask().getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getTask().getProject().getId());		
			pstmt.setTimestamp(4, rtto.getStartDate());			
			pstmt.setInt(5, rtto.getEstimatedTime().intValue());
			pstmt.setString(6, rtto.getTaskStatus().getId());
			
			if (rtto.getActualDate()!=null){
				pstmt.setTimestamp(7, rtto.getActualDate());
			} else {
				pstmt.setNull(7, java.sql.Types.TIMESTAMP);
			}
			if (rtto.getActualTime()!=null){
				pstmt.setInt(8, rtto.getActualTime().intValue());    
			} else {
			    pstmt.setNull(8, java.sql.Types.DECIMAL);
			}
			if (rtto.getBillableStatus()!=null){
				pstmt.setInt(9, rtto.getBillableStatus().booleanValue()?1:0);    
			} else {
				pstmt.setInt(9, 0);
			}	
			pstmt.executeUpdate();
			
		    //create and insert into data base a new Resource Task History object
		    TaskHistoryDAO thdao = new TaskHistoryDAO();
		    TaskHistoryTO thto = thdao.populateBeanByResourceTask(rtto, tsto, null);
		    thdao.insert(thto, c);
		    
		    //insert a list of Resource Task Alloc objects related
		    ResourceTaskAllocDAO rtadao = new ResourceTaskAllocDAO();
		    rtadao.insert(rtto.getAllocList(), rtto, c);

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    /**
     * Remove a list of Resource Task objects from data base.
     */
    public void remove(Vector v, Connection c) throws DataAccessException {
        if (v!=null){
    		Iterator i = v.iterator();
    		while (i.hasNext()){
    		    ResourceTaskTO rtto = (ResourceTaskTO)i.next();
    		    this.remove(rtto, c);
    		}            
        }
    }
    
    
    /**
     * Remove a Resource Task object from data base.
     */
    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    ResourceTaskTO rtto = (ResourceTaskTO)to;
		    TaskStatusTO tsto = rtto.getTaskStatus();
		    
		    boolean stateToRemove = (tsto!=null && 
		    		(tsto.getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_OPEN)
		    				|| tsto.getStateMachineOrder().equals(TaskStatusTO.STATE_MACHINE_OPEN)));
		    boolean isLeaderHandler = (rtto.getHandler()!=null && (rtto.getHandler() instanceof LeaderTO) );

		    if (rtto.getTaskStatus()==null || stateToRemove || isLeaderHandler) {
		        
			    //remove all resource task alloc objects related
			    ResourceTaskAllocDAO rtadao = new ResourceTaskAllocDAO();
			    rtadao.removeByResourceTask(rtto, c);		        

			    //remove all history related 
			    TaskHistoryDAO thdao = new TaskHistoryDAO();
			    thdao.removeByResourceTask(rtto, c);

			    pstmt = c.prepareStatement("delete from resource_task where " +
				   							"task_id=? and resource_id=? and project_id=?");
			    pstmt.setString(1, rtto.getTask().getId());
			    pstmt.setString(2, rtto.getResource().getId());
			    pstmt.setString(3, rtto.getTask().getProject().getId());		
			    pstmt.executeUpdate();

		    } else {
		        throw new DataAccessException("A task already started cannot be removed."); 
		    }

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }   
    
    
    
    /**
     * Update a list of Resource Task objects from data base.
     */
    public void update(Vector v, Connection c) throws Exception {
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
        
        if (v!=null){
            
            //create list of allocated slots
            v = rtbus.generateAllocation(v);
            
    		Iterator i = v.iterator();
    		while (i.hasNext()){
    		    ResourceTaskTO rtto = (ResourceTaskTO)i.next();
    		    this.update(rtto, c);
    		}            
        }
    }
    
    /**
     * Update a Resource Task object from data base.
     */  
    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    ResourceTaskTO rtto = (ResourceTaskTO)to;
		    TaskStatusTO tsto = rtto.getTaskStatus();

		    //update status of task
			pstmt = c.prepareStatement("update resource_task set task_status_id=?, start_date=?, " +
									   "estimated_time=?, actual_date=?, actual_Time=?, billable=? " +
									   "where task_id=? and resource_id=? and project_id=?");
			pstmt.setString(1, tsto.getId());
			pstmt.setTimestamp(2, rtto.getStartDate());
			pstmt.setInt(3, rtto.getEstimatedTime().intValue());
			if (rtto.getActualDate()!=null){
				pstmt.setTimestamp(4, rtto.getActualDate());
			} else {
				pstmt.setNull(4, java.sql.Types.TIMESTAMP);
			}
			if (rtto.getActualTime()!=null){
			    pstmt.setInt(5, rtto.getActualTime().intValue());    
			} else {
			    pstmt.setNull(5, java.sql.Types.DECIMAL);
			}
			if (rtto.getBillableStatus()!=null){
			    pstmt.setInt(6, rtto.getBillableStatus().booleanValue()?1:0);    
			} else {
			    pstmt.setInt(6, 1);    
			}
			
			pstmt.setString(7, rtto.getTask().getId());
			pstmt.setString(8, rtto.getResource().getId());
			pstmt.setString(9, rtto.getTask().getProject().getId());
			pstmt.executeUpdate();

		    //create and insert into data base a new Resource Task History object
		    TaskHistoryDAO thdao = new TaskHistoryDAO();
		    TaskHistoryTO thto = thdao.populateBeanByResourceTask(rtto, tsto, rtto.getThirdPartComment());		    
		    thdao.insert(thto, c);		        

		    //insert a list of Resource Task Alloc objects related
		    if (rtto.getAllocList()!=null) {
			    ResourceTaskAllocDAO rtadao = new ResourceTaskAllocDAO();
			    rtadao.removeByResourceTask(rtto, c);		    
			    rtadao.insert(rtto.getAllocList(), rtto, c);		        
		    }
		    
			//update the content of object into Knowledge Base
	        IndexEngineBUS ind = new IndexEngineBUS();
	        ind.update(rtto);
		    		    
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} catch (Exception e) {
		    throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    
    /**
     * Update, remove or insert lists of Resource Task objects into data base.
     */
    public void updateLists(TaskTO tto, Vector removeList, Vector insertList, Vector updateList) throws DataAccessException {     
        Connection c = null;
		try {
			c = getConnection(false);
	        
	        //remove list of resource tasks, but check if any current ResourceTask object is NOT 'open' status
		    this.remove(removeList, c);

	        //insert new list of resource tasks objects
	        this.insert(insertList, null, c);

	        //update list of resource tasks objects
	        this.update(updateList, c);

	        //verify if there are remaining open resource task...otherwise, close the task 	        
	        if (tto.getFinalDate()==null) {
	        	boolean openExists = false;
		        Vector resTaskList = this.getListByTask(tto, c, true);
		        if (resTaskList!=null) {
		    		Iterator i = resTaskList.iterator();
		    		while (i.hasNext()){
		    		    ResourceTaskTO rtto = (ResourceTaskTO)i.next();
		    		    if (!rtto.getTaskStatus().isFinish()) {
		    		    	openExists = true; 
		    		    	break;
		    		    }
		    		}	        	        	
		        }
		        if (!openExists) {
		        	tto.setFinalDate(DateUtil.getNow());
		        	TaskDAO tdao = new TaskDAO();
		        	tdao.update(tto, c);
		        }
	        }
	        
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
     * Change the status of Resource Task (replace status of ResourceTask object and create 
     * a new record into history)
     */
    public void changeStatus(ResourceTaskTO rtto, Integer newState, String comment, boolean isMTClosingAllowed) throws DataAccessException {
        Connection c = null;
        RequirementDAO rdao = new RequirementDAO(); 
        AdditionalFieldDAO afdao = new AdditionalFieldDAO();
        TaskNodeTemplateDAO ntdao = new TaskNodeTemplateDAO();
        TaskDAO tdao = new TaskDAO();
        boolean changeReq = true;
        
		try {

			c = getConnection(false);

			TaskTO tto = rtto.getTask();
			NodeTemplateTO relatedNode = null;
			String answer = null;
			
			if (newState.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
				if (tto!=null && tto.getDecisionNode()!=null && rtto.getQuestionAnswer()!=null){
					relatedNode = ntdao.getNodeByTask(tto, c);
					DecisionNodeTemplateTO decision = tto.getDecisionNode();
					if (rtto.getQuestionAnswer().booleanValue()) {
						answer = DecisionNodeTemplateTO.LABEL_YES;
					} else if (!rtto.getQuestionAnswer().booleanValue()) {
						answer = DecisionNodeTemplateTO.LABEL_NO;
					}
					
					if (answer!=null) {
					    String history = decision.getQuestionContent() + " " + rtto.getHandler().getBundle().getMessage(rtto.getHandler().getLocale(), answer);
					    if (comment==null) {
					    	comment = "";
					    }
					    comment = comment.concat("\n\n[" + history + "]");						
					}
				}
			}
			
			//perform the resource task changing of status 
			this.changeStatus(rtto, newState, comment, isMTClosingAllowed, c);
			String reqId = tto.getRequirementId();
			
			//check if the finish event of resource task is triggered with a workflow...
			if (newState.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
				
				//check if the next node is a decision node...
				if (tto!=null && tto.getDecisionNode()!=null){
					DecisionNodeTemplateTO decision = tto.getDecisionNode();
					
					if (rtto.getQuestionAnswer()!=null) {
						
						//get the next node of workflow based on the resource answer...
						NodeTemplateTO nextNode = null;
						if (rtto.getQuestionAnswer().booleanValue() && decision.getNextNode()!=null) {
							NodeTemplateTO yesNode = new NodeTemplateTO(decision.getNextNode().getId());
							yesNode.setInstanceId(relatedNode.getInstanceId());
							nextNode = (NodeTemplateTO) ntdao.getObjectInTree(yesNode);
							
						} else if (!rtto.getQuestionAnswer().booleanValue() && decision.getNextNodeIfFalse()!=null) {
							NodeTemplateTO noNode = new NodeTemplateTO(decision.getNextNodeIfFalse().getId());
							noNode.setInstanceId(relatedNode.getInstanceId());
							nextNode = (NodeTemplateTO) ntdao.getObjectInTree(noNode);
						}
						
						if (nextNode!=null) {					
							TaskTO nextTask = ntdao.createNewTaskFromWorkflow(nextNode, reqId, rtto.getHandler(), comment, c);
							ntdao.createWorkflowLink(c, tto, nextTask);
							changeReq = false;						    
						}
						
						if (answer!=null && decision!=null) {
							ntdao.saveDecisionAnswer(answer, decision, reqId, c);	
						}
					}
					
				} else {
					relatedNode = ntdao.getNodeByTask(tto, c);
					
					//check if the next node is related to the parent task
					if (relatedNode==null && tto.getParentTask()!=null) {
						TaskTO macroTask = (TaskTO)tdao.getObject(tto.getParentTask(), c);
						relatedNode = ntdao.getNodeByTask(macroTask, c);
						reqId = macroTask.getRequirementId();
					}
					
					if (relatedNode!=null) {
						NodeTemplateTO nextOfNext = relatedNode.getNextNode();
						nextOfNext.setPlanningId(relatedNode.getPlanningId());
						nextOfNext.setInstanceId(relatedNode.getInstanceId());
						
						
						CustomNodeTemplateTO customNextNode = ntdao.getCustomNodeTemplate(nextOfNext, reqId, c);
						if (customNextNode.getRelatedTaskId()!=null) {				
							ntdao.reopenTask(customNextNode.getRelatedTaskId(), rtto.getHandler(), comment, c);
							changeReq = false;
						}
					}
				}
			}
			
            //update status of requirement related
			if (changeReq && reqId!=null && !reqId.trim().equals("")) {
			    rdao.changeRequirementStatus(rtto, newState, comment, c);			    
			}
			
			//save the additional fields into data base
			afdao.insert(tto.getAdditionalFields(), tto, c);
			tdao.updateByResource(tto, c);
			
			c.commit();

		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			if (e instanceof AcceptTaskInsertionException) {
			    throw (AcceptTaskInsertionException)e;
			}
			if (e instanceof ZeroCapacityException) {
				throw new ZeroCapacityDBException(e);
			}
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);			
		}
    }


    /**
     * Change the status of Resource Task (replace status of ResourceTask object and create 
     * a new record into history) 
     */
    public void changeStatus(ResourceTaskTO rtto, Integer newState, String comment, 
    		boolean isMTClosingAllowed, Connection c) throws DataAccessException {
    	
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
	    ResourceTaskAllocDAO rtadao = new ResourceTaskAllocDAO();
		PreparedStatement pstmt = null;
		try {
		    
		    boolean containAlloc = false;
		    if (rtto.getActualTime()!=null && rtto.getActualTime().intValue()>0) {
		        containAlloc = true;    
		    } else {
		        containAlloc = (rtto.getAllocList()!=null && rtto.getAllocList().size()>0);
		    }
		    
	        TaskStatusDAO tsdao = new TaskStatusDAO();
			TaskStatusTO tsto = tsdao.getObjectByStateMachine(c, newState);
			if (tsto==null){
			    throw new DataAccessException("The Task Status related with StateMachine='" + newState.toString() + "' cannot be null into data base.");
			}
			
			//if is close/cancel state, update task object into data base
			if (newState.equals(TaskStatusTO.STATE_MACHINE_CANCEL) || newState.equals(TaskStatusTO.STATE_MACHINE_CLOSE)){
				this.finishingChangeStatus(rtto, isMTClosingAllowed, c);
			}				
			
		    //create and insert into data base a new Task History object
			TaskHistoryDAO thdao = new TaskHistoryDAO();
			TaskHistoryTO thto = thdao.populateBeanByResourceTask(rtto, tsto, comment);
			thdao.insert(thto, c);

		    //update status of Resource Task to new status
			pstmt = c.prepareStatement("update resource_task set task_status_id=?, actual_date=?, " +
									   "actual_Time=?, start_date=?, estimated_time=?, billable=? " +
									   "where task_id=? and resource_id=? and project_id=?");
			pstmt.setString(1, tsto.getId());
			if (rtto.getActualDate()!=null && containAlloc){
			    pstmt.setTimestamp(2, rtto.getActualDate());    
			} else {
			    pstmt.setNull(2, java.sql.Types.TIMESTAMP);
			}
			if (rtto.getActualTime()!=null && containAlloc){
			    pstmt.setInt(3, rtto.getActualTime().intValue());    
			} else {
			    pstmt.setNull(3, java.sql.Types.DECIMAL);
			}
			pstmt.setTimestamp(4, rtto.getStartDate());
			if (rtto.getEstimatedTime()!=null && containAlloc){
			    pstmt.setInt(5, rtto.getEstimatedTime().intValue());    
			} else {
				pstmt.setInt(5, 0);
			}			
			if (rtto.getBillableStatus()!=null){
			    pstmt.setInt(6, rtto.getBillableStatus().booleanValue()?1:0);    
			} else {
			    pstmt.setInt(6, 1);    
			}
			
			pstmt.setString(7, rtto.getTask().getId());
			pstmt.setString(8, rtto.getResource().getId());
			pstmt.setString(9, rtto.getResource().getProject().getId());
			pstmt.executeUpdate();	
		    			
			if (containAlloc) {
				//re-build the list of resourceTask allocations (if necessary)...
				rtto = rtbus.generateAllocation(rtto);
			    
			    //insert a list of Resource Task Alloc objects related				
			    rtadao.removeByResourceTask(rtto, c);		    
			    rtadao.insert(rtto.getAllocList(), rtto, c);			    
			} else {
			    rtadao.removeByResourceTask(rtto, c);				
			}
		
		} catch (ZeroCapacityException ze) {
			throw new ZeroCapacityDBException(ze);
			
		} catch (Exception e) {
			throw new DataAccessException(e);
        }finally{
        	super.closeStatement(null, pstmt);
		} 		
    }
    
    private void finishingChangeStatus(ResourceTaskTO rtto, boolean isMTClosingAllowed, Connection c) throws DataAccessException{
	    ResourceTaskDelegate rtdel = new ResourceTaskDelegate();
	    TaskDAO tdao = new TaskDAO();
	    
	    TaskTO tto = rtto.getTask();

		//check if the task must be also finished ...
		Vector allResourceTaskList = this.getListByTask(tto, true);
		int numberOfOpenedResTask = 0;
		Iterator i = allResourceTaskList.iterator();
		while(i.hasNext()) {
		    ResourceTaskTO dummy = (ResourceTaskTO)i.next();
		    if (!dummy.getTaskStatus().isFinish() && !rtto.getId().equals(dummy.getId())) {
		        numberOfOpenedResTask++;
		    }
		}
		
		if (numberOfOpenedResTask==0) {
		    tto.setFinalDate(DateUtil.getNow());
		    tdao.update(tto, c);
    		TaskTO parenttask = tto.getParentTask();
    		
		    //check if the macro-task must be also finished...
		    if (isMTClosingAllowed && parenttask!=null) {
		    	
		    	//get a list of non-closed tasks under the macro task...
		    	Vector list;
				try {
					list = rtdel.getNotClosedTasksUnderMacroTask(tto);
				} catch (BusinessException e) {
					list = null;
					e.printStackTrace();
				}
		    					    	
		    	//TODO ainda existe um problema caso exista macro-tarefa de macro-tarefa de macro-tarefa..etc. 
				//O fechamento em cascata nao funciona!
		    	if (list!=null && list.size()==0) {
		    		parenttask = (TaskTO)tdao.getObject(parenttask, c);
		    		parenttask.setFinalDate(DateUtil.getNow());
				    tdao.update(parenttask, c);		    			
		    	}
		    }		    
		}    	
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

	public void changeAssignment(ResourceTaskTO rtto, UserTO uto, String comment) throws DataAccessException {
        Connection c = null;
        PreparedStatement pstmt = null;
        TaskHistoryDAO thdao = new TaskHistoryDAO();
        ResourceTaskAllocDAO rtdao = new ResourceTaskAllocDAO();
		try {
			
			c = getConnection(false);
			
			//get all history records related to resource_task
			Vector historyList = thdao.getListByResourceTask(rtto.getTask().getId(), rtto.getResource().getId());
			
			//fetch the allocation of resource_task from data base 
			rtto.setAllocList(rtdao.getListByResourceTask(rtto));
			
			//remove task history records...
			if (historyList!=null) {
				TaskStatusTO lastStatus = null;
				Iterator i = historyList.iterator();
				while(i.hasNext()) {
					TaskHistoryTO thto = (TaskHistoryTO)i.next();
					thdao.remove(thto, c);
					lastStatus = thto.getStatus();
				}
				
				TaskHistoryTO newHistory = thdao.populateBeanByResourceTask(rtto, lastStatus, comment);
				historyList.addElement(newHistory);
			}

			//remove allocation records...
			rtdao.removeByResourceTask(rtto, c);

			//perform the assingment updating (new resource id)... 
			pstmt = c.prepareStatement("update resource_task set resource_id=? " +
									   "where resource_id=? and task_id=? and project_id=?");
			pstmt.setString(1, uto.getId());
			pstmt.setString(2, rtto.getResource().getId());
			pstmt.setString(3, rtto.getTask().getId());
			pstmt.setString(4, rtto.getResource().getProject().getId());
			pstmt.executeUpdate();	

			ResourceTO newRes = new ResourceTO(uto.getId());
			newRes.setProject(rtto.getResource().getProject());
			
			if (historyList!=null) {
				Iterator i = historyList.iterator();
				while(i.hasNext()) {
					TaskHistoryTO thto = (TaskHistoryTO)i.next();
					thto.getResourceTask().setResource(newRes);
					thdao.insert(thto, c);
				}				
			}
			
			if (rtto.getAllocList()!=null) {
				Iterator i = rtto.getAllocList().iterator();
				while(i.hasNext()) {
					ResourceTaskAllocTO rtato = (ResourceTaskAllocTO)i.next();
					rtato.getResourceTask().setResource(newRes);
					rtdao.insert(rtato, c);
				}				
			}
			
			c.commit();

		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			throw new DataAccessException(e);
		} finally{
			try {
				if(pstmt != null) pstmt.close();	
			} catch (SQLException er) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}			
			this.closeConnection(c);			
		}
		
	}


    private Vector getListUntilID(String initialId, String finalId, Connection c) throws DataAccessException {
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String sql = "select rt.task_id, rt.resource_id, rt.project_id, rt.billable, " +
					   		"rt.start_date, rt.estimated_time, rt.actual_date, rt.actual_Time, rt.task_status_id, " +
					   		"u.username as USER_NAME, u.name as USER_FULL_NAME, ts.state_machine_order, ts.name as STATUS_NAME, " +
					   		"t.name as TASK_NAME, pt.description as TASK_DESC, pt.creation_date as TASK_CREATION_DATE, c.name as CATEGORY_NAME " +
					     "from resource_task rt, tool_user u, task_status ts, resource r, task t, planning pt, category c " +
					     "where u.id = rt.resource_id and u.id = r.id " +
					        "and rt.project_id = r.project_id and rt.task_id = t.id " +
					        "and t.id = pt.id and rt.task_status_id = ts.id " +
					        "and t.category_id = c.id " +
					     	"and t.id > '" + initialId + "' and t.id <= '" + finalId + "'";
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()){
			    ResourceTaskTO rtto = this.populateBeanByResultSet(rs);

			    //get remaining fields...
			    ResourceTO rto = rtto.getResource();
			    rto.setName(getString(rs, "USER_FULL_NAME"));
			    rto.setUsername(getString(rs, "USER_NAME"));

			    TaskStatusTO tsto = rtto.getTaskStatus();
			    tsto.setStateMachineOrder(getInteger(rs, "state_machine_order"));
			    tsto.setName(getString(rs, "STATUS_NAME"));

			    TaskTO tto = rtto.getTask();
			    tto.setName(getString(rs, "TASK_NAME"));
			    tto.setDescription(getString(rs, "TASK_DESC"));
			    tto.setCreationDate(getTimestamp(rs, "TASK_CREATION_DATE"));
			    
			    CategoryTO cato = new CategoryTO();
			    cato.setName(getString(rs, "CATEGORY_NAME"));
			    tto.setCategory(cato);
			    
			    //get allocations for current Resource task
			    ResourceTaskAllocDAO rtadao = new ResourceTaskAllocDAO();
			    rtto.setAllocList(rtadao.getListByResourceTask(rtto, c));
			    
				response.addElement(rtto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
        
     
    
    /**
     * Create a new TO object based on data into result set.
     */
    protected ResourceTaskTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        ResourceTaskTO rtto = new ResourceTaskTO();
        
        ProjectTO pto = new ProjectTO(getString(rs, "project_id"));
        
        rtto.setEstimatedTime(getInteger(rs, "estimated_time"));        
        rtto.setStartDate(getTimestamp(rs, "start_date"));
        rtto.setActualTime(getInteger(rs, "actual_time"));        
        rtto.setActualDate(getTimestamp(rs, "actual_date"));
        
        Integer billable = getInteger(rs, "billable");
        if (billable!=null) {
        	rtto.setBillableStatus(new Boolean(billable.intValue()==1));	
        }
        
        TaskTO tto = new TaskTO(getString(rs, "task_id"));
        tto.setProject(pto);
        rtto.setTask(tto);
        
        rtto.setTaskStatus(new TaskStatusTO(getString(rs, "task_status_id")));
        ResourceTO rto = new ResourceTO(getString(rs, "resource_id"));
        
        rto.setProject(pto);
        rtto.setResource(rto);        
        return rtto;
    }

	
}
