package com.pandora.bus.kb;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

import com.pandora.PreferenceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;

/**
 */
public class IndexEngineBUS {

    private File index;
    private int step;
    private final static Object mutex = new Object();
    private UserTO caller;


    public IndexEngineBUS() throws Exception {
        
        //get the root user from data base
        UserDelegate udel = new UserDelegate();
        UserTO root = udel.getRoot();
        PreferenceTO pref = root.getPreference();
        
        this.index = new File(pref.getPreference(PreferenceTO.KB_INDEX_FOLDER));
        this.step = 50;
        this.caller = root;
        
        if (!index.exists()){
            index.mkdir();            
            this.reset();
        }
    }
    
    
    public void reset() throws IOException {
        synchronized (mutex){
            final IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), true);
            writer.close();
        }       
    }

    
    public void call() throws Exception{
        
        String classes = caller.getPreference().getPreference(PreferenceTO.KB_BUS_CLASS);
	    if (classes!=null) {
	        String[] classList = classes.split(";");
	        if (classList!=null && classList.length>0) {
	            for (int i = 0; i<classList.length; i++) {
	                String classStr = classList[i].trim();
	                KbIndex kbus = getKbClass(classStr);
                    if (kbus!=null) {
                        this.call(kbus);
                    }
	            }
	        }
	        
	        PreferenceDelegate pdel = new PreferenceDelegate(); 
	        pdel.insertOrUpdate(caller.getPreference());            	        
	    }
    }
    
    
    private void call(KbIndex kbus) throws Exception{
        try {
            PreferenceTO pto = caller.getPreference();
            String cursorKey = PreferenceTO.KB_CURSOR_PREFIX + kbus.getUniqueName();
            long initial = new Long(pto.getPreference(cursorKey)).longValue();
            
            String maxKey = PreferenceTO.KB_MAX_PREFIX + kbus.getUniqueName();
            long max = kbus.getMaxId();
                
            if (initial<max) {
                long finalCursor = initial + step;
                if (finalCursor>max) {
                    finalCursor = max;
                }

                //call the data from specific entity!!!!                        
                Vector vr = kbus.call(initial, finalCursor);
                
                long cursor = kbus.add(index, vr, finalCursor);
                pto.addPreferences(new PreferenceTO(cursorKey, cursor+"", caller));                
            }
            
            pto.addPreferences(new PreferenceTO(maxKey, max+"", caller));            
            
        } catch (BusinessException e) {
            e.printStackTrace();
        }
    }
    
    
	public static KbIndex getKbClass(String className){
	    KbIndex response = null;
        try {
            Class klass = Class.forName(className);
            response = (KbIndex)klass.newInstance();
        } catch (Exception e) {
            response = null;
        }
        return response;
	}


    public void update(TransferObject to) throws Exception {
        String classes = caller.getPreference().getPreference(PreferenceTO.KB_BUS_CLASS);
	    if (classes!=null) {
	        String[] classList = classes.split(";");
	        if (classList!=null && classList.length>0) {
	            for (int i = 0; i<classList.length; i++) {
	                String classStr = classList[i].trim();
	                KbIndex kbus = getKbClass(classStr);
                    if (kbus!=null && kbus.getBusinessClass()!=null 
                            && (to.getClass().equals(kbus.getBusinessClass()))) {
                        Document doc = kbus.getObjectToIndex(to);
                        if (doc!=null) {
                            kbus.update(index, kbus, to, doc);
                        }
                        break;
                    }
	            }
	        }
	    }
    }
    

}
