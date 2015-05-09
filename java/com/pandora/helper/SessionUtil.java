package com.pandora.helper;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.pandora.EventTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;

/**
 * This class contain util methods to handler http session.
 */
public final class SessionUtil {

	private static HashMap<String,EventTO> lastEventList;
	
	private static Vector<EventTO> events;
	
	
	/**
	 * The constructor was set to private to avoid instancing creation  
	 */
	private SessionUtil(){}
	
	/**
	 * Get current UserTO from http session (connected on system)
	 */
	public static UserTO getCurrentUser(HttpServletRequest request){
	    return (UserTO)request.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
	}

	/**
	 * Get current locale based on current user connected
	 */
	public static Locale getCurrentLocale(HttpServletRequest request){
	    return (request.getLocale());
	}

	
	public static void addEvent(UserTO uto, String event){
		if (uto!=null && uto.getId()!=null) {
			if (lastEventList==null) {
				lastEventList = new HashMap<String,EventTO>();
				events = new Vector<EventTO>();
			}
			try {
				lastEventList.remove(uto.getId());
			} catch (Exception e) {
				//it is not necessary...
			}

			//keep event max log = 200 records...
			if (events.size()==200) {
				events.remove(0);
			}
			
			EventTO eto = new EventTO();
			eto.setCreationDate(DateUtil.getNow());
			eto.setDescription(event);
			eto.setUsername(uto.getUsername());
			eto.setSummary(LogUtil.SUMMARY_USER_ACTION);
			
			lastEventList.put(uto.getId(), eto);
			events.addElement(eto);
		}
	}

	public static HashMap<String,EventTO> getLastEvents(){
		return lastEventList;
	}

	public static Vector<EventTO> getEvents(){
		return events;
	}
	
	
	/**
	 * Check if a user exists into LDAP server, and authenticate this user. <br>
	 * Before process, replace the <USER> tag of register string with username field value.
	 */
	public static boolean checkLdapLogin(String ldapHost, int ldapPort, String register, String username, String password) throws BusinessException {
		boolean response = false;
		LDAPConnection conn = null;

		String reg = register.replaceAll("<USER>", username);
		conn = new LDAPConnection();
		
		try {
			conn.connect( ldapHost, ldapPort);
			try {
				conn.bind(LDAPConnection.LDAP_V3, reg, password.getBytes("UTF8") );
			} catch (UnsupportedEncodingException e){
				throw new LDAPException("Invalid UTF8 Encoding", LDAPException.LOCAL_ERROR, null, e);
			}

			response = conn.isBound();
			
		} catch (LDAPException e) {
			throw new BusinessException(e);
			
		} finally {
			try {
				conn.disconnect();
			} catch (LDAPException e) {
				System.err.println("Error closing LDAP connection: " + e.toString());
			}
		}
		return response;

	}
	
	
	public static String getUri(HttpServletRequest request){
	    String hst = request.getServerName();
	    String prt = request.getServerPort()+"";
	    if (!prt.equals("0")) {
	        prt = ":" + prt; 
	    } else {
	        prt = "";
	    }
	    return hst + prt;
	}
	
	
    public static String getUID(String sessionId) {
        String strRetVal = "";
        String strTemp = "";
        try {
        	
            //Get IdentityHash() segment
            strTemp = Long.toHexString(System.identityHashCode(sessionId));
            while (strTemp.length() < 8) {
                strTemp = '0' + strTemp;
            }
            strRetVal = strTemp + ":";

            //Get CurrentTimeMillis() segment
            strTemp = Long.toHexString(System.currentTimeMillis());
            while (strTemp.length() < 12) {
                strTemp = '0' + strTemp;
            }
            strRetVal += strTemp + ':';

            //Get Random Segment
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            strTemp = Integer.toHexString(prng.nextInt());
            while (strTemp.length() < 8) {
                strTemp = '0' + strTemp;
            }
            strRetVal += strTemp.substring(4);

	    } catch (java.security.NoSuchAlgorithmException ex) {
	    	ex.printStackTrace();
	    }

        return strRetVal.toUpperCase();
	}
	
}
