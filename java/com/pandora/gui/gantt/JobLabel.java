package com.pandora.gui.gantt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;

import java.util.Vector;
import javax.swing.JLabel;



/**
 * 
 */
public class JobLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	/** Job Decorative type used to display a label */
	public static final int TYPE_HIDDEN = 0;

	/** Job Decorative type used to display a label */
	public static final int TYPE_DASH_BLOCK = 1;

	/** Job Decorative type used to display a label */
	public static final int TYPE_CROSS = 2;

	/** Job Decorative type used to display a label */
	public static final int TYPE_DASH = 3;

	private int slotWidth;
	
	private Vector visibleSlots;
	
	
  	private int decorativeType;
  	
  	public void paint(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();

		for (int i=0 ; i < visibleSlots.size() ; i++) {
		    boolean transition = false;
		    
		    if (((Boolean)visibleSlots.elementAt(i)).booleanValue()) {
		        this.paintSlot(g, slotWidth * i, slotWidth, height);
		        transition = (i-1>0 && !((Boolean)visibleSlots.elementAt(i-1)).booleanValue());		        
		    } else {
		        this.paintEmptySlot(g, slotWidth * i, slotWidth, height);
		        transition = (i-1>0 && ((Boolean)visibleSlots.elementAt(i-1)).booleanValue());
		    }
		    
	        //if the previous slot contain a different visibility...draw a vertical line
	        if (transition) {
				g.setColor(Color.BLACK);
				g.drawLine(slotWidth * i, 1, slotWidth * i, height-3);       		            
	        }
		}
		
		g.setColor(Color.BLACK);
		g.drawLine(0, 1 , 0, height-1);
		g.drawLine(width-1, 1 , width-1, height-1);		
  	}    
  	
  	
    private void paintSlot(Graphics g, int x1, int w, int y2) {
        Graphics2D g2d = (Graphics2D)g;
        Color c1 = this.getBackground().brighter();
        Color c2 = this.getBackground().darker();
        GradientPaint grad = new GradientPaint(0, 0, c2, 0, y2, c1, false);
        g2d.setPaint(grad);
        g2d.fillRect(x1, 1 , w, y2);
		
		g.setColor(Color.BLACK);
		if (decorativeType==TYPE_CROSS){
		   	g.drawLine(x1, y2/2, x1+w, y2/2);
		   	g.drawLine(x1+(w/2), 1, x1+(w/2), y2);
		   	
		} else if (decorativeType==TYPE_DASH_BLOCK){
		    for (int y=1; y<y2; y+=2) {
		        for (int x=x1+1; x<(x1+w); x+=2) {
		            g.drawLine(x, y, x, y);        
		        }
		    }
			
		} else if (decorativeType==TYPE_DASH){
			g.drawLine(x1, y2, x1+w, 1);
			g.drawLine(x1, y2/2, x1+(w/2), 1);
			g.drawLine(x1+(w/2), y2, x1+w, y2/2);
		}
		
		g.setColor(Color.BLACK);
		g.drawLine(x1, 1 , x1+w, 1);
		g.drawLine(x1, y2-1 , x1+w, y2-1);
    }

    private void paintEmptySlot(Graphics g, int x1, int x2, int y2) {
        g.setColor(this.getBackground());
        g.drawLine(x1, y2-2, x1+x2, y2-2);
        
		g.setColor(Color.BLACK);
        g.drawLine(x1, y2-1, x1+x2, y2-1);
        g.drawLine(x1, y2-3, x1+x2, y2-3);        
    }
    

    /////////////////////////////////////////	
    public int getDecorativeType() {
        return decorativeType;
    }
    public void setDecorativeType(int newValue) {
        this.decorativeType = newValue;
    }

	/////////////////////////////////////////	    
    public void setSlotWidth(int newValue) {
        this.slotWidth = newValue;
    }
    
	/////////////////////////////////////////
    public void initVisibleSlots(){
        visibleSlots = new Vector();
    }
    public void addVisibleSlots(boolean isVisible) {
        if (this.visibleSlots==null){
            this.visibleSlots = new Vector();
        }
        this.visibleSlots.add(new Boolean(isVisible));
    }
}