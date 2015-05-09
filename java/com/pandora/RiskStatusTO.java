package com.pandora;

public class RiskStatusTO extends TransferObject {

	public static String MATERIALIZE_RISK_TYPE = "1";
	
	public static String VOIDED_RISK_TYPE      = "2";

	
	private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String statusType;
    
    
    public RiskStatusTO(String statusId) {
        super.setId(statusId);
    }
    
    
    ////////////////////////////////////////////////
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    ////////////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

    //////////////////////////////////////////////// 
	public String getStatusType() {
		return statusType;
	}
	public void setStatusType(String newValue) {
		this.statusType = newValue;
	}
        
}
