package com.pandora;

import java.sql.Timestamp;
import java.util.Vector;

public class RepositoryFileTO extends AttachmentTO {

	public static final String REPOSITORY_HEAD = "HEAD";
	
	private static final long serialVersionUID = 1L;
	
	private String path;
	
	private Boolean isLocked;

	private Boolean isDirectory;
	
	private Long revision;
	
	private String author;
	
	
	private String lockOwner;
	
	private Timestamp lockDate;
	
	private Timestamp lockExpirationDate;
	
	private String lockComment;
	
	
	private Vector<RepositoryFilePlanningTO> entities;
	
	private ArtifactTO artifact;
	
	
	public RepositoryFileTO() {
	}

	public RepositoryFileTO(String newValue) {
		this.setId(newValue);
	}

	//////////////////////////////////////////////
	public String getPath() {
		return path;
	}
	public void setPath(String newValue) {
		this.path = newValue;
	}

	
	//////////////////////////////////////////////	
	public Boolean getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Boolean newValue) {
		this.isLocked = newValue;
	}

	
	//////////////////////////////////////////////	
	public Long getRevision() {
		return revision;
	}
	public void setRevision(Long newValue) {
		this.revision = newValue;
	}
	
	
	//////////////////////////////////////////////		
	public Boolean getIsDirectory() {
		return isDirectory;
	}
	public void setIsDirectory(Boolean newValue) {
		this.isDirectory = newValue;
	}

	
	//////////////////////////////////////////////
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String newValue) {
		this.author = newValue;
	}	

	
	//////////////////////////////////////////////	
	public String getLockOwner() {
		return lockOwner;
	}
	public void setLockOwner(String newValue) {
		this.lockOwner = newValue;
	}
	
	
	//////////////////////////////////////////////
	public Timestamp getLockDate() {
		return lockDate;
	}
	public void setLockDate(Timestamp newValue) {
		this.lockDate = newValue;
	}
	
	
	//////////////////////////////////////////////
	public Timestamp getLockExpirationDate() {
		return lockExpirationDate;
	}
	public void setLockExpirationDate(Timestamp newValue) {
		this.lockExpirationDate = newValue;
	}
	
	
	//////////////////////////////////////////////
	public String getLockComment() {
		return lockComment;
	}
	public void setLockComment(String newValue) {
		this.lockComment = newValue;
	}
	
	
	//////////////////////////////////////////////
	public void setFileSize(long newValue){
		Long sz = new Long(newValue);
		super.fileSize = sz.intValue();
	}
	
	
	//////////////////////////////////////////////
	public Vector<RepositoryFilePlanningTO> getEntities() {
		return entities;
	}
	public void setEntities(Vector<RepositoryFilePlanningTO> newValue) {
		this.entities = newValue;
	}

	
	//////////////////////////////////////////////		
	public ArtifactTO getArtifact() {
		return artifact;
	}
	public void setArtifact(ArtifactTO newValue) {
		this.artifact = newValue;
	}
	
	
	
	public boolean checkEntity(String entityId) {
		boolean response = false;
		if (this.entities!=null){
			for(int i=0; i<this.entities.size(); i++) {
				RepositoryFilePlanningTO rfpto = (RepositoryFilePlanningTO)this.entities.elementAt(i);
				if (rfpto.getEntity()!=null && rfpto.getEntity().getId().equals(entityId)) {
					response = true;
					break;
				}
			}
		}
		return response;
	}

	
	public String getArtifactTemplateType() {
		String response = null;
		if (this.artifact!=null) {
			response = this.artifact.getExportType();
		}
		return response;
	}
	
	public void setArtifactTemplateType(String artifactTemplateType) {
		if (this.artifact==null) {
			this.artifact = new ArtifactTO();
			this.artifact.setId(this.getId());
		}
		this.artifact.setExportType(artifactTemplateType);
	}
	
	
	public String getFileName(){
		String name = this.path;
		
		if (this.path!= null && (this.path.lastIndexOf("/")>-1 || this.path.lastIndexOf("\\")>-1)) {
			int pos = this.path.lastIndexOf("/");
			if (pos==-1) {
				pos = this.path.lastIndexOf("\\");
			}		
			if (pos>-1) {
				name = this.path.substring(pos+1);
			}
		}
		
		return name;
	}
	
}

