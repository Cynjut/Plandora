package com.pandora;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class InvoiceTO extends PlanningTO {

	private static final long serialVersionUID = 1L;

	
	private ProjectTO project;
	
	private String name;
	
	private CategoryTO category;
	
	private InvoiceStatusTO invoiceStatus;
	
	private String invoiceNumber;
	
	private Timestamp invoiceDate;
	
	private Timestamp dueDate;

	private String purchaseOrder;

	private String contact;
	
	private Vector<InvoiceItemTO> itemsList;
	
	private ArrayList itemsToBeUpdated = new ArrayList();
	
	private ArrayList itemsToBeRemoved = new ArrayList();

	private UserTO handler;
	
	
	public InvoiceTO(){
	}

	public InvoiceTO(String newId){
		this.setId(newId);
	}


	public Integer getTotal() {
    	int total = 0;
    	try {
        	if (itemsList!=null) {
            	Iterator<InvoiceItemTO> i = itemsList.iterator();
            	while(i.hasNext()) {
            		InvoiceItemTO item = i.next();
            		total += (item.calculatePrice() * item.getAmount().intValue());
            	}     		
        	}    		
    	} catch(Exception e) {
    		e.printStackTrace();
    		total = 0;
    	}
		return new Integer(total);
	}	
	
	///////////////////////////////////////
	public ProjectTO getProject() {
		return project;
	}
	public void setProject(ProjectTO newValue) {
		this.project = newValue;
	}

	
	///////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	///////////////////////////////////////
	public void setCategory(CategoryTO newValue) {
		this.category = newValue;
	}
	public CategoryTO getCategory() {
		return category;
	}

	
	///////////////////////////////////////
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String newValue) {
		this.invoiceNumber = newValue;
	}
	
		
	
	///////////////////////////////////////
	public String getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(String newValue) {
		this.purchaseOrder = newValue;
	}
	
	
	///////////////////////////////////////
	public String getContact() {
		return contact;
	}
	public void setContact(String newValue) {
		this.contact = newValue;
	}

	
	///////////////////////////////////////	
	public InvoiceStatusTO getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(InvoiceStatusTO newValue) {
		this.invoiceStatus = newValue;
	}
	
	
	///////////////////////////////////////		
	public Timestamp getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(Timestamp newValue) {
		this.invoiceDate = newValue;
	}
	
	
	///////////////////////////////////////	
	public Timestamp getDueDate() {
		return dueDate;
	}
	public void setDueDate(Timestamp newValue) {
		this.dueDate = newValue;
	}

	
	///////////////////////////////////
	public Vector<InvoiceItemTO> getItemsList() {
		return itemsList;
	}
	public void setItemsList(Vector<InvoiceItemTO> newValue) {
		this.itemsList = newValue;
	}
	
	
	///////////////////////////////////////	
	public ArrayList getItemsToBeUpdated() {
		return itemsToBeUpdated;
	}
	public void setItemsToBeUpdated(ArrayList newValue) {
		this.itemsToBeUpdated = newValue;
	}
	
	
	///////////////////////////////////////	
	public ArrayList getItemsToBeRemoved() {
		return itemsToBeRemoved;
	}
	public void setItemsToBeRemoved(ArrayList newValue) {
		this.itemsToBeRemoved = newValue;
	}

	
    //////////////////////////////////////////
	public UserTO getHandler() {
		return handler;
	}
	public void setHandler(UserTO newValue) {
		this.handler = newValue;
	}

}
