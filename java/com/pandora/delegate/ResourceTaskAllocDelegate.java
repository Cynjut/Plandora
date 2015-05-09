package com.pandora.delegate;

import java.util.Vector;

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
}
