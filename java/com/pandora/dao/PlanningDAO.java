package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import com.pandora.OccurrenceTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementTO;
import com.pandora.RiskTO;
import com.pandora.TaskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.kb.IndexEngineBUS;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;


/**
 * This class contain all methods to handle data related with Planning entity into data base.
 */
public class PlanningDAO extends DataAccess {

	
    public Vector<PlanningTO> getListByKeyword(Vector<String> keyword, String projectId, UserTO uto) throws DataAccessException{
    	Vector<PlanningTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByKeyword(keyword, projectId, uto, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;    	
    }

    
    /**
     * Insert a new Planning object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		AdditionalFieldDAO afdao = new AdditionalFieldDAO();
		DiscussionTopicDAO dtdao = new DiscussionTopicDAO();
		
		try {
		    PlanningTO pto = (PlanningTO)to;
			pstmt = c.prepareStatement("insert into planning (id, description, " +
									   "creation_date, final_date, iteration) " +
									   "values (?,?,?,?,?)");
			pstmt.setString(1, pto.getId());
			pstmt.setString(2, pto.getDescription());
			pstmt.setTimestamp(3, pto.getCreationDate());
			pstmt.setTimestamp(4, pto.getFinalDate());
			if (pto.getIteration()!=null && !pto.getIteration().trim().equals("")){
				pstmt.setString(5, pto.getIteration());	
			} else {
				pstmt.setNull(5, Types.VARCHAR);
			}
			
			pstmt.executeUpdate();

			//save the additional fields into data base
			afdao.insert(pto.getAdditionalFields(), pto, c);

			//save the discussion topics into data base
			dtdao.insert(pto.getDiscussionTopics(), pto, c);

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }

    
    /**
     * Update Planning object into data base, except id and creationDate colluns
     */
    public void update(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		AdditionalFieldDAO afdao = new AdditionalFieldDAO();
		DiscussionTopicDAO dtdao = new DiscussionTopicDAO();

		try {
		    PlanningTO pto = (PlanningTO)to;
			pstmt = c.prepareStatement("update planning set description=?, final_date=?, " +
									   "creation_date=?, iteration=? where id=?");
			pstmt.setString(1, pto.getDescription());
			pstmt.setTimestamp(2, pto.getFinalDate());
			if (pto.getCreationDate()!=null){
			    pstmt.setTimestamp(3, pto.getCreationDate());
			} else {
			    throw new DataAccessException("Creation date cannot be null");
			}
			pstmt.setString(4, pto.getIteration());
			pstmt.setString(5, pto.getId());
			pstmt.executeUpdate();
					
			//save the additional fields into data base
			afdao.insert(pto.getAdditionalFields(), pto, c);

			//save the discussion topics into data base
			dtdao.insert(pto.getDiscussionTopics(), pto, c);

			//update the content of object into Knowledge Base
	        IndexEngineBUS ind = new IndexEngineBUS();
	        ind.update(to);
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} catch (Exception e) {
		    throw new DataAccessException(e);			
			
		}finally{
			closeStatement(null, pstmt);
		}       
    }

    public PlanningTO getSpecializedObject(PlanningTO to) throws DataAccessException {
    	PlanningTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getSpecializedObject(to, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;    	    	
    }
    
    private PlanningTO getSpecializedObject(PlanningTO to, Connection c) throws DataAccessException {
    	PlanningTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			String id = to.getId();
			String sql = "select p.id, (select id from requirement where p.id = id) as is_req, " +
					     "(select id from task where p.id = id) as is_task, " +
					     "(select id from occurrence where p.id = id) as is_occ, " +
					     "(select id from project where p.id = id) as is_prj, " +
					     "(select id from risk where p.id = id) as is_risk " +
					     "from planning as p where p.id=?";
		    pstmt = c.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();						
			if (rs.next()){
				String buff = getString(rs, "is_task");
				if (buff!=null) {
					TaskDAO dao = new TaskDAO();
					response = (TaskTO)dao.getObject(new TaskTO(id));
					response.setType(PlanningRelationTO.ENTITY_TASK);
				} else {
					buff = getString(rs, "is_req");
					if (buff!=null) {
						RequirementDAO dao = new RequirementDAO();
						response = (RequirementTO)dao.getObject(new RequirementTO(id));
						response.setType(PlanningRelationTO.ENTITY_REQ);
					} else {
						buff = getString(rs, "is_occ");
						if (buff!=null) {
							OccurrenceDAO dao = new OccurrenceDAO();
							response = (OccurrenceTO)dao.getObject(new OccurrenceTO(id));
							response.setType(PlanningRelationTO.ENTITY_OCCU);
							
							//if the occurrence visibility is private...
							if (!((OccurrenceTO)response).isVisible()) {
								((OccurrenceTO)response).setName("** Prvt.Info. **");
								response.setDescription("** Prvt.Info. **");
							}

						} else {
							buff = getString(rs, "is_prj");
							if (buff!=null) {
								ProjectDAO dao = new ProjectDAO();
								response = (ProjectTO)dao.getObject(new ProjectTO(id));
								response.setType(PlanningRelationTO.ENTITY_PROJ);
							} else {
								buff = getString(rs, "is_risk");
								if (buff!=null) {
									RiskDAO dao = new RiskDAO();
									response = (RiskTO)dao.getObject(new RiskTO(id));
									response.setType(PlanningRelationTO.ENTITY_RISK);
								} else {
									LogUtil.log(this, LogUtil.LOG_INFO, "A suitable entity that match with id [" + id + "] was not found.", null);
								}							
							}							
						}
					}
				}
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    
    private Vector<PlanningTO> getListByKeyword(Vector<String> kwList, String projectId, UserTO uto, Connection c) throws DataAccessException{
    	Vector<PlanningTO> response = new Vector<PlanningTO>();
    	ResultSet rs = null;
		PreparedStatement pstmt = null; 

		try {
	    
		    Vector<String> vfields = new Vector<String>();
		    vfields.addElement("p.description");
		    vfields.addElement("t.name");
		    vfields.addElement("r.name");
		    vfields.addElement("o.name");
		    vfields.addElement("pr.name");
		    vfields.addElement("p.id");
		    String keyWhere = StringUtil.getSQLKeywordsByFields(kwList, vfields);
		    if (!keyWhere.equals("")){
		        keyWhere = " AND (" + keyWhere + ")"; 
		    }
			
		    String projectWhere = "", projProjWhere = "";
		    if (projectId!=null && !projectId.trim().equals("")) {
		    	projectWhere = " and project_id='" + projectId + "'";
		    	projProjWhere = " and id='" + projectId + "'";
		    }
		    
		    String projectResource = "", projProjResource = "";
		    projectResource = " and project_id in (select project_id from resource where id='" + uto.getId() + "')";
		    projProjResource = " and id in (select project_id from resource where id='" + uto.getId() + "')";
		    
		    String sql = "select p.id, p.description, p.creation_date, p.final_date, " +
					    "(select '" + PlanningTO.PLANNING_TASK + "' from task where id=p.id " + projectWhere + projectResource + ") as IS_TSK, " + 
					    "(select '" + PlanningTO.PLANNING_REQUIREMENT + "' from requirement where id=p.id " + projectWhere + projectResource + ") as IS_REQ, " +
					    "(select '" + PlanningTO.PLANNING_PROJECT + "' from project where id=p.id " + projProjWhere + projProjResource + ") as IS_PRJ, " +
					    "(select '" + PlanningTO.PLANNING_OCCURENCE + "' from occurrence where id=p.id " + projectWhere + projectResource + ") as IS_OCC, " +
					    "(select '" + PlanningTO.PLANNING_RISK + "' from risk where id=p.id " + projectWhere + projectResource + ") as IS_RSK, " +
					    "t.name as taskname, r.name as riskname, o.name as occname, pr.name as projname " +					
					    "from planning p " +
					       "LEFT OUTER JOIN task t on p.id = t.id " +
					       "LEFT OUTER JOIN risk r on p.id = r.id " +
					       "LEFT OUTER JOIN occurrence o on p.id = o.id " +
					       "LEFT OUTER JOIN project pr on p.id = pr.id " +
					    "where p.description is not null and p.id<>'0'" + keyWhere;
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()){
				String name = ""; 
				PlanningTO pto = this.populateBeanByResultSet(rs);
				
				String isTsk = getString(rs, "IS_TSK");
				String isReq = getString(rs, "IS_REQ");
				String isPrj = getString(rs, "IS_PRJ");
				String isOcc = getString(rs, "IS_OCC");
				String isRsk = getString(rs, "IS_RSK");
				
				if (isTsk!=null) {
					name = getString(rs, "taskname"); 
					pto.setType(isTsk);
				} else if (isReq!=null) {
					pto.setType(isReq);
				} else if (isOcc!=null) {
					name = getString(rs, "occname");
					pto.setType(isOcc);
				} else if (isPrj!=null) {
					name = getString(rs, "projname");
					pto.setType(isPrj);
				} else if (isRsk!=null) {
					name = getString(rs, "riskname");
					pto.setType(isRsk);
				} else {
					pto.setType("NONE");
				}
								
				if (!pto.getType().equals("NONE")) {
				    
					if (name!=null && !name.trim().equals("")) {
						name = "[" + name + "] ";
					}
					pto.setDescription(name + pto.getDescription());				    
					response.add(pto);	
				}
			} 
					
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}
		return response;
    }
    

    public long getMaxId(String tableName) throws DataAccessException {
        long response = -1;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getMaxId(c, tableName);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }

    

    private long getMaxId(Connection c, String tableName) throws DataAccessException {
        long response= -1;
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		try {
			String sql = "select id as maxid from planning " +
					     "where creation_date = " +
					     		"(select max(p.creation_date) " +
					     		 "from " + tableName + " as s, planning p " +
					     		 "where s.id = p.id) order by 1 desc";
			pstmt = c.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()){
			    String maxId = getString(rs, "maxid");
			    if (maxId!=null) {
			        response = new Long(maxId).longValue();    
			    }
			} 						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    private PlanningTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
    	PlanningTO response = new PlanningTO();
        response.setId(getString(rs, "id"));
        response.setDescription(getString(rs, "description"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        return response;
    }

    
    public PlanningTO populatePlanning(ResultSet rs) throws DataAccessException{
        return populateBeanByResultSet(rs);
    }
    
}
