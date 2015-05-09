package com.pandora.gui.flowchart;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import com.pandora.helper.WorkflowUtil;

public class JLabelNodeEnd extends JLabelNode {
	
	private static final long serialVersionUID = 1L;
	
  	public void paint(Graphics g) {
  		int width = this.getWidth();
  		nodeHeight = WorkflowUtil.paintEgde(g, (width/2), 0, false, 
  				new ImageIcon(this.getURL("end-workflow.png")));
  	}
}
