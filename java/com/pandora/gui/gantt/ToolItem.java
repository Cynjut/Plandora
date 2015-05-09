package com.pandora.gui.gantt;

import javax.swing.JComponent;

public abstract class ToolItem {

	public static final String SAVE_ROLE     = "SAVE_BUTTON";
	public static final String ZOOM_IN_ROLE  = "ZOOM_IN_BUTTON";	
	public static final String ZOOM_OUT_ROLE = "ZOOM_OUT_BUTTON";
	public static final String UP_RES_ROLE   = "UP_RES_BUTTON";
	public static final String DOWN_RES_ROLE = "DOWN_RES_BUTTON";
	
	public static final String LAYER_COMBO   = "LAYER_COMBO";
	public static final String FILTER_COMBO  = "FILTER_COMBO";
	
	/** The separator between two buttons */
	public static final String SEPARATOR = "SEPARATOR";

	
	/** The name of tool item */
	private String name;
		
	/** The role of tool item (each role contain diferent behavior) */
	private String role;	

	
	//this methos must be re-implemented by sub-class
	public abstract JComponent getVisualObject();

	
	//////////////////////////////////////////	
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}
	
	//////////////////////////////////////////	
	public String getRole() {
		return role;
	}
	public void setRole(String newValue) {
		this.role = newValue;
	}
	
}
