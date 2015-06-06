package com.pandora.bus;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Vector;

import com.pandora.AttachmentHistoryTO;
import com.pandora.AttachmentTO;
import com.pandora.PlanningTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
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

    /** The Data Access Object related with current business entity */
    AttachmentDAO dao = new AttachmentDAO();

    
    public Vector<AttachmentTO> getAttachmentList(String planningId) throws BusinessException {
        Vector<AttachmentTO> response = null;
        try {
        	ProjectTO pto = this.getAttachmentProject(planningId);
           	if (pto!=null && pto.getId()!=null) {
                response = dao.getListByPlanningId(planningId, pto.getId());        		
        	}            
        } catch (Exception e) {
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

    public Vector<AttachmentHistoryTO> getHistory(String attachmentId) throws BusinessException {
        Vector<AttachmentHistoryTO> response = new Vector<AttachmentHistoryTO>();
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

	public Vector<AttachmentTO> getAttachmentByProject(String projectId) throws BusinessException {
        Vector<AttachmentTO> response = new Vector<AttachmentTO>();
        ProjectBUS pbus = new ProjectBUS();
        try {

        	Vector<ProjectTO> childs = pbus.getProjectListByParent(new ProjectTO(projectId), true);
            Iterator<ProjectTO> i = childs.iterator();
            while(i.hasNext()){
                ProjectTO childProj = i.next();
                Vector<AttachmentTO> attOfChild = this.getAttachmentByProject(childProj.getId());
                response.addAll(attOfChild);
            }
                    
            response.addAll(dao.getListByProject(new ProjectTO(projectId)));
            
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
	public Vector<AttachmentTO> getListUntilID(String initialId, String finalId) throws BusinessException {
    	Vector<AttachmentTO> response = new Vector<AttachmentTO>();
        try {
            response = dao.getListUntilID(initialId, finalId);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
	}

	
    public long getMaxID() throws BusinessException {
        long response = -1;
        try {
            response = dao.getMaxId();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }        
        return response;
    }
    
    
    public ProjectTO getAttachmentProject(String relatedPlanningId) throws Exception {
    	ProjectTO response = null;
        PlanningBUS pbus = new PlanningBUS();
    	PlanningTO plan = pbus.getSpecializedObject(new PlanningTO(relatedPlanningId));
    	if (plan!=null) {
        	response = plan.getPlanningProject(); 
    	}
        return response;
    }
    
    public static String getTypeFromMime(String mime){
    	String response = "application/octet-stream";
    	if (mime!=null) {
    		if (mime.indexOf("html")>-1) {
    			response = "text/html";	
    		} else if (mime.indexOf("text")>-1) {
    			response = "text/plain";	
    		} else if (mime.indexOf("pdf")>-1) {
    			response = "application/pdf";
    		} else if (mime.indexOf("latex")>-1) {
    			response = "application/x-latex";
    		} else if (mime.indexOf("word")>-1) {
    			response = "application/msword";
    		} else if (mime.indexOf("powerpoint")>-1) {
    			response = "application/mspowerpoint";
    		} else if (mime.indexOf("excel")>-1) {
    			response = "application/ms-excel";
    		} else if (mime.indexOf("zip")>-1) {
    			response = "application/zip";
    		} else if (mime.indexOf("gif")>-1) {
    			response = "image/gif";
    		} else if (mime.indexOf("jpeg")>-1) {
    			response = "image/jpeg";
    		} else if (mime.indexOf("bmp")>-1) {
    			response = "image/x-ms-bmp";
    		} else if (mime.indexOf("png")>-1) {
    			response = "image/image/png";
    		} else if (mime.indexOf("mind")>-1) {
    			response = "application/x-xmind";
    		}
    	}
		return response;
    }
    
}