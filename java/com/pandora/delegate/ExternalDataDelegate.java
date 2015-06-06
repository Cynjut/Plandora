package com.pandora.delegate;

import com.pandora.ExternalDataTO;
import com.pandora.bus.ExternalDataBUS;
import com.pandora.exception.BusinessException;

public class ExternalDataDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private ExternalDataBUS bus = new ExternalDataBUS();


    public ExternalDataTO getExternalData(String externalId) throws BusinessException {
    	return bus.getExternalData(externalId);
    }

	
	public void createRequirement(ExternalDataTO edto, String projectId) throws BusinessException{
		bus.createRequirement(edto, projectId);
	}


	public void updateRequirement(ExternalDataTO edto, String projectId) throws BusinessException {
		bus.updateRequirement(edto, projectId);
	}
}
