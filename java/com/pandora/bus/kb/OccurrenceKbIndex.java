package com.pandora.bus.kb;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.pandora.OccurrenceFieldTO;
import com.pandora.OccurrenceHistoryTO;
import com.pandora.OccurrenceTO;
import com.pandora.bus.occurrence.OccurrenceBUS;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

/**
 */
public class OccurrenceKbIndex extends KbIndex {
    
    public String getUniqueName() {
        return "OccurrenceKbIndex";
    }

    public String getContextLabel() {
        return "label.viewKb.Ocur";
    }
        
    
    public Timestamp getCreationDate(Object to){
        OccurrenceTO oto = (OccurrenceTO)to;
        return oto.getCreationDate();
    }

    public String getProjectId(Object to){
        OccurrenceTO oto = (OccurrenceTO)to;
        return oto.getProject().getId();
    }
    
    public long getId(Object to) {
        long response = -1;
        OccurrenceTO oto = (OccurrenceTO)to;
        if (oto!=null) {
            Long reqId = new Long(oto.getId());
            response = reqId.longValue();
        }
        return response;
        
    }
        
    public Vector call(long initialCursor, long finalCursor) throws Exception {
        OccurrenceBUS obus = new OccurrenceBUS();
        Vector vr = null;
        try {
            vr = obus.getListUntilID(initialCursor+"", finalCursor+"");
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return vr;

    }        
    
    public long getMaxId() throws Exception {
        long response = -1;
        try {        
	        OccurrenceBUS obus = new OccurrenceBUS();
	        response = obus.getMaxID();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return response;
    }
    
    
    public Document getObjectToIndex(Object to) throws Exception {
        Document response = new Document();
        OccurrenceDelegate del = new OccurrenceDelegate();
        OccurrenceTO oto = (OccurrenceTO)to;
        String content = "", fieldStr = "", history = ""; 
    
        if (oto.isVisible()) {
            Vector hist = del.getHistory(oto.getId());
            if (hist!=null) {
            	history = history.concat("</p>");
                Iterator i = hist.iterator();
                while(i.hasNext()){
                    OccurrenceHistoryTO ohto = (OccurrenceHistoryTO)i.next();
                    history = history.concat("<li>");
                    if (ohto.getContent()!=null) {
                        history = history.concat(ohto.getContent());    
                    }                
                    history = history.concat("(<i>" + ohto.getUser().getName() + " - ");
                    history = history.concat(ohto.getOccurrenceStatusLabel() + " - ");
                    history = history.concat(DateUtil.getDateTime(ohto.getCreationDate(), "yyyy-MM-dd hh:mm:ss") + "</i>)");
                    history = history.concat("</li>");
                }
            }
            
            
            Vector fieldList = oto.getFields();
            if (fieldList!=null) {
                Iterator i = fieldList.iterator();
                while(i.hasNext()){
                    OccurrenceFieldTO ofto = (OccurrenceFieldTO)i.next();
                    fieldStr = fieldStr.concat("<br><i>* " + ofto.getField() + "</i> - ");
                    fieldStr = fieldStr.concat(ofto.getValue());
                }
            }

            content = content.concat("<b>" + oto.getName() + "</b><br>");
            content = content.concat("<i>" + oto.getStatusLabel() + " - " + DateUtil.getDateTime(oto.getCreationDate(), "yyyy-MM-dd hh:mm:ss") + "</i></p>");
            
            content = content.concat("<table border=\"0\"><tr><td width=\"40\"></td><td class=\"tableCell\">");
            content = content.concat(oto.getDescription());
            content = content.concat(fieldStr);
            content = content.concat(history);
            content = content.concat("</td></tr></table></p>");
            
            response.add(new Field(KB_CONTENT, content, Field.Store.YES, Field.Index.TOKENIZED));
            response.add(new Field(KB_ID, oto.getId(), Field.Store.YES, Field.Index.TOKENIZED));
            response.add(new Field(KB_PROJECT_ID, oto.getProject().getId(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        } else {
        	response = null;
        }
        
        return response;
    }
    
    
    public Class getBusinessClass() throws Exception {
        return OccurrenceTO.class;
    }
    
}
