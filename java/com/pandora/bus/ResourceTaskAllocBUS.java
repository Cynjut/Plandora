package com.pandora.bus;

import java.util.Vector;

import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.dao.ResourceTaskAllocDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Resource Task Alloc entity.
 */
public class ResourceTaskAllocBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    ResourceTaskAllocDAO dao = new ResourceTaskAllocDAO();

    /**
     * Get a list of Resource Task Alloc objects based on Resource Task object
     */
    public Vector<ResourceTaskAllocTO> getListByResourceTask(ResourceTaskTO rtto) throws BusinessException {
        Vector<ResourceTaskAllocTO> list = null;
        try {
            list = dao.getListByResourceTask(rtto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return list;        
    }
    
    
}
