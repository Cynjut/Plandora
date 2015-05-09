package com.pandora.gui.gantt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;


public class DecorativePanel extends Panel {
	
	private static final long serialVersionUID = 1L;

	boolean isInserted;
	
  	public DecorativePanel(boolean inserted) {
  		this.isInserted = inserted;
	}

  	public void paint(Graphics g) {
		int x2 = this.getWidth();
		int y2 = this.getHeight();
	  		     	  		
		g.setColor(this.getBackground());
   		g.fillRect(0, 0, x2, y2);

		if (this.isInserted) {
			g.setColor(Color.darkGray);			
	   		g.drawLine(0, 0, 0, y2);
	   		g.drawLine(0, 1, x2, 1);
			g.setColor(Color.white);	   		
			g.drawLine(0, y2-1, x2, y2-1);
			g.drawLine(x2-1, 0, x2-1, y2);			
		} else {
			g.setColor(Color.white);
	   		g.drawLine(0, 0, 0, y2);
	   		g.drawLine(0, 1, x2, 1);
			g.setColor(Color.darkGray);
			g.drawLine(0, y2-1, x2, y2-1);
			g.drawLine(x2-1, 0, x2-1, y2);
		}
  	}

	public boolean isInserted() {
		return isInserted;
	}

	public void setIsInserted(boolean newValue) {
		this.isInserted = newValue;
	}

}
