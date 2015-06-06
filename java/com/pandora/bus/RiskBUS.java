package com.pandora.bus;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import com.pandora.CustomerTO;
import com.pandora.ProjectTO;
import com.pandora.RiskHistoryTO;
import com.pandora.RiskStatusTO;
import com.pandora.RiskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.UserTO;
import com.pandora.dao.RiskDAO;
import com.pandora.dao.RiskHistoryDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;

/**
 */
public class RiskBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    RiskDAO dao = new RiskDAO();
    
    public Vector<RiskTO> getRiskList(String projectId) throws BusinessException {
    	return this.getRiskList(projectId, null);
    }
    
    public Vector<RiskTO> getRiskList(String projectId, String userId) throws BusinessException {
        Vector<RiskTO> response = new Vector<RiskTO>();
        ProjectBUS pbus = new ProjectBUS();
        try {
        	
            //get the risks of child projects 
            Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(projectId), true);
            Iterator<ProjectTO> i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = i.next();
                Vector<RiskTO> rskOfChild = this.getRiskList(childProj.getId(), userId);
                response.addAll(rskOfChild);
            }
                    
            response.addAll(dao.getListByProjectId(projectId, userId));
            
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
    
    public Vector<RiskTO> getListUntilID(String initialId, String finalId) throws BusinessException{
    	Vector<RiskTO> response = new Vector<RiskTO>();
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
 
    public String getRiskLifecycle(Vector<RiskHistoryTO> items, UserTO reader) throws BusinessException {
        StringBuffer buff = new StringBuffer();

        Locale loc = reader.getLocale();
        String label = " " + reader.getBundle().getMessage(loc, "label.requestHistory.wrote");

        for (RiskHistoryTO rhto : items) {
            if (rhto.getContent()!=null && rhto.getContent().trim().length()>0 ){

                buff.append("\n------" + rhto.getUser().getUsername() + 
                        label + DateUtil.getDateTime(rhto.getCreationDate(), loc, 2, 2) + "------");
                buff.append("\n" + rhto.getContent() + "\n");                    
            }
        }

        return buff.toString();    	
    }    
}
