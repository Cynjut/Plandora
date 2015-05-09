package com.pandora.bus.occurrence;

import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.TransferObject;

public class IterationOccurrence extends Occurrence {

    public static final String ITERATION_INI_DATE    = "INI_DATE";

    public static final String ITERATION_FINAL_DATE  = "FINAL_DATE";
    
    public static final String ITERATION_DESCRIPTION = "DESCRIPTIO";
    
    //public static final String ITERATION_RELATED_REQ = "RELAT_REQ";
	
	
    public String getUniqueName() {
        return "label.occurrence.iteration";
    }

    public Vector getFields(){
        Vector response = new Vector();
        response.add(new FieldValueTO(ITERATION_INI_DATE,    "label.occurrence.iteration.initialdate", FieldValueTO.FIELD_TYPE_DATE, 10, 10));
        response.add(new FieldValueTO(ITERATION_FINAL_DATE,  "label.occurrence.iteration.finaldate", FieldValueTO.FIELD_TYPE_DATE, 10, 10));
        response.add(new FieldValueTO(ITERATION_DESCRIPTION, "label.occurrence.iteration.description", FieldValueTO.FIELD_TYPE_AREA, 255, 10));
        //response.add(new FieldValueTO(ITERATION_RELATED_REQ, "label.occurrence.iteration.description", new Vector(), 4, true));        
        return response;
    }
    
    
    public String getContextHelp() {
        return "label.occurrence.iteration.help";
    }
    
    
    public Vector getStatusValues() {
        Vector response = new Vector();
        response.add(new TransferObject(STATE_START, "label.occurrence.iteration.status.planned"));
        response.add(new TransferObject("50", "label.occurrence.iteration.status.started"));
        response.add(new TransferObject(STATE_FINAL_1, "label.occurrence.iteration.status.Aborted"));
        response.add(new TransferObject(STATE_FINAL_2, "label.occurrence.iteration.status.successClosed"));
        response.add(new TransferObject(STATE_FINAL_3, "label.occurrence.iteration.status.pendClosed"));        
        return response;
    }    
    
}
