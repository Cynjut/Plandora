package com.pandora;

import java.sql.Timestamp;
import java.util.Vector;

/**
 * This object it is a java bean that represents an MetaField entity.
 */
public class AdditionalFieldTO extends TransferObject {
    
	private static final long serialVersionUID = 1L;
	
    /** The planning entity (Requirement, Project or Task) */
    private PlanningTO planning;
    
    /** The MetaField related. */
    private MetaFieldTO metaField;
    
    /** The value of meta field for a specific Requirement, task or project */
    private String value;
    
    private Vector<AdditionalTableTO> tableValues;
    
    private Timestamp dateValue;
    
    private Float numericValue;
    
    /**
     * Constructor 
     */
    public AdditionalFieldTO(){
    }

    /**
     * Constructor 
     */    
    public AdditionalFieldTO(String id){
        this.setId(id);
    }

    /**
     * Return the id of Meta Field related with object
     * @return
     */
    public String getMetaFieldId(){
        String response = null;
        if (this.getMetaField()!=null) {
            response = this.getMetaField().getId();
        }
        return response;
    }

    
    /////////////////////////////////////////////
    public MetaFieldTO getMetaField() {
        return metaField;
    }
    public void setMetaField(MetaFieldTO newValue) {
        this.metaField = newValue;
    }
    
    
    /////////////////////////////////////////////    
    public PlanningTO getPlanning() {
        return planning;
    }
    public void setPlanning(PlanningTO newValue) {
        this.planning = newValue;
    }
    
    
    /////////////////////////////////////////////    
    public String getValue() {
        return value;
    }
    public void setValue(String newValue) {
        this.value = newValue;
    }
    

    /////////////////////////////////////////////    
	public Vector<AdditionalTableTO> getTableValues() {
		return tableValues;
	}
	public void setTableValues(Vector<AdditionalTableTO> newValue) {
		this.tableValues = newValue;
	}

	
    /////////////////////////////////////////////    
	public Timestamp getDateValue() {
		return dateValue;
	}
	public void setDateValue(Timestamp newValue) {
		this.dateValue = newValue;
	}

	
	/////////////////////////////////////////////    
	public Float getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(Float numericValue) {
		this.numericValue = numericValue;
	}
    
	
    
}
