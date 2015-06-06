package com.pandora;

import java.sql.Timestamp;
import java.util.Locale;

import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

/**
 * This object it is a bean that represents an ReportResult entity.
 */
public class ReportResultTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    /** Id of report performed */
    private String reportId;

   
    /** Id of project performed */
    private String projectId;
    private String projectName;
    
    /** Timestamp of last execution of report */
    private Timestamp lastExecution;
    
    /** Value returned by the performed report */
    private String value;

    /**
     * Constructor 
     */
    public ReportResultTO(){
    }

    /**
     * Constructor 
     */    
    public ReportResultTO(String id){
        this.setId(id);
    }
    
    ///////////////////////////////////////////
    public Timestamp getLastExecution() {
        return lastExecution;
    }
    public void setLastExecution(Timestamp newValue) {
        this.lastExecution = newValue;
    }
    
    ///////////////////////////////////////////    
    public String getReportId() {
        return reportId;
    }
    public void setReportId(String newValue) {
        this.reportId = newValue;
    }
    
    
    ///////////////////////////////////////////    
    public String getValue() {
        return value;
    }
    public void setValue(String newValue) {
        this.value = newValue;
    }
    
    
    ///////////////////////////////////////////    
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}    
    
	
    ///////////////////////////////////////////  
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String newValue) {
		this.projectName = newValue;
	}



    /**
     * Convert ReportResult value to html format
     */
    public StringBuffer convertToHtml(Locale loc, String mask, Integer dataType, Locale currencyLoc) {
        return new StringBuffer("<td class=\"tableCell\" align=\"center\" valign=\"top\">" + 
        		format(loc, mask, dataType, this.getValue(), currencyLoc) + "</td>");
    }

    /**
     * Convert ReportResult value to formated content
     * @return
     */
    public static StringBuffer format(Locale loc, String mask, Integer dataType, String val, Locale currencyLoc) {
        StringBuffer sb = new StringBuffer();
        String value = "";

        try {
            if (val!=null) {
                if (dataType.equals(ReportTO.FLOAT_DATA_TYPE)){
                    value = StringUtil.getFloatToString(Float.parseFloat(val), ReportTO.KPI_DEFAULT_LOCALE);   
                } else if (dataType.equals(ReportTO.DATE_DATA_TYPE)){
                    Timestamp ts = DateUtil.getDateTime(val, ReportTO.KPI_DEFAULT_MASK, ReportTO.KPI_DEFAULT_LOCALE);
                    value = DateUtil.getDate(ts, mask, loc);
                } else if (dataType.equals(ReportTO.CURRENCY_DATA_TYPE)){
                    value = StringUtil.getCurrencyValue(Float.parseFloat(val), currencyLoc);
                } else if (dataType.equals(ReportTO.PERCENTUAL_DATA_TYPE)){
                	float f = Float.parseFloat(val);
                	value = StringUtil.getFloatToString(f, "0.00", loc) + "%";
                } else {
                    value = val;
                }        	
            }            
        }catch(Exception e) {
            value = "";
        }
        
        return sb.append(value);
    }

    
}
