package com.pandora.delegate;

import java.io.InputStream;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.UserTO;
import com.pandora.exception.BusinessException;
import com.pandora.imp.ExportBUS;
import com.pandora.imp.ImportBUS;
import com.pandora.imp.ImportExportBUS;

/**
 */
public class ImportExportDelegate extends GeneralDelegate {

	
	public void validateImportFile(ImportExportBUS importClass, InputStream is, ProjectTO pto, Vector fields) throws BusinessException{
		((ImportBUS)importClass).validate(is, pto, fields);
	}

	public void importFile(ImportExportBUS importClass, InputStream is, ProjectTO pto, Vector fields) throws BusinessException{
		((ImportBUS)importClass).importFile(is, pto, fields);
	}
	
	
    public String getExportContentType(ImportExportBUS export) throws BusinessException{
        return ((ExportBUS)export).getContentType() ;
    }

    public String getExportFileName(ImportExportBUS export, ProjectTO pto) throws BusinessException{
        return ((ExportBUS)export).getFileName(pto) ;
    }
    
    public StringBuffer getExportHeader(ImportExportBUS export, ProjectTO pto, Vector fields) throws BusinessException{
        return ((ExportBUS)export).getHeader(pto, fields) ;
    }

    public StringBuffer getExportBody(ImportExportBUS export, ProjectTO pto, UserTO handler, Vector fields) throws BusinessException{
        return ((ExportBUS)export).getBody(pto, handler, fields) ;
    }
    
    public StringBuffer getExportFooter(ImportExportBUS export, ProjectTO pto, Vector fields) throws BusinessException{
        return ((ExportBUS)export).getFooter(pto, fields) ;
    }

	public String getEncoding(ImportExportBUS export) {
		return ((ExportBUS)export).getEncoding();
	}

}
