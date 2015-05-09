package com.pandora.bus;

import java.util.Vector;

import com.pandora.ResourceTaskTO;
import com.pandora.TaskStatusTO;
import com.pandora.dao.TaskStatusDAO;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;

/**
 * This class contain the business rules related with Task Status entity.
 */
public class TaskStatusBUS extends GeneralBusiness {

    /** The Data Acess Object related with current business entity */
    TaskStatusDAO dao = new TaskStatusDAO();
    
    
    /**
     * Get a list of all Task Status objects from data base.
     * @return
     * @throws BusinessException
     */
    public Vector<TaskStatusTO> getTaskStatusList() throws BusinessException{
        Vector<TaskStatusTO> response = new Vector<TaskStatusTO>();
        try {
            response = dao.getList();
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    

    /**
     * Get a list of all Task Status objects from data base.
     */
    public Vector<TaskStatusTO> getTaskStatusList(Integer state) throws BusinessException{
        Vector<TaskStatusTO> response = new Vector<TaskStatusTO>();
        TaskStatusTO tto = null;
        try {
            if (state!=null) {
                tto = (TaskStatusTO) dao.getObjectByStateMachine(state);
                
                //first of all set the current state into list
                response.addElement(tto);
                
                if (state.equals(TaskStatusTO.STATE_MACHINE_OPEN) || state.equals(TaskStatusTO.STATE_MACHINE_REOPEN)) {
                    response.addElement(dao.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_PROGRESS));
                } else if (state.equals(TaskStatusTO.STATE_MACHINE_PROGRESS)) {
                    response.addElement(dao.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_HOLD));
                    response.addElement(dao.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_CLOSE));
                } else if (state.equals(TaskStatusTO.STATE_MACHINE_HOLD)) {
                    response.addElement(dao.getObjectByStateMachine(TaskStatusTO.STATE_MACHINE_PROGRESS));
                }
                
            } else {
                response = this.getTaskStatusList();    
            }
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }
    
    
    /**
     * This method get a TaskStatus object from BD that has the meaning of State Machine 
     * sent by argument.
     */
    public TaskStatusTO getObjectByStateMachine(Integer state) throws BusinessException{
        TaskStatusTO response = null;
        try {
            response = dao.getObjectByStateMachine(state);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Get a Task Status object from data base.
     * @param tsto
     * @return
     * @throws BusinessException
     */
    public TaskStatusTO getTaskStatusObject(TaskStatusTO tsto) throws BusinessException{
        TaskStatusTO response = null;
        try {
            response = (TaskStatusTO)dao.getObject(tsto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;
    }

    
    /**
     * Get a TaskStatus object from data base related with a Resource Task object.
     * @param rtto
     * @return
     * @throws BusinessException
     */
    public TaskStatusTO getObjectByResourceTask(ResourceTaskTO rtto) throws BusinessException{
        TaskStatusTO response = null;
        try {
            response = dao.getObjectByResourceTask(rtto);
        } catch (DataAccessException e) {
            throw new  BusinessException(e);
        }
        return response;        
    }
}
