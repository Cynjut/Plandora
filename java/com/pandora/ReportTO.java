package com.pandora;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.exception.BusinessException;

/**
 * This object it is a bean that represents an Report entity.
 */
public class ReportTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    /** The current PROJECT constant related to report and replaced on execution time  */
    public static final String PROJECT_ID = "#PROJECT_ID#";
    /** The INITAL RANGE related to report and replaced on execution time  */
    public static final String INITIAL_RANGE = "#INITIAL_RANGE#";
    /** The INITAL RANGE related to report and replaced on execution time  */
    public static final String FINAL_RANGE = "#FINAL_RANGE#";
    /** The current user id connected */
    public static final String USER_ID = "#USER_ID#";
    /** The project and his descendants constant related to report and replaced on execution time  */
    public static final String PROJECT_DESCENDANT = "#PROJECT_DESCENDANT#";
        
    
    /** DAILY type of Report */
    public static final Integer DAILY_TYPE = new Integer(1);

    /** Export format of report */
    public static final String REPORT_EXPORT_PDF = "PDF";
    public static final String REPORT_EXPORT_RTF = "RTF";
    public static final String REPORT_EXPORT_ODT = "ODT";
    public static final String REPORT_EXPORT_JPG = "JPG";

    /** Financial perspective  */
    public static final String FINANCIAL_PERSP = "1";
    /** Financial perspective  */
    public static final String CUSTOMER_PERSP  = "2";
    /** Financial perspective  */
    public static final String PROCESS_PERSP   = "3";
    /** Financial perspective  */
    public static final String LEARNING_PERSP  = "4";

    
    /** The dataType of report result is a Float number */
    public static final Integer FLOAT_DATA_TYPE    = new Integer(0);    
    /** The dataType of report result is a Date */
    public static final Integer DATE_DATA_TYPE     = new Integer(1);
    /** The dataType of report result is a Currency */
    public static final Integer CURRENCY_DATA_TYPE = new Integer(2);
    /** The dataType of report result is a Currency */
    public static final Integer PERCENTUAL_DATA_TYPE = new Integer(3);
    
    
    public static final Locale KPI_DEFAULT_LOCALE = new Locale("en", "US");
    public static final String KPI_DEFAULT_MASK   = "yyyyMMdd";
    
    
    /** The Name of Report */
    private String name;
    
    private String description;
    
    /** Type of report (daily, monthly, weekly) */
    private Integer type;

    /** Type of KPI (custom KPI, CPI, SPI, etc) */
    private Integer kpiType;

    /** BSC Report Perspective id: Financial, Process, Customer or Training */ 
    private String reportPerspectiveId;
    
    /** SQL Statement to be executed */
    private String sqlStement;
    
    /** Hour of day (0-23) that report must be executed */
    private Integer executionHour;
    
    /** Timestamp of last execution of report */
    private Timestamp lastExecution;
    
    /** Project related */
    private ProjectTO project;
    private Vector<ProjectTO> appliedProjectList = null;
    
    /** Timestamp of report ending */
    private Timestamp finalDate;
    
    /** The list of report result related with report */
    private Vector<ReportResultTO> resultList;

    /** DateType of report result (date, number, etc) */
    private Integer dataType;
    
    /** The path (absolute path) of Jasper file */
    private String reportFileName;
    
    /** The values selected by used at ViewReport form, indexed by html field id */ 
    private Vector<ReportFieldTO> formFieldsValues = null;
    
    /** The locale of user that is performing the report.*/
    private Locale locale;

    /** Related Category.*/
    private CategoryTO category = new CategoryTO();

    private UserTO handler;    
    
    private String profile;
    
    private String exportReportFormat; //transient attribute

    private String goal;
    
    private String tolerance;
    
    private String toleranceType;

    
    /**
     * Constructor 
     */
    public ReportTO(){
    }

    /**
     * Constructor 
     */    
    public ReportTO(String id){
        this.setId(id);
    }

    
	/**
	 * Convert Report object to html format 
	 * @param list
	 * @return
	 */
	public StringBuffer convertToHtml(String mask, Locale loc, Locale currencyLoc){
	   StringBuffer sb = new StringBuffer();
   	   sb.append("<tr class=\"formBody\">");
   	   sb.append("<td class=\"tableCell\" align=\"right\" valign=\"top\">" + this.getName() + ":</td>");
   	   
   	   if (this.resultList!=null){
   		   Iterator<ReportResultTO> j = this.resultList.iterator();
   		   while(j.hasNext()){
   		       ReportResultTO rrto = j.next();
   		       if (rrto.getValue()==null){
   		           sb.append("<td class=\"tableCell\" align=\"center\" valign=\"top\">-</td>");    
   		       } else {
   		           sb.append(rrto.convertToHtml(loc, mask, this.dataType, currencyLoc));
   		       }
   		   }   	       
   	   } else {
   	       sb.append("<td>&nbsp;</td>");    
   	   }
   	   
   	   sb.append("</tr> \n");
	   return sb;
	}

	
	/**
	 * Remove the domain (braces symbols) of the sql statement.
	 * @return
	 */
    public String getSqlWithoutDomain() {
        sqlStement = this.removeInBetween(sqlStement, "{!", "!}");
        sqlStement = this.removeInBetween(sqlStement, "{", "}");
        return sqlStement;
    }

    private String removeInBetween(String content, String iniToken, String finalToken){
        int i = content.indexOf(iniToken);
        while(i>=0) {
            int f = content.indexOf(finalToken, i+1);
            if (f>=0) {
                String part1 = content.substring(0, i);
                int newFinalToken = f + finalToken.length() + 3;
                if (newFinalToken<=content.length()) {
                    String part2 = content.substring(f + finalToken.length() + 3);
                    content = part1 + part2;
                    i = content.indexOf(iniToken);                    
                } else {
                    i=-1;
                }
            } else {
                i= -1;
            }
        }
        return content;
    }
    

    /**
     * Check if a given token is a id of a report field. 
     * After finding, call the method of reportFieldTO to set the value
     * into the 'value' list and 'type' list to be used by preparedStatement. 
     */
    public boolean setValueIntoPreparedStatement(String token, String mask, Locale loc, Vector<Integer> types, Vector<Object> values) throws BusinessException{
        boolean response = false;
        ReportFieldTO field = this.getReportField(token);
        if (field!=null) {
            response = field.setValueIntoPreparedStatement(mask, loc, types, values);
        }
        return response;
    }
    
    
    public ReportFieldTO getReportField(String token){
    	ReportFieldTO response = null;
    	
        if (this.formFieldsValues!=null) {
            Iterator<ReportFieldTO> i = this.getFormFieldsValues().iterator();
            while(i.hasNext()) {
                ReportFieldTO field = i.next();
                if (field.getId().equals(token)) {
                    response = field;
                    break;
                }
            }
        } 
        
        return response;
    }

   
    
    ////////////////////////////////////////
    public Integer getExecutionHour() {
        return executionHour;
    }
    public void setExecutionHour(Integer newValue) {
        this.executionHour = newValue;
    }
    
    
    ////////////////////////////////////////    
    public Timestamp getFinalDate() {
        return finalDate;
    }
    public void setFinalDate(Timestamp newValue) {
        this.finalDate = newValue;
    }
    
    ////////////////////////////////////////    
    public Timestamp getLastExecution() {
        return lastExecution;
    }
    public void setLastExecution(Timestamp newValue) {
        this.lastExecution = newValue;
    }
    
    
    ////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

    
    ////////////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }

    
    ////////////////////////////////////////    
    public String getReportPerspectiveId() {
        return reportPerspectiveId;
    }
    public void setReportPerspectiveId(String newValue) {
        this.reportPerspectiveId = newValue;
    }
    
    ////////////////////////////////////////
    public String getSqlStement() {
        return sqlStement;
    }
    public void setSqlStement(String newValue) {
        this.sqlStement = newValue;
    }

    ////////////////////////////////////////
    public Integer getType() {
        return type;
    }
    public void setType(Integer newValue) {
        this.type = newValue;
    }
    
    ////////////////////////////////////////
    public Vector<ReportResultTO> getResultList() {
        return resultList;
    }
    public void setResultList(Vector<ReportResultTO> newValue) {
        this.resultList = newValue;
    }
    public void addResultList(ReportResultTO newValue) {
        if (resultList==null){
            resultList = new Vector<ReportResultTO>();
        }
        resultList.addElement(newValue);
    }    
    

    ////////////////////////////////////////    
    public Integer getDataType() {
        return dataType;
    }
    public void setDataType(Integer newValue) {
        this.dataType = newValue;
    }
    
    
    ////////////////////////////////////////        
    public String getReportFileName() {
        return reportFileName;
    }
    public void setReportFileName(String newValue) {
        this.reportFileName = newValue;
    }
    
    ////////////////////////////////////////       
    public Vector<ReportFieldTO> getFormFieldsValues() {
        return formFieldsValues;
    }
    public void setFormFieldsValues(Vector<ReportFieldTO> newValue) {
        this.formFieldsValues = newValue;
    }
    
    ////////////////////////////////////////        
    public Locale getLocale() {
        return locale;
    }
    public void setLocale(Locale newValue) {
        this.locale = newValue;
    }
    
    ////////////////////////////////////////        
    public CategoryTO getCategory() {
        return category;
    }
    public void setCategory(CategoryTO newValue) {
        this.category = newValue;
    }
    
    ////////////////////////////////////    
    public UserTO getHandler() {
        return handler;
    }
    public void setHandler(UserTO newValue) {
        this.handler = newValue;
    }
    
    ///////////////////////////////////////////////////      
	public String getProfile() {
		return profile;
	}
	public void setProfile(String newValue) {
		this.profile = newValue;
	}
	
    ///////////////////////////////////////////////////	
	public String getExportReportFormat() {
		return exportReportFormat;
	}
	public void setExportReportFormat(String newValue) {
		this.exportReportFormat = newValue;
	}

    ///////////////////////////////////////////////////     
	public String getGoal() {
		return goal;
	}
	public void setGoal(String newValue) {
		this.goal = newValue;
	}

    ///////////////////////////////////////////////////     
	public String getTolerance() {
		return tolerance;
	}
	public void setTolerance(String newValue) {
		this.tolerance = newValue;
	}

    ///////////////////////////////////////////////////     
	public String getToleranceType() {
		return toleranceType;
	}
	public void setToleranceType(String newValue) {
		this.toleranceType = newValue;
	}

	
    ///////////////////////////////////////////////////     
	public Vector<ProjectTO> getAppliedProjectList() {
		return appliedProjectList;
	}
	public void addAppliedProjectList(ProjectTO item) {
		if (this.appliedProjectList==null) {
			this.appliedProjectList = new Vector<ProjectTO>();
		}
		this.appliedProjectList.add(item);
	}
	public void clearAppliedProjectList() {
		this.appliedProjectList = null;		
	}


    ////////////////////////////////////////
	public String getDescription() {
		return description;
	}   
	public void setDescription(String newValue) {
		this.description = newValue;
	}
	

    ////////////////////////////////////////
    public Integer getKpiType() {
        return kpiType;
    }
    public void setKpiType(Integer newValue) {
        this.kpiType = newValue;
    }
}
