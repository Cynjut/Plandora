package com.pandora;

import java.sql.Timestamp;

public class RiskHistoryTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    private String riskId;
    
    private String content;
    
    private String riskStatusLabel;
    
    private String riskStatusId;
    
    private String riskStatusType;
    
    private Timestamp creationDate;
    
    private UserTO user;
    
    private String probability;
    
    private String impact;

    private String tendency;
    
    private boolean costImpact;
    
    private boolean qualityImpact;
    
    private boolean scopeImpact;
    
    private boolean timeImpact;
    
    private Integer riskType;
    
    
    
    /////////////////////////////////////////
    public String getContent() {
        return content;
    }
    public void setContent(String newValue) {
        this.content = newValue;
    }
    
    ////////////////////////////////////
    public UserTO getUser() {
        return user;
    }
    public void setUser(UserTO newValue) {
        this.user = newValue;
    }    
    
    ////////////////////////////////////    
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }

    
    /////////////////////////////////////////    
    public String getRiskId() {
        return riskId;
    }
    public void setRiskId(String newValue) {
        this.riskId = newValue;
    }
    
    
    /////////////////////////////////////////    
    public String getRiskStatusLabel() {
        return riskStatusLabel;
    }
    public void setRiskStatusLabel(String newValue) {
        this.riskStatusLabel = newValue;
    }
    
    
    /////////////////////////////////////////      
    public String getRiskStatusId() {
        return riskStatusId;
    }
    public void setRiskStatusId(String newValue) {
        this.riskStatusId = newValue;
    }
    
    
    
    //////////////////////////////////////////    
    public String getProbability() {
        return probability;
    }
    public void setProbability(String newValue) {
        this.probability = newValue;
    }
    
    
    //////////////////////////////////////////    
    public String getTendency() {
        return tendency;
    }
    public void setTendency(String newValue) {
        this.tendency = newValue;
    }

    
    //////////////////////////////////////////    
    public String getImpact() {
        return this.impact;
    }
    public void setImpact(String newValue) {
        this.impact = newValue;
    }


    //////////////////////////////////////////
	public boolean getCostImpact() {
		return costImpact;
	}
	public void setCostImpact(boolean newValue) {
		this.costImpact = newValue;
	}


    //////////////////////////////////////////
	public boolean getQualityImpact() {
		return qualityImpact;
	}
	public void setQualityImpact(boolean newValue) {
		this.qualityImpact = newValue;
	}

	
    //////////////////////////////////////////
	public boolean getScopeImpact() {
		return scopeImpact;
	}
	public void setScopeImpact(boolean newValue) {
		this.scopeImpact = newValue;
	}

	
    //////////////////////////////////////////
	public boolean getTimeImpact() {
		return timeImpact;
	}
	public void setTimeImpact(boolean newValue) {
		this.timeImpact = newValue;
	}

	
    //////////////////////////////////////////
	public Integer getRiskType() {
		return riskType;
	}
	public void setRiskType(Integer newValue) {
		this.riskType = newValue;
	}
	
	
    //////////////////////////////////////////	
	public String getRiskStatusType() {
		return riskStatusType;
	}
	public void setRiskStatusType(String newValue) {
		this.riskStatusType = newValue;
	}
	
	
}
