package com.pandora.delegate;

import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.bus.PreferenceBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for the Preference entity.
 */
public class PreferenceDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
    PreferenceBUS bus = new PreferenceBUS();
    

    /* (non-Javadoc)
     * @see com.pandora.bus.PreferenceBUS.getObjectByUser(com.pandora.UserTO)
     */
    public PreferenceTO getObjectByUser(UserTO uto) throws BusinessException{
        return bus.getObjectByUser(uto);
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.PreferenceBUS.insertOrUpdate(com.pandora.PreferenceTO)
     */
    public void insertOrUpdate(PreferenceTO pto) throws BusinessException{
        bus.insertOrUpdate(pto);
    }

}
