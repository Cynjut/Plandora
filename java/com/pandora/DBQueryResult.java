package com.pandora;

import java.util.Vector;

public class DBQueryResult extends TransferObject {

	private static final long serialVersionUID = 1L;

	private Vector<Object> columnNames = new Vector<Object>();
	
	private Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	
	public void addColumn(String newcol){
		columnNames.add(newcol);
	}

	public int size() {
		return data.size();
	}

	public void add(Vector<Object> line) {
		data.addElement(line);
	}

	public boolean isEmpty() {
		return (data==null || data.isEmpty());
	}

	public Vector<Object> getData(int i) {
		if (data!=null) {
			return data.get(i);
		} else {
			return null;	
		}
	}

	public Vector<Vector<Object>> getData() {
		return data;
	}

	public Vector<Object> getColumns() {
		return columnNames;
	}
}
