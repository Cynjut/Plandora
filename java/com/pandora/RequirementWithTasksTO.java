package com.pandora;

import java.util.Vector;

public class RequirementWithTasksTO extends RequirementTO {

	private static final long serialVersionUID = 1L;

	private Vector<ResourceTaskTO> resourceTaskList;

	
	public void addResourceTask(ResourceTaskTO rtto) {
		if (this.resourceTaskList==null) {
			this.resourceTaskList = new Vector<ResourceTaskTO>();
		}
		this.resourceTaskList.addElement(rtto);		
	}

	
	public String getComboName(){
		String text = "";
		String id = "";
		
		if (super.getId()!=null && !super.getId().equals("-1")) {
			id = "[" + super.getId() + "] " ;
			
			if (super.getDescription()!=null) {
				if (super.getDescription().length()<=45) {
					text = super.getDescription();	
				} else {
					text = super.getDescription().substring(0, 45) + "...";
				}
			}			
		}
		
		return id + text;
	}
	
	/////////////////////////////////////
	public Vector<ResourceTaskTO> getResourceTaskList() {
		return resourceTaskList;
	}
	public void setResourceTaskList(Vector<ResourceTaskTO> list) {
		resourceTaskList = list;
	}
	
	
}
