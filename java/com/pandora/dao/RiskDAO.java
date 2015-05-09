package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.OccurrenceFieldTO;
import com.pandora.OccurrenceTO;
import com.pandora.PlanningRelationTO;
import com.pandora.ProjectTO;
import com.pandora.RiskHistoryTO;
import com.pandora.RiskStatusTO;
import com.pandora.RiskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.occurrence.IssueOccurrence;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

/**
 */
public class RiskDAO extends PlanningDAO {

    public Vector<RiskTO> getListByProjectId(String projectId) throws DataAccessException { 
        Vector<RiskTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByProjectId(projectId, c);
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
    
    
    private Vector getListUntilID(String initialId, String finalId, Connection c) throws DataAccessException{
        Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    String sql = "select r.id, p.description, p.creation_date, p.final_date, " +
					   "r.project_id, r.category_id, r.name, r.probability, r.impact, r.tendency, " +
					   "r.responsible, r.risk_status_id, r.strategy, r.contingency, rs.name as STATUS_NAME," +
					   "r.impact_cost, r.impact_time, r.impact_quality, r.impact_scope, r.risk_type, rs.status_type " +
					   "from risk r, planning p, risk_status rs " +
					   "where r.id= p.id and r.risk_status_id = rs.id " +
				   	   "and r.id > " + initialId + " and r.id <= " + finalId;
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()){
			    RiskTO rto = this.populateObjectByResultSet(rs, c);
			    response.addElement(rto);
			}

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    public Vector<RiskTO> getListByProjectId(String projectId, Connection c) throws DataAccessException {
		Vector<RiskTO> response= new Vector<RiskTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    pstmt = c.prepareStatement("select r.id, p.description, p.creation_date, p.final_date, " +
			    						   "r.project_id, r.category_id, r.name, r.probability, r.impact, r.tendency, " +
			    						   "r.responsible, r.risk_status_id, r.strategy, r.contingency, " +
			    						   "c.name as CATEGORY_NAME, rs.name as STATUS_NAME, pr.name as PROJECT_NAME, " +
			    						   "r.impact_cost, r.impact_time, r.impact_quality, r.impact_scope, r.risk_type, " +
			    						   "rs.status_type " +
		    						   "from risk r, planning p, category c, risk_status rs, project pr " +
		            				   "where r.id= p.id and r.category_id = c.id and r.risk_status_id = rs.id " +
		            				   	   "and r.project_id = pr.id and r.project_id=?");
			pstmt.setString(1, projectId);	
			rs = pstmt.executeQuery();
			while (rs.next()) {
			    RiskTO rto = this.populateObjectByResultSet(rs, c);
			    
			    CategoryTO cto = rto.getCategory();
			    cto.setName(getString(rs, "CATEGORY_NAME"));
			    
			    ProjectTO pto = rto.getProject();
			    pto.setName(getString(rs, "PROJECT_NAME"));
			    
			    response.addElement(rto);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        RiskTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 

		try {

		    RiskTO filter = (RiskTO)to;
		    pstmt = c.prepareStatement("select r.id, p.description, p.creation_date, p.final_date, " +
						   "r.project_id, r.category_id, r.name, r.probability, r.impact, r.tendency, " +
						   "r.responsible, r.risk_status_id, r.strategy, r.contingency, rs.name as STATUS_NAME, " +
						   "pr.name as PROJECT_NAME, r.impact_cost, r.impact_time, r.impact_quality, r.impact_scope, " +
						   "r.risk_type, rs.status_type, c.name as CATEGORY_NAME " +
					   "from risk r, planning p, risk_status rs, project pr, category c " +
					   "where r.id= p.id and r.risk_status_id = rs.id and pr.id = r.project_id " +
 				   	   		"and r.id=? and r.category_id = c.id");		
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateObjectByResultSet(rs, c);

			    CategoryTO cto = response.getCategory();
			    cto.setName(getString(rs, "CATEGORY_NAME"));
				
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

    
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    RiskTO rto = (RiskTO)to;
		    
		    super.update(rto, c);
		    
		    pstmt = c.prepareStatement("update risk set project_id=?, category_id=?, name=?, " +
		    					"probability=?, impact=?, tendency=?, responsible=?, strategy=?, " +
		    					"contingency=?, risk_status_id=?, impact_cost=?, impact_time=?, " +
		    					"impact_quality=?, impact_scope=?, risk_type=? where id=?");
			pstmt.setString(1, rto.getProject().getId());
			pstmt.setString(2, rto.getCategory().getId());
			pstmt.setString(3, rto.getName());
			pstmt.setString(4, rto.getProbability());
			pstmt.setString(5, rto.getImpact());
			pstmt.setString(6, rto.getTendency());
			pstmt.setString(7, rto.getResponsible());
			pstmt.setString(8, rto.getStrategy());
			pstmt.setString(9, rto.getContingency());
			pstmt.setString(10, rto.getStatus().getId());
			pstmt.setString(11, (rto.getCostImpact()?"1":"0"));
			pstmt.setString(12, (rto.getTimeImpact()?"1":"0"));
			pstmt.setString(13, (rto.getQualityImpact()?"1":"0"));
			pstmt.setString(14, (rto.getScopeImpact()?"1":"0"));
			if (rto.getRiskType()!=null) {
				pstmt.setInt(15, rto.getRiskType().intValue());	
			} else {
				pstmt.setNull(15, Types.INTEGER);
			}
			pstmt.setString(16, rto.getId());
			pstmt.executeUpdate();
					
		    //create and insert into data base a new Risk History object
			this.populateHistory(rto, c);

			//check if it is necessary to create a new occurrence
			if (rto.isCreateIssueLinked()) {
				this.createIssueLink(rto, c);
			}

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    

    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    RiskTO rto = (RiskTO)to;
		    rto.setId(this.getNewId());
		    rto.setCreationDate(DateUtil.getNow());

		    //insert data into parent entity (PlanningDAO)
		    super.insert(rto, c);
		    
		    pstmt = c.prepareStatement("insert into risk (id, project_id, category_id, name, " +
					"probability, impact, tendency, responsible, strategy, contingency, risk_status_id, " +
					"impact_cost, impact_time, impact_quality, impact_scope, risk_type) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, rto.getProject().getId());
			pstmt.setString(3, rto.getCategory().getId());
			pstmt.setString(4, rto.getName());
			pstmt.setString(5, rto.getProbability());
			pstmt.setString(6, rto.getImpact());
			pstmt.setString(7, rto.getTendency());
			pstmt.setString(8, rto.getResponsible());
			pstmt.setString(9, rto.getStrategy());
			pstmt.setString(10, rto.getContingency());
			pstmt.setString(11, rto.getStatus().getId());
			pstmt.setString(12, (rto.getCostImpact()?"1":"0"));
			pstmt.setString(13, (rto.getTimeImpact()?"1":"0"));
			pstmt.setString(14, (rto.getQualityImpact()?"1":"0"));
			pstmt.setString(15, (rto.getScopeImpact()?"1":"0"));
			if (rto.getRiskType()!=null) {
				pstmt.setInt(16, rto.getRiskType().intValue());	
			} else {
				pstmt.setNull(16, Types.INTEGER);
			}			
			pstmt.executeUpdate();
			
		    //create and insert into data base a new Risk History object
			this.populateHistory(rto, c);
			
			//check if it is necessary to create a new occurrence
			if (rto.isCreateIssueLinked()) {
				this.createIssueLink(rto, c);
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
    
    private void createIssueLink(RiskTO rto, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null;
		OccurrenceDAO ocdao = new OccurrenceDAO();
		PlanningRelationDAO prdao = new PlanningRelationDAO();
		
		try {
			UserTO uto = rto.getHandler();
			
			OccurrenceTO issue = new OccurrenceTO();
			issue.setCreationDate(DateUtil.getNow());
			issue.setName(rto.getName());
			issue.setDescription("");
			issue.setProject(rto.getProject());
			issue.setHandler(uto);
			issue.setSource(IssueOccurrence.class.getName());
			issue.setStatus(IssueOccurrence.STATE_START);
			issue.setStatusLabel(uto.getBundle().getMessage("label.occurrence.issue.status.open"));
			issue.setVisible(false);
			issue.setLocale(uto.getLocale());
			
			OccurrenceFieldTO field1 = new OccurrenceFieldTO();
			field1.setField(IssueOccurrence.ISSUE_RESPONSIBLE);
			field1.setOccurrence(issue);
			field1.setValue(uto.getName());
			issue.addField(field1);

			OccurrenceFieldTO field2 = new OccurrenceFieldTO();
			field2.setField(IssueOccurrence.ISSUE_ANALYSIS);
			field2.setOccurrence(issue);
			field2.setValue(uto.getBundle().getMessage("message.formRisk.issueAnalysis") + "[" + rto.getId() + "]");
			issue.addField(field2);

			OccurrenceFieldTO field3 = new OccurrenceFieldTO();
			field3.setField(IssueOccurrence.ISSUE_ACTION);
			field3.setOccurrence(issue);
			field3.setValue(rto.getContingency());
			issue.addField(field3);
			
			ocdao.insert(issue, c);
		
			PlanningRelationTO prto = new PlanningRelationTO();
			prto.setPlanning(rto);
			prto.setRelated(issue);
			prto.setPlanType(PlanningRelationTO.ENTITY_RISK);
			prto.setRelatedType(PlanningRelationTO.ENTITY_OCCU);
			prto.setRelationType(PlanningRelationTO.CONSEQUENCE_OF);
			prdao.insertRelation(prto,c);
			
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }

    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    RiskTO rto = (RiskTO)to;

			pstmt = c.prepareStatement("delete from risk_history where risk_id=?");
			pstmt.setString(1, rto.getId());
			pstmt.executeUpdate();
		    			
			pstmt = c.prepareStatement("delete from risk where id=?");
			pstmt.setString(1, rto.getId());
			pstmt.executeUpdate();

			pstmt = c.prepareStatement("delete from plan_relation where plan_related_id=? or planning_id=?");
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, rto.getId());
			pstmt.executeUpdate();	

			pstmt = c.prepareStatement("delete from additional_field where planning_id=?");
			pstmt.setString(1, rto.getId());
			pstmt.executeUpdate();	
			
			pstmt = c.prepareStatement("delete from planning where id = ?");
			pstmt.setString(1, rto.getId());			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    
    private void populateHistory(RiskTO rto, Connection c) throws DataAccessException{
        RiskHistoryDAO hdao = new RiskHistoryDAO();
        
        RiskHistoryTO hto = new RiskHistoryTO();
        hto.setContent(rto.getFieldToString());
	    hto.setCreationDate(DateUtil.getNow());
	    hto.setRiskId(rto.getId());
	    hto.setRiskStatusId(rto.getStatus().getId());
	    hto.setUser(rto.getHandler());
	    
	    hto.setCostImpact(rto.getCostImpact());
	    hto.setQualityImpact(rto.getQualityImpact());
	    hto.setTimeImpact(rto.getTimeImpact());
	    hto.setScopeImpact(rto.getScopeImpact());
	    hto.setProbability(rto.getProbability());
	    hto.setImpact(rto.getImpact());
	    hto.setTendency(rto.getTendency());
	    
        hdao.insert(hto, c);	        
    }
    
    private RiskTO populateObjectByResultSet(ResultSet rs, Connection c) throws DataAccessException{
    	AdditionalFieldDAO afdao = new AdditionalFieldDAO();
    	
        RiskTO response = new RiskTO();       
        response.setId(getString(rs, "id"));
        response.setCategory(new CategoryTO(getString(rs, "category_id")) );
        response.setContingency(getString(rs, "contingency"));
        response.setName(getString(rs, "name"));
        response.setProbability(getString(rs, "probability"));
        response.setImpact(getString(rs, "impact"));
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        response.setResponsible(getString(rs, "responsible"));
        response.setStrategy(getString(rs, "strategy"));
        response.setTendency(getString(rs, "tendency"));               
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        
        String cost = getString(rs, "impact_cost");
        response.setCostImpact(cost!=null && cost.trim().equals("1"));
        
        String time = getString(rs, "impact_time");
        response.setTimeImpact(time!=null && time.trim().equals("1"));
        
        String quality = getString(rs, "impact_quality");
        response.setQualityImpact(quality!=null && quality.trim().equals("1"));
        
        String scope = getString(rs, "impact_scope");
        response.setScopeImpact(scope!=null && scope.trim().equals("1"));
        
        RiskStatusTO rsto = new RiskStatusTO(getString(rs, "risk_status_id"));
        rsto.setName(getString(rs, "STATUS_NAME"));
        rsto.setStatusType(getString(rs, "status_type"));
        response.setStatus(rsto);

        Integer type = getInteger(rs, "risk_type");
        if (type!=null) {
        	response.setRiskType(type);	
        } else {
        	response.setRiskType(RiskTO.RISK_TYPE_THREAT);
        }

	    //get the additional fields
	    response.setAdditionalFields(afdao.getListByPlanning(response, null, c));

        return response;
    }
    
}
