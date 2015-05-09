package com.pandora.delegate;

import java.util.Vector;

import com.pandora.AttachmentTO;
import com.pandora.bus.AttachmentBUS;
import com.pandora.exception.BusinessException;

/**
 */
public class AttachmentDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */
    private AttachmentBUS bus = new AttachmentBUS();
    
    
    public void insertAttachment(AttachmentTO ato) throws BusinessException {
        bus.insertAttachment(ato);
    }

    
    public void updateAttachment(AttachmentTO ato) throws BusinessException {
        bus.updateAttachment(ato);
    }


    public Vector getAttachmentByPlanning(String planningId) throws BusinessException {
        return bus.getAttachmentList(planningId);
    }

    
    public void removeAttachment(AttachmentTO ato) throws BusinessException  {
        bus.removeAttachment(ato);        
    }
    
    
    public AttachmentTO getAttachment(AttachmentTO ato) throws BusinessException {
        return bus.getAttachment(ato);
    }

    
    public Vector getHistory(String attachmentId)  throws BusinessException  {
        return bus.getHistory(attachmentId);
    }    
    
    
    public AttachmentTO getAttachmentFile(AttachmentTO ato) throws BusinessException {
        return bus.getAttachmentFile(ato);
    }

}
