package com.pandora.bus.auth;

import com.pandora.UserTO;
import com.pandora.exception.BusinessException;

public abstract class Authentication {

	public abstract String encrypt(String rawPassword) throws BusinessException;
	
	public abstract boolean authenticate(UserTO fullUTo, String formPassword, boolean isPassEncrypted) throws BusinessException;

	public abstract String getUniqueName();	
	
	
	@SuppressWarnings("rawtypes")
	public static Authentication getClass(String authClass){
        Authentication auth = null;
		try {
			Class busClass = Class.forName(authClass);
            auth = (Authentication)busClass.newInstance();					
		} catch (InstantiationException e) {
			e.printStackTrace();
			auth = null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			auth = null;					
		} catch (ClassNotFoundException e) {
			auth = null;
		}
		return auth;
	}
}
