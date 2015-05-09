package com.pandora.gui.gantt;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 */
public class DecorativeLine extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final Color LINE_COLOR = Color.DARK_GRAY;
	
  	public void paint(Graphics g) {
		int x2 = this.getWidth();
		//int y2 = this.getHeight();

		g.setColor(LINE_COLOR);
		g.drawLine(0, 0, x2, 0);
		
		for (int i=0; i< x2; i+=6){
		    g.setColor(Color.WHITE);
		    g.drawLine(i, 0, i+3, 0);    
		}
  	}
    
}
