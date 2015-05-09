package com.pandora;

import java.sql.Timestamp;

public class RepositoryLogTO extends TransferObject {

	private static final long serialVersionUID = 1L;

	private String path;
	
	private Long revision;
	
	private String author;

	private Timestamp date;
	
	private String message;
	
	
	
	//////////////////////////////////////////////
	public String getPath() {
		return path;
	}
	public void setPath(String newValue) {
		this.path = newValue;
	}
	
	//////////////////////////////////////////////	
	public Long getRevision() {
		return revision;
	}
	public void setRevision(Long newValue) {
		this.revision = newValue;
	}

	
	//////////////////////////////////////////////
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String newValue) {
		this.author = newValue;
	}

	
	//////////////////////////////////////////////
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp newValue) {
		this.date = newValue;
	}
	
	
	//////////////////////////////////////////////
	public String getMessage() {
		return message;
	}
	public void setMessage(String newValue) {
		this.message = newValue;
	}	

	
	
}
