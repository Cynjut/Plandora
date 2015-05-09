package com.pandora;

import java.sql.Timestamp;

public class CostInstallmentTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private CostTO cost;
	
	private Integer installmentNum;

	private Integer value;
	
	private Timestamp dueDate;
	
	private CostStatusTO costStatus;
	
	private UserTO approver;

	private Timestamp statusDate;
	
	

	//////////////////////////////////
	public CostTO getCost() {
		return cost;
	}
	public void setCost(CostTO newValue) {
		this.cost = newValue;
	}
	
	
	//////////////////////////////////	
	public Integer getInstallmentNum() {
		return installmentNum;
	}
	public void setInstallmentNum(Integer newValue) {
		this.installmentNum = newValue;
	}
	
	
	//////////////////////////////////	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer newValue) {
		this.value = newValue;
	}
	
	
	//////////////////////////////////
	public Timestamp getDueDate() {
		return dueDate;
	}
	public void setDueDate(Timestamp newValue) {
		this.dueDate = newValue;
	}

	
	//////////////////////////////////
	public CostStatusTO getCostStatus() {
		return costStatus;
	}
	public void setCostStatus(CostStatusTO newValue) {
		this.costStatus = newValue;
	}
	
	
	//////////////////////////////////
	public UserTO getApprover() {
		return approver;
	}
	public void setApprover(UserTO newValue) {
		this.approver = newValue;
	}
	
	
	//////////////////////////////////	
	public Timestamp getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(Timestamp newValue) {
		this.statusDate = newValue;
	}
	
}
