package com.pandora.bus.kb;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import com.pandora.TransferObject;
import com.pandora.bus.GenericPlugin;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

/**
 */
public class KbIndex extends GenericPlugin {  

    public static final String KB_CONTENT       = "contents";
    
    public static final String KB_ID            = "id";
    
    public static final String KB_PROJECT_ID    = "project_id";
    
    public static final String KB_TYPE          = "kb_type";
    
    public static final String KB_CREATION_DATE = "creation_date";
    
    
        
    private final static Object mutex = new Object();
    


    /** Should be implemented by each sub class with 
     * Knowledge Base Index business rule */        
    public String getContextLabel() {
        return null;
    }
    
    
    /** Should be implemented by each sub class with 
     * Knowledge Base Index business rule */            
    public Timestamp getCreationDate(Object to){
        return null;
    }
    

    /** Should be implemented by each sub class with 
     * Knowledge Base Index business rule */            
    public String getProjectId(Object to){
        return null;
    }

    
    /** Should be implemented by each sub class with 
     * Knowledge Base Index business rule */    
    public Vector<?> call(long initialCursor, long finalCursor) throws Exception {
        return null;
    }

    
    /** Should be implemented by each sub class with 
     * Knowledge Base Index business rule */    
    public long getMaxId() throws Exception {
        return -1;
    }    

    
    /** Should be implemented by each sub class with 
     * Knowledge Base Index business rule */    
    public Document getObjectToIndex(Object to) throws Exception {
        return null;
    }

    
    /** Should be implemented by each sub class with 
     * Knowledge Base Index business rule */    
    @SuppressWarnings("rawtypes")
	public Class getBusinessClass() throws Exception {
        return null;
    }
    

    /** Should be implemented by each sub class with 
     * Knowledge Base Index business rule */        
    public long getId(Object to) {
        return -1;
    }
    
    
    protected long add(File index, Vector<?> objectList, long defaultCursor) throws Exception{
        long response = -1;

        if (objectList!=null && !objectList.isEmpty()) {
            
            Iterator<?> i = objectList.iterator();
            while(i.hasNext()) {
            	Object to = i.next();
                Document doc = getObjectToIndex(to);
                if (doc!=null) {
                    doc = this.additionalFields(this.getDefaultLocale(), to, doc);
                    this.add(index, doc);                	
                }
            }
            
            //calculate the new max ID
            response = this.checkMaxId(objectList);
            
	        //log event...
            LogUtil.log(LogUtil.SUMMARY_KB_GENERATE, this, "system", LogUtil.LOG_INFO, objectList.size() + " object(s) add into Knowledge Base.");
            
        } else {
            response = defaultCursor;
        }
        
        return response;
    }
    

    private Document additionalFields(Locale loc, Object to, Document doc) {
        doc.add(new Field(KB_TYPE, this.getUniqueName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        
        Timestamp ts = this.getCreationDate(to);
        doc.add(new Field(KB_CREATION_DATE, DateUtil.getDateTime(ts, loc, 2, 2), Field.Store.YES, Field.Index.UN_TOKENIZED));

        String projectId = this.getProjectId(to);
        doc.add(new Field(KB_PROJECT_ID, projectId, Field.Store.YES, Field.Index.UN_TOKENIZED));
        
        return doc;
    }


    protected void add(File indexDir, Document doc, boolean optimizeIt) throws Exception{
        try {
            synchronized (mutex) {
                final IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(), false);
                writer.addDocument(doc);
                if (optimizeIt) {
                    writer.optimize();    
                }
                writer.close();
            }
        } catch (IOException e) {
            throw new Exception("Unable to index", e);
        }                
    }
    
    protected void add(File indexDir, Document doc) throws Exception{
        this.add(indexDir, doc, true);
    }

    
    public void update(File indexDir, KbIndex kbidx, TransferObject to, Document doc) throws Exception {
        synchronized (mutex) {
            doc = this.additionalFields(this.getDefaultLocale(), to, doc);
            this.delete(indexDir, kbidx, to);
            this.add(indexDir, doc, false);
        }
    }
    
    
    private void delete(File indexDir, KbIndex kbidx, TransferObject to) throws Exception{
        try {
            synchronized (mutex) {
                final IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(), false);
                writer.deleteDocuments(new Term(KB_ID, kbidx.getId(to)+""));
                //writer.optimize();
                writer.close();                
            }
        } catch (IOException e) {
            throw new Exception("Unable to index", e);
        }        
    }

    
    private long checkMaxId(Vector<?> objectList){
        Iterator<?> i = objectList.iterator();
        long buff = -1;
        while(i.hasNext()) {
            Object obj = i.next();
            long id = this.getId(obj);
            if (id > -1){
                if (buff < id) {
                    buff = id;
                }
            }
        }
        return buff;
    }

    private Locale getDefaultLocale(){
        //the locale contain an arbitrary value, just to store the date into index. 
        return new Locale("pt", "BR");   
    }
        
}
