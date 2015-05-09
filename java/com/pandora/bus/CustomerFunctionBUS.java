package com.pandora.bus;

import com.pandora.CustomerFunctionTO;
import com.pandora.dao.CustomerFunctionDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class CustomerFunctionBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    CustomerFunctionDAO dao = new CustomerFunctionDAO();

    
	public CustomerFunctionTO getCustomerFunctionObject(CustomerFunctionTO filter) throws BusinessException {
		CustomerFunctionTO response = null;
        try {
            response = (CustomerFunctionTO) dao.getObject(filter);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}


	public void removeCustomerFunction(CustomerFunctionTO cfto) throws BusinessException {
        try {
            dao.remove(cfto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
	}


	public void insertCustomerFunction(CustomerFunctionTO cfto) throws BusinessException {
        try {
            dao.insert(cfto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
	}

}
