package com.pandora.gui.gantt;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class ToolButton extends ToolItem{
	
	private static final long serialVersionUID = 1L;

	/** The URL used to dispatch the action after button click */
	private String urlTarget;	

	/** The swing button of tool item */
	private JButton visualObject;
	

	/** Constructor */
	public ToolButton(){
		this.visualObject = new JButton(); 
	}

	
	public void setIcon(ImageIcon image) {
		this.visualObject.setIcon(image);	
	}
	
	public JComponent getVisualObject(){
		return this.visualObject;
	}
	

	//////////////////////////////////////////
	public String getUrlTarget() {
		return urlTarget;
	}
	public void setUrlTarget(String newValue) {
		this.urlTarget = newValue;
	}

		
}
