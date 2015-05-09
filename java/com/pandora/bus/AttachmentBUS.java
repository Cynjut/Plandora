package com.pandora.bus;

import java.io.ByteArrayInputStream;
import java.util.Vector;

import com.pandora.AttachmentTO;
import com.pandora.PreferenceTO;
import com.pandora.UserTO;
import com.pandora.dao.AttachmentDAO;
import com.pandora.dao.AttachmentHistoryDAO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.MaxSizeAttachmentException;

/**
 */
public class AttachmentBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    AttachmentDAO dao = new AttachmentDAO();

    public Vector getAttachmentList(String planningId) throws BusinessException {
        Vector response;
        try {
            response = dao.getListByPlanningId(planningId);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

    public AttachmentTO getAttachment(AttachmentTO ato)
            throws BusinessException {
        AttachmentTO response;
        try {
            response = (AttachmentTO) dao.getObject(ato);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

    public void insertAttachment(AttachmentTO ato) throws BusinessException {
        UserDelegate udel = new UserDelegate();
        try {
            if (ato.getFileSize()>0) {
                UserTO root = udel.getRoot();
                PreferenceTO pref = root.getPreference();        
                long maxSize = Long.parseLong(pref.getPreference(PreferenceTO.UPLOAD_MAX_SIZE));
                if (ato.getFileSize() <= maxSize) {
                    ByteArrayInputStream inStream = new ByteArrayInputStream(ato.getFileInBytes());
                    ato.setBinaryFile(inStream, ato.getFileSize());
                    dao.insert(ato);
                } else {
                    throw new MaxSizeAttachmentException();    
                }
            } else {
                throw new BusinessException("Attachment file cannot be zero sized.");    
            }
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }

    public void updateAttachment(AttachmentTO ato) throws BusinessException {
        try {
            dao.update(ato);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }

    public void removeAttachment(AttachmentTO ato) throws BusinessException {
        try {
            ato.setStatus(AttachmentTO.ATTACH_STATUS_REMOVED);
            dao.update(ato);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
    }

    public Vector getHistory(String attachmentId) throws BusinessException {
        Vector response = new Vector();
        try {
            AttachmentHistoryDAO dao = new AttachmentHistoryDAO();
            response = dao.getListByAttachment(attachmentId);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

    
    public AttachmentTO getAttachmentFile(AttachmentTO ato) throws BusinessException {
        AttachmentTO response;
        try {
            response = dao.getAttachmentFile(ato);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

}