package com.pandora.dao;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;

import com.pandora.BSCReportTO;
import com.pandora.CategoryTO;
import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.ReportFieldTO;
import com.pandora.ReportResultTO;
import com.pandora.ReportTO;
import com.pandora.ResourceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.ReportBUS;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.EmptyReportException;
import com.pandora.helper.DateUtil;

/**
 * This class contain the methods to access information about 
 * Report entity into data base. 
 */
public class ReportDAO extends DataAccess {

    /**
     * Perform statement into data base and return an raw object (ResultSet) 
     */
    public byte[] performJasperReport(ReportTO rto) throws DataAccessException{
        Connection c = null;
        byte[] response = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        boolean isEmpty = false;
        
		try {
			c = getConnection();
			pstmt = this.prepareStatement(rto.getSqlWithoutDomain(), rto, c);
	        rs = pstmt.executeQuery();
	        
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put(JRParameter.REPORT_CONNECTION, c);
			if (rto.getHandler()!=null) {
				Locale loc = rto.getHandler().getLocale();
				parameters.put(JRParameter.REPORT_LOCALE, loc);
			}

			JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
			JasperPrint jp = JasperFillManager.fillReport(rto.getReportFileName(), parameters, jrRS );
			isEmpty = (jp.getPages().size()==0);
			if (!isEmpty) {
				if (rto.getExportReportFormat().equals(ReportTO.REPORT_EXPORT_PDF)) {
					response = JasperExportManager.exportReportToPdf(jp);    

				} else if (rto.getExportReportFormat().equals(ReportTO.REPORT_EXPORT_RTF)) {
					JRRtfExporter exporter = new JRRtfExporter();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");   
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp );
					exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos  );
					exporter.exportReport();	
					response = baos.toByteArray();
					
				} else if (rto.getExportReportFormat().equals(ReportTO.REPORT_EXPORT_ODT)) {
					JROdtExporter exporter = new JROdtExporter();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");   
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp );
					exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos  );
					exporter.exportReport();	
					response = baos.toByteArray();
				}
			}
			
		} catch(Exception e){
			throw new DataAccessException(e);
		} finally{
			super.closeStatement(rs, pstmt);
			this.closeConnection(c);
		}

		if (isEmpty){
		    throw new EmptyReportException();
		}
		
        return response;		
    }

    /**
     * Return a list of reportResult objects based data from ReportFieldTO
     */
    public Vector<ReportResultTO> performSQLByReportField(ReportFieldTO field, UserTO uto) throws DataAccessException{
        Vector<ReportResultTO> response = null;
        Connection c = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
    	try {    	    
    		c = getConnection();
    		
    		//create a fake report object
    		ReportTO fakeReport = new ReportTO();
    		String newSql = field.getLabel();
    		newSql = newSql.replaceAll("!", "");
    		fakeReport.setSqlStement(newSql);
    		fakeReport.setFormFieldsValues(null);
    		fakeReport.setLastExecution(DateUtil.getNow());
    		fakeReport.setDataType(ReportTO.DAILY_TYPE);
    		fakeReport.setProject(field.getProject());
    		fakeReport.setHandler(uto);
    		fakeReport.setLocale(uto.getLocale());
    		
    		pstmt = this.prepareStatement(fakeReport.getSqlStement(), fakeReport, c);
			rs = pstmt.executeQuery();
			while (rs.next()){
			    String id = rs.getString(1);
			    String value = rs.getString(2);
			    ReportResultTO item = new ReportResultTO(id);
			    item.setValue(value);
			    if (response==null) {
			        response = new Vector<ReportResultTO>();
			    }
			    response.addElement(item);
			} 
    		
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
			super.closeStatement(rs, pstmt);
    		this.closeConnection(c);
    	}
        return response;                
    }
    
    
    /**
     * Perform the sql statement of kpi object and put the result into ReportResult object.
     */
    private PreparedStatement prepareStatement(String sqlStatement, ReportTO rto, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		Vector<Integer> types = new Vector<Integer>();
		Vector<Object> values = new Vector<Object>();    		
		
		try {
		    String previousSql = new String(ReportBUS.replaceStatement(sqlStatement, rto));
		    String sql = ReportBUS.getStatementValues(previousSql, rto, types, values);
		    
		    pstmt = c.prepareStatement(sql);
		    
		    //check if current PreparedStatememt object must be set with values
    		if (types!=null && values!=null) {
    			for (int i=0; i<types.size(); i++) {
    				Integer type = (Integer)types.elementAt(i);
    				if (type.intValue()==Types.VARCHAR) {
    					pstmt.setString(i+1, (String)values.elementAt(i));
    					
    				} else if (type.intValue()==Types.TIMESTAMP) {
    					pstmt.setTimestamp(i+1, (Timestamp)values.elementAt(i));
    				
    				} else if (type.intValue()==Types.INTEGER) {
    					pstmt.setInt(i+1, ((Integer)values.elementAt(i)).intValue());
    					
    				} else if (type.intValue()==Types.FLOAT) {
    					pstmt.setFloat(i+1, ((Float)values.elementAt(i)).floatValue());
    					
    				} else if (type.intValue()==Types.BOOLEAN) {
    					pstmt.setBoolean(i+1, ((Boolean)values.elementAt(i)).booleanValue());
    				}
    			}
    		}
			
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
		
		return pstmt;
    }

    
	public Vector<ReportTO> getKpiByOccurrence(String occurrenceId) throws DataAccessException {
        Vector<ReportTO> response = new Vector<ReportTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getKpiByOccurrence(occurrenceId, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
	}    

	
	
	public Vector<BSCReportTO> getBSC(Timestamp reference, ProjectTO pto, CategoryTO cto, boolean onCascade) throws DataAccessException {
        Vector<BSCReportTO> response = new Vector<BSCReportTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getBSC(reference, pto, cto, onCascade, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
	}    	

	
	/**
     * Return a list of report objects that must be executed by timer
     */
    public Vector<ReportTO> getReportListToPerfom() throws DataAccessException {
        Vector<ReportTO> response = new Vector<ReportTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getReportListToPerfom(c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
    }

    
    /**
     * Get all Kpi (if true) or flat reports (if false)
     * from data base
     */
    public Vector<ReportTO> getListBySource(boolean isKpi, String categoryId, ProjectTO pto, boolean includeClosed) throws DataAccessException {
        Vector<ReportTO> response = new Vector<ReportTO>();
        Connection c = null;
    	try {
    		c = getConnection();
    		response = this.getListBySource(isKpi, categoryId, pto, includeClosed, c);
    	} catch(Exception e) {
    		throw new DataAccessException(e);
    	} finally {
    		this.closeConnection(c);
    	}
        return response;        
    }


    private Vector<BSCReportTO> getBSC(Timestamp reference, ProjectTO pto, CategoryTO cto, boolean onCascade, Connection c) throws DataAccessException{
		Vector<BSCReportTO> response= new Vector<BSCReportTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		ProjectDelegate pdel = new ProjectDelegate();
		try {

			String projList = "";
			if (onCascade) {
				try {
					projList = pdel.getProjectIn(pto.getId());
				}catch(BusinessException e){
					projList = "";
				}				
			} else {
				projList = "'" + pto.getId() + "'";
			}
		
			
			String catWhere = "";
			if (!cto.getId().equals(CategoryTO.DEFAULT_CATEGORY_ID)) {
				catWhere = " and r.category_id='" + cto.getId() + "' "; 
			}
			
			String sql = "select r.id, r.name, r.report_perspective_id, r.data_type, " + 
		       	         "r.category_id, r.goal, r.tolerance, r.tolerance_type, " +
		       	         "rr.last_execution, re.value, o.strategy, o.weight, re.report_id, " +
		       	         "p.id as project_id, p.name as project_name, o.occurrence_id, r.final_date " +
		          "from report_result re, (select report_id, max(last_execution) as last_execution  " +
				                          "from report_result where last_execution <= ? " +
				                          "group by report_id) as rr, project p, " +
		                "report r LEFT OUTER JOIN (select report_id, ok.weight, o.name as strategy, ok.occurrence_id " + 
						                          "from occurrence_kpi ok, occurrence o  " +
						                          "where o.id = ok.occurrence_id and occurrence_status <> '99' " +
					                             ") as o on o.report_id = r.id " +
		         "where r.type=1  " +
		           "and rr.report_id = r.id and rr.report_id = re.report_id " +
		           "and rr.last_execution = re.last_execution and r.project_id = p.id " +
		           "and r.project_id in (" + projList + ") " + catWhere +  
		         "order by p.id, o.strategy asc, o.weight asc, r.name";
			pstmt = c.prepareStatement(sql);		
			pstmt.setTimestamp(1, reference);
			rs = pstmt.executeQuery();
			while (rs.next()){
				BSCReportTO bsc = this.populateBSCReportByResultSet(rs);
			    response.addElement(bsc);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;    	
    }

    
    private Vector<ReportTO> getKpiByOccurrence(String occurrenceId, Connection c) throws DataAccessException {
		Vector<ReportTO> response= new Vector<ReportTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			pstmt = c.prepareStatement("select r.id, r.name, r.type, r.report_perspective_id, " +
									      "r.sql_text, r.execution_hour, r.last_execution, r.project_id, " +
									      "r.final_date, r.data_type, r.file_name, r.category_id, r.profile_view, ok.weight, " +
									      "r.goal, r.tolerance, r.tolerance_type " +
									   "from occurrence_kpi ok, report r " +
									   "where r.final_date is null and ok.report_id = r.id " +
									     "and ok.occurrence_id= ?");
			pstmt.setString(1, occurrenceId);			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ReportTO rto = this.populateBeanByResultSet(rs);
			    rto.setGenericTag(getString(rs, "weight"));
			    response.addElement(rto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
	}

    
    /**
     * Return a list of report objects that must be executed by timer
     */
    private Vector<ReportTO> getReportListToPerfom(Connection c) throws DataAccessException {
		Vector<ReportTO> response= new Vector<ReportTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
			int hourNow = DateUtil.get(DateUtil.getNow(), Calendar.HOUR_OF_DAY);
		    Timestamp tsDateNow = DateUtil.getDate(DateUtil.getNow(), true);
		    
		    //get a list of objects from data base
			pstmt = c.prepareStatement("select Id, name, type, report_perspective_id, " +
									   "sql_text, execution_hour, last_execution, project_id, " +
									   "final_date, data_type, file_name, category_id, profile_view, " +
									   "goal, tolerance, tolerance_type " +
									   "from report " +
									   "where final_date is null " +
									     "and execution_hour = ? and type <> '0' " +
									     "and (last_execution < ? or last_execution is null)");
			pstmt.setInt(1, hourNow);
			pstmt.setTimestamp(2, tsDateNow);			
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ReportTO rto = this.populateBeanByResultSet(rs);
			    rto.setCategory(this.getCategory(rto.getCategory()));
			    response.addElement(rto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }

    
    private Vector<ReportTO> getListBySource(boolean isKpi, String categoryId, ProjectTO pto, boolean includeClosed, Connection c) throws DataAccessException {
		Vector<ReportTO> response= new Vector<ReportTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String sourceSql = "", profileSql = "", projectSql = "";
		ProjectDelegate pdel = new ProjectDelegate();
		
		try {
		     
		    if (isKpi) {
		        sourceSql = "and r.type <> '0' ";
		    } else {
		        sourceSql = "and r.type = '0' ";
		    }
		    
			String closedWhere = "";
			if (!includeClosed) {
				closedWhere = " and r.final_date is null ";
			}

		    
		    if (pto!=null) {
		    	projectSql = " and (r.project_id='0' or r.project_id in (" + pdel.getProjectIn(pto.getId()) + ")) ";
		    	if (!isKpi && pto.getRoleIntoProject()!=null) {
		    		//check if report profile is equals the current role of project or 'all'
		    		profileSql = "and (r.profile_view='" + pto.getRoleIntoProject() + "' or r.profile_view='4' " +
		    			"or (" + ResourceTO.ROLE_RESOURCE + "=" + pto.getRoleIntoProject() + " and r.profile_view='3')" +
		    			"or (" + LeaderTO.ROLE_LEADER + "=" + pto.getRoleIntoProject() + " and r.profile_view='3') ) ";
		    	}
		    }
		    
		    String condition = "";
		    if (categoryId!=null && !categoryId.equals("0")) {
		        condition = "and category_id=? ";
		    }
		    
			pstmt = c.prepareStatement("select r.Id, r.name, r.type, r.report_perspective_id, " +
									   "r.sql_text, r.execution_hour, r.last_execution, r.project_id, r.final_date, " +
									   "r.data_type, p.name as project_name, r.file_name, r.category_id, r.profile_view, " +
									   "r.goal, r.tolerance, r.tolerance_type " +
									   "from report r, project p " +
									   "where r.project_id = p.id " + condition + projectSql + closedWhere +
									     sourceSql + profileSql + 
									     "order by r.project_id, r.report_perspective_id, r.name");
			if (categoryId!=null && !categoryId.equals("0")) {
			    pstmt.setString(1, categoryId);
			}
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ReportTO rto = this.populateBeanByResultSet(rs);
			    rto.setCategory(this.getCategory(rto.getCategory()));
			    
			    //get remaining field...
			    ProjectTO prto = rto.getProject();
			    prto.setName(getString(rs, "project_name"));
			    
			    response.addElement(rto);
			} 
						
		} catch (Exception e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;

    }

    /**
     * Get a list of all Report TOs from data base, based on time range and area.
     */
    public Vector<ReportTO> getReportListBySearch(Timestamp initialDate, Timestamp finalDate, String reportPerspectiveId, String projectId, String categoryId) throws DataAccessException {
        Vector<ReportTO> response = new Vector<ReportTO>();
        ReportResultDAO rrdao = new ReportResultDAO(); 
        Connection c = null;
    	try {
    		c = getConnection();
    		
    		//get all reports objects related with filter 
    		response = this.getReportListBySearch(initialDate, finalDate, reportPerspectiveId, projectId, categoryId, c);
    		
    		//fill in the reports with data based on time range
    		Iterator<ReportTO> i = response.iterator();
    		while(i.hasNext()){
    		    ReportTO rto = i.next();
    		    rto.setCategory(this.getCategory(rto.getCategory()));
    		    rrdao.fillInReport(rto, initialDate, finalDate, c);
    		}
    		
    		
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}
        return response;        
    }

    
    public void getKpiValues(Timestamp initialDate, Timestamp finalDate, ReportTO kpiObj) throws DataAccessException {
        ReportResultDAO rrdao = new ReportResultDAO(); 
        Connection c = null;
    	try {
    		c = getConnection();
   		    rrdao.fillInReport(kpiObj, initialDate, finalDate, c);
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	} finally{
    		this.closeConnection(c);
    	}       
    }
    
    
    /**
     * Get a list of all Report TOs from data base, based on time range and area.
     */
    private Vector<ReportTO> getReportListBySearch(Timestamp initialDate, Timestamp finalDate, String reportPerspectiveId, 
            String projectId, String categoryId, Connection c) throws DataAccessException {
		Vector<ReportTO> response= new Vector<ReportTO>();
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		     
		    //get a list of objects from data base
			pstmt = c.prepareStatement("select Id, name, type, report_perspective_id, " + 
									   "sql_text, execution_hour, last_execution, project_id, " +
									   "final_date, data_type, file_name, category_id, profile_view, " +
									   "goal, tolerance, tolerance_type " +
									   "from report " +
									   "where final_date is null " +
									   	 "and report_perspective_id=? and type <> '0' " +
									   	 "and project_id=? and (category_id is NULL or category_id=?) " +
									     "order by name, type");
			pstmt.setString(1, reportPerspectiveId);
			pstmt.setString(2, projectId);
			pstmt.setString(3, categoryId);
			rs = pstmt.executeQuery();
			while (rs.next()){
			    ReportTO rto = this.populateBeanByResultSet(rs);
			    rto.setCategory(this.getCategory(rto.getCategory()));
			    response.addElement(rto);
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    


    /**
     * Get a Report object from data base.
     */
    public TransferObject getObject(TransferObject to, Connection c)  throws DataAccessException {
        ReportTO response= null;
		ResultSet rs = null;
		PreparedStatement pstmt = null; 
		try {
		    ReportTO rto = (ReportTO)to;
			pstmt = c.prepareStatement("select Id, name, type, report_perspective_id, " +
					   				   "sql_text, execution_hour, last_execution, project_id, " +
					   				   "final_date, data_type, file_name, category_id, profile_view, " +
					   				   "goal, tolerance, tolerance_type " +
					   				   "from report where id = ?");			
			pstmt.setString(1, rto.getId());
			rs = pstmt.executeQuery();
			while (rs.next()){
			    response = this.populateBeanByResultSet(rs);
			    response.setCategory(this.getCategory(response.getCategory()));
			} 
						
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(rs, pstmt);
		}	 
		return response;
    }
    
    
    /**
     * Insert a new Report object into data base.
     */
    public void insert(TransferObject to, Connection c) throws DataAccessException {
		PreparedStatement pstmt = null;
		try {
		    
		    ReportTO rto = (ReportTO)to;
			rto.setId(this.getNewId());
					    
			pstmt = c.prepareStatement("insert into report (Id, name, type, report_perspective_id, " +
					   				   "sql_text, execution_hour, last_execution, project_id, final_date, " +
					   				   "data_type, file_name, category_id, profile_view, goal, tolerance, tolerance_type) " +
									   "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmt.setString(1, rto.getId());
			pstmt.setString(2, rto.getName());
			if (rto.getType()!=null) {
			    pstmt.setInt(3, rto.getType().intValue());    
			} else {
			    pstmt.setNull(3, java.sql.Types.DECIMAL);
			}
		    pstmt.setString(4, rto.getReportPerspectiveId());    
			pstmt.setString(5, rto.getSqlStement());
			if (rto.getExecutionHour()!=null) {
			    pstmt.setInt(6, rto.getExecutionHour().intValue());    
			} else {
			    pstmt.setNull(6, java.sql.Types.DECIMAL);
			}
		    pstmt.setTimestamp(7, rto.getLastExecution());    
			pstmt.setString(8, rto.getProject().getId());
			pstmt.setTimestamp(9, rto.getFinalDate());
			if (rto.getDataType()!=null) {
			    pstmt.setInt(10, rto.getDataType().intValue());    
			} else {
			    pstmt.setNull(10, java.sql.Types.DECIMAL);
			}
			if (rto.getReportFileName()!=null) {
			    pstmt.setString(11, rto.getReportFileName());    
			} else {
			    pstmt.setNull(11, java.sql.Types.VARCHAR);
			}
			if (rto.getCategory()!=null) {
			    pstmt.setString(12, rto.getCategory().getId());    
			} else {
			    pstmt.setNull(12, java.sql.Types.VARCHAR);
			}
			if (rto.getProfile()!=null) {
			    pstmt.setString(13, rto.getProfile());    
			} else {
			    pstmt.setNull(13, java.sql.Types.VARCHAR);
			}
			if (rto.getGoal()!=null) {
			    pstmt.setString(14, rto.getGoal());    
			} else {
			    pstmt.setNull(14, java.sql.Types.VARCHAR);
			}
			if (rto.getTolerance()!=null) {
			    pstmt.setString(15, rto.getTolerance());    
			} else {
			    pstmt.setNull(15, java.sql.Types.VARCHAR);
			}
			if (rto.getToleranceType()!=null) {
			    pstmt.setString(16, rto.getToleranceType());    
			} else {
			    pstmt.setNull(16, java.sql.Types.VARCHAR);
			}						
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}               
    }
    
    
    /**
     * Update data of a Report object from data base.
     */
    public void update(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    ReportTO rto = (ReportTO)to;
			pstmt = c.prepareStatement("update report set name=?, type=?, report_perspective_id=?, " +
									   "sql_text=?, execution_hour=?, last_execution=?, project_id=?, " +
									   "final_date=?, data_type=?, file_name=?, category_id=?, profile_view=?, " +
									   "goal=?, tolerance=?, tolerance_type=? " +
									   "where id=?");
			pstmt.setString(1, rto.getName());
			if (rto.getType()!=null) {
			    pstmt.setInt(2, rto.getType().intValue());    
			} else {
			    pstmt.setNull(2, java.sql.Types.DECIMAL);
			}
			pstmt.setString(3, rto.getReportPerspectiveId());
			pstmt.setString(4, rto.getSqlStement());
			if (rto.getExecutionHour()!=null) {
			    pstmt.setInt(5, rto.getExecutionHour().intValue());    
			} else {
			    pstmt.setNull(5, java.sql.Types.DECIMAL);
			}
			pstmt.setTimestamp(6, rto.getLastExecution());
			pstmt.setString(7, rto.getProject().getId());
			pstmt.setTimestamp(8, rto.getFinalDate());
			if (rto.getDataType()!=null) {
			    pstmt.setInt(9, rto.getDataType().intValue());    
			} else {
			    pstmt.setNull(9, java.sql.Types.DECIMAL);
			}
			pstmt.setString(10, rto.getReportFileName());
			if (rto.getCategory()!=null) {
			    pstmt.setString(11, rto.getCategory().getId());    
			} else {
			    pstmt.setNull(11, java.sql.Types.VARCHAR);
			}
			if (rto.getProfile()!=null) {
			    pstmt.setString(12, rto.getProfile());    
			} else {
			    pstmt.setNull(12, java.sql.Types.VARCHAR);
			}
			if (rto.getGoal()!=null) {
			    pstmt.setString(13, rto.getGoal());    
			} else {
			    pstmt.setNull(13, java.sql.Types.VARCHAR);
			}
			if (rto.getTolerance()!=null) {
			    pstmt.setString(14, rto.getTolerance());    
			} else {
			    pstmt.setNull(14, java.sql.Types.VARCHAR);
			}
			if (rto.getToleranceType()!=null) {
			    pstmt.setString(15, rto.getToleranceType());    
			} else {
			    pstmt.setNull(15, java.sql.Types.VARCHAR);
			}
			pstmt.setString(16, rto.getId());
			pstmt.executeUpdate();
			
			//if necessary, insert a new Report Result into data base
			if (rto.getResultList()!=null){
			    ReportResultDAO rrdao = new ReportResultDAO();
			    Iterator<ReportResultTO> i = rto.getResultList().iterator();
			    while(i.hasNext()){
			        ReportResultTO rrto = i.next();
			        rrdao.insert(rrto, c);    
			    }			    
			}
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }
    
    /**
     * 
     */
    public void remove(TransferObject to, Connection c)  throws DataAccessException {
		PreparedStatement pstmt = null; 
		try {
		    
		    ReportTO rto = (ReportTO)to;
			pstmt = c.prepareStatement("update report set final_date=? where id=?");
			pstmt.setTimestamp(1, DateUtil.getNow());
			pstmt.setString(2, rto.getId());
			pstmt.executeUpdate();
												
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally{
			super.closeStatement(null, pstmt);
		}        
    }

    
    /**
     * Create a new TO object based on data into result set.
     */
    protected ReportTO populateBeanByResultSet(ResultSet rs) throws DataAccessException{
        ReportTO response = new ReportTO();
        response.setId(getString(rs, "id"));
        response.setExecutionHour(getInteger(rs, "execution_hour"));
        response.setFinalDate(getTimestamp(rs, "final_date"));
        response.setLastExecution(getTimestamp(rs, "last_execution"));
        response.setName(getString(rs, "name"));
        response.setProject(new ProjectTO(getString(rs, "project_id")));
        response.setReportPerspectiveId(getString(rs, "report_perspective_id"));
        response.setSqlStement(getString(rs, "sql_text"));
        response.setType(getInteger(rs, "type"));
        response.setDataType(getInteger(rs, "data_type"));
        response.setReportFileName(getString(rs, "file_name"));
        response.setProfile(getString(rs, "profile_view"));
        response.setGoal(getString(rs, "goal"));
        response.setTolerance(getString(rs, "tolerance"));
        response.setToleranceType(getString(rs, "tolerance_type"));

        String categoryId = getString(rs, "category_id");
        if (categoryId!=null) {
            response.setCategory(new CategoryTO(categoryId));
        }
        
        return response;
    }

    private CategoryTO getCategory(CategoryTO category) throws DataAccessException{
        CategoryTO response = new CategoryTO();
	    if (category!=null) {
	        CategoryDAO cdao = new CategoryDAO();
	        CategoryTO buff = (CategoryTO)cdao.getObject(category);
	        if (buff!=null) {
	            response = buff;
	        }
	    }
	    return response;
    }
    
    
    /**
     * Create a new TO object based on data into result set.
     */
    private BSCReportTO populateBSCReportByResultSet(ResultSet rs) throws DataAccessException{
    	BSCReportTO response = new BSCReportTO();
    	ReportResultDAO rrdao = new ReportResultDAO();
    	
    	ProjectTO pto = new ProjectTO(getString(rs, "project_id"));
    	pto.setName(getString(rs, "project_name"));
    	
        ReportTO kpi = new ReportTO();
        kpi.setId(getString(rs, "id"));
        kpi.setLastExecution(getTimestamp(rs, "last_execution"));
        kpi.setName(getString(rs, "name"));
        kpi.setProject(pto);
        kpi.setReportPerspectiveId(getString(rs, "report_perspective_id"));
        kpi.setDataType(getInteger(rs, "data_type"));
        String categoryId = getString(rs, "category_id");
        if (categoryId!=null) {
            kpi.setCategory(new CategoryTO(categoryId));
        }
        kpi.setGoal(getString(rs, "goal"));
        kpi.setTolerance(getString(rs, "tolerance"));
        kpi.setToleranceType(getString(rs, "tolerance_type"));
        kpi.setFinalDate(getTimestamp(rs, "final_date"));
        response.setKpi(kpi);   

        response.setKpiWeight(getInteger(rs, "weight"));
        response.setStrategyName(getString(rs, "strategy"));
        response.setStrategyId(getString(rs, "occurrence_id"));
        
        ReportResultTO rrto = rrdao.populateReanByResultSet(rs);
        response.setResult(rrto);
        
        return response;
    }
    
}