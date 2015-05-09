package com.pandora;

import java.sql.Timestamp;

public class ProjectHistoryTO  extends TransferObject {
    
	private static final long serialVersionUID = 1L;

    /** Current leader envolved with project 'step changing' */
    private String leaderUserName;
    
    /** Id of related Project */
    private String projectId;

    /** Current status of project */
    private ProjectStatusTO status;
    
    /** Date of project 'step changing' */
    private Timestamp date;
    
    
    /////////////////////////////////////////
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp newValue) {
        this.date = newValue;
    }

    /////////////////////////////////////////    
    public String getLeaderUserName() {
        return leaderUserName;
    }
    public void setLeaderUserName(String newValue) {
        this.leaderUserName = newValue;
    }
    
    /////////////////////////////////////////    
    public String getProjectId() {
        return projectId;
    }
    public void setProjectId(String newValue) {
        this.projectId = newValue;
    }
    
    /////////////////////////////////////////    
    public ProjectStatusTO getStatus() {
        return status;
    }
    public void setStatus(ProjectStatusTO newValue) {
        this.status = newValue;
    }
}
