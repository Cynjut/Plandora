package com.pandora.bus;

import java.util.Vector;

import com.pandora.CompanyTO;
import com.pandora.dao.CompanyDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class CompanyBUS extends GeneralBusiness {

	
    /** The Data Access Object related with current business entity */
    CompanyDAO dao = new CompanyDAO();

    
	public Vector<CompanyTO> getCompanyList() throws BusinessException {
        Vector<CompanyTO> response = new Vector<CompanyTO>();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}


	public CompanyTO getCompany(CompanyTO filter) throws BusinessException {
		CompanyTO response = null;
        try {
            response = (CompanyTO) dao.getObject(filter);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}
	
	
    public void insertCompany(CompanyTO cto) throws BusinessException {
        try {
            dao.insert(cto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }


    public void updateCompany(CompanyTO cto) throws BusinessException {
        try {
            dao.update(cto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }


    public void removeCompany(CompanyTO cto) throws BusinessException {
        try {
            dao.remove(cto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }

}
