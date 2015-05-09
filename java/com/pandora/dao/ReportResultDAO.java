package com.pandora.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import com.pandora.ReportResultTO;
import com.pandora.ReportTO;
import com.pandora.TransferObject;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 * This class contain the methods to access information about 
 * Report Result entity into data base. 
 */
public class ReportResultDAO extends DataAccess {

    /**
     * Insert a new Report object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    ReportResultTO rrto = (ReportResultTO)to;
			pstmt = c.prepareStatement("insert into report_result (report_Id, last_execution, value) " +
									   "values (?,?,?)");
			pstmt.setString(1, rrto.getReportId());
			pstmt.setTimestamp(2, rrto.getLastExecution());
			pstmt.setString(3, rrto.getValue());
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
     * Fill In Report object with a list of results based on time range.
     */
    public void fillInReport(ReportTO rto, Timestamp initialDate, Timestamp finalDate, Connection c) throws DataAccessException {
		Vector<ReportResultTO> list= new Vector<ReportResultTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {

		    Timestamp cursor = initialDate;
		    while(cursor.before(finalDate) || cursor.equals(finalDate)){
		        
		        //define the range to be used by query...
		        Timestamp iniC = DateUtil.getDate(cursor, true);
		        Timestamp finalC = DateUtil.getDate(cursor, false);
		        cursor = DateUtil.getChangedDate(cursor, Calendar.DATE, 1);
				pstmt = c.prepareStatement("select report_Id, last_execution, value " +
					       				   "from report_result " +
					       				   "where report_Id = ? " +
					       				   "and last_execution >= ? " +
					         			   "and last_execution <= ?");
				pstmt.setString(1, rto.getId());
				pstmt.setTimestamp(2, iniC);
				pstmt.setTimestamp(3, finalC);
				rs = pstmt.executeQuery();
				ReportResultTO rrto = null;
				if (rs.next()){
				    rrto = this.populateReanByResultSet(rs);
				} else {
				    rrto = new ReportResultTO();
				}
				list.addElement(rrto);
				
		    }
			rto.setResultList(list);
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			//Close the current result set and statement
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException ec) {
			    LogUtil.log(this, LogUtil.LOG_ERROR, "DB Closing statement error", ec);
			} 		
		}	 
    }


    /**
     * Create a new TO object based on data into result set.
     */
    public ReportResultTO populateReanByResultSet(ResultSet rs) throws DataAccessException {
        ReportResultTO response = new ReportResultTO();
        response.setReportId(getString(rs, "report_id"));
        response.setLastExecution(getTimestamp(rs, "last_execution"));
        response.setValue(getString(rs, "value"));
        return response;
    }
    
    
}
