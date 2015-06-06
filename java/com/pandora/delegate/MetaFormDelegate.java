package com.pandora.delegate;

import java.sql.Timestamp;
import java.util.Vector;

import com.pandora.CustomFormTO;
import com.pandora.MetaFormTO;
import com.pandora.bus.MetaFormBUS;
import com.pandora.exception.BusinessException;

/**
 */
public class MetaFormDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private MetaFormBUS bus = new MetaFormBUS();
    
    
    public Vector<MetaFormTO> getMetaFormList() throws BusinessException {
        return bus.getMetaFormList();
    }

    public MetaFormTO getObject(MetaFormTO mto) throws BusinessException {
        return bus.getObject(mto);
    }

    public void insertMetaForm(MetaFormTO to) throws BusinessException {
        bus.insertMetaForm(to);
    }
    
    public void updateMetaForm(MetaFormTO to) throws BusinessException {
        bus.updateMetaForm(to);
    }

    public void removeMetaForm(MetaFormTO to) throws BusinessException {
        bus.removeMetaForm(to);
    }

    public Vector<CustomFormTO> getRecords(String metaFormId, Timestamp iniRange) throws BusinessException {
        return bus.getRecords(metaFormId, iniRange);
    }
 
    public CustomFormTO getRecord(CustomFormTO cfto) throws BusinessException {
    	return bus.getRecord(cfto);
    }
    
    public void insertRecord(CustomFormTO cfto) throws BusinessException  {
        bus.insertRecord(cfto);        
    }

    public void updateRecord(CustomFormTO cfto) throws BusinessException  {
        bus.updateRecord(cfto);
    }

    public void removeRecord(CustomFormTO cfto) throws BusinessException  {
        bus.removeRecord(cfto);        
    }
    
}
