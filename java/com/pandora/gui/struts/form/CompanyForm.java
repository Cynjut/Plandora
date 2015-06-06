package com.pandora.gui.struts.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

public class CompanyForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

    private String name;
    
    private String fullName;
    
    private String companyNumber;
    
    private String address;
    
    private String city;
    
    private String stateProvince;
    
    private String country;
    
	
	public void clear() {
		super.id = null;
        this.name = null;
        this.fullName = null;
        this.companyNumber = null;
        this.address = null;
        this.city = null;
        this.stateProvince = null;
        this.country = null;
	}

	
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (this.operation.equals("saveCompany")) {
		    if (this.name==null || this.name.trim().equals("")){
		    	errors.add("Name", new ActionError("validate.company.blankName") );
		    }
		}
		
		return errors;
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
