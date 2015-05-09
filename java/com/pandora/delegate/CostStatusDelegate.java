package com.pandora.delegate;

import java.util.Vector;

import com.pandora.CostStatusTO;
import com.pandora.bus.CostStatusBUS;
import com.pandora.exception.BusinessException;

public class CostStatusDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */        
    CostStatusBUS bus = new CostStatusBUS(); 

    
    public Vector<CostStatusTO> getCostStatusList(Integer minimumState) throws BusinessException{
        return bus.getCostStatusList(minimumState);
    }


	public CostStatusTO getCostStatusByState(Integer state) throws BusinessException{
		return bus.getCostStatusByState(state);
	}

}
