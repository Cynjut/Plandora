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
			
			pstmt = c.prepareStatement("select m.id, m.name, m.type, m.project_id, m.category_id, m.field_order, m.domain, m.apply_to," +
									   "m.final_date, p.name as project_name, m.meta_form_id, m.help_content, is_mandatory, is_qualifier " +
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
		String categoryWhere = " and category_id is NULL ";
		try {

		    if (categoryId!=null && !categoryId.equals("0")) {
			    categoryWhere = " and (category_id='" + categoryId + "' OR category_id is NULL) ";  
			}

			pstmt = c.prepareStatement("select id, name, type, project_id, category_id, domain, apply_to, final_date, " +
									   "meta_form_id, help_content, field_order, is_mandatory, is_qualifier " +
									   "from meta_field " +
									   "where final_date is null and (is_qualifier is null or is_qualifier <> '1') and " +
									   "(project_id=? or project_id='0') and apply_to=?" + categoryWhere + 
									   "order by field_order");
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

	public Vector<MetaFieldTO> getQualifierByProjectId(String projectId, Connection c) throws DataAccessException {
		Vector<MetaFieldTO> response= new Vector<MetaFieldTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("select m.id, m.name, m.type, m.project_id, m.category_id, m.domain, m.apply_to, m.final_date, " +
					   			       "m.meta_form_id, m.help_content, m.field_order, m.is_mandatory, m.is_qualifier, a.value " +
					   			       "from meta_field m left outer join additional_field a on (a.meta_field_id = m.id and a.planning_id=?) " +
					   			       "where m.final_date is null and m.is_qualifier='1' and " +
					   			       "(m.project_id=? or m.project_id='0') and m.apply_to=" +  MetaFieldTO.APPLY_TO_PROJECT + 
					   				   " order by m.field_order");
			pstmt.setString(1, projectId);
			pstmt.setString(2, projectId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    MetaFieldTO mfto = this.populateReanByResultSet(rs);
			    mfto.setGenericTag(getString(rs, "value"));
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
     * Get a specific Meta Field TO from data base, based on id.
     */
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        MetaFieldTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    MetaFieldTO filter = (MetaFieldTO)to;
			pstmt = c.prepareStatement("select id, name, type, project_id, category_id, domain, apply_to, final_date, " +
									   "meta_form_id, help_content, field_order, is_mandatory, is_qualifier " +
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
									   "final_date, meta_form_id, help_content, field_order, is_mandatory, is_qualifier " +
									   "from meta_field " +
									   "where final_date is null and meta_form_id=? and (is_qualifier is null or is_qualifier <> '1') " +
									   "order by field_order");
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
									   "category_id, domain, apply_to, final_date, meta_form_id, help_content, field_order, is_mandatory, is_qualifier) " +
									   "values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
			if (mfto.getOrder()!=null) {
				pstmt.setInt(11, mfto.getOrder());			    
			} else {
			    pstmt.setNull(11, java.sql.Types.INTEGER);
			}		
			if (mfto.isMandatory() != null) {
				pstmt.setString(12, (mfto.isMandatory()?"1":"0"));			    
			} else {
			    pstmt.setNull(12, java.sql.Types.VARCHAR);
			}
			if (mfto.getIsQualifier() != null) {
				pstmt.setString(13, (mfto.getIsQualifier()?"1":"0"));			    
			} else {
			    pstmt.setNull(13, java.sql.Types.VARCHAR);
			}			
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
									   "domain=?, apply_to=?, final_date=?, meta_form_id=?, help_content=?, field_order=?, is_mandatory=?, is_qualifier=? " +
									   "where id=?");
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
			if (mfto.getOrder()!=null) {
				pstmt.setInt(10, mfto.getOrder());			    
			} else {
			    pstmt.setNull(10, java.sql.Types.INTEGER);
			}
			if (mfto.isMandatory() != null) {
				pstmt.setString(11, (mfto.isMandatory()?"1":"0"));				    
			} else {
			    pstmt.setNull(11, java.sql.Types.VARCHAR);
			}
			if (mfto.getIsQualifier() != null) {
				pstmt.setString(12, (mfto.getIsQualifier()?"1":"0"));				    
			} else {
			    pstmt.setNull(12, java.sql.Types.VARCHAR);
			}			
			pstmt.setString(13, mfto.getId());
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
			super.closeStatement(null, pstmt);
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
        response.setOrder(getInteger(rs, "field_order"));
        		
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
        
        Boolean isMandatory = getBoolean(rs, "is_mandatory");
        response.setIsMandatory(isMandatory != null? isMandatory : Boolean.FALSE);

        Boolean isQualifier = getBoolean(rs, "is_qualifier");
        response.setIsQualifier(isQualifier != null? isQualifier : Boolean.FALSE);
        
        return response;
    }
}