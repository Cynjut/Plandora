package com.pandora;

import java.sql.Timestamp;

/**
 */
public class OccurrenceFieldTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    private OccurrenceTO occurrence;
    
    private String value;
    
    private String field;
    
    private Timestamp dateValue;

    
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

    
    
}
