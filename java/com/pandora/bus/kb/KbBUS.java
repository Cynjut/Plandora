package com.pandora.bus.kb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.bus.GeneralBusiness;
import com.pandora.bus.ProjectBUS;
import com.pandora.delegate.IndexEngineDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;


/**
 * 
 */
public class KbBUS extends GeneralBusiness {
    
    
    public Vector search(String subject) throws BusinessException{
        IndexSearcher searcher = null;
        UserDelegate udel = new UserDelegate();
        Vector response = new Vector();
        ProjectDelegate pdel = new ProjectDelegate();
        
        UserTO root = udel.getRoot();
        PreferenceTO pref = root.getPreference();        
        String indexLocation = pref.getPreference(PreferenceTO.KB_INDEX_FOLDER);
        
        try {
            searcher = new IndexSearcher(indexLocation);
        } catch (Exception e) {  
            
        }
                   
	    Hits hits = this.query(subject, searcher);
	    if (hits!=null && hits.length()>0) {
	        HashMap projList = pdel.getProjectListToHash(false);
	        
	        for (int j=0 ; j<hits.length(); j++) {
	            Document doc;
	            try {
	                doc = hits.doc(j);
	                                            
	                KbDocumentTO kbDoc = new KbDocumentTO(doc);
	                response.add(kbDoc);
	
	                //link a project object with KbDocument
	                Object obj = projList.get(doc.get(KbIndex.KB_PROJECT_ID));
	                if (obj!=null) {
	                    kbDoc.setProject((ProjectTO)obj);
	                }
	            
	            } catch (CorruptIndexException e1) {
	                e1.printStackTrace();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }                        
	        }
	    }
        
        return response;
    }
    
    
    public KbIndex getKbByUniqueName(String uniqueName) throws Exception{        
        KbIndex response = null;
        UserDelegate udel = new UserDelegate();
        IndexEngineDelegate indexDel = new IndexEngineDelegate();
        
        UserTO root = udel.getRoot();
        PreferenceTO pref = root.getPreference();
        String classes = pref.getPreference(PreferenceTO.KB_BUS_CLASS);
        if (classes!=null) {
            String[] classList = classes.split(";");
            if (classList!=null && classList.length>0) {
                for (int i = 0; i<classList.length; i++) {
                    KbIndex kbus = indexDel.getKbClass(classList[i].trim());
                    if (kbus.getUniqueName().equals(uniqueName)){
                        response = kbus;
                        break;
                    }
                }
            }
        }
        return response;
    }

    public String getProjectSearchSintax(String projectId) throws BusinessException{
        String response = "(" + KbIndex.KB_PROJECT_ID + ":\"" + projectId + "\"";
        
        ProjectBUS pbus = new ProjectBUS();
        Vector childList = pbus.getProjectListByParent(new ProjectTO(projectId), false);
        if (childList!=null) {
            Iterator i = childList.iterator();
            while(i.hasNext()) {
                ProjectTO child = (ProjectTO)i.next();
                response = response + this.getProjectSearchSintax(child);
            }
        }
        return response + ")";
    }

    private String getProjectSearchSintax(ProjectTO project) throws BusinessException{
    	String response = "";
        ProjectBUS pbus = new ProjectBUS();
        Vector childList = pbus.getProjectListByParent(project, false);
        if (childList!=null) {
            Iterator i = childList.iterator();
            while(i.hasNext()) {
                ProjectTO child = (ProjectTO)i.next();
                response = response + getProjectSearchSintax(child);
            }
        }
        response = response + " or " + KbIndex.KB_PROJECT_ID + ":\"" + project.getId() + "\"";
        
        return response;
    }
        
    private Hits query(String subject, IndexSearcher searcher) {
        Hits hits = null;
        Analyzer analyzer = new StandardAnalyzer();

        try {
            
            QueryParser qp = new QueryParser(KbIndex.KB_CONTENT, analyzer);
            Query q = qp.parse(subject);
            hits = searcher.search(q);
                                   
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hits;
    }


}
