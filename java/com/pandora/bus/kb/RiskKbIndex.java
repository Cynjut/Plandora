package com.pandora.bus.kb;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.pandora.RiskHistoryTO;
import com.pandora.RiskTO;
import com.pandora.bus.RiskBUS;
import com.pandora.delegate.RiskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class RiskKbIndex extends KbIndex {

    @Override
    public String getUniqueName() {
        return "RiskKbIndex";
    }

    @Override
    public String getContextLabel() {
        return "label.viewKb.Risk";
    }

    @Override
    public Timestamp getCreationDate(Object to){
        RiskTO rto = (RiskTO)to;
        return rto.getCreationDate();
    }
    
    @Override
    public String getProjectId(Object to){
        RiskTO rto = (RiskTO)to;
        return rto.getProject().getId();
    }
    
    public long getId(Object to) {
        long response = -1;
        RiskTO rto = (RiskTO)to;
        if (rto!=null) {
            Long reqId = new Long(rto.getId());
            response = reqId.longValue();
        }
        return response;
    }

    @Override
	public Vector<?> call(long initialCursor, long finalCursor) throws Exception {
        RiskBUS bus = new RiskBUS();
        Vector<RiskTO> vr = null;
        try {
            vr = bus.getListUntilID(initialCursor+"", finalCursor+"");
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return vr;

    }        
 
    @Override
    public long getMaxId() throws Exception {
        long response = -1;
        try {        
            RiskBUS bus = new RiskBUS();
	        response = bus.getMaxID();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return response;
    }
    
    
    @Override
    public Document getObjectToIndex(Object to) throws Exception {
        Document response = new Document();
        RiskDelegate del = new RiskDelegate();
        RiskTO rto = (RiskTO)to;
        String content = "", history = ""; 
    
        Vector<RiskHistoryTO> hist = del.getHistory(rto.getId());
        if (hist!=null) {
        	history = history.concat("</p>");
            Iterator<RiskHistoryTO> i = hist.iterator();
            while(i.hasNext()){
                RiskHistoryTO rhto = i.next();
                history = history.concat("<li>");
                if (rhto.getContent()!=null) {
                    history = history.concat(rhto.getContent());    
                }                
                history = history.concat("(<i>" + rhto.getUser().getName() + " - ");
                history = history.concat(rhto.getRiskStatusLabel() + " - ");
                history = history.concat(DateUtil.getDateTime(rhto.getCreationDate(), "yyyy-MM-dd hh:mm:ss") + "</i>)");
                history = history.concat("</li>");
            }
        }
        
        content = content.concat("<b>" + rto.getName() + "</b><br>");
        content = content.concat("<i>" + rto.getStatus().getName() + " - " + 
                DateUtil.getDateTime(rto.getCreationDate(), "yyyy-MM-dd hh:mm:ss") + "</i></p>");
        
        content = content.concat("<table border=\"0\"><tr><td width=\"40\"></td><td class=\"tableCell\">");
        content = content.concat(rto.getDescription());
        content = content.concat(history);
        content = content.concat("</td></tr></table></p>");
        
        response.add(new Field(KB_CONTENT, content, Field.Store.YES, Field.Index.TOKENIZED));
        response.add(new Field(KB_ID, rto.getId(), Field.Store.YES, Field.Index.TOKENIZED));
        response.add(new Field(KB_PROJECT_ID, rto.getProject().getId(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        
        return response;
    }
    
    
    @SuppressWarnings("rawtypes")
	public Class getBusinessClass() throws Exception {
        return RiskTO.class;
    }
    
}
