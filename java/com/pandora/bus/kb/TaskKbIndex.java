package com.pandora.bus.kb;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.pandora.AdditionalFieldTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskTO;
import com.pandora.bus.ResourceTaskBUS;
import com.pandora.bus.TaskBUS;
import com.pandora.delegate.ResourceTaskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.DateUtil;


public class TaskKbIndex extends KbIndex {

    @Override
    public String getUniqueName() {
        return "TaskKbIndex";
    }

    
    @Override
    public String getContextLabel() {
        return "label.viewKb.Task";
    }
    
    
    @Override
    public Timestamp getCreationDate(Object to){
        ResourceTaskTO rtto = (ResourceTaskTO)to;
        return rtto.getTask().getCreationDate();
    }
    
    
    @Override
    public String getProjectId(Object to){
        ResourceTaskTO rtto = (ResourceTaskTO)to;
        ProjectTO pto = rtto.getTask().getProject();
        return pto.getId();
    }    
    
    @Override
    public long getId(Object to) {
        long response = -1;
        ResourceTaskTO rtto = (ResourceTaskTO)to;
        if (rtto!=null) {
            TaskTO tto = rtto.getTask();
            if (tto!=null) {
                Long taskId = new Long(tto.getId());
                response = taskId.longValue();
            }
        }
        return response;
    }
    
    
    @Override
    public Vector<?> call(long initialCursor, long finalCursor) throws Exception {
        ResourceTaskBUS rtbus = new ResourceTaskBUS();
        Vector<?> vr = null;
        try {
            vr = rtbus.getListUntilID(initialCursor+"", finalCursor+"");
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return vr;
    }
        
    @Override
    public long getMaxId() throws Exception {
        long response = -1;
        try {        
	        TaskBUS tbus = new TaskBUS();
	        response = tbus.getMaxID();
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return response;        
    }
    
    
    @Override
    public Document getObjectToIndex(Object to) throws Exception {
        ResourceTaskDelegate del = new ResourceTaskDelegate(); 
        Document response = new Document();
        ResourceTaskTO rtto = (ResourceTaskTO)to;
        String content = "", history = ""; 
        
        TaskTO tto = rtto.getTask();

        Vector<TaskHistoryTO> hist = del.getHistory(tto.getId(), rtto.getResource().getId());
        if (hist!=null) {
            Iterator<TaskHistoryTO> i = hist.iterator();
            while(i.hasNext()){
                TaskHistoryTO thto = i.next();
                history = history.concat("<li>");
                if (thto.getComment()!=null) {
                    history = history.concat(thto.getComment());    
                }                
                history = history.concat("(<i>" + thto.getHandler().getName() + " - ");
                history = history.concat(thto.getStatus().getName() + " - ");
                history = history.concat(DateUtil.getDateTime(thto.getDate(), "yyyy-MM-dd hh:mm:ss") + "</i>)");
                history = history.concat("</li>");
            }
        }

        content = content.concat("<b>" + tto.getName() + "</b>");
        if (tto.getCategory()!=null && tto.getCategory().getName()!=null) {
            content = content.concat(" - " + tto.getCategory().getName());    
        }
        content = content.concat("<br>");
        content = content.concat(rtto.getResource().getName() + " (<i>" + rtto.getResource().getUsername()+ "</i>)</p>");
        
        content = content.concat("<table border=\"0\"><tr><td width=\"40\"></td><td class=\"tableCell\">");
        if (tto.getDescription()!=null) {
        	content = content.concat(tto.getDescription());	
        }
        content = content.concat(history);
        content = content.concat("</td></tr></table></p>");
        
        String addFields = this.getAdditionalFields(tto);
        if (addFields!=null) {
            content = content.concat(" " + addFields); 
        }
        
        response.add(new Field(KB_CONTENT, content, Field.Store.YES, Field.Index.TOKENIZED));
        response.add(new Field(KB_ID, tto.getId(), Field.Store.YES, Field.Index.TOKENIZED));
        response.add(new Field(KB_PROJECT_ID, tto.getProject().getId(), Field.Store.YES, Field.Index.UN_TOKENIZED));

        return response;
    }
    
    @SuppressWarnings("rawtypes")
	public Class getBusinessClass() throws Exception {
        return ResourceTaskTO.class;
    }
    
    
    private String getAdditionalFields(TaskTO tto){
        String response = null;
        Vector<AdditionalFieldTO> addFields = tto.getAdditionalFields();
        if (addFields!=null) {
            Iterator<AdditionalFieldTO> i = addFields.iterator();
            while(i.hasNext()) {
                AdditionalFieldTO af = i.next();
                response = response + "<li>" + " " + af.getValue() + "</li>";
            }
        }
        return response;
    }
    
}
