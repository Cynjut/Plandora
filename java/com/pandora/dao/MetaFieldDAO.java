package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.MetaFieldTO;
import com.pandora.MetaFormTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 * This class contain the methods to access information about 
 * Meta Field entity into data base. 
 */
public class MetaFieldDAO extends DataAccess {

    /**
     * Get a list of all Meta Field TOs from data base.
     */
    public Vector<MetaFieldTO> getList(Connection c) throws DataAccessException {
		Vector<MetaFieldTO> response= new Vector<MetaFieldTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			
			pstmt = c.prepareStatement("select m.id, m.name, m.type, m.project_id, m.category_id, " +
									   "m.domain, m.apply_to, m.final_date, p.name as project_name, m.meta_form_id, m.help_content " +
									   "from meta_field m, project p " +
									   "where m.project_id = p.id");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    MetaFieldTO mfto = this.populateReanByResultSet(rs);
			    
			    String projName = getString(rs, "project_name"); 
			    mfto.getProject().setName(projName);
			    
			    MetaFormTO frm = mfto.getMetaform();
			    if (frm!=null) {
			        MetaFormDAO mfdao = new MetaFormDAO();
			        mfto.setMetaform((MetaFormTO)mfdao.getObject(frm, c));
			    }
			    
			    response.addElement(mfto);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * Get a list of all MetaField TOs from data base related 
     * with a specific projectId and applyTo values.
     */
    public Vector<MetaFieldTO> getListByProjectAndContainer(String projectId, String categoryId, Integer applyTo) throws DataAccessException {
        Vector<MetaFieldTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProjectAndContainer(projectId, categoryId, applyTo, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }
    
    
    private Vector<MetaFieldTO> getListByProjectAndContainer(String projectId, String categoryId, Integer applyTo, Connection c) throws DataAccessException {
		Vector<MetaFieldTO> response= new Vector<MetaFieldTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String categoryWhere = " and category_id is NULL";
		try {

		    if (categoryId!=null && !categoryId.equals("0")) {
			    categoryWhere = " and (category_id='" + categoryId + "' OR category_id is NULL)";  
			}

			pstmt = c.prepareStatement("select id, name, type, project_id, category_id, domain, apply_to, final_date, meta_form_id, help_content " +
									   "from meta_field " +
									   "where final_date is null and " +
									   "(project_id=? or project_id='0') and apply_to=?" + categoryWhere);
			pstmt.setString(1, projectId);
			pstmt.setInt(2, applyTo.intValue());
			rs = pstmt.executeQuery();			
			while (rs.next()){
			    MetaFieldTO mfto = this.populateReanByResultSet(rs);
			    response.addElement(mfto);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


    /**
     * Get a specific Meta Field TO from data base, based on id.
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        MetaFieldTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    MetaFieldTO filter = (MetaFieldTO)to;
			pstmt = c.prepareStatement("select id, name, type, project_id, category_id, domain, apply_to, final_date, meta_form_id, help_content " +
									   "from meta_field where id = ?");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();
						
			if (rs.next()){
				response = this.populateReanByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    public Vector<MetaFieldTO> getFieldByMetaForm(String metaFormId) throws DataAccessException {
        Vector<MetaFieldTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getFieldByMetaForm(metaFormId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }
    
    
    private Vector<MetaFieldTO> getFieldByMetaForm(String metaFormId, Connection c) throws DataAccessException {
		Vector<MetaFieldTO> response= new Vector<MetaFieldTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {

			pstmt = c.prepareStatement("select id, name, type, project_id, category_id, domain, apply_to, " +
									   "final_date, meta_form_id, help_content " +
									   "from meta_field " +
									   "where final_date is null and meta_form_id=?");
			pstmt.setString(1, metaFormId);
			rs = pstmt.executeQuery();			
			while (rs.next()){
			    MetaFieldTO mfto = this.populateReanByResultSet(rs);
			    response.addElement(mfto);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    
    
    /**
     * Insert a new Meta Field object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    MetaFieldTO mfto = (MetaFieldTO)to;
		    mfto.setId(this.getNewId());
					    
			pstmt = c.prepareStatement("insert into meta_field (id, name, type, project_id, " +
									   "category_id, domain, apply_to, final_date, meta_form_id, help_content) " +
									   "values (?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, mfto.getId());
			pstmt.setString(2, mfto.getName());
			pstmt.setInt(3, mfto.getType().intValue());
			pstmt.setString(4, mfto.getProject().getId());
			if (mfto.getCategory()!=null) {
				pstmt.setString(5, mfto.getCategory().getId());			    
			} else {
			    pstmt.setNull(5, java.sql.Types.VARCHAR);
			}
			pstmt.setString(6, mfto.getDomain());
			pstmt.setInt(7, mfto.getApplyTo().intValue());
			if (mfto.getDisableDate()!=null) {
				pstmt.setTimestamp(8, mfto.getDisableDate());			    
			} else {
			    pstmt.setNull(8, java.sql.Types.TIMESTAMP);
			}
			if (mfto.getMetaform()!=null) {
				pstmt.setString(9, mfto.getMetaform().getId());			    
			} else {
			    pstmt.setNull(9, java.sql.Types.VARCHAR);
			}
			pstmt.setString(10, mfto.getHelpContent());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
    
    
    /**
     * Update data of a Meta Field object from data base.
     */
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    MetaFieldTO mfto = (MetaFieldTO)to;
			pstmt = c.prepareStatement("update meta_field set name=?, type=?, project_id=?, category_id=?," +
									   "domain=?, apply_to=?, final_date=?, meta_form_id=?, help_content=? where id=?");
			pstmt.setString(1, mfto.getName());
			pstmt.setInt(2, mfto.getType().intValue());
			pstmt.setString(3, mfto.getProject().getId());
			if (mfto.getCategory()!=null) {
				pstmt.setString(4, mfto.getCategory().getId());			    
			} else {
			    pstmt.setNull(4, java.sql.Types.VARCHAR);
			}
			pstmt.setString(5, mfto.getDomain());
			pstmt.setInt(6, mfto.getApplyTo().intValue());
			if (mfto.getDisableDate()!=null) {
				pstmt.setTimestamp(7, mfto.getDisableDate());			    
			} else {
			    pstmt.setNull(7, java.sql.Types.TIMESTAMP);
			}
			if (mfto.getMetaform()!=null) {
				pstmt.setString(8, mfto.getMetaform().getId());			    
			} else {
			    pstmt.setNull(8, java.sql.Types.VARCHAR);
			}
			pstmt.setString(9, mfto.getHelpContent());
			pstmt.setString(10, mfto.getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    
    
    /**
     * Remove a Meta Field object from data base.
     */
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    MetaFieldTO mfto = (MetaFieldTO)to;
			pstmt = c.prepareStatement("delete from meta_field where id=?");
			pstmt.setString(1, mfto.getId());
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
    
    
    /**
     * Create a new TO object based on data into result set.
     */
    protected MetaFieldTO populateReanByResultSet(ResultSet rs) throws DataAccessException{
        MetaFieldTO response = new MetaFieldTO();
        response.setId(getString(rs, "id"));
        response.setType(getInteger(rs, "type"));
        response.setApplyTo(getInteger(rs, "apply_to"));
        response.setName(getString(rs, "name"));
        response.setDomain(getString(rs, "domain"));
        response.setHelpContent(getString(rs, "help_content"));
        
        String cat= getString(rs, "category_id");
        if (cat!=null) {
            response.setCategory(new CategoryTO(cat));
        }
        
        response.setDisableDate(getTimestamp(rs, "final_date"));
        response.setProject(new ProjectTO(getString(rs, "project_id")));

        String mf= getString(rs, "meta_form_id");
        if (mf!=null) {
            response.setMetaform(new MetaFormTO(mf));
        }
        
        return response;
    }

}
