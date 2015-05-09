package com.pandora.delegate;

import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.bus.TaskHistoryBUS;
import com.pandora.exception.BusinessException;

public class TaskHistoryDelegate extends GeneralDelegate {

	private TaskHistoryBUS bus = new TaskHistoryBUS();
	
	public void insert(ResourceTaskTO rtto, TaskStatusTO tsto, String comment) throws BusinessException {
		bus.insert(rtto, tsto, comment);
	}
}
