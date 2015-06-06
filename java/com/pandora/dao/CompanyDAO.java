package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.CompanyTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

public class CompanyDAO extends DataAccess {

	
    public Vector<CompanyTO> getList(Connection c) throws DataAccessException {
		Vector<CompanyTO> response= new Vector<CompanyTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, full_name, company_number, address, city, country, state_province from company");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    CompanyTO to = this.populateBeanByResultSet(rs);
			    response.addElement(to);
			} 
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }



	public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
		CompanyTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			CompanyTO filter = (CompanyTO)to;
			pstmt = c.prepareStatement("select id, name, full_name, company_number, address, city, country, state_province from company where id=?");
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
    
	
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			CompanyTO cto = (CompanyTO)to;
			pstmt = c.prepareStatement("insert into company (id, name, full_name, company_number, address, city, country, state_province) values (?,?,?,?,?,?,?,?)");
			pstmt.setString(1, this.getNewId());
			pstmt.setString(2, cto.getName());
			pstmt.setString(3, cto.getFullName());			
			pstmt.setString(4, cto.getCompanyNumber());
			pstmt.setString(5, cto.getAddress());
			pstmt.setString(6, cto.getCity());
			pstmt.setString(7, cto.getCountry());
			pstmt.setString(8, cto.getStateProvince());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    

    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			CompanyTO cto = (CompanyTO)to;
			pstmt = c.prepareStatement("update company set name=?, full_name=?, company_number=?, address=?, city=?, country=?, state_province=? where id=?");
			pstmt.setString(1, cto.getName());
			pstmt.setString(2, cto.getFullName());
			pstmt.setString(3, cto.getCompanyNumber());
			pstmt.setString(4, cto.getAddress());
			pstmt.setString(5, cto.getCity());
			pstmt.setString(6, cto.getCountry());
			pstmt.setString(7, cto.getStateProvince());			
			pstmt.setString(8, cto.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}
    }
    

    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			CompanyTO cto = (CompanyTO)to;
			pstmt = c.prepareStatement("delete from company where id=?");
			pstmt.setString(1, cto.getId());			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}
    }

    
    private CompanyTO populateBeanByResultSet(ResultSet rs) throws DataAccessException {
    	CompanyTO response = new CompanyTO();
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setFullName(getString(rs, "full_name"));
        response.setCompanyNumber(getString(rs, "company_number"));
        response.setAddress(getString(rs, "address"));
        response.setCity(getString(rs, "city"));
        response.setCountry(getString(rs, "country"));
        response.setStateProvince(getString(rs, "state_province"));
        return response;
	}

}
