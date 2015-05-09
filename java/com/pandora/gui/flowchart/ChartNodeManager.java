package com.pandora.gui.flowchart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.net.URL;
import java.util.Hashtable;

public class ChartNodeManager extends Panel {

	private static final long serialVersionUID = 1L;

	/** The list of node objects indexed by resource id */
  	private Hashtable htNodes;
  	
  	private ChartNode root;


  	public ChartNodeManager(FlowChart flowChart) {
	}


	public void addNode(String p, URL appletCodeBase){
		ChartNode n = new ChartNode(p, this, appletCodeBase);
		if (this.htNodes==null) {
			this.htNodes = new Hashtable();
			this.root = n;
		}
		this.htNodes.put(n.getId(), n);
		this.add(n.getJLabel());		
	}


	public void paint(Graphics g) {
  		int top = 0;
  		int x = ((this.getWidth() / 2)-100);
  		
	    if (this.htNodes!=null) {

	    	ChartNode cn = root;
	    	do{
	        	cn.paint(x, top);
	    		top += cn.getJLabel().getHeight();
	    		String nextId = cn.getNextNodeId();
	    		cn = (ChartNode)this.htNodes.get(nextId);
	    		
	    	} while(cn!=null);	    	
	    }
  	   	setVisible(true);
  	   	setBackground(Color.lightGray);
  	}
	
}
