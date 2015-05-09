package com.pandora.bus.alert;

import java.util.Vector;

import com.pandora.bus.EventBUS;
import com.pandora.helper.LogUtil;

public class ExecuteNotification extends Notification {

	private static final String LOG_SUMMARY  = "LOG_SUMMARY";
	private static final String LOG_USERNAME = "LOG_USERNAME";
	
	/* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */    
    public boolean sendNotification(Vector fields, Vector sqlData) throws Exception {
        EventBUS bus = new EventBUS();
        
        String summary = this.getParamByKey(LOG_SUMMARY, fields);
        String username = this.getParamByKey(LOG_USERNAME, fields);
        
        Integer rows = (Integer)sqlData.elementAt(0);
        bus.insertEvent(LogUtil.LOG_INFO, summary, rows + " row(s) affected", username, null);    
        
        return true;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldLabels()
     */
    public Vector getFieldLabels() {
        Vector list = new Vector();
        list.add("notification.execute.summary");
        list.add("notification.execute.username");
        return list;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldTypes()
     */    
    public Vector getFieldTypes() {
        Vector list = new Vector();
        list.add("1");
        list.add("1");
        return list;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldKeys()
     */    
    public Vector getFieldKeys() {
        Vector list = new Vector();
        list.add(LOG_SUMMARY);
        list.add(LOG_USERNAME);
        return list;
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "SQL Execution";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.execute.help";
    }

    
	protected boolean isQuery() {
		return false;
	}

}
