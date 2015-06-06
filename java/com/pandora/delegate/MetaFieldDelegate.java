package com.pandora.delegate;

import java.util.Vector;

import com.pandora.MetaFieldTO;
import com.pandora.TransferObject;
import com.pandora.bus.MetaFieldBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for MetaField entity information access.
 */
public class MetaFieldDelegate extends GeneralDelegate {
    
    /** The Business object related with current delegate */
    private MetaFieldBUS bus = new MetaFieldBUS();

    
    /* (non-Javadoc)
     * @see com.pandora.bus.MetaFieldBUS.getMetaFieldList()
     */    
    public Vector<MetaFieldTO> getMetaFieldList() throws BusinessException{
        return bus.getMetaFieldList();
    }

    /* (non-Javadoc)
     * @see com.pandora.bus.MetaFieldBUS.getListByProjectAndContainer(java.lang.String, java.lang.String, java.lang.Integer)
     */    
    public Vector<MetaFieldTO> getListByProjectAndContainer(String projectId, String categoryId, Integer applyTo) throws BusinessException{
        return bus.getListByProjectAndContainer(projectId, categoryId, applyTo);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.MetaFieldBUS.getMetaFieldObject(com.pandora.MetaFieldTO)
     */    
    public MetaFieldTO getMetaFieldObject(MetaFieldTO current) throws BusinessException{
        return bus.getMetaFieldObject(current);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.MetaFieldBUS.insertMetaField(com.pandora.MetaFieldTO)
     */    
    public void insertMetaField(MetaFieldTO mfto) throws BusinessException{
        bus.insertMetaField(mfto);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.MetaFieldBUS.updateMetaField(com.pandora.MetaFieldTO)
     */    
    public void updateMetaField(MetaFieldTO mfto) throws BusinessException{
        bus.updateMetaField(mfto);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.MetaFieldBUS.removeMetaField(com.pandora.MetaFieldTO)
     */    
    public void removeMetaField(MetaFieldTO mfto) throws BusinessException {
        bus.removeMetaField(mfto);
    }

    
    /* (non-Javadoc)
     * @see com.pandora.bus.MetaFieldBUS.getFieldByMetaForm(java.lang.String)
     */        
    public Vector<MetaFieldTO> getFieldByMetaForm(String metaFormId) throws BusinessException {
        return bus.getFieldByMetaForm(metaFormId);
    }    
    
    public void updateAdditionalField(TransferObject to) throws BusinessException {
        bus.updateAdditionalField(to);
    } 
    
}
