package com.pandora.gui.gantt;

import javax.swing.JComboBox;
import javax.swing.JComponent;

public class ToolCombo extends ToolItem {
	
	/** The swing combo of tool item */
	private JComboBox visualObject;

	/** Constructor */
	public ToolCombo(){
		this.visualObject = new JComboBox(); 
	}
	
	public void addOption(Object option) {
		this.visualObject.addItem(option);
	}
	
	public JComponent getVisualObject(){
		return this.visualObject;
	}

	
}
