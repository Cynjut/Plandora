package com.pandora.bus.alert;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.NotificationFieldTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.bus.EventBUS;
import com.pandora.helper.LogUtil;

public class PingNotification extends Notification {

    private static final String PING_HOST    = "PING_HOST";
    private static final String PING_TYPE    = "PING_TYPE";
    private static final String PING_TIMEOUT = "PING_TM_OUT";
    

	@Override
	public boolean sendNotification(Vector<NotificationFieldTO> fields, Vector<Vector<Object>> sqlData) throws Exception {
        EventBUS bus = new EventBUS();
    	
    	String host = this.getParamByKey(PING_HOST, fields);
    	String tmoutStr = this.getParamByKey(PING_TIMEOUT, fields);
    	String type = this.getParamByKey(PING_TYPE, fields);
    	String content = "ping=[" + type+ "] ";
    	
    	int timeOut = 3000;
    	try {
    		timeOut = Integer.parseInt(tmoutStr);
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	content = content + "timeout=[" + timeOut + "] ";
    	
    	boolean doPing = false;
    	if (type!=null) {
    		if (type.equals("JAVA")) {
    			 doPing = doPing(host, timeOut);		
    		} else {
    			doPing = runSOPing(host, timeOut, type);
    		}
    	}
    	content = content + "host=[" + host + "] result=[" + doPing + "] ";
    	
        bus.insertEvent(LogUtil.LOG_INFO, LogUtil.SUMMARY_NOTIFIC, content, "root", null);    
        LogUtil.log(LogUtil.SUMMARY_NOTIFIC, this, RootTO.ROOT_USER, LogUtil.LOG_INFO, content);
        
		return doPing;
	}

    
	/**
	 * check if host is reachable
	 */
	private boolean doPing(String host, int timeOut) {
		try {
			return InetAddress.getByName(host).isReachable(timeOut);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean runSOPing(String ipstr, int timeoutMili, String pingType) {
		boolean retv = false;
		try {
			
			String cmd = null;
			if (pingType.equals("WIN")) {
				cmd = "ping -n 1 -w " + timeoutMili + " " + ipstr;
			} else if (pingType.equals("LIX")) {
				cmd = "ping -c1 -W" + (timeoutMili/1000) + " " + ipstr;
			}
			
			if (cmd!=null) {
				InputStream ins = Runtime.getRuntime().exec(cmd).getInputStream();
				Thread.sleep(timeoutMili);
				byte[] buff = new byte[ins.available()];
				ins.read(buff);
				String response = new StringTokenizer(new String(buff), "%").nextToken().trim();
				if (!response.endsWith("100")) {
					retv = true;
				}
			}
			
		} catch (Exception e) {
			retv = false;
		}
		return retv;
	}

	
	
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getUniqueName()
     */
    public String getUniqueName() {
        return "Ping Host";
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.Notification#getContextHelp()
     */
    public String getContextHelp() {
        return "notification.ping.help";
    }

    
	@Override
	public Vector<FieldValueTO> getFields() {
        Vector<FieldValueTO> response = new Vector<FieldValueTO>();
        
        Vector<TransferObject> typeList = new Vector<TransferObject>();
        typeList.add(new TransferObject("JAVA", "Java"));
        typeList.add(new TransferObject("WIN",  "Windows"));
        typeList.add(new TransferObject("LIX",  "Linux"));
        response.add(new FieldValueTO(PING_TYPE, "notification.ping.type", typeList));
        
        response.add(new FieldValueTO(PING_HOST, "notification.ping.host", FieldValueTO.FIELD_TYPE_TEXT, 100, 30));
        response.add(new FieldValueTO(PING_TIMEOUT, "notification.ping.timeout", FieldValueTO.FIELD_TYPE_TEXT, 30, 10));

		return response;
	}
		
}
