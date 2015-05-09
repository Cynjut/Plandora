package com.pandora.gui.gantt;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * The panel used to decorate a specific alloc unit. 
 */
public class AllocUnitPanel extends JComponent {
    
	private static final long serialVersionUID = 1L;

    /** The decoration type used by class to paint itself */
    private int decorateType = AllocUnit.TYPE_HIDDEN;
    
  	public void paint(Graphics g) {
  	    
  	    if (decorateType!=AllocUnit.TYPE_HIDDEN){
  			int x2 = this.getWidth();
  			int y2 = this.getHeight();
  			if (decorateType==AllocUnit.TYPE_SOLID) {
  				g.setColor(Color.BLACK);
  				g.drawRect(0, 0, x2, y2);
  		   		g.drawLine(0, y2/2, x2, y2/2);
  		   		g.drawLine(x2/2, 0, x2/2, y2); 	  
  			}
  	    }
  	}

  	////////////////////////////////////////
    public int getDecorateType() {
        return decorateType;
    }
    public void setDecorateType(int newValue) {
        this.decorateType = newValue;
    }
}
