package com.pandora;

import java.sql.Timestamp;

/**
 */
public class AttachmentHistoryTO extends TransferObject {

	private static final long serialVersionUID = 1L;
	
    private AttachmentTO attachment;
    
    private String status;

    private Timestamp creationDate;
    
    private UserTO user;

    private String history;
    
    
    ////////////////////////////////
    public AttachmentTO getAttachment() {
        return attachment;
    }
    public void setAttachment(AttachmentTO newValue) {
        this.attachment = newValue;
    }
    
    
    ////////////////////////////////    
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }
    
    
    ////////////////////////////////    
    public String getHistory() {
        return history;
    }
    public void setHistory(String newValue) {
        this.history = newValue;
    }
    
    
    ////////////////////////////////    
    public String getStatus() {
        return status;
    }
    public void setStatus(String newValue) {
        this.status = newValue;
    }
    
    
    ////////////////////////////////    
    public UserTO getUser() {
        return user;
    }
    public void setUser(UserTO newValue) {
        this.user = newValue;
    }
}
