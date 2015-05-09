package com.pandora.bus;

import java.util.Vector;

import com.pandora.InvoiceStatusTO;
import com.pandora.dao.InvoiceStatusDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class InvoiceStatusBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    InvoiceStatusDAO dao = new InvoiceStatusDAO();
    
    
    /**
     * Get all Invoice Status objects from data base.
     */
	public Vector getList() throws BusinessException {
        Vector response = new Vector();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}


	public InvoiceStatusTO getInitialStatus() throws BusinessException {
		InvoiceStatusTO response = null;
		try {
			response = dao.getInitialStatus();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

}
