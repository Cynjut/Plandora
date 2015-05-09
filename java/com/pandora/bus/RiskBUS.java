package com.pandora.bus;

import java.util.Iterator;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.RiskHistoryTO;
import com.pandora.RiskStatusTO;
import com.pandora.RiskTO;
import com.pandora.dao.RiskDAO;
import com.pandora.dao.RiskHistoryDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 */
public class RiskBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    RiskDAO dao = new RiskDAO();
    
    
    public Vector<RiskTO> getRiskList(String projectId) throws BusinessException {
        Vector<RiskTO> response = new Vector<RiskTO>();
        ProjectBUS pbus = new ProjectBUS();
        try {
        	
            //get the risks of child projects 
            Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(projectId), true);
            Iterator<ProjectTO> i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = i.next();
                Vector<RiskTO> rskOfChild = this.getRiskList(childProj.getId());
                response.addAll(rskOfChild);
            }
                    
            response.addAll(dao.getListByProjectId(projectId));
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;

    }

    
    public RiskTO getRisk(RiskTO rto) throws BusinessException {
        RiskTO response;
        try {
            response = (RiskTO) dao.getObject(rto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

    
    public void insertRisk(RiskTO rto) throws BusinessException {
        try {        
            dao.insert(rto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }


    public void updateRisk(RiskTO rto) throws BusinessException {
        try {        
            dao.update(rto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
    }


    public void removeRisk(RiskTO rto) throws BusinessException {
        try {        
            dao.remove(rto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
    }


    public Vector<RiskHistoryTO> getHistory(String riskId) throws BusinessException{
        Vector<RiskHistoryTO> response = new Vector<RiskHistoryTO>();
        try {
            RiskHistoryDAO dao = new RiskHistoryDAO();
            response = dao.getListByRisk(riskId);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    public Vector getListUntilID(String initialId, String finalId) throws BusinessException{
        Vector response = new Vector();
        try {
            response = dao.getListUntilID(initialId, finalId);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }


    public long getMaxID() throws BusinessException {
        long response = -1;
        try {
            response = dao.getMaxId("risk");
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
        return response;
    }
    
        
    public boolean isMaterializedRisk(RiskTO rto) throws BusinessException {
    	RiskStatusBUS rsbus = new RiskStatusBUS();
    	boolean response = false;
    	if (rto.getStatus()!=null) {
    		RiskStatusTO rsto = rto.getStatus();
    		if (rsto!=null) {
    			rsto = rsbus.getRiskStatus(rsto);
    			String type = rsto.getStatusType();
    			if (type!=null) {
    	    		if (type!=null && type.equals(RiskStatusTO.MATERIALIZE_RISK_TYPE)) {
    	    			response = true;
    	    		}    				
    			}	
    		}
    	}
    	return response;
    }
    
}
