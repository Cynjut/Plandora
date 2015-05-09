package com.pandora.bus.occurrence;

import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.TransferObject;

public class StrategicObjectivesOccurrence extends Occurrence {

    public static final String SO_DESCRIPTION = "DESCRIPTIO";    
    
    public static final String SO_PERSPECTIVE = "PERSPECT";
    

    public String getUniqueName() {
        return "label.occurrence.so";
    }
    
    public Vector getFields(){
        Vector response = new Vector();
        response.add(new FieldValueTO(SO_DESCRIPTION, "label.occurrence.so.description", FieldValueTO.FIELD_TYPE_AREA, 255, 8));
        
        Vector vp = new Vector();
        vp.add(new TransferObject("1", "label.manageReport.persp.1"));
        vp.add(new TransferObject("2", "label.manageReport.persp.2"));
        vp.add(new TransferObject("3", "label.manageReport.persp.3"));
        vp.add(new TransferObject("4", "label.manageReport.persp.4"));
        response.add(new FieldValueTO(SO_PERSPECTIVE, "label.manageReport.persp", vp));
        
        return response;
    }
    
    
    public String getContextHelp() {
        return "label.occurrence.so.help";
    }
    
    
    public Vector getStatusValues() {
        Vector response = new Vector();
        
        response.add(new TransferObject(STATE_START, "label.occurrence.so.status.proposed"));
        response.add(new TransferObject(STATE_FINAL_1, "label.occurrence.so.status.active"));
        response.add(new TransferObject(STATE_FINAL_2, "label.occurrence.so.status.disabled"));
        return response;
    }    

}
