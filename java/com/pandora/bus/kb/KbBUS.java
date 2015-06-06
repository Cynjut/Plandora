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
import org.apache.lucene.search.Hit;
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


public class KbBUS extends GeneralBusiness {
    
    

	public Vector<KbDocumentTO> search(String subject) throws BusinessException{
        IndexSearcher searcher = null;
        UserDelegate udel = new UserDelegate();
        Vector<KbDocumentTO> response = new Vector<KbDocumentTO>();
        ProjectDelegate pdel = new ProjectDelegate();
        
        UserTO root = udel.getRoot();
        PreferenceTO pref = root.getPreference();        
        String indexLocation = pref.getPreference(PreferenceTO.KB_INDEX_FOLDER);
        
        try {
            searcher = new IndexSearcher(indexLocation);
        } catch (Exception e) {  
            e.printStackTrace();
        }

       /*
        try {
			computeTopTermQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
        */
	    Hits hits = this.query(subject, searcher);
	    if (hits!=null && hits.length()>0) {
	        HashMap<String, ProjectTO> projList = pdel.getProjectListToHash(false);
	        
	        @SuppressWarnings("unchecked")
			Iterator<Hit> i = hits.iterator();
	        
            while (i.hasNext()){
            	Hit hit = i.next();
  	            Document doc;
	            try {
	                doc = hit.getDocument();	                
	                KbDocumentTO kbDoc = new KbDocumentTO(doc, hit.getScore());
	                response.add(kbDoc);
	
	                //link a project object with KbDocument
	                ProjectTO prj = projList.get(doc.get(KbIndex.KB_PROJECT_ID));
	                if (prj!=null) {
	                    kbDoc.setProject(prj);
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
        Vector<ProjectTO> childList = pbus.getProjectListByParent(new ProjectTO(projectId), false);
        if (childList!=null) {
            Iterator<ProjectTO> i = childList.iterator();
            while(i.hasNext()) {
                ProjectTO child = i.next();
                response = response + this.getProjectSearchSintax(child);
            }
        }
        return response + ")";
    }

    private String getProjectSearchSintax(ProjectTO project) throws BusinessException{
    	String response = "";
        ProjectBUS pbus = new ProjectBUS();
        Vector<ProjectTO> childList = pbus.getProjectListByParent(project, false);
        if (childList!=null) {
            Iterator<ProjectTO> i = childList.iterator();
            while(i.hasNext()) {
                ProjectTO child = i.next();
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

/*
    private void computeTopTermQuery() throws Exception {
    	UserDelegate udel = new UserDelegate();
        HashMap<String,Integer> frequencyMap = new HashMap<String,Integer>();
        ArrayList<String> termlist = new ArrayList<String>();
        
        UserTO root = udel.getRoot();
        PreferenceTO pref = root.getPreference();        
        String indexLocation = pref.getPreference(PreferenceTO.KB_INDEX_FOLDER);
        
        IndexReader reader = IndexReader.open(indexLocation);
        TermEnum terms = reader.terms();
        while (terms.next()) {
          Term term = terms.term();
          String termText = term.text();
          int frequency = reader.docFreq(term);
          frequencyMap.put(termText, frequency);
          termlist.add(termText);
        }
        reader.close();
        
        StringBuilder termBuf = new StringBuilder();
        BooleanQuery q = new BooleanQuery();
        for (String t : termlist) {
        	System.out.println(">>> " + t + " : " + frequencyMap.get(t));
        }
      }    
 */
    
}
