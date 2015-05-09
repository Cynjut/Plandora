package com.pandora;

/**
 * 
 */
public class NotificationFieldTO extends TransferObject{

	private static final long serialVersionUID = 1L;

    /** The Notification object */
    private NotificationTO notification;
    
    private String value;
    
    private String name;

    
    ////////////////////////////////////////
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    ////////////////////////////////////////    
    public NotificationTO getNotification() {
        return notification;
    }
    public void setNotification(NotificationTO newValue) {
        this.notification = newValue;
    }
    
    ////////////////////////////////////////    
    public String getValue() {
        return value;
    }
    public void setValue(String newValue) {
        this.value = newValue;
    }
}
