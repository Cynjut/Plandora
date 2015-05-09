package com.pandora.bus;

import com.pandora.EventTO;
import com.pandora.dao.EventDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.StringUtil;


/**
 */
public class EventBUS extends GeneralBusiness {

    
    /** The Data Acess Object related with current business entity */
    EventDAO dao = new EventDAO();
    
    
    /**
     * Insert a new event record into data base
     * @param eto
     * @throws BusinessException
     */
    public void insertEvent(int type, String summary, String content, String username, Throwable e) throws BusinessException {
        try {
            EventTO eto = this.getEvent(type, summary, content, username, e);
            dao.insert(eto);
        } catch (DataAccessException ei) {
            throw new BusinessException("error saving log into data base: " + ei);
        }
    }
    
    
	private EventTO getEvent(int type, String summary, String content, String username, Throwable e){   
        EventTO response = new EventTO();	    
		        
        String errorMsg = "";
        if (e!=null) {
            errorMsg = StringUtil.getStackTraceToString(e);
        }

        if (summary!=null) {
            response.setSummary(summary);
        } else {
            response.setSummary(LogUtil.getTypeInString(type));
        }
	        
        response.setDescription(content + " " + errorMsg);
        
        if (username!=null) {
            response.setUsername(username);    
        } else {
            response.setUsername("UNKNOWN");
        }
            
        response.setCreationDate(DateUtil.getNow());
        
        return response;
	}
    
}
