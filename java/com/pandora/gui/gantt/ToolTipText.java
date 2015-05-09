package com.pandora.gui.gantt;

import java.awt.*;

import java.util.Iterator;
import java.util.Vector;

public class ToolTipText extends Panel {

	private static final long serialVersionUID = 1L;

	/** Default Font of tool tip */
	private final Font TT_FONT = new Font("arial", Font.PLAIN, 10);
	
	/** Content of tool tip text*/
	private Vector content;
	
	private	Vector listText = null;
		
	
	/**
	 * Constructor. <br> 
	 */
  	public ToolTipText() {
		this.content = new Vector();  	
	}	

	
	/**
	 * Paint a tool tip text box
	 */
  	public void paint(Graphics g) {
		g.setFont(TT_FONT);  	    
  		FontMetrics fm = g.getFontMetrics(g.getFont());
  		int c = 0;
  	  		
   		int x2 = this.getWidth();
	    this.listText = new Vector();
	    
	    //break long descriptions..creating a vector of content cropped
   		Iterator i = this.content.iterator();
   		while(i.hasNext()){
   		    c++;
   		    String line = (String)i.next();
			while (!line.equals("")){
   		    	line = this.cropText(line, g, x2-10);
			}
		}
   		
   		//calculate the real height o panel
   		int y2 = fm.getHeight() * listText.size(); 
   		this.setSize(x2, y2);
   		
   		//draw panel
		g.setColor(new Color(242, 243, 207));
   		g.fillRect(0, 0, x2, y2);
		g.setColor(Color.BLACK);
   		g.drawRect(0, 0, x2-1, y2-1);
		
		//display text
   		for (int j=0; j < listText.size(); j++){
   		    String line = (String)listText.elementAt(j);
			int ws = fm.stringWidth(line); //real width of string
			int x = (x2/2) - (ws/2); //centralized
			g.drawString(line, x, ((j+1)*fm.getHeight())-2);
		}
  	}

	
	/**
	 * Insert a new content line into vector. Each item of vector
	 * is a line into tool tip box.
	 */
	public void addContent(String contentLine) {
		this.content.addElement(contentLine);
	}

	
	/**
	 * 
	 * @param content
	 * @return
	 */
	private String cropText(String content, Graphics g, int maxWidth){
	    String piece = "";
	    FontMetrics fm = g.getFontMetrics();
	    int size = 0;
	    int c = 0;
   		
	    if (fm.stringWidth(content)>maxWidth){
	        
		    //check max content that fit into width
		    while(size<maxWidth){
		        c++; 
		        piece = content.substring(0, c);
		        size = fm.stringWidth(piece);
		    }
		    
		    if (!piece.equals("")){
		        for (int i=piece.length(); i>0; i--){
		            if(piece.substring(i-1, i).equals(" ")){
		                piece = piece.substring(0, i);
		                break;
		            }
		        }
		        content = content.substring(piece.length());
		    }	        
	    } else {
	        piece = content;
	        content = "";
	    }
	    
	    this.listText.addElement(piece);
	    
	    return content;
	}
	


	//////////////////////////////////////////
	public Vector getContent() {
		return content;
	}
	public void setContent(Vector mewValue) {
		this.content = mewValue;
	}
	
}
