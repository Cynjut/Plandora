package com.pandora;

public class GadgetTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private String name;

	public GadgetTO(String id, String label, String value) {
		this.setId(id);
		this.setGenericTag(value);
		this.setName(label);
	}
	
	
	/////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
}
