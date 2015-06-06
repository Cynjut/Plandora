package com.pandora.delegate;

import java.util.HashMap;

import com.pandora.EdiTO;
import com.pandora.UserTO;
import com.pandora.bus.EdiBUS;
import com.pandora.exception.BusinessException;

public class EdiDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private EdiBUS bus = new EdiBUS();

	public HashMap<String, EdiTO> getUserEdiUUID(UserTO uto) throws BusinessException{
		return bus.getUserEdiUUID(uto);
	}

	public void createUUID(String ediType, UserTO uto, String uuid) throws BusinessException{
		bus.createUUID(ediType, uto, uuid);
	}

	public EdiTO getEdiFromUUID(String uuid) throws BusinessException{
		return bus.getEdiFromUUID(uuid);
	}

}
