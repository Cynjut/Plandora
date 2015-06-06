package com.pandora.bus.occurrence;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.TransferObject;
import com.pandora.bus.GenericPlugin;

/**
 */
public class Occurrence extends GenericPlugin {  
    
    public static final String STATE_START = "0";
    
    public static final String STATE_FINAL_1   = "100";
    
    public static final String STATE_FINAL_2   = "99";
    
    public static final String STATE_FINAL_3   = "98";
    
    
    public Vector<FieldValueTO> getFields(){
        return null;
    }
    
    
    public Vector getStatusValues(){
        return null;
    }
    

    public String getContextHelp() {
        return "";
    }
    
    public String getStatusLabelByKey(String key){
        String reponse = null;
        Vector statusValues = getStatusValues();
        if (statusValues!=null) {
            Iterator i = statusValues.iterator();
            while(i.hasNext()) {
                TransferObject to = (TransferObject)i.next();
                if (to.getId().equals(key)) {
                    reponse = to.getGenericTag();
                    break;
                }
            }
        }
        return reponse;
    }
    
    
    public String getType(String key){
    	String response = null;
    	Vector<FieldValueTO> list = getFields();
        if (list!=null) {
            Iterator<FieldValueTO> i = list.iterator();
            while(i.hasNext()) {
            	FieldValueTO to = i.next();
                if (to.getId().equals(key)) {
                    response = to.getType();
                    break;
                }
            }
        }
        return response;
    }
    
    
    public FieldValueTO getField(String fieldId){
    	FieldValueTO response = null;
    	Vector<FieldValueTO> list = getFields();
        if (list!=null) {
            Iterator<FieldValueTO> i = list.iterator();
            while(i.hasNext()) {
            	FieldValueTO to = i.next();
                if (to.getId().equals(fieldId)) {
                    response = to;
                    break;
                }
            }
        }
        return response;
    }    
    
}
