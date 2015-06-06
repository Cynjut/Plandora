package com.pandora;

public class CompanyTO extends TransferObject {

	private static final long serialVersionUID = 1L;
	
    private String name;
    
    private String fullName;
    
    private String companyNumber;
    
    private String address;
    
    private String city;
    
    private String stateProvince;
    
    private String country;
    
    
    public CompanyTO(String newId) {
    	super.setId(newId);
	}
    
    
    public CompanyTO() {
	}

    
	//////////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }


    //////////////////////////////////////////    
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String newValue) {
		this.fullName = newValue;
	}	
        
    //////////////////////////////////////////
	public String getCompanyNumber() {
		return companyNumber;
	}
	public void setCompanyNumber(String newValue) {
		this.companyNumber = newValue;
	}


    //////////////////////////////////////////
	public String getAddress() {
		return address;
	}
	public void setAddress(String newValue) {
		this.address = newValue;
	}


    //////////////////////////////////////////
	public String getCity() {
		return city;
	}
	public void setCity(String newValue) {
		this.city = newValue;
	}


    //////////////////////////////////////////
	public String getStateProvince() {
		return stateProvince;
	}
	public void setStateProvince(String newValue) {
		this.stateProvince = newValue;
	}

	
    //////////////////////////////////////////
	public String getCountry() {
		return country;
	}
	public void setCountry(String newValue) {
		this.country = newValue;
	}	
}
