package com.pandora.bus;

import java.util.Vector;

import com.pandora.CategoryTO;
import com.pandora.CustomerTO;
import com.pandora.ExternalDataTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.RequirementTO;
import com.pandora.RootTO;
import com.pandora.dao.ExternalDataDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class ExternalDataBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
	ExternalDataDAO dao = new ExternalDataDAO();

	
	public ExternalDataTO getExternalData(String externalId) throws BusinessException {
		ExternalDataTO response = null;
		try {
			response = dao.getExternalData(externalId);
		} catch (DataAccessException e) {
			throw new  BusinessException(e);
		}
		return response;
	}
	
	
	public void insert(ExternalDataTO edto) throws BusinessException {
		try {
			dao.insert(edto);
		} catch (DataAccessException e) {
			throw new  BusinessException(e);
		}
	}

	
	public void update(ExternalDataTO edto) throws BusinessException {
		try {
			dao.update(edto);
		} catch (DataAccessException e) {
			throw new  BusinessException(e);
		}
	}

	
	public void updateRequirement(ExternalDataTO edto, String projectId) throws BusinessException {
		RequirementBUS rbus = new RequirementBUS();
		UserBUS ubus = new UserBUS();
		
		try {
			if (edto.getPlanningId()!=null) {
				RequirementTO rto = rbus.getRequirement(new RequirementTO(edto.getPlanningId()));	
				
				rto.setCreationDate(edto.getMessageDate());			
				rto.setDescription(edto.getContent());
				rto.setPriority(edto.getPriority());
				rto.setSuggestedDate(edto.getDueDate());

				//update requester, ONLY if the external data requester match with one of those project customers
				ProjectTO pto = new ProjectTO(projectId);
				CustomerTO requester = null;
				Vector<CustomerTO> list = ubus.getCustomerByProject(pto, false);
				for (CustomerTO cto : list) {
					if (!cto.getUsername().equals(RootTO.ROOT_USER)) {
						if (cto.getUsername().equalsIgnoreCase(edto.getSource())) {
							requester = cto;
							break;
						}
					}
				}
				if (requester!=null) {
					rto.setRequester(requester);	
				}
				dao.updateRequirement(edto, rto);
			}
		} catch (DataAccessException e) {
			throw new  BusinessException(e);
		}

	}
	

	public void createRequirement(ExternalDataTO edto, String projectId) throws BusinessException {
		RequirementStatusBUS rsbus = new RequirementStatusBUS();
		UserBUS ubus = new UserBUS();
		
		try {
			ProjectTO pto = new ProjectTO(projectId);
			CustomerTO requester = null;
			Vector<CustomerTO> list = ubus.getCustomerByProject(pto, false);
			for (CustomerTO cto : list) {
				if (!cto.getUsername().equals(RootTO.ROOT_USER)) {
					requester = cto;
					if (cto.getUsername().equalsIgnoreCase(edto.getSource())) {
						break;
					}			
				}
			}

			RequirementStatusTO rsto = rsbus.getObjectByStateMachine(RequirementStatusTO.STATE_MACHINE_WAITING);
			
			RequirementTO rto = new RequirementTO();
			rto.setCategory(new CategoryTO(CategoryTO.DEFAULT_CATEGORY_ID));
			rto.setCreationDate(edto.getMessageDate());
			rto.setDeadlineDate(null);
			rto.setDescription(edto.getContent());
			rto.setFinalDate(null);
			rto.setEstimatedTime(null);
			rto.setIteration(null);
			rto.setPriority(edto.getPriority());
			rto.setProject(pto);
			rto.setSuggestedDate(edto.getDueDate());
			rto.setRequirementStatus(rsto);
			rto.setRequester(requester);
			
			dao.createRequirement(edto, rto);
			
		} catch (DataAccessException e) {
			throw new  BusinessException(e);
		}
	}
	
}
