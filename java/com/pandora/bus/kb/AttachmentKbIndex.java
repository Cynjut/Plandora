package com.pandora.bus.kb;

import java.sql.Timestamp;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.pandora.AttachmentTO;
import com.pandora.ProjectTO;
import com.pandora.bus.AttachmentBUS;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;

public class AttachmentKbIndex extends KbIndex {

    @Override
    public String getUniqueName() {
        return "AttachmentKbIndex";
    }

    
    @Override
    public String getContextLabel() {
        return "label.viewKb.Attachment";
    }

    
    @Override
    public Timestamp getCreationDate(Object to){
        AttachmentTO ato = (AttachmentTO)to;
        return ato.getCreationDate();
    }
    
    
    @Override
    public String getProjectId(Object to){
    	String response = null;
    	AttachmentBUS bus = new AttachmentBUS();
    	AttachmentTO ato = (AttachmentTO)to;
    	if (ato.getPlanning()!=null) {
    		ProjectTO pto = null;
			try {
				pto = bus.getAttachmentProject(ato.getPlanning().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
    		if (pto!=null) {
    			response = pto.getId();	
    		}
    	}
    	
        return response;
    }
    
    
    public long getId(Object to) {
        long response = -1;
        AttachmentTO ato = (AttachmentTO)to;
        if (ato!=null) {
            Long reqId = new Long(ato.getId());
            response = reqId.longValue();
        }
        return response;
    }

    
    @Override
	public Vector<?> call(long initialCursor, long finalCursor) throws Exception {
    	AttachmentBUS bus = new AttachmentBUS();
        Vector<AttachmentTO> vr = null;
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
            AttachmentBUS bus = new AttachmentBUS();
	        response = bus.getMaxID();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return response;
    }
    
    
    @Override
    public Document getObjectToIndex(Object to) throws Exception {
        Document response = new Document();
        AttachmentTO ato = (AttachmentTO)to;
        String content = ""; 
    
        content = content.concat(ato.getName() + " ");
        if (ato.getComment()!=null && !ato.getComment().trim().equals("")) {
            content = content.concat(ato.getComment() + " ");        	
        }
        content = content.concat(DateUtil.getDateTime(ato.getCreationDate(), "yyyy-MM-dd hh:mm:ss") + " ");
        
        //TODO append file content (extracted from PDF, DOC, TXT, and PPT files...)        
        //content = content.concat(history);
        
        response.add(new Field(KB_CONTENT, content, Field.Store.YES, Field.Index.TOKENIZED));
        response.add(new Field(KB_ID, ato.getId(), Field.Store.YES, Field.Index.TOKENIZED));
        response.add(new Field(KB_PROJECT_ID, this.getProjectId(ato), Field.Store.YES, Field.Index.UN_TOKENIZED));
        
        return response;
    }
    
    
    @SuppressWarnings("rawtypes")
	public Class getBusinessClass() throws Exception {
        return AttachmentTO.class;
    }
}
