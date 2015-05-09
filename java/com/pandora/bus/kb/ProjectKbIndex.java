package com.pandora.bus.kb;

import java.sql.Timestamp;
import java.util.Vector;

import org.apache.lucene.document.Document;

import com.pandora.ProjectTO;

/**
 */
public class ProjectKbIndex extends KbIndex {

    public String getUniqueName() {
        return "ProjectKbIndex";
    }

    public String getContextLabel() {
        return "label.viewKb.Proj";
    }

    public Timestamp getCreationDate(Object to){
        ProjectTO pto = (ProjectTO)to;
        return pto.getCreationDate();
    }
    
    public String getProjectId(Object to){
        ProjectTO pto = (ProjectTO)to;
        return pto.getId();
    }

    
    public Vector call(long initialCursor, long finalCursor) throws Exception {
        return null;
    }
        
    
    public long getMaxId() throws Exception {
        return super.getMaxId();
    }
    
    
    public Document getObjectToIndex(Object to) throws Exception {
        return super.getObjectToIndex(to);
    }
    
    
    public Class getBusinessClass() throws Exception {
        return ProjectTO.class;
    }

}
