package com.pandora.bus.alert;

import java.util.Vector;

import com.pandora.NotificationFieldTO;
import com.pandora.bus.EventBUS;
import com.pandora.helper.LogUtil;

public class ExecuteNotification extends Notification {

	private static final String LOG_SUMMARY  = "LOG_SUMMARY";
	private static final String LOG_USERNAME = "LOG_USERNAME";
	
	/* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */    
	public boolean sendNotification(Vector<NotificationFieldTO> fields, Vector<Vector<Object>> sqlData) throws Exception {
        EventBUS bus = new EventBUS();
        String message = "no rows affected";
        
        String summary = this.getParamByKey(LOG_SUMMARY, fields);
        String username = this.getParamByKey(LOG_USERNAME, fields);
        
        if (sqlData!=null) {
            Object element = sqlData.elementAt(0);
            Integer rows = (Integer)element;
            if (rows!=null && rows.intValue()>0) {
            	message = rows + " row(s) affected";
            }
        }

        bus.insertEvent(LogUtil.LOG_INFO, summary, message, username, null);
        
        return true;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldLabels()
     */
    public Vector<String> getFieldLabels() {
        Vector<String> list = new Vector<String>();
        list.add("notification.execute.summary");
        list.add("notification.execute.username");
        return list;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldTypes()
     */    
    public Vector<String> getFieldTypes() {
        Vector<String> list = new Vector<String>();
        list.add("1");
        list.add("1");
        return list;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldKeys()
     */    
    public Vector<String> getFieldKeys() {
        Vector<String> list = new Vector<String>();
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
