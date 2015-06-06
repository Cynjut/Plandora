package com.pandora.bus.kb;

import java.sql.Timestamp;
import java.util.Locale;

import org.apache.lucene.document.Document;

import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.helper.DateUtil;

/**
 */
public class KbDocumentTO extends TransferObject{

	private static final long serialVersionUID = 1L;

    private Document doc = null;
    
    private String type = null;
    
    private ProjectTO project= null;
    
    private float score = 0;
    
    
    public KbDocumentTO(Document d, float sc) {
        this.doc = d;
        this.type = d.get(KbIndex.KB_TYPE);
        this.score = sc;
    }
    
    public Timestamp getCreationDate(){
                
        Timestamp ts = null;
        String tsStr = doc.get(KbIndex.KB_CREATION_DATE);
        if (tsStr!=null && tsStr.trim().length()>0) {
            ts = DateUtil.getDateTime(tsStr, "dd/MM/yyyy hh:mm:ss", new Locale("pt", "BR"));    
        }
        return ts;
    }
    
    public float getRelevance(){
        return score;
    }
    
    /////////////////////////////////////    
    public String getType(){
        return this.type;
    }
    
    /////////////////////////////////////
    public Document getDoc() {
        return this.doc;
    }
    
    /////////////////////////////////////    
    public ProjectTO getProject() {
        return project;
    }
    public void setProject(ProjectTO newValue) {
        this.project = newValue;
    }
}
