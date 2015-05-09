package com.pandora.bus;

import com.pandora.ResourceTaskTO;
import com.pandora.TaskHistoryTO;
import com.pandora.TaskStatusTO;
import com.pandora.dao.TaskHistoryDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

public class TaskHistoryBUS extends GeneralBusiness {
	
	
	private TaskHistoryDAO dao = new TaskHistoryDAO();
	
	
	public void insert(ResourceTaskTO rtto, TaskStatusTO tsto, String comment) throws BusinessException {
        try {
        	
        	TaskStatusTO ts = rtto.getTaskStatus();
			if (tsto!=null) {
				ts = tsto;
			}        	
        	
		    TaskHistoryTO thto = dao.populateBeanByResourceTask(rtto, ts, comment);
		    dao.insert(thto);
			
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }	
	}

}
