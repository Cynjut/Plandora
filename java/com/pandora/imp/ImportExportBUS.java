package com.pandora.imp;

import java.util.Vector;

import com.pandora.FieldValueTO;
import com.pandora.UserTO;
import com.pandora.bus.GenericPlugin;
import com.pandora.exception.BusinessException;

public abstract class ImportExportBUS extends GenericPlugin {

	public static final Integer TYPE_EXPORT = new Integer("1");
	public static final Integer TYPE_IMPORT = new Integer("0");
	
	public UserTO handler;

	
	
    public abstract String getLabel() throws BusinessException;

    public abstract Vector<FieldValueTO> getFields() throws BusinessException;
    
    
    
    
    
    public Integer getType() throws Exception{
    	throw new Exception("This method must be implemented");
    }
    
    
	public static ImportExportBUS getClass(String className){
		ImportExportBUS response = null;
        try {
            @SuppressWarnings("rawtypes")
			Class klass = Class.forName(className);
            response = (ImportExportBUS)klass.newInstance();
        } catch (java.lang.NoClassDefFoundError e) {
        	response = null;
        } catch (Exception e) {
            response = null;
        }
        return response;
	}

	
	////////////////////////////////////////////
	public UserTO getHandler() {
		return handler;
	}
	public void setHandler(UserTO newValue) {
		this.handler = newValue;
	}
    
	
}
