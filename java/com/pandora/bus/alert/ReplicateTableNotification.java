package com.pandora.bus.alert;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Vector;

import java.sql.PreparedStatement;
import com.pandora.FieldValueTO;
import com.pandora.TransferObject;
import com.pandora.delegate.DbQueryDelegate;

public class ReplicateTableNotification extends Notification {
	
	private static final String REPLI_DEST_DRIVER = "REPLI_DVR";
    private static final String REPLI_DEST_URL    = "REPLI_URL";
    private static final String REPLI_DEST_USER   = "REPLI_USR";
    private static final String REPLI_DEST_PASS   = "REPLI_PSS";
    private static final String REPLI_DEST_TABLE  = "REPLI_TBL";
    private static final String REPLI_SQL         = "REPLI_SQL";
    
    private static final String REPLI_INSERT_SQL  = "REPLI_INS";
    private static final String REPLI_DEL_FIRST   = "REPLI_DEL";
    private static final String REPLI_DEBUG       = "REPLI_DBG";

	private String debug = "NOK";
	
	@Override
	public boolean sendNotification(Vector fields, Vector sqlData) throws Exception {
    	DbQueryDelegate dbQuery = new DbQueryDelegate();
    	boolean response = true;

    	PreparedStatement pstmt = null;
    	Connection c = null;
    	
    	try {
	    	this.debug = this.getParamByKey(REPLI_DEBUG, fields);
	    	if (this.debug==null) {
	    		this.debug = "KO"; 
	    	}
	    		    		    	
			String destTable = this.getParamByKey(REPLI_DEST_TABLE, fields);
			String insertSql = this.getParamByKey(REPLI_INSERT_SQL, fields);
			String sql = this.getParamByKey(REPLI_SQL, fields);
			Vector sourceData = dbQuery.performQuery(sql);
			if (sourceData!=null && destTable!=null && insertSql!=null && sourceData.size()>0) {
				
				if (this.debug.equals("OK")) {
					System.out.println("sourceData is loaded... [" + sourceData.size() + "] rows");
				}
				
		    	//open the connection to destination database...
		    	c = this.getConnection(true, fields);
				if (this.debug.equals("OK")) {
					System.out.println("Connection to destination is ready [" + c + "]");	
				}
				
				//clean all data of destination table...
				String deleteall = this.getParamByKey(REPLI_DEL_FIRST, fields);
				if (deleteall!=null && deleteall.equals("OK")) {
					pstmt = c.prepareStatement("delete from " + destTable);
					pstmt.executeUpdate();
					if (this.debug.equals("OK")) {
						System.out.println("all rows has been removed from [" + destTable + "]...");	
					}
				}
				
				for (int j=1; j<sourceData.size(); j++) {
					Vector item = (Vector)sourceData.elementAt(j);
					pstmt = (PreparedStatement) c.prepareStatement(insertSql);
					
					for (int k=0; k<item.size(); k++) {
						Object colItem = item.elementAt(k);
						if (colItem instanceof Timestamp) {
							pstmt.setTimestamp(k+1, (Timestamp)colItem);
						} else if (colItem instanceof Integer) {
							pstmt.setInt(k+1, (Integer)colItem);
						} else if (colItem instanceof Float) {
							pstmt.setFloat(k+1, (Float)colItem);
						} else if (colItem instanceof Date) {
							pstmt.setDate(k+1, (Date)colItem);
						} else {
							pstmt.setString(k+1, (String)colItem);
						}							
					}
					
					pstmt.executeUpdate();
					if (this.debug.equals("OK")) {
						System.out.println("new line inserted into [" + destTable + "]...");	
					}					
				}
				
			} else {
				if (this.debug.equals("OK")) {
					System.out.println("[" + sourceData + "] [" + destTable + "] [" + insertSql + "]");	
				}
			}
    		
		} catch(Exception e){
			e.printStackTrace();
			response = false;
			
		} finally{
			try {
				if (c!=null) {
					c.close();
				}				
			} catch (Exception e){
				e.printStackTrace();	
			}
		}		
		return response;
	}

	
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFields()
     */        
    public Vector getFields(){
        Vector response = new Vector();

        response.add(new FieldValueTO(REPLI_DEST_DRIVER, "notification.replicate.driver", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));
        response.add(new FieldValueTO(REPLI_DEST_URL, "notification.replicate.url", FieldValueTO.FIELD_TYPE_TEXT, 100, 50));
        response.add(new FieldValueTO(REPLI_DEST_USER, "notification.replicate.user", FieldValueTO.FIELD_TYPE_TEXT, 30, 10));
        response.add(new FieldValueTO(REPLI_DEST_PASS, "notification.replicate.pass", FieldValueTO.FIELD_TYPE_PASS, 30, 10));

        response.add(new FieldValueTO(REPLI_SQL, "notification.replicate.selectSql", FieldValueTO.FIELD_TYPE_AREA, 85, 4));
        response.add(new FieldValueTO(REPLI_INSERT_SQL, "notification.replicate.insertSql", FieldValueTO.FIELD_TYPE_AREA, 85, 4));
        response.add(new FieldValueTO(REPLI_DEST_TABLE, "notification.replicate.table", FieldValueTO.FIELD_TYPE_TEXT, 30, 20));

        Vector boolList = new Vector();
        boolList.add(new TransferObject("OK", "OK"));
        boolList.add(new TransferObject("NOK", "NOK"));
        response.add(new FieldValueTO(REPLI_DEL_FIRST, "notification.replicate.removeFirst", boolList));
        response.add(new FieldValueTO(REPLI_DEBUG, "notification.replicate.verbose", boolList));

        return response;
    }
    

	/* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "Replicate Table";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.replicate.help";
    }
    
    
	public Connection getConnection(boolean isAutoCommit, Vector fields) throws Exception{
		Connection con = null;

		try {
			String driver = this.getParamByKey(REPLI_DEST_DRIVER, fields);
	    	String url = this.getParamByKey(REPLI_DEST_URL, fields);
	    	String user = this.getParamByKey(REPLI_DEST_USER, fields);
	    	String pass = this.getParamByKey(REPLI_DEST_PASS, fields);

		    Class.forName(driver);
		    con = java.sql.DriverManager.getConnection(url, user, pass);
		    con.setAutoCommit(isAutoCommit);
		    
		    if(!con.isClosed()) {
				if (this.debug.equals("OK")) {
					System.out.println("Successfully connected to [" + driver + "] [" + url + "] using JDBC...");	
				}
		    }
		    
		}catch(Exception e)	{
		    throw new Exception(e);
		}

		return con;
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
