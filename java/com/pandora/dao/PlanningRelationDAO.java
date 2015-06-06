package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.RequirementTO;
import com.pandora.TaskTO;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

public class PlanningRelationDAO extends DataAccess {

    public Vector<PlanningRelationTO> getRelationList(PlanningTO pto) throws DataAccessException { 
    	Vector<PlanningRelationTO> response = new Vector<PlanningRelationTO>();
        Connection c = null;
		try {
			c = getConnection();
			response = this.getRelationList(pto, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
        
    }
	
    private Vector<PlanningRelationTO> getRelationList(PlanningTO pto, Connection c) throws DataAccessException{
    	Vector<PlanningRelationTO> response = new Vector<PlanningRelationTO>();
    	ResultSet rs = null;
		PreparedStatement pstmt = null; 
		PlanningDAO pdao = new PlanningDAO();
		
		try {
			pstmt = c.prepareStatement("select p.id, p.description, p.creation_date, p.final_date, " +
							"pr.planning_id, pr.plan_type, pr.plan_related_type, pr.relation_type " +
							"from planning p , plan_relation pr " +
							"where pr.plan_related_id = p.id and pr.planning_id=?");
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
				PlanningRelationTO prto = new PlanningRelationTO();
				
				PlanningTO relatedPlan = pdao.populatePlanning(rs);
								
				prto.setPlanning(pto);
				prto.setRelated(relatedPlan);
				prto.setPlanType(getString(rs, "plan_type"));
				prto.setRelatedType(getString(rs, "plan_related_type"));
				prto.setRelationType(getString(rs, "relation_type"));

			    response.add(prto);
			} 

			pstmt = c.prepareStatement("select p.id, p.description, p.creation_date, p.final_date, " +
					"pr.planning_id, pr.plan_type, pr.plan_related_type, pr.relation_type " +
					"from planning p , plan_relation pr " +
					"where pr.planning_id = p.id and pr.plan_related_id=?");
			
			pstmt.setString(1, pto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    PlanningRelationTO prto = new PlanningRelationTO();
		
			    PlanningTO planning = pdao.populatePlanning(rs);
						
			    prto.setPlanning(planning);
			    prto.setRelated(pto);
			    prto.setPlanType(getString(rs, "plan_type"));
			    prto.setRelatedType(getString(rs, "plan_related_type"));
			    prto.setRelationType(getString(rs, "relation_type"));
			    response.add(prto);
			} 
			
			//check if planning is a task and implements a requirement
			if (pto instanceof TaskTO) {
				TaskTO task = (TaskTO)pto;
				if (task.getRequirement()!=null && 
						task.getRequirement().getId()!=null && !task.getRequirement().getId().trim().equals("")) {
				    PlanningRelationTO prto = new PlanningRelationTO();
				    prto.setPlanning(task.getRequirement());
				    prto.setRelated(pto);
				    prto.setPlanType(PlanningRelationTO.ENTITY_REQ);
				    prto.setRelatedType(PlanningRelationTO.ENTITY_TASK);
				    prto.setRelationType(PlanningRelationTO.RELATION_IMPLEMENTED_BY);
				    response.add(prto);				
				}
			} else if (pto instanceof RequirementTO) { 

				//check if planning is a requirement 
				RequirementTO req = (RequirementTO)pto;
				TaskDAO tdao = new TaskDAO();
				Vector<TaskTO> taskList = tdao.getTaskListByRequirement(req, req.getProject(), true, c);

				Iterator<TaskTO> i = taskList.iterator();
				while(i.hasNext()) {
					TaskTO tto = i.next();
					PlanningRelationTO prto = new PlanningRelationTO();
				    prto.setPlanning(req);
				    prto.setRelated(tto);
				    prto.setPlanType(PlanningRelationTO.ENTITY_REQ);
				    prto.setRelatedType(PlanningRelationTO.ENTITY_TASK);
				    prto.setRelationType(PlanningRelationTO.RELATION_IMPLEMENTED_BY);
				    response.add(prto);				
				}
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    
    
    public void insertRelation(PlanningRelationTO prto) throws DataAccessException {
		Connection c = null;
		try {
			c = getConnection(false);
			this.insertRelation(prto, c);
			c.commit();
		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			throw new DataAccessException(e);
			
		} finally{
			this.closeConnection(c);
		}		
    }

    public void removeRelation(PlanningRelationTO prto) throws DataAccessException {
		Connection c = null;
		try {
			c = getConnection(false);
			this.removeRelation(prto, c);
			c.commit();
		} catch(Exception e){
			try {
				c.rollback();
			} catch (SQLException er) {
				LogUtil.log(this, LogUtil.LOG_ERROR, "", er);
			}
			throw new DataAccessException(e);
			
		} finally{
			this.closeConnection(c);
		}		
    }
    
    public void insertRelation(PlanningRelationTO prto, Connection c) throws DataAccessException {
    	PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("insert into plan_relation (planning_id, plan_related_id, plan_type, plan_related_type, relation_type)" +
		    		                   "values (?, ?, ?, ?, ?)");
			pstmt.setString(1, prto.getPlanning().getId());
			pstmt.setString(2, prto.getRelated().getId());
			pstmt.setString(3, prto.getPlanType());
			pstmt.setString(4, prto.getRelatedType());
			pstmt.setString(5, prto.getRelationType());
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

    public void removeRelation(PlanningRelationTO prto, Connection c) throws DataAccessException {
    	PreparedStatement pstmt = null; 
		try {
		    pstmt = c.prepareStatement("delete from plan_relation where planning_id=? and plan_related_id=?");
			pstmt.setString(1, prto.getPlanning().getId());
			pstmt.setString(2, prto.getRelated().getId());
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

}
