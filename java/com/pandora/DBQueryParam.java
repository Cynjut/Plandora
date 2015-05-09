package com.pandora;

import java.util.Vector;

public class DBQueryParam extends TransferObject {

	private static final long serialVersionUID = 1L;

	private int[] types;
	
	private Vector<Object> params;

	
	public DBQueryParam(int[] t, Vector<Object> p){
		this.types = t;
		this.params = p;
	}
	
	
	///////////////////////////////////////
	public int[] getTypes() {
		return types;
	}
	public void setTypes(int[] newValue) {
		this.types = newValue;
	}

	
	
	///////////////////////////////////////
	public Vector<Object> getParams() {
		return params;
	}
	public void setParams(Vector<Object> newValue) {
		this.params = newValue;
	}
	
	
}
