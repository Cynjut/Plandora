package com.pandora.bus;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

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
		byte[] raw = DigestUtils.sha(rawPassword.getBytes());
		byte[] encoded = Base64.encodeBase64(raw);
		return  new String(encoded);
	}

	
	public static synchronized PasswordEncrypt getInstance(){
		if (instance == null) {
			instance = new PasswordEncrypt();
		}
		return instance;
	}

}