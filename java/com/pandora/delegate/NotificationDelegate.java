package com.pandora.delegate;

import java.util.Vector;

import com.pandora.NotificationTO;
import com.pandora.bus.alert.NotificationBUS;
import com.pandora.exception.BusinessException;


/**
 */
public class NotificationDelegate extends GeneralDelegate {

    /** The Business object related with current delegate */    
    NotificationBUS bus = new NotificationBUS();
    
    /* (non-Javadoc)
     * @see com.pandora.bus.alert.NotificationBUS.performNotification()
     */        
    public void performNotification() throws BusinessException {
        bus.performNotification();
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.alert.NotificationBUS.getNotificationList()
     */            
    public Vector<NotificationTO> getNotificationList() throws BusinessException {
        return this.getNotificationList(false);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.alert.NotificationBUS.getNotificationList(boolean)
     */            
    public Vector<NotificationTO> getNotificationList(boolean hideClosedAgents) throws BusinessException {
        return bus.getNotificationList(hideClosedAgents);
    }
    

    /* (non-Javadoc)
     * @see com.pandora.bus.alert.NotificationBUS.getNotificationObject(com.pandora.NotificationTO)
     */                
    public NotificationTO getNotificationObject(NotificationTO nto) throws BusinessException {
        return bus.getNotificationObject(nto);
    }
    
    
    /* (non-Javadoc)
     * @see com.pandora.bus.NotificationBUS.insertNotification(com.pandora.NotificationTO)
     */    
    public void insertNotification(NotificationTO nto) throws BusinessException{
        bus.insertNotification(nto);
    } 
    
    /* (non-Javadoc)
     * @see com.pandora.bus.NotificationBUS.updateNotification(com.pandora.NotificationTO)
     */    
    public void updateNotification(NotificationTO nto) throws BusinessException{
        bus.updateNotification(nto);
    }


    /* (non-Javadoc)
     * @see com.pandora.bus.NotificationBUS.removeNotification(com.pandora.NotificationTO)
     */    
    public void removeNotification(NotificationTO nto) throws BusinessException {
        bus.removeNotification(nto);
    }
    
}
