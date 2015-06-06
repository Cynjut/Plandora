package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.AdditionalFieldTO;
import com.pandora.AdditionalTableTO;
import com.pandora.MetaFieldTO;
import com.pandora.PlanningTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.MandatoryMetaFieldDataException;


/**
 * This class contain the methods to access information about 
 * AdditionalField entity into data base.
 */
public class AdditionalFieldDAO extends DataAccess {

    /**
     * Get a list of Additional Fields from data base based on current planning object.
     */
    public Vector<AdditionalFieldTO> getListByPlanning(PlanningTO pto, Timestamp iniRange, Connection c) throws DataAccessException {
        Vector<AdditionalFieldTO> response= new Vector<AdditionalFieldTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
			
			String rangeWhere = "";
			if (iniRange!=null) {
				rangeWhere = "and pl.creation_date > ? ";
			}
			
			pstmt = c.prepareStatement("select af.planning_id, af.meta_field_id, af.value, " +
					                        "af.date_value, mf.domain, mf.name, mf.type, af.numeric_value " +
								       "from additional_field af, meta_field mf, planning pl " +
								       "where af.planning_id = pl.id and af.planning_id=? " + rangeWhere +
								       "and af.meta_field_id = mf.id and mf.final_date is null order by mf.field_order");
			pstmt.setString(1, pto.getId());
			if (iniRange!=null) {
				pstmt.setTimestamp(2, iniRange);
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    AdditionalFieldTO afto = this.populateByResultSet(rs);

			    MetaFieldTO mfto = afto.getMetaField();
			    mfto.setName(getString(rs, "name"));
			    mfto.setDomain(getString(rs, "domain"));
			    mfto.setType(getInteger(rs, "type"));

		        if (mfto.getType().intValue()==MetaFieldTO.TYPE_TABLE) {
		        	afto.setTableValues(this.getTableFields(afto.getValue(), pto.getId(), c));
		        }
			    
				response.addElement(afto); 
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    /**
     * Insert a list of Additional Fields into data base. 
     * But first, remove all additional fields from data base
     */
    public void insert(Vector<AdditionalFieldTO> addFields, PlanningTO me, Connection c) throws DataAccessException {

        //remove all additional Fields related with current Planning object
        this.removeByPlanning(me, false, c);
        
        //insert into data base all additional fields of list
        if (addFields!=null) {
            Iterator<AdditionalFieldTO> i =  addFields.iterator();
            while(i.hasNext()) {
                AdditionalFieldTO afto = i.next();
                afto.setPlanning(me);
                this.insert(afto, c);
            }
        }
    }
    
    /**
     * Remove all additional Fields related with current Planning object
     */
    public void removeByPlanning(PlanningTO pto, boolean isQualifier, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {

			String qualifConstraint = "";
			if (isQualifier) {
				qualifConstraint = " and meta_field_id in (select id from meta_field where is_qualifier='1')";	
			}

			pstmt = c.prepareStatement("delete from additional_field where planning_id=?" + qualifConstraint);
			pstmt.setString(1, pto.getId());
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from additional_table where planning_id=?" + qualifConstraint);
			pstmt.setString(1, pto.getId());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    /**
     * Insert a new Additional Field into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    AdditionalFieldTO afto = (AdditionalFieldTO)to;
		    if( afto.getMetaField().isMandatory() && (afto.getValue() == null || afto.getValue().trim().equals(""))){
		    	throw new MandatoryMetaFieldDataException(afto);
		    }
		    	
			pstmt = c.prepareStatement("insert into additional_field (planning_id, meta_field_id, value, date_value, numeric_value) values (?,?,?,?,?)");
			pstmt.setString(1, afto.getPlanning().getId());
			pstmt.setString(2, afto.getMetaField().getId());
			pstmt.setString(3, afto.getValue());
			pstmt.setTimestamp(4, afto.getDateValue());
			
			if(afto.getNumericValue() != null){
				pstmt.setFloat(5, afto.getNumericValue());
			}else{
				pstmt.setNull(5, java.sql.Types.FLOAT);
			}
			
			pstmt.executeUpdate();
			
            if (afto.getMetaField().getType().equals(MetaFieldTO.TYPE_TABLE)) {
            	Vector<AdditionalTableTO> tableVals = afto.getTableValues();
                if (tableVals!=null) {
                    Iterator<AdditionalTableTO> i =  tableVals.iterator();
                    while(i.hasNext()) {
                        AdditionalTableTO atto = i.next();
                        this.insertTable(atto, afto, c);
                    }
                }
            }
            
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    public void update(TransferObject to) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection();
			this.update(to, c);
			c.commit();
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
    }
    
    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    AdditionalFieldTO afto = (AdditionalFieldTO)to;
			pstmt = c.prepareStatement("update additional_field set value = ?, date_value = ?, numeric_value = ? where planning_id = ? and meta_field_id = ?");
			
			pstmt.setString(1, afto.getValue());
			pstmt.setTimestamp(2, afto.getDateValue());
			
			if(afto.getNumericValue() != null){
				pstmt.setFloat(3, afto.getNumericValue());
			}else{
				pstmt.setNull(3, java.sql.Types.FLOAT);
			}
			
			pstmt.setString(4, afto.getPlanning().getId());
			pstmt.setString(5, afto.getMetaField().getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    

    public void insertTable(AdditionalTableTO atto, AdditionalFieldTO afto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("insert into additional_table (planning_id, meta_field_id, " +
												"line, col, value, date_value) values (?,?,?,?,?,?)");
			pstmt.setString(1, afto.getPlanning().getId());
			pstmt.setString(2, afto.getMetaField().getId());			
			pstmt.setInt(3, atto.getLine().intValue());
			pstmt.setInt(4, atto.getCol().intValue());
			pstmt.setString(5, atto.getValue());
			pstmt.setTimestamp(6, atto.getDateValue());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }
    
    private Vector<AdditionalTableTO> getTableFields(String tableId, String planningId, Connection c) throws DataAccessException {
		Vector<AdditionalTableTO> response= new Vector<AdditionalTableTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select planning_id, meta_field_id, line, col, value, date_value " +
					"from additional_table where meta_field_id=? and planning_id=?");
			pstmt.setString(1, tableId);
			pstmt.setString(2, planningId);
			rs = pstmt.executeQuery();			
			while (rs.next()){
			    AdditionalTableTO atto = this.populateAddTableByResultSet(rs);
			    response.addElement(atto);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    
    
    /**
     * Create a new TO object based on data into result set.
     */
    protected AdditionalFieldTO populateByResultSet(ResultSet rs) throws DataAccessException{
        AdditionalFieldTO response = new AdditionalFieldTO();
        
        PlanningTO pto = new PlanningTO();
        pto.setId(getString(rs, "planning_id"));

        MetaFieldTO mfto = new MetaFieldTO(getString(rs, "meta_field_id"));

        response.setMetaField(mfto);
        response.setPlanning(pto);
        response.setValue(getString(rs, "value"));
        response.setDateValue(getTimestamp(rs, "date_value"));
        response.setNumericValue(getFloat(rs, "numeric_value"));
        
        return response;
    }
    
    protected AdditionalTableTO populateAddTableByResultSet(ResultSet rs) throws DataAccessException{    
    	AdditionalTableTO response = new AdditionalTableTO();
    	response.setCol(getInteger(rs, "col"));
    	response.setLine(getInteger(rs, "line"));
    	response.setValue(getString(rs, "value"));
    	response.setDateValue(getTimestamp(rs, "date_value"));
    	return response;
    }
    
}
