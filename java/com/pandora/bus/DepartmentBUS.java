package com.pandora.bus;

import java.util.Vector;

import com.pandora.DepartmentTO;
import com.pandora.dao.DepartmentDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Department entity.
 */
public class DepartmentBUS extends GeneralBusiness {
    
    /** The Data Access Object related with current business entity */
    DepartmentDAO dao = new DepartmentDAO();
    
    
    /**
     * Get a list of all Department TOs from data base.
     */
    public Vector<DepartmentTO> getDepartmentList() throws BusinessException{
        Vector<DepartmentTO> response = new Vector<DepartmentTO>();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
}
