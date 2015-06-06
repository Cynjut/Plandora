package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.ProjectTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;

/**
 * This class contain all methods to handle data related with Customer entity into data base. 
 */
public class CustomerDAO extends UserDAO {
    
    /**
     * Check into data base if a list of Customers exists for a specific project
     */
    public boolean checkCustomerExists(Vector customerList, ProjectTO parent) throws DataAccessException{
        boolean response = false;
        Connection c = null;
		try {
			c = getConnection();
			response = this.checkCustomerExists(customerList, parent, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    /**
     * Get a specific Customer TO from data base, based on id.
     */
    public CustomerTO getFirstCustomerByUserId(UserTO filter) throws DataAccessException{
        CustomerTO response = null;
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
     * Check into data base if a list of Customers exists for a specific project
     */
    private boolean checkCustomerExists(Vector customerList, ProjectTO parent, Connection c) throws DataAccessException {
        boolean response = false;
        String where = "";
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    
		    //create a 'where' clause based on list of customer objects		    
		    Iterator i = customerList.iterator();
		    while(i.hasNext()){
		        if (where.equals("")){
		            where += "AND (";
		        } else {
		            where += " OR ";
		        }
		        UserTO uto = (UserTO)i.next();
		        where += "ID='" + uto.getId() + "'";
		    }
		    where += ")";
		    
		    //execute query...
			pstmt = c.prepareStatement("select id, project_id from customer " +
									   "where project_id = ? " + where);
			pstmt.setString(1, parent.getId());
			rs = pstmt.executeQuery();
			int rowNumber = 0;
			while (rs.next()){
			    rowNumber++;
			} 
			
			response = (rowNumber==customerList.size()); 
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get a specific Customer TO from data base, based on id.
     */
    private CustomerTO getFirstObjectByUserId(UserTO filter, Connection c) throws DataAccessException {
		CustomerTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select c.id, c.project_id, u.username, u.color, u.email, u.name, " +
									   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, " +
									   "u.birth, u.company_id, u.auth_mode, u.permission, u.pic_file, u.final_date, u.creation_date, " +
									   "c.is_disable, c.can_see_tech_comment, c.is_req_acceptable, c.pre_approve_req, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
									   "from tool_user u, customer c " +
									   "WHERE c.ID = u.ID " +
									     "AND c.ID = ?");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();
						
			//Prepare data to return
			if (rs.next()){
				response = this.populateCustomerByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    /**
     * Get a list of customer objects based on project id.
     */
    public Vector<CustomerTO> getCustomerListByProjectId(ProjectTO filter) throws DataAccessException{
        Vector<CustomerTO> response = new Vector<CustomerTO>();
        Connection c = null;
		try {
			c = getConnection();
			response = this.getCustomerListByProjectId(filter, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    
    /**
     * Get a list of Customers objects based on a user id.
     */
    public Vector<CustomerTO> getCustomerByUser(UserTO uto) throws DataAccessException {
        Vector<CustomerTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getCustomerByUser(uto, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    } 

    
    /**
     * Get a list of Customers objects based on a user id.
     */
    private Vector<CustomerTO> getCustomerByUser(UserTO uto, Connection c) throws DataAccessException{
        Vector<CustomerTO> response= new Vector<CustomerTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select c.id, c.project_id, u.username, u.color, u.email, u.name, " +
			        				   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, " +
			        				   "u.birth, u.company_id, u.auth_mode, u.permission, u.pic_file, u.final_date, u.creation_date, " +
									   "c.is_disable, c.can_see_tech_comment, c.is_req_acceptable, c.pre_approve_req, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
									   "from tool_user u, customer c " +
									   "WHERE c.ID = u.ID " +
										 "AND u.ID = ?");
			pstmt.setString(1, uto.getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
				response.addElement(this.populateCustomerByResultSet(rs));
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;        
    }

    
    /**
     * Get a list of customer objects based on project id.
     */
    public Vector<CustomerTO> getCustomerListByProjectId(ProjectTO filter, Connection c) throws DataAccessException{
        Vector<CustomerTO> response= new Vector<CustomerTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {		    
			pstmt = c.prepareStatement("select c.id, c.project_id, u.username, u.color, u.email, u.name, " +
									   "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, " +
									   "u.birth, u.company_id, u.auth_mode, u.permission, u.pic_file, u.final_date, u.creation_date, " +
									   "c.is_disable, c.can_see_tech_comment, c.is_req_acceptable, c.pre_approve_req, " +
									   "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
									   "from customer c, tool_user u " +
									   "where c.id = u.id " + //and u.username <> '" + RootTO.ROOT_USER + "' " +
										 "and c.project_id = ? " +
									   "order by u.username");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
				response.addElement(this.populateCustomerByResultSet(rs));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;        
    }

    
    public Vector<CustomerTO> getCanOtherReqCustomerList(UserTO uto, Connection c) throws DataAccessException{
        Vector<CustomerTO> response= new Vector<CustomerTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    String sql = "select c.id, c.project_id, u.username, u.color, u.email, u.name, " +
						    "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, " +
						    "u.birth, u.company_id, u.auth_mode, u.permission, u.pic_file, u.final_date, u.creation_date, " +
						    "c.is_disable, c.can_see_tech_comment, c.is_req_acceptable, c.pre_approve_req, " +
						    "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
						 "from customer c, tool_user u " +
						 "where u.id = c.id " +
						   "and c.project_id in (select distinct project_id from customer where id=? and can_see_other_reqs=1)";
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, uto.getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
			    CustomerTO cto = this.populateCustomerByResultSet(rs);			    
				response.addElement(cto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
   
    /**
     * Get the customer object from database.
     */
	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException{
		CustomerTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    CustomerTO filter = (CustomerTO)to;
			pstmt = c.prepareStatement("select c.id, c.project_id, u.username, u.color, u.email, u.name, " +
					   				     "u.phone, u.password, u.department_id, u.area_id, u.function_id, u.country, u.language, " +
					   				     "u.birth, u.company_id, u.auth_mode, u.permission, u.pic_file, u.final_date, u.creation_date, " +
					   				     "is_disable, c.can_see_tech_comment, c.is_req_acceptable, c.pre_approve_req, " +
					   				     "c.can_see_discussion, c.can_see_other_reqs, c.can_open_otherowner_reqs " +
					   				   "from customer c, tool_user u " +
					   				   "where c.id = u.id " +
						 			     "and c.id = ? and c.project_id=?");
			pstmt.setString(1, filter.getId());
			pstmt.setString(2, filter.getProject().getId());
			rs = pstmt.executeQuery();						
			while (rs.next()){
			    response = this.populateCustomerByResultSet(rs);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}
    
    /**
     * Update a resource into data base related with project id.
     */    
    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    CustomerTO cto = (CustomerTO)to;
			pstmt = c.prepareStatement("update customer set is_disable=?, is_req_acceptable=?, " +
									   "can_see_tech_comment=?, pre_approve_req=?, can_see_discussion=?, " +
									   "can_see_other_reqs=?, can_open_otherowner_reqs=? " +
									   "where id=? and project_id=?");
		    pstmt.setInt(1, (cto.getBoolIsDisabled()?1:0));
		    pstmt.setInt(2, (cto.getBoolIsReqAcceptable()?1:0));
		    pstmt.setInt(3, (cto.getBoolCanSeeTechComments()?1:0));

		    pstmt.setInt(4, cto.getIntPreApproveReq());
		    pstmt.setInt(5, cto.getIntCanSeeDiscussion());
		    pstmt.setInt(6, cto.getIntCanSeeOtherReqs());
		    pstmt.setInt(7, cto.getIntCanOpenOtherOwnerReq());
		    
			pstmt.setString(8, cto.getId());
			pstmt.setString(9, cto.getProject().getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}
    }

	
    /**
     * Insert a new customer into data base related with project id.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			
		    CustomerTO cto = (CustomerTO)to;
			pstmt = c.prepareStatement("insert into customer (id, project_id, is_disable, " +
									   "is_req_acceptable, can_see_tech_comment, pre_approve_req, " +
									   "can_see_discussion, can_see_other_reqs, can_open_otherowner_reqs) " +
									   "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			pstmt.setString(1, cto.getId());
			pstmt.setString(2, cto.getProject().getId());
			pstmt.setInt(3, (cto.getBoolIsDisabled()?1:0));
			pstmt.setInt(4, (cto.getBoolIsReqAcceptable()?1:0));
			pstmt.setInt(5, (cto.getBoolCanSeeTechComments()?1:0));
			pstmt.setInt(6, cto.getIntPreApproveReq());
			pstmt.setInt(7, cto.getIntCanSeeDiscussion());
			pstmt.setInt(8, cto.getIntCanSeeOtherReqs());
			pstmt.setInt(9, cto.getIntCanOpenOtherOwnerReq());
			
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    /**
     * Insert a list of new user into data base related with project id. <br>
     * This method is used by insert resource and leader too.
     */
    public void insert(Vector vto, Connection c) throws DataAccessException {
        if (vto!=null){
            Iterator i = vto.iterator();
            while(i.hasNext()){
                UserTO uto = (UserTO)i.next();
                this.insert(uto, c);
            }
        }
    }
    

    /**
     * Remove a customer from data base related with project id.
     */
    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    CustomerTO cto = (CustomerTO)to;
			pstmt = c.prepareStatement("delete from customer where id=? and project_id=?");
			pstmt.setString(1, cto.getId());
			pstmt.setString(2, cto.getProject().getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    
    /**
     * Remove a list of users from data base related with project id. <br>
     * This method is used by remove resource and leader too.
     */
    public void remove(Vector vto, Connection c) throws DataAccessException {
        if (vto!=null){
            Iterator i = vto.iterator();
            while(i.hasNext()){
                UserTO uto = (UserTO)i.next();
                this.remove(uto, c);
            }
        }
    }
    

    /**
     * Update a list of customers into data base related with project id. <br>
\     */
    public void update(Vector vto, Connection c) throws DataAccessException {
        if (vto!=null){
            Iterator i = vto.iterator();
            while(i.hasNext()){
                CustomerTO cto = (CustomerTO)i.next();
                this.update(cto, c);
            }
        }
    }
    
    
    /**
     * Create a new TO object based on data into result set.
     */
    protected CustomerTO populateCustomerByResultSet(ResultSet rs) throws DataAccessException{
    	CustomerFunctionDAO cfdao = new CustomerFunctionDAO();
        CustomerTO response = null;
        
        //call parent class (UserDAO) and get all generic attributes
        UserTO parent = super.populateUserByResultSet(rs);
        response = new CustomerTO(parent);

        //get attributes specifics of customer.
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        response.setIsDisabled(getBoolean(rs, "is_disable"));
        response.setIsReqAcceptable(getBoolean(rs, "is_req_acceptable"));
        response.setCanSeeTechComments(getBoolean(rs, "can_see_tech_comment"));
        response.setPreApproveReq(getBoolean(rs, "pre_approve_req"));
        
        response.setCanSeeDiscussion(getBoolean(rs, "can_see_discussion"));
        response.setCanSeeOtherReqs(getBoolean(rs, "can_see_other_reqs"));
        response.setCanOpenOtherOwnerReq(getBoolean(rs, "can_open_otherowner_reqs"));
        
        Vector roleList = cfdao.getListByCustomerProject(response.getId(), response.getProject().getId());
        response.setRoles(roleList);
        
        return response;
    }


}
