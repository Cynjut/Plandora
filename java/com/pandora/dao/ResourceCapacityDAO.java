package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.LeaderTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceCapacityTO;
import com.pandora.ResourceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.UserBUS;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

public class ResourceCapacityDAO extends DataAccess {

	
    public Vector<ResourceCapacityTO> getListByResourceProject(String resourceId, String projectId, UserTO leader) throws DataAccessException {
        Vector<ResourceCapacityTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByResourceProject(resourceId, projectId, leader, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }

    
    public ResourceCapacityTO getObjectByResourceProject(ResourceCapacityTO rcto) throws DataAccessException {
    	ResourceCapacityTO response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getObjectByResourceProject(rcto, c);
		} catch(Exception e) {
			throw new DataAccessException(e);
		} finally {
			this.closeConnection(c);
		}
        return response;
    }

    
	public void saveCapacity(Vector<ResourceTO> resourceList, Connection c) throws DataAccessException{
		
		Iterator<ResourceTO> i = resourceList.iterator();
		while(i.hasNext()) {
			ResourceTO rto = (ResourceTO)i.next();
			
			//check if there is some value of capacity for resource and project references
			ResourceCapacityTO rcto = this.getMoreRecentObjectByResourceProject(rto, c);
			
			if (rcto==null) {

				//if not exists, create a new object..
				Timestamp projectIni = rto.getProject().getCreationDate();
				rcto = this.getNewObject(rto, projectIni);
				this.insert(rcto, c);
				
			} else {
				
				//if exists, checks if the date of capacity is =TODAY 
				if (rcto.isToday()){
					
					//update the value of capacity
					rcto.setCapacity(rto.getCapacityPerDay(rcto.getDate()));
					this.update(rcto, c);
					
				} else {

					//insert a new resource capacity record					
					if (rcto.getCapacity().intValue()!=rto.getCapacityPerDay(rcto.getDate()).intValue()) {
						Timestamp today = DateUtil.getNow();
						rcto = this.getNewObject(rto, today);
						this.insert(rcto, c);						
					}
				}
			}
		}
	}


    private ResourceCapacityTO getObjectByResourceProject(ResourceCapacityTO rcto, Connection c)  throws DataAccessException {
    	ResourceCapacityTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select resource_id, project_id, cap_year, cap_month, " +
									   "cap_day, capacity, cost_per_hour from resource_capacity " +
									   "where resource_id=? and  project_id=? and cap_year=? " +
									   "and cap_month=? and cap_day=?");
			pstmt.setString(1, rcto.getResourceId());
			pstmt.setString(2, rcto.getProjectId());
			pstmt.setInt(3, rcto.getYear().intValue());
			pstmt.setInt(4, rcto.getMonth().intValue());
			pstmt.setInt(5, rcto.getDay().intValue());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    response = this.populateObjectByResultSet(rs);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    /**
     * NOTE: the 'order by' criteria of this method is mandatory, because it is used by
     * CostAction algorithm  
     */
    public Vector<ResourceCapacityTO> getListByResourceProject(String resourceId, String projectId, UserTO leader, Connection c)  throws DataAccessException {
    	Vector<ResourceCapacityTO> response = new Vector<ResourceCapacityTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String whereClause = "";
		boolean selectIn = false;
		try {

			if (projectId!=null) {
				if (projectId.trim().equals("-1")) {
					whereClause = getProjectIn(leader);
					selectIn = true;
				}else {
					whereClause = " and rc.project_id=? ";
				}		
			}
			
			if (resourceId!=null) {
				whereClause = whereClause + " and rc.resource_id=? ";
				//whereClause = whereClause + "and rc.project_id in (select id from planning where final_date is null) ";	
			}
			
			pstmt = c.prepareStatement("select rc.resource_id, rc.project_id, rc.cap_year, rc.cap_month, " +
									   "rc.cap_day, rc.capacity, rc.cost_per_hour, p.name as PROJECT_NAME, tu.username as RESOURCE_USERNAME " +
									   "from resource_capacity rc, project p, tool_user tu " +
									   "where rc.cap_year is not null and rc.project_id = p.id and rc.resource_id = tu.id " + whereClause + 
									   "order by rc.project_id, rc.cap_year asc, rc.cap_month asc, rc.cap_day asc");
			if (!selectIn) {
				if (projectId!=null && resourceId==null) {
					pstmt.setString(1, projectId);	
				}
				if (resourceId!=null && projectId==null) {
					pstmt.setString(1, resourceId);	
				}
				if (resourceId!=null && projectId!=null) {
					pstmt.setString(1, projectId);
					pstmt.setString(2, resourceId);	
				}				
			}
			
			rs = pstmt.executeQuery();
			while (rs.next()){
				ResourceCapacityTO rcto = this.populateObjectByResultSet(rs);
				rcto.setProjectName(getString(rs, "PROJECT_NAME"));
				rcto.setResourceUserName(getString(rs, "RESOURCE_USERNAME"));
			    response.add(rcto);
			}
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }


	private String getProjectIn(UserTO leader) throws DataAccessException {
		ProjectDAO pdeao = new ProjectDAO();
		String whereClause = "";
		if (leader!=null && leader instanceof LeaderTO) {
		    Vector<ProjectTO> prjlist = pdeao.getProjectListForManagement((LeaderTO)leader, true);
			Iterator<ProjectTO> j = prjlist.iterator();
			while(j.hasNext()) {
				ProjectTO to = j.next();
				if (whereClause.equals("")) {
					whereClause = " and project_id in ('" + to.getId()+ "'";		
				} else {
					whereClause = whereClause + ", '" + to.getId()+ "'";
				}
			}
			whereClause = whereClause + ") ";
		}
		return whereClause;
	}
    
    private ResourceCapacityTO getMoreRecentObjectByResourceProject(ResourceTO rto, Connection c) throws DataAccessException{
    	ResourceCapacityTO response = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select resource_id, project_id, cap_year, cap_month, " +
					   "cap_day, capacity, cost_per_hour from resource_capacity " +
					   "where resource_id=? and  project_id=? " +
					   "order by cap_year desc, cap_month desc, cap_day desc");
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, rto.getProject().getId());
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

    
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
			ResourceCapacityTO rcto = (ResourceCapacityTO)to;
		    pstmt = c.prepareStatement("insert into resource_capacity (resource_id, project_id, cap_year, cap_month, " +
									   "cap_day, capacity, cost_per_hour) values (?,?,?,?,?,?,?)");
			pstmt.setString(1, rcto.getResourceId());
			pstmt.setString(2, rcto.getProjectId());
			pstmt.setInt(3, rcto.getYear().intValue());
			pstmt.setInt(4, rcto.getMonth().intValue());
			pstmt.setInt(5, rcto.getDay().intValue());
			pstmt.setInt(6, rcto.getCapacity().intValue());
			if (rcto.getCostPerHour()==null) {
				pstmt.setNull(7, Types.INTEGER);	
			} else {
				pstmt.setInt(7, rcto.getCostPerHour().intValue());
			}
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
		    
