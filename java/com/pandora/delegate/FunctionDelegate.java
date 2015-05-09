package com.pandora.delegate;

import java.util.Vector;

import com.pandora.FunctionTO;
import com.pandora.bus.FunctionBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for Function entity information access.
 */
public class FunctionDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
    FunctionBUS bus = new FunctionBUS();
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.FunctionBUS.getFunctionList()
     */    
    public Vector<FunctionTO> getFunctionList() throws BusinessException{
        return bus.getFunctionList();
    }
    
}
