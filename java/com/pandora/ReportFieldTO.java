package com.pandora;

import java.sql.Types;
import java.util.Locale;
import java.util.Vector;

import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;

/**
 * 
 */
public class ReportFieldTO extends TransferObject {
    
	private static final long serialVersionUID = 1L;

    /** Type of report field */
    public static final Integer TYPE_STRING       = new Integer(1);
    
    /** Type of report field */
    public static final Integer TYPE_TIMESTAMP    = new Integer(2);
    
    /** Type of report field */
    public static final Integer TYPE_INTEGER      = new Integer(3);
    
    /** Type of report field */
    public static final Integer TYPE_FLOAT        = new Integer(4);

    /** Type of report field */
    public static final Integer TYPE_OBJECT       = new Integer(5);

    /** Type of report field */
    public static final Integer TYPE_BOOLEAN      = new Integer(6);
    
    
    /** The type of a inner report field of a customized SQL */
    private Integer reportFieldType;
    
    /** The label of the report field */
    private String label;

    /** The value of report field set by user at report viewer */
    private String value;

    /** The project of the report related to current report field */
    private ProjectTO project;
    
    /** If true, the report field should be displayed on GUI */
    private boolean isVisible = true;
    
    
    public void setReportFieldType(String content) {
        try {
            if (content!=null) {
                Integer type = new Integer(content);
                if (type.equals(TYPE_STRING) || type.equals(TYPE_TIMESTAMP) || type.equals(TYPE_INTEGER) ||
                        type.equals(TYPE_FLOAT) || type.equals(TYPE_OBJECT) || type.equals(TYPE_BOOLEAN)  ) {
                    this.reportFieldType = type;  
                    
                } else {
                    this.reportFieldType = null;
                }
            }
        } catch(Exception e) {
            this.reportFieldType = null;   
        }
    }
    
    
	/**
	 * Format a field name to be used such a html form id 
	 * (replace spaces to underline, create a standard name, etc)
	 */
	public String getFieldToHtml(ReportTO rto){
	    String response = "";
	    response = this.getId().replaceAll(" ", "_");
	    response = response + "_" + rto.getId();
	    return response;
	}    
    
	/**
	 * This method creates a list of values and types to be used by preparedStatement
	 */
	public boolean setValueIntoPreparedStatement(String mask, Locale loc, Vector<Integer> types, Vector<Object> values) throws BusinessException{
	    boolean response = true;
	    
	    Integer type = this.getReportFieldType();
	    if (type!=null) {
	        if (loc!=null) {
		        if (type.equals(TYPE_STRING)) {
                	types.addElement(new Integer(Types.VARCHAR));
                	values.addElement(this.value);
		            
		        } else if (type.equals(TYPE_TIMESTAMP)) {
                	types.addElement(new Integer(Types.TIMESTAMP));
                	values.addElement(DateUtil.getDateTime(this.value, mask, loc));
		        	
		        } else if (type.equals(TYPE_INTEGER)) {
                	types.addElement(new Integer(Types.INTEGER));
                	values.addElement(new Integer(this.value));		            
		            
		        } else if (type.equals(TYPE_FLOAT)) {
                	types.addElement(new Integer(Types.FLOAT));
                	values.addElement(new Float(StringUtil.getStringToFloat(this.value, loc)));		            

		        } else if (type.equals(TYPE_BOOLEAN)) {
		        	String val = this.value.trim();
                	types.addElement(new Integer(Types.BOOLEAN));
                	values.addElement(new Boolean((val.equals("1") || val.equalsIgnoreCase("true"))));		            
		            
		        } else {
		            response = false;
		        }	            
	        } else {
	            response = false;	            
	            throw new BusinessException("The locale used to set the value of report field into preparedStatement cannot be null.");
	        }
	    } else {
            response = false;	        
            throw new BusinessException("The type of report field cannot be null.");	        
	    }
	    
	    return response;
	}

	
    /////////////////////////////////////////////////
    public Integer getReportFieldType() {
        return reportFieldType;
    }
    
    /////////////////////////////////////////////////    
    public String getLabel() {
        return label;
    }
    public void setLabel(String newValue) {
        this.label = newValue;
    }
    
    /////////////////////////////////////////////////     
    public String getValue() {
        return value;
    }
    public void setValue(String newValue) {
        this.value = newValue;
    }
    
    /////////////////////////////////////////////////        
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }
    
    /////////////////////////////////////////////////      
    public boolean isVisible() {
        return isVisible;
    }
    public void setVisible(boolean newValue) {
        this.isVisible = newValue;
    }
}
