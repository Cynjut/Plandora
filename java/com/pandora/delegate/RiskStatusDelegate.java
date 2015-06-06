package com.pandora.delegate;

import java.util.Vector;

import com.pandora.RiskStatusTO;
import com.pandora.bus.RiskStatusBUS;
import com.pandora.exception.BusinessException;

/**
 */
public class RiskStatusDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */        
    private RiskStatusBUS bus = new RiskStatusBUS(); 

    
    public Vector<RiskStatusTO> getRiskStatusList() throws BusinessException{
        return bus.getRiskStatusList();
    }


}
