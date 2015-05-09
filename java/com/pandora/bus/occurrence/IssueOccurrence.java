package com.pandora.bus.occurrence;

import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.TransferObject;

/**
 */
public class IssueOccurrence extends Occurrence {

    public static final String ISSUE_FORECAST    = "FORECAST";
    
    public static final String ISSUE_RESPONSIBLE = "RESPONSIBL";
    
    public static final String ISSUE_IMPACT      = "IMPACT";
    
    public static final String ISSUE_ANALYSIS    = "ANALYSIS";
    
    public static final String ISSUE_ACTION      = "ACTION";
    
    
    
    public String getUniqueName() {
        return "label.occurrence.issue";
    }
    
    public Vector getFields(){
        Vector response = new Vector();
        response.add(new FieldValueTO(ISSUE_FORECAST, "label.occurrence.issue.forecast", FieldValueTO.FIELD_TYPE_DATE, 10, 10));
        response.add(new FieldValueTO(ISSUE_RESPONSIBLE, "label.occurrence.issue.responsible", FieldValueTO.FIELD_TYPE_TEXT, 50, 40));

        Vector domain = new Vector();
        domain.add(new TransferObject("5","label.occurrence.issue.impact.high"));
        domain.add(new TransferObject("3","label.occurrence.issue.impact.medium"));
        domain.add(new TransferObject("1","label.occurrence.issue.impact.low"));
        response.add(new FieldValueTO(ISSUE_IMPACT, "label.occurrence.issue.impact", domain));

        response.add(new FieldValueTO(ISSUE_ANALYSIS, "label.occurrence.issue.analysis", FieldValueTO.FIELD_TYPE_AREA, 255, 3));
        response.add(new FieldValueTO(ISSUE_ACTION, "label.occurrence.issue.action", FieldValueTO.FIELD_TYPE_AREA, 255, 3));        
        return response;
    }
    
    
    public String getContextHelp() {
        return "label.occurrence.issue.help";
    }
    
    
    public Vector getStatusValues() {
        Vector response = new Vector();
        response.add(new TransferObject(STATE_START, "label.occurrence.issue.status.open"));
        response.add(new TransferObject("50", "label.occurrence.issue.status.wait"));
        response.add(new TransferObject(STATE_FINAL_1, "label.occurrence.issue.status.closed"));
        return response;
    }
}
