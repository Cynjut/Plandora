package com.pandora.bus;

import java.util.Vector;

import com.pandora.FunctionTO;
import com.pandora.dao.FunctionDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Function entity.
 */
public class FunctionBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    FunctionDAO dao = new FunctionDAO();
    
    
    /**
     * Get a list of all Function TOs from data base.
     */
    public Vector<FunctionTO> getFunctionList() throws BusinessException{
        Vector<FunctionTO> response = new Vector<FunctionTO>();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
}
