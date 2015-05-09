package com.pandora.gui.struts.form;


public class InvoiceItemForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String invoiceId;
	
	private String itemName;
	
	private String type;
		
	private String price;
	
	private String amount;

	private String editInvoiceItemId;
	
	private String currencySymbol;
	

	/**
     * Clear values of Form
     */
    public void clear(){
    	this.type = "1";
    	this.price = null;
    	this.amount = null;
    	this.itemName = null;
    }

	
	//////////////////////////////////
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String newValue) {
		this.invoiceId = newValue;
	}

	
	//////////////////////////////////	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String newValue) {
		this.itemName = newValue;
	}


	//////////////////////////////////
	public String getType() {
		return type;
	}
	public void setType(String newValue) {
		this.type = newValue;
	}

	
	//////////////////////////////////
	public String getPrice() {
		return price;
	}
	public void setPrice(String newValue) {
		this.price = newValue;
	}

	
	//////////////////////////////////
	public String getAmount() {
		return amount;
	}
	public void setAmount(String newValue) {
		this.amount = newValue;
	}

	
	//////////////////////////////////
	public String getEditInvoiceItemId() {
		return editInvoiceItemId;
	}
	public void setEditInvoiceItemId(String newValue) {
		this.editInvoiceItemId = newValue;
	}

	
	//////////////////////////////////
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String newValue) {
		this.currencySymbol = newValue;
	}
	
	
}
