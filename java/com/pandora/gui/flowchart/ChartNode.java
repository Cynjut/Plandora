package com.pandora.gui.flowchart;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class ChartNode  implements MouseListener {

	/** String used by PARAM applet tag */
	public static final String NODE_NUM = "NODENUM";
	
	/** String used by PARAM applet tag */
	public static final String NODE     = "NODE_";

	public static final String NODE_TYPE_STEP      = "NODE_STEP";
	public static final String NODE_TYPE_DECISION  = "NODE_DECISION";
	public static final String NODE_TYPE_START     = "NODE_START";
	public static final String NODE_TYPE_END       = "NODE_END";
	
	/** The id of current node (from applet param) */
	private String id;
	
	/** Name of node (from applet param) */
  	private String name;
  	
  	private String nextNodeId;
  	
  	private String nodeType;
  	
  	/** Pointer to ChartNodeManager object that control the current resource */
	private ChartNodeManager parentManager;
  	
  	/** A AWT visual object used to show the label of current node */
  	private JLabelNode label;
	

    public ChartNode(String s, ChartNodeManager container, URL appletCodeBase) {
  	    
  	    this.parentManager = container;
		StringTokenizer stList = new StringTokenizer(s, "|");

    	this.id = stList.nextToken();
    	this.name = stList.nextToken();
    	this.nextNodeId = stList.nextToken();    	
		this.nodeType = stList.nextToken();
		
    	if (nodeType.equals(NODE_TYPE_STEP)) {
    		this.label = new JLabelNodeStep();
    		
    	} else if (nodeType.equals(NODE_TYPE_START)) {
    		this.label = new JLabelNodeStart();
    		
    	} else if (nodeType.equals(NODE_TYPE_END)) {
    		this.label = new JLabelNodeEnd();
    		
    	} else if (nodeType.equals(NODE_TYPE_DECISION)) {
    		this.label = new JLabelNodeDecision();
    	}
    	
    	if (this.label!=null) {
    		this.label.setCodeBase(appletCodeBase);
        	this.label.setHorizontalAlignment(JLabel.LEFT);
        	this.label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
        	this.label.setForeground(Color.black);
        	this.label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        	this.label.setOpaque(true);
        	this.label.addMouseListener(this);    		
    	}
	}

    
  	public void paint(int x, int y) {
		label.setLocation(x, y);
		label.setSize(200, label.nodeHeight);
      	//label.setBackground(this.background);		
		label.setText(this.name);
		label.setFont(new Font("Verdana", Font.PLAIN, 11));
		label.setVisible(true);		
  	}
    
    ///////////////////////////////////////////
	public String getId() {
		return id;
	}
	public void setId(String newValue) {
		this.id = newValue;
	}

	
	///////////////////////////////////////////
	public String getName() {
		return name;
	}
	public void setName(String newValue) {
		this.name = newValue;
	}

	
	///////////////////////////////////////////	
	public String getNextNodeId() {
		return nextNodeId;
	}


	///////////////////////////////////////// 
	public JLabel getJLabel(){
		return(label);
	}
	

	public void mouseClicked(MouseEvent e) {
	}


	public void mouseEntered(MouseEvent e) {
	}


	public void mouseExited(MouseEvent e) {
	}


	public void mousePressed(MouseEvent e) {
	}


	public void mouseReleased(MouseEvent e) {
	}

}
