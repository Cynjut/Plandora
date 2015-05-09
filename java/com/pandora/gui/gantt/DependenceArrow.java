package com.pandora.gui.gantt;

import java.awt.*;
import javax.swing.*;

public class DependenceArrow extends JComponent {

	private static final long serialVersionUID = 1L;

	/** String used by PARAM applet tag */
	public static final String DEP_NUM = "DEPNUM";
	
	/** String used by PARAM applet tag */	
	public static final String DEP     = "DEP_";

	private static final Color ARROW_COLOR = Color.darkGray;
	//private static final int ARROW_DIR_TOP   = 0;
	//private static final int ARROW_DIR_LEFT  = 1;
	private static final int ARROW_DIR_RIGHT = 2;
	//private static final int ARROW_DIR_DOWN  = 3;
	
	private int inc;
	private boolean upToDown = true;
	private boolean leftToRight = true;
	
		
  	public DependenceArrow() {
  		this.inc = 10;
	}

  	public void paint(Graphics g) {
		//int x2 = this.getWidth();
		//int y2 = this.getHeight();   		
		this.paintNormal(g);
  	}

	
	private void paintNormal(Graphics g){
		int x2 = this.getWidth();
		int y2 = this.getHeight();
		g.setColor(ARROW_COLOR);

		if (this.isLeftToRight() && this.isUpToDown()) {
			g.drawLine(inc+10, (inc/2), x2-inc-10, (inc/2));
			g.drawLine(x2-inc-10, (inc/2), x2-inc-10, y2-4);
			g.drawLine(x2-inc-10, y2-4, x2, y2-4);
			paintArrow(g, x2-inc, y2, ARROW_DIR_RIGHT);
			
		} else if (this.isLeftToRight() && !this.isUpToDown()) {
			g.drawLine(inc+15, y2-5, x2-inc-10, y2-5);
			g.drawLine(x2-inc-10, y2-5 , x2-inc-10, (inc/2)+2);
			g.drawLine(x2-inc-10, (inc/2)+2, x2-inc, (inc/2)+2);
			paintArrow(g, x2-inc, (inc/2)+6, ARROW_DIR_RIGHT);
			
		} else if (!this.isLeftToRight() && this.isUpToDown()) {	
			g.drawLine(inc-10, (inc/2), x2-inc, (inc/2));
			g.drawLine(inc-10, (inc/2), inc-10, y2-4);
			g.drawLine(inc-10, y2-4, inc, y2-4);
			paintArrow(g, inc, y2, ARROW_DIR_RIGHT);
			
		} else {		
			g.drawLine(0, (inc/2), inc, (inc/2));
			g.drawLine(0, (inc/2), 0, y2-5);	
			g.drawLine(0, y2-5, x2-11, y2-5);
			paintArrow(g, inc, (inc/2)+4, ARROW_DIR_RIGHT);
		}		
	}


	/**
	 * Paint the arrow of dependence
	 */
	private void paintArrow(Graphics g, int x2, int y2, int direction){
		if (direction==ARROW_DIR_RIGHT) {
			g.drawLine(x2-4, y2-7, x2-4, y2-1);
			g.drawLine(x2-3, y2-6, x2-3, y2-2);
			g.drawLine(x2-2, y2-5, x2-2, y2-3);
		}
	}
	
	
	//////////////////////////////////////////	
	public boolean isUpToDown() {
		return upToDown;
	}
	public void setUpToDown(boolean newValue) {
		this.upToDown = newValue;
	}

	
	//////////////////////////////////////////	
	public boolean isLeftToRight() {
		return leftToRight;
	}

	public void setLeftToRight(boolean newValue) {
		this.leftToRight = newValue;
	}

	
	
}
