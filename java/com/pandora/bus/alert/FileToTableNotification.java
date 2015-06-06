package com.pandora.bus.alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import com.pandora.DBQueryParam;
import com.pandora.FieldValueTO;
import com.pandora.NotificationFieldTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.bus.EventBUS;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;

public class FileToTableNotification extends Notification {

    private static final String SOURCE_PATH       = "SOURCE_PATH";
    private static final String SOURCE_TYPE       = "SOURCE_TYPE";
    private static final String SOURCE_SEPARATOR  = "SOURCE_SEPARATOR";
    
    private static final String PROC_DEBUG         = "PROC_DEBUG";
    private static final String PROC_DELETE_BEFORE = "PROC_DEL_BEFORE";
    
    private static final String DEST_TABLE_NAME        = "DEST_TABLE_NAME";
    private static final String DEST_TABLE_FIELDS      = "DEST_TABLE_FIELDS";
    private static final String DEST_TABLE_FIELD_TYPES = "DEST_TABLE_FIELD_TYPES";
    private static final String DEST_DB_CONNECTION     = "DEST_DB_CONN";


    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */
    public boolean sendNotification(Vector<NotificationFieldTO> fields, Vector<Vector<Object>> sqlData) throws Exception {
    	boolean response = true;
    	BufferedReader in = null;

    	try {
	        String path = this.getParamByKey(SOURCE_PATH,  fields);
	        String type = this.getParamByKey(SOURCE_TYPE,  fields);
	        String isDebug = this.getParamByKey(PROC_DEBUG,  fields);
	    
	        if (type.equalsIgnoreCase("HTTP")) {
	        	in = this.openURL(path);
	        } else {
	        	in = this.openFile(path);                	
	        }
	        
	        this.insertFile(in, fields, isDebug.equalsIgnoreCase("OK"));
 	        
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

    
    private void insertFile(BufferedReader in, Vector<NotificationFieldTO> fields, boolean isDebug) throws Exception {
    	DbQueryDelegate db = new DbQueryDelegate();
    	EventBUS bus = new EventBUS();
    	ArrayList<String> statement = new ArrayList<String>();
		ArrayList<DBQueryParam> dquery = new ArrayList<DBQueryParam>();
		
    	String deleteBefore = this.getParamByKey(PROC_DELETE_BEFORE, fields);
    	
    	String sep    = this.getParamByKey(SOURCE_SEPARATOR, fields);
		String table  = this.getParamByKey(DEST_TABLE_NAME, fields);
		String flist  = this.getParamByKey(DEST_TABLE_FIELDS, fields);
		String ftlist = this.getParamByKey(DEST_TABLE_FIELD_TYPES, fields);
		String conn   = this.getParamByKey(DEST_DB_CONNECTION, fields);

		String dbconnection = "";
		if (!conn.equalsIgnoreCase("LOCAL")) {
			dbconnection = "[" + conn + "]";
		}
		
		//remove all records before execution...
		if (deleteBefore.equalsIgnoreCase("OK")) {
			db.executeQuery(dbconnection + "delete from " + table);
		}
				
        String str;
        while ((str = in.readLine()) != null) {
        	String[] tokens = str.split(sep);
        	String[] f = flist.split(";");
        	String[] t = ftlist.split(";");
        	
        	int[] tp = new int[t.length];
        	Vector<Object> param = new Vector<Object>();
        	
        	if (tokens.length == f.length && f.length == t.length) {
        		
        		String sqlValues = "";
        		String sql = "insert into " + table + " (";
        		for(int i = 0; i<tokens.length; i++) {
        			if (i > 0) {
        				sql = sql + ", ";
        				sqlValues = sqlValues + ", ";  
        			}
        			sql = sql + f[i].trim();
        			sqlValues = sqlValues + "?";
        			
        			String[] typedef = t[i].trim().split(",");
        			int type = Integer.parseInt(typedef[0].trim());
        			String pattern = "";
        			if (typedef.length>1) {
        				pattern = typedef[1].trim();
        			}
        			
        			tp[i] = type;
        			param.add(this.getObject(tokens[i], type, pattern));
        		}
        		sql = sql + ") values (" + sqlValues + ")";
        		
        		if (isDebug) {
			        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "preparing insert statement: [" + 
			        		sql + "][" + str + "]", RootTO.ROOT_USER, null);							
        		}
        		statement.add(dbconnection + sql);
        		dquery.add(new DBQueryParam(tp, param));
        	} else {
        		if (isDebug) {
			        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "The number of fields [" + f.length + "] or types [" + t.length + "] is different from number of values: [" + 
			        		str + "]", RootTO.ROOT_USER, null);							
        		}
        	}	
        }
        
        if (statement.size()>0 && dquery.size()>0 && statement.size()==dquery.size()) {
        	db.executeQuery(statement, dquery);	
        } else {
    		if (isDebug) {
		        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, "The number of fields [" + dquery.size() + "] is different from number of values: [" + 
		        		statement.size() + "]", RootTO.ROOT_USER, null);							
    		}        	
        }
	}


	private Object getObject(String value, int type, String pattern) {
		Object response = null;

		if (type==Types.VARCHAR) {
			response = value;
		} else if (type==Types.INTEGER) {
			response = new Integer(Integer.parseInt(value));
		} else if (type==Types.DOUBLE) {
			response = new Double(Double.parseDouble(value));
		} else if (type==Types.TIMESTAMP) {
			response = DateUtil.getDateTime(value, pattern, new Locale("US"));
		}
		
		return response;
	}


	/* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFields()
     */        
    public Vector<FieldValueTO> getFields(){
        Vector<FieldValueTO> response = new Vector<FieldValueTO>();
        
        Vector<TransferObject> typeList = new Vector<TransferObject>();
        typeList.add(new TransferObject("HTTP", "HTTP"));
        typeList.add(new TransferObject("FILE", "FILE"));
        response.add(new FieldValueTO(SOURCE_TYPE, "notification.file2table.type", typeList));
        
        response.add(new FieldValueTO(SOURCE_PATH, "notification.file2table.path", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));

        response.add(new FieldValueTO(SOURCE_SEPARATOR, "notification.file2table.sep", FieldValueTO.FIELD_TYPE_TEXT, 10, 10));

        response.add(new FieldValueTO(DEST_TABLE_NAME, "notification.file2table.tablename", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));
        response.add(new FieldValueTO(DEST_TABLE_FIELDS, "notification.file2table.fields", FieldValueTO.FIELD_TYPE_TEXT, 255, 50));
        response.add(new FieldValueTO(DEST_TABLE_FIELD_TYPES, "notification.file2table.fieldtypes", FieldValueTO.FIELD_TYPE_TEXT, 255, 50));
        response.add(new FieldValueTO(DEST_DB_CONNECTION, "notification.file2table.uri", FieldValueTO.FIELD_TYPE_TEXT, 255, 50));

        Vector<TransferObject> boolList = new Vector<TransferObject>();
        boolList.add(new TransferObject("NOK", "-"));
        boolList.add(new TransferObject("OK", "OK"));
        response.add(new FieldValueTO(PROC_DEBUG, "notification.file2table.debug", boolList));
        response.add(new FieldValueTO(PROC_DELETE_BEFORE, "notification.file2table.delete", boolList));

        
        return response;
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "File 2 Table";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.file2table.help";
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
  
    
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldTypes()
     */    
    public Vector<String> getFieldTypes() {
        return null;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldKeys()
     */    
    public Vector<String> getFieldKeys() {
        return null;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldLabels()
     */
    public Vector<String> getFieldLabels() {
        return null;
    }    
}
