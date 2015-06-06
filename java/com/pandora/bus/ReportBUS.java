package com.pandora.bus;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.BSCReportTO;
import com.pandora.CategoryTO;
import com.pandora.DBQueryResult;
import com.pandora.ProjectTO;
import com.pandora.ReportFieldTO;
import com.pandora.ReportResultTO;
import com.pandora.ReportTO;
import com.pandora.RootTO;
import com.pandora.UserTO;
import com.pandora.dao.ReportDAO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.DuplicatedKpiTypeException;
import com.pandora.exception.EmptyReportBusinessException;
import com.pandora.exception.EmptyReportException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;

/**
 * This class contain the business rules related with Report entity.
 */
public class ReportBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    ReportDAO dao = new ReportDAO();
    

    /**
     * Get a list of all Report or Kpi objects from data base.
     */
    public Vector<ReportTO> getListBySource(boolean isKpi, String categoryId, ProjectTO pto, boolean includeClosed) throws BusinessException {
        Vector<ReportTO> response = new Vector<ReportTO>();
        try {
            response = dao.getListBySource(isKpi, categoryId, pto, includeClosed);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    /**
     * Get a list of all Report TOs from data base, based on time range and area.
     */
    public Vector<ReportTO> getReportListBySearch(Timestamp initialDate, Timestamp finalDate, String reportPerspectiveId, String projectId, String categoryId) throws BusinessException {
        ProjectBUS pbus = new ProjectBUS();
        Vector<ReportTO>  response = new Vector<ReportTO>();
        try {
            
            //get the reports of child projects 
            Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(projectId), true);
            Iterator<ProjectTO> i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = i.next();
                Vector<ReportTO> resOfChild = this.getReportListBySearch(initialDate, finalDate, reportPerspectiveId, childProj.getId(), categoryId);
                response.addAll(resOfChild);
            }

            response.addAll(dao.getReportListBySearch(initialDate, finalDate, reportPerspectiveId, projectId, categoryId));
            
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    public void getKpiValues(Timestamp initialDate, Timestamp finalDate, ReportTO kpiObj, String projectId) throws BusinessException {
        try {
        	dao.getKpiValues(initialDate, finalDate, kpiObj, projectId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }
    
    /**
     * Get a Report object from data base.
     */
    public ReportTO getReport(ReportTO filter) throws BusinessException {
        ReportTO response = null;
        try {
            response = (ReportTO) dao.getObject(filter);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Insert a new Report object into data base.
     */
    public void insertReport(ReportTO rto) throws BusinessException {
        try {
        	//this.checkDuplicateKpiType(rto);
            dao.insert(rto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }

    //TODO refatorar...
	private void checkDuplicateKpiType(ReportTO rto) throws BusinessException, DuplicatedKpiTypeException {
		if (rto!=null && rto.getKpiType()!=null && !rto.getKpiType().equals("") && !rto.getKpiType().equals("0")) {
			Vector<ReportTO> list = this.getReportByKpiType(rto.getKpiType());
			for (ReportTO item : list) {

				if (rto.getId()==null || (rto.getId()!=null && !item.getId().equals(rto.getId())  )) {
					for (ProjectTO pto1 : item.getAppliedProjectList()) {
						for (ProjectTO pto2 : rto.getAppliedProjectList()) {
							System.out.println("pto1:" + pto1.getId() + " pto2:" + pto2.getId());
							
							if (pto1!=null && pto2!=null && rto.getKpiType()!=null && item.getKpiType()!=null &&
									pto1.getId().equals(pto2.getId()) && item.getKpiType().equals(rto.getKpiType())) {
								throw new DuplicatedKpiTypeException();
							}
						}
					}					
				}
				
			}
		}
	}

    
    private Vector<ReportTO> getReportByKpiType(Integer kpiType) throws BusinessException {
        try {            
            return dao.getReportByKpiType(kpiType);     
        } catch (Exception e) {
            throw new  BusinessException(e);
        }                
	}

    
	/**
     * Update data of a Report object from data base.
     */
    public void updateReport(ReportTO rto) throws BusinessException {
        try {
        	//this.checkDuplicateKpiType(rto);
            dao.update(rto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }


    /**
     * Remove a report object from data base.
     */
    public void removeReport(ReportTO rto) throws BusinessException {
        try {
            dao.remove(rto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }

    
    public byte[] performReport(ReportTO rto) throws BusinessException {
        try {            
            return dao.performJasperReport(rto);     
        } catch (EmptyReportException e) {
            throw new  EmptyReportBusinessException(e);
        } catch (Exception e) {
            throw new  BusinessException(e);
        }        
    }

    public Vector<ReportResultTO> performSQLByReportField(ReportFieldTO field, UserTO uto) throws BusinessException {
        try {            
            return dao.performSQLByReportField(field, uto);     
        } catch (Exception e) {
            throw new  BusinessException(e);
        }                
    }
    
    /**
     * Perform the sql statement of kpi object and put the result into ReportResult object.
     */
    public void performKPI() throws BusinessException {       
        try {
		    //get from database all Reports that must be performed now...
		    Vector<ReportTO> kpiList = dao.getReportListToPerfom();
		    
		    //for each report, execute it!
		    Iterator<ReportTO> i = kpiList.iterator();
		    while(i.hasNext()){
		        long initTime = DateUtil.getNow().getTime();
		        ReportTO rto = i.next();
		        String currentProjectId = rto.getProject().getId();
		        
		        //calculate the new lastExecution date 
		        //Increment 24hs (if is not null) or get the current timestamp (is is null)
		        Timestamp execution = rto.getLastExecution();
		        if (execution==null){
		            execution = DateUtil.getNow();
		        } else {
		            execution = DateUtil.getChangedDate(execution, Calendar.HOUR, 24);    
		        }
		        
		        Vector<ProjectTO> list = rto.getAppliedProjectList();
	    		for (ProjectTO pto : list) {
	    			rto.setResultList(null);
	    			
	    			ReportResultTO rsto = this.performKPI(rto, pto);
			        if (rsto!=null) {
				        rto.addResultList(rsto);
				        				        
				        //update the result object into data base and lastExecution information
				        rto.setLastExecution(execution);
				        rto.setProject(new ProjectTO(currentProjectId));
				        rsto.setLastExecution(execution);
				        rsto.setReportId(rto.getId());
				        rsto.setProjectId(pto.getId());
				        dao.update(rto);
				        
				        long elapsedTime = (DateUtil.getNow().getTime()-initTime);
			            LogUtil.log(LogUtil.SUMMARY_KPI_GENERATE, this, RootTO.ROOT_USER, LogUtil.LOG_INFO, 
			                    "The KPI [" + rto.getName() + "] was successfully executed. Value:[" + rsto.getValue() + 
			                    "] Next Execution:[" + DateUtil.getDateTime(execution, new Locale("en", "US"),2,2) +
			                    "] Elapsed time (ms):[" + elapsedTime + "]");		        		
			        } else {

			        	long elapsedTime = (DateUtil.getNow().getTime()-initTime);
			        	LogUtil.log(LogUtil.SUMMARY_KPI_GENERATE, this, RootTO.ROOT_USER, LogUtil.LOG_INFO, 
			                    "The query of KPI [" + rto.getName() + "] returned NOTHING, then the cursor will not be increased. " + 
			                    "Next Execution:[" + DateUtil.getDateTime(rto.getLastExecution(), new Locale("en", "US"),2,2) +
			                    "] Elapsed time (ms):[" + elapsedTime + "]");		        	
			        }
	    			
	    		}
		    }
                
        } catch (DataAccessException e) {
        	e.printStackTrace();
            throw new  BusinessException(e);
        }
    }

    
    private ReportResultTO performKPI(ReportTO rto, ProjectTO pto) throws DataAccessException {
    	ReportResultTO response = null;
        DbQueryBUS dbBus = new DbQueryBUS();
    	try {
    		
    		Vector<Integer> types = new Vector<Integer>();
    		Vector<Object> values = new Vector<Object>();
    		int[] sqlTypes = null;
    		
    		rto.setProject(pto);
    		String sql = getStatementValues(rto.getSqlStement(), rto, types, values);
            if (types!=null) {
            	sqlTypes = new int[types.size()];
    	        for (int j=0; j<types.size();j++){
    	        	sqlTypes[j] = ((Integer)types.elementAt(j)).intValue();
    	        }
            }

            DBQueryResult resp = dbBus.performQuery(sql, sqlTypes, values);
            
            if (resp!=null && !resp.isEmpty()) {
            	Vector<Object> line = ((Vector<Object>)resp.getData(0));
			    String value = "";
			    try {
				    if (rto.getDataType().equals(ReportTO.FLOAT_DATA_TYPE) || 
				            rto.getDataType().equals(ReportTO.CURRENCY_DATA_TYPE)) {
				    	value = (String)line.get(0);
			            
				    } else if (rto.getDataType().equals(ReportTO.PERCENTUAL_DATA_TYPE)) {
				    	value = (String)line.get(0);
				    	try {
				    		if (StringUtil.checkIsFloat(value, ReportTO.KPI_DEFAULT_LOCALE)) {
				    			float f = StringUtil.getStringToFloat(value, ReportTO.KPI_DEFAULT_LOCALE);
				    			value = StringUtil.getFloatToString(f, "0.00", ReportTO.KPI_DEFAULT_LOCALE);
				    		}
				    	}catch(Exception e){
				    		value = null;
				    	}
				    	
				    } else if (rto.getDataType().equals(ReportTO.DATE_DATA_TYPE)) {
						String str = (String)line.get(0);	
						if (str != null){
						    Timestamp v = Timestamp.valueOf(str);	
						    value = DateUtil.getDate(v, ReportTO.KPI_DEFAULT_MASK, ReportTO.KPI_DEFAULT_LOCALE);
						}			        
				    }			        
			    } catch(Exception e){
			        value = "";
			    }
			    
			    if (value!=null) {
				    response = new ReportResultTO();
				    response.setValue(value);
				    response.setProjectId(pto.getId());			    	
			    }
			    
	            LogUtil.log(LogUtil.SUMMARY_KPI_GENERATE, this, RootTO.ROOT_USER, LogUtil.LOG_INFO, 
	                    "The KPI [" + rto.getName() + "] for project [" + pto.getId() + 
	                    "] was performed returning value:[" + value + "]");    			    
			}     			
    		
    	} catch(Exception e){
    		throw new DataAccessException(e);
    	}
        return response;        
    }
    

    /**
     * This method parse the query of a report and return a list of filter fields
     * that was extracted from it. 
     */
    public Vector<ReportFieldTO> getReportFields(String content) {
        Vector<ReportFieldTO> response = new Vector<ReportFieldTO>();
        String completeField = "";
        int iniIdx = 0;
        
        if (content!=null) {
            iniIdx = content.indexOf("?#");
            while (iniIdx>0) {
                int finIdx = content.indexOf("#", iniIdx+2);
                if (finIdx>0) {
                    int nextIniIdx = content.indexOf("?#", iniIdx+2);                
                    if (finIdx-1==nextIniIdx) {
                        int newFinIdx = content.indexOf("!}(", finIdx+2);
                        if (newFinIdx>1) {
                            completeField = content.substring(iniIdx+2, newFinIdx+5);
                            response.addAll(getReportFields(completeField));
                        }
                    } else {
                        completeField = content.substring(iniIdx+2, finIdx);                    
                    }
                    
                    if (!completeField.equals("")) {
                        int separator = completeField.indexOf("{");
                        ReportFieldTO field = new ReportFieldTO();
                        String value = null;
                        if (separator<0) {
                            separator = completeField.length();
                        } else {
                           value = completeField.substring(separator+1);                                    
                        }
                        String param = completeField.substring(0, separator);
                        field.setId(param.trim());
                        if (value!=null) {
                            String type = null;                            
                            int tsep =  value.lastIndexOf("}(");
                            if (tsep>=0) {
                                type = value.substring(tsep+2, value.length()-1);
                                field.setReportFieldType(type);
                                field.setLabel(value.substring(0, tsep).trim());                                
                            } else {
                                field.setLabel("");
                            }
                        }
                        
                        //define visibility of GUI fields 
                        //(note: the fields with key bellow must not be used into report filter GUI)
                        if (("#"+field.getId()+"#").equalsIgnoreCase(ReportTO.PROJECT_ID)) {
                            field.setVisible(false);
                        } if (("#"+field.getId()+"#").equalsIgnoreCase(ReportTO.USER_ID)) {
                        	field.setVisible(false);
                        } if (("#"+field.getId()+"#").equalsIgnoreCase(ReportTO.PROJECT_DESCENDANT)) {
                        	field.setVisible(false);                        	
                        }
                        
                        response.add(field);                    
                    }
                    
                    completeField = "";
                    iniIdx = content.indexOf("?#", finIdx+1);
                } else {
                    iniIdx = -1;    
                }
            }            
        }
        return response;
    }
    

    /**
     * Get a list of KPIs based to the occurrence ID. This method uses occurrence_kpi table .
     */
	public Vector<ReportTO> getKpiByOccurrence(String occurrenceId) throws BusinessException{
        Vector<ReportTO> response = new Vector<ReportTO>();
        try {
            response = dao.getKpiByOccurrence(occurrenceId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

	
	public Vector<BSCReportTO> getBSC(Timestamp refDate, ProjectTO pto, CategoryTO cto, boolean onCascade) throws BusinessException {
        Vector<BSCReportTO> response = new Vector<BSCReportTO>();
        try {
        	response = dao.getBSC(refDate, pto, cto, onCascade);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

	/**
	 * This method calculate the status of kpi based to the tolerance and the goal. 
	 * It returns the following values:
	 * <li>0 = means that there is not data enough to calcalate status.</li>
	 * <li>1  = the value is bad. It is worst than goal and tolerance.</li>
	 * <li>2  = the value is medium. It is worst than goal but is into the tolerance.</li>
	 * <li>3  = the value is good. It is better than goal.</li>   
	 */
	public int getKpiStatus(float value, float goal, float tolerance, String type){
	    int result = 0;
	    
	    if (type!=null) {
		    float variation = 0;
		    boolean isUnit = type.equals("1") || type.equals("2") || type.equals("3");
		    boolean isPercentage = type.equals("4") || type.equals("5") || type.equals("6");	    

			if (isUnit) {
			    variation = tolerance;
			} else if (isPercentage){
			    variation = (tolerance/100) * value;
			}

			if (isUnit || isPercentage) {

				boolean isMoreLess = type.equals("1") || type.equals("4");
			    boolean isMax = type.equals("2") ||	type.equals("5");
			    boolean isMin = type.equals("3") ||	type.equals("6");	    

				if (isMoreLess) {
					
					if (value == goal) {
						result = 3;					
					} else {
						result = ((value <= (goal + variation) && value >= (goal - variation)) ? 2 : 1);	
					}
					 
				} else if (isMax) {

					if (value <= goal) {
						result = 3;
					} else {
						result = ((value <= (goal + variation)) ? 2 : 1);	
					}

				} else if (isMin) {

					if (value >= goal) {
						result = 3;
					} else {
						result = ((value >= (goal - variation)) ? 2 : 1);	
					}
				}
			}
	    }
		
		return result;
	}
	
	
	public int getKpiStatus(Timestamp value, Timestamp goal, float tolerance, String type){
	    int result = 0;
	    
	    if (type!=null) {
		    boolean isUnit = type.equals("1") || type.equals("2") || type.equals("3");
		    
			if (isUnit) {
				boolean isMoreLess = type.equals("1");
			    boolean isMax = type.equals("2");
			    boolean isMin = type.equals("3");	    

				if (isMoreLess) {
					
					if (value.equals(goal)) {
						result = 3;
					} else {
						Timestamp upperTolerance = DateUtil.getChangedDate(goal, Calendar.DATE, (int)tolerance); 
						Timestamp lowerTolerance = DateUtil.getChangedDate(goal, Calendar.DATE, -(int)tolerance);
						result = ((value.before(upperTolerance) && value.after(lowerTolerance)) ? 2 : 1);	
					}
					 
				} else if (isMax) {

					if (!value.after(goal)) {
						result = 3;
					} else {
						Timestamp upperTolerance = DateUtil.getChangedDate(goal, Calendar.DATE, (int)tolerance); 
						result = (value.before(upperTolerance) ? 2 : 1);	
					}

				} else if (isMin) {

					if (!value.before(goal)) {
						result = 3;
					} else {
						Timestamp lowerTolerance = DateUtil.getChangedDate(goal, Calendar.DATE, -(int)tolerance);
						result = (value.after(lowerTolerance) ? 2 : 1);	
					}
				}
			}
	    }
		
		return result;
	}
	
	
    public static String getStatementValues(String sqlStatement, ReportTO rto, Vector<Integer> types, Vector<Object> values) throws BusinessException {
    	String cleanedSql = sqlStatement;
        int i = 0;
        String token = "";

	    String rawSql = new String(replaceStatement(sqlStatement, rto));
	    cleanedSql = cleanKeyWordsFromSQL(sqlStatement, rto);

        while(i>=0){
            i = rawSql.indexOf("?", i+1);
            if (i>0) {
                int endBlockIdx = rawSql.indexOf("#", i+2);
                if (endBlockIdx>0){
                    token = rawSql.substring(i+1, endBlockIdx+1);
                    i = endBlockIdx;
                    
                    if (token.equals(ReportTO.PROJECT_ID)){
                    	types.addElement(new Integer(Types.VARCHAR));
                    	values.addElement(rto.getProject().getId());

                    } else if (token.equals(ReportTO.INITIAL_RANGE)){
                    	types.addElement(new Integer(Types.TIMESTAMP));
                    	values.addElement(getRange(rto, true));
                    	
                    } else if (token.equals(ReportTO.FINAL_RANGE)){
                    	types.addElement(new Integer(Types.TIMESTAMP));
                    	values.addElement(getRange(rto, false));
                    
                    } else if (token.equals(ReportTO.USER_ID)){
                    	types.addElement(new Integer(Types.VARCHAR));
                    	values.addElement(rto.getHandler().getId());
                    	
                    } else {
                        //also, search the token into the fields of report...                    	
                        token = token.replaceAll("#", "");
                        
                        ReportFieldTO field = rto.getReportField(token);
                        if (field!=null && field.getReportFieldType()!=null 
                        		&& !field.getReportFieldType().equals(ReportFieldTO.TYPE_OBJECT)) {
                        	
                        	String mask = "dd/MM/yyyy";
                        	if (rto.getHandler()!=null && rto.getHandler().getCalendarMask()!=null) {
                        		mask = rto.getHandler().getCalendarMask();
                        	}
                        	
                            if (!rto.setValueIntoPreparedStatement(token, mask, rto.getLocale(), types, values)) {
                                throw new BusinessException("The Token [" + token + "] of Report is unknown. It cannot be converted to a value.");
                            }
                        }
                    }

                }
            }
        }
        	    		
		return cleanedSql;
    }
	
    
    private static String cleanKeyWordsFromSQL(String sqlStatement, ReportTO rto) throws BusinessException {
    	ProjectDelegate pdel = new ProjectDelegate();
	    String sql = replaceStatement(sqlStatement, rto);
	    
	    //remove the wildcards of sql statement
	    sql = sql.replaceAll(ReportTO.PROJECT_ID, "");
	    sql = sql.replaceAll(ReportTO.INITIAL_RANGE, "");
	    sql = sql.replaceAll(ReportTO.FINAL_RANGE, "");
	    sql = sql.replaceAll(ReportTO.USER_ID, "");
        
	    //replace the project descendant key to a lista of projects id's
	    int pd = sql.indexOf(ReportTO.PROJECT_DESCENDANT);
	    if (pd > -1) {
	    	String inList = pdel.getProjectIn(rto.getProject().getId());
	    	sql = sql.substring(0, pd-1) + sql.substring(pd);
	    	sql = sql.replaceAll(ReportTO.PROJECT_DESCENDANT, inList);
	    }
	    
	    //replace the specific report wildcards to empty 
	    if (rto.getFormFieldsValues()!=null) {
	        sql = removeKeywordsFromReport(sql, rto);
	    }    	
	    
	    return sql;
    }
    

    public static String replaceStatement(String sql, ReportTO rto) throws BusinessException {
        int i = 0;
        String token = "";
        String response = sql;

        while(i>=0){
            i = sql.indexOf("?", i+1);
            if (i>0) {
                int endBlockIdx = sql.indexOf("#", i+2);
                if (endBlockIdx>0){
                    token = sql.substring(i+1, endBlockIdx+1);
                    i = endBlockIdx;
                    token = token.replaceAll("#", "");

                    ReportFieldTO field = rto.getReportField(token);
                    if (field!=null && field.getReportFieldType()!=null && field.getReportFieldType().equals(ReportFieldTO.TYPE_OBJECT)) {
                    	response = response.replaceAll("\\?#" + token + "#", field.getValue());
                    }
                }
            }
        }
        
        return response;
    }    
    
    
    /**
     * Remove the wildcards from sql statement.  
     */
    private static String removeKeywordsFromReport(String sql, ReportTO rto) {
        Iterator<ReportFieldTO> i = rto.getFormFieldsValues().iterator();
        while(i.hasNext()) {
            ReportFieldTO field = i.next();
            sql = sql.replaceAll("#" + field.getId() + "#", "");
        }
        return sql;
    }
    
    
    /**
     * Return the current timestamp value based on the type of report
     */
    private static Timestamp getRange(ReportTO rto, boolean isInitial) throws BusinessException{
        Timestamp response = null;
        if (rto.getLastExecution()==null){
            response = DateUtil.getNow();
            response = DateUtil.getChangedDate(response, Calendar.DATE, -1);
        } else {
            response = new Timestamp(rto.getLastExecution().getTime());    
        }
        
        if (rto.getType()!=null){
            if (rto.getType().equals(ReportTO.DAILY_TYPE)){
            	response = DateUtil.getDate(response, isInitial);
            } else {
                throw new BusinessException("The Report Type contain a invalid value");
            }            
        } else {
            throw new BusinessException("The Report Type cannot be null");
        }
        
        return response;
    }
    
}
