package com.pandora;

import java.sql.Timestamp;
import java.util.Locale;

import com.pandora.helper.DateUtil;

/**
 * 
 */
public class EventTO extends TransferObject {

	private static final long serialVersionUID = 1L;

    private String summary;
    
    private String description;
    
    private Timestamp creationDate;
    
    private String username;
    
    
    //////////////////////////////////////////////
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }
    
    
    //////////////////////////////////////////////    
    public String getDescription() {
        return description;
    }
    public void setDescription(String newValue) {
        this.description = newValue;
    }
    
    
    //////////////////////////////////////////////    
    public String getSummary() {
        return summary;
    }
    public void setSummary(String newValue) {
        this.summary = newValue;
    }
    

    //////////////////////////////////////////////
    public String getUsername() {
        return username;
    }
    public void setUsername(String newValue) {
        this.username = newValue;
    }
    
    
    public String getLog(Locale loc){
    	return "[" + DateUtil.getDateTime(this.getCreationDate(), loc, 2, 2) + "] " +
				this.getUsername() + " - " + this.getDescription() + "\n";    	
    }

}
