package com.pandora.gui.gantt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;

/**
 * This class represents the visual object that must be displayed
 * into gantt area when a specific resource don't has any job in it. 
 */
public class JoinnerJob extends Panel {
    
	private static final long serialVersionUID = 1L;

  	public void paint(Graphics g) {
		int x2 = this.getWidth();
		int y2 = this.getHeight();
		
		g.setColor(Color.BLACK);

		//paint lateral left line
		g.drawRect(0, 0, 1, y2);
		
		//paint lateral right line		
		g.drawRect(x2-2, 0, 1, y2);
		
		int midleY = (y2/2);
		g.fillRect(0, midleY-1, x2, 3);
  	}
    
}
