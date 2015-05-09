package com.pandora.bus.kb;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.pandora.AdditionalFieldTO;
import com.pandora.RequirementHistoryTO;
import com.pandora.RequirementTO;
import com.pandora.bus.RequirementBUS;
import com.pandora.delegate.RequirementDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

/**
 */
public class RequirementKbIndex extends KbIndex{

    
    public String getUniqueName() {
        return "RequirementKbIndex";
    }


    public String getContextLabel() {
        return "label.viewKb.Req";
    }

    
    public Timestamp getCreationDate(Object to){
        RequirementTO rto = (RequirementTO)to;
        return rto.getCreationDate();
    }

    public String getProjectId(Object to){
        RequirementTO rto = (RequirementTO)to;
        return rto.getProject().getId();
    }
    
    public long getId(Object to) {
        long response = -1;
        RequirementTO rto = (RequirementTO)to;
        if (rto!=null) {
            Long reqId = new Long(rto.getId());
            response = reqId.longValue();
        }
        return response;
    }


    public Vector call(long initialCursor, long finalCursor) throws Exception {
        RequirementBUS rbus = new RequirementBUS();
        Vector vr = null;
        try {
            vr = rbus.getListUntilID(initialCursor+"", finalCursor+"");
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return vr;
    }


    public long getMaxId() throws Exception {
        long response = -1;
        try {        
	        RequirementBUS rbus = new RequirementBUS();
	        response = rbus.getMaxID();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return response;
    }
    
    
    public Document getObjectToIndex(Object to) throws Exception {
        Document response = new Document();
        RequirementDelegate del = new RequirementDelegate();
        RequirementTO rto = (RequirementTO)to;
        String content = "", history = ""; 
        
        Vector hist = del.getHistory(rto.getId());
        if (hist!=null) {
            Iterator i = hist.iterator();
            while(i.hasNext()){
                RequirementHistoryTO rhto = (RequirementHistoryTO)i.next();
                history = history.concat("<li>");
                if (rhto.getComment()!=null) {
                    history = history.concat(rhto.getComment());    
                }                
                history = history.concat("(<i>" + rhto.getResource().getName() + " - ");
                history = history.concat(rhto.getStatus().getName() + " - ");
                history = history.concat(DateUtil.getDateTime(rhto.getDate(), "yyyy-MM-dd hh:mm:ss") + "</i>)");
                history = history.concat("</li>");
            }
        }
        
        content = content.concat(rto.getRequester().getName() + " (<i>" + rto.getRequester().getUsername()+ "</i>)</p>");
                
        content = content.concat("<table border=\"0\"><tr><td width=\"40\"></td><td class=\"tableCell\">");
        content = content.concat(rto.getDescription());
        content = content.concat(history);
        content = content.concat("</td></tr></table></p>");
        
        String addFields = this.getAdditionalFields(rto);
        if (addFields!=null) {
            content = content + " " + addFields; 
        }
        
        response.add(new Field(KB_CONTENT, content, Field.Store.YES, Field.Index.TOKENIZED));
        response.add(new Field(KB_ID, rto.getId(), Field.Store.YES, Field.Index.TOKENIZED));
        response.add(new Field(KB_PROJECT_ID, rto.getProject().getId(), Field.Store.YES, Field.Index.UN_TOKENIZED));

        return response;
    }
    
    public Class getBusinessClass() throws Exception {
        return RequirementTO.class;
    }
    
    private String getAdditionalFields(RequirementTO rto){
        String response = null;
        Vector addFields = rto.getAdditionalFields();
        if (addFields!=null) {
            response = ""; 
            Iterator i = addFields.iterator();
            while(i.hasNext()) {
                AdditionalFieldTO af = (AdditionalFieldTO)i.next();
                response = response + "<li>" + af.getValue() + "</li>";
            }
        }
        return response;
    }
}
