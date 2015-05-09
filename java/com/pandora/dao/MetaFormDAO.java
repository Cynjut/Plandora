package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.MetaFormTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;

/**
 * 
 */
public class MetaFormDAO extends DataAccess {

    
    public Vector<MetaFormTO> getList(Connection c) throws DataAccessException {
		Vector<MetaFormTO> response= new Vector<MetaFormTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select id, name, viewable_cols, grid_row_num, " +
									   "filter_col_id, js_before_save, js_after_save, js_after_load " +
									   "from meta_form");
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    MetaFormTO to = this.populateObjectByResultSet(rs);
			    response.addElement(to);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }

    
    public TransferObject getObject(TransferObject to, Connection c)  throws DataAccessException {
        MetaFormTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    MetaFormTO mto = (MetaFormTO)to;
			pstmt = c.prepareStatement("select id, name, viewable_cols, grid_row_num, " +
									   "filter_col_id, js_before_save, js_after_save, js_after_load " +
									   "from meta_form where id=?");
			pstmt.setString(1, mto.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
			    response = this.populateObjectByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    MetaFormTO mfto = (MetaFormTO)to;
		    pstmt = c.prepareStatement("update meta_form set name=?, viewable_cols=?, grid_row_num=?, " +
									   "filter_col_id=?, js_before_save=?, js_after_save=?, js_after_load=? where id=?");
			pstmt.setString(1, mfto.getName());
			pstmt.setString(2, mfto.getViewableCols());
			pstmt.setInt(3, mfto.getGridNumber().intValue());
			pstmt.setString(4, mfto.getFilterColId());
			pstmt.setString(5, mfto.getJsBeforeSave());
			pstmt.setString(6, mfto.getJsAfterSave());
			pstmt.setString(7, mfto.getJsAfterLoad());
			pstmt.setString(8, mfto.getId());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    

    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    MetaFormTO mfto = (MetaFormTO)to;
		    mfto.setId(this.getNewId());
		    pstmt = c.prepareStatement("insert into meta_form (id, name, viewable_cols, grid_row_num, " +
									   "filter_col_id, js_before_save, js_after_save, js_after_load) values (?,?,?,?,?,?,?,?)");
			pstmt.setString(1, mfto.getId());
			pstmt.setString(2, mfto.getName());
			pstmt.setString(3, mfto.getViewableCols());
			pstmt.setInt(4, mfto.getGridNumber().intValue());
			pstmt.setString(5, mfto.getFilterColId());
			pstmt.setString(6, mfto.getJsBeforeSave());
			pstmt.setString(7, mfto.getJsAfterSave());
			pstmt.setString(8, mfto.getJsAfterLoad());
			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
    

    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    MetaFormTO mfto = (MetaFormTO)to;
			pstmt = c.prepareStatement("delete from meta_form where id=?");
			pstmt.setString(1, mfto.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    
    
    private MetaFormTO populateObjectByResultSet(ResultSet rs) throws DataAccessException{
        String id = getString(rs, "id");
        MetaFormTO response = new MetaFormTO(id);
        response.setName(getString(rs, "name"));
        response.setViewableCols(getString(rs, "viewable_cols"));
        response.setFilterColId(getString(rs, "filter_col_id"));
        response.setJsAfterLoad(getString(rs, "js_after_load"));
        response.setJsAfterSave(getString(rs, "js_after_save"));
        response.setJsBeforeSave(getString(rs, "js_before_save"));
        Integer val = getInteger(rs, "grid_row_num");
        if (val==null) {
        	response.setGridNumber(new Integer("6"));
        } else {
        	response.setGridNumber(val);	
        }
        return response;
    }
    
}
