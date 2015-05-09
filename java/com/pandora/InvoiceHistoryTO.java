package com.pandora;

import java.sql.Timestamp;

import com.pandora.helper.DateUtil;

public class InvoiceHistoryTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    private Timestamp creationDate;
    
	private InvoiceTO invoice;
		
	private String name;
	
	private CategoryTO category;
	
	private InvoiceStatusTO invoiceStatus;
	
	private Timestamp dueDate;
	
	private String invoiceNumber;
	
	private Timestamp invoiceDate;
	
	private String purchaseOrder;

	private String contact;
	
	private String description;
	
	private Integer totalPrice;
	
    private UserTO handler;

    
    public InvoiceHistoryTO(){
    }
    
    public InvoiceHistoryTO(InvoiceTO ito){
    	this.invoice = ito;
    	this.category = ito.getCategory();
    	this.creationDate = DateUtil.getNow();
    	this.name = ito.getName();
    	this.invoiceStatus = ito.getInvoiceStatus();
    	this.dueDate = ito.getDueDate();
    	this.invoiceNumber = ito.getInvoiceNumber();
    	this.invoiceDate = ito.getInvoiceDate();
    	this.purchaseOrder = ito.getPurchaseOrder();
    	this.contact = ito.getContact();
    	this.description = ito.getDescription();
    	this.handler = ito.getHandler();
    	this.totalPrice = ito.getTotal();
    }
    
    
    
    //////////////////////////////////////////
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Timestamp newValue) {
		this.creationDate = newValue;
	}

	
    //////////////////////////////////////////
	public InvoiceTO getInvoice() {
		return invoice;
	}
	public void setInvoice(InvoiceTO newValue) {
		this.invoice = newValue;
	}

	
    //////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
    //////////////////////////////////////////
	public CategoryTO getCategory() {
		return category;
	}
	public void setCategory(CategoryTO newValue) {
		this.category = newValue;
	}

	
    //////////////////////////////////////////
	public InvoiceStatusTO getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(InvoiceStatusTO newValue) {
		this.invoiceStatus = newValue;
	}

	
    //////////////////////////////////////////
	public Timestamp getDueDate() {
		return dueDate;
	}
	public void setDueDate(Timestamp newValue) {
		this.dueDate = newValue;
	}

	
    //////////////////////////////////////////
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String newValue) {
		this.invoiceNumber = newValue;
	}

	
    //////////////////////////////////////////
	public Timestamp getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(Timestamp newValue) {
		this.invoiceDate = newValue;
	}

	
    //////////////////////////////////////////
	public String getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(String newValue) {
		this.purchaseOrder = newValue;
	}

	
    //////////////////////////////////////////
	public String getContact() {
		return contact;
	}
	public void setContact(String newValue) {
		this.contact = newValue;
	}

	
    //////////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}

	
    //////////////////////////////////////////
	public Integer getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Integer newValue) {
		this.totalPrice = newValue;
	}

	
    //////////////////////////////////////////
	public UserTO getHandler() {
		return handler;
	}
	public void setHandler(UserTO newValue) {
		this.handler = newValue;
	}
    
}
