package com.pandora;

import java.sql.Timestamp;

public class OccurrenceHistoryTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    private String occurrenceId;
    
    private String occurrenceStatus;
    
    private String occurrenceStatusLabel;
    
    private Timestamp creationDate;
    
    private UserTO user;
    
    private String content;
    
    
    ////////////////////////////////////
    public String getContent() {
        return content;
    }
    public void setContent(String newValue) {
        this.content = newValue;
    }
    
    ////////////////////////////////////    
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }
    
    ////////////////////////////////////    
    public String getOccurrenceId() {
        return occurrenceId;
    }
    public void setOccurrenceId(String newValue) {
        this.occurrenceId = newValue;
    }
    
    ////////////////////////////////////    
    public String getOccurrenceStatus() {
        return occurrenceStatus;
    }
    public void setOccurrenceStatus(String newValue) {
        this.occurrenceStatus = newValue;
    }
    
    ////////////////////////////////////
    public UserTO getUser() {
        return user;
    }
    public void setUser(UserTO newValue) {
        this.user = newValue;
    }
    
    ////////////////////////////////////        
    public String getOccurrenceStatusLabel() {
        return occurrenceStatusLabel;
    }
    public void setOccurrenceStatusLabel(String newValue) {
        this.occurrenceStatusLabel = newValue;
    }
}
