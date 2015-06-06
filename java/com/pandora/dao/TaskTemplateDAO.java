package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.NodeTemplateTO;
import com.pandora.TemplateTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

public class TaskTemplateDAO extends PlanningDAO {
	
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        TemplateTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			TemplateTO filter = (TemplateTO)to;
		    pstmt = c.prepareStatement("select t.id, t.name, t.deprecated_date, t.root_node_id, " +
		    							"p.description, p.creation_date, p.final_date, t.category_id, c.name as CATEG_NAME " +
		    						   "from planning p, template t LEFT OUTER JOIN category c on c.id = t.category_id " +
		    						   "where t.id = p.id and t.id=?");		
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateBeanByResultSet(rs);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
	
    public Vector<TemplateTO> getListByProject(String projectId) throws DataAccessException { 
        Vector<TemplateTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProject(projectId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }
    

    public TemplateTO getObjectByInstance(String instanceId) throws DataAccessException { 
        TemplateTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getObjectByInstance(instanceId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }

    
    public Vector<TemplateTO> getListByProject(String projectId, Connection c) throws DataAccessException {
		Vector<TemplateTO> response= new Vector<TemplateTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select t.id, t.name, t.deprecated_date, t.root_node_id, " +
									    "p.description, p.creation_date, p.final_date, t.category_id, c.name as CATEG_NAME " +
									    "from planning p, node_template nt, template t LEFT OUTER JOIN category c on c.id = t.category_id " +
				   					    "where t.id = p.id and nt.id = t.root_node_id " + (projectId==null?"": "and (nt.project_id=? or nt.project_id is null) ") + 
				   					    "order by t.name");
		    if (projectId!=null) {
		    	pstmt.setString(1, projectId);	
		    }
			rs = pstmt.executeQuery();
			while (rs.next()){
			    TemplateTO ttto = this.populateBeanByResultSet(rs);
			    response.addElement(ttto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    private TemplateTO getObjectByInstance(String instanceId, Connection c) throws DataAccessException {
    	TemplateTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("select t.id, t.name, t.deprecated_date, t.root_node_id, " +
									   "p.description, p.creation_date, p.final_date, t.category_id, c.name as CATEG_NAME " +
									   "from planning p, template t LEFT OUTER JOIN category c on c.id = t.category_id " +
				   					   "where t.id = p.id " +
				   					   "and t.id in (select distinct template_id from custom_node_template where instance_id=?)");
			pstmt.setInt(1, Integer.parseInt(instanceId));	
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateBeanByResultSet(rs);
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
    
    
    
    private TemplateTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        String id = getString(rs, "id");
        
        TemplateTO response = new TemplateTO(id);
        response.setName(getString(rs, "name"));
        response.setGenericTag(response.getName());
        
        Timestamp buff = getTimestamp(rs, "deprecated_date");
        if (buff!=null) {
        	response.setDeprecatedDate(buff);	
        }
        
        String buff2 = getString(rs, "root_node_id");
        if (buff2!=null && !buff2.trim().equals("")) {
        	response.setRootNode(new NodeTemplateTO(buff2));	
        }

        String buff3 = getString(rs, "category_id");
        if (buff3!=null && !buff3.trim().equals("")) {
        	CategoryTO cto = new CategoryTO(buff3);
        	cto.setName(getString(rs, "CATEG_NAME"));
        	response.setCategory(cto);	
        }

        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        
        return response;
    }    

}
