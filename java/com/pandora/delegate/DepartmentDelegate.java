package com.pandora.delegate;

import java.util.Vector;

import com.pandora.DepartmentTO;
import com.pandora.bus.DepartmentBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for Department entity information access.
 */
public class DepartmentDelegate extends GeneralDelegate{

    /** The Business object related with current delegate */
    private DepartmentBUS bus = new DepartmentBUS();

    
    /* (non-Javadoc)
     * @see com.pandora.bus.DepartmentBUS.getDepartmentList()
     */    
    public Vector<DepartmentTO> getDepartmentList() throws BusinessException{
        return bus.getDepartmentList();
    }
}
