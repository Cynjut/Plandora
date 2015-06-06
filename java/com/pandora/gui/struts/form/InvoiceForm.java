package com.pandora.gui.struts.form;

import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

public class InvoiceForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;
	
	private String projectId;
	
	private String projectName;
	
	private String name;
	
	private String categoryId;

	private String invoiceStatusId;

	private String invoiceDate;
	
	private String invoiceNumber;
			
	private String description;
	
	private String dueDate;
	
	private String purchaseOrder;
	
	private String contact;
	
	
	private ArrayList<String> invoiceItemsToBeUpdated;
	
	private ArrayList<String> invoiceItemsToBeRemoved;
	
	
	private String unitPrice;
	
	private String quantity;
	
	private String removedInvoiceItemId;
	
	private String editInvoiceItemId;	
	
	private String showEditInvoiceItem;	
	
	private String totalInvoice;
	
	
	
		
	/////////////////////////////
	public ArrayList<String> getInvoiceItemsToBeRemoved() {
		return invoiceItemsToBeRemoved;
	}
	public void addInvoiceItemsToBeRemoved(String item) {
		boolean already = false;
		if (this.invoiceItemsToBeRemoved==null) {
			this.invoiceItemsToBeRemoved = new ArrayList<String>();
		} else {
			Object[] list = this.invoiceItemsToBeRemoved.toArray();
			for (int i=0 ; i<list.length; i++) {
				if (list[i].equals(item)) {
					already = true;
					break;
				}
			}			
		}
		if (!already) {
			this.invoiceItemsToBeRemoved.add(item);	
		}
	}	
	public void clearInvoiceItemsToBeRemoved(){
	    this.invoiceItemsToBeRemoved = null;
	}
	
	
	/////////////////////////////	
	public ArrayList<String> getInvoiceItemsToBeUpdated() {
		return invoiceItemsToBeUpdated;
	}
	public void addInvoiceItemsToBeUpdated(String item) {
		boolean already = false;
		if (this.invoiceItemsToBeUpdated==null) {
			this.invoiceItemsToBeUpdated = new ArrayList<String>();
		} else {
			Object[] list = this.invoiceItemsToBeUpdated.toArray();
			for (int i=0 ; i<list.length; i++) {
				if (list[i].equals(item)) {
					already = true;
					break;
				}
			}			
		}
		if (!already) {
			this.invoiceItemsToBeUpdated.add(item);	
		}		
	}
	public void clearInvoiceItemsToBeUpdated(){
	    this.invoiceItemsToBeUpdated = null;
	}
	
	///////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	///////////////////////////////////////
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String newValue) {
		this.projectName = newValue;
	}

	
	///////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	///////////////////////////////////////
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String newValue) {
		this.categoryId = newValue;
	}

	
	///////////////////////////////////////	
	public String getInvoiceStatusId() {
		return invoiceStatusId;
	}
	public void setInvoiceStatusId(String newValue) {
		this.invoiceStatusId = newValue;
	}
	
	
	///////////////////////////////////////
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String newValue) {
		this.invoiceDate = newValue;
	}
	
	
	///////////////////////////////////////
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String newValue) {
		this.invoiceNumber = newValue;
	}
	
	
	///////////////////////////////////////
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String newValue) {
		this.unitPrice = newValue;
	}
	
	
	///////////////////////////////////////
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String newValue) {
		this.quantity = newValue;
	}
	
	
	///////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}
	
	
	///////////////////////////////////////
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String newValue) {
		this.dueDate = newValue;
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
    public String getEditInvoiceItemId() {
        return editInvoiceItemId;
    }
    public void setEditInvoiceItemId(String newValue) {
        this.editInvoiceItemId = newValue;
    }
    
    
	///////////////////////////////////////
    public String getRemovedInvoiceItemId() {
        return removedInvoiceItemId;
    }
    public void setRemovedInvoiceItemId(String newValue) {
        this.removedInvoiceItemId = newValue;
    }
    
    
	///////////////////////////////////////
    public String getShowEditInvoiceItem() {
        return showEditInvoiceItem;
    }
    public void setShowEditInvoiceItem(String newValue) {
        this.showEditInvoiceItem = newValue;
    }
    
    
	///////////////////////////////////////    
    public String getTotalInvoice() {
		return totalInvoice;
	}
	public void setTotalInvoice(String newValue) {
		this.totalInvoice = newValue;
	}
	
	
	
	/**
     * Clear values of Form
     */
    public void clear(){
    	this.id = null;
    	this.name = null;
    	this.categoryId = null;
    	this.invoiceStatusId = null;
    	this.invoiceDate = null;
    	this.invoiceNumber = null;
    	this.description = null;
    	this.dueDate = null;
    	this.purchaseOrder = null;
    	this.contact = null;
    	this.invoiceItemsToBeUpdated = null;
    	this.invoiceItemsToBeRemoved = null;
    	this.unitPrice = null;
    	this.quantity = null;
    	this.removedInvoiceItemId = "";
    	this.editInvoiceItemId = "";	
    	this.showEditInvoiceItem = "off";	
    	this.totalInvoice = "";
    }
    
	/**
	 * Validate the form.
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		
		if (operation!=null && operation.equals("saveInvoice")){

			if (this.name==null || this.name.trim().equals("")){
		        errors.add("Name", new ActionError("validate.invoiceForm.blankName") );
		    }

			@SuppressWarnings({ "unchecked", "rawtypes" })
			Vector<String> iList = (Vector)request.getSession().getAttribute("invoiceItemList");
			if (iList==null || iList.size()==0){
		        errors.add("Name", new ActionError("validate.invoiceForm.blankList") );
		    }

		}
	    
		return errors;
	}
    
}
