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
    
    
    public Vector getFields(){
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
    	Vector list = getFields();
        if (list!=null) {
            Iterator i = list.iterator();
            while(i.hasNext()) {
            	FieldValueTO to = (FieldValueTO)i.next();
                if (to.getId().equals(key)) {
                    response = to.getType();
                    break;
                }
            }
        }
        return response;
    }
    
}
