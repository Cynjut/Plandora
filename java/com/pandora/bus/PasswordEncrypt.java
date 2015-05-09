package com.pandora.bus;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.pandora.helper.Base64Coder;

import com.pandora.exception.BusinessException;


/**
 * This singleton return a incrypted content based on a plain string <br>
 * This class was modifyed based on Marcio's Andreeta class version.  
 */
public final class PasswordEncrypt {

    /** static instance of singleton */
	private static PasswordEncrypt instance;

	private PasswordEncrypt(){}

	/**
	 * Convert a password into encrypted string
	 * @param rawPassword
	 * @return
	 * @throws BusinessException
	 */
	public synchronized String encrypt(String rawPassword) throws BusinessException {
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			throw new BusinessException(e.getMessage());
		}
		
		try {
			md.update(rawPassword.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new BusinessException(e.getMessage());
		}

		byte[] raw = md.digest();
		String hash = Base64Coder.encodeString(new String(raw));
		
		return hash;
	}

	
	public static synchronized PasswordEncrypt getInstance(){
		if (instance == null) {
			instance = new PasswordEncrypt();
		}
		return instance;
	}

}