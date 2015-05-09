package com.pandora.bus.auth;

import com.pandora.UserTO;
import com.pandora.exception.BusinessException;

public class DummyAuthentication extends Authentication {

	public boolean authenticate(UserTO fullUTo, String formPassword,
			boolean isPassEncrypted) throws BusinessException {
		
		boolean response = false;
		String u = fullUTo.getUsername();
		if (u.equals("franz") || u.equals("noam") || u.equals("oscar") || u.equals("camus")) {
			if (formPassword.equals("123")) {
				response = true;
			}
		}
		return response;
	}

	public String getUniqueName() {
		return "DUMMY";
	}

}
