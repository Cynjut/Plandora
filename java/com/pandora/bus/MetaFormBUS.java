package com.pandora.bus;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.MetaFormTO;
import com.pandora.CustomFormTO;
import com.pandora.dao.CustomFormDAO;
import com.pandora.dao.MetaFormDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 */
public class MetaFormBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    MetaFormDAO dao = new MetaFormDAO();
    CustomFormDAO cdao = new CustomFormDAO();
    
    
    public Vector<MetaFormTO> getMetaFormList() throws BusinessException {
    	Vector<MetaFormTO> response;
        try {
            response = dao.getList() ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }


    public MetaFormTO getObject(MetaFormTO mto) throws BusinessException {
        MetaFormTO response = null;
        try {
            response = (MetaFormTO) dao.getObject(mto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

    public void insertMetaForm(MetaFormTO to) throws BusinessException {
        try {        
            dao.insert(to);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }


    public void updateMetaForm(MetaFormTO to) throws BusinessException {
        try {        
            dao.update(to);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
    }


    public void removeMetaForm(MetaFormTO to) throws BusinessException {
        try {        
            dao.remove(to);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
    }
    

    public Vector<CustomFormTO> getRecords(String metaFormId, Timestamp iniRange) throws BusinessException {
        CustomFormDAO cdao = new CustomFormDAO();
        Vector<CustomFormTO> response;
        try {
            response = cdao.getRecords(metaFormId, iniRange) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

    
    public CustomFormTO getRecord(CustomFormTO cfto) throws BusinessException {
    	CustomFormTO response = null;
        try {
            response = (CustomFormTO) cdao.getObject(cfto) ;
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }
    

    public void insertRecord(CustomFormTO cfto) throws BusinessException {
        try {        
        	cdao.insert(cfto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }


    public void updateRecord(CustomFormTO cfto) throws BusinessException {
        try {        
        	cdao.update(cfto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
    }


    public void removeRecord(CustomFormTO cfto) throws BusinessException {
        try {        
        	cdao.remove(cfto);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }   
    }
    
}
