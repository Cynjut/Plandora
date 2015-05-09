package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CustomerFunctionTO;
import com.pandora.CustomerTO;
import com.pandora.FunctionTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

public class CustomerFunctionDAO extends DataAccess {

    public Vector getListByCustomerProject(String customerId, String projectId) throws DataAccessException {
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByCustomerProject(customerId, projectId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }

    
    public TransferObject getObject(TransferObject to, Connection c)
			throws DataAccessException {
    	CustomerFunctionTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			CustomerFunctionTO cfto = (CustomerFunctionTO)to;
			pstmt = c.prepareStatement("select customer_id, project_id, function_id, creation_date " + 
									   "from customer_function " + 
									   "where customer_id=? and project_id=? and function_id=?"); 
			pstmt.setString(1, cfto.getCustomer().getId());
			pstmt.setString(2, cfto.getCustomer().getProject().getId());
			pstmt.setString(3, cfto.getFunct().getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateObjectByResultSet(rs);
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


	public Vector getListByCustomerProject(String customerId, String projectId, Connection c)  throws DataAccessException {
    	Vector response = new Vector();
    	FunctionDAO fdao = new FunctionDAO();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		HashMap hm = new HashMap();
		try {						
			pstmt = c.prepareStatement("select c.customer_id, c.project_id, c.function_id, c.creation_date, f.name " + 
									   "from function f, customer_function c " + 
									   "where c.function_id = f.id and c.customer_id=? and c.project_id=?"); 
			pstmt.setString(1, customerId);
			pstmt.setString(2, projectId);	
			rs = pstmt.executeQuery();
			while (rs.next()){
				CustomerFunctionTO cfto = this.populateObjectByResultSet(rs);
				FunctionTO fto = cfto.getFunct();
				fto.setName(getString(rs, "name"));
			    response.add(cfto);
			    hm.put(fto.getId(), fto.getId());
			}
			
			Vector flist = fdao.getList(c);
			Iterator i = flist.iterator();
			while(i.hasNext()) {
				FunctionTO fto = (FunctionTO)i.next();
				if (hm.get(fto.getId())==null) {
					CustomerFunctionTO cfto = new CustomerFunctionTO();
					cfto.setFunct(fto);
					response.add(cfto);
				}
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

    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			CustomerFunctionTO cfto = (CustomerFunctionTO)to;
			pstmt = c.prepareStatement("insert into customer_function(customer_id, project_id, function_id, creation_date) " +
					   				   "values (?,?,?,?)");
			pstmt.setString(1, cfto.getCustomer().getId());
			pstmt.setString(2, cfto.getCustomer().getProject().getId());
			pstmt.setString(3, cfto.getFunct().getId());
			pstmt.setTimestamp(4, cfto.getCreationDate());
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
    
    
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			CustomerFunctionTO cfto = (CustomerFunctionTO)to;
	        pstmt = c.prepareStatement("delete from customer_function " +
	        						   "where customer_id=? and project_id=? and function_id=?"); 
			pstmt.setString(1, cfto.getCustomer().getId());
			pstmt.setString(2, cfto.getCustomer().getProject().getId());
			pstmt.setString(3, cfto.getFunct().getId());
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

    
    public void insertByCustomerList(Vector insertCustomers, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		CustomerDAO cdao = new CustomerDAO();
		try {
			if (insertCustomers!=null) {
			    Iterator i = insertCustomers.iterator();
			    while(i.hasNext()) {
			    	CustomerTO cto = (CustomerTO)i.next();
			    	cto = (CustomerTO) cdao.getObject(cto, c);
			    	
			    	CustomerFunctionTO cfto = new CustomerFunctionTO();
		    		cfto.setCustomer(cto);
		    		cfto.setFunct(cto.getFunction());
		    		cfto.setCreationDate(DateUtil.getNow());
			    	
			    	Object buff = this.getObject(cfto, c);
			    	if (buff==null) {
			    		this.insert(cfto, c);
			    	}
			    }				
			}
			
		}finally{
			try {
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}                   	
    }
    
    
	public void removeByCustomerList(Vector removeCustomers, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			if (removeCustomers!=null) {
			    Iterator i = removeCustomers.iterator();
			    while(i.hasNext()) {
			    	CustomerTO cto = (CustomerTO)i.next();
				    pstmt = c.prepareStatement("delete from customer_function where customer_id=? " +
											   "and project_id=?");
					pstmt.setString(1, cto.getId());
					pstmt.setString(2, cto.getProject().getId());
					pstmt.executeUpdate();
			    }				
			}
			
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
	
	
    private CustomerFunctionTO populateObjectByResultSet(ResultSet rs) throws DataAccessException{
    	CustomerFunctionTO response = new CustomerFunctionTO();
    	
    	FunctionTO fto = new FunctionTO(getString(rs, "function_id"));
    	response.setFunct(fto);

    	String cid = getString(rs, "customer_id");
    	if (cid!=null) {
        	String pid = getString(rs, "project_id");    		
        	CustomerTO cto = new CustomerTO(cid);
        	cto.setProject(new ProjectTO(pid));
        	response.setCustomer(cto);
    	}
    	
    	response.setCreationDate(getTimestamp(rs, "creation_date"));
    	
    	return response;
    }

}
