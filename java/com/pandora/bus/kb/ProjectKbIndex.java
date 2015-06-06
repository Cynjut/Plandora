package com.pandora.bus.kb;

import java.sql.Timestamp;
import java.util.Vector;

import org.apache.lucene.document.Document;

import com.pandora.ProjectTO;

public class ProjectKbIndex extends KbIndex {

    @Override
    public String getUniqueName() {
        return "ProjectKbIndex";
    }

    @Override
    public String getContextLabel() {
        return "label.viewKb.Proj";
    }

    @Override
    public Timestamp getCreationDate(Object to){
        ProjectTO pto = (ProjectTO)to;
        return pto.getCreationDate();
    }
    
    @Override
    public String getProjectId(Object to){
        ProjectTO pto = (ProjectTO)to;
        return pto.getId();
    }

    @Override
    public Vector<?> call(long initialCursor, long finalCursor) throws Exception {
        return null;
    }
        
    @Override
    public long getMaxId() throws Exception {
        return super.getMaxId();
    }
    
    @Override
    public Document getObjectToIndex(Object to) throws Exception {
        return super.getObjectToIndex(to);
    }
    
    
    @SuppressWarnings("rawtypes")
	public Class getBusinessClass() throws Exception {
        return ProjectTO.class;
    }

}
