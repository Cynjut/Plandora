package com.pandora.bus;

import java.util.Vector;

import com.pandora.RiskStatusTO;
import com.pandora.dao.RiskStatusDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 */
public class RiskStatusBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    RiskStatusDAO dao = new RiskStatusDAO();
    

    public Vector getRiskStatusList() throws BusinessException{
        Vector response = new Vector();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    public RiskStatusTO getRiskStatus(RiskStatusTO rsto) throws BusinessException{
    	RiskStatusTO response = null;
        try {
            response = (RiskStatusTO)dao.getObject(rsto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
}
