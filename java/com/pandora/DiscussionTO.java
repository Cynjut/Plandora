package com.pandora;

public class DiscussionTO extends PlanningTO {

	private static final long serialVersionUID = 1L;

	private String name;
	
	private UserTO owner;
	
	private Boolean isBlocked;
	
	private CategoryTO category;

	
	///////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	///////////////////////////////////////
	public UserTO getOwner() {
		return owner;
	}
	public void setOwner(UserTO newValue) {
		this.owner = newValue;
	}

	
	///////////////////////////////////////
	public Boolean getIsBlocked() {
		return isBlocked;
	}
	public void setIsBlocked(Boolean newValue) {
		this.isBlocked = newValue;
	}

	
	///////////////////////////////////////
	public CategoryTO getCategory() {
		return category;
	}
	public void setCategory(CategoryTO newValue) {
		this.category = newValue;
	}
	
	
	
}
