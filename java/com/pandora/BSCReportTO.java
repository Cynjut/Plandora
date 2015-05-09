package com.pandora;

public class BSCReportTO extends TransferObject {

	private static final long serialVersionUID = 1L;
	
	private ReportTO kpi;
	
	private String strategyName;
	
	private String strategyId;
	
	private Integer kpiWeight;
	
	private ReportResultTO result;
	

	public String getProjectName() {
	    String response = "";
	    if (kpi!=null) {
	        ProjectTO pto = kpi.getProject();
	        if (pto!=null) {
	            response = pto.getName();    
	        }
	    }
		return response;
	}

	public String getProjectId() {
	    String response = "";
	    if (kpi!=null && kpi.getProject()!=null) {
            response = kpi.getProject().getId();    
	    }
		return response;
	}
	
	
	public Integer getKpiDataType(){
		Integer response = null;
		if (this.kpi!=null && this.kpi.getDataType()!=null){
			response = this.kpi.getDataType();
		}
		return response;
	}

	public String getKpiTolerance(){
		String response = null;
		if (this.kpi!=null && this.kpi.getTolerance()!=null){
			response = this.kpi.getTolerance();
		}
		return response;
	}

	public String getKpiToleranceType(){
		String response = null;
		if (this.kpi!=null && this.kpi.getToleranceType()!=null){
			response = this.kpi.getToleranceType();
		}
		return response;
	}

	public String getKpiGoal(){
		String response = null;
		if (this.kpi!=null && this.kpi.getGoal()!=null){
			response = this.kpi.getGoal();
		}
		return response;
	}
	
	///////////////////////////////////////////
	public ReportTO getKpi() {
		return kpi;
	}
	public void setKpi(ReportTO newValue) {
		this.kpi = newValue;
	}

	
	///////////////////////////////////////////	
	public String getStrategyName() {
		return strategyName;
	}
	public void setStrategyName(String newValue) {
		this.strategyName = newValue;
	}

	
	///////////////////////////////////////////	
    public String getStrategyId() {
        return strategyId;
    }
    public void setStrategyId(String newValue) {
        this.strategyId = newValue;
    }
    
    
	///////////////////////////////////////////	
	public Integer getKpiWeight() {
		return kpiWeight;
	}
	public void setKpiWeight(Integer newValue) {
		this.kpiWeight = newValue;
	}
	
	
	///////////////////////////////////////////		
	public ReportResultTO getResult() {
		return result;
	}
	public void setResult(ReportResultTO newValue) {
		this.result = newValue;
	}

	
	
}
