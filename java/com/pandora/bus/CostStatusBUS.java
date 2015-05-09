package com.pandora.bus;

import java.util.Vector;

import com.pandora.CostStatusTO;
import com.pandora.dao.CostStatusDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class CostStatusBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    CostStatusDAO dao = new CostStatusDAO();

    
	public Vector<CostStatusTO> getCostStatusList(Integer minimumState) throws BusinessException {
        Vector<CostStatusTO> response = new Vector<CostStatusTO>();
        try {
            response = dao.getCostStatusList(minimumState);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}


	public CostStatusTO getCostStatusByState(Integer state) throws BusinessException {
		CostStatusTO response = null;
        try {
            response = dao.getCostStatusByState(state);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

}
