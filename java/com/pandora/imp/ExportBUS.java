package com.pandora.imp;

import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.exception.BusinessException;

/**
 * This class is responsible by import process.
 */
public abstract class ExportBUS extends ImportExportBUS {
    
    public abstract String getFileName(ProjectTO pto) throws BusinessException;
    
    public abstract String getContentType() throws BusinessException;
    
    public abstract StringBuffer getHeader(ProjectTO pto, Vector fields) throws BusinessException;
    
    public abstract StringBuffer getBody(ProjectTO pto, UserTO handler, Vector fields) throws BusinessException;
    
    public abstract StringBuffer getFooter(ProjectTO pto, Vector fields) throws BusinessException;
     
    
    public String getEncoding(){
    	return SystemSingleton.getInstance().getDefaultEncoding();
    }

    public Integer getType() {
    	return TYPE_EXPORT;
    }
}
