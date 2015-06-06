package com.pandora.gui.struts.form;

import java.sql.Timestamp;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.UserTO;
import com.pandora.helper.DateUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;


/**
 * This class handle the data of Report Form
 */
public class ReportForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
    /** The Name of Report */
    private String name;
    
    private String description;
    
    /** Type of report (daily, monthly, weekly) */
    private String type;

    /** Type of KPI (custom KPI, CPI, SPI, etc) */
    private String kpiType;
    
    /** BSC Report Perspective id: Financial, Process, Customer or Training */ 
    private String reportPerspectiveId;
    
    /** SQL Statement to be executed */
    private String sqlStement;
    
    /** Hour of day (0-23) that report must be executed */
    private String executionHour;
    
    /** last execution of report */
    private String lastExecution;
    
    /** Project related */
    private String projectId;
    private String appliedProjectList;
    
    
    /** DateType of report result (date, number, etc) */
    private String dataType;
    
    /** if 'on' is formated like a KPI form (default value), otherwise, 
     * should be formated like a plain report form */
    private String isKpiForm;
    
    /** The jasper absolute path name */
    private String reportFileName;

    /** Category related */
    private String categoryId;

    /** Profile view */
    private String profile;
        
    
    private String goal;
    
    private String tolerance;
    
    private String toleranceScale;
    
    private String toleranceUnit;
    
    
    private boolean hideClosedReport;
    
    /**
     * Clear values of Form
     */
    public void clear(){
    	this.id = null;
        this.name = "";
        this.description = "";
        this.type = "";
        this.appliedProjectList = null;
        this.reportFileName = "";
        this.reportPerspectiveId = "";
        this.sqlStement = "";
        this.executionHour = "";
        this.projectId = "";
        this.categoryId = "";
        this.setSaveMethod(null, null);
        this.dataType = "0";
        this.profile = null;
        this.goal = "";
        this.toleranceUnit = "1";
        this.toleranceScale = "1";
        this.tolerance="";
    }    
    
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	this.hideClosedReport = false;
	}    
    
    
    public boolean getBoolIsKpiForm(){
        boolean response = true;
        if (this.isKpiForm!=null) {
            response = this.isKpiForm.equalsIgnoreCase("on");
        }
        return response;
    }
    
    ///////////////////////////////////////////////////
    public String getExecutionHour() {
        return executionHour;
    }
    public void setExecutionHour(String newValue) {
        this.executionHour = newValue;
    }
    
    ///////////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

    
    ///////////////////////////////////////////////////    
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }

    
    ///////////////////////////////////////////////////    
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
    
    
    ///////////////////////////////////////////////////    
	public String getAppliedProjectList() {
		return appliedProjectList;
	}
	public void setAppliedProjectList(String newValue) {
		this.appliedProjectList = newValue;
	}
    
    
    ///////////////////////////////////////////////////    
    public String getReportPerspectiveId() {
        return reportPerspectiveId;
    }
    public void setReportPerspectiveId(String newValue) {
        this.reportPerspectiveId = newValue;
    }
    
    ///////////////////////////////////////////////////    
    public String getSqlStement() {
        return sqlStement;
    }
    public void setSqlStement(String newValue) {
        this.sqlStement = newValue;
    }
    
    ///////////////////////////////////////////////////    
    public String getType() {
        return type;
    }
    public void setType(String newValue) {
        this.type = newValue;
    }
    
    ///////////////////////////////////////////////////        
    public String getLastExecution() {
        return lastExecution;
    }
    public void setLastExecution(String newValue) {
        this.lastExecution = newValue;
    }
    
    ///////////////////////////////////////////////////            
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String newValue) {
        this.dataType = newValue;
    }
    
    ///////////////////////////////////////////////////      
    public String getIsKpiForm() {
        return isKpiForm;
    }
    public void setIsKpiForm(String newValue) {
        this.isKpiForm = newValue;
    }
    
    
    ///////////////////////////////////////////////////          
    public String getReportFileName() {
        return reportFileName;
    }
    public void setReportFileName(String newValue) {
        this.reportFileName = newValue;
    }
    
    
    ///////////////////////////////////////////////////       
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String newValue) {
        this.categoryId = newValue;
    }


    ///////////////////////////////////////////////////      
	public String getProfile() {
		return profile;
	}
	public void setProfile(String newValue) {
		this.profile = newValue;
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
	public String getToleranceUnit() {
		return toleranceUnit;
	}
	public void setToleranceUnit(String newValue) {
		this.toleranceUnit = newValue;
	}
    
	
    /////////////////////////////////////////////////// 
	public String getToleranceScale() {
		return toleranceScale;
	}
	public void setToleranceScale(String newValue) {
		this.toleranceScale = newValue;
	}
	
	
    //////////////////////////////////////////  
	public boolean getHideClosedReport() {
		return hideClosedReport;
	}
	public void setHideClosedReport(boolean newValue) {
		this.hideClosedReport = newValue;
	}	

	
    ////////////////////////////////////////
    public String getKpiType() {
        return kpiType;
    }
    public void setKpiType(String newValue) {
        this.kpiType = newValue;
    }
    
    
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		if (this.operation.equals("saveReport")) {
			
			if (this.name==null || this.name.trim().equals("")){             	
			    errors.add("Nome", new ActionError("validate.manageReport.name") );
			}

			if (this.sqlStement==null || this.sqlStement.trim().equals("")){             	
			    errors.add("Nome", new ActionError("validate.manageReport.sql") );
			}

			if (isKpiForm!=null && isKpiForm.equals("on")) {
				
				if (this.dataType.equals("1") && this.toleranceUnit.equals("2")) {
					errors.add("", new ActionError("validate.manageReport.percdate") );	
				}

				if ((this.appliedProjectList==null || this.appliedProjectList.trim().equals("")) && projectId.equals("0")) {
					errors.add("", new ActionError("validate.manageReport.projList") );	
				}
				
				UserTO uto = SessionUtil.getCurrentUser(request);
				Locale loc = SessionUtil.getCurrentLocale(request);				
				if (this.dataType.equals("1")) {
					Timestamp ts = null;
					try {
						ts = DateUtil.getDateTime(this.goal, uto.getCalendarMask(), loc);	
					} catch (Exception e){
						ts = null;
					}
					
					if (ts==null) {
						errors.add("", new ActionError("validate.manageReport.goal") );	
					}
					
				} else if (this.dataType.equals("0") || this.dataType.equals("2") || this.dataType.equals("3")) {
					if (!StringUtil.checkIsFloat(this.goal, loc)){
						errors.add("", new ActionError("validate.manageReport.goal") );	
					}
				}
			}
		}
		return errors;		
	}
		
}
