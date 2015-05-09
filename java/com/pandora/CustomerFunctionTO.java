package com.pandora;

import java.sql.Timestamp;

public class CustomerFunctionTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private CustomerTO customer;
	
	private FunctionTO funct;

    private Timestamp creationDate;

    
	
    //////////////////////////////////
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Timestamp newValue) {
		this.creationDate = newValue;
	}
	
	
    //////////////////////////////////	
	public CustomerTO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerTO newValue) {
		this.customer = newValue;
	}
	
	
    //////////////////////////////////	
	public FunctionTO getFunct() {
		return funct;
	}
	public void setFunct(FunctionTO newValue) {
		this.funct = newValue;
	}

}
