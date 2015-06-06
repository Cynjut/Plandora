package com.pandora.delegate;

import java.util.Vector;

import com.pandora.ResourceCapacityTO;
import com.pandora.UserTO;
import com.pandora.bus.ResourceCategoryBUS;
import com.pandora.exception.BusinessException;

public class ResourceCapacityDelegate extends GeneralDelegate {

	ResourceCategoryBUS bus = new ResourceCategoryBUS();
	
    public Vector<ResourceCapacityTO> getListByResourceProject(String resourceId, String projectId, UserTO leader) throws BusinessException {
    	return bus.getListByResourceProject(resourceId, projectId, leader);
    }

    public void insertOrUpdate(ResourceCapacityTO rcto) throws BusinessException {
    	bus.insertOrUpdate(rcto);
    }

    public void remove(ResourceCapacityTO rcto) throws BusinessException {
    	bus.remove(rcto);
    }
    
}