			ResourceCapacityTO rcto = (ResourceCapacityTO)to;
		    pstmt = c.prepareStatement("update resource_capacity set capacity=?, cost_per_hour=? " +
		    						    "where resource_id=? and project_id=? " +
		    							"and cap_year=? and cap_month=? and cap_day=?");
			pstmt.setInt(1, rcto.getCapacity().intValue());
			if (rcto.getCostPerHour()==null) {
				pstmt.setNull(2, Types.INTEGER);	
			} else {
				pstmt.setInt(2, rcto.getCostPerHour().intValue());
			}
			pstmt.setString(3, rcto.getResourceId());
			pstmt.setString(4, rcto.getProjectId());
			pstmt.setInt(5, rcto.getYear().intValue());
			pstmt.setInt(6, rcto.getMonth().intValue());
			pstmt.setInt(7, rcto.getDay().intValue());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }


    //TODO deprecated
    public void remove(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
			ResourceCapacityTO rcto = (ResourceCapacityTO)to;
		    pstmt = c.prepareStatement("delete from resource_capacity where resource_id=? " +
		    							"and project_id=? and cap_year=? and cap_month=? and cap_day=?");
			pstmt.setString(1, rcto.getResourceId());
			pstmt.setString(2, rcto.getProjectId());
			pstmt.setInt(3, rcto.getYear().intValue());
			pstmt.setInt(4, rcto.getMonth().intValue());
			pstmt.setInt(5, rcto.getDay().intValue());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }

    public void removeByResourceList(Vector<ResourceTO> removeResourceList, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
			if (removeResourceList!=null) {
			    Iterator<ResourceTO> i = removeResourceList.iterator();
			    while(i.hasNext()) {
			    	ResourceTO rto = (ResourceTO)i.next();
				    pstmt = c.prepareStatement("delete from resource_capacity where resource_id=? " +
											   "and project_id=?");
					pstmt.setString(1, rto.getId());
					pstmt.setString(2, rto.getProject().getId());
					pstmt.executeUpdate();
			    }				
			}
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
    
    
    private ResourceCapacityTO populateObjectByResultSet(ResultSet rs) throws DataAccessException{
    	ResourceCapacityTO response = new ResourceCapacityTO();
    	response.setResourceId(getString(rs, "resource_id"));
    	response.setProjectId(getString(rs, "project_id"));
    	response.setYear(getInteger(rs, "cap_year"));
    	response.setMonth(getInteger(rs, "cap_month"));
    	response.setDay(getInteger(rs, "cap_day"));
    	response.setCapacity(getInteger(rs, "capacity"));
    	response.setCostPerHour(getInteger(rs, "cost_per_hour"));
    	return response;

    }

	private ResourceCapacityTO getNewObject(ResourceTO rto, Timestamp projectIni){
		UserBUS ubus = new UserBUS();
		ResourceCapacityTO rcto = new ResourceCapacityTO(rto);
		
		//define the default capacity based to the root preferences...
		try {
			UserTO root = ubus.getRoot();
			String defCapacityMinutesStr = root.getPreference().getPreference(PreferenceTO.GENERAL_DEFAULT_CAPACITY);
			rcto.setCapacity(new Integer(defCapacityMinutesStr));
		} catch (BusinessException e) {
			e.printStackTrace();
		} 
		
		rcto.setYear(new Integer(DateUtil.get(projectIni, Calendar.YEAR)));
		rcto.setMonth(new Integer(DateUtil.get(projectIni, Calendar.MONTH)+1));
		rcto.setDay(new Integer(DateUtil.get(projectIni, Calendar.DATE)));
		
		return rcto;
	}

}
