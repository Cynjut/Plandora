package com.pandora;

public class ArtifactTemplateTO extends TransferObject {

	public static final String CURDATE_MM_DD_YYYY = "#CURDATE_MM/DD/YYYY#";
	
	public static final String CURDATE_DD_MM_YYYY = "#CURDATE_DD/MM/YYYY#";
	
	public static final String CURDATE_YYYYMMDD   = "#CURDATE_YYYYMMDD#";
	
	public static final String USER_NAME          = "#USER_NAME#";
	
	public static final String USER_FULLNAME      = "#USER_FULLNAME#";
	
	public static final String ARTIFACT_NAME      = "#ARTIFACT_NAME#";
	
	
	
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;
	
	private String description;
	
	private CategoryTO category;
	
	private String header;
	
	private String body;
	
	private String footer;
	
    private String profileViewer;
	
    
    public ArtifactTemplateTO(){
    }

    
    public ArtifactTemplateTO(String newId){
    	this.id = newId;
    }

	///////////////////////////////////////
	public String getId() {
		return id;
	}
	public void setId(String newValue) {
		this.id = newValue;
	}

	
	///////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	///////////////////////////////////////
	public String getDescription() {
		return description;
	}
	public void setDescription(String newValue) {
		this.description = newValue;
	}

	
	///////////////////////////////////////
	public CategoryTO getCategory() {
		return category;
	}
	public void setCategory(CategoryTO newValue) {
		this.category = newValue;
	}

	
	///////////////////////////////////////
	public String getHeader() {
		return header;
	}
	public void setHeader(String newValue) {
		this.header = newValue;
	}

	
	///////////////////////////////////////
	public String getBody() {
		return body;
	}
	public void setBody(String newValue) {
		this.body = newValue;
	}

	
	///////////////////////////////////////
	public String getFooter() {
		return footer;
	}
	public void setFooter(String newValue) {
		this.footer = newValue;
	}

	
	///////////////////////////////////////
	public String getProfileViewer() {
		return profileViewer;
	}
	public void setProfileViewer(String newValue) {
		this.profileViewer = newValue;
	}	
}
