package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.ProjectBUS;
import com.pandora.exception.DataAccessException;

/**
 * This class contain all methods to handle data related with Resource entity into data base.
 */
public class ResourceDAO extends CustomerDAO {

    /**
     * Insert a new resource into data base related with project id.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    ResourceTO rto = (ResourceTO)to;
			pstmt = c.prepareStatement("insert into resource (id, project_id, can_see_customer, " +
									  "can_self_alloc, can_see_repository, can_see_invoice) values (?,?,?,?,?,?)");
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, rto.getProject().getId());
			if (rto.getCanSeeCustomer()!=null){
			    pstmt.setInt(3, (rto.getBoolCanSeeCustomer()?1:0));    
			} else {
			    pstmt.setInt(3, 0);
			}
			if (rto.getCanSelfAlloc()!=null){
			    pstmt.setInt(4, (rto.getBoolCanSelfAlloc()?1:0));    
			} else {
			    pstmt.setInt(4, 0);
			}
			if (rto.getCanSeeRepository()!=null){
			    pstmt.setInt(5, (rto.getCanSeeRepository()?1:0));    
			} else {
			    pstmt.setInt(5, 0);
			}
			if (rto.getCanSeeInvoice()!=null){
			    pstmt.setInt(6, (rto.getCanSeeInvoice()?1:0));    
			} else {
			    pstmt.setInt(6, 0);
			}
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    /**
     * Remove a resource from data base related with project id.
     */
    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    ResourceTO rto = (ResourceTO)to;
			pstmt = c.prepareStatement("delete from resource where id=? and project_id=?");
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, rto.getProject().getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
	public void attribRootIntoProjectResource(UserTO root, ProjectTO pto) throws DataAccessException {
        Connection c = null;
		PreparedStatement pstmt = null;  
		CustomerDAO cdao = new CustomerDAO();
		try {
			
			c = getConnection(false);
			
			//insert a new customer object
			CustomerTO cto = new CustomerTO(root.getId());
			cto.setProject(pto);
			cdao.insert(cto, c);  
			
			ResourceTO rto = new ResourceTO(root.getId());
			rto.setProject(pto);
			this.insert(rto, c);
			
			c.commit();
												
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}			
	}
	
    	
    /**
     * Update a resource into data base related with project id.
     */    
    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			 
		    ResourceTO rto = (ResourceTO)to;
			pstmt = c.prepareStatement("update resource set can_see_customer=?, can_self_alloc=?, " +
										          "can_see_repository=?, can_see_invoice=? " +
									   "where id=? and project_id=?");
			if (rto.getCanSeeCustomer()!=null){
			    pstmt.setInt(1, (rto.getBoolCanSeeCustomer()?1:0));    
			} else {
			    pstmt.setInt(1, 0);
			}
			if (rto.getCanSelfAlloc()!=null){
			    pstmt.setInt(2, (rto.getBoolCanSelfAlloc()?1:0));    
			} else {
			    pstmt.setInt(2, 0);
			}
			if (rto.getCanSeeRepository()!=null){
			    pstmt.setInt(3, (rto.getCanSeeRepository()?1:0));    
			} else {
			    pstmt.setInt(3, 0);
			}
			if (rto.getCanSeeInvoice()!=null){
			    pstmt.setInt(4, (rto.getCanSeeInvoice()?1:0));    
			} else {
			    pstmt.setInt(4, 0);
			}
			pstmt.setString(5, rto.getId());
			pstmt.setString(6, rto.getProject().getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}
    }

    
    /**
     * Update a list of resources into data base related with project id. <br>
     */
    public void update(Vector vto, Connection c) throws DataAccessException {
        if (vto!=null){
            Iterator i = vto.iterator();
            while(i.hasNext()){
                ResourceTO rto = (ResourceTO)i.next();
                this.update(rto, c);
            }
        }
    }

    
    /**
     * Get a list of Resources objects based on a project id.
     */
    public Vector<ResourceTO> getResourceByProject(String projectId, boolean hideDisabled, 
    		boolean considerSubProjects) throws DataAccessException {
        Vector<ResourceTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getResourceByProject(projectId, hideDisabled, considerSubProjects, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }


    /**
     * Get a list of Resources objects based on a project id.
     * @throws  
     */
    public Vector<ResourceTO> getResourceByProject(String projectId, boolean hideDisabled, 
    		boolean considerSubProjects, Connection c) throws DataAccessException {
        Vector<ResourceTO> response= new Vector<ResourceTO>();
        ProjectBUS pbus = new ProjectBUS();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    String disabledSQL = "";
		    if (hideDisabled) {
		        disabledSQL = "and (c.is_disable is null or c.is_disable=0) ";
		    }
		    
		    String projectSQL = "and r.project_id = '" + projectId + "' ";
		    if (considerSubProjects) {
		    	projectSQL = "and r.project_id in (" + pbus.getProjectIn(projectId, true) + ") ";
		    }
		    
			pstmt = c.prepareStatement("select r.id, r.project_id, u.username, u.color, u.email, u.name, " +
			                           "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, " +
			                           "u.language, u.birth, u.auth_mode, u.permission, u.pic_file, u.final_date, " +
									   "c.pre_approve_req, r.can_see_customer, r.can_self_alloc, r.can_see_repository, r.can_see_invoice, " +
									   "c.is_disable, c.is_req_acceptable, c.can_see_tech_comment, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs  " +
									   "from resource r, tool_user u, customer c " +
									   "where r.id = u.id and c.id = u.id and u.username <> '" + RootTO.ROOT_USER + "' " +
									   	 "and r.project_id = c.project_id " + disabledSQL + projectSQL + 
									   	 "order by u.username");
			rs = pstmt.executeQuery();						
			while (rs.next()){
				response.addElement(this.populateResourceByResultSet(rs, c));
			} 

		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
		    super.closeStatement(rs, pstmt);
		}	 
		return response;        
    }

    
    /**
     * Get a list of Resources objects based on a user id.
     */
    public Vector getResourceByUser(UserTO uto) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getResourceByUser(uto, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }

    
    /**
     * Get a list of Resources objects based on a user id.
     */
    private Vector getResourceByUser(UserTO uto, Connection c) throws DataAccessException{
        Vector response= new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select r.id, r.project_id, u.username, u.color, u.email, u.name, " +
                    				   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, u.birth, u.auth_mode, u.permission, u.pic_file, u.final_date, " +			        
									   "c.pre_approve_req, r.can_see_customer, r.can_self_alloc, r.can_see_repository, r.can_see_invoice, " +
									   "c.is_disable, c.is_req_acceptable, c.can_see_tech_comment, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
									   "from resource r, tool_user u, customer c " +
									   "WHERE r.id = u.id and c.id = u.id " +
									   	 "and r.project_id = c.project_id " +
										 "AND u.id = ?");
			pstmt.setString(1, uto.getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
				response.addElement(this.populateResourceByResultSet(rs, c));
			}

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
		    super.closeStatement(rs, pstmt);
		}	 
		return response;        
    }
    
    
    /**
     * Get a specific Resource TO from data base, based on id.
     */
    public ResourceTO getFirstResourceByUserId(UserTO filter) throws DataAccessException{
        ResourceTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getFirstObjectByUserId(filter, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    /**
     * Get a specific Resource TO from data base, based on id.
     */
    private ResourceTO getFirstObjectByUserId(UserTO filter, Connection c) throws DataAccessException {
		ResourceTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select r.id, r.project_id, u.username, u.color, u.email, u.name, " +
	 				   				   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, u.birth, u.auth_mode, u.permission, u.pic_file, u.final_date, " +
									   "c.pre_approve_req, r.can_see_customer, r.can_self_alloc, r.can_see_repository, r.can_see_invoice, " +
									   "c.is_disable, c.is_req_acceptable, c.can_see_tech_comment, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
									   "from tool_user u, customer c, resource r " +
									   "WHERE r.id = u.id and c.id = u.id " +
									   	 "and r.project_id = c.project_id " +
									     "AND r.id = ?");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateResourceByResultSet(rs, c);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
		    super.closeStatement(rs, pstmt);		    
		}	 
		return response;
    }

    
	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException{
		ResourceTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    ResourceTO filter = (ResourceTO)to;
			pstmt = c.prepareStatement("select r.id, r.project_id, u.username, u.color, u.email, u.name, " +
	   				   				   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, u.birth, u.auth_mode, u.permission, u.pic_file, u.final_date, " +			        
									   "c.pre_approve_req, r.can_see_customer, r.can_self_alloc, r.can_see_repository, r.can_see_invoice, " +
									   "c.is_disable, c.is_req_acceptable, c.can_see_tech_comment, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
									   "from tool_user u, customer c, resource r " +
									   "WHERE r.id = u.id and c.id = u.id " +
									   	 "and r.project_id = c.project_id " +
									     "AND r.id = ? AND r.project_id=?");
			pstmt.setString(1, filter.getId());
			pstmt.setString(2, filter.getProject().getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = this.populateResourceByResultSet(rs, c);
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
    protected ResourceTO populateResourceByResultSet(ResultSet rs, Connection c) throws DataAccessException{
    	ResourceCapacityDAO rcdao = new ResourceCapacityDAO();
        ResourceTO response = null;
        
        //call parent class (CustomerDAO) and get all generic attributes
        CustomerTO parent = super.populateCustomerByResultSet(rs);
        response = new ResourceTO(parent);
        
        //get attributes specifics of resource.
        response.setCanSeeCustomer(getBoolean(rs, "can_see_customer"));
        response.setCanSelfAlloc(getBoolean(rs, "can_self_alloc"));
        response.setCanSeeRepository(getBoolean(rs, "can_see_repository"));
        response.setCanSeeInvoice(getBoolean(rs, "can_see_invoice"));        
        
        Vector capacityList = rcdao.getListByResourceProject(response.getId(), response.getProject().getId(), c);
        response.setResourceCapacityList(capacityList);
        
        return response;
    }

}
