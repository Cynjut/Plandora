package com.pandora;

public class RepositoryPolicyTO extends TransferObject {
  	
	public static final String POLICY_OPEN_PROJ         = "allow.commit.open.proj";
	public static final String POLICY_PROJ_RESOURCE     = "allow.commit.resource.proj";
	public static final String POLICY_REPOS_SAME_PROJ   = "allow.commit.repos.same.proj";
	public static final String POLICY_ENTITY_REF        = "allow.commit.entity.ref";	
	public static final String POLICY_COMMENT_MANDATORY = "allow.commit.comment.mandatory";

	private static final long serialVersionUID = 1L;
	

	private ProjectTO project;
	
	private String policyType;
	
	private String value;

	
	///////////////////////////////////////////
	public ProjectTO getProject() {
		return project;
	}
	public void setProject(ProjectTO newValue) {
		this.project = newValue;
	}

	
	///////////////////////////////////////////
	public String getPolicyType() {
		return policyType;
	}
	public void setPolicyType(String newValue) {
		this.policyType = newValue;
	}

	
	///////////////////////////////////////////
	public String getValue() {
		return value;
	}
	public void setValue(String newValue) {
		this.value = newValue;
	}
	
	
}
