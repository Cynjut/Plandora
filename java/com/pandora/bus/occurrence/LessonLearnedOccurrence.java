package com.pandora.bus.occurrence;

import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.TransferObject;

/**
 */
public class LessonLearnedOccurrence extends Occurrence {

    public static final String LL_DESCRIPTION = "DESCRIPTIO";
    
    public static final String LL_CATEGORY    = "CATEGORY";
    
    
    public String getUniqueName() {
        return "label.occurrence.lesson";
    }
    
    public Vector getFields(){
        Vector response = new Vector();
        
        Vector catList = new Vector();
        catList.add(new TransferObject("1", "label.occurrence.lesson.cat.doc"));
        catList.add(new TransferObject("2", "label.occurrence.lesson.cat.proc"));
        catList.add(new TransferObject("3", "label.occurrence.lesson.cat.customer"));
        catList.add(new TransferObject("4", "label.occurrence.lesson.cat.supplier"));
        catList.add(new TransferObject("5", "label.occurrence.lesson.cat.operat"));
        catList.add(new TransferObject("6", "label.occurrence.lesson.cat.other"));
        
        response.add(new FieldValueTO(LL_DESCRIPTION, "label.occurrence.lesson.description", FieldValueTO.FIELD_TYPE_AREA, 255, 8));
        response.add(new FieldValueTO(LL_CATEGORY, "label.occurrence.lesson.category", catList));
        
        return response;
    }
    
    
    public String getContextHelp() {
        return "label.occurrence.lesson.help";
    }
    
    
    public Vector getStatusValues() {
        Vector response = new Vector();
        response.add(new TransferObject(STATE_START,   "label.occurrence.event.status.proposed"));
        response.add(new TransferObject(STATE_FINAL_1, "label.occurrence.event.status.accepted"));
        response.add(new TransferObject(STATE_FINAL_2, "label.occurrence.event.status.rejected"));
        return response;
    }    
    
}
