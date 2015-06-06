package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.AdditionalFieldTO;
import com.pandora.CustomerTO;
import com.pandora.LeaderTO;
import com.pandora.MetaFieldTO;
import com.pandora.ProjectHistoryTO;
import com.pandora.ProjectStatusTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.UserBUS;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.StringUtil;

/**
 * This class contain all methods to handle data related with Project entity into data base.
 */
public class ProjectDAO extends PlanningDAO {

    AdditionalFieldDAO afdao = new AdditionalFieldDAO();

    public Vector<ProjectTO> getProjectAllocation(UserTO uto, ProjectTO pto) throws DataAccessException{
        Vector<ProjectTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getProjectAllocation(uto, pto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    public Vector<ProjectTO> getProjectRoot(boolean leanSelect) throws DataAccessException{
        Vector<ProjectTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getProjectRoot(leanSelect, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;        
    }
    

    public ProjectTO getProjectById(ProjectTO pto, boolean lazyLoad) throws DataAccessException {
        ProjectTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getProjectById(pto, lazyLoad, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }    
    
    
    public Vector<ProjectTO> getProjectListByParent(ProjectTO pto, boolean considerOnlyNonClosed) throws DataAccessException {
        Vector<ProjectTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getProjectListByParent(pto, considerOnlyNonClosed, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }    
    
    
    public Vector<ProjectTO> getProjectListForManagement(LeaderTO uto, boolean hideClosed) throws DataAccessException{
        Vector<ProjectTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getProjectListForManagement(uto, hideClosed, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    public Vector<ProjectTO> getProjectListForWork(UserTO uto, boolean isAlloc, boolean nonClosed) throws DataAccessException{
        Vector<ProjectTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getProjectListForWork(uto, isAlloc, nonClosed, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }
    
    
    public Vector getList(Connection c) throws DataAccessException {
		Vector<ProjectTO> response= new Vector<ProjectTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			
			pstmt = c.prepareStatement("select p.id, p.name, a.description, a.creation_date, p.allow_billable, " +
	   				   				 "p.project_status_id, p.parent_id, p.can_alloc, p.estimated_closure_date, " +
	   				   				 "p.repository_url, p.repository_class, p.repository_user, p.repository_pass, p.budget " +
	   				   				 "from project p, planning a " +
	   				   				 "WHERE p.id = a.id and a.final_date is null " + 
									 "AND p.id <> '" + ProjectTO.PROJECT_ROOT_ID + "'");
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    ProjectTO pto = this.populateObjectByResultSet(rs, false, c);
			    response.addElement(pto);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
   
    
    public Vector<ProjectTO> getProjectAllocation(UserTO uto, ProjectTO pto, Connection c) throws DataAccessException{
		Vector<ProjectTO> response= new Vector<ProjectTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String projFilter = "";
		try {
		    if (pto!=null && pto.getId()!=null) {
		    	projFilter = "and p.id = '" + pto.getId() + "' ";
		    }
		    
		    //select a user from database
			pstmt = c.prepareStatement("select p.id, p.name, a.description, a.creation_date, p.estimated_closure_date, p.budget, " +
									   "p.project_status_id, p.parent_id, pp.name as PARENT_PROJECT_NAME, ps.name as PROJECT_STATUS_NAME, p.can_alloc, " +
									   "p.repository_url, p.repository_class, p.repository_user, p.repository_pass, " +
									   "r.can_self_alloc, r.can_see_repository, r.can_see_invoice, p.allow_billable, ps.state_machine_order, " +
									   "(select distinct id from customer where id=c.id and project_id = c.project_id) as IS_CUSTOMER, " +
									   "(select distinct id from resource  where id=c.id and project_id = c.project_id) as IS_RESOURCE, " +
									   "(select distinct id from leader where id=c.id and project_id = c.project_id) as IS_LEADER " +
									   "from planning a, project_status ps, project p LEFT OUTER JOIN project pp on pp.id = p.parent_id, " +
									   "customer c LEFT OUTER JOIN resource r on r.id = c.id and r.project_id = c.project_id " +
									   "WHERE p.id = a.id " +
									     "and p.id = c.project_id " +
									     "and ps.id = p.project_status_id " +
									     "and (c.is_disable is null OR c.is_disable = 0) " +
									     "and c.id = ? and a.final_date is null " + projFilter +
									     "and p.id <> '" + ProjectTO.PROJECT_ROOT_ID + "' order by p.name");
			pstmt.setString(1, uto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ProjectTO to = this.populateObjectByResultSet(rs, true, c);
			    
			    //verify which is the user's role for each project
			    String isCustomer = getString(rs, "IS_CUSTOMER");
			    String isResource = getString(rs, "IS_RESOURCE");
			    String isLeader = getString(rs, "IS_LEADER");
			    
			    ProjectTO parent = to.getParentProject();
			    if (parent!=null) {
			    	parent.setName(getString(rs, "PARENT_PROJECT_NAME"));
			    }
			    
			    Integer canSelfAlloc = getInteger(rs, "can_self_alloc");
			    to.setCanSelfAlloc(canSelfAlloc!=null && canSelfAlloc.intValue()==1);

			    Integer canSeeRepository = getInteger(rs, "can_see_repository");
			    to.setCanSeeRepository(canSeeRepository!=null && canSeeRepository.intValue()==1);

			    Integer canSeeInvoice = getInteger(rs, "can_see_invoice");
			    to.setCanSeeInvoice(canSeeInvoice!=null && canSeeInvoice.intValue()==1);
			    
			    
			    if (isLeader!=null){
			        to.setRoleIntoProject(LeaderTO.ROLE_LEADER+"");
			    } else if (isResource!=null){
			        to.setRoleIntoProject(ResourceTO.ROLE_RESOURCE+"");
			    } else if (isCustomer!=null){
			        to.setRoleIntoProject(CustomerTO.ROLE_CUSTOMER+"");
			    } else {
			        throw new DataAccessException("current user [id:" + uto.getId() + "] contain an invalid role for the project id=" + to.getId());        
			    }
			    
			    response.addElement(to);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


    private Vector<ProjectTO> getProjectRoot(boolean leanSelect, Connection c) throws DataAccessException{
		Vector<ProjectTO> response= new Vector<ProjectTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select p.id, p.name, a.description, a.creation_date, p.estimated_closure_date, p.budget, " +
									     "p.project_status_id, p.parent_id, ps.name as PROJECT_STATUS_NAME, " +
									     "p.can_alloc, pp.name as PARENT_PROJECT_NAME, p.allow_billable, ps.state_machine_order, " +
									     "p.repository_url, p.repository_class, p.repository_user, p.repository_pass " +
									   "from planning a, project_status ps, project p LEFT OUTER JOIN project pp on pp.id = p.parent_id " +
									   "where p.id = a.id " +
									     "and ps.id = p.project_status_id ");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ProjectTO to = this.populateObjectByResultSet(rs, true, c);
			    to.setRoleIntoProject(RootTO.ROLE_ROOT+"");
			    
			    ProjectTO parent = to.getParentProject();
			    if (parent!=null && parent.getId()!=null) {
			    	String parentName = getString(rs, "PARENT_PROJECT_NAME");
			    	parent.setName(parentName);
			    }
			    
			    response.addElement(to);
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    private Vector<ProjectTO> getProjectListByParent(ProjectTO pto, boolean considerOnlyNonClosed, Connection c) throws DataAccessException{
		Vector<ProjectTO> response= new Vector<ProjectTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String finishWhere = "";
		try {
		    
			if (considerOnlyNonClosed) {
				finishWhere = " and a.final_date is null";
			}
			
			pstmt = c.prepareStatement("select p.id, p.name, a.description, a.creation_date, p.estimated_closure_date, p.budget, " +
									   "p.project_status_id, p.parent_id, p.can_alloc, p.allow_billable, " +
									   "p.repository_url, p.repository_class, p.repository_user, p.repository_pass, a.final_date " +
									   "from project p, planning a " + 
									   "where p.id = a.id and p.parent_id=?" + finishWhere);
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ProjectTO to = this.populateObjectByResultSet(rs, false, c);
			    to.setFinalDate(getTimestamp(rs, "final_date"));
			    response.addElement(to);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    private Vector<ProjectTO> getProjectListForManagement(LeaderTO uto, boolean hideClosed, Connection c) throws DataAccessException{
		Vector<ProjectTO> response= new Vector<ProjectTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String userWhere = "";
			if (!uto.getUsername().equals(RootTO.ROOT_USER)){
				userWhere = "and e.id = ? ";
			}
			
			String hideClosedWhere = "";
			if (hideClosed) {
				hideClosedWhere = "and a.final_date is null "; 
			}
			
			pstmt = c.prepareStatement("select distinct p.id, p.name, a.description, a.creation_date, p.estimated_closure_date, p.budget, " +
			        				   "p.project_status_id, p.parent_id, ps.name as PROJECT_STATUS_NAME, p.can_alloc, " +
			        				   "p.repository_url, p.repository_class, p.repository_user, p.repository_pass, " +
			        				   "p.allow_billable, ps.state_machine_order " +
			        				   "from project p, planning a, project_status ps, leader e " +
					   				   "where p.id = a.id and ps.id = p.project_status_id " + userWhere + hideClosedWhere +
					   				     "and (p.can_alloc is null or p.can_alloc='1') and p.id = e.project_id " +
					   				     "and p.id <> '" + ProjectTO.PROJECT_ROOT_ID + "' " +
					   				   "order by p.name");
			
			if (userWhere!=null && !userWhere.equals("")){
				pstmt.setString(1, uto.getId());	
			}
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ProjectTO to = this.populateObjectByResultSet(rs, true, c);
			    response.addElement(to);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    private Vector<ProjectTO> getProjectListForWork(UserTO uto, boolean isAlloc, boolean nonClosed, Connection c) throws DataAccessException{
		Vector<ProjectTO> response= new Vector<ProjectTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String allocWhere = "";
		    if (isAlloc) {
		    	allocWhere = "and (p.can_alloc is null or p.can_alloc='1') ";
		    }
		    String nonCloseWhere = "";
		    if (nonClosed) {
		    	nonCloseWhere = "and a.final_date is null ";
		    }

			pstmt = c.prepareStatement("select distinct p.id, p.name, a.description, a.creation_date, p.estimated_closure_date, p.budget, " +
			        				   "p.project_status_id, p.parent_id, ps.name as PROJECT_STATUS_NAME, p.can_alloc, " +
			        				   "p.repository_url, p.repository_class, p.repository_user, p.repository_pass, " +
			        				   "p.allow_billable, ps.state_machine_order, a.final_date " +
			        				   "from project p, planning a, project_status ps, resource r, customer cu " +
					   				   "where p.id = a.id " +
								   		 "and ps.id = p.project_status_id " +
					   				     "and r.id = ? and p.id = r.project_id and cu.id = r.id " +
					   				     "and (cu.is_disable is null or cu.is_disable=0) " +
					   				     "and p.id = cu.project_id " + nonCloseWhere + allocWhere +
					   				     "and p.id <> '" + ProjectTO.PROJECT_ROOT_ID + "' " +
					   				     "order by p.name");
			pstmt.setString(1, uto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ProjectTO to = this.populateObjectByResultSet(rs, true, c);
			    to.setFinalDate(getTimestamp(rs, "final_date"));
			    response.addElement(to);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    private void getProjectUsers(ProjectTO pto, Connection c) throws DataAccessException, BusinessException{
    	UserBUS ubus = new UserBUS(); 
        Vector allProjUsers = new Vector();
        
        //get from data base the customers related with project
        UserDelegate udel = new UserDelegate();
        Vector leaderList;
		try {
			leaderList = udel.getLeaderByProject("'" + pto.getId() + "'");
		} catch (BusinessException e) {
			throw new DataAccessException(e);
		}
        pto.setInsertLeaders(leaderList);
        allProjUsers.addAll(leaderList); 

        //get from data base the customers related with project
        ResourceDAO rdao = new ResourceDAO();
        Vector<ResourceTO> resList = ubus.getResourceByProject(pto.getId(), false, false);
        pto.setInsertResources(resList);
        allProjUsers.addAll(StringUtil.minus(resList, leaderList));

        //get from data base the customers related with project
        CustomerDAO cdao = new CustomerDAO();
        Vector custList = cdao.getCustomerListByProjectId(pto, c);
        pto.setInsertCustomers(custList);
        allProjUsers.addAll(StringUtil.minus(custList, resList));        
                
        pto.setAllocUsers(allProjUsers);
    }  
    
    
    private ProjectTO getProjectById(ProjectTO pto, boolean isLazyLoad, Connection c) throws DataAccessException{
		ProjectTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select p.id, p.name, a.description, a.creation_date, p.estimated_closure_date, p.budget, " +
					   				   "p.PROJECT_STATUS_ID, p.PARENT_ID, ps.NAME as PROJECT_STATUS_NAME, p.can_alloc, " +
					   				   "p.repository_url, p.repository_class, p.repository_user, p.repository_pass, " +
					   				   "p.allow_billable, ps.state_machine_order " +
					   				   "from project p, project_status ps, planning a " +
					   					"where p.id = a.id " +
					   					  "and ps.id = p.project_status_id " +
					   					  "and p.id=?");
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    response = this.populateObjectByResultSet(rs, true, c);
			    if (!isLazyLoad) {		    	
			    	this.getProjectUsers(response, c);	
			    }
			} 
						
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    private ProjectTO populateObjectByResultSet(ResultSet rs, boolean procAdditFields, Connection c) throws DataAccessException{
        RepositoryPolicyDAO ppdao = new RepositoryPolicyDAO();
    	ProjectTO response = new ProjectTO();
        ProjectStatusTO psto = new ProjectStatusTO();
        ProjectTO ppto = new ProjectTO();
        MetaFieldDAO mfdao = new MetaFieldDAO();
        
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setGenericTag(response.getName());
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setEstimatedClosureDate(getTimestamp(rs, "estimated_closure_date"));
        response.setCanAlloc(getString(rs, "can_alloc"));
        response.setBudget(getLong(rs, "budget"));
        
        response.setRepositoryURL(getString(rs, "repository_url"));
        response.setRepositoryClass(getString(rs, "repository_class"));
        response.setRepositoryUser(getString(rs, "repository_user"));
        response.setRepositoryPass(getString(rs, "repository_pass"));
        
        String allow = getString(rs, "allow_billable");
        if (allow!=null) {
        	response.setAllowBillable(new Boolean(allow.equals("1")));	
        }
        
        if (procAdditFields){
		    String statusName = getString(rs, "PROJECT_STATUS_NAME");
		    psto.setName(statusName);
		    
		    Integer state = getInteger(rs, "state_machine_order");
		    psto.setStateMachineOrder(state);
        }
        
        //ref with current project status (NOT NULL collumn)
        String psid = getString(rs, "project_status_id");
        psto.setId(psid);
        response.setProjectStatus(psto);

        //ref with parent project (NOT NULL collumn)
        String pprid = getString(rs, "parent_id");
        if (pprid!=null && !pprid.equals(response.getId())) {
            ppto.setId(pprid);        	
        } else {
        	ppto.setId("0");
        }
        response.setParentProject(ppto);
                
	    //get the additional fields
	    response.setAdditionalFields(afdao.getListByPlanning(response, null, c));
        
	    //get the related repository policies
	    response.setRepositoryPolicies(ppdao.getListByProject(response, c));

	    Vector<MetaFieldTO> qualif = mfdao.getQualifierByProjectId(response.getId(), c);
	    response.setQualifications(qualif);
	    
        return response;
    }
        
    
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		ResourceCapacityDAO rcdao = new ResourceCapacityDAO();
		RepositoryPolicyDAO rpdao = new RepositoryPolicyDAO();
		
		try {
		    //create a new id
		    ProjectTO pto = (ProjectTO)to;
		    String newId = this.getNewId(); 
		    pto.setId(newId);
		    
		    //insert data into parent entity (PlanningDAO)
		    super.insert(pto, c);
		    
		    //insert data of project
			pstmt = c.prepareStatement("insert into project (id, name, parent_id, project_status_id, can_alloc, " +
									   "repository_url, repository_class, repository_user, repository_pass, " +
									   "estimated_closure_date, allow_billable, budget) " +
									   "values (?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, newId);
			pstmt.setString(2, pto.getName());
			pstmt.setString(3, pto.getParentProject().getId());
			pstmt.setString(4, pto.getProjectStatus().getId());
			if (pto.getCanAlloc()!=null) {
				pstmt.setString(5, pto.getCanAlloc());			    
			} else {
			    pstmt.setNull(5, java.sql.Types.VARCHAR);
			}
			pstmt.setString(6, pto.getRepositoryURL());
			pstmt.setString(7, pto.getRepositoryClass());
			pstmt.setString(8, pto.getRepositoryUser());
			pstmt.setString(9, pto.getRepositoryPass());
			if (pto.getEstimatedClosureDate()!=null) {
				pstmt.setTimestamp(10, pto.getEstimatedClosureDate());	
			} else {
				pstmt.setNull(10, java.sql.Types.TIMESTAMP);
			}
			if (pto.getAllowBillable()!=null) {
				pstmt.setInt(11, pto.getAllowBillable().booleanValue()?1:0);	
			} else {
				pstmt.setInt(11, 0);	
			}
			if (pto.getBudget()!=null) {
				pstmt.setLong(12, pto.getBudget());	
			} else {
				pstmt.setNull(12, java.sql.Types.BIGINT);	
			}
			
			pstmt.executeUpdate();
			
		    //create and insert into data base a new Project History object
		    ProjectHistoryDAO phdao = new ProjectHistoryDAO(); 
		    ProjectHistoryTO phto = new ProjectHistoryTO();
		    phto.setProjectId(newId);
		    phto.setStatus(pto.getProjectStatus());
		    phdao.insert(phto, c);

			//save data related with customers, resource and leaders allocated into project
			CustomerDAO cdao = new CustomerDAO();
			cdao.insert(pto.getInsertCustomers(), c);
			ResourceDAO rdao = new ResourceDAO();
			rdao.insert(pto.getInsertResources(), c);
			LeaderDAO edao = new LeaderDAO();
			edao.insert(pto.getInsertLeaders(), c);

			rcdao.saveCapacity(pto.getInsertResources(), c);
			rpdao.saveByProject(pto, c);
			this.saveQualifications(pto, c);
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}         
    }
    
    
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null;
		CustomerDAO cdao = new CustomerDAO();
		ResourceDAO rdao = new ResourceDAO();
		LeaderDAO edao = new LeaderDAO();
		ResourceCapacityDAO rcdao = new ResourceCapacityDAO();
		CustomerFunctionDAO cfdao = new CustomerFunctionDAO();
		RepositoryPolicyDAO rpdao = new RepositoryPolicyDAO();

		try {

		    ProjectTO pto = (ProjectTO)to;

		    //update data into parent entity (PlanningDAO)
		    super.update(pto, c);

			pstmt = c.prepareStatement("update project set name=?, parent_id=?, project_status_id=?, can_alloc=?, " +
									   "repository_url=?, repository_class=?, repository_user=?, repository_pass=?, " +
									   "estimated_closure_date=?, allow_billable=?, budget=? " +
									   "where id=?");
			pstmt.setString(1, pto.getName());
			pstmt.setString(2, pto.getParentProject().getId());
			pstmt.setString(3, pto.getProjectStatus().getId());
			if (pto.getCanAlloc()!=null) {
				pstmt.setString(4, pto.getCanAlloc());			    
			} else {
			    pstmt.setNull(4, java.sql.Types.VARCHAR);
			}
			pstmt.setString(5, pto.getRepositoryURL());
			pstmt.setString(6, pto.getRepositoryClass());
			pstmt.setString(7, pto.getRepositoryUser());
			pstmt.setString(8, pto.getRepositoryPass());
			if (pto.getEstimatedClosureDate()!=null) {
				pstmt.setTimestamp(9, pto.getEstimatedClosureDate());	
			} else {
				pstmt.setNull(9, java.sql.Types.TIMESTAMP);
			}
			if (pto.getAllowBillable()!=null) {
				pstmt.setInt(10, pto.getAllowBillable().booleanValue()?1:0);	
			} else {
				pstmt.setInt(10, 0);	
			}			
			if (pto.getBudget()!=null) {
				pstmt.setLong(11, pto.getBudget());	
			} else {
				pstmt.setNull(11, java.sql.Types.BIGINT);	
			}			
			pstmt.setString(12, pto.getId());
			pstmt.executeUpdate();
			
			rcdao.removeByResourceList(pto.getRemoveResources(), c);
			cfdao.removeByCustomerList(pto.getRemoveCustomers(), c);
			
			edao.remove(pto.getRemoveLeaders(), c);
			rdao.remove(pto.getRemoveResources(), c);
			cdao.remove(pto.getRemoveCustomers(), c);
			
			pto.setId(pto.getId()); //force linkProjectWithAlloc() method calling
			cdao.insert(pto.getInsertCustomers(), c);			
			rdao.insert(pto.getInsertResources(), c);
			edao.insert(pto.getInsertLeaders(), c);
			
			cdao.update(pto.getUpdateCustomers(), c);
			rdao.update(pto.getUpdateResources(), c);
 
			cfdao.insertByCustomerList(pto.getInsertCustomers(), c);
			
		    //insert new Project History object into data base
		    ProjectHistoryDAO phdao = new ProjectHistoryDAO(); 
		    ProjectHistoryTO phto = new ProjectHistoryTO();
		    phto.setProjectId(pto.getId());
		    phto.setStatus(pto.getProjectStatus());
		    phdao.insert(phto, c);

			rcdao.saveCapacity(pto.getInsertResources(), c);
			rcdao.saveCapacity(pto.getUpdateResources(), c);
			rpdao.saveByProject(pto, c);
			
			this.saveQualifications(pto, c);

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }

    
	private void saveQualifications(ProjectTO pto, Connection c) throws DataAccessException {
		AdditionalFieldDAO afdao = new AdditionalFieldDAO();
		
		if (pto.getQualifications()!=null) {
			
			afdao.removeByPlanning(pto, true, c);
			
			for (MetaFieldTO mfto : pto.getQualifications()) {
				AdditionalFieldTO afto = new AdditionalFieldTO();
				afto.setMetaField(mfto);
				afto.setPlanning(pto);
				afto.setValue(mfto.getGenericTag());
				afdao.insert(afto, c);
			}				
		}		
	}


	public ProjectTO getProjectByName(String projectName) throws DataAccessException {
		ProjectTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getProjectByName(projectName, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;		
	}
	
	
	private ProjectTO getProjectByName(String projectName, Connection c) throws DataAccessException {
		ProjectTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select p.id, p.name, a.description, a.creation_date, p.estimated_closure_date, " +
					   				      "p.project_status_id, p.parent_id, ps.name as PROJECT_STATUS_NAME, p.can_alloc, " +
					   				      "p.repository_url, p.repository_class, p.repository_user, p.repository_pass, " +
					   				      "p.allow_billable, ps.state_machine_order, p.budget " +
					   				   "from project p, project_status ps, planning a " +
					   				   "where p.id = a.id " +
					   					  "and ps.id = p.project_status_id " +
					   					  "and p.name = ?");
			pstmt.setString(1, projectName);
			rs = pstmt.executeQuery();
			while (rs.next()){
			    response = this.populateObjectByResultSet(rs, true, c);
			} 

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}

}
