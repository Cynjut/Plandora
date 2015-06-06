package com.pandora;

import java.sql.Timestamp;
import java.util.Locale;

import com.pandora.helper.StringUtil;

public class CostInstallmentTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private CostTO cost;
	
	private Integer installmentNum;

	private Long value;
	
	private Timestamp dueDate;
	
	private CostStatusTO costStatus;
	
	private UserTO approver;

	private Timestamp statusDate;
	
	
	//transient attribute	
	private Locale showCurrencyLocale;
	

	
	@Override
	public String getId() {
		String response = "";
		
		if (cost!=null) {
			response = cost.getId();
		}

		if (installmentNum!=null) {
			response = response + "_" + installmentNum;
		}

		return response;
	}
	
	
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
	public Long getValue() {
		return value;
	}
	public void setValue(Long newValue) {
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
	
	
	
	//////////////////////////////////	
	public Locale getShowCurrencyLocale() {
		return showCurrencyLocale;
	}
	public void setShowCurrencyLocale(Locale newValue) {
		this.showCurrencyLocale = newValue;
	}
	
	
	public String getValueStr(){
		String response = "";
		if (this.showCurrencyLocale!=null) {
			float value = ((float)this.value) / 100;
			response = StringUtil.getCurrencyValue(value, showCurrencyLocale);   
		}
		return response;
	}

	
	public String getApproverName(){
		String response = "";
		if (this.approver!=null) {
			response = approver.getUsername();   
		}
		return response;
	}
	
}
