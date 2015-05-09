package com.pandora;

import java.util.Iterator;
import java.util.Vector;

import org.apache.struts.util.MessageResources;

/**
 */
public class FieldValueTO extends TransferObject{

	private static final long serialVersionUID = 1L;

    public static final String FIELD_TYPE_TEXT           = "TEXT";
    
    public static final String FIELD_TYPE_COMBO          = "COMBO";
    
    public static final String FIELD_TYPE_DATE           = "DATE";
    
    public static final String FIELD_TYPE_AREA           = "TEXT_AREA";
    
    public static final String FIELD_TYPE_BOOL           = "BOOLEAN";
    
    public static final String FIELD_TYPE_PASS           = "PASSWORD";
    
    public static final String FIELD_TYPE_RELATION_MANY  = "RELATION_MANY";
    
    private String type;
        
    private String label;
    
    private int maxLen;
    
    private int size;
    
    private String helpMessage;
    
    private Vector<TransferObject> domain;
    
    private String currentValue;
    
    private boolean readOnly;
    
    public FieldValueTO(String id, String n, String t, int m, int s){
       super.setId(id);
       this.type = t;
       this.label = n;
       this.maxLen = m;
       this.size = s;
    }
    
    public FieldValueTO(String id, String n, Vector<TransferObject> d){
        super.setId(id);
        this.type = FIELD_TYPE_COMBO;
        this.label = n;
        this.domain = d;
    }

    public FieldValueTO(String id, String n, Vector<TransferObject> d, int s, boolean ro){
        super.setId(id);
        this.type = FIELD_TYPE_RELATION_MANY;
        this.label = n;
        this.domain = d;
        this.size = s;
        this.readOnly = ro;
    }
    
    
    public void translateDomain(UserTO uto) {
    	if (domain!=null) {
    		MessageResources mr = uto.getBundle();
    		Iterator<TransferObject> i = domain.iterator();
    		while(i.hasNext()) {
    			TransferObject to = i.next();
    			String key = to.getGenericTag();
    			try {
    				String buff = mr.getMessage(uto.getLocale(), key);
    				if (buff.startsWith("???")) {
    					to.setGenericTag(key);	
    				} else {
    					to.setGenericTag(buff);	
    				}
    			} catch(Exception e){
    				to.setGenericTag(key);
    			}
    		}
    	}
    }    
    
    /////////////////////////////////////////
    public String getHelpMessage() {
        return helpMessage;
    }
    public void setHelpMessage(String newValue) {
        this.helpMessage = newValue;
    }

    
    /////////////////////////////////////////    
    public int getMaxLen() {
        return maxLen;
    }
    public void setMaxLen(int newValue) {
        this.maxLen = newValue;
    }
    
    
    /////////////////////////////////////////    
    public String getLabel() {
        return label;
    }
    public void setLabel(String newValue) {
        this.label = newValue;
    }
    
    
    /////////////////////////////////////////    
    public String getType() {
        return type;
    }
    public void setType(String newValue) {
        this.type = newValue;
    }
    
    
    /////////////////////////////////////////
    public Vector<TransferObject> getDomain() {
        return domain;
    }
    public void setDomain(Vector<TransferObject> newValue) {
        this.domain = newValue;
    }
    
    
    /////////////////////////////////////////          
    public int getSize() {
        return size;
    }
    public void setSize(int newValue) {
        this.size = newValue;
    }

    
    /////////////////////////////////////////     
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String newValue) {
		this.currentValue = newValue;
	}

	
    /////////////////////////////////////////     	
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean newValue) {
		this.readOnly = newValue;
	}
    
}
