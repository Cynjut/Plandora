package com.pandora.bus.artifact;

import com.pandora.ArtifactTO;
import com.pandora.bus.GeneralBusiness;
import com.pandora.dao.ArtifactDAO;
import com.pandora.exception.BusinessException;

public class ArtifactBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    ArtifactDAO dao = new ArtifactDAO();

    
	public void updateArtifact(String path, ArtifactTO ato) throws BusinessException {
        try {
        	dao.updateArtifact(path, ato);
        } catch (Exception e) {
        	throw new  BusinessException(e);
        }
	}


}
