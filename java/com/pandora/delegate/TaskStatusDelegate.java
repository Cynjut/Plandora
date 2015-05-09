package com.pandora.delegate;

import java.util.Vector;

import com.pandora.TaskStatusTO;
import com.pandora.bus.TaskStatusBUS;
import com.pandora.exception.BusinessException;

/**
 * This class has the interface for the Task Status entity.
 */
public class TaskStatusDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */      
    TaskStatusBUS bus = new TaskStatusBUS();


    /* (non-Javadoc)
     * @see com.pandora.bus.TaskStatusBUS.getTaskStatusList()
     */    
    public Vector<TaskStatusTO> getTaskStatusList() throws BusinessException{
        return bus.getTaskStatusList();
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.TaskStatusBUS.getTaskStatusList(java.lang.Integer)
     */    
    public Vector<TaskStatusTO> getTaskStatusList(Integer state) throws BusinessException{
        return bus.getTaskStatusList(state);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.TaskStatusBUS.getObjectByStateMachine(java.lang.Integer)
     */    
    public TaskStatusTO getObjectByStateMachine(Integer state) throws BusinessException{
        return bus.getObjectByStateMachine(state);
    }
    
    /* (non-Javadoc)
     * @see com.pandora.bus.TaskStatusBUS.getTaskStatusList(com.pandora.TaskStatusTO)
     */        
    public TaskStatusTO getTaskStatusObject(TaskStatusTO tsto) throws BusinessException{
        return bus.getTaskStatusObject(tsto);
    }
    
}
