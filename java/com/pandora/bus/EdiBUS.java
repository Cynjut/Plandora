package com.pandora.bus;

import java.util.HashMap;

import com.pandora.EdiTO;
import com.pandora.UserTO;
import com.pandora.dao.EdiDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

public class EdiBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    EdiDAO dao = new EdiDAO();
    
    
	public HashMap<String, EdiTO> getUserEdiUUID(UserTO uto) throws BusinessException{
		HashMap<String, EdiTO> response = null;
        try {
            response = dao.getUserEdiUUID(uto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}


	public void createUUID(String ediType, UserTO uto, String uuid) throws BusinessException{
        try {
        	EdiTO rto = new EdiTO();
        	rto.setEdiId(ediType);
        	rto.setUserId(uto.getId());
        	rto.setUpdateDate(DateUtil.getNow());
        	rto.setEdiUUID(uuid);
            dao.insert(rto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
	}

	
	public EdiTO getEdiFromUUID(String uuid) throws BusinessException{
		EdiTO response = null;
        try {
            response = dao.getEdiFromUUID(uuid);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}
	
}
