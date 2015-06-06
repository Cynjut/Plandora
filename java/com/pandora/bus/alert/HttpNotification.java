package com.pandora.bus.alert;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.NotificationFieldTO;
import com.pandora.RootTO;
import com.pandora.bus.EventBUS;
import com.pandora.helper.LogUtil;
import com.pandora.helper.PosterUtil;

/**
 */
public class HttpNotification extends Notification {

    private static final String HTTP_URL  = "HTTP_URL";
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#sendNotification(java.util.Vector, java.util.Vector)
     */    
    public boolean sendNotification(Vector<NotificationFieldTO> fields, Vector<Vector<Object>> sqlData) throws Exception {
        EventBUS bus = new EventBUS();
        PosterUtil poster = new PosterUtil();
        
        String url = this.getParamByKey(HTTP_URL, fields);
        
		Iterator<Vector<Object>> i = sqlData.iterator();
		while(i.hasNext()) {
		    Vector<Object> sqlDataItem = i.next();
		
		    url = super.replaceByToken(sqlDataItem, url);
	        poster.setURL(url);
	        poster.openGet();

	        //retrieve the http response, deserialize and populate the result object
	        String httpResponse = "";        
	        while (poster.readLine()) {
	            httpResponse+= poster.getLine();
	        }
	        
	        if (httpResponse!=null) {
	            bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, httpResponse, RootTO.ROOT_USER, null);
	        }		    
		}
                    
        return true;
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldLabels()
     */
    public Vector<String> getFieldLabels() {
        Vector<String> list = new Vector<String>();
        list.add("notification.http.url");
        return list;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldTypes()
     */    
    public Vector<String> getFieldTypes() {
        Vector<String> list = new Vector<String>();
        list.add("1");
        return list;
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getFieldKeys()
     */    
    public Vector<String> getFieldKeys() {
        Vector<String> list = new Vector<String>();
        list.add(HTTP_URL);
        return list;
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "Http post";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.http.help";
    }
    
}
