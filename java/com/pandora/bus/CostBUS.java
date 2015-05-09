package com.pandora.bus;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.CostTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceCapacityTO;
import com.pandora.ResourceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.dao.CostDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class CostBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    CostDAO dao = new CostDAO();

	public Vector<TransferObject> getAccountCodesByLeader(UserTO uto) throws BusinessException {
		Vector<TransferObject> response = null;
        try {
            response = dao.getAccountCodesByLeader(uto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
	public Vector<CostTO> getListByProject(ProjectTO pto, boolean includeSubProjects, Timestamp inidate, Timestamp finalDate) throws BusinessException {
		Vector<CostTO> response = new Vector<CostTO>();
		ProjectBUS pbus = new ProjectBUS();
        try {
        	
            //get the costs of child projects 
        	if (includeSubProjects) {
                Vector<ProjectTO> childs = pbus.getProjectListByParent(pto, true);
                Iterator<ProjectTO> i = childs.iterator();
                while(i.hasNext()){
                    ProjectTO childProj = i.next();
                    Vector<CostTO> cstOfChild = this.getListByProject(childProj, true, inidate, finalDate);
                    response.addAll(cstOfChild);
                }        		
        	}
                    
            response.addAll(dao.getListByProject(pto, inidate, finalDate));

        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}


	public Vector<CostTO> getListByAccountCode(String accountCode, Timestamp iniDate, Timestamp finalDate) throws BusinessException {
		Vector<CostTO> response = new Vector<CostTO>();
        try {
            response.addAll(dao.getListByAccountCode(accountCode, iniDate, finalDate));
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
	public Vector<CostTO> getListByCategory(CategoryTO catTO, Timestamp iniDate, Timestamp finalDate) throws BusinessException {
		Vector<CostTO> response = null;
        try {
            response = dao.getListByCategory(catTO, iniDate, finalDate) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}	

	
	public void insertCost(CostTO cto) throws BusinessException {
        try {
            dao.insert(cto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }		
	}


	public void updateCost(CostTO cto) throws BusinessException {
        try {
            dao.update(cto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }		
	}


	public void removeCost(CostTO cto) throws BusinessException {
        try {
            dao.remove(cto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }		
	}


	public CostTO getCost(CostTO cto) throws BusinessException {
		CostTO response = null;
        try {
        	response = (CostTO) dao.getObject(cto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
	public Vector<CostTO> getPendindCosts(UserTO uto) throws BusinessException {
        Vector<CostTO> response = new Vector<CostTO>();
        try {
            response = dao.getPendindCosts(uto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}	
	
	
	public void approveCost(CostTO cto, UserTO approver) throws BusinessException {
        try {
            dao.approveCost(cto, approver);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
	}


	public void refuseCost(CostTO cto, UserTO refuser) throws BusinessException {
        try {
            dao.refuseCost(cto, refuser);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
	}


	public Integer getCost(Vector<ResourceCapacityTO> rescapList, Timestamp bucketRef, ProjectTO pto, ResourceTO rto){
		Integer cost = null;
		for (ResourceCapacityTO rcto : rescapList) {
			if (rcto.getProjectId().equals(pto.getId()) && rcto.getResourceId().equals(rto.getId())) {
				if (rcto.getDate().after(bucketRef)) {
					break;
				} else {
					cost = rcto.getCostPerHour();
				}
			}
		}
		return cost;
	}



}
