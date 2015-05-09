package com.pandora.bus.alert;

import java.util.Vector;

import com.pandora.bus.EventBUS;
import com.pandora.helper.LogUtil;

/**
 * This class contain the business rule to save a message into the data base.
 * It can be usefull for audit purposes.
 * 
 * In sendNotification the fields vector must contain the following values:
 * <li>summary: used to categore the log message into event_log table</li>
 * <li>username: the user name that created the notification. It will be related to the log record into data base.</li>
 */
public class DbLogNotification extends Notification {
    
    private static final String LOG_SUMMARY  = "LOG_SUMMARY";
    private static final String LOG_USERNAME = "LOG_USERNAME";

    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */    
    public boolean sendNotification(Vector fields, Vector sqlData) throws Exception {
        EventBUS bus = new EventBUS();
        
        String summary = this.getParamByKey(LOG_SUMMARY, fields);
        String username = this.getParamByKey(LOG_USERNAME, fields);
        
        String content = super.getContent(sqlData, true, true);
        bus.insertEvent(LogUtil.LOG_INFO, summary, content, username, null);    
        
        return true;
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldLabels()
     */
    public Vector getFieldLabels() {
        Vector list = new Vector();
        list.add("notification.dblog.summary");
        list.add("notification.dblog.username");
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
        return "DB Log";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.dblog.help";
    }
    
}
