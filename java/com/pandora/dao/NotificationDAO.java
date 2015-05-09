package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.NotificationFieldTO;
import com.pandora.NotificationTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.LogUtil;

/**
 */
public class NotificationDAO extends DataAccess {

   
    
    public Vector getList(Connection c) throws DataAccessException {
		Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
			pstmt = c.prepareStatement("select id, name, description, notification_class, " +
					"sql_text, retry_number, next_notification, final_date, " +
					"last_check, period_minute, period_hour, periodicity " +
					"from notification");
			rs = pstmt.executeQuery();
			while (rs.next()){
			    NotificationTO nto = this.populateBeanByResultSet(rs);
			    nto.setFields(this.getFieldList(nto, c));
			    response.addElement(nto);
			} 
		
		} catch (Exception e) {
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

    
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    NotificationTO nto = (NotificationTO)to;
			pstmt = c.prepareStatement("update notification set name=?, description=?, notification_class=?, " +
									   "sql_text=?, retry_number=?, next_notification=?, final_date=?, " +
									   "last_check=?, period_minute=?, period_hour=?, periodicity=? " +
									   "where id=?");
			pstmt.setString(1, nto.getName());
			pstmt.setString(2, nto.getDescription());
			pstmt.setString(3, nto.getNotificationClass());
			pstmt.setString(4, nto.getSqlStement());		
			if (nto.getRetryNumber()!=null) {
			    pstmt.setInt(5, nto.getRetryNumber().intValue());    
			} else {
			    pstmt.setNull(5, java.sql.Types.DECIMAL);
			}
			pstmt.setTimestamp(6, nto.getNextNotification());
			
			if (nto.getFinalDate()!=null) {
			    pstmt.setTimestamp(7, nto.getFinalDate());    
			} else {
			    pstmt.setNull(7, java.sql.Types.TIMESTAMP);
			}
			pstmt.setTimestamp(8, nto.getLastCheck());
			
			if (nto.getPeriodicityMinute()!=null) {
			    pstmt.setInt(9, nto.getPeriodicityMinute().intValue());    
			} else {
			    pstmt.setNull(9, java.sql.Types.DECIMAL);
			}			
			if (nto.getPeriodicityHour()!=null) {
			    pstmt.setInt(10, nto.getPeriodicityHour().intValue());    
			} else {
			    pstmt.setNull(10, java.sql.Types.DECIMAL);
			}			
			if (nto.getPeriodicity()!=null) {
			    pstmt.setInt(11, nto.getPeriodicity().intValue());    
			} else {
			    pstmt.setNull(11, java.sql.Types.DECIMAL);
			}			
			pstmt.setString(12, nto.getId());
			pstmt.executeUpdate();
					
			//save fields related to the notification
			this.updateFieldList(nto, c);
			
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
		    
		    NotificationTO nto = (NotificationTO)to;
		    nto.setId(this.getNewId());

			pstmt = c.prepareStatement("insert into notification(id, name, description, notification_class, " +
					   				   "sql_text, retry_number, next_notification, final_date, " +
					   				   "last_check, period_minute, period_hour, periodicity) " +
					   				   "values (?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, nto.getId());
			pstmt.setString(2, nto.getName());
			pstmt.setString(3, nto.getDescription());
			pstmt.setString(4, nto.getNotificationClass());
			pstmt.setString(5, nto.getSqlStement());
			if (nto.getRetryNumber()!=null) {
			    pstmt.setInt(6, nto.getRetryNumber().intValue());			    
			} else {
			    pstmt.setNull(6, java.sql.Types.DECIMAL);
			}			
			pstmt.setTimestamp(7, nto.getNextNotification());
			if (nto.getFinalDate()!=null) {
			    pstmt.setTimestamp(8, nto.getFinalDate());			    
			} else {
			    pstmt.setNull(8, java.sql.Types.TIMESTAMP);
			}
		    pstmt.setTimestamp(9, nto.getLastCheck());
			if (nto.getPeriodicityMinute()!=null) {
			    pstmt.setInt(10, nto.getPeriodicityMinute().intValue());			    
			} else {
			    pstmt.setNull(10, java.sql.Types.DECIMAL);
			}
			if (nto.getPeriodicityHour()!=null) {
			    pstmt.setInt(11, nto.getPeriodicityHour().intValue());			    
			} else {
			    pstmt.setNull(11, java.sql.Types.DECIMAL);
			}			
			pstmt.setInt(12, nto.getPeriodicity().intValue());
			
			pstmt.executeUpdate();
			
			//save fields related to the notification
			this.updateFieldList(nto, c);
			
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
    
    
    public TransferObject getObject(TransferObject to, Connection c) throws DataAccessException {
        NotificationTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
		    NotificationTO filter = (NotificationTO)to;
		    pstmt = c.prepareStatement("select id, name, description, notification_class, sql_text, retry_number, " +
				       			"next_notification, final_date, last_check, period_minute, period_hour, periodicity " +
				       			"from notification " +
				       			"where id = ?");
			pstmt.setString(1, filter.getId());
			rs = pstmt.executeQuery();						
			if (rs.next()){
				response = this.populateBeanByResultSet(rs);
				if (response!=null) {
				    response.setFields(this.getFieldList(response, c));    
				}
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
    
    
    private Vector getFieldList(NotificationTO nto, Connection c) throws DataAccessException {
		Vector response = new Vector();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    
			pstmt = c.prepareStatement("select notification_id, name, value from notification_field " +
					                   "where notification_id=?");
			pstmt.setString(1, nto.getId());
			rs = pstmt.executeQuery();
		
			while (rs.next()){
			    NotificationFieldTO nfto = this.populateBeanByResultSet(nto, rs);
			    response.addElement(nfto);
			} 
		
		} catch (Exception e) {
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

    
    private void updateFieldList(NotificationTO nto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    Vector fields = nto.getFields();
		    if (fields!=null && !fields.isEmpty()) {		    
		        
		        //remove all fields related to the notification
		        this.removeFieldList(nto, c);

		        Iterator i = fields.iterator();
		        while(i.hasNext()) {
		            NotificationFieldTO nfto = (NotificationFieldTO)i.next();
				    pstmt = c.prepareStatement("insert into notification_field (notification_id, name, value) values (?,?,?)");
				    pstmt.setString(1, nto.getId());
				    pstmt.setString(2, nfto.getName());
				    pstmt.setString(3, nfto.getValue());
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
    
    
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    NotificationTO nto = (NotificationTO)to;
		    
	        //remove all fields related to the notification
	        this.removeFieldList(nto, c);
		    
			pstmt = c.prepareStatement("delete from notification where id=?");
			pstmt.setString(1, nto.getId());
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
        

    private void removeFieldList(NotificationTO nto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
	        pstmt = c.prepareStatement("delete from notification_field where notification_id=?");
	        pstmt.setString(1, nto.getId());
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
    
    
    private NotificationFieldTO populateBeanByResultSet(NotificationTO nto, ResultSet rs) throws DataAccessException{
        NotificationFieldTO response = new NotificationFieldTO();
        response.setNotification(nto);
        response.setName(getString(rs, "name"));
        response.setValue(getString(rs, "value"));
        return response;
    }
    
    
    private NotificationTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        NotificationTO response = new NotificationTO();
        response.setId(getString(rs, "id"));
        response.setName(getString(rs, "name"));
        response.setDescription(getString(rs, "description"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        response.setNextNotification(getTimestamp(rs, "next_notification"));
        response.setSqlStement(getString(rs, "sql_text"));
        response.setRetryNumber(getInteger(rs, "retry_number"));
        response.setNotificationClass(getString(rs, "notification_class"));
        response.setLastCheck(getTimestamp(rs, "last_check"));        
        response.setPeriodicityHour(getInteger(rs, "period_hour"));
        response.setPeriodicityMinute(getInteger(rs, "period_minute"));
        response.setPeriodicity(getInteger(rs, "periodicity"));
        
        return response;
    }
    
}
