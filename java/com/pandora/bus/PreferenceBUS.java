package com.pandora.bus;

import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.dao.PreferenceDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Preference entity.
 */
public class PreferenceBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */    
    PreferenceDAO dao = new PreferenceDAO();
    
    
    public PreferenceTO getObjectByUser(UserTO uto) throws BusinessException{
        PreferenceTO response = null;
        try {
            response = dao.getObjectByUser(uto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * For each preference of hash list, verify if need to insert or update data.
     */
    public void insertOrUpdate(PreferenceTO pto) throws BusinessException{
        try {
            dao.insertOrUpdate(pto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }
    
}
