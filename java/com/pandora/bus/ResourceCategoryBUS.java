package com.pandora.bus;

import java.util.Vector;

import com.pandora.ResourceCapacityTO;
import com.pandora.dao.ResourceCapacityDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class ResourceCategoryBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    ResourceCapacityDAO dao = new ResourceCapacityDAO();

	public Vector<ResourceCapacityTO> getListByResourceProject(String resourceId, String projectId) throws BusinessException {
        Vector<ResourceCapacityTO> response = new Vector<ResourceCapacityTO>();
        try {
            response = dao.getListByResourceProject(resourceId, projectId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
	}

	
	public void insertOrUpdate(ResourceCapacityTO rcto) throws BusinessException {
        try {
        	ResourceCapacityTO resCap = dao.getObjectByResourceProject(rcto);
        	if (resCap==null) {
        		dao.insert(rcto);
        	} else {
        		dao.update(rcto);
        	}
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
	}

	
	public void remove(ResourceCapacityTO rcto) throws BusinessException {
        try {
       		dao.remove(rcto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
	}
	
}
