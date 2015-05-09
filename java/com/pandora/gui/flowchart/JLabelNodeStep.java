package com.pandora.gui.flowchart;

import java.awt.Color;
import java.awt.Graphics;

import com.pandora.NodeTemplateTO;
import com.pandora.helper.WorkflowUtil;

public class JLabelNodeStep extends JLabelNode {

	private static final long serialVersionUID = 1L;
	
  	public void paint(Graphics g) {
  		NodeTemplateTO node = new NodeTemplateTO();
  		
  		node.setName(super.getText());
  		node.setGenericTag("aaa");
  		
		int width = this.getWidth();
		//int height = this.getHeight();

		nodeHeight = WorkflowUtil.paintSquare(g, (width/2), 0, node, Color.LIGHT_GRAY, null);
		
		//g.setColor(Color.BLACK);
		//g.drawLine(0, 1 , 0, height-1);
		//g.drawLine(width-1, 1 , width-1, height-1);		
  	}
  	
}
