package com.pandora.gui.gantt;

import java.awt.HeadlessException;
import java.awt.PopupMenu;

public class MenuGantt extends PopupMenu {
	
	private static final long serialVersionUID = 1L;

	/** String used by PARAM applet tag */
	public static final String MENU_NUM = "MENUNUM";
	/** String used by PARAM applet tag */	
	public static final String MENU     = "MENU_";	

	
	/** CONTEXT of Gantt Area (all clicks into Gantt Area)*/	
	public static final String GANTT_AREA    = "GANTT_AREA";

	/** CONTEXT of Job Area (all clicks into a specific Job)*/	
	public static final String JOB_AREA      = "JOB_AREA";

	/** CONTEXT of Resource Area (all clicks into a specific Resource)*/	
	public static final String RESOURCE_AREA = "RESOURCE_AREA";
	

	/** Constructor. */	
    public MenuGantt(String arg0) throws HeadlessException {
        super(arg0);
    }
    
	/** Constructor. */	
	public MenuGantt(){
	}

}
