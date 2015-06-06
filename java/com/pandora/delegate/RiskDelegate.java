package com.pandora.delegate;

import java.util.Vector;

import com.pandora.RiskHistoryTO;
import com.pandora.RiskTO;
import com.pandora.UserTO;
import com.pandora.bus.RiskBUS;
import com.pandora.exception.BusinessException;

/**
 */
public class RiskDelegate extends GeneralDelegate{

    /** The Business object related with current delegate */
    private RiskBUS bus = new RiskBUS();
    
    
    public Vector<RiskTO> getRiskList(String projectId) throws BusinessException {
        return this.getRiskList(projectId, null);
    }
    
    public Vector<RiskTO> getRiskList(String projectId, String userId) throws BusinessException {
        return bus.getRiskList(projectId, userId);
    }

    
    public RiskTO getRisk(RiskTO rto) throws BusinessException {
        return bus.getRisk(rto);
    }

    
    public void insertRisk(RiskTO rto) throws BusinessException  {
        bus.insertRisk(rto);        
    }


    public void updateRisk(RiskTO rto) throws BusinessException  {
        bus.updateRisk(rto);
    }


    public void removeRisk(RiskTO rto) throws BusinessException  {
        bus.removeRisk(rto);        
    }


    public Vector<RiskHistoryTO> getHistory(String riskId)  throws BusinessException  {
        return bus.getHistory(riskId);
    }
 
    
    public boolean isMaterializedRisk(RiskTO rto) throws BusinessException {
        return bus.isMaterializedRisk(rto);
    }
    
    public String getRiskLifecycle(Vector<RiskHistoryTO> items, UserTO reader) throws BusinessException {
    	return bus.getRiskLifecycle(items, reader);
    }
}
