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
			pstmt = c.prepareStatement("insert into report_result (report_Id, project_id, last_execution, value) " +
									   "values (?,?,?,?)");
			pstmt.setString(1, rrto.getReportId());
			pstmt.setString(2, rrto.getProjectId());
			pstmt.setTimestamp(3, rrto.getLastExecution());
			pstmt.setString(4, rrto.getValue());
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }

    
	public boolean containResult(ReportTO rto, Connection c) throws DataAccessException {
		boolean response = false;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = c.prepareStatement("select report_id from report_result where report_id=?");
			pstmt.setString(1, rto.getId());
			rs = pstmt.executeQuery();
			if (rs.next()){
				response = true;
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}    
		return response;
	}    

	
    /**
     * Fill In Report object with a list of results based on time range.
     */
    public void fillInReport(ReportTO rto, String projectId, Timestamp initialDate, Timestamp finalDate, Connection c) throws DataAccessException {
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
				pstmt = c.prepareStatement("select report_id, last_execution, value " +
					       				   "from report_result " +
					       				   "where report_Id=? and project_id=? and last_execution>=? and last_execution<=?");
				pstmt.setString(1, rto.getId());
				pstmt.setString(2, projectId);
				pstmt.setTimestamp(3, iniC);
				pstmt.setTimestamp(4, finalC);
				rs = pstmt.executeQuery();
				
				ReportResultTO rrto = null;
				if (rs.next()){
				    rrto = this.populateBeanByResultSet(rs, projectId, null);
				} else {
				    rrto = new ReportResultTO();
				}
				list.addElement(rrto);
		    }
			rto.setResultList(list);
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
    }


    /**
     * Create a new TO object based on data into result set.
     */
    public ReportResultTO populateBeanByResultSet(ResultSet rs, String projectId, String projectName) throws DataAccessException {
        ReportResultTO response = new ReportResultTO();
        response.setReportId(getString(rs, "report_id"));
        response.setProjectId(projectId);
        response.setProjectName(projectName);
        response.setLastExecution(getTimestamp(rs, "last_execution"));
        response.setValue(getString(rs, "value"));
        return response;
    }



    
    
}
