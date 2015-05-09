package com.pandora.delegate;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.CostTO;
import com.pandora.ProjectTO;
import com.pandora.ResourceCapacityTO;
import com.pandora.ResourceTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.CostBUS;
import com.pandora.exception.BusinessException;

public class CostDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private CostBUS bus = new CostBUS();

    
	public Vector<TransferObject> getAccountCodesByLeader(UserTO uto) throws BusinessException {
		return bus.getAccountCodesByLeader(uto);
	}

	public Vector<CostTO> getListByProject(ProjectTO pto, boolean includeSubProjects, Timestamp inidate, Timestamp finalDate) throws BusinessException {
		return bus.getListByProject(pto, includeSubProjects, inidate, finalDate);
	}

	public CostTO getCost(CostTO costTO) throws BusinessException {
		return bus.getCost(costTO);
	}

	
	public Vector<CostTO> getListByAccountCode(String accountCode, Timestamp iniDate, Timestamp finalDate) throws BusinessException {
		return bus.getListByAccountCode(accountCode, iniDate, finalDate);
	}

	
	public Vector<CostTO> getListByCategory(CategoryTO catTO, Timestamp iniDate, Timestamp finalDate) throws BusinessException {
		return bus.getListByCategory(catTO, iniDate, finalDate);
	}

	
	public void insertCost(CostTO cto) throws BusinessException {
		bus.insertCost(cto);
	}

	public void updateCost(CostTO cto) throws BusinessException {
		bus.updateCost(cto);
	}

	public void removeCost(CostTO cto) throws BusinessException {
		bus.removeCost(cto);
	}
	
	
	public Vector<CostTO> getPendindCosts(UserTO uto) throws BusinessException  {
		return bus.getPendindCosts(uto);        
	}
	

	public void approveCost(CostTO cto, UserTO approver) throws BusinessException  {
		bus.approveCost(cto, approver);
	}

	public void refuseCost(CostTO cto, UserTO refuser) throws BusinessException  {
		bus.refuseCost(cto, refuser);
	}

	public Integer getCost(Vector<ResourceCapacityTO> rescapList, Timestamp bucketRef, ProjectTO pto, ResourceTO rto){
		return bus.getCost(rescapList, bucketRef, pto, rto);
	}
}
