package com.pandora.bus.auth;

import com.pandora.UserTO;
import com.pandora.bus.PasswordEncrypt;
import com.pandora.exception.BusinessException;

public class SystemAuthentication extends Authentication{

    public boolean authenticate(UserTO fullUTo, String formPassword, boolean isPassEncrypted) throws BusinessException{
        boolean response = true;
        
 
        String encPass = formPassword;
        if (!isPassEncrypted && !encPass.equals("")) {
            //encrypt the password
            encPass = PasswordEncrypt.getInstance().encrypt(formPassword);
        }
                
        //verify if password is correct...
        if (fullUTo!=null){
            if (!fullUTo.getPassword().equals(encPass)){
                response = false; 
            }
        }
        
        return response;
    }

    
	public String getUniqueName() {
		return "label.authMode.DB";
	}
	
	@Override
	public String encrypt(String rawPassword) throws BusinessException {
		return PasswordEncrypt.getInstance().encrypt(rawPassword);
	}
	
	
}
