package com.pandora.bus.auth;

import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.SessionUtil;

public class LdapAuthentication extends Authentication {

	public boolean authenticate(UserTO fullUTo, String formPassword, boolean isPassEncrypted) throws BusinessException {
        boolean response = true;
        UserDelegate udel = new UserDelegate();
        UserTO root = udel.getRoot(); 
        
		String host = root.getPreference().getPreference(PreferenceTO.LDAP_HOST);
		int port = Integer.parseInt(root.getPreference().getPreference(PreferenceTO.LDAP_PORT)); 
		String uid = root.getPreference().getPreference(PreferenceTO.LDAP_UID_REGISTER);
		
		boolean isAuth = SessionUtil.checkLdapLogin(host, port, uid, fullUTo.getUsername(), formPassword);
		if (!isAuth) {
		    response = false;		    
			throw new BusinessException("The user was not authenticated by LDAP server.");	
		}        
		return response;
	}

	
	public String getUniqueName() {
		return "label.authMode.LDAP";
	}

}
