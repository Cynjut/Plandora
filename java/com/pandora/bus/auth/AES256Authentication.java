package com.pandora.bus.auth;

import sun.misc.BASE64Encoder;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import com.pandora.UserTO;
import com.pandora.exception.BusinessException;

public class AES256Authentication extends Authentication {

	@Override
	public boolean authenticate(UserTO fullUTo, String formPassword,
			boolean isPassEncrypted) throws BusinessException {
		
		boolean response = true;

		String encPass = formPassword;
        if (!isPassEncrypted && !encPass.equals("")) {
            //encrypt the password
            encPass = encrypt(formPassword);
        }
        if (fullUTo!=null){
            if (!fullUTo.getPassword().equals(encPass)){
                response = false; 
            }
        }
		return response;
	}

	@Override
	public String getUniqueName() {
		return "label.authMode.AES";
	}

	@Override
	public String encrypt(String message) throws BusinessException {
		try {

			KeyGenerator key = KeyGenerator.getInstance("AES");
			key.init(256);
			Key k = key.generateKey();
			Cipher c = Cipher.getInstance("AES");
			
			c.init(Cipher.ENCRYPT_MODE, k);
			byte[] encVal = c.doFinal(message.getBytes());
			String encryptedValue = new BASE64Encoder().encode(encVal);
			return encryptedValue;
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}
}