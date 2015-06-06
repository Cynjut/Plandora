package com.pandora;

public class InvoiceItemTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private InvoiceTO invoice;
	 	 
	private String itemName;
	
	private Integer type;
	 
	private Long price;
	 
	private Integer amount;

	private Integer typeIndex;
	 
	
	public long calculatePrice(){
		long total = 0;
		if (price!=null && typeIndex!=null) {
			total = price.longValue() * typeIndex.intValue();
		}
		return total;
	}
	
	
	/////////////////////////////////
	public InvoiceTO getInvoice() {
		return invoice;
	}
	public void setInvoice(InvoiceTO newValue) {
		this.invoice = newValue;
	}
	
	
	/////////////////////////////////	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String newValue) {
		this.itemName = newValue;
	}


	/////////////////////////////////	
	public Integer getType() {
		return type;
	}
	public void setType(Integer newValue) {
		this.type = newValue;
	}
	
	
	/////////////////////////////////	
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long newValue) {
		this.price = newValue;
	}
	
	
	/////////////////////////////////
	public Integer getAmount() {
		return amount;
	}	
	public void setAmount(Integer newValue) {
		this.amount = newValue;
	}

	
	/////////////////////////////////
	public Integer getTypeIndex() {
		return typeIndex;
	}	
	public void setTypeIndex(Integer newValue) {
		this.typeIndex = newValue;
	}
}
