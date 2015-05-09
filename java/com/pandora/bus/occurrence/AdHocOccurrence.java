package com.pandora.bus.occurrence;

import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.TransferObject;

/**
 */
public class AdHocOccurrence extends Occurrence {
    
    public static final String ADHOC_DESCRIPTION = "DESCRIPTIO";    
    
    
    public String getUniqueName() {
        return "label.occurrence.adhoc";
    }
    
    public Vector getFields(){
        Vector response = new Vector();
        response.add(new FieldValueTO(ADHOC_DESCRIPTION, "label.occurrence.adhoc.description", FieldValueTO.FIELD_TYPE_AREA, 255, 10));        
        return response;
    }
    
    
    public String getContextHelp() {
        return "label.occurrence.adhoc.help";
    }
    
    
    public Vector getStatusValues() {
        Vector response = new Vector();
        response.add(new TransferObject(STATE_START, "label.occurrence.adhoc.status.open"));
        response.add(new TransferObject(STATE_FINAL_1, "label.occurrence.adhoc.status.closed"));
        return response;
    }    
}
