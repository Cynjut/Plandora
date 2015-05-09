package com.pandora.gui.taglib.form;

/**
 * This class represents a specific field of the generic form
 */
public class FormField {
    
    public static final Integer TYPE_STRING = new Integer(12);

    /** The sequence of field from meta data resultSet */
    private int sequence;
    
    /** The label of form field */
    private String label;
    
    /** The type of field */
    private Integer type;

    /** The maxlength of field */
    private int maxlength;
    
    /** The content of form field*/
    private String content;
    
    
    /////////////////////////////////////////
    public String getContent() {
        return content;
    }
    public void setContent(String newValue) {
        this.content = newValue;
    }
    
    /////////////////////////////////////////    
    public String getLabel() {
        return label;
    }
    public void setLabel(String newValue) {
        this.label = newValue;
    }
    
    /////////////////////////////////////////    
    public Integer getType() {
        return type;
    }
    public void setType(Integer newValue) {
        this.type = newValue;
    }
    
    /////////////////////////////////////////        
    public int getMaxlength() {
        return maxlength;
    }
    public void setMaxlength(int newValue) {
        this.maxlength = newValue;
    }
    
    /////////////////////////////////////////          
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int newValue) {
        this.sequence = newValue;
    }
}
