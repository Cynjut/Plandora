package com.pandora;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

/**
 */
public class AttachmentTO extends TransferObject {

	private static final long serialVersionUID = 1L;
	
    public static final String ATTACH_STATUS_AVAILABLE = "1";
    
    public static final String ATTACH_STATUS_REMOVED   = "2";
    
    public static final String ATTACH_STATUS_RECOVERED = "3";
    
    public static final String VISIBILITY_RESTRICT = "1";
    public static final String VISIBILITY_PRIVATE  = "2";
    public static final String VISIBILITY_PUBLIC   = "3";
    
    private String name;
    
    private String status;
    
    private PlanningTO planning;
    
    private UserTO handler;
    
    private String visibility;
    
    private String visibilityLabel;
    
    private Timestamp creationDate;
    
    //content-type selected by user that made the upload
    private String type;

    //content-type detected automatically by system
    private String contentType;

    private String comment;
    
    private ByteArrayInputStream binaryFile;
    
    private byte[] fileInBytes;
         
    protected int fileSize = 0;
    
    
    public AttachmentTO(String newId) {
        super.setId(newId);
    }
    
    public AttachmentTO() {
    }

    
    ////////////////////////////////////
    public String getComment() {
        return comment;
    }
    public void setComment(String newValue) {
        this.comment = newValue;
    }
    

    ////////////////////////////////////    
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp newValue) {
        this.creationDate = newValue;
    }
    
    
    ////////////////////////////////////    
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }
    
    
    ////////////////////////////////////      
    public PlanningTO getPlanning() {
        return planning;
    }
    public void setPlanning(PlanningTO newValue) {
        this.planning = newValue;
    }
    
    
    ////////////////////////////////////    
    public String getStatus() {
        return status;
    }
    public void setStatus(String newValue) {
        this.status = newValue;
    }
    
    
    ////////////////////////////////////        
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String newValue) {
        this.contentType = newValue;
    }
    
    
    ////////////////////////////////////    
    public String getType() {
        return type;
    }
    public void setType(String newValue) {
        this.type = newValue;
    }
    
    
    ////////////////////////////////////    
    public UserTO getHandler() {
        return handler;
    }
    public void setHandler(UserTO newValue) {
        this.handler = newValue;
    }
    
    
    ////////////////////////////////////    
    public String getVisibility() {
        return visibility;
    }
    public void setVisibility(String newValue) {
        this.visibility = newValue;
    }

    
    ////////////////////////////////////       
    public ByteArrayInputStream getBinaryFile() {
        return binaryFile;
    }
    public void setBinaryFile(ByteArrayInputStream newValue, int size) {
        this.binaryFile = newValue;
        this.fileSize = size;
    }

    
    ////////////////////////////////////       
    public byte[] getFileInBytes() {
        return fileInBytes;
    }
    public void setFileInBytes(byte[] newValue, int size) {
        this.fileInBytes = newValue;
        this.fileSize = size;
    }    
    
    ///////////////////////////////////
    public int getFileSize(){
        return this.fileSize;
    }
    
    
    ///////////////////////////////////    
    public String getVisibilityLabel() {
        return visibilityLabel;
    }
    public void setVisibilityLabel(String visibilityLabel) {
        this.visibilityLabel = visibilityLabel;
    }
    
    
    
    public String getFieldToString(){
        String response = "";        
        response = response + "Visibil. :[" + this.getVisibilityLabel() + "]\n";
        response = response + "Type     :[" + this.getType() + "]\n";
        if (this.getContentType()!=null) {
            response = response + "Cont-Type:[" + this.getContentType() + "]\n";    
        }
        if (this.fileSize>0) {
            response = response + "Size     :[" + this.fileSize + " bytes]\n";    
        }
        response = response + this.getComment() + "\n";
        return response;
    }
}
