package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import com.pandora.ProjectTO;
import com.pandora.RepositoryPolicyTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

public class RepositoryPolicyDAO extends DataAccess {

	
    public void saveByProject(ProjectTO pto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			HashMap<String, RepositoryPolicyTO> hm = pto.getRepositoryPolicies();
			if (hm!=null) {
			    pstmt = c.prepareStatement("delete from repository_policy where project_id=?");
			    pstmt.setString(1, pto.getId());
			    pstmt.executeUpdate();
			    
			    Iterator<RepositoryPolicyTO> i = hm.values().iterator();
			    while(i.hasNext()) {
			    	RepositoryPolicyTO rpto = i.next();
			    	this.insert(rpto, c);
			    }
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
		}    	
    }

    
	public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;		 
		try {
	    	RepositoryPolicyTO rpto = (RepositoryPolicyTO)to;			
		    pstmt = c.prepareStatement("insert into repository_policy (project_id, type_policy, value) values (?, ?, ?)");
			pstmt.setString(1, rpto.getProject().getId());
			pstmt.setString(2, rpto.getPolicyType());
			pstmt.setString(3, rpto.getValue());
		    pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(null, pstmt);
		}
	}


	public HashMap<String, RepositoryPolicyTO> getListByProject(ProjectTO project, Connection c) throws DataAccessException {
		HashMap<String, RepositoryPolicyTO> response= new HashMap<String, RepositoryPolicyTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		 
		try {
		    pstmt = c.prepareStatement("select project_id, type_policy, value " +
		    						   "from repository_policy " +
		            				   "where project_id=?");
			pstmt.setString(1, project.getId());	
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    RepositoryPolicyTO rpto = this.populateObjectByResultSet(rs, c);
			    rpto.setProject(project);
			    response.put(rpto.getPolicyType(), rpto);			    	
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    

	private RepositoryPolicyTO populateObjectByResultSet(ResultSet rs,	Connection c) throws DataAccessException {
		RepositoryPolicyTO response = new RepositoryPolicyTO();
		response.setPolicyType(getString(rs, "type_policy"));
		response.setValue(getString(rs, "value"));
		return response;
	}

}
