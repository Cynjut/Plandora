package com.pandora.delegate;

import java.util.Vector;

import com.pandora.bus.kb.KbBUS;
import com.pandora.bus.kb.KbIndex;
import com.pandora.exception.BusinessException;

/**
 */
public class KbDelegate {

    /** The Business object related with current delegate */    
    KbBUS bus = new KbBUS();

    public Vector search(String subject) throws BusinessException {
        return bus.search(subject);
    }
    
    public String getProjectSearchSintax(String projectId) throws BusinessException{
        return bus.getProjectSearchSintax(projectId);
    }
    
    public KbIndex getKbByUniqueName(String uniqueName) throws Exception{
        return bus.getKbByUniqueName(uniqueName);
    }

}
