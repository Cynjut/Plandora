package com.pandora.gui.flowchart;

import java.net.URL;

import javax.swing.JLabel;

public class JLabelNode extends JLabel {

	private static final long serialVersionUID = 1L;
	
	private URL codeBase;
	
	protected int nodeHeight = 70;
	
	
	public void setCodeBase(URL newValue) {
		this.codeBase = newValue;
	}
	
	
	protected URL getURL(String filename) {
		URL url = null;
		try {
	    	url = new URL(this.codeBase, filename);
	    } catch (java.net.MalformedURLException e) {
	    	return null;
	    }
	    return url;
	}


	public int getNodeHeight() {
		return nodeHeight;
	}
	
	
}
