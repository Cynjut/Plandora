package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.OccurrenceFieldTO;
import com.pandora.OccurrenceHistoryTO;
import com.pandora.OccurrenceTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.bus.occurrence.IterationOccurrence;
import com.pandora.bus.occurrence.Occurrence;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 */
public class OccurrenceDAO extends PlanningDAO {

	public Collection<OccurrenceTO> getIterationListByProject(String projectId) throws DataAccessException {
        Vector<OccurrenceTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getIterationListByProject(projectId, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
	}

	
    public Vector<OccurrenceTO> getListByProjectId(String projectId, boolean hideClosed) throws DataAccessException { 
        Vector<OccurrenceTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProjectId(projectId, hideClosed, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;        
    }

    public Vector getListUntilID(String initialId, String finalId) throws DataAccessException { 
        Vector response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListUntilID(initialId, finalId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }  

    
	public void saveKpiLink(String kpiId, String weight, String id) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection();
			this.saveKpiLink(kpiId, weight, id, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
	}

	
	public void removeKpiLink(String kpiId, String id) throws DataAccessException {
        Connection c = null;
		try {
			c = getConnection();
			this.removeKpiLink(kpiId, id, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
	}

	
    private void removeKpiLink(String kpiId, String id, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("delete from occurrence_kpi where report_id=? and occurrence_id=?");
			pstmt.setString(1, kpiId);
			pstmt.setString(2, id);
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

	private void saveKpiLink(String kpiId, String weight, String id, Connection c) throws DataAccessException {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		boolean alreadyLinked = false;
		try {	
			pstmt2 = c.prepareStatement("select report_id from occurrence_kpi where report_id=? and occurrence_id=?");
			pstmt2.setString(1, kpiId);
			pstmt2.setString(2, id);
			rs = pstmt2.executeQuery();
			if (rs.next()){
				alreadyLinked = true;
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt2);
		}	 
		
		if (!alreadyLinked) {
			try {
			    pstmt = c.prepareStatement("insert into occurrence_kpi (report_id, occurrence_id, creation_date, weight) values (?, ?, ?, ?)");
				pstmt.setString(1, kpiId);
				pstmt.setString(2, id);
				pstmt.setTimestamp(3, DateUtil.getNow());
				pstmt.setInt(4, Integer.parseInt(weight));				
				pstmt.executeUpdate();			
			} catch (SQLException e) {
				throw new DataAccessException(e);
			}finally{
				super.closeStatement(null, pstmt);
			}			
		}
	}

	
	private Vector getListUntilID(String initialId, String finalId, Connection c) throws DataAccessException{
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    String sql = "select o.id, o.source, o.project_id, o.name, o.occurrence_status, " +
					       "o.occurrence_status_label, o.visible, o.loc, p.description, p.creation_date, p.final_date " +
 				         "from occurrence o, planning p " +
 				         "where (o.visible is null or o.visible='1') " +
 				           "and o.id = p.id and o.ID > '" + initialId + "' and o.ID <= '" + finalId + "'";		
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()){
			    OccurrenceTO oto = this.populateObjectByResultSet(rs);
			    response.addElement(oto);
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
    
    
    public Vector<OccurrenceTO> getListByProjectId(String projectId, boolean hideClosed, Connection c) throws DataAccessException {
		Vector<OccurrenceTO> response= new Vector<OccurrenceTO>();
		ResultSet rs = null;
		String hideClosedWhere = "";
		PreparedStatement pstmt = null; 
		try {
			
			if (hideClosed) {
				hideClosedWhere = " and o.occurrence_status <> ? and o.occurrence_status <> ? " +
						"and o.occurrence_status <> ?";
			}

		    pstmt = c.prepareStatement("select o.id, o.source, o.project_id, o.name, o.occurrence_status, " +
					   "o.occurrence_status_label, o.visible, o.loc, p.description, p.creation_date, " +
					   "p.final_date, pr.name as PROJECT_NAME " +
    				   "from occurrence o, planning p, project pr " +
    				   "where o.id = p.id and o.project_id = pr.id and o.project_id=?" + hideClosedWhere);		
		    pstmt.setString(1, projectId);
			if (hideClosed) {
				pstmt.setString(2, Occurrence.STATE_FINAL_1);
				pstmt.setString(3, Occurrence.STATE_FINAL_2);
				pstmt.setString(4, Occurrence.STATE_FINAL_3);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    OccurrenceTO oto = this.populateObjectByResultSet(rs);
			    
			    ProjectTO pto = oto.getProject();
			    pto.setName(rs.getString("PROJECT_NAME"));
			    
			    response.addElement(oto);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    
    
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        OccurrenceTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		
		try {

		    OccurrenceTO filter = (OccurrenceTO)to;
		    pstmt = c.prepareStatement("select o.id, o.source, o.project_id, o.name, o.occurrence_status, pr.name as PROJECT_NAME, " +
		    						   "o.occurrence_status_label, o.visible, o.loc, p.description, p.creation_date, p.final_date " +
				       				   "from occurrence o, planning p, project pr " +
				       				   "where o.id = ? and o.id = p.id and pr.id = o.project_id");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateObjectByResultSet(rs);
				if (response!=null) {
				    response.setFields(this.getFieldList(response, c));    
				}
				
			    ProjectTO pto = response.getProject();
			    pto.setName(getString(rs, "PROJECT_NAME"));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    private Vector getFieldList(OccurrenceTO oto, Connection c) throws DataAccessException {
		Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
			pstmt = c.prepareStatement("select occurrence_id, field, value, date_value from occurrence_field " +
					                   "where occurrence_id=?");
			pstmt.setString(1, oto.getId());			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    OccurrenceFieldTO ofto = this.populateObjectByResultSet(oto, rs);
			    response.addElement(ofto);
			} 
		
		} catch (Exception e) {
		    throw new DataAccessException(e);

		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }   

    
    public Vector<OccurrenceTO> getIterationListByProject(String projectId, Connection c) throws DataAccessException {
		Vector<OccurrenceTO> response = new Vector<OccurrenceTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    pstmt = c.prepareStatement("select o.id, o.source, o.project_id, o.name, " +
		    						         "o.occurrence_status, o.occurrence_status_label, " +
		    						         "o.visible, o.loc, p.description, p.creation_date, " +
		    						         "p.final_date, of.value, of.date_value " +
					                   "from occurrence o, planning p, occurrence_field of " +
 				   					   "where o.id = p.id and o.id = of.occurrence_id " +
 				   					     "and o.project_id = ? and o.source = ? and of.field = ?" +
 				   					   "order by of.date_value");
			pstmt.setString(1, projectId);
			pstmt.setString(2, IterationOccurrence.class.getName());
			pstmt.setString(3, IterationOccurrence.ITERATION_FINAL_DATE);
			rs = pstmt.executeQuery();
			while (rs.next()){
				OccurrenceTO oto = this.populateObjectByResultSet(rs);
				if (response!=null) {
				    oto.setFields(this.getFieldList(oto, c));
				    response.addElement(oto);
				}
			} 
		
		} catch (Exception e) {
		    throw new DataAccessException(e);

		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}    
    
    
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    OccurrenceTO oto = (OccurrenceTO)to;
		    
		    super.update(oto, c);
		    
		    pstmt = c.prepareStatement("update occurrence set source=?, project_id=?, name=?, " +
									   "occurrence_status=?, occurrence_status_label=?, visible=?, loc=? " +
									   "where id=?");
			pstmt.setString(1, oto.getSource());
			pstmt.setString(2, oto.getProject().getId());
			pstmt.setString(3, oto.getName());
			pstmt.setString(4, oto.getStatus());
			pstmt.setString(5, oto.getStatusLabel());
			pstmt.setString(6, (oto.isVisible()?"1":"0"));
			pstmt.setString(7, oto.getLocale().toString());
			pstmt.setString(8, oto.getId());			
			pstmt.executeUpdate();
					
			//save fields related to the occurrence
			this.updateFieldList(oto, c);

		    //create and insert into data base a new Occurrence History object
			this.populateHistory(oto, c);
			
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
    

    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    OccurrenceTO oto = (OccurrenceTO)to;
		    oto.setId(this.getNewId());

		    //insert data into parent entity (PlanningDAO)
		    super.insert(oto, c);		    
		    
			pstmt = c.prepareStatement("insert into occurrence(id, name, source, project_id, " +
					   				   "occurrence_status, occurrence_status_label, visible, loc) " +
					   				   "values (?,?,?,?,?,?,?,?)");
			pstmt.setString(1, oto.getId());
			pstmt.setString(2, oto.getName());
			pstmt.setString(3, oto.getSource());
			pstmt.setString(4, oto.getProject().getId());
			pstmt.setString(5, oto.getStatus());
			pstmt.setString(6, oto.getStatusLabel());
			pstmt.setString(7, (oto.isVisible()?"1":"0"));
			pstmt.setString(8, oto.getLocale().toString());
			pstmt.executeUpdate();
			
			//save fields related to the occurrence
			this.updateFieldList(oto, c);

		    //create and insert into data base a new Occurrence History object
			this.populateHistory(oto, c);
			
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
		    
		    OccurrenceTO oto = (OccurrenceTO)to;
		    
	        pstmt = c.prepareStatement("delete from occurrence_history where occurrence_id=?");
			pstmt.setString(1, oto.getId());
			pstmt.executeUpdate();
		    
	        //remove all fields related to the occurrence
	        this.removeFieldList(oto, c);
		    	        
			pstmt = c.prepareStatement("delete from plan_relation where plan_related_id=? or planning_id=?");
			pstmt.setString(1, oto.getId());
			pstmt.setString(2, oto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from additional_field where planning_id=?");
			pstmt.setString(1, oto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from occurrence where id=?");
			pstmt.setString(1, oto.getId());
			pstmt.executeUpdate();
						
			pstmt = c.prepareStatement("delete from planning where id=?");
			pstmt.setString(1, oto.getId());			
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
    
    
    private void populateHistory(OccurrenceTO oto, Connection c) throws DataAccessException{
        OccurrenceHistoryDAO ohdao = new OccurrenceHistoryDAO();
        
	    OccurrenceHistoryTO ohto = new OccurrenceHistoryTO();
	    ohto.setContent(oto.getFieldToString());
	    ohto.setCreationDate(DateUtil.getNow());
	    ohto.setOccurrenceId(oto.getId());
	    ohto.setOccurrenceStatus(oto.getStatus());
	    ohto.setOccurrenceStatusLabel(oto.getStatusLabel());
	    ohto.setUser(oto.getHandler());
	    
        ohdao.insert(ohto, c);	        
    }
    
    private void updateFieldList(OccurrenceTO oto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    Vector fields = oto.getFields();
		    if (fields!=null && !fields.isEmpty()) {		    
		    	
		        //remove all fields related to the notification
		        this.removeFieldList(oto, c);
  
		        Iterator i = fields.iterator();
		        while(i.hasNext()) {
		            OccurrenceFieldTO ofto = (OccurrenceFieldTO)i.next();
				    pstmt = c.prepareStatement("insert into occurrence_field (occurrence_id, field, value, date_value) values (?,?,?,?)");
				    pstmt.setString(1, oto.getId());
				    pstmt.setString(2, ofto.getField());
				    pstmt.setString(3, ofto.getValue());
				    pstmt.setTimestamp(4, ofto.getDateValue());
				    pstmt.executeUpdate();		            
		        }
		    }
		
		} catch (Exception e) {
		    throw new DataAccessException(e);

		}finally{
			try {
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
    }

    
    private void removeFieldList(OccurrenceTO oto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
	        pstmt = c.prepareStatement("delete from occurrence_field where occurrence_id=?");
	        pstmt.setString(1, oto.getId());
	        pstmt.executeUpdate();
		
		} catch (Exception e) {
		    throw new DataAccessException(e);

		}finally{
			try {
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
    }    

    
    private OccurrenceFieldTO populateObjectByResultSet(OccurrenceTO oto, ResultSet rs) throws DataAccessException{
        OccurrenceFieldTO response = new OccurrenceFieldTO();
        response.setOccurrence(oto);
        response.setField(getString(rs, "field"));
        response.setValue(getString(rs, "value"));
        response.setDateValue(getTimestamp(rs, "date_value"));
        return response;
    }

    private OccurrenceTO populateObjectByResultSet(ResultSet rs) throws DataAccessException{
        OccurrenceTO response = new OccurrenceTO();       
        response.setId(getString(rs, "id"));
        response.setSource(getString(rs, "source"));
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        response.setName(getString(rs, "name"));
        response.setStatus(getString(rs, "occurrence_status"));
        response.setStatusLabel(getString(rs, "occurrence_status_label"));
        
        String visibleStr = getString(rs, "visible");
        if (visibleStr!=null) {
        	response.setVisible(visibleStr.equals("1"));	
        } else {
        	response.setVisible(false);
        }

        String localeStr = getString(rs, "loc");
        if (localeStr!=null) {
            Locale loc = null;
            String locs[] = localeStr.split("_");
            if (locs!=null && locs.length==2) {
                loc = new Locale(locs[0], locs[1]);
                response.setLocale(loc);
            }
        } else {
        	response.setLocale(null);
        }
        
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        
        return response;
    }


	
  
}
