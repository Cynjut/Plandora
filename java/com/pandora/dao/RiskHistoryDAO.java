package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

import com.pandora.RiskHistoryTO;
import com.pandora.RiskTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.exception.DataAccessException;

public class RiskHistoryDAO extends DataAccess {

    
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    RiskHistoryTO hto = (RiskHistoryTO)to;
		    
			pstmt = c.prepareStatement("insert into risk_history (risk_id, risk_status_id, " +
									   "creation_date, user_id, history, " +
									   "probability, impact, tendency, impact_cost, impact_time, " +
									   "impact_quality, impact_scope, risk_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, hto.getRiskId());
			pstmt.setString(2, hto.getRiskStatusId());
			pstmt.setTimestamp(3, hto.getCreationDate());
			pstmt.setString(4, hto.getUser().getId());
			pstmt.setString(5, hto.getContent());
			pstmt.setString(6, hto.getProbability());
			pstmt.setString(7, hto.getImpact());
			pstmt.setString(8, hto.getTendency());
			pstmt.setString(9, (hto.getCostImpact()?"1":"0"));
			pstmt.setString(10, (hto.getTimeImpact()?"1":"0"));
			pstmt.setString(11, (hto.getQualityImpact()?"1":"0"));
			pstmt.setString(12, (hto.getScopeImpact()?"1":"0"));
			if (hto.getRiskType()!=null) {
				pstmt.setInt(13, hto.getRiskType().intValue());	
			} else {
				pstmt.setNull(13, Types.INTEGER);
			}
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}       
    }


    public Vector<RiskHistoryTO> getListByRisk(String riskId) throws DataAccessException{
        Vector<RiskHistoryTO> response = null;
        Connection c = null;
		try {
			c = getConnection();
			response = this.getListByRisk(riskId, c);
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			this.closeConnection(c);
		}
        return response;
    }    
    
    
    private Vector<RiskHistoryTO> getListByRisk(String riskId, Connection c) throws DataAccessException{
        Vector<RiskHistoryTO> response= new Vector<RiskHistoryTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try {
		    String sql = "select rh.risk_id, rh.risk_status_id, rh.creation_date, rh.user_id, " +
		    				    "rh.history, u.name as USER_NAME, rs.name as STATUS_NAME, rs.status_type, " +
		    				    "rh.probability, rh.impact, rh.tendency, rh.impact_cost, rh.impact_time, " +
							    "rh.impact_quality, rh.impact_scope, rh.risk_type " +
		    		     "from risk_history rh, tool_user u, risk_status rs " +
		    			 "where u.id = rh.user_id and rh.risk_status_id = rs.id " +
		    			   "and rh.risk_id = ? " +
		    			 "order by rh.creation_date";
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, riskId);
			rs = pstmt.executeQuery();						
			while (rs.next()){
			    RiskHistoryTO hto = this.populateByResultSet(rs);		        
				response.addElement(hto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    protected RiskHistoryTO populateByResultSet(ResultSet rs) throws DataAccessException{
        RiskHistoryTO response = new RiskHistoryTO();
        
	    response.setRiskId(getString(rs, "risk_id"));
	    response.setRiskStatusId(getString(rs, "risk_status_id"));
	    response.setRiskStatusLabel(getString(rs, "STATUS_NAME"));
	    response.setRiskStatusType(getString(rs, "status_type"));
	    response.setContent(getString(rs, "history"));
        response.setCreationDate(getTimestamp(rs, "creation_date"));
        
        response.setProbability(getString(rs, "probability"));
        response.setImpact(getString(rs, "impact"));
        response.setTendency(getString(rs, "tendency"));
        
        String cost = getString(rs, "impact_cost");
        response.setCostImpact(cost!=null && cost.trim().equals("1"));
        
        String time = getString(rs, "impact_time");
        response.setTimeImpact(time!=null && time.trim().equals("1"));
        
        String quality = getString(rs, "impact_quality");
        response.setQualityImpact(quality!=null && quality.trim().equals("1"));
        
        String scope = getString(rs, "impact_scope");
        response.setScopeImpact(scope!=null && scope.trim().equals("1"));
        
        Integer type = getInteger(rs, "risk_type");
        if (type!=null) {
        	response.setRiskType(type);	
        } else {
        	response.setRiskType(RiskTO.RISK_TYPE_THREAT);
        }
        
        UserTO uto = new UserTO(getString(rs, "user_id"));
        uto.setName(getString(rs, "USER_NAME"));
        response.setUser(uto);
        
        return response;
    }
}