package com.pandora;


/**
 * 
 */
public class RiskTO extends PlanningTO {
	
	public static final Integer RISK_TYPE_THREAT      = new Integer(0);
	public static final Integer RISK_TYPE_OPPORTUNITY = new Integer(1);
	
	
	private static final long serialVersionUID = 1L;

    private String name;
    
    private RiskStatusTO status;

    private ProjectTO project;
    
    private UserTO handler;

    private String responsible;
    
    private CategoryTO category;

    private String probability;
    
    private String impact;

    private String tendency;

    private String strategy;

    private String contingency;
    
    private boolean createIssueLinked = false;
    
    private boolean costImpact;
    
    private boolean qualityImpact;
    
    private boolean scopeImpact;
    
    private boolean timeImpact;
    
    private Integer riskType;
    
    private String lastComment;
    
    /**
     * Constructor 
     */
    public RiskTO(){
    	this.createIssueLinked = false;
    	this.riskType = RISK_TYPE_THREAT;
    }

    
    /**
     * Constructor 
     */
    public RiskTO(String id){
        this.setId(id);
    }    
    
    
    public String getEntityType(){
        return PLANNING_RISK;
    }

    
    //////////////////////////////////////////
    public CategoryTO getCategory() {
        return category;
    }
    public void setCategory(CategoryTO newValue) {
        this.category = newValue;
    }
    
    
    //////////////////////////////////////////    
    public String getContingency() {
        return contingency;
    }
    public void setContingency(String newValue) {
        this.contingency = newValue;
    }
    
    
    //////////////////////////////////////////    
    public UserTO getHandler() {
        return handler;
    }
    public void setHandler(UserTO newValue) {
        this.handler = newValue;
    }
    
    
    //////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    //////////////////////////////////////////    
    public String getProbability() {
        return probability;
    }
    public void setProbability(String newValue) {
        this.probability = newValue;
    }
    
    
    //////////////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }
    
    
    //////////////////////////////////////////    
    public String getResponsible() {
        return responsible;
    }
    public void setResponsible(String newValue) {
        this.responsible = newValue;
    }
    
    
    //////////////////////////////////////////    
    public String getStrategy() {
        return strategy;
    }
    public void setStrategy(String newValue) {
        this.strategy = newValue;
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
    public RiskStatusTO getStatus() {
        return status;
    }
    public void setStatus(RiskStatusTO newValue) {
        this.status = newValue;
    }

    
    //////////////////////////////////////////    
    public boolean isCreateIssueLinked() {
		return createIssueLinked;
	}
	public void setCreateIssueLinked(boolean newValue) {
		this.createIssueLinked = newValue;
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
	public String getLastComment() {
		return lastComment;
	}
	public void setLastComment(String newValue) {
		this.lastComment = newValue;
	}
	
}
