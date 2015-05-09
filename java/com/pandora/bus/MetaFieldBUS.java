package com.pandora.bus;

import java.util.Vector;

import com.pandora.MetaFieldTO;
import com.pandora.dao.MetaFieldDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with MetaField entity.
 */
public class MetaFieldBUS extends GeneralBusiness {

    
    /** The Data Access Object related with current business entity */
    private MetaFieldDAO dao = new MetaFieldDAO();
    
    
    /**
     * Get a list of all MetaField TOs from data base.
     */
    public Vector<MetaFieldTO> getMetaFieldList() throws BusinessException {
        Vector<MetaFieldTO> response = new Vector();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Get a list of all MetaField TOs from data base related 
     * with a specific project, category and container (Requirement, task forms, etc).
     */
    public Vector<MetaFieldTO> getListByProjectAndContainer(String projectId, String categoryId, Integer applyTo) throws BusinessException {
        Vector<MetaFieldTO> response = new Vector<MetaFieldTO>();
        try {
            response = dao.getListByProjectAndContainer(projectId, categoryId, applyTo);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }    

    
    /**
     * Get a specific Meta Field object from data base
     */
    public MetaFieldTO getMetaFieldObject(MetaFieldTO current) throws BusinessException {
        MetaFieldTO response = new MetaFieldTO();
        try {        
            response = (MetaFieldTO) dao.getObject(current);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Insert a new Meta Field object into data base
     */
    public void insertMetaField(MetaFieldTO mfto) throws BusinessException {
        try {        
            dao.insert(mfto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
    }

    
    /**
     * Update data of Meta Field into data base
     */
    public void updateMetaField(MetaFieldTO mfto) throws BusinessException {
        try {        
            dao.update(mfto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }

    
    /**
     * Remove data of Meta Field from data base
     */
    public void removeMetaField(MetaFieldTO mfto) throws BusinessException {
        try {        
            dao.remove(mfto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
    }


    /**
     * Get meta fields that is related with the same meta form
     */
    public Vector<MetaFieldTO> getFieldByMetaForm(String metaFormId) throws BusinessException {
        Vector<MetaFieldTO> response;
        try {
            response = dao.getFieldByMetaForm(metaFormId) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }
}
