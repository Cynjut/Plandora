package com.pandora.bus.alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.bus.EventBUS;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

public class CheckFileNotification extends Notification {

    private static final String CHECK_PATH  = "CHECK_PATH";
    private static final String CHECK_RULE  = "CHECK_RULE";
    private static final String CHECK_VALUE = "CHECK_VALUE";
    private static final String CHECK_TYPE  = "CHECK_TYPE";

    private static final String RULE_FIND_CONTENT      = "FIND_CONTENT";
    private static final String RULE_NOT_FIND_CONTENT  = "NOT_FIND_CONTENT";
    private static final String RULE_SIZE_GREATER_THAN = "SIZE_GREATER_THAN";
    private static final String RULE_SIZE_SMALLER_THAN = "SIZE_SMALLER_THAN";
    private static final String RULE_SIZE_EQUAL        = "SIZE_EQUAL";
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */
    public boolean sendNotification(Vector fields, Vector sqlData) throws Exception {
        EventBUS bus = new EventBUS();
        String[] rules = {RULE_FIND_CONTENT, RULE_NOT_FIND_CONTENT, RULE_SIZE_GREATER_THAN, 
        				  RULE_SIZE_SMALLER_THAN, RULE_SIZE_EQUAL};
        boolean response = true;
        BufferedReader in = null;
        
        try {
            String path  = this.getParamByKey(CHECK_PATH,  fields);
            String rule  = this.getParamByKey(CHECK_RULE,  fields);
            String value = this.getParamByKey(CHECK_VALUE, fields);
            String type  = this.getParamByKey(CHECK_TYPE,  fields);
            
            //validate if rule attribute contain a valid rule ID
            boolean ruleIsValid = false;
            for (int i =0; i<rules.length; i++) {
            	String ruleId = rules[i];
            	if (rule.trim().equalsIgnoreCase(ruleId)) {
            		ruleIsValid = true;
            		break;
            	}
            }
            
            if (ruleIsValid) {
            	
            	//replace current value using the key words..
            	value = this.replaceValue(value);
            	
            	//process the rule logic...
            	String content = null;
                if (type.equalsIgnoreCase("HTTP")) {
                	in = this.openURL(path);
                } else {
                	in = this.openFile(path);                	
                }
                
                if (in != null) {
                	if (rule.trim().equalsIgnoreCase(RULE_FIND_CONTENT)) {
                		content = this.findContent(path, in, value, true);
                		
                	} else if (rule.trim().equalsIgnoreCase(RULE_NOT_FIND_CONTENT)) {
                    	content = this.findContent(path, in, value, false);
                    		
                	} else {
                		content = this.checkSize(path, in, value, rule.trim());
                	}
                }
                
                //if the rule pass...
                if (content!=null) {
                	bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_CHECK_FILE, 
                			content, RootTO.ROOT_USER, null);	
                } else {
                	bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_CHECK_FILE, 
                			"NULL rule: [" + rule + "]", RootTO.ROOT_USER, null);
                }
            }        	
            
        } catch (Exception e) {
        	e.printStackTrace();
        	response = false;
    	} finally {
    		if (in!=null) {
    			in.close();	
    		}
    	}
        
        return response;
    }

            
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFields()
     */        
    public Vector getFields(){
        Vector response = new Vector();
        
        Vector typeList = new Vector();
        typeList.add(new TransferObject("HTTP", "HTTP"));
        typeList.add(new TransferObject("FILE", "FILE"));
        response.add(new FieldValueTO(CHECK_TYPE, "notification.checkFile.type", typeList));
        
        response.add(new FieldValueTO(CHECK_PATH, "notification.checkFile.path", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));

        Vector ruleList = new Vector();
        ruleList.add(new TransferObject(RULE_FIND_CONTENT, RULE_FIND_CONTENT));
        ruleList.add(new TransferObject(RULE_NOT_FIND_CONTENT, RULE_NOT_FIND_CONTENT));
        ruleList.add(new TransferObject(RULE_SIZE_GREATER_THAN, RULE_SIZE_GREATER_THAN));
        ruleList.add(new TransferObject(RULE_SIZE_SMALLER_THAN, RULE_SIZE_SMALLER_THAN));
        ruleList.add(new TransferObject(RULE_SIZE_EQUAL, RULE_SIZE_EQUAL));
        response.add(new FieldValueTO(CHECK_RULE, "notification.checkFile.rule", ruleList));
        
        response.add(new FieldValueTO(CHECK_VALUE, "notification.checkFile.value", FieldValueTO.FIELD_TYPE_TEXT, 100, 30));
        
        return response;
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "Check File";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.checkFile.help";
    }

    
    private String replaceValue(String value){
    	String response = value;
    	
    	String token = "";
    	int tokenIni = value.indexOf("#");
    	if (tokenIni>-1) {
    		int tokenEnd = value.indexOf("#", tokenIni+1);
    		if (tokenEnd>-1) {
    			token = value.substring(tokenIni+1, tokenEnd);
    			String[] parts = token.split("\\|");
    			if (parts!=null && parts.length==2) {
    				
    				if (parts[0].equalsIgnoreCase("DATE")) {
    					String dateStr = DateUtil.getDateTime(DateUtil.getNow(), parts[1].trim());
    					response = value.substring(0, tokenIni);
    					response = response + dateStr;
    					response = response + value.substring(tokenEnd+1);
    				}

    			}
    		}
    	}
    	return response;
    }
    
    
    private BufferedReader openFile(String path) throws Exception{
    	BufferedReader in = null;
    	try {
            File file = new File(path);
            if (file.exists()) {
               	in = new BufferedReader(new FileReader(file));
            }    		
	    } catch (Exception e) {
	    	throw new Exception(e);
	    }
        
        return in;
    }
    
    private BufferedReader openURL(String path) throws Exception{
    	OutputStreamWriter wr = null;
    	BufferedReader in = null;
    	try {
    		URL url = new URL(path);
    		URLConnection conn = url.openConnection();
    		conn.setDoOutput(true);
    		wr = new OutputStreamWriter(conn.getOutputStream());
    		wr.write("");
    		wr.flush();
    
    		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    } catch (Exception e) {
	    	throw new Exception(e);
	    } finally {
	        if (wr!=null) wr.close();
	    }
	    return in; 
    }
    

    private String findContent(String path, BufferedReader in, String value, boolean mustFind) throws Exception{
    	String response = null;
    	try {
    		String contentFound = null;
            String str;
            while ((str = in.readLine()) != null) {
                if (str.indexOf(value)>-1) {
                	contentFound = str;
                	break;
                }
            }
            
        	if (contentFound!=null && mustFind) {
        		response = contentFound + " File [" + path + "]"; 
        	} else if (contentFound==null && !mustFind) {
           		response = "Content [" + value + "] was not found into the file. File [" + path + "]";            	
            }
    		
    	} catch (Exception e) {
    		throw new Exception(e);
    	}

    	return response;
    }
    
    
    private String checkSize(String path, BufferedReader in, String value, String rule) throws Exception{
    	String response = null;
    	String fileContent = "";
    	try {
            String str;
            while ((str = in.readLine()) != null) {
                fileContent = fileContent.concat(str);
            }
    		long size = fileContent.length();
    		int baseline = Integer.parseInt(value);
    		
    		if (rule.equals(RULE_SIZE_GREATER_THAN)) {
    			if (size > baseline) {
    				response = "SIZE_GREATER_THAN: file size [" + size + "] > value [" + baseline + "] path: [" + path + "]";
    			}
            	
    		} else if (rule.equals(RULE_SIZE_SMALLER_THAN)) {
    			if (size < baseline) {
    				response = "SIZE_SMALLER_THAN: file size [" + size + "] < value [" + baseline + "] path: [" + path + "]";
    			}
    			    			
    		} else if (rule.equals(RULE_SIZE_EQUAL)) {
    			if (size == baseline) {
    				response = "SIZE_EQUAL: file size [" + size + "] = value [" + baseline + "] path: [" + path + "]";
    			}
    		}
    		
    	} catch (Exception e) {
    		throw new Exception(e);
    	}

    	return response;
    }
    

    
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldTypes()
     */    
    public Vector getFieldTypes() {
        return null;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldKeys()
     */    
    public Vector getFieldKeys() {
        return null;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldLabels()
     */
    public Vector getFieldLabels() {
        return null;
    }
    
}
