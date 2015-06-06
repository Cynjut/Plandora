package com.pandora.delegate;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.ResourceDateAllocTO;
import com.pandora.ResourceTO;
import com.pandora.ResourceTaskAllocTO;
import com.pandora.ResourceTaskTO;
import com.pandora.bus.ResourceTaskAllocBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for the Resource Task Alloc entity information access.
 */
public class ResourceTaskAllocDelegate extends GeneralDelegate {
    
    /** The Business object related with current delegate */        
    ResourceTaskAllocBUS bus = new ResourceTaskAllocBUS();

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ResourceTaskAllocBUS.getListByResourceTask(com.pandora.ResourceTaskTO)
     */
    public Vector<ResourceTaskAllocTO> getListByResourceTask(ResourceTaskTO rtto) throws BusinessException {
        return bus.getListByResourceTask(rtto);
    }
    
    public Vector<ResourceDateAllocTO> getResourceDateAllocList(ResourceTO rto, Timestamp inicialDate, Timestamp finalDate) throws BusinessException {
        return bus.getResourceDateAllocList(rto, inicialDate, finalDate);
    }

	public void updateResourceTask(ResourceTaskAllocTO rtato) throws BusinessException {
		bus.updateResourceTask(rtato);
	}

}
