package com.pandora.delegate;

import java.util.Vector;

import com.pandora.AreaTO;
import com.pandora.bus.AreaBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for Area entity information access.
 */
public class AreaDelegate extends GeneralDelegate{

    /** The Business object related with current delegate */
    private AreaBUS bus = new AreaBUS();
    
    /**
     * Get a list of all Area TOs from data base.
     */
    public Vector<AreaTO> getAreaList() throws BusinessException{
        return bus.getAreaList();
    }
    
    
    public AreaTO getAreaObject(AreaTO ato) {
    	return null;//bus.getAreaObject(ato);
    }
    
    /**
     * Update information of current area into data base.
     * @param uto
     * @throws BusinessException
     */
    public void updateArea(AreaTO uto) throws BusinessException{
        bus.updateArea(uto);
    }

    /**
     * Check if areaname already exists.
     * @param uto
     * @throws BusinessException
     */
    public void checkAreaName(AreaTO uto) throws BusinessException{
        bus.checkAreaName(uto);        
    }
    
    /**
     * Insert a new area into data base.
     * @param uto
     * @throws BusinessException
     */
    public void insertArea(AreaTO uto) throws BusinessException{
        bus.insertArea(uto);
    }
    
    /**
     * Get a specific Area object from data base.
     * @param filter
     * @return
     * @throws BusinessException
     */
    public AreaTO getArea(AreaTO filter) throws BusinessException{
        return bus.getArea(filter);
    }

    /**
     * Remove an area of data base
     * @param uto
     * @throws BusinessException
     */
    public void removeArea(AreaTO uto) throws BusinessException{
        bus.removeArea(uto);
    }
}
