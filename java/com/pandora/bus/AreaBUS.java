package com.pandora.bus;

import java.util.Vector;

import com.pandora.AreaTO;
import com.pandora.dao.AreaDAO;
import com.pandora.exception.AreaNameAlreadyExistsException;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Area entity.
 */
public class AreaBUS extends GeneralBusiness {
    
    /** The Data Acess Object related with current business entity */
    AreaDAO dao = new AreaDAO();
    
    /**
     * Get a list of all Area TOs from data base.
     */
    public Vector<AreaTO> getAreaList() throws BusinessException{
        Vector<AreaTO> response = new Vector<AreaTO>();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    /**
     * Get a specific Area object from data base.
     * @param filter
     * @return
     * @throws BusinessException
     */
    public AreaTO getArea(AreaTO filter) throws BusinessException{
    	AreaTO response = null;
        try {
            response = (AreaTO)dao.getObject(filter);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    /**
     * Update information of current area into data base.
     * @param uto
     * @throws BusinessException
     */
    public void updateArea(AreaTO uto) throws BusinessException{
        try {
            dao.update(uto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }
    
    /**
     * Check if username already exists into the data base and if is "in use".
     * @param uto
     * @throws BusinessException
     */
    
    public void checkAreaName(AreaTO uto) throws BusinessException{
    	AreaTO response = null;
        try {
            response = dao.getObjectByName(uto);
            if (response!=null){
                if (!response.getId().equals(uto.getId())){
                    throw new AreaNameAlreadyExistsException();
                }
            }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }         
    }
    
    /**
     * Insert a new user into data base.
     * @param uto
     * @throws BusinessException
     */
    public void insertArea(AreaTO uto) throws BusinessException{
        try {
            dao.insert(uto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }         
    }
    
    /**
     * Remove an user of data base
     * @param uto
     * @throws BusinessException
     */
    public void removeArea(AreaTO uto) throws BusinessException{
        try {
            dao.remove(uto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }         
    }
}
