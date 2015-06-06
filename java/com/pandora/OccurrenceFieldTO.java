package com.pandora;

import java.sql.Timestamp;
import java.util.Vector;

/**
 */
public class OccurrenceFieldTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    private OccurrenceTO occurrence;
    
    private String value;
    
    private String field;
    
    private Timestamp dateValue;
    
    private Vector<Vector<Object>> tableValues;

    
    ////////////////////////////////////////
    public String getField() {
        return field;
    }
    public void setField(String newValue) {
        this.field = newValue;
    }

    
    ////////////////////////////////////////    
    public OccurrenceTO getOccurrence() {
        return occurrence;
    }
    public void setOccurrence(OccurrenceTO newValue) {
        this.occurrence = newValue;
    }
    
    
    ////////////////////////////////////////    
    public String getValue() {
        return value;
    }
    public void setValue(String newValue) {
        this.value = newValue;
    }

    
    ////////////////////////////////////////  
    public Timestamp getDateValue() {
		return dateValue;
	}
	public void setDateValue(Timestamp newValue) {
		this.dateValue = newValue;
	}
	
	
    ////////////////////////////////////////  	
	public Vector<Vector<Object>> getTableValues() {
		return tableValues;
	}
	public void setTableValues(Vector<Vector<Object>> newValue) {
		this.tableValues = newValue;
	}

	public boolean isEmpty(Vector<Object> line) {
		boolean response = true;
		if (line!=null) {
			for (Object cell : line) {
				if (cell!=null && !(cell+"").trim().equals("")) {
					response = false;
				}
			}
		}
		return response;
	}
    
}
