package com.pandora.delegate;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.pandora.UserTO;
import com.pandora.bus.ConnectorBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for Connector object (Integration package).
 */
public class ConnectorDelegate extends GeneralDelegate {
    
    /** The Business object related with current delegate */
    private ConnectorBUS bus = new ConnectorBUS();

    
    /* (non-Javadoc)
     * @see com.pandora.bus.ConnectorBUS.process(javax.servlet.http.HttpServletRequest, java.util.ArrayList)
     */    
    public boolean process(HttpServletRequest request, ArrayList list) throws BusinessException{
        return bus.process(request, list);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.ConnectorBUS.generatePublicKey(javax.servlet.http.HttpServletRequest, com.pandora.UserTO)
     */        
    public String generatePublicKey(HttpServletRequest request, UserTO uto) throws BusinessException {
    	return bus.generatePublicKey(request, uto);
    }
}
