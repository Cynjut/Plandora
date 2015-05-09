package com.pandora.gui.struts.form;

import org.apache.struts.upload.FormFile;

public class RepositoryUploadForm extends GeneralStrutsForm {

	private static final long serialVersionUID = 1L;

	private String maxFileSize;
	
	private String path;
	
	private String projectId;

	private String comment;
	
    private FormFile theFile;

    private String folderCreation = "off";
    
    private String newFolder;
    
    
	/////////////////////////////////////////        
    public FormFile getTheFile() {
      return theFile;
    }
    public void setTheFile(FormFile newValue) {
      this.theFile = newValue;
    }

    
	////////////////////////////////////////
	public String getMaxFileSize() {
		return maxFileSize;
	}
	public void setMaxFileSize(String newValue) {
		this.maxFileSize = newValue;
	}

	
	////////////////////////////////////////
	public String getPath() {
		return path;
	}
	public void setPath(String newValue) {
		this.path = newValue;
	}

	
	////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}
	
	
	////////////////////////////////////////
	public String getComment() {
		return comment;
	}
	public void setComment(String newValue) {
		this.comment = newValue;
	}
	
	
	////////////////////////////////////////	
	public String getFolderCreation() {
		return folderCreation;
	}
	public void setFolderCreation(String newValue) {
		this.folderCreation = newValue;
	}
	
	
	////////////////////////////////////////	
	public String getNewFolder() {
		return newFolder;
	}
	public void setNewFolder(String newValue) {
		this.newFolder = newValue;
	}
	
	
	
}
