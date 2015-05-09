package com.pandora.delegate;

import java.util.Vector;

import com.pandora.RequirementTO;
import com.pandora.UserTO;
import com.pandora.bus.RequirementHistoryBUS;
import com.pandora.exception.BusinessException;

public class RequirementHistoryDelegate extends GeneralDelegate {

	private RequirementHistoryBUS bus = new RequirementHistoryBUS(); 
	
	
	public void insert(RequirementTO rto, UserTO handler, String comment) throws BusinessException{
		bus.insert(rto, handler, comment);
	}


	public Vector getIterationList(String requirementId) throws BusinessException{
		return bus.getIterationList(requirementId);
	}
}
