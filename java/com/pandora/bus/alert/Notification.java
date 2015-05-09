package com.pandora.bus.alert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.NotificationFieldTO;
import com.pandora.RootTO;
import com.pandora.bus.EventBUS;
import com.pandora.bus.GenericPlugin;
import com.pandora.helper.LogUtil;

/**
 */
public class Notification extends GenericPlugin{
    
    private static final String TOKEN_INITIAL = "#COL_";
    
    private static final String TOKEN_FINAL   = "#";
    
    
    /** Should be implemented by each sub class with 
     * notification business rule */
    public boolean sendNotification(Vector fields, Vector sqlData) throws Exception {
        return false;
    }
        
    /**
     * Should be implemented by each sub class with 
     * the labels of specific fields that will be shown 
     * by PLANdora in GUI */
    public Vector getFieldLabels() {
        return null;
    }
    
    
    /**
     * Should be implemented by each sub class with
     * the types of each specific field that will be used by PLANdora
     * to validate the GUI fields inputs.
     * 
     * <li>Text Box = new String("1")</li>
     * <li>Boolean Combo Box = new String("2")</li>
     * <li>Password Text Box = new String("3")</li>
     * 
     */
    public Vector getFieldTypes() {
        return null;
    }
    
    
    /**
     * Should be implemented by each sub class with
     * the key of each specific field. Each key should be unique.
     */
    public Vector getFieldKeys(){
        return null;
    }
    
  
    /**
     * Should be (optionally) implemented by each sub class with
     * a list of FieldValueTO objects .
     */
    public Vector getFields(){
        return null;
    }
    
    /**
     * Should be implemented by each sub class.
     * The text returned by this method will be used into
     * GUI suck a help text.
     */
    public String getContextHelp() {
        return "";
    }
    
    
    
    protected String getParamByKey(String key, Vector fields){
        String response = null;
        if (fields!=null && !fields.isEmpty()){
            Iterator i = fields.iterator();
            while(i.hasNext()) {
                NotificationFieldTO field = (NotificationFieldTO)i.next();
                if (field.getName().equalsIgnoreCase(key)){
                    response = field.getValue();
                    break;
                }
            }
        }
        return response;
    }

    
    public ArrayList getToken(String content) {
        ArrayList response = new ArrayList();
        if (content!=null) {
            String[] raw = content.split(TOKEN_INITIAL); 
            if (raw!=null && raw.length>0) {
                for(int i=0; i<raw.length; i++) {
                    int end = raw[i].indexOf(TOKEN_FINAL);
                    if (end>0) {
                        String colStr = raw[i].substring(0, end);
                        Integer value = null;
                        try {
                            value = new Integer(colStr);
                        } catch(Exception e){
                            value = null;
                        }
                        if (value!=null) {
                            response.add(value);
                        }                    
                    }
                }
            }            
        }
        return response;
    }
    
    
    public String replaceByToken(Vector fieldList, String source) {
        ArrayList tokenIds = this.getToken(source);
        for (int i=0; i< tokenIds.size(); i++) {
            Integer tokenId = (Integer)tokenIds.get(i);
            String token = TOKEN_INITIAL + tokenId + TOKEN_FINAL;
            String s = null;
            try {
                s = fieldList.get(tokenId.intValue()-1) + "";    
            } catch(Exception e) {
                s = "ERR!";
            }
            source = source.replaceAll(token, s);
        }
        return source;
    }
    
    
    public String getContent(Vector sqlData, boolean considerFirstRow, boolean showBrackets){
        String content = "";
        if (sqlData!=null) {
        	
        	int initial = 0;
        	if (!considerFirstRow) {
        		initial = 1;
        	}
        	
            for (int i= initial; i<sqlData.size(); i++) {
            	if (showBrackets) {
            		content = content.concat(sqlData.elementAt(i)+"");	
            	} else {
            		Vector line = (Vector)sqlData.elementAt(i);
                    for (int j=0; j<line.size(); j++) {
                    	content = content.concat(line.elementAt(j) + " ");
                    }
            	}
            }            
        }
        return content;
    }
    
    public String getHtmlContent(Vector sqlData){
        String content = "<TABLE BORDER=\"1\">";
        if (sqlData!=null) {
			Iterator it = sqlData.iterator();
			while(it.hasNext()) {
				content = content + "<TR>";			    
			    Vector sqlDataItem = (Vector)it.next();
	            for (int i= 0; i<sqlDataItem.size(); i++) {
	                content = content + "<TD>" + sqlDataItem.elementAt(i) + "</TD>";
	            }
				content = content + "</TR>";	            
			}
        }
        return content + "</TABLE>";        
    }
    
    protected void log(String content) throws Exception {
    	EventBUS bus = new EventBUS();
    	bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, content, RootTO.ROOT_USER, null);
    }
    
    protected boolean isQuery() {
    	return true;
    }
    
    
    public String getType(String key){
    	String response = null;
    	Vector list = getFields();
        if (list!=null) {
            Iterator i = list.iterator();
            while(i.hasNext()) {
            	FieldValueTO to = (FieldValueTO)i.next();
                if (to.getId().equals(key)) {
                    response = to.getType();
                    break;
                }
            }
        }
        return response;
    }    
    
}