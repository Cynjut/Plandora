package com.pandora.imp;

import java.io.InputStream;
import java.util.Vector;

import com.pandora.ProjectTO;
import com.pandora.exception.BusinessException;

/**
 * This class is responsible by import process. 
 */
public abstract class ImportBUS extends ImportExportBUS {     
       
    /** This constant defines the import from MSProject XML file  */
    public static final Integer MSPROJECT_XML_IMPORT = new Integer("1");
    
    
    public Integer getType() {
    	return TYPE_IMPORT;
    }
    
    /**
     * This method should be implemented by sub-class.<br>
     * It contain the rules related with the validation of input file.
     */
    public abstract void validate(InputStream is, ProjectTO pto, Vector fields) throws BusinessException;

    /**
     * This method should be implemented by sub-class.<br>
     * It contain the business rules in order to persist data
     * based on input file. 
     */
    public abstract void importFile(InputStream is, ProjectTO pto, Vector fields) throws BusinessException;
    

     
}
