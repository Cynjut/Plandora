package com.pandora.gui.gantt;

import java.awt.Font;
import java.awt.MenuItem;
import java.util.StringTokenizer;

public class MenuItemGantt extends MenuItem {

	private static final long serialVersionUID = 1L;

	/** String used to specify a SEPARATOR menu item */
	public static final String SEPARATOR = "SEPARATOR";

	
	/** TARGET used to INSERT Resource feature */	
	public static final String INSERT_RES_TARGET = "INSERT_RES_TARGET";
	/** TARGET used to increase position of Resource */	
	public static final String UP_RES_TARGET = "UP_RES_TARGET";
	/** TARGET used to digrease position of Resource */	
	public static final String DOWN_RES_TARGET = "DOWN_RES_TARGET";
	

	/** TARGET used to INSERT Job feature */	
	public static final String INSERT_JOB_TARGET = "INSERT_JOB_TARGET";	
	/** TARGET used to REMOVE Job feature */
	public static final String REMOVE_JOB_TARGET = "REMOVE_JOB_TARGET";
	
	
	/** TARGET used to Alloc a Layer into Resource */	
	public static final String ALLOC_LAYER_TARGET = "ALLOC_LAYER_TARGET";
	/** TARGET used to Edit a Allocation of some task */	
	public static final String ALLOC_EDIT_TARGET = "ALLOC_EDIT_TARGET";

	
	/** The "after click" destination of menu item  */
	private String target;
	
	/** The group of menu item */
	private String context;
	

	/**
	 * Constructor. <br> 
	 * Initialize attributes of the new MenuItemGantt object, 
	 * parsing information from applet PARAM
	 * @param s
	 */
  	public MenuItemGantt(String s) {
		StringTokenizer stList = new StringTokenizer(s, "|");
    	this.context = stList.nextToken();
    	this.setLabel(stList.nextToken());
    	this.target = stList.nextToken();
   		this.setFont(new Font("Arial", Font.PLAIN, 10));    	
	}


	/////////////////////////////////////////   	
	public String getContext() {
		return context;
	}
	public void setContext(String newValue) {
		this.context = newValue;
	}

	///////////////////////////////////////// 	
	public String getTarget() {
		return target;
	}
	public void setTarget(String newValue) {
		this.target = newValue;
	}

}
